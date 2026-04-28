package fr.seeeek.carpenterprotocol.systems;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.common.BroadcastMessage;
import fr.seeeek.carpenterprotocol.common.MessageType;
import fr.seeeek.carpenterprotocol.components.MiniGameComponent;
import fr.seeeek.carpenterprotocol.components.MiniGamePlayerComponent;
import fr.seeeek.carpenterprotocol.enums.MiniGamePlayerState;
import fr.seeeek.carpenterprotocol.enums.MiniGameState;
import fr.seeeek.carpenterprotocol.interfaces.MiniGameLogic;
import fr.seeeek.carpenterprotocol.registry.MiniGameLogicRegistry;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.*;

import static fr.seeeek.carpenterprotocol.utils.MiniGameUtils.*;

public class MiniGameSystem extends EntityTickingSystem<EntityStore> {

    @Override
    public void tick(float dt, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        // 1. Retrieve the component and the entity reference
        World world = store.getExternalData().getWorld();
        Collection<PlayerRef> allPlayerRefs = world.getPlayerRefs();
        Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(i);
        MiniGameComponent game = world.getEntityStore().getStore().getComponent(entityRef, MiniGameComponent.getComponentType());

        assert game != null;
        MiniGameLogic minigameLogic = MiniGameLogicRegistry.get(game.getMiniGameType());

        // 2. Process State Machine
        switch (game.getState()) {
            case CREATED:
                game.setState(MiniGameState.PENDING);
                break;

            case PENDING:
                // Check if enough players are in the world
                if (world.getPlayerCount() >= game.getMinPlayers()) {
                    game.setState(MiniGameState.STARTING);
                    game.setStartingTimer(game.getStartingTimer());
                    game.setEndingTimer(game.getEndingTimer());

                    minigameLogic.setup(commandBuffer, world, game, dt, store, entityRef, allPlayerRefs);
                }
                break;

            case STARTING:
                game.setStartingTimer(game.getStartingTimer() - dt);

                if (game.getStartingTimer() <= 0) {
                    world.execute(() -> {
                        allPlayerRefs.forEach(playerRef -> {
                            if(playerRef.getReference() == null || !playerRef.isValid()) return;

                            MiniGamePlayerComponent playerComponent = store.getComponent(playerRef.getReference(), MiniGamePlayerComponent.getComponentType());
                            if(playerComponent == null) return;

                            playerComponent.setPlayerState(MiniGamePlayerState.ALIVE);
                            game.getAlivePlayers().add(playerRef.getReference());
                        });
                    });
                    minigameLogic.starting(commandBuffer, world, game, dt, store, entityRef, allPlayerRefs);

                    game.setState(MiniGameState.RUNNING);
                }
                break;

            case RUNNING:
                minigameLogic.running(commandBuffer, world, game, dt, store, entityRef);
                break;

            case ENDING:
                game.setEndingTimer(game.getEndingTimer() - dt);
                if (game.getEndingTimer() <= 0) {
                    game.getAlivePlayers().clear();

                    store.forEachChunk(Player.getComponentType(), (chunk, _) -> {
                        for (int j = 0; j < chunk.size(); j++) {
                            Player player = chunk.getComponent(j, Player.getComponentType());
                            assert player != null;
                            restoreHudForPlayer(player, world, store);
                            restoreModelFromPlayer(player, world);
                        }
                    });

                    teleportsBackAllPlayersToLobby(world);
                    game.setState(MiniGameState.FINISHED);
                }
                break;

            case FINISHED:
                world.execute(() -> {
                    commandBuffer.removeComponent(entityRef, MiniGameComponent.getComponentType());
                });
                break;
        }
    }

    private void teleportsBackAllPlayersToLobby(World world) {
        World lobbyWorld = Universe.get().getDefaultWorld();
        assert lobbyWorld != null;

        Collection<PlayerRef> playersRef = world.getPlayerRefs();

        BroadcastMessage.toWorld(world, "Teleporting players to lobby...", MessageType.INFO);

        world.execute(() -> {
            playersRef.forEach(p -> {
                if(p.getReference() == null || !p.isValid()) return;

                Player player = p.getReference().getStore().getComponent(p.getReference(), Player.getComponentType());
                assert player != null;

                restoreMovementFromPlayer(p);

                InstancesPlugin.teleportPlayerToInstance(
                        p.getReference(),
                        p.getReference().getStore(),
                        lobbyWorld,
                        null
                );
            });
        });
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(MiniGameComponent.getComponentType());
    }
}