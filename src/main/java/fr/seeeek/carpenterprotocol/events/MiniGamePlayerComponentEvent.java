package fr.seeeek.carpenterprotocol.events;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;
import fr.seeeek.carpenterprotocol.components.MiniGameHudComponent;
import fr.seeeek.carpenterprotocol.components.MiniGamePlayerComponent;
import fr.seeeek.carpenterprotocol.huds.MiniGameInGameHud;

import java.awt.*;

public class MiniGamePlayerComponentEvent {

    public static void onPlayerReady(PlayerReadyEvent event){
        Player player = event.getPlayer();

        if(player.getReference() == null || player.getWorld() == null) return;

        PlayerRef playerRef = player.getWorld().getEntityStore().getStore().getComponent(event.getPlayerRef(), PlayerRef.getComponentType());
        // player.sendMessage(Message.raw("COMPONENT " + player.getWorld().getEntityStore().getStore().getComponent(event.getPlayerRef(), MiniGamePlayerComponent.getComponentType())));

        // remove MiniGamePlayerComponent
        if(playerRef == null || playerRef.getReference() == null || !playerRef.isValid()) return;

        if(player.getWorld().getName().startsWith("default")) {
            player.getWorld().execute(() -> {
                MiniGameHudComponent miniGameHudComponent = player.getReference().getStore().getComponent(player.getReference(), MiniGameHudComponent.getComponentType());
                if(miniGameHudComponent != null){
                    CustomUIHud customHud = player.getHudManager().getCustomHud();
                    if (customHud != null) {
                        UICommandBuilder builder = new UICommandBuilder();
                        customHud.update(true, builder);
                    }

                    player.getReference().getStore().removeComponent(player.getReference(), MiniGameHudComponent.getComponentType());
                    // BroadcastMessage.toPlayer(playerRef, "- Removed MiniGameHudComponent", MessageType.DEBUG);
                }

                MiniGamePlayerComponent miniGamePlayerComponent = player.getReference().getStore().getComponent(player.getReference(), MiniGamePlayerComponent.getComponentType());
                if (miniGamePlayerComponent != null) {
                    player.getReference().getStore().removeComponent(player.getReference(), MiniGamePlayerComponent.getComponentType());
                    // BroadcastMessage.toPlayer(playerRef, "- Removed MiniGamePlayerComponent", MessageType.DEBUG);
                }

                LaserTagPlayerComponent laserTagPlayerComponent = player.getReference().getStore().getComponent(player.getReference(), LaserTagPlayerComponent.getComponentType());
                if (laserTagPlayerComponent != null) {
                    player.getReference().getStore().removeComponent(player.getReference(), LaserTagPlayerComponent.getComponentType());
                    // BroadcastMessage.toPlayer(playerRef, "- Removed LaserTagPlayerComponent", MessageType.DEBUG);
                }
            });
        }

        // add MiniGamePlayerComponent if it's a mini game instance
        if(player.getWorld().getName().startsWith("mini_game-")){
            MiniGamePlayerComponent miniGamePlayerComponent = new MiniGamePlayerComponent();
            MiniGameHudComponent miniGameHudComponent = new MiniGameHudComponent();
            Store<EntityStore> store = player.getReference().getStore();

            player.getWorld().execute(() -> {
                player.setGameMode(event.getPlayerRef(), GameMode.Adventure, player.getReference().getStore());
                store.addComponent(event.getPlayerRef(), MiniGamePlayerComponent.getComponentType(), miniGamePlayerComponent);
                store.addComponent(event.getPlayerRef(), MiniGameHudComponent.getComponentType(), miniGameHudComponent);

                MiniGameInGameHud miniGameInGameHud = new MiniGameInGameHud(playerRef, 12, 2, "10:00");
                player.getHudManager().setCustomHud(playerRef, miniGameInGameHud);

//                if(player.getWorld().getName().startsWith("epixia-mini_game-laser_tag")){
//                    LaserTagInGameHud laserTagInGameHud = new LaserTagInGameHud(playerRef);
//                    player.getHudManager().setCustomHud(playerRef, laserTagInGameHud);
//                }

                // BroadcastMessage.toPlayer(playerRef, "+ Added MiniGameHudComponent", MessageType.DEBUG);
            });
        }
    }
}
