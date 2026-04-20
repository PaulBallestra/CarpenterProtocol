package fr.seeeek.carpenterprotocol.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.SavedMovementStates;
import com.hypixel.hytale.protocol.packets.player.SetMovementStates;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.cosmetics.CosmeticsModule;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.common.BroadcastMessage;
import fr.seeeek.carpenterprotocol.common.MessageType;
import fr.seeeek.carpenterprotocol.components.MiniGameHudComponent;


public class MiniGameUtils {
    public static void makeSpectator(Player player, PlayerRef playerRef) {
        if (player == null || !playerRef.isValid() || playerRef.getReference() == null) return;

        BroadcastMessage.toPlayer(playerRef, "You've been set to spectator", MessageType.INFO);


        assert player.getWorld() != null;
        player.getWorld().execute(
                () -> {
                    Store<EntityStore> store = player.getWorld().getEntityStore().getStore();
                    Ref<EntityStore> entityStoreRef = player.getReference();

                    if (entityStoreRef == null || !entityStoreRef.isValid()) return;

                    // FLY
                    playerRef.getPacketHandler().writeNoCache(new SetMovementStates(new SavedMovementStates(true)));

                    // TODO : NO CLIP / NO COLLISION

                    // MODEL
                    float scale = 1.0f;
                    ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("Chicken");
                    assert modelAsset != null;
                    // player.sendMessage(Message.raw("Model to override with " + modelAsset));
                    Model model = Model.createScaledModel(modelAsset, scale);
                    store.putComponent(entityStoreRef, ModelComponent.getComponentType(), new ModelComponent(model));
                }
        );

        // player.sendMessage(Message.raw("[DEBUG] - Model changed to Chicken").color(Color.ORANGE));
    }

    public static void restoreMovementFromPlayer(PlayerRef playerRef){
        assert playerRef != null;
        playerRef.getPacketHandler().writeNoCache(new SetMovementStates(new SavedMovementStates(false)));
        // BroadcastMessage.toPlayer(playerRef, "MovementStates changed to flying false", MessageType.DEBUG);
    }

    public static void restoreHudForPlayer(Player player, World world, Store<EntityStore> store){
        if(player.getReference() == null || !player.getReference().isValid()) return;

        world.execute(() -> {
            PlayerRef playerRef = store.getComponent(player.getReference(), PlayerRef.getComponentType());
            CustomUIHud customHud = player.getHudManager().getCustomHud();

            if (customHud != null) {
                UICommandBuilder builder = new UICommandBuilder();
                customHud.update(true, builder);
            }

            store.removeComponent(player.getReference(), MiniGameHudComponent.getComponentType());
            // BroadcastMessage.toPlayer(playerRef, "- Removed MiniGameHudComponent", MessageType.DEBUG);
        });
    }

    public static void restoreModelFromPlayer(Player player, World world){
        if(player.getReference() == null || !player.getReference().isValid()) return;

        // BroadcastMessage.toPlayer(player.getReference().getStore().getComponent(player.getReference(), PlayerRef.getComponentType()), "Model restored", MessageType.DEBUG);

        world.execute(() -> {
            Store<EntityStore> store = world.getEntityStore().getStore();
            PlayerSkinComponent skin = store.getComponent(player.getReference(), PlayerSkinComponent.getComponentType());
            if (skin == null) return;

            Model normalModel = CosmeticsModule.get().createModel(skin.getPlayerSkin());
            store.putComponent(player.getReference(), ModelComponent.getComponentType(), new ModelComponent(normalModel));
            skin.setNetworkOutdated();
        });
    }
}
