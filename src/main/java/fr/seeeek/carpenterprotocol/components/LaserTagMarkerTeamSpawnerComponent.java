package fr.seeeek.carpenterprotocol.components;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import javax.annotation.Nullable;

public class LaserTagMarkerTeamSpawnerComponent implements Component<ChunkStore> {

    private static ComponentType<ChunkStore, LaserTagMarkerTeamSpawnerComponent> type;
    public static ComponentType<ChunkStore, LaserTagMarkerTeamSpawnerComponent> getComponentType() { return type; }
    public static void setComponentType(ComponentType<ChunkStore, LaserTagMarkerTeamSpawnerComponent> type) { LaserTagMarkerTeamSpawnerComponent.type = type; }

    @Nullable
    public Component<ChunkStore> clone() {
        return new LaserTagMarkerTeamSpawnerComponent(this);
    }

    private int teamId;
    private Vector3i position;

    public LaserTagMarkerTeamSpawnerComponent(){
        this.teamId = 0;
        this.position = new Vector3i(0, 0, 0);
    }

    public LaserTagMarkerTeamSpawnerComponent(int teamId, Vector3i position){
        this.teamId = teamId;
        this.position = position;
    }

    public LaserTagMarkerTeamSpawnerComponent(LaserTagMarkerTeamSpawnerComponent other){
        this.teamId = other.teamId;
        this.position = other.position;
    }

    public static final BuilderCodec<LaserTagMarkerTeamSpawnerComponent> CODEC = BuilderCodec.builder(LaserTagMarkerTeamSpawnerComponent.class, LaserTagMarkerTeamSpawnerComponent::new)
            .append(
                    new KeyedCodec<>("TeamId", Codec.INTEGER),
                    (component, value) -> component.teamId = value,
                    component -> component.teamId
            ).add()
            .append(
                    new KeyedCodec<>("Position", Vector3i.CODEC),
                    (component, value) -> component.position = value,
                    component -> component.position
            ).add()
            .build();

    public int getTeamId() { return this.teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public Vector3i getPosition() { return this.position; }
    public void setPosition(Vector3i position) { this.position = position; }
}
