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

public class SpawnLaserTagBlueBotCommand extends AbstractPlayerCommand {

    public SpawnLaserTagBlueBotCommand(){
        super("spawnlasertagbluebot", "Spawn a Blue Bot for Laser Tag");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        if(playerRef.getReference() == null || !playerRef.isValid()) return;

        BroadcastMessage.toPlayer(playerRef, "Spawning Blue Bot for Laser Tag...", MessageType.DEBUG);

        Pair<Ref<EntityStore>, INonPlayerCharacter> lasertagBlueBot = NPCPlugin.get().spawnNPC(store, "Laser_Tag_Blue_Bot", "Blue_Team", playerRef.getTransform().getPosition(), playerRef.getTransform().getRotation());

        if(lasertagBlueBot != null) {
            Ref<EntityStore> blueBotRef = lasertagBlueBot.first();
            INonPlayerCharacter blueBotNpc = lasertagBlueBot.second(); // if need for further interaction

            BroadcastMessage.toPlayer(playerRef, "Red Bot Npc Type : " + blueBotNpc.getNPCTypeId(), MessageType.DEBUG);

            setupBotArmor(blueBotRef, store);
        }

        BroadcastMessage.toPlayer(playerRef, "Blue Bot spawned!", MessageType.SUCCESS);
    }

    private void setupBotArmor(Ref<EntityStore> ref, Store<EntityStore> store) {
        NPCEntity npcEntity = store.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));

        if(npcEntity == null) return;

        Inventory npcEntityInventory = npcEntity.getInventory();

        String weaponItemId = "Laser_Tag_Weapon_Gun_Blue";
        String armorHeadItemId = "Armor_Laser_Tag_Head_Blue";
        String armorChestItemId = "Armor_Laser_Tag_Chest_Blue";
        String armorHandsItemId = "Armor_Laser_Tag_Hands";
        String armorLegsItemId = "Armor_Laser_Tag_Legs_Blue";


        InventoryHelper.useArmor(npcEntityInventory.getArmor(), armorHeadItemId);
        InventoryHelper.useArmor(npcEntityInventory.getArmor(), armorChestItemId);
        InventoryHelper.useArmor(npcEntityInventory.getArmor(), armorHandsItemId);
        InventoryHelper.useArmor(npcEntityInventory.getArmor(), armorLegsItemId);
    }
}
