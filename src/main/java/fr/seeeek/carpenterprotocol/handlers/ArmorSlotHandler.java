//package fr.seeeek.carpenterprotocol.handlers;
//
//import com.hypixel.hytale.component.Ref;
//import com.hypixel.hytale.component.Store;
//import com.hypixel.hytale.protocol.InteractionType;
//import com.hypixel.hytale.protocol.Packet;
//import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
//import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
//import com.hypixel.hytale.protocol.packets.inventory.SetActiveSlot;
//import com.hypixel.hytale.server.core.entity.entities.Player;
//import com.hypixel.hytale.server.core.inventory.Inventory;
//import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
//import com.hypixel.hytale.server.core.universe.PlayerRef;
//import com.hypixel.hytale.server.core.universe.world.World;
//import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
//
//import javax.annotation.Nonnull;
//
//public class ArmorSlotHandler implements PlayerPacketFilter {
//
//    private static final int ABILITY_SLOT = 8;  // Slot index 8 = Key "9"
//
//    @Override
//    public boolean test(@Nonnull PlayerRef playerRef, @Nonnull Packet packet) {
//        // Step 1
//        if (!(packet instanceof SyncInteractionChains syncChain)) {
//            return false;
//        }
//        // Step 2
//        for (SyncInteractionChain chain : syncChain.updates) {
//            if (chain.interactionType == InteractionType.SwapFrom
//                    && chain.data != null
//                    && chain.data.targetSlot == ABILITY_SLOT
//                    && chain.initial) {
//                int originalSlot = chain.activeHotbarSlot;
//                handleAbilityTrigger(playerRef, originalSlot);
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private void handleAbilityTrigger(PlayerRef playerRef, int originalSlot) {
//        Ref<EntityStore> entityRef = playerRef.getReference();
//        if (entityRef == null || !entityRef.isValid()) {
//            return;
//        }
//        Store<EntityStore> store = entityRef.getStore();
//        World world = store.getExternalData().getWorld();
//        world.execute(() -> {
//            Player playerComponent = store.getComponent(entityRef, Player.getComponentType());
//            if (playerComponent == null) {
//                return;
//            }
//            // Your ability logic here
//
//            // Update server-side state
//            playerComponent.getInventory().setActiveHotbarSlot((byte) originalSlot);
//
//            // Send packet to force client to the correct slot
//            SetActiveSlot setActiveSlotPacket = new SetActiveSlot(
//                    Inventory.HOTBAR_SECTION_ID,  // -1 indicates the hotbar
//                    originalSlot                   // The slot index to select
//            );
//            playerRef.getPacketHandler().write(setActiveSlotPacket);
//        });
//    }
//}