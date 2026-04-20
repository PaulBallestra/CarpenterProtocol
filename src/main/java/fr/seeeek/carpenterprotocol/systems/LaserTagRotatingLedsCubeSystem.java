package fr.seeeek.carpenterprotocol.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.components.LaserTagRotatingLedsCubeComponent;
import fr.seeeek.carpenterprotocol.components.MiniGameComponent;
import fr.seeeek.carpenterprotocol.enums.MiniGameState;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LaserTagRotatingLedsCubeSystem extends EntityTickingSystem<EntityStore> {
    @Override
    public void tick(float dt, int index,
                     @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store,
                     @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

        LaserTagRotatingLedsCubeComponent rotating = archetypeChunk.getComponent(index, LaserTagRotatingLedsCubeComponent.getComponentType());
        TransformComponent transform = archetypeChunk.getComponent(index, TransformComponent.getComponentType());

        if (rotating == null || transform == null) return;

        MiniGameComponent game = rotating.getMiniGameComponent();
        if (game == null) return;

        float speed = rotating.getBaseSpeed();

        if (game.getState() == MiniGameState.STARTING) {

            float timer = game.getStartingTimer(); // 10 → 0
            float total = 10f;

            float progress = 1f - (timer / total);

            speed = rotating.getBaseSpeed() + progress * 5f;
        }

        Vector3f rot = transform.getRotation();

        float newYaw = (rot.getY() + speed * dt) % 360f;

        rot.setY(newYaw);
        transform.setRotation(rot);

    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(
                LaserTagRotatingLedsCubeComponent.getComponentType());
    }
}
