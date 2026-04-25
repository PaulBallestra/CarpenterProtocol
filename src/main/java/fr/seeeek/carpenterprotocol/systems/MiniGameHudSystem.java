package fr.seeeek.carpenterprotocol.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;
import fr.seeeek.carpenterprotocol.components.LobbyComponent;
import fr.seeeek.carpenterprotocol.components.MiniGameComponent;
import fr.seeeek.carpenterprotocol.components.MiniGameHudComponent;
import fr.seeeek.carpenterprotocol.huds.MiniGameInGameHud;

public class MiniGameHudSystem extends EntityTickingSystem<EntityStore> {

    @Override
    public void tick(float dt,
                     int i,
                     ArchetypeChunk<EntityStore> chunk,
                     Store<EntityStore> store,
                     CommandBuffer<EntityStore> commandBuffer) {

        LobbyComponent lobbyComponent = chunk.getComponent(i, LobbyComponent.getComponentType());
        MiniGameComponent miniGameComponent = chunk.getComponent(i, MiniGameComponent.getComponentType());

        if (miniGameComponent == null || lobbyComponent == null) return;

        World world = store.getExternalData().getWorld();

        for (PlayerRef playerRef : world.getPlayerRefs()) {

            if (!playerRef.isValid() || playerRef.getReference() == null)
                continue;

            Ref<EntityStore> playerEntity = playerRef.getReference();

            MiniGameHudComponent hudComponent = commandBuffer.getComponent(playerEntity, MiniGameHudComponent.getComponentType());
            if (hudComponent == null)
                continue;

            LaserTagPlayerComponent laserTagPlayerComponent = commandBuffer.getComponent(playerEntity, LaserTagPlayerComponent.getComponentType());


            if(laserTagPlayerComponent == null)
                continue;

            if (!hudComponent.isDifferent(miniGameComponent, laserTagPlayerComponent))
                continue;

            Player player = commandBuffer.getComponent(playerEntity, Player.getComponentType());

            if (player == null)
                continue;

            if (player.getHudManager().getCustomHud() instanceof MiniGameInGameHud miniGameInGameHud) {
                miniGameInGameHud.refresh(miniGameComponent, lobbyComponent, laserTagPlayerComponent);
                hudComponent.cache(miniGameComponent, laserTagPlayerComponent);
            }
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(MiniGameComponent.getComponentType());
    }
}