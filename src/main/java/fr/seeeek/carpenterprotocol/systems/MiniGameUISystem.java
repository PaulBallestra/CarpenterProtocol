package fr.seeeek.carpenterprotocol.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import fr.seeeek.carpenterprotocol.common.BroadcastMessage;
import fr.seeeek.carpenterprotocol.common.MessageType;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;
import fr.seeeek.carpenterprotocol.components.MiniGameComponent;
import fr.seeeek.carpenterprotocol.components.MiniGamePlayerComponent;
import fr.seeeek.carpenterprotocol.components.MiniGameUIComponent;
import fr.seeeek.carpenterprotocol.enums.MiniGamePlayerState;
import fr.seeeek.carpenterprotocol.enums.MiniGameState;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class MiniGameUISystem extends EntityTickingSystem<EntityStore> {

    private String winnerDisplayName = "Forced ending";

    @Override
    public void tick(float dt, int i,
                     @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store,
                     @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

        MiniGameComponent game = archetypeChunk.getComponent(i, MiniGameComponent.getComponentType());
        MiniGameUIComponent ui = archetypeChunk.getComponent(i, MiniGameUIComponent.getComponentType());
        World world = store.getExternalData().getWorld();

        if (world.getPlayerCount() == 0) return;

        // Accumulate time
        assert ui != null;
        ui.setRefreshTimer(ui.getRefreshTimer() + dt);

        if (ui.getRefreshTimer() < ui.getRefreshInterval()) {
            return;
        }

        // Reset timer
        ui.setRefreshTimer(0f);

        assert game != null;

        int seconds = (int) Math.ceil(game.getStartingTimer());
        if (seconds > 0 && Math.ceil(game.getStartingTimer() - seconds) < dt && game.getState() == MiniGameState.STARTING) {
            showMinorUITitle(store, seconds, world);
        }

        if(!game.isStartingTitleShown() && game.getState() == MiniGameState.RUNNING){
            game.setIsStartingTitleShown(true);
            showMajorUITitle(store, "Game has started", "GO!", 1.6f);
        }

        if (game.getState() == MiniGameState.ENDING && !game.isWinnerTitleShown()) {
            game.setIsWinnerTitleShown(true);

            store.forEachChunk(MiniGamePlayerComponent.getComponentType(), (chunk, cb) -> {
                for (int j = 0; j < chunk.size(); j++) {
                    MiniGamePlayerComponent playerComp = chunk.getComponent(j, MiniGamePlayerComponent.getComponentType());

                    assert playerComp != null;
                    if (playerComp.getPlayerState() == MiniGamePlayerState.WINNER) {
                        PlayerRef playerRef = chunk.getComponent(j, PlayerRef.getComponentType());

                        if (playerRef != null && playerRef.isValid()) {
                            Ref<EntityStore> entityStoreRef = playerRef.getReference();
                            if(entityStoreRef != null && entityStoreRef.isValid()){
                                LaserTagPlayerComponent laserTagPlayerComponent = cb.getComponent(playerRef.getReference(), LaserTagPlayerComponent.getComponentType());

                                if(laserTagPlayerComponent != null){
                                    switch (laserTagPlayerComponent.getTeamId()){
                                        case 0:
                                            winnerDisplayName = "Red Team";
                                            break;
                                        case 1:
                                            winnerDisplayName = "Blue Team";
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
            });

            // --- SECURE ECS ITERATION END ---
            showMajorUITitle(store, "Game over !", "Winner: " + winnerDisplayName, 6.4f);
        }

        if(game.getState() == MiniGameState.ENDING){
            String message = buildUIMessage(game, world);

            world.getPlayerRefs().forEach(p ->
                    BroadcastMessage.toPlayer(p, message, MessageType.INFO)
            );
        }

    }

    private void showMajorUITitle(Store<EntityStore> store, String subMsg, String headerMsg, float duration){
        EventTitleUtil.showEventTitleToWorld(
                Message.raw(headerMsg),
                Message.raw(subMsg),
                true,
                null,
                duration,
                0.1f,
                0.1f,
                store);

        // playTimerEventSound(store, "SFX_Ore_Break");
    }

    private void showMinorUITitle(Store<EntityStore> store, int timeLeft, World world){
        EventTitleUtil.showEventTitleToWorld(
                Message.raw("%d".formatted(timeLeft)),
                Message.raw("Game starts in"),
                false,
                null,
                0.8f,
                0.1f,
                0.1f,
                store);

        if(timeLeft > 9) {
            playTimerEventSound(store, "SFX_Rotating_Cube");
        }
        if(timeLeft == 5 || timeLeft == 3 || timeLeft == 1) playTimerEventSound(store, "SFX_Counter_Punch");
    }

    private String buildUIMessage(MiniGameComponent game, World world) {
        String state = game.getState().name();
        int players = world.getPlayerCount();
        float startingTimer = game.getStartingTimer();
        float endingTimer = game.getEndingTimer();

        return switch (game.getState()) {
            case PENDING -> "State: " + state + " | Players: " + players + "/" + game.getMinPlayers();
            case STARTING -> "Starting in: " + Math.ceil(startingTimer) + "s";
            case RUNNING -> "Running | Players alive: " + players;
            case ENDING -> "Teleporting in: " + Math.ceil(endingTimer) + "s";
            case FINISHED -> "Game Finished";
            default -> "Preparing game...";
        };
    }

    private void playTimerEventSound(Store<EntityStore> store, String assetMapSoundKey){
        int index = SoundEvent.getAssetMap().getIndex(assetMapSoundKey);
        SoundUtil.playSoundEvent2d(index, SoundCategory.UI, store);
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(
                MiniGameComponent.getComponentType(),
                MiniGameUIComponent.getComponentType()
        );
    }
}
