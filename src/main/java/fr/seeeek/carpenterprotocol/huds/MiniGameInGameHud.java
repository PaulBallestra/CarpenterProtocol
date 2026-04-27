package fr.seeeek.carpenterprotocol.huds;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import fr.seeeek.carpenterprotocol.components.LaserTagPlayerComponent;
import fr.seeeek.carpenterprotocol.components.MiniGameComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class MiniGameInGameHud extends CustomUIHud {

    // Data to display - passed via constructor
    private final int dataValue1;
    private final int dataValue2;

    public MiniGameInGameHud(@NonNullDecl PlayerRef playerRef, int dataValue1, int dataValue2) {
        super(playerRef);
        this.dataValue1 = dataValue1;
        this.dataValue2 = dataValue2;
    }

    @Override
    protected void build(@NonNullDecl UICommandBuilder builder) {
        builder.append("Hud/MiniGameInGameHud.ui");
    }

    public void refresh(MiniGameComponent game, LaserTagPlayerComponent laserTagPlayerComponent) {

        UICommandBuilder commandBuilder = new UICommandBuilder();

        String teamName = getTeamName(laserTagPlayerComponent.getTeamId());
        String teamColor = getTeamColorHex(laserTagPlayerComponent.getTeamId());

        commandBuilder.set("#MiniGameState.Text", "State: " + game.getState());

        commandBuilder.set("#Team.Text", "Team: " + teamName);
        commandBuilder.set("#Team.Background", teamColor);

        commandBuilder.set("#PlayerCount.Text", game.getAlivePlayers().size() + "/" + game.getMinPlayers());

        commandBuilder.set("#Stat1Value.Text", String.valueOf(laserTagPlayerComponent.getKills()));
        commandBuilder.set("#Stat1Key.Text", "Kills");

        commandBuilder.set("#Stat2Value.Text", String.valueOf(laserTagPlayerComponent.getDeaths()));
        commandBuilder.set("#Stat2Key.Text", "Deaths");

        update(false, commandBuilder);
    }

    private String getTeamName(int teamId){
        return switch (teamId){
            case 0 -> "Red";
            case 1 -> "Blue";
            default -> "Unassigned";
        };
    }

    private String getTeamColorHex(int teamId) {
        return switch (teamId) {
            case 0 -> "#960000"; //red
            case 1 -> "#000096"; // blue
            case 2 -> "#009600"; // green
            case 3 -> "#969600"; // yellow
            default -> "#FFFFFF";
        };
    }
}
