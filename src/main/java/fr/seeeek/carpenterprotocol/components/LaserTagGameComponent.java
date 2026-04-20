package fr.seeeek.carpenterprotocol.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LaserTagGameComponent implements Component<EntityStore> {
    private static ComponentType<EntityStore, LaserTagGameComponent> type;
    public static ComponentType<EntityStore, LaserTagGameComponent> getComponentType() {
        return type;
    }
    public static void setComponentType(ComponentType<EntityStore, LaserTagGameComponent> type) {
        LaserTagGameComponent.type = type;
    }

    private int maxKillsPerTeam;
    private float maxGameTime;
    private int teamCount;

    private Map<Integer, Integer> teamKills = new ConcurrentHashMap<>();

    public LaserTagGameComponent(){
        this.maxKillsPerTeam = 50;
        this.maxGameTime = 600f;
        this.teamCount = 2;
        this.teamKills.put(0, 0);
        this.teamKills.put(1, 0);
    }

    public LaserTagGameComponent(int maxKillsPerTeam, float maxGameTime, int teamCount, Map<Integer, Integer> teamKills){
        this.maxKillsPerTeam = maxKillsPerTeam;
        this.maxGameTime = maxGameTime;
        this.teamCount = teamCount;
        this.teamKills = teamKills;
    }

    public LaserTagGameComponent(LaserTagGameComponent other) {
        this.maxKillsPerTeam = other.maxKillsPerTeam;
        this.maxGameTime = other.maxGameTime;
        this.teamCount = other.teamCount;
        this.teamKills = other.teamKills;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new LaserTagGameComponent(this);
    }

    public static final BuilderCodec<LaserTagGameComponent> CODEC = BuilderCodec.builder(LaserTagGameComponent.class, LaserTagGameComponent::new)
            .append(
                    new KeyedCodec<>("MaxKillsPerTeam", Codec.INTEGER),
                    (component, value) -> component.maxKillsPerTeam = value,
                    component -> component.maxKillsPerTeam
            ).add()
            .append(
                    new KeyedCodec<>("MaxGameTime", Codec.FLOAT),
                    (component, value) -> component.maxGameTime = value,
                    component -> component.maxGameTime
            ).add()
            .append(
                    new KeyedCodec<>("TeamCount", Codec.INTEGER),
                    (component, value) -> component.teamCount = value,
                    component -> component.teamCount
            ).add()
            .build();

    public int getTeamKills(int team) {
        return teamKills.getOrDefault(team, 0);
    }

    public void addKillToTeam(int team) {
        teamKills.put(team, getTeamKills(team) + 1);
    }

    public int getMaxKillsPerTeam() {
        return maxKillsPerTeam;
    }
    public void setMaxKillsPerTeam(int maxKillsPerTeam) { this.maxKillsPerTeam = maxKillsPerTeam; }

    public float getMaxGameTime() {
        return maxGameTime;
    }
    public void setMaxGameTime(float time) { this.maxGameTime = time; }

    public int getTeamCount() { return teamCount; }

}
