package fr.seeeek.carpenterprotocol.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.common.BroadcastMessage;
import fr.seeeek.carpenterprotocol.common.MessageType;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class OverrideLaserTagPlayerComponentKillsCommand extends AbstractPlayerCommand {

    public OverrideLaserTagPlayerComponentKillsCommand(){
        super("overrideplayerlaser", "Set LaserTagPlayerComponent to 50 kills to win the game");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        if(playerRef.getReference() == null || !playerRef.isValid()) return;

        LaserTagPlayerComponent laserTagPlayerComponent = store.getComponent(playerRef.getReference(), LaserTagPlayerComponent.getComponentType());

        if (laserTagPlayerComponent != null) {
            laserTagPlayerComponent.setKills(50);
            BroadcastMessage.toPlayer(playerRef, "Your LaserTagPlayerComponent.Kills has been overridden to 50 kills to win the game", MessageType.DEBUG);
        }else{
            BroadcastMessage.toPlayer(playerRef, "No LaserTagPlayerComponent founded on Player : " + playerRef.getUsername(), MessageType.ERROR);
        }
    }
}