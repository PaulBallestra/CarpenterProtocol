package fr.seeeek.carpenterprotocol.huds;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;
import fr.seeeek.carpenterprotocol.components.LobbyComponent;
import fr.seeeek.carpenterprotocol.components.MiniGameComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class MiniGameInGameHud extends CustomUIHud {

    // Data to display - passed via constructor
    private final int dataValue1;
    private final int dataValue2;
    private final String dataValue3;

    public MiniGameInGameHud(@NonNullDecl PlayerRef playerRef, int dataValue1, int dataValue2, String dataValue3) {
        super(playerRef);
        this.dataValue1 = dataValue1;
        this.dataValue2 = dataValue2;
        this.dataValue3 = dataValue3;
    }

    @Override
    protected void build(@NonNullDecl UICommandBuilder builder) {
        builder.append("Hud/MiniGameInGameHud.ui");
    }

    public void refresh(MiniGameComponent game, LobbyComponent lobby, LaserTagPlayerComponent laserTagPlayerComponent) {

        UICommandBuilder commandBuilder = new UICommandBuilder();

        commandBuilder.set("#LobbyDisplayName.Text", lobby.getLobbyDisplayName());

        commandBuilder.set("#MiniGameType.Text", game.getMiniGameType().toString());

        commandBuilder.set("#MiniGameState.Text",
                "State: " + game.getState());

        commandBuilder.set("#PlayerCount.Text",
                game.getAlivePlayers().size()
                        + "/" +
                        game.getMaxPlayers());

        commandBuilder.set("#Stat1Value.Text", String.valueOf(laserTagPlayerComponent.getKills()));
        commandBuilder.set("#Stat1Key.Text", "Kills");

        commandBuilder.set("#Stat2Value.Text", String.valueOf(laserTagPlayerComponent.getDeaths()));
        commandBuilder.set("#Stat2Key.Text", "Deaths");

        commandBuilder.set("#Stat3Value.Text", dataValue3);
        commandBuilder.set("#Stat3Key.Text", "Timer");

        update(false, commandBuilder);
    }
}
