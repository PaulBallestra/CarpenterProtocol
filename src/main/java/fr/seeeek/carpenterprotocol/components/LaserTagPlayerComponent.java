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
    private float respawnTimer;

    public LaserTagPlayerComponent(){
        this.teamId = 0;
        this.kills = 0;
        this.deaths = 0;
        this.respawnTimer = 3f;
    }

    public LaserTagPlayerComponent(int teamId, int kills, int deaths, float respawnTimer) {
        this.teamId = teamId;
        this.kills = kills;
        this.deaths = deaths;
        this.respawnTimer = respawnTimer;
    }

    public LaserTagPlayerComponent(LaserTagPlayerComponent other){
        this.teamId = other.teamId;
        this.kills = other.kills;
        this.deaths = other.deaths;
        this.respawnTimer = other.respawnTimer;
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
            .append(
                    new KeyedCodec<>("RespawnTimer", Codec.FLOAT),
                    (component, value) -> component.respawnTimer = value,
                    component -> component.respawnTimer
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

    public float getRespawnTimer() { return respawnTimer; }
    public void setRespawnTimer(float timer) { this.respawnTimer = timer; }
}
