package fr.seeeek.carpenterprotocol.components;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.enums.MiniGameState;

public class MiniGameHudComponent implements Component<EntityStore> {

    private static ComponentType<EntityStore, MiniGameHudComponent> type;

    public static ComponentType<EntityStore, MiniGameHudComponent> getComponentType() {
        return type;
    }

    public static void setComponentType(ComponentType<EntityStore, MiniGameHudComponent> type) {
        MiniGameHudComponent.type = type;
    }

    // Cache last displayed values to avoid spam
    private MiniGameState lastState;
    private LaserTagPlayerComponent lastLaserTagPlayerComponent;
    private LaserTagGameComponent lastLaserTagGameComponent;
    private int lastAliveCount;
    private int lastPlayersCount;

    @Override
    public Component<EntityStore> clone() {
        return new MiniGameHudComponent();
    }

    public void cache(MiniGameComponent game, LaserTagPlayerComponent laserTagPlayerComponent, LaserTagGameComponent laserTagGameComponent) {
        lastState = game.getState();
        lastAliveCount = game.getAlivePlayers().size();
        lastPlayersCount = game.getPlayers().size();
        lastLaserTagPlayerComponent = (LaserTagPlayerComponent) laserTagPlayerComponent.clone();
        lastLaserTagGameComponent = (LaserTagGameComponent) laserTagGameComponent.clone();
    }

    public boolean isDifferent(MiniGameComponent game, LaserTagPlayerComponent laserTagPlayerComponent, LaserTagGameComponent laserTagGameComponent) {
        if (lastLaserTagPlayerComponent == null) return true;

        return lastState != game.getState()
                || lastAliveCount != game.getAlivePlayers().size()
                || lastPlayersCount != game.getPlayers().size()
                || lastLaserTagPlayerComponent.getKills() != laserTagPlayerComponent.getKills()
                || lastLaserTagPlayerComponent.getDeaths() != laserTagPlayerComponent.getDeaths()
                || lastLaserTagPlayerComponent.getTeamId() != laserTagPlayerComponent.getTeamId()
                || lastLaserTagGameComponent.getTeamKills(0) != laserTagGameComponent.getTeamKills(0)
                || lastLaserTagGameComponent.getTeamKills(1) != laserTagGameComponent.getTeamKills(1);
    }
}