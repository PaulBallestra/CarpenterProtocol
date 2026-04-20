package fr.seeeek.carpenterprotocol.components;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LaserTagRotatingLedsCubeComponent implements Component<EntityStore> {

    private static ComponentType<EntityStore, LaserTagRotatingLedsCubeComponent> type;
    public static ComponentType<EntityStore, LaserTagRotatingLedsCubeComponent> getComponentType() { return type; }
    public static void setComponentType(ComponentType<EntityStore, LaserTagRotatingLedsCubeComponent> t) { type = t; }

    private float baseSpeed;
    private float currentSpeed;
    private MiniGameComponent miniGameComponent;

    public LaserTagRotatingLedsCubeComponent() {
        this.baseSpeed = 0.1f;
    }

    public LaserTagRotatingLedsCubeComponent(float baseSpeed, MiniGameComponent miniGameComponent) {
        this.baseSpeed = baseSpeed;
        this.currentSpeed = baseSpeed;
        this.miniGameComponent = miniGameComponent;
    }

    public LaserTagRotatingLedsCubeComponent(LaserTagRotatingLedsCubeComponent rotatingComponent) {
        this.baseSpeed = rotatingComponent.baseSpeed;
        this.currentSpeed = rotatingComponent.currentSpeed;
        this.miniGameComponent = rotatingComponent.miniGameComponent;
    }

    public static final BuilderCodec<LaserTagRotatingLedsCubeComponent> CODEC = BuilderCodec.<LaserTagRotatingLedsCubeComponent>builder(LaserTagRotatingLedsCubeComponent.class, LaserTagRotatingLedsCubeComponent::new)
            .append(
                    new KeyedCodec<>("MiniGameComponent", MiniGameComponent.CODEC),
                    (rotatingComponent, value) -> rotatingComponent.miniGameComponent = value,
                    rotatingComponent -> rotatingComponent.miniGameComponent
            ).add()
            .build();

    public float getBaseSpeed() { return baseSpeed; }
    public float getCurrentSpeed() { return currentSpeed; }

    public void setCurrentSpeed(float speed) { this.currentSpeed = speed; }

    public void setMiniGameComponent(MiniGameComponent miniGameComponent) { this.miniGameComponent = miniGameComponent; }
    public MiniGameComponent getMiniGameComponent() { return this.miniGameComponent; }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new LaserTagRotatingLedsCubeComponent(this);
    }
}
