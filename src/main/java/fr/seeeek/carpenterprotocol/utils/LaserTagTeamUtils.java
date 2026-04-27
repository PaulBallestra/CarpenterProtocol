package fr.seeeek.carpenterprotocol.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;

public class LaserTagTeamUtils {
    public static void assignTeam(int team, Store<EntityStore> store, Ref<EntityStore> playerRef){
        LaserTagPlayerComponent laserTagPlayerComponent = new LaserTagPlayerComponent();
        laserTagPlayerComponent.setTeamId(team);

        store.addComponent(playerRef, LaserTagPlayerComponent.getComponentType(), laserTagPlayerComponent);
    }
}
