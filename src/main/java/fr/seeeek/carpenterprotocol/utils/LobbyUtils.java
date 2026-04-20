package fr.seeeek.carpenterprotocol.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.components.LobbyComponent;

public class LobbyUtils {
    public static void updateLobby(Store<EntityStore> store, Ref<EntityStore> lobbyRef) {
        LobbyComponent lobbyComponent = store.getComponent(lobbyRef, LobbyComponent.getComponentType());

        if (lobbyComponent != null) {
            // Dispatch the event for MongoDB persistence
            // LobbyUpdatedEvent.dispatch(lobbyRef, lobbyComponent);
        }
    }
}