package fr.seeeek.carpenterprotocol;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.DropItemEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.registry.AssetRegistry;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import com.hypixel.hytale.server.core.universe.world.events.StartWorldEvent;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.seeeek.carpenterprotocol.commands.*;
import fr.seeeek.carpenterprotocol.commands.debug.OverrideMiniGameStateCommand;
import fr.seeeek.carpenterprotocol.components.*;
import fr.seeeek.carpenterprotocol.events.AddBlockH13WorldEvent;
import fr.seeeek.carpenterprotocol.events.MiniGamePlayerComponentEvent;
import fr.seeeek.carpenterprotocol.services.InstanceService;
import fr.seeeek.carpenterprotocol.systems.*;

import javax.annotation.Nonnull;

public class CarpenterProtocol extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();


    public CarpenterProtocol(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Init : " + this.getName() + " , Version : " + this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + this.getName());

        // Services
        InstanceService instanceService = new InstanceService(Universe.get());

        ComponentRegistryProxy<ChunkStore> chunkStoreRegistry = this.getChunkStoreRegistry();
        ComponentRegistryProxy<EntityStore> entityStoreRegistry = this.getEntityStoreRegistry();
        CommandRegistry commandRegistry = this.getCommandRegistry();
        EventRegistry eventRegistry = this.getEventRegistry();
        AssetRegistry assetRegistry = this.getAssetRegistry();

        // LASER TAG
        ComponentType<EntityStore, LaserTagRotatingLedsCubeComponent> rotatingLedsCubeComponentComponentType = entityStoreRegistry.registerComponent(LaserTagRotatingLedsCubeComponent.class, LaserTagRotatingLedsCubeComponent::new);
        LaserTagRotatingLedsCubeComponent.setComponentType(rotatingLedsCubeComponentComponentType);

        ComponentType<EntityStore, LaserTagGameComponent> laserTagGameComponentComponentType = entityStoreRegistry.registerComponent(LaserTagGameComponent.class, "LaserTagGameComponent", LaserTagGameComponent.CODEC);
        LaserTagGameComponent.setComponentType(laserTagGameComponentComponentType);

        ComponentType<EntityStore, LaserTagPlayerComponent> laserTagPlayerComponentComponentType = entityStoreRegistry.registerComponent(LaserTagPlayerComponent.class, LaserTagPlayerComponent::new);
        LaserTagPlayerComponent.setComponentType(laserTagPlayerComponentComponentType);

        ComponentType<EntityStore, LaserTagTeamComponent> laserTagTeamComponentComponentType = entityStoreRegistry.registerComponent(LaserTagTeamComponent.class, LaserTagTeamComponent::new);
        LaserTagTeamComponent.setComponentType(laserTagTeamComponentComponentType);

        ComponentType<ChunkStore, LaserTagMarkerTeamSpawnerComponent> laserTagMarkerTeamSpawnerComponentComponentType = chunkStoreRegistry.registerComponent(LaserTagMarkerTeamSpawnerComponent.class, "LaserTagMarkerTeamSpawnerComponent", LaserTagMarkerTeamSpawnerComponent.CODEC);
        LaserTagMarkerTeamSpawnerComponent.setComponentType(laserTagMarkerTeamSpawnerComponentComponentType);

        entityStoreRegistry.registerSystem(new LaserTagRotatingLedsCubeSystem());
        entityStoreRegistry.registerSystem(new LaserTagPlayerDeathSystem());
        entityStoreRegistry.registerSystem(new LaserTagPlayerComponentSystem());
        entityStoreRegistry.registerSystem(new LaserTagFriendlyFireSystem());

        // bots
//        commandRegistry.registerCommand(new SpawnLaserTagBlueBotCommand());
//        commandRegistry.registerCommand(new SpawnLaserTagRedBotCommand());
//        commandRegistry.registerCommand(new SpawnLaserTagBotsCommand());

        // MINI-GAMES
        ComponentType<EntityStore, MiniGameComponent> miniGameComponentComponentType = entityStoreRegistry.registerComponent(MiniGameComponent.class, MiniGameComponent::new);
        MiniGameComponent.setComponentType(miniGameComponentComponentType);

        ComponentType<EntityStore, MiniGamePlayerComponent> miniGamePlayerComponentComponentType = entityStoreRegistry.registerComponent(MiniGamePlayerComponent.class, MiniGamePlayerComponent::new);
        MiniGamePlayerComponent.setComponentType(miniGamePlayerComponentComponentType);

        ComponentType<EntityStore, MiniGameUIComponent> miniGameUIComponentComponentType = entityStoreRegistry.registerComponent(MiniGameUIComponent.class, MiniGameUIComponent::new);
        MiniGameUIComponent.setComponentType(miniGameUIComponentComponentType);

        ComponentType<EntityStore, MiniGameHudComponent> miniGameHudComponentComponentType = entityStoreRegistry.registerComponent(MiniGameHudComponent.class, MiniGameHudComponent::new);
        MiniGameHudComponent.setComponentType(miniGameHudComponentComponentType);

        // debugs commands
        commandRegistry.registerCommand(new OverrideMiniGameStateCommand());
        // commandRegistry.registerCommand(new LaserTagCreateCommand(instanceService));
        // commandRegistry.registerCommand(new GetMiniGameStateCommand());
        // commandRegistry.registerCommand(new OverrideLaserTagPlayerComponentKillsCommand());
        // commandRegistry.registerCommand(new GetMiniGamePlayerStateCommand());
        // commandRegistry.registerCommand(new OverridePlayerStateCommand());
        // commandRegistry.registerCommand(new GetConfigCommand());
        // commandRegistry.registerCommand(new GetLaserTagGameCommand());
        // commandRegistry.registerCommand(new GetLaserTagPlayerCommand());

        eventRegistry.registerGlobal(PlayerReadyEvent.class, MiniGamePlayerComponentEvent::onPlayerReady);
        eventRegistry.registerGlobal(AddWorldEvent.class, AddBlockH13WorldEvent::onBlockH13Added);
        eventRegistry.registerGlobal(StartWorldEvent.class, AddBlockH13WorldEvent::onBlockH13Start);

        getEventRegistry().register(DropItemEvent.class, event -> {
            // DropItemEvent is cancellable
            event.setCancelled(true);  // Prevent drop
        });

        entityStoreRegistry.registerSystem(new MiniGameSystem());
        entityStoreRegistry.registerSystem(new MiniGameUISystem());
        entityStoreRegistry.registerSystem(new MiniGameHudSystem());
    }
}