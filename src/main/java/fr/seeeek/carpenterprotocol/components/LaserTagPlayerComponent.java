package fr.seeeek.carpenterprotocol.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LaserTagPlayerComponent implements Component<EntityStore> {
    private static ComponentType<EntityStore, LaserTagPlayerComponent> type;
    public static ComponentType<EntityStore, LaserTagPlayerComponent> getComponentType() {
        return type;
    }
    public static void setComponentType(ComponentType<EntityStore, LaserTagPlayerComponent> type) {
        LaserTagPlayerComponent.type = type;
    }

    private int teamId;
    private int kills;
    private int deaths;

    public LaserTagPlayerComponent(){
        this.teamId = 0;
        this.kills = 0;
        this.deaths = 0;
    }

    public LaserTagPlayerComponent(int teamId, int kills, int deaths) {
        this.teamId = teamId;
        this.kills = kills;
        this.deaths = deaths;
    }

    public LaserTagPlayerComponent(LaserTagPlayerComponent other){
        this.teamId = other.teamId;
        this.kills = other.kills;
        this.deaths = other.deaths;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new LaserTagPlayerComponent(this);
    }

    public static final BuilderCodec<LaserTagPlayerComponent> CODEC = BuilderCodec.<LaserTagPlayerComponent>builder(LaserTagPlayerComponent.class, LaserTagPlayerComponent::new)
            .append(
                    new KeyedCodec<>("TeamId", Codec.INTEGER),
                    (component, value) -> component.teamId = value,
                    component -> component.teamId
            ).add()
            .append(
                    new KeyedCodec<>("Kills", Codec.INTEGER),
                    (component, value) -> component.kills = value,
                    component -> component.kills
            ).add()
            .append(
                    new KeyedCodec<>("Deaths", Codec.INTEGER),
                    (component, value) -> component.deaths = value,
                    component -> component.deaths
            ).add()
            .build();

    public int getTeamId() {
        return teamId;
    }
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getKills() { return kills; }
    public int getDeaths() { return deaths; }

    public void addKill() {
        kills++;
    }
    public void setKills(int value) { this.kills = value; }

    public void addDeath() {
        deaths++;
    }
}
