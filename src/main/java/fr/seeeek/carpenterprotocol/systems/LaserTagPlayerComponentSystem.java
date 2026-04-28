package fr.seeeek.carpenterprotocol.systems;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;
import fr.seeeek.carpenterprotocol.utils.LaserTagUtils;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LaserTagPlayerComponentSystem extends RefChangeSystem<EntityStore, LaserTagPlayerComponent> {
    @NonNullDecl
    @Override
    public ComponentType<EntityStore, LaserTagPlayerComponent> componentType() {
        return LaserTagPlayerComponent.getComponentType();
    }

    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl LaserTagPlayerComponent laserTagPlayerComponent, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        assert player != null;

        LaserTagUtils.assignLaserTagPlayerInventory(store, player, laserTagPlayerComponent.getTeamId());
    }

    @Override
    public void onComponentSet(@NonNullDecl Ref<EntityStore> ref, @NullableDecl LaserTagPlayerComponent laserTagPlayerComponent, @NonNullDecl LaserTagPlayerComponent laserTagPlayerComponent1, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
    }

    @Override
    public void onComponentRemoved(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl LaserTagPlayerComponent laserTagPlayerComponent, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        assert player != null;

        LaserTagUtils.clearLaserTagPlayerInventory(store, player);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return LaserTagPlayerComponent.getComponentType();
    }
}
