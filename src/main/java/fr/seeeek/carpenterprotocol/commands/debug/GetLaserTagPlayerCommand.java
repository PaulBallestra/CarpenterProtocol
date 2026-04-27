package fr.seeeek.carpenterprotocol.commands.debug;

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

public class GetLaserTagPlayerCommand extends AbstractPlayerCommand {
    public GetLaserTagPlayerCommand() {
        super("getplayerlaser", "Get player laser tag component");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        if(!playerRef.isValid() || playerRef.getReference() == null) return;
        LaserTagPlayerComponent laserTagPlayerComponent = playerRef.getReference().getStore().getComponent(playerRef.getReference(), LaserTagPlayerComponent.getComponentType());

        if(laserTagPlayerComponent != null){
            BroadcastMessage.toPlayer(playerRef,"----- LaserTagPlayerComponent ----- ", MessageType.DEBUG);
            BroadcastMessage.toPlayer(playerRef,"TeamId : " + laserTagPlayerComponent.getTeamId(), MessageType.DEBUG);
            BroadcastMessage.toPlayer(playerRef,"Kills : " + laserTagPlayerComponent.getKills(), MessageType.DEBUG);
            BroadcastMessage.toPlayer(playerRef,"Deaths : " + laserTagPlayerComponent.getDeaths(), MessageType.DEBUG);
            BroadcastMessage.toPlayer(playerRef,"Reference : " + laserTagPlayerComponent, MessageType.DEBUG);
        }else{
            BroadcastMessage.toPlayer(playerRef,"No LaserTagPlayerComponent found on Player : " + playerRef.getUsername(), MessageType.ERROR);
        }
    }
}