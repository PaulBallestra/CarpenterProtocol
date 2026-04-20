package fr.seeeek.carpenterprotocol.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;

public class LaserTagPlayerDeathSystem extends DeathSystems.OnDeathSystem{
    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        PlayerRef victimPlayerRef = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
        if (victimPlayerRef != null) {
            EntityStore entityStore = commandBuffer.getExternalData();
            World world = entityStore.getWorld();
            world.execute(() -> {
                Ref<EntityStore> playerEntityRef = victimPlayerRef.getReference();
                if (playerEntityRef != null && playerEntityRef.isValid()) {
                    LaserTagPlayerComponent victimLaserTagPlayerComponent = store.getComponent(playerEntityRef, LaserTagPlayerComponent.getComponentType());

                    if(victimLaserTagPlayerComponent == null) return;

                    Store<EntityStore> liveStore = playerEntityRef.getStore();
                    Player playerComponent = liveStore.getComponent(playerEntityRef, Player.getComponentType());
                    if (playerComponent != null) playerComponent.getPageManager().setPage(playerEntityRef, liveStore, Page.None);

                    Damage damage = component.getDeathInfo();
                    if(damage == null) return;

                    if(damage.getSource() instanceof Damage.EntitySource damageEntitySource){
                        LaserTagPlayerComponent killerLaserTagPlayerComponent = store.getComponent(damageEntitySource.getRef(), LaserTagPlayerComponent.getComponentType());
                        if(killerLaserTagPlayerComponent == null) return;
                        if(victimLaserTagPlayerComponent.getTeamId() == killerLaserTagPlayerComponent.getTeamId()) return;

                        victimLaserTagPlayerComponent.addDeath();
                        killerLaserTagPlayerComponent.addKill();
                    }
                }
            });
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return LaserTagPlayerComponent.getComponentType();
    }
}
