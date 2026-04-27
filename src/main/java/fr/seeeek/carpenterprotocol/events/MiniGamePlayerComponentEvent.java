package fr.seeeek.carpenterprotocol.events;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;
import fr.seeeek.carpenterprotocol.components.MiniGameHudComponent;
import fr.seeeek.carpenterprotocol.components.MiniGamePlayerComponent;
import fr.seeeek.carpenterprotocol.huds.MiniGameInGameHud;

import java.awt.*;

public class MiniGamePlayerComponentEvent {

    public static void onPlayerReady(PlayerReadyEvent event){
        Player player = event.getPlayer();

        World defaultWorld = Universe.get().getDefaultWorld();

        assert defaultWorld != null;

        String defaultWorldName = Universe.get().getDefaultWorld().getName();

        if(player.getReference() == null || player.getWorld() == null) return;

        PlayerRef playerRef = player.getWorld().getEntityStore().getStore().getComponent(event.getPlayerRef(), PlayerRef.getComponentType());

        if(playerRef == null || playerRef.getReference() == null || !playerRef.isValid()) return;

        if(player.getWorld().getName().equals(defaultWorldName)) {
            player.getWorld().execute(() -> {
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
        if(player.getWorld().getName().startsWith("block_h13") || player.getWorld().getName().startsWith("mini_game-laser_tag-")){
            MiniGamePlayerComponent miniGamePlayerComponent = new MiniGamePlayerComponent();
            MiniGameHudComponent miniGameHudComponent = new MiniGameHudComponent();
            Store<EntityStore> store = player.getReference().getStore();

            player.getWorld().execute(() -> {
                player.setGameMode(event.getPlayerRef(), GameMode.Adventure, player.getReference().getStore());
                store.addComponent(event.getPlayerRef(), MiniGamePlayerComponent.getComponentType(), miniGamePlayerComponent);
                store.addComponent(event.getPlayerRef(), MiniGameHudComponent.getComponentType(), miniGameHudComponent);

                MiniGameInGameHud miniGameInGameHud = new MiniGameInGameHud(playerRef, 0, 0);

                player.getHudManager().setCustomHud (playerRef, miniGameInGameHud);

                // inventory
                Inventory inventory = player.getInventory();
                if(inventory != null){
                    inventory.clear();
                    inventory.setUsingToolsItem(false);
                }

                // Check if a game is running, if not go to spawn override fallback, else --> auto join team

                // Spawn override fallback
                ISpawnProvider spawnProvider = player.getWorld().getWorldConfig().getSpawnProvider();
                if (spawnProvider == null) return;

                Transform spawnTransform = spawnProvider.getSpawnPoint(player.getWorld(), playerRef.getUuid());
                Teleport teleport = Teleport.createForPlayer(player.getWorld(), spawnTransform);
                store.addComponent(event.getPlayerRef(), Teleport.getComponentType(), teleport);
            });
        }
    }
}
