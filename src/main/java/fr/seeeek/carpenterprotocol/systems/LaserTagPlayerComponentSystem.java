package fr.seeeek.carpenterprotocol.systems;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LaserTagPlayerComponentSystem extends RefChangeSystem<EntityStore, LaserTagPlayerComponent> {
    @NonNullDecl
    @Override
    public ComponentType<EntityStore, LaserTagPlayerComponent> componentType() {
        return LaserTagPlayerComponent.getComponentType();
    }

    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl LaserTagPlayerComponent laserTagPlayerComponent, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        assert player != null;

        assignLaserTagStuff(player, laserTagPlayerComponent.getTeamId());
    }

    @Override
    public void onComponentSet(@NonNullDecl Ref<EntityStore> ref, @NullableDecl LaserTagPlayerComponent laserTagPlayerComponent, @NonNullDecl LaserTagPlayerComponent laserTagPlayerComponent1, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
    }

    @Override
    public void onComponentRemoved(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl LaserTagPlayerComponent laserTagPlayerComponent, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        assert player != null;

        removeStuff(player, laserTagPlayerComponent.getTeamId());
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return LaserTagPlayerComponent.getComponentType();
    }


    private void assignLaserTagStuff(Player player, int teamId){
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

        Inventory playerInventory = player.getInventory();
        ItemContainer hotbarContainer = playerInventory.getHotbar();
        hotbarContainer.addItemStackToSlot((short) 0, laserTagWeaponGunItem);

        ItemContainer armorContainer = playerInventory.getArmor();
        armorContainer.addItemStack(laserTagArmorHeadItem);
        armorContainer.addItemStack(laserTagArmorChestItem);
        armorContainer.addItemStack(laserTagArmorHandsItem);
        armorContainer.addItemStack(laserTagArmorLegsItem);
    }

    private void removeStuff(Player player, int teamId){
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

        Inventory playerInventory = player.getInventory();
        ItemContainer hotbarContainer = playerInventory.getHotbar();
        hotbarContainer.removeItemStack(laserTagWeaponGunItem);

        ItemContainer armorContainer = playerInventory.getArmor();
        armorContainer.removeItemStack(laserTagArmorHeadItem);
        armorContainer.removeItemStack(laserTagArmorChestItem);
        armorContainer.removeItemStack(laserTagArmorHandsItem);
        armorContainer.removeItemStack(laserTagArmorLegsItem);
    }
}
