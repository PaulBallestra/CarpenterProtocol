package fr.seeeek.carpenterprotocol.commands.debug;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.npc.INonPlayerCharacter;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.util.InventoryHelper;
import fr.seeeek.carpenterprotocol.common.BroadcastMessage;
import fr.seeeek.carpenterprotocol.common.MessageType;
import it.unimi.dsi.fastutil.Pair;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Objects;

public class SpawnLaserTagBotsCommand extends AbstractPlayerCommand {

    public SpawnLaserTagBotsCommand(){
        super("spawnlasertagbots", "Spawn 2 Laser Tag Bots (one for each team)");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        if(playerRef.getReference() == null || !playerRef.isValid()) return;

        BroadcastMessage.toPlayer(playerRef, "Spawning Red and Blue Bots for Laser Tag...", MessageType.DEBUG);

        Pair<Ref<EntityStore>, INonPlayerCharacter> lasertagRedBot = NPCPlugin.get().spawnNPC(store, "Laser_Tag_Red_Bot", "Red_Team", playerRef.getTransform().getPosition(), playerRef.getTransform().getRotation());
        Pair<Ref<EntityStore>, INonPlayerCharacter> lasertagBlueBot = NPCPlugin.get().spawnNPC(store, "Laser_Tag_Blue_Bot", "Blue_Team", playerRef.getTransform().getPosition(), playerRef.getTransform().getRotation());

        if(lasertagRedBot != null){
            Ref<EntityStore> redBotRef = lasertagRedBot.first();
            // INonPlayerCharacter redBotNpc = lasertagRedBot.second(); // if need for further interaction

            setupBotArmor(redBotRef, store, 0);
        }

        if(lasertagBlueBot != null) {
            Ref<EntityStore> blueBotRef = lasertagBlueBot.first();
            // INonPlayerCharacter blueBotNpc = lasertagBlueBot.second(); // if need for further interaction

            setupBotArmor(blueBotRef, store, 1);
        }

        BroadcastMessage.toPlayer(playerRef, "Bots spawned!", MessageType.SUCCESS);
    }

    private void setupBotArmor(Ref<EntityStore> ref, Store<EntityStore> store, int teamId) {
        NPCEntity npcEntity = store.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));

        if(npcEntity == null) return;

        Inventory npcEntityInventory = npcEntity.getInventory();
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

        InventoryHelper.useArmor(npcEntityInventory.getArmor(), armorHeadItemId);
        InventoryHelper.useArmor(npcEntityInventory.getArmor(), armorChestItemId);
        InventoryHelper.useArmor(npcEntityInventory.getArmor(), armorHandsItemId);
        InventoryHelper.useArmor(npcEntityInventory.getArmor(), armorLegsItemId);
    }
}
