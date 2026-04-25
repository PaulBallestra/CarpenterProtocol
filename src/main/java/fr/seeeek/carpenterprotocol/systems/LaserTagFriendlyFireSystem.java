package fr.seeeek.carpenterprotocol.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;

public class LaserTagFriendlyFireSystem extends DamageEventSystem {

    @Override
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getFilterDamageGroup();
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk,
                       Store<EntityStore> store,
                       CommandBuffer<EntityStore> commandBuffer,
                       Damage damage) {

        if (!(damage.getSource() instanceof Damage.EntitySource source)) return;

        Ref<EntityStore> victimRef = chunk.getReferenceTo(index);

        LaserTagPlayerComponent victim = store.getComponent(victimRef, LaserTagPlayerComponent.getComponentType());

        LaserTagPlayerComponent attacker =  store.getComponent(source.getRef(), LaserTagPlayerComponent.getComponentType());

        if (victim == null || attacker == null) return;

        if (victim.getTeamId() == attacker.getTeamId()) {
            damage.setCancelled(true);
        }
    }
}