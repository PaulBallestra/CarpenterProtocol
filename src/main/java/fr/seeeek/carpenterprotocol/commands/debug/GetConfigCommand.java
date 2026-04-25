package fr.seeeek.carpenterprotocol.commands.debug;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.gameplay.*;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.common.BroadcastMessage;
import fr.seeeek.carpenterprotocol.common.MessageType;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class GetConfigCommand extends AbstractPlayerCommand {

    public GetConfigCommand(){
        super("getconfig", "Get and show the GameplayConfig with respawn and combat configs from the world you're in");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        if(playerRef.getReference() == null || !playerRef.isValid()) return;
        WorldConfig worldConfig = world.getWorldConfig();
        GameplayConfig gameplayConfig = world.getGameplayConfig();
        RespawnConfig respawnConfig = gameplayConfig.getRespawnConfig();
        CombatConfig combatConfig = gameplayConfig.getCombatConfig();
        SpawnConfig spawnConfig = gameplayConfig.getSpawnConfig();
        DeathConfig deathConfig = gameplayConfig.getDeathConfig();


        BroadcastMessage.toPlayer(playerRef, "World Config : " + worldConfig.getDisplayName(), MessageType.DEBUG);
        BroadcastMessage.toPlayer(playerRef, "Gameplay Config : " + gameplayConfig.getId(), MessageType.DEBUG);
        BroadcastMessage.toPlayer(playerRef, "Spawn Config : " + spawnConfig, MessageType.DEBUG);
        BroadcastMessage.toPlayer(playerRef, "Respawn Config : " + respawnConfig, MessageType.DEBUG);
        BroadcastMessage.toPlayer(playerRef, "Combat Config : " + combatConfig, MessageType.DEBUG);
        BroadcastMessage.toPlayer(playerRef, "Death Config : " + deathConfig, MessageType.DEBUG);
    }
}
