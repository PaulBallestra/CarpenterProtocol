package fr.seeeek.carpenterprotocol.huds;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class LaserTagInGameHud extends CustomUIHud {

    public LaserTagInGameHud(@NonNullDecl PlayerRef playerRef){
        super(playerRef);
    }

    @Override
    protected void build(@NonNullDecl UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("Hud/LaserTagInGameHud.ui");
    }

    public void refresh(LaserTagPlayerComponent laserTagPlayerComponent) {

        UICommandBuilder commandBuilder = new UICommandBuilder();

        commandBuilder.set("#Stat1Value.Text", String.valueOf(laserTagPlayerComponent.getKills()));
        commandBuilder.set("#Stat1Key.Text", "Kills");

        commandBuilder.set("#Stat2Value.Text", String.valueOf(laserTagPlayerComponent.getDeaths()));
        commandBuilder.set("#Stat2Key.Text", "Deaths");

        commandBuilder.set("#Stat3Value.Text", String.valueOf(laserTagPlayerComponent.getTeamId()));
        commandBuilder.set("#Stat3Key.Text", "TeamId");

        update(false, commandBuilder);
    }
}
