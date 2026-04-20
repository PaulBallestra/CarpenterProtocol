package fr.seeeek.carpenterprotocol.services;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.builtin.instances.config.InstanceWorldConfig;
import com.hypixel.hytale.builtin.instances.removal.WorldEmptyCondition;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.asset.type.gameplay.DeathConfig;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.PendingTeleport;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.spawn.GlobalSpawnProvider;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.storage.provider.EmptyChunkStorageProvider;
import com.hypixel.hytale.server.core.universe.world.storage.resources.EmptyResourceStorageProvider;
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.VoidWorldGenProvider;

import com.hypixel.hytale.protocol.Color;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * High-level API for creating and managing instances (worlds).
 */
public class InstanceService {
    private final Universe universe;
    private final Map<UUID, InstanceInfo> managedInstances = new ConcurrentHashMap<>();

    private record InstanceInfo(Vector3i min, Vector3i max, boolean checkBounds) {}

    private static final Vector3i INSTANCE_ANCHOR_BLOCK = new Vector3i(0, 80, 0);

    public InstanceService(Universe universe) {
        this.universe = universe;

        // Start a task to check for out-of-bounds players
        // HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(this::checkOutOfBounds, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * Creates a new instance that doesn't save on disk, spawns a prefab at (0,0,0)
     * (matching the prefab's anchor), and optionally teleports a player once ready.
     *
     * @param name             The display name of the instance.
     * @param prefabKey        The key of the prefab to spawn at origin.
     * @param deleteWhenEmpty  Whether the instance should be deleted when the last player leaves.
     * @param playersToTeleport Optional player to teleport when the world and prefab are ready.
     * @param spawnPosition    Position of the spawn
     * @param weatherKey       Weather string key to override with
     * @param pvp              If players can PVP
     * @param gameplayConfig   GameplayConfig string id to override with
     * @return A future that completes with the created World.
     */
    public CompletableFuture<World> createInstance(
            @Nonnull String name,
            @Nonnull String prefabKey,
            boolean deleteWhenEmpty,
            boolean checkBounds,
            @Nullable List<PlayerRef> playersToTeleport,
            Vector3d spawnPosition,
            String weatherKey,
            boolean pvp,
            String gameplayConfig
    ) {
        UUID uuid = UUID.randomUUID();
        String worldKey = InstancesPlugin.safeName(name);
        Path worldPath = universe.getPath().resolve("worlds").resolve(worldKey);

        WorldConfig config = new WorldConfig();
        config.setUuid(uuid);
        config.setDisplayName(WorldConfig.formatDisplayName(name));

        // Set spawn position to 0,80,0 (relative to prefab anchor which is at 0.5,0,0.5)
        spawnPosition.x += 0.5;
        spawnPosition.z += 0.5;

        config.setSpawnProvider(new GlobalSpawnProvider(new Transform(spawnPosition)));

        // Don't save on disk
        config.setCanSaveChunks(false);
        config.setSavingConfig(false);
        config.setDeleteOnRemove(true);
        config.setDeleteOnUniverseStart(true);
        config.setForcedWeather(weatherKey);
        config.setPvpEnabled(pvp);

        config.setCompassUpdating(false);
        config.setSavingPlayers(false);
        config.setSaveNewChunks(false);

        if(gameplayConfig != null) config.setGameplayConfig(gameplayConfig);

        // Use Void generator and Empty storage for a clean slate
        config.setWorldGenProvider(new VoidWorldGenProvider(new Color((byte)91, (byte)-98, (byte)40), "Env_Zone1_Plains"));
        config.setChunkStorageProvider(EmptyChunkStorageProvider.INSTANCE);
        config.setResourceStorageProvider(EmptyResourceStorageProvider.INSTANCE);

        // Configure Instance-specific settings
        InstanceWorldConfig instanceConfig = InstanceWorldConfig.ensureAndGet(config);
        if (deleteWhenEmpty) {
            instanceConfig.setRemovalConditions(WorldEmptyCondition.REMOVE_WHEN_EMPTY);
        }

        return universe.makeWorld(worldKey, worldPath, config)
                .thenCompose(world -> {
                    // Load and spawn the prefab
                    BlockSelection prefabFromAssets = PrefabStore.get().getServerPrefab(prefabKey);
                    if (prefabFromAssets == null) {
                        return CompletableFuture.failedFuture(new IllegalArgumentException("Prefab not found: " + prefabKey));
                    }

                    // IMPORTANT: never mutate the cached prefab instance from PrefabStore.
                    // Also: prefabs may carry a non-zero (x,y,z) "selection position". Our world placement always
                    // uses selection position = (0,0,0), so bounds must be computed from a copy with that position.
                    BlockSelection prefab = new BlockSelection(prefabFromAssets);
                    prefab.setPosition(0, 0, 0);

                    // Track bounds
                    Vector3i targetPos = new Vector3i((int)(spawnPosition.x-0.5), (int)spawnPosition.y, (int)(spawnPosition.z-0.5));

                    // BlockSelection.placeNoReturn placement formula (see BlockSelection):
                    //   worldX = localX + selectionX + targetX - anchorX
                    // so compute bounds using the SAME formula by iterating the prefab's stored blocks.
                    Vector3i[] bounds = computeWorldBoundsForPlacement(prefab, targetPos);
                    Vector3i worldMin = bounds[0];
                    Vector3i worldMax = bounds[1];
                    managedInstances.put(uuid, new InstanceInfo(worldMin, worldMax, checkBounds));

                    CompletableFuture<Void> prefabFuture = new CompletableFuture<>();
                    world.execute(() -> {
                        try {
                            prefab.placeNoReturn(world, targetPos, world.getEntityStore().getStore());

                            // Set the anchor block position (0,80,0) to Empty as requested
                            world.setBlock((int)(spawnPosition.x-0.5), (int)spawnPosition.y, (int)(spawnPosition.z-0.5), "Empty");

                            prefabFuture.complete(null);
                        } catch (Exception e) {
                            prefabFuture.completeExceptionally(e);
                        }
                    });

                    return prefabFuture.thenApply(v -> world);
                })
                .thenCompose(world -> {
                    if (playersToTeleport == null || playersToTeleport.isEmpty()) {
                        return CompletableFuture.completedFuture(world);
                    }

                    // Teleport players when ready
                    CompletableFuture<?>[] teleportFutures = playersToTeleport.stream()
                            .map(player -> {
                                if (player != null) {
                                    CompletableFuture<PlayerRef> f = world.addPlayer(player);
                                    return f != null ? f : CompletableFuture.completedFuture(null);
                                }
                                return CompletableFuture.completedFuture(null);
                            })
                            .toArray(CompletableFuture[]::new);

                    return CompletableFuture.allOf(teleportFutures).thenApply(v -> world);
                });
    }

    /**
     * Teleports a player to an existing instance.
     *
     * @param player The player to teleport.
     * @param world  The target instance world.
     */
    public void teleportToInstance(@Nonnull PlayerRef player, @Nonnull World world) {
        Ref<EntityStore> playerRef = player.getReference();
        if (playerRef == null || !playerRef.isValid()) return;

        // We must be on the world thread of the player's CURRENT world to access their store/components safely
        World currentWorld = playerRef.getStore().getExternalData().getWorld();
        if (currentWorld == null) return;

        currentWorld.execute(() -> {
            // Now we are on the correct thread for the player's current world

            // Fix "Incorrect teleportId": The client might send a ClientReady packet
            // for a previous world while the server is transitioning.
            // To fix this, we ensure the player's store has the correct world association
            // before the teleportation flow begins.

            // Check if player is already in a teleportation flow
            Store<EntityStore> store = playerRef.getStore();
            if (store.getComponent(playerRef, PendingTeleport.getComponentType()) != null) {
                store.removeComponent(playerRef, PendingTeleport.getComponentType());
            }

            InstancesPlugin.teleportPlayerToInstance(
                    playerRef,
                    playerRef.getStore(),
                    world,
                    null
            );
        });
    }

    private void checkOutOfBounds() {
        for (World world : universe.getWorlds().values()) {
            UUID worldUuid = world.getWorldConfig().getUuid();
            InstanceInfo info = managedInstances.get(worldUuid);
            if (info == null || !info.checkBounds) continue;

            // Run check on world thread
            world.execute(() -> {
                ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
                if (spawnProvider == null) return;

                for (PlayerRef pr : world.getPlayerRefs()) {
                    Ref<EntityStore> ref = pr.getReference();
                    if (ref == null || !ref.isValid()) continue;

                    TransformComponent tc = ref.getStore().getComponent(ref, TransformComponent.getComponentType());
                    if (tc == null) continue;

                    Vector3d pos = tc.getPosition();

                    // Bounds are inclusive for integer block coordinates.
                    // A block at max occupies space up to (max + 1.0) on that axis.
                    // We intentionally enforce X/Z only (2D play area) to avoid false positives from jumping,
                    // stair half-blocks, head bobbing etc. Y is only used as a safety "fell out" rescue.
                    double eps = 0.10;
                    boolean oobXYZ = pos.getX() < (info.min.getX() - eps)
                            || pos.getX() > (info.max.getX() + 1.0 + eps)
                            || pos.getZ() < (info.min.getZ() - eps)
                            || pos.getZ() > (info.max.getZ() + 1.0 + eps)
                            || pos.getY() < (info.min.getY() - eps)
                            || pos.getY() > (info.max.getY() + 1.0 + eps);

                    boolean fellOut = pos.getY() < (info.min.getY() - 16.0);

                    if (oobXYZ || fellOut) {

                        // Out of bounds! Teleport to spawn
                        Transform spawn = spawnProvider.getSpawnPoint(world, pr.getUuid());
                        if (spawn != null) {
                            Teleport tp = Teleport.createForPlayer(world, spawn);

                            // Defensive: ensure we don't fight an ongoing teleport handshake
                            Store<EntityStore> store = ref.getStore();
                            if (store.getComponent(ref, PendingTeleport.getComponentType()) != null) {
                                store.removeComponent(ref, PendingTeleport.getComponentType());
                            }

                            store.putComponent(ref, Teleport.getComponentType(), tp);
                        }
                    }
                }
            });
        }
    }

    /**
     * Computes an axis-aligned bounding box for the prefab as placed by
     * {@ncode BlockSelection.placeNoReturn(...)}.
     */
    private static Vector3i[] computeWorldBoundsForPlacement(@Nonnull BlockSelection prefab, @Nonnull Vector3i targetPos) {
        // Offset used by BlockSelection.placeNoReturn conversions.
        final int ox = prefab.getX() + targetPos.getX() - prefab.getAnchorX();
        final int oy = prefab.getY() + targetPos.getY() - prefab.getAnchorY();
        final int oz = prefab.getZ() + targetPos.getZ() - prefab.getAnchorZ();

        final int[] min = new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
        final int[] max = new int[] {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
        final boolean[] any = new boolean[] {false};

        prefab.forEachBlock((lx, ly, lz, block) -> {
            any[0] = true;
            int wx = lx + ox;
            int wy = ly + oy;
            int wz = lz + oz;
            if (wx < min[0]) min[0] = wx;
            if (wy < min[1]) min[1] = wy;
            if (wz < min[2]) min[2] = wz;
            if (wx > max[0]) max[0] = wx;
            if (wy > max[1]) max[1] = wy;
            if (wz > max[2]) max[2] = wz;
        });

        Vector3i worldMin;
        Vector3i worldMax;

        if (any[0]) {
            worldMin = new Vector3i(min[0], min[1], min[2]);
            worldMax = new Vector3i(max[0], max[1], max[2]);
        } else {
            // Fallback: use selection bounds if the prefab has no blocks (should not happen).
            Vector3i offset = new Vector3i(ox, oy, oz);
            worldMin = prefab.getSelectionMin().clone().add(offset);
            worldMax = prefab.getSelectionMax().clone().add(offset);
        }

        // Ensure our spawn/anchor point is always considered "inside".
        worldMin = Vector3i.min(worldMin, INSTANCE_ANCHOR_BLOCK);
        worldMax = Vector3i.max(worldMax, INSTANCE_ANCHOR_BLOCK);

        return new Vector3i[] {worldMin, worldMax};
    }
}
