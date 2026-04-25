package fr.seeeek.carpenterprotocol.events;

import com.hypixel.hytale.builtin.instances.config.InstanceWorldConfig;
import com.hypixel.hytale.builtin.instances.removal.WorldEmptyCondition;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import com.hypixel.hytale.server.core.universe.world.events.StartWorldEvent;
import com.hypixel.hytale.server.core.universe.world.spawn.GlobalSpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.storage.provider.EmptyChunkStorageProvider;
import com.hypixel.hytale.server.core.universe.world.storage.resources.EmptyResourceStorageProvider;
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.VoidWorldGenProvider;
import fr.seeeek.carpenterprotocol.components.*;
import fr.seeeek.carpenterprotocol.enums.MiniGameState;
import fr.seeeek.carpenterprotocol.enums.MiniGameType;

public class AddBlockH13WorldEvent {

    static String waitingZonePrefabKey = "Laser_Tag_Waiting_Zone.prefab.json", weatherKey = "Zone3_Cave_Deep", gameplayConfig = "LaserTag_GameplayConfig";
    static Vector3i waitingZoneSpawnPoint = new Vector3i(0, 78, 0);
    static Vector3i waitingZonePortalSpawnPoint = new Vector3i(1, 78, 1);

    public static void onBlockH13Added(AddWorldEvent addWorldEvent){
        World world = addWorldEvent.getWorld();

        if(!world.getName().equals("block_h13")) return;

        Store<EntityStore> store = world.getEntityStore().getStore();

        BlockSelection prefabFromAssets = PrefabStore.get().getAssetPrefabFromAnyPack(waitingZonePrefabKey);
        assert prefabFromAssets != null;
        BlockSelection prefab = new BlockSelection(prefabFromAssets);

        setWorldConfig(world);

        // Configure Instance-specific settings
        InstanceWorldConfig instanceConfig = InstanceWorldConfig.ensureAndGet(world.getWorldConfig());
        instanceConfig.setRemovalConditions(WorldEmptyCondition.REMOVE_WHEN_EMPTY);

        world.execute(() -> {
            prefab.placeNoReturn(world, waitingZoneSpawnPoint, store);
        });
    }

    public static void onBlockH13Start(StartWorldEvent startWorldEvent){
        World world = startWorldEvent.getWorld();

        if(!world.getName().equals("block_h13")) return;

        Store<EntityStore> store = world.getEntityStore().getStore();

        world.execute(() -> {
            initTime(world, store);
            initECS(world, store);
        });
    }

    private static void setWorldConfig(World world) {
        WorldConfig worldConfig = world.getWorldConfig();

        worldConfig.setSpawnProvider(new GlobalSpawnProvider(new Transform(waitingZonePortalSpawnPoint)));

        // Don't save on disk
        worldConfig.setCanSaveChunks(false);
        worldConfig.setSavingConfig(false);
        worldConfig.setDeleteOnRemove(true);
        worldConfig.setDeleteOnUniverseStart(true);
        worldConfig.setForcedWeather(weatherKey);
        worldConfig.setPvpEnabled(true);

        worldConfig.setCompassUpdating(false);
        worldConfig.setSavingPlayers(false);
        worldConfig.setSaveNewChunks(false);

        if(gameplayConfig != null) worldConfig.setGameplayConfig(gameplayConfig);

        // Use Void generator and Empty storage for a clean slate
        worldConfig.setWorldGenProvider(new VoidWorldGenProvider(new Color((byte)91, (byte)-98, (byte)40), "Env_Zone1_Plains"));
        worldConfig.setChunkStorageProvider(EmptyChunkStorageProvider.INSTANCE);
        worldConfig.setResourceStorageProvider(EmptyResourceStorageProvider.INSTANCE);
    }

    private static void initTime(World gameWorld, Store<EntityStore> store){
        WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
        worldTimeResource.setDayTime(0.0, gameWorld, store);
        gameWorld.getWorldConfig().setGameTimePaused(true);
        gameWorld.getWorldConfig().markChanged();
    }

    private static void initECS(World gameWorld, Store<EntityStore> store) {
        // We create a Holder (blueprint), add the component, and add it to the world.
        Holder<EntityStore> miniGameEntityHolder =  store.getRegistry().newHolder();

        MiniGameComponent miniGameComponent = new MiniGameComponent();
        miniGameComponent.setState(MiniGameState.CREATED);
        miniGameComponent.setMinPlayers(4);
        miniGameComponent.setStartingTimer(10f);
        miniGameComponent.setEndingTimer(10f);
        miniGameComponent.setMiniGameType(MiniGameType.LASERTAG);

        MiniGameUIComponent miniGameUIComponent = new MiniGameUIComponent();
        miniGameUIComponent.setRefreshTimer(0f);
        miniGameUIComponent.setRefreshInterval(1f);

        LaserTagGameComponent laserTagGameComponent = new LaserTagGameComponent();

        LobbyComponent lobby = new LobbyComponent(
                "lobby-" + gameWorld.getWorldConfig().getUuid(),
                "Block H13",
                miniGameComponent,
                "127.0.0.1",
                5520
        );

        miniGameEntityHolder.addComponent(LobbyComponent.getComponentType(), lobby);
        miniGameEntityHolder.addComponent(MiniGameComponent.getComponentType(), miniGameComponent);
        miniGameEntityHolder.addComponent(MiniGameUIComponent.getComponentType(), miniGameUIComponent);
        miniGameEntityHolder.addComponent(LaserTagGameComponent.getComponentType(), laserTagGameComponent);

        // 4. Add the entity to the new world's store
        store.addEntity(miniGameEntityHolder, AddReason.SPAWN);

        initCubeLeds(store, miniGameComponent);
    }

    private static void initCubeLeds(Store<EntityStore> store, MiniGameComponent miniGameComponent){
        // Create entity holder
        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();

        // Load your custom model
        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("Leds_Cube_Model");
        assert modelAsset != null;
        Model model = Model.createScaledModel(modelAsset, 1.0f);

        // Spawn position
        Vector3d pos = new Vector3d(0, 82, 0);
        Vector3f rot = new Vector3f(0, 0, 0);

        // Add components
        holder.addComponent(
                TransformComponent.getComponentType(),
                new TransformComponent(pos, rot)
        );

        holder.addComponent(
                LaserTagRotatingLedsCubeComponent.getComponentType(),
                new LaserTagRotatingLedsCubeComponent(0.1f, miniGameComponent)
        );

        holder.addComponent(
                PersistentModel.getComponentType(),
                new PersistentModel(model.toReference())
        );

        holder.addComponent(
                ModelComponent.getComponentType(),
                new ModelComponent(model)
        );

        assert model.getBoundingBox() != null;
        holder.addComponent(
                BoundingBox.getComponentType(),
                new BoundingBox(model.getBoundingBox())
        );

        holder.addComponent(
                NetworkId.getComponentType(),
                new NetworkId(store.getExternalData().takeNextNetworkId())
        );

        // Required default components
        holder.ensureComponent(UUIDComponent.getComponentType());

        // Spawn entity
        store.addEntity(holder, AddReason.SPAWN);
    }
}
