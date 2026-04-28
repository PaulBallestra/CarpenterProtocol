package fr.seeeek.carpenterprotocol.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.ItemArmorSlot;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
import com.hypixel.hytale.server.core.inventory.container.filter.SlotFilter;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;

public class LaserTagUtils {
    public static void assignTeam(int team, Store<EntityStore> store, Ref<EntityStore> playerRef){
        LaserTagPlayerComponent laserTagPlayerComponent = new LaserTagPlayerComponent();
        laserTagPlayerComponent.setTeamId(team);

        store.addComponent(playerRef, LaserTagPlayerComponent.getComponentType(), laserTagPlayerComponent);
    }

    public static void assignLaserTagPlayerInventory(Store<EntityStore> store, Player player, int teamId){
        String weaponItemId = "", armorHeadItemId = "", armorChestItemId = "", armorHandsItemId = "", armorLegsItemId = "";

        switch (teamId){
            case 0:
                weaponItemId = "Laser_Tag_Weapon_Gun_Red";
                armorHeadItemId = "Armor_Laser_Tag_Head_Red";
                armorChestItemId = "Armor_Laser_Tag_Chest_Red";
                armorHandsItemId = "Armor_Laser_Tag_Hands";
                armorLegsItemId = "Armor_Laser_Tag_Legs_Red";
                break;
            case 1:
                weaponItemId = "Laser_Tag_Weapon_Gun_Blue";
                armorHeadItemId = "Armor_Laser_Tag_Head_Blue";
                armorChestItemId = "Armor_Laser_Tag_Chest_Blue";
                armorHandsItemId = "Armor_Laser_Tag_Hands";
                armorLegsItemId = "Armor_Laser_Tag_Legs_Blue";
                break;
        }

        ItemStack laserTagWeaponGunItem = new ItemStack(weaponItemId);
        ItemStack laserTagArmorHeadItem = new ItemStack(armorHeadItemId);
        ItemStack laserTagArmorChestItem = new ItemStack(armorChestItemId);
        ItemStack laserTagArmorHandsItem = new ItemStack(armorHandsItemId);
        ItemStack laserTagArmorLegsItem = new ItemStack(armorLegsItemId);

        // NEW VERSION FOR INVENTORY
        assert player.getReference() != null;
        InventoryComponent.Armor armor = store.getComponent(player.getReference(), InventoryComponent.Armor.getComponentType());
        InventoryComponent.Hotbar hotbar = store.getComponent(player.getReference(), InventoryComponent.Hotbar.getComponentType());
        if (armor == null || hotbar == null) return;

        ItemContainer hotbarContainer = hotbar.getInventory();
        hotbarContainer.setItemStackForSlot((short) 0, laserTagWeaponGunItem);

        ItemContainer armorContainer = armor.getInventory();
        armorContainer.setItemStackForSlot((short) ItemArmorSlot.Head.getValue(), laserTagArmorHeadItem);
        armorContainer.setItemStackForSlot((short) ItemArmorSlot.Chest.getValue(), laserTagArmorChestItem);
        armorContainer.setItemStackForSlot((short) ItemArmorSlot.Hands.getValue(), laserTagArmorHandsItem);
        armorContainer.setItemStackForSlot((short) ItemArmorSlot.Legs.getValue(), laserTagArmorLegsItem);

        lockArmor(armorContainer);
    }

    public static void clearLaserTagPlayerInventory(Store<EntityStore> store, Player player){
        assert player.getReference() != null;
        InventoryComponent.Armor armor = store.getComponent(player.getReference(), InventoryComponent.Armor.getComponentType());
        InventoryComponent.Hotbar hotbar = store.getComponent(player.getReference(), InventoryComponent.Hotbar.getComponentType());
        if(armor == null  || hotbar == null) return;


        ItemContainer armorContainer = armor.getInventory();
        unlockArmor(armorContainer);

        armorContainer.removeAllItemStacks();

        ItemContainer hotbarContainer = hotbar.getInventory();
        hotbarContainer.removeAllItemStacks();

    }

    private static void lockArmor(ItemContainer armorInv) {
        for (ItemArmorSlot slot : ItemArmorSlot.VALUES) {
            short index = (short) slot.getValue();

            armorInv.setSlotFilter(FilterActionType.REMOVE, index, SlotFilter.DENY);
            armorInv.setSlotFilter(FilterActionType.DROP, index, SlotFilter.DENY);
        }
    }

    private static void unlockArmor(ItemContainer armorInv) {
        for (ItemArmorSlot slot : ItemArmorSlot.VALUES) {
            short index = (short) slot.getValue();

            armorInv.setSlotFilter(FilterActionType.REMOVE, index, null);
            armorInv.setSlotFilter(FilterActionType.DROP, index, null);
        }
    }
}
