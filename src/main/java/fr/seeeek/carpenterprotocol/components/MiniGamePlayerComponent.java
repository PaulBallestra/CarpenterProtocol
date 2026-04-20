package fr.seeeek.carpenterprotocol.components;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.enums.MiniGamePlayerState;

public class MiniGamePlayerComponent implements Component<EntityStore> {

    // Boiler plate ComponentType setup
    private static ComponentType<EntityStore, MiniGamePlayerComponent> type;
    public static ComponentType<EntityStore, MiniGamePlayerComponent> getComponentType() { return type; }
    public static void setComponentType(ComponentType<EntityStore, MiniGamePlayerComponent> type) { MiniGamePlayerComponent.type = type; }

    private MiniGamePlayerState playerState;

    public MiniGamePlayerComponent() { this(MiniGamePlayerState.PENDING); }
    public MiniGamePlayerComponent(MiniGamePlayerState state) { this.playerState = state; }

    // Serialization
    public static final BuilderCodec<MiniGamePlayerComponent> CODEC = BuilderCodec.<MiniGamePlayerComponent>builder(MiniGamePlayerComponent.class, MiniGamePlayerComponent::new)
            .append(new KeyedCodec<>("MiniGamePlayerState", new EnumCodec<>(MiniGamePlayerState.class)),
                    (c, v) -> c.playerState = v, c -> c.playerState)
            .add()
            .build();

    public MiniGamePlayerState getPlayerState() { return playerState; }
    public void setPlayerState(MiniGamePlayerState state) { this.playerState = state; }

    @Override public Component<EntityStore> clone() { return new MiniGamePlayerComponent(this.playerState); }
}
