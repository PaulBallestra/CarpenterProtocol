package fr.seeeek.carpenterprotocol.interfaces;

import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class LaserTagTeamSpawnProvider implements ISpawnProvider {
    private final Map<Integer, List<Transform>> teamSpawns;
    private final Map<UUID, Integer> playerTeams = new ConcurrentHashMap<>();

    public LaserTagTeamSpawnProvider(Map<Integer, List<Transform>> teamSpawns) {
        Map<Integer, List<Transform>> copy = new HashMap<>();

        for (Map.Entry<Integer, List<Transform>> entry : teamSpawns.entrySet()) {
            List<Transform> spawns = new ArrayList<>();
            for (Transform spawn : entry.getValue()) {
                if (spawn != null) {
                    spawns.add(spawn.clone());
                }
            }
            copy.put(entry.getKey(), Collections.unmodifiableList(spawns));
        }

        this.teamSpawns = Collections.unmodifiableMap(copy);
    }

    public void setPlayerTeam(UUID playerUuid, int teamId) {
        playerTeams.put(playerUuid, teamId);
    }

    public void clearPlayerTeam(UUID playerUuid) {
        playerTeams.remove(playerUuid);
    }

    @Override
    public Transform getSpawnPoint(@NonNullDecl World world, @NonNullDecl UUID playerUuid) {
        Integer teamId = playerTeams.get(playerUuid);

        if (teamId != null) {
            Transform teamSpawn = randomSpawn(teamSpawns.get(teamId));
            if (teamSpawn != null) {
                return teamSpawn;
            }
        }

        Transform anySpawn = randomAnySpawn();
        return anySpawn != null ? anySpawn : fallbackSpawn();
    }

    private Transform randomSpawn(List<Transform> spawns) {
        if (spawns == null || spawns.isEmpty()) {
            return null;
        }

        return spawns.get(ThreadLocalRandom.current().nextInt(spawns.size())).clone();
    }

    private Transform randomAnySpawn() {
        List<Transform> allSpawns = new ArrayList<>();

        for (List<Transform> spawns : teamSpawns.values()) {
            allSpawns.addAll(spawns);
        }

        return randomSpawn(allSpawns);
    }

    private Transform fallbackSpawn() {
        return new Transform(new Vector3d(0, 80, 0));
    }

    @Override
    public Transform[] getSpawnPoints() {
        List<Transform> spawns = new ArrayList<>();

        for (List<Transform> teamSpawnList : teamSpawns.values()) {
            for (Transform spawn : teamSpawnList) {
                spawns.add(spawn.clone());
            }
        }

        return spawns.toArray(Transform[]::new);
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