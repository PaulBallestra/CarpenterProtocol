package fr.seeeek.carpenterprotocol.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.enums.MiniGameState;
import fr.seeeek.carpenterprotocol.enums.MiniGameType;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashSet;
import java.util.Set;

public class MiniGameComponent implements Component<EntityStore> {
    private static ComponentType<EntityStore, MiniGameComponent> type;
    public static ComponentType<EntityStore, MiniGameComponent> getComponentType() {
        return type;
    }
    public static void setComponentType(ComponentType<EntityStore, MiniGameComponent> type) {
        MiniGameComponent.type = type;
    }

    public MiniGameComponent(){
        this(MiniGameState.CREATED, null, 0f, 10f, 10f, 2, 12, false, false);
    }

    public MiniGameComponent(MiniGameState state, MiniGameType miniGameType, float cleanupTimer, float startingTimer, float endingTimer, int minPlayers, int maxPlayers, boolean isStartingTitleShown, boolean isWinnerTitleShown){
        this.currentState = state;
        this.miniGameType = miniGameType;
        this.cleanupTimer = cleanupTimer;
        this.startingTimer = startingTimer;
        this.endingTimer = endingTimer;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.isStartingTitleShown = isStartingTitleShown;
        this.isWinnerTitleShown = isWinnerTitleShown;
    }

    public MiniGameComponent(MiniGameComponent other){
        this.currentState = other.currentState;
        this.miniGameType = other.miniGameType;
        this.cleanupTimer = other.cleanupTimer;
        this.startingTimer = other.startingTimer;
        this.endingTimer = other.endingTimer;
        this.minPlayers = other.minPlayers;
        this.maxPlayers = other.maxPlayers;
        this.isStartingTitleShown = other.isStartingTitleShown;
        this.isWinnerTitleShown = other.isWinnerTitleShown;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new MiniGameComponent(this);
    }

    // 3. Data Fields
    private MiniGameState currentState;
    private MiniGameType miniGameType;
    private float startingTimer;
    private float endingTimer;
    private float cleanupTimer;

    private int minPlayers;
    private int maxPlayers;
    private final Set<Ref<EntityStore>> alivePlayers = new HashSet<>();
    private final Set<Ref<EntityStore>> players = new HashSet<>();

    private boolean isStartingTitleShown = false;
    private boolean isWinnerTitleShown = false;

    // 4. Codec for Serialization (REQUIRED for Hytale ECS)
    public static final BuilderCodec<MiniGameComponent> CODEC = BuilderCodec.<MiniGameComponent>builder(MiniGameComponent.class, MiniGameComponent::new)
            .append(
                    new KeyedCodec<>("CurrentState", new EnumCodec<MiniGameState>(MiniGameState.class)),
                    (component, value) -> component.currentState = value,
                    component -> component.currentState
            ).add()
            .append(
                    new KeyedCodec<>("MiniGameType", new EnumCodec<MiniGameType>(MiniGameType.class)),
                    (component, value) -> component.miniGameType = value,
                    component -> component.miniGameType
            ).add()
            .append(
                    new KeyedCodec<>("MinPlayers", Codec.INTEGER),
                    (component, value) -> component.minPlayers = value,
                    component -> component.minPlayers
            ).add()
            .append(
                    new KeyedCodec<>("MaxPlayers", Codec.INTEGER),
                    (component, value) -> component.maxPlayers = value,
                    component -> component.maxPlayers
            ).add()
            .append(
                    new KeyedCodec<>("StartingTimer", Codec.FLOAT),
                    (component, value) -> component.startingTimer = value,
                    component -> component.startingTimer
            ).add()
            .append(
                    new KeyedCodec<>("CleanupTimer", Codec.FLOAT),
                    (component, value) -> component.cleanupTimer = value,
                    component -> component.cleanupTimer
            ).add()
            .append(
                    new KeyedCodec<>("EndingTimer", Codec.FLOAT),
                    (component, value) -> component.endingTimer = value,
                    component -> component.endingTimer
            ).add()
            .append(
                    new KeyedCodec<>("IsStartingTitleShown", Codec.BOOLEAN),
                    (component, value) -> component.isStartingTitleShown = value,
                    component -> component.isStartingTitleShown
            ).add()
            .append(
                    new KeyedCodec<>("IsWinnerTitleShown", Codec.BOOLEAN),
                    (component, value) -> component.isWinnerTitleShown = value,
                    component -> component.isWinnerTitleShown
            ).add()
            .build();

    // Getters and Setters
    public MiniGameState getState() { return currentState; }
    public void setState(MiniGameState state) { this.currentState = state; }

    public MiniGameType getMiniGameType() { return miniGameType; }
    public void setMiniGameType(MiniGameType miniGameType) { this.miniGameType = miniGameType; }

    public float getCleanupTimer() { return cleanupTimer; }
    public void setCleanupTimer(float value) { this.cleanupTimer = value; }

    public float getStartingTimer() { return startingTimer; }
    public void setStartingTimer(float value) { this.startingTimer = value; }

    public float getEndingTimer() { return endingTimer; }
    public void setEndingTimer(float value) { this.endingTimer = value; }

    public int getMinPlayers() { return minPlayers; }
    public void setMinPlayers(int value) { this.minPlayers = value; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int value) { this.maxPlayers = value; }

    public boolean isStartingTitleShown() { return isStartingTitleShown; }
    public void setIsStartingTitleShown(boolean value) { this.isStartingTitleShown = value; }

    public boolean isWinnerTitleShown() {return isWinnerTitleShown;}
    public void setIsWinnerTitleShown(boolean value) { this.isWinnerTitleShown = value; }

    public Set<Ref<EntityStore>> getAlivePlayers() {
        return alivePlayers;
    }
    public Set<Ref<EntityStore>> getPlayers() { return players; }
}
