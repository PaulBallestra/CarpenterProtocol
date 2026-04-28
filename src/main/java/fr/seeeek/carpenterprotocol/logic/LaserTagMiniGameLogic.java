package fr.seeeek.carpenterprotocol.logic;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.AndQuery;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.common.BroadcastMessage;
import fr.seeeek.carpenterprotocol.common.MessageType;
import fr.seeeek.carpenterprotocol.components.LaserTagMarkerTeamSpawnerComponent;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;
import fr.seeeek.carpenterprotocol.components.MiniGameComponent;
import fr.seeeek.carpenterprotocol.components.MiniGamePlayerComponent;
import fr.seeeek.carpenterprotocol.enums.MiniGamePlayerState;
import fr.seeeek.carpenterprotocol.enums.MiniGameState;
import fr.seeeek.carpenterprotocol.interfaces.LaserTagTeamSpawnProvider;
import fr.seeeek.carpenterprotocol.interfaces.MiniGameLogic;
import fr.seeeek.carpenterprotocol.utils.LaserTagUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class LaserTagMiniGameLogic implements MiniGameLogic {
    private final Map<Integer, List<Transform>> markerTeamSpawnPoints = new ConcurrentHashMap<>();
    private final Vector3i arenaTargetSpawnPosition = new Vector3i(128, 196, 128);
    private final int maxKills = 50;

    public void setup(CommandBuffer<EntityStore> commandBuffer, World world, MiniGameComponent miniGameComponent, float dt, Store<EntityStore> store, Ref<EntityStore> ref, Collection<PlayerRef> allPlayerRefs){
        spawnArena(world, store);
    }

    public void starting(CommandBuffer<EntityStore> commandBuffer, World world, MiniGameComponent miniGameComponent, float dt, Store<EntityStore> store, Ref<EntityStore> ref, Collection<PlayerRef> allPlayerRefs){        // team assignment
        assignTeams(world, allPlayerRefs, store);
    }

    @Override
    public void running(CommandBuffer<EntityStore> commandBuffer, World world, MiniGameComponent miniGameComponent, float dt, Store<EntityStore> store, Ref<EntityStore> entityRef) {
        if (miniGameComponent.getState() != MiniGameState.RUNNING) return;

        boolean isPlayerAlone = store.getEntityCountFor(LaserTagPlayerComponent.getComponentType()) == 1;

        int team0Score = 0;
        int team1Score = 0;
        int winningTeam = -1;

        // Calculate scores
        for (Ref<EntityStore> playerRef : miniGameComponent.getAlivePlayers()) {
            if (playerRef == null || !playerRef.isValid()) continue;

            LaserTagPlayerComponent laserTag = commandBuffer.getComponent(playerRef, LaserTagPlayerComponent.getComponentType());

            if (laserTag == null) continue;

            if(isPlayerAlone) winningTeam = laserTag.getTeamId();

            switch (laserTag.getTeamId()) {
                case 0 -> team0Score += laserTag.getKills();
                case 1 -> team1Score += laserTag.getKills();
            }
        }


        if (team0Score >= maxKills) {
            winningTeam = 0;
        } else if (team1Score >= maxKills) {
            winningTeam = 1;
        }

        if (winningTeam != -1) {

            for (Ref<EntityStore> playerRef : miniGameComponent.getAlivePlayers()) {
                if (playerRef == null || !playerRef.isValid()) continue;


                LaserTagPlayerComponent laserTagPlayerComponent = commandBuffer.getComponent(playerRef, LaserTagPlayerComponent.getComponentType());
                MiniGamePlayerComponent playerComponent = commandBuffer.getComponent(playerRef, MiniGamePlayerComponent.getComponentType());

                if (laserTagPlayerComponent == null || playerComponent == null) continue;

                if (laserTagPlayerComponent.getTeamId() == winningTeam) {
                    playerComponent.setPlayerState(MiniGamePlayerState.WINNER);
                }

                Player player = commandBuffer.getComponent(playerRef, Player.getComponentType());
                assert player != null;
                LaserTagUtils.clearLaserTagPlayerInventory(store, player);
            }

            miniGameComponent.setState(MiniGameState.ENDING);
        }
    }

    // HELPERS METHODS
    private void spawnArena(World world, Store<EntityStore> store){
        BlockSelection prefabFromAssets = PrefabStore.get().getAssetPrefabFromAnyPack("Block_H13_Arena.prefab.json");
        BlockSelection prefab = new BlockSelection(prefabFromAssets);

        world.execute(() -> {
            prefab.placeNoReturn(world, arenaTargetSpawnPosition, store);

            // load spawn marker blocks
            loadTeamSpawnMarkerBlocks(world);
        });
    }

    private void loadTeamSpawnMarkerBlocks(World world) {
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

    private void assignTeams(World world, Collection<PlayerRef> allPlayerRefs, Store<EntityStore> store) {
        AtomicInteger team = new AtomicInteger();

        world.execute(() -> {
            // BroadcastMessage.toWorld(world, "Assigning teams", MessageType.DEBUG);
            allPlayerRefs.forEach(playerRef -> {
                if(playerRef.getReference() != null){
                    Player player = store.getComponent(playerRef.getReference(), Player.getComponentType());
                    if(player == null) return;

                    if(store.getComponent(playerRef.getReference(), LaserTagPlayerComponent.getComponentType()) != null) return;

                    LaserTagUtils.assignTeam(team.get(), store, playerRef.getReference());

                    team.set((team.get() + 1) % 2);
                }
            });

            teleportPlayersToTeamSpawnMarkers(world, allPlayerRefs, store);
        });
    }

    public void teleportPlayersToTeamSpawnMarkers(
            World world,
            Collection<PlayerRef> players,
            Store<EntityStore> store
    ) {

        Vector3d arenaCenter = arenaTargetSpawnPosition.toVector3d();

        world.execute(() -> {

            for (PlayerRef playerRef : players) {

                Ref<EntityStore> playerEntity = playerRef.getReference();
                if (playerEntity == null) continue;

                LaserTagPlayerComponent laserTag = store.getComponent(playerEntity, LaserTagPlayerComponent.getComponentType());

                if (laserTag == null) continue;

                int teamId = laserTag.getTeamId();

                List<Transform> spawns = markerTeamSpawnPoints.get(teamId);

                if (spawns == null || spawns.isEmpty()) {
                    BroadcastMessage.toPlayer(playerRef,
                            "No spawn available for your team!",
                            MessageType.ERROR);
                    continue;
                }

                // Pick random spawn
                Transform spawn = spawns.get(ThreadLocalRandom.current().nextInt(spawns.size()));

                Vector3d spawnPos = spawn.getPosition();

                // Make player face arena center
                double dx = arenaCenter.getX() - spawnPos.getX();
                double dz = arenaCenter.getZ() - spawnPos.getZ();

                float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));

                Vector3f rotation = new Vector3f(0f, yaw, 0f);

                Teleport teleport = Teleport.createForPlayer(
                        world,
                        spawnPos,
                        rotation
                );

                store.addComponent(playerEntity, Teleport.getComponentType(), teleport);
            }

            BroadcastMessage.toWorld(world, "Teleporting players to team spawns...", MessageType.SUCCESS);
        });
    }
}