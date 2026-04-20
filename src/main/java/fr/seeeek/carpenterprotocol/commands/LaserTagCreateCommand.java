package fr.seeeek.carpenterprotocol.commands;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.common.BroadcastMessage;
import fr.seeeek.carpenterprotocol.common.MessageType;
import fr.seeeek.carpenterprotocol.components.*;
import fr.seeeek.carpenterprotocol.enums.MiniGameState;
import fr.seeeek.carpenterprotocol.enums.MiniGameType;
import fr.seeeek.carpenterprotocol.services.InstanceService;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.CompletableFuture;


public class LaserTagCreateCommand extends AbstractPlayerCommand {

    private final InstanceService instanceService;

    public LaserTagCreateCommand(InstanceService instanceService) {
        super("lasertag", "Create a laser tag mini-game");
        this.instanceService = instanceService;
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        if(playerRef.getReference() == null || !playerRef.isValid()) return;

        String playerName = playerRef.getUsername();
        if(world.getName().startsWith("mini_game-laser_tag-" + playerName)) {
            BroadcastMessage.toPlayer(playerRef, "You're already in a world with the same name", MessageType.ERROR);
            return;
        }

        ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();

        Transform returnPoint = spawnProvider != null ? spawnProvider.getSpawnPoint(world, playerRef.getUuid()) : new Transform();

        String prefabKey = "Laser_Tag_Waiting_Zone.prefab.json";
        Vector3d waitingZoneSpawnPoint = new Vector3d(0, 78, 0);
        CompletableFuture<World> worldCompletableFuture = instanceService.createInstance("mini_game-laser_tag-" + playerName, prefabKey, true, false, null, waitingZoneSpawnPoint, "Zone3_Cave_Deep", true, "LaserTag_GameplayConfig.json");

        worldCompletableFuture.thenApply(gameWorld -> {
            Store<EntityStore> gameWorldStore = gameWorld.getEntityStore().getStore();
            gameWorld.execute(() -> {
                initTime(gameWorld, gameWorldStore);
                initECS(gameWorld, ref, playerRef);
            });

            return gameWorld;
        });

        // Teleport the host player
        if(playerRef.getReference() != null){
            InstancesPlugin.teleportPlayerToLoadingInstance(
                    playerRef.getReference(),
                    playerRef.getReference().getStore(),
                    worldCompletableFuture,
                    returnPoint
            );
        }
    }

    private void initTime(World gameWorld, Store<EntityStore> store){
        WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
        worldTimeResource.setDayTime(0.0, gameWorld, store);
        gameWorld.getWorldConfig().setGameTimePaused(true);
        gameWorld.getWorldConfig().markChanged();
    }

    private void initCubeLeds(Store<EntityStore> store, MiniGameComponent miniGameComponent){
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

    private void initECS(World gameWorld, Ref<EntityStore> ref, PlayerRef playerRef) {
        // We create a Holder (blueprint), add the component, and add it to the world.
        Holder<EntityStore> miniGameEntityHolder =  gameWorld.getEntityStore().getStore().getRegistry().newHolder();

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
                playerRef.getUsername() + "'s Lobby",
                miniGameComponent,
                "127.0.0.1",
                5520
        );

        miniGameEntityHolder.addComponent(LobbyComponent.getComponentType(), lobby);
        miniGameEntityHolder.addComponent(MiniGameComponent.getComponentType(), miniGameComponent);
        miniGameEntityHolder.addComponent(MiniGameUIComponent.getComponentType(), miniGameUIComponent);
        miniGameEntityHolder.addComponent(LaserTagGameComponent.getComponentType(), laserTagGameComponent);

        // 4. Add the entity to the new world's store
        gameWorld.getEntityStore().getStore().addEntity(miniGameEntityHolder, AddReason.SPAWN);

        initCubeLeds(gameWorld.getEntityStore().getStore(), miniGameComponent);

//        LobbyCreatedEvent.dispatch(ref, lobby);
    }
}
