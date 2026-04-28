package fr.seeeek.carpenterprotocol.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.event.events.ecs.DropItemEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class LaserTagDropSystem extends EntityEventSystem<EntityStore, DropItemEvent.PlayerRequest> {
    private final ComponentType<EntityStore, LaserTagPlayerComponent> laserTagPlayerComponentComponentType = LaserTagPlayerComponent.getComponentType();

    public LaserTagDropSystem() {
        super(DropItemEvent.PlayerRequest.class);
    }

    public Query<EntityStore> getQuery() {
        return Archetype.of(this.laserTagPlayerComponentComponentType);
    }



    @Override
    public void handle(int index, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl DropItemEvent.PlayerRequest playerRequest) {
        LaserTagPlayerComponent laserTagPlayerComponent = (LaserTagPlayerComponent) archetypeChunk.getComponent(index, this.laserTagPlayerComponentComponentType);

        if(laserTagPlayerComponent != null){
            playerRequest.setCancelled(true);
        }
    }
}