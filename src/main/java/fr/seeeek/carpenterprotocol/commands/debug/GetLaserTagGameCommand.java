package fr.seeeek.carpenterprotocol.commands.debug;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.permissions.HytalePermissions;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.common.BroadcastMessage;
import fr.seeeek.carpenterprotocol.common.MessageType;
import fr.seeeek.carpenterprotocol.components.LaserTagGameComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.atomic.AtomicBoolean;

public class GetLaserTagGameCommand extends AbstractPlayerCommand {

    public GetLaserTagGameCommand(){
        super("getlasertag", "Returns all the values for the laser tag mini-game you're in");

        requirePermission(HytalePermissions.fromCommand("admin"));
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        if(playerRef.getReference() == null || !playerRef.isValid()) return;

        AtomicBoolean lasertagGameComponentExists = new AtomicBoolean(false);

        store.forEachChunk((LaserTagGameComponent.getComponentType()), (chunk, _) -> {
            for (int i = 0; i < chunk.size(); i++) {
                LaserTagGameComponent laserTagGameComponent = chunk.getComponent(i, LaserTagGameComponent.getComponentType());
                if(laserTagGameComponent != null){
                    lasertagGameComponentExists.set(true);
                    BroadcastMessage.toPlayer(playerRef, "----- LaserTagGameComponent -----", MessageType.DEBUG);
                    BroadcastMessage.toPlayer(playerRef, "MaxKillsPerTeam : " + laserTagGameComponent.getMaxKillsPerTeam(), MessageType.DEBUG);
                    BroadcastMessage.toPlayer(playerRef, "MaxGameTime : " + laserTagGameComponent.getMaxGameTime(), MessageType.DEBUG);
                    BroadcastMessage.toPlayer(playerRef, "TeamCount : " + laserTagGameComponent.getTeamCount(), MessageType.DEBUG);
                    for (int teamTmp = 0; teamTmp < laserTagGameComponent.getTeamCount(); teamTmp++){
                        BroadcastMessage.toPlayer(playerRef, "Team " + teamTmp + " : " + laserTagGameComponent.getTeamKills(teamTmp), MessageType.DEBUG);
                    }
                    return;
                }
            }
        });

        if(!lasertagGameComponentExists.get()) BroadcastMessage.toPlayer(playerRef, "No LaserTagGameComponent founded on World : " + world.getName(), MessageType.ERROR);
    }
}