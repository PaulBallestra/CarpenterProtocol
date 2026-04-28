package fr.seeeek.carpenterprotocol.interfaces;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LaserTagTeamSpawnProvider implements ISpawnProvider {

    private final Map<Integer, List<Transform>> teamSpawns;

    public LaserTagTeamSpawnProvider(Map<Integer, List<Transform>> teamSpawns) {
        this.teamSpawns = teamSpawns;
    }

    @Override
    public Transform getSpawnPoint(World world, @NonNullDecl UUID playerUuid) {
        Store<EntityStore> store = world.getEntityStore().getStore();
        Ref<EntityStore> playerRef = world.getEntityStore().getRefFromUUID(playerUuid);

        assert playerRef != null;
        LaserTagPlayerComponent player = store.getComponent(playerRef, LaserTagPlayerComponent.getComponentType());

        if (player == null) {
            return fallbackSpawn();
        }

        int teamId = player.getTeamId();

        List<Transform> spawns = teamSpawns.get(teamId);

        if (spawns == null || spawns.isEmpty()) {
            return fallbackSpawn();
        }

        return spawns.get(ThreadLocalRandom.current().nextInt(spawns.size()));
    }

    private Transform fallbackSpawn() {
        return new Transform(new Vector3d(0, 80, 0));
    }

    @Override
    public Transform[] getSpawnPoints() {
        return teamSpawns.values()
                .stream()
                .flatMap(List::stream)
                .toArray(Transform[]::new);
    }

    @Override
    public boolean isWithinSpawnDistance(@NonNullDecl Vector3d position, double distance) {
        for (List<Transform> list : teamSpawns.values()) {
            for (Transform transform : list) {
                if (transform.getPosition().distanceTo(position) <= distance) {
                    return true;
                }
            }
        }
        return false;
    }
}