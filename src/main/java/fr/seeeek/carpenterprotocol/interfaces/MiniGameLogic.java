package fr.seeeek.carpenterprotocol.interfaces;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.components.MiniGameComponent;

import java.util.Collection;

public interface MiniGameLogic {
    default void setup(CommandBuffer<EntityStore> commandBuffer, World world, MiniGameComponent miniGameComponent, float dt, Store<EntityStore> store, Ref<EntityStore> entityRef, Collection<PlayerRef> allPlayerRefs) {}
    default void starting(CommandBuffer<EntityStore> commandBuffer, World world, MiniGameComponent miniGameComponent, float dt, Store<EntityStore> store, Ref<EntityStore> entityRef, Collection<PlayerRef> allPlayerRefs) {}
    void running(CommandBuffer<EntityStore> commandBuffer, World world, MiniGameComponent miniGameComponent, float dt, Store<EntityStore> store, Ref<EntityStore> ref);
}