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
import fr.seeeek.carpenterprotocol.components.MiniGamePlayerComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class GetMiniGamePlayerStateCommand extends AbstractPlayerCommand {
    public GetMiniGamePlayerStateCommand() {
        super("getplayer", "Get player mini game state");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        if(!playerRef.isValid() || playerRef.getReference() == null) return;
        MiniGamePlayerComponent miniGamePlayerComponent = playerRef.getReference().getStore().getComponent(playerRef.getReference(), MiniGamePlayerComponent.getComponentType());

        if(miniGamePlayerComponent != null){
            BroadcastMessage.toPlayer(playerRef, "----- MiniGamePlayerComponent -----", MessageType.DEBUG);
            BroadcastMessage.toPlayer(playerRef,"State : " + miniGamePlayerComponent.getPlayerState(), MessageType.DEBUG);
        }else{
            BroadcastMessage.toPlayer(playerRef,"No MiniGamePlayerComponent found on Player : " + playerRef.getUsername(), MessageType.ERROR);
        }
    }
}