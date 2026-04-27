package fr.seeeek.carpenterprotocol.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LaserTagTeamComponent implements Component<EntityStore> {

    private static ComponentType<EntityStore, LaserTagTeamComponent> type;
    public static ComponentType<EntityStore, LaserTagTeamComponent> getComponentType() {
        return type;
    }
    public static void setComponentType(ComponentType<EntityStore, LaserTagTeamComponent> type) {
        LaserTagTeamComponent.type = type;
    }

    public LaserTagTeamComponent() {
        this(0, "Red", "#FF0000");
    }

    public LaserTagTeamComponent(int teamId, String name, String color){
        this.teamId = teamId;
        this.name = name;
        this.color = color;
    }

    public LaserTagTeamComponent(LaserTagTeamComponent other){
        this.teamId = other.teamId;
        this.name = other.name;
        this.color = other.color;
    }

    private int teamId;
    private String name;
    private String color;

    public static final BuilderCodec<LaserTagTeamComponent> CODEC = BuilderCodec.<LaserTagTeamComponent>builder(LaserTagTeamComponent.class, LaserTagTeamComponent::new)
            .append(
                    new KeyedCodec<>("TeamId", Codec.INTEGER),
                    (component, value) -> component.teamId = value,
                    component -> component.teamId
            ).add()
            .append(
                    new KeyedCodec<>("Name", Codec.STRING),
                    (component, value) -> component.name = value,
                    component -> component.name
            ).add()
            .append(
                    new KeyedCodec<>("Color", Codec.STRING),
                    (component, value) -> component.color = value,
                    component -> component.color
            ).add()
            .build();

    public int getTeamId() { return teamId; }
    public String getName() { return name; }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new LaserTagTeamComponent(this);
    }
}
