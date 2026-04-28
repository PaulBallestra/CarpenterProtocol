package fr.seeeek.carpenterprotocol.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.AndQuery;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.common.BroadcastMessage;
import fr.seeeek.carpenterprotocol.common.MessageType;
import fr.seeeek.carpenterprotocol.components.LaserTagMarkerTeamSpawnerComponent;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;
import fr.seeeek.carpenterprotocol.components.MiniGameHudComponent;
import fr.seeeek.carpenterprotocol.components.MiniGamePlayerComponent;
import fr.seeeek.carpenterprotocol.huds.MiniGameInGameHud;
import fr.seeeek.carpenterprotocol.interfaces.LaserTagTeamSpawnProvider;
import fr.seeeek.carpenterprotocol.utils.LaserTagUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class MiniGamePlayerComponentEvent {
    private static final Map<Integer, List<Transform>> markerTeamSpawnPoints = new ConcurrentHashMap<>();

    public static void onPlayerReady(PlayerReadyEvent event){
        Player player = event.getPlayer();

        World defaultWorld = Universe.get().getDefaultWorld();

        assert defaultWorld != null;

        String defaultWorldName = Universe.get().getDefaultWorld().getName();

        if(player.getReference() == null || player.getWorld() == null) return;

        PlayerRef playerRef = player.getWorld().getEntityStore().getStore().getComponent(event.getPlayerRef(), PlayerRef.getComponentType());
        World playerWorld = player.getWorld();

        if(playerRef == null || playerRef.getReference() == null || !playerRef.isValid()) return;

        if(playerWorld.getName().equals(defaultWorldName)) {
            playerWorld.execute(() -> {
                MiniGameHudComponent miniGameHudComponent = player.getReference().getStore().getComponent(player.getReference(), MiniGameHudComponent.getComponentType());

                if(miniGameHudComponent != null){
                    CustomUIHud customHud = player.getHudManager().getCustomHud();
                    if (customHud != null) {
                        UICommandBuilder builder = new UICommandBuilder();
                        customHud.update(true, builder);
                    }

                    player.getReference().getStore().removeComponent(
                            player.getReference(),
                            MiniGameHudComponent.getComponentType()
                    );
                }

                MiniGamePlayerComponent miniGamePlayerComponent = player.getReference().getStore().getComponent(player.getReference(), MiniGamePlayerComponent.getComponentType());
                if (miniGamePlayerComponent != null) {
                    player.getReference().getStore().removeComponent(player.getReference(), MiniGamePlayerComponent.getComponentType());
                }

                LaserTagPlayerComponent laserTagPlayerComponent = player.getReference().getStore().getComponent(player.getReference(), LaserTagPlayerComponent.getComponentType());
                if (laserTagPlayerComponent != null) {
                    player.getReference().getStore().removeComponent(player.getReference(), LaserTagPlayerComponent.getComponentType());
                }
            });
        }

        // add MiniGamePlayerComponent if it's a mini game instance
        if(playerWorld.getName().startsWith("block_h13") || playerWorld.getName().startsWith("mini_game-laser_tag-")){
            MiniGamePlayerComponent miniGamePlayerComponent = new MiniGamePlayerComponent();
            MiniGameHudComponent miniGameHudComponent = new MiniGameHudComponent();
            Store<EntityStore> store = player.getReference().getStore();
            Collection<PlayerRef> allPlayerRefs = playerWorld.getPlayerRefs();

            loadTeamSpawnMarkerBlocks(playerWorld);

            int numberOfPlayerWithLaserTagPlayerComponent = store.getEntityCountFor(LaserTagPlayerComponent.getComponentType());
            AtomicInteger team0PlayerCount = new AtomicInteger();
            AtomicInteger team1PlayerCount = new AtomicInteger();

            playerWorld.execute(() -> {
                allPlayerRefs.forEach(allPlayerRef -> {
                    if(allPlayerRef.getReference() == null || !allPlayerRef.isValid()) return;

                    LaserTagPlayerComponent laserTagPlayerComponent = store.getComponent(allPlayerRef.getReference(), LaserTagPlayerComponent.getComponentType());
                    if(laserTagPlayerComponent == null) return;

                    switch (laserTagPlayerComponent.getTeamId()){
                        case 0:
                            team0PlayerCount.getAndIncrement();
                            break;
                        case 1:
                            team1PlayerCount.getAndIncrement();
                            break;
                    }
                });
            });

            playerWorld.execute(() -> {
                player.setGameMode(event.getPlayerRef(), GameMode.Adventure, player.getReference().getStore());
                store.addComponent(event.getPlayerRef(), MiniGamePlayerComponent.getComponentType(), miniGamePlayerComponent);
                store.addComponent(event.getPlayerRef(), MiniGameHudComponent.getComponentType(), miniGameHudComponent);

                MiniGameInGameHud miniGameInGameHud = new MiniGameInGameHud(playerRef, 0, 0);

                player.getHudManager().setCustomHud (playerRef, miniGameInGameHud);

                LaserTagUtils.clearLaserTagPlayerInventory(store, player);

                if(numberOfPlayerWithLaserTagPlayerComponent > 0){
                    if(team1PlayerCount.get() >= team0PlayerCount.get()){
                        List<Transform> spawns = markerTeamSpawnPoints.get(0);
                        Transform spawn = spawns.get(ThreadLocalRandom.current().nextInt(spawns.size()));

                        registerSpawnTeam(playerWorld, playerRef.getUuid(), 0);

                        LaserTagUtils.assignTeam(0, store, playerRef.getReference());
                        LaserTagUtils.assignLaserTagPlayerInventory(store, player, 0);

                        Teleport teleport = Teleport.createForPlayer(playerWorld, spawn);
                        store.addComponent(event.getPlayerRef(), Teleport.getComponentType(), teleport);

                        BroadcastMessage.toPlayer(playerRef, "Joined Red Team", MessageType.SUCCESS);
                    }else{
                        List<Transform> spawns = markerTeamSpawnPoints.get(1);
                        Transform spawn = spawns.get(ThreadLocalRandom.current().nextInt(spawns.size()));

                        registerSpawnTeam(playerWorld, playerRef.getUuid(), 1);

                        LaserTagUtils.assignTeam(1, store, playerRef.getReference());
                        LaserTagUtils.assignLaserTagPlayerInventory(store, player, 1);

                        Teleport teleport = Teleport.createForPlayer(playerWorld, spawn);
                        store.addComponent(event.getPlayerRef(), Teleport.getComponentType(), teleport);

                        BroadcastMessage.toPlayer(playerRef, "Joined Blue Team", MessageType.SUCCESS);
                    }
                }else{
                    // Spawn override fallback
                    ISpawnProvider spawnProvider = playerWorld.getWorldConfig().getSpawnProvider();
                    if (spawnProvider == null) return;

                    Transform spawnTransform = spawnProvider.getSpawnPoint(playerWorld, playerRef.getUuid());
                    Teleport teleport = Teleport.createForPlayer(playerWorld, spawnTransform);
                    store.addComponent(event.getPlayerRef(), Teleport.getComponentType(), teleport);
                }
            });
        }
    }

    private static void loadTeamSpawnMarkerBlocks(World world) {
        int spawnYOffset = 2;
        markerTeamSpawnPoints.clear();
        Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
        WorldConfig worldConfig = world.getWorldConfig();

        AndQuery<ChunkStore> query = Query.and(
                LaserTagMarkerTeamSpawnerComponent.getComponentType(), BlockModule.BlockStateInfo.getComponentType()
        );

        world.execute(() -> {
            chunkStore.forEachChunk(query, ((archetypeChunk, commandBuffer) -> {
                for (int index = 0; index < archetypeChunk.size(); index++) {
                    LaserTagMarkerTeamSpawnerComponent spawner = archetypeChunk.getComponent(index, LaserTagMarkerTeamSpawnerComponent.getComponentType());

                    assert spawner != null;

                    BlockModule.BlockStateInfo blockInfo =
                            archetypeChunk.getComponent(index, BlockModule.BlockStateInfo.getComponentType());

                    assert blockInfo != null;
                    Ref<ChunkStore> chunkRef = blockInfo.getChunkRef();

                    int blockIndex = blockInfo.getIndex();

                    int x = ChunkUtil.xFromBlockInColumn(blockIndex);
                    int y = ChunkUtil.yFromBlockInColumn(blockIndex);
                    int z = ChunkUtil.zFromBlockInColumn(blockIndex);

                    ChunkColumn column = commandBuffer.getComponent(chunkRef, ChunkColumn.getComponentType());
                    assert column != null;
                    Ref<ChunkStore> sectionRef = column.getSection(ChunkUtil.chunkCoordinate(y));

                    assert sectionRef != null;
                    ChunkSection chunkSection = commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());

                    assert chunkSection != null;
                    int worldX = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getX(), x);
                    int worldY = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getY(), y);
                    int worldZ = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getZ(), z);

                    Vector3i position = new Vector3i(worldX, worldY + spawnYOffset, worldZ);
                    spawner.setPosition(position);

                    Transform transform = new Transform(position);
                    markerTeamSpawnPoints.computeIfAbsent(
                            spawner.getTeamId(),
                            id -> new ArrayList<>()
                    ).add(transform);
                }
            }));

            LaserTagTeamSpawnProvider spawnProvider = new LaserTagTeamSpawnProvider(markerTeamSpawnPoints);
            worldConfig.setSpawnProvider(spawnProvider);
        });
    }

    private static void registerSpawnTeam(World world, UUID playerUuid, int teamId) {
        if (world.getWorldConfig().getSpawnProvider() instanceof LaserTagTeamSpawnProvider spawnProvider) {
            spawnProvider.setPlayerTeam(playerUuid, teamId);
        }
    }
}
