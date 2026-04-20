package fr.seeeek.carpenterprotocol.components;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class MiniGameUIComponent implements Component<EntityStore> {

    private static ComponentType<EntityStore, MiniGameUIComponent> type;

    public static ComponentType<EntityStore, MiniGameUIComponent> getComponentType() {
        return type;
    }

    public static void setComponentType(ComponentType<EntityStore, MiniGameUIComponent> type) {
        MiniGameUIComponent.type = type;
    }

    private float refreshTimer = 0f;
    private float refreshInterval = 1f; // update every 1 second

    public MiniGameUIComponent() {}

    public MiniGameUIComponent(MiniGameUIComponent other) {
        this.refreshTimer = other.refreshTimer;
        this.refreshInterval = other.refreshInterval;
    }

    @Override
    public Component<EntityStore> clone() {
        return new MiniGameUIComponent(this);
    }

    public float getRefreshTimer() { return refreshTimer; }
    public void setRefreshTimer(float value) { this.refreshTimer = value; }

    public float getRefreshInterval() { return refreshInterval; }
    public void setRefreshInterval(float value) {this.refreshInterval = value;}
}
