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
import fr.seeeek.carpenterprotocol.components.MiniGamePlayerComponent;
import fr.seeeek.carpenterprotocol.enums.MiniGamePlayerState;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class OverridePlayerStateCommand extends AbstractPlayerCommand {

    public OverridePlayerStateCommand(){
        super("overrideplayer", "Set MiniGamePlayerState to the state passed in argument, set next MiniGamePlayerState when no argument passed");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        if(playerRef.getReference() == null || !playerRef.isValid()) return;

        MiniGamePlayerComponent miniGamePlayerComponent = store.getComponent(playerRef.getReference(), MiniGamePlayerComponent.getComponentType());
        MiniGamePlayerState overriddenState = MiniGamePlayerState.ALIVE;

        if (miniGamePlayerComponent != null) {
            miniGamePlayerComponent.setPlayerState(overriddenState);
            BroadcastMessage.toPlayer(playerRef, "Your MiniGamePlayerComponent.State has been overridden to " + overriddenState, MessageType.DEBUG);
        }else{
            BroadcastMessage.toPlayer(playerRef, "No MiniGamePlayerComponent founded on Player : " + playerRef.getUsername(), MessageType.ERROR);
        }
    }
}