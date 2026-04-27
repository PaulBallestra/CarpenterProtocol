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
import fr.seeeek.carpenterprotocol.components.MiniGameComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.atomic.AtomicBoolean;

public class GetMiniGameStateCommand extends AbstractPlayerCommand {

    public GetMiniGameStateCommand(){
        super("getgame", "Returns all the actual parameters for the mini game");
    }


    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        if(playerRef.getReference() == null || !playerRef.isValid()) return;

        AtomicBoolean miniGameComponentExists = new AtomicBoolean(false);

        store.forEachChunk((MiniGameComponent.getComponentType()), (chunk, _) -> {
            for (int i = 0; i < chunk.size(); i++) {
                MiniGameComponent miniGameComponent = chunk.getComponent(i, MiniGameComponent.getComponentType());
                if(miniGameComponent != null){
                    miniGameComponentExists.set(true);
                    BroadcastMessage.toPlayer(playerRef, "----- MiniGameComponent -----", MessageType.DEBUG);
                    BroadcastMessage.toPlayer(playerRef, "Type : " + miniGameComponent.getMiniGameType(), MessageType.DEBUG);
                    BroadcastMessage.toPlayer(playerRef, "State : " + miniGameComponent.getState(), MessageType.DEBUG);
                    return;
                }
            }
        });

        if(!miniGameComponentExists.get()) BroadcastMessage.toPlayer(playerRef, "No MiniGameComponent founded on World : " + world.getName(), MessageType.ERROR);
    }
}