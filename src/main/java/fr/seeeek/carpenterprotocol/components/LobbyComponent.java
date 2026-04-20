package fr.seeeek.carpenterprotocol.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LobbyComponent implements Component<EntityStore> {

    // Component boilerplate
    private static ComponentType<EntityStore, LobbyComponent> type;
    public static ComponentType<EntityStore, LobbyComponent> getComponentType() {
        return type;
    }
    public static void setComponentType(ComponentType<EntityStore, LobbyComponent> type) {
        LobbyComponent.type = type;
    }

    public LobbyComponent() {
        // Only initialize nested objects to prevent null issues during decoding
        this.lobbyMiniGameComponent = new MiniGameComponent();
    }

    // 2. The Functional Constructor (For your code to use)
    public LobbyComponent(String lobbyId, String lobbyDisplayName, MiniGameComponent miniGameComponent, String lobbyServerHostIp, int lobbyServerPort) {
        this.lobbyId = lobbyId;
        this.lobbyDisplayName = lobbyDisplayName;
        this.lobbyMiniGameComponent = miniGameComponent;
        this.lobbyServerHostIp = lobbyServerHostIp;
        this.lobbyServerPort = lobbyServerPort;
    }

    // 3. The Copy Constructor (Required for ECS cloning)
    public LobbyComponent(LobbyComponent other) {
        this.lobbyId = other.lobbyId;
        this.lobbyDisplayName = other.lobbyDisplayName;
        this.lobbyMiniGameComponent = (other.lobbyMiniGameComponent != null) ? (MiniGameComponent) other.lobbyMiniGameComponent.clone() : null;
        this.lobbyServerHostIp = other.lobbyServerHostIp;
        this.lobbyServerPort = other.lobbyServerPort;
    }

    // 3. Data Fields
    private String lobbyId;
    private String lobbyDisplayName;

    // game component
    private MiniGameComponent lobbyMiniGameComponent;

    private String lobbyServerHostIp;
    private int lobbyServerPort;

    public static final BuilderCodec<LobbyComponent> CODEC = BuilderCodec.<LobbyComponent>builder(LobbyComponent.class, LobbyComponent::new)
            .append(
                    new KeyedCodec<>("LobbyId", Codec.STRING),
                    (lobbyComponent, value) -> lobbyComponent.lobbyId = value,
                    lobbyComponent -> lobbyComponent.lobbyId
            ).add()
            .append(
                    new KeyedCodec<>("LobbyDisplayName", Codec.STRING),
                    (lobbyComponent, value) -> lobbyComponent.lobbyDisplayName = value,
                    lobbyComponent -> lobbyComponent.lobbyDisplayName
            ).add()
            .append(
                    new KeyedCodec<>("LobbyMiniGameComponent", MiniGameComponent.CODEC),
                    (lobbyComponent, value) -> lobbyComponent.lobbyMiniGameComponent = value,
                    lobbyComponent -> lobbyComponent.lobbyMiniGameComponent
            ).add()
            .append(
                    new KeyedCodec<>("LobbyServerHostIp", Codec.STRING),
                    (lobbyComponent, value) -> lobbyComponent.lobbyServerHostIp = value,
                    lobbyComponent -> lobbyComponent.lobbyServerHostIp
            ).add()
            .append(
                    new KeyedCodec<>("LobbyServerPort", Codec.INTEGER),
                    (lobbyComponent, value) -> lobbyComponent.lobbyServerPort = value,
                    lobbyComponent -> lobbyComponent.lobbyServerPort
            ).add()
            .build();

    // GETTER & SETTERS
    public void setLobbyId(String value) { this.lobbyId = value; }
    public String getLobbyId() { return this.lobbyId; }

    public void setLobbyDisplayName(String value) { this.lobbyDisplayName = value; }
    public String getLobbyDisplayName() { return this.lobbyDisplayName; }

    public void setLobbyMiniGameComponent(MiniGameComponent miniGameComponent) { this.lobbyMiniGameComponent = miniGameComponent; }
    public MiniGameComponent getLobbyMiniGameComponent() { return this.lobbyMiniGameComponent; }

    public void setLobbyServerHostIp(String value) { this.lobbyServerHostIp = value; }
    public String getLobbyServerHostIp() { return this.lobbyServerHostIp; }

    public void setLobbyServerPort(Integer value) { this.lobbyServerPort = value; }
    public int getLobbyServerPort() { return this.lobbyServerPort; }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new LobbyComponent(this);
    }
}
