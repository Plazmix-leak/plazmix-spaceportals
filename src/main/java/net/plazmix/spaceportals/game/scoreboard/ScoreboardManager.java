package net.plazmix.spaceportals.game.scoreboard;

import lombok.NonNull;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.state.type.StandardWaitingState;
import net.plazmix.scoreboard.BaseScoreboardBuilder;
import net.plazmix.scoreboard.BaseScoreboardScope;
import net.plazmix.spaceportals.game.player.InGamePlayer;
import net.plazmix.spaceportals.game.team.GameTeam;
import net.plazmix.spaceportals.game.team.Team;
import net.plazmix.spaceportals.utils.GameLibrary;
import net.plazmix.spaceportals.utils.StringUtil;
import net.plazmix.utility.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ScoreboardManager {

    private final Player player;

    private final GamePlugin gamePlugin = GamePlugin.getInstance();
    private final BaseScoreboardBuilder scoreboardBuilder;

    private ScoreboardManager(@NonNull Player player) {
        this.player = player;
        scoreboardBuilder = BaseScoreboardBuilder.newScoreboardBuilder();

        scoreboardBuilder.scoreboardDisplay("§5§lSpacePortals");
        scoreboardBuilder.scoreboardScope(BaseScoreboardScope.PROTOTYPE);
    }

    public static ScoreboardManager get(Player player) {
        return new ScoreboardManager(player);
    }

     public void setWaitingBoard(StandardWaitingState.TimerStatus timerStatus) {
         scoreboardBuilder.scoreboardLine(8, "");
         scoreboardBuilder.scoreboardLine(7, "§fКарта: §a" + gamePlugin.getService().getMapName());
         scoreboardBuilder.scoreboardLine(6, "§fРежим: §e" + GameLibrary.GAME_TYPE.getLocName());
         scoreboardBuilder.scoreboardLine(5, "");
         scoreboardBuilder.scoreboardLine(4, "...");
         scoreboardBuilder.scoreboardLine(3, "§fИгроки: §5" + Bukkit.getOnlinePlayers().size() + "§f/§c" + gamePlugin.getService().getMaxPlayers());
         scoreboardBuilder.scoreboardLine(2, "");
         scoreboardBuilder.scoreboardLine(1, "§dwww.plazmix.net");

         scoreboardBuilder.scoreboardUpdater((baseScoreboard, player1) -> {
             baseScoreboard.updateScoreboardLine(
                     4, player,
                     (!timerStatus.isLived() ?
                             "§7Ожидание игроков..."
                             :
                             "§fИгра начнется через §a" + NumberUtil.formattingSpaced(timerStatus.getLeftSeconds(), "§fсекунду", "§fсекунды", "§fсекунд"))
             );

             baseScoreboard.updateScoreboardLine(
                     3, player,
                     "§fИгроки: §a" + Bukkit.getOnlinePlayers().size() + "§f/§c" + gamePlugin.getService().getMaxPlayers()
             );
         }, 20);

         scoreboardBuilder.build().setScoreboardToPlayer(player);
     }

    public void setInGameBoard() {
        InGamePlayer inGamePlayer = InGamePlayer.of(player);

        scoreboardBuilder.scoreboardLine(13, "");
        scoreboardBuilder.scoreboardLine(12, "§fГенераторов: §c" + GameLibrary.GAME.getTeam(inGamePlayer.getTeam()).getGenerators().size());
        scoreboardBuilder.scoreboardLine(11, "§fВаша энергия: §e" + inGamePlayer.getEnergy());
        scoreboardBuilder.scoreboardLine(10, "");
        scoreboardBuilder.scoreboardLine(9, "§fИнформация о командах:");
        scoreboardBuilder.scoreboardLine(8, "");
        scoreboardBuilder.scoreboardLine(7, "");
        scoreboardBuilder.scoreboardLine(6, "§fФинальных убийств: §c" + inGamePlayer.getFinalKills());
        scoreboardBuilder.scoreboardLine(5, "");
        scoreboardBuilder.scoreboardLine(4, "§fКарта: §a" + gamePlugin.getService().getMapName());
        scoreboardBuilder.scoreboardLine(3, "§fРежим: §e" + GameLibrary.GAME_TYPE.getLocName());
        scoreboardBuilder.scoreboardLine(2, "");
        scoreboardBuilder.scoreboardLine(1, "§dwww.plazmix.net");

        scoreboardBuilder.scoreboardUpdater((baseScoreboard, player1) -> {
            baseScoreboard.updateScoreboardLine(
                    12, player,
                    "§fГенераторов: §c" + inGamePlayer.getGameTeam().getGenerators().size()
            );

            baseScoreboard.updateScoreboardLine(
                    11, player,
                    "§fВаша энергия: §e" + inGamePlayer.getEnergy()
            );

            StringBuilder builder = new StringBuilder();

            Arrays.stream(GameLibrary.GAME_TYPE.getTeams()).forEach(team -> {
                        GameTeam gameTeam = GameLibrary.GAME.getTeam(team);

                        builder.append(StringUtil.format("%s[%s] ", team.getColor(), gameTeam.getGenerators().size()));
                    }
            );

            scoreboardBuilder.scoreboardLine(8, builder.toString());
            scoreboardBuilder.scoreboardLine(6, "§fФинальных убийств: §c" + inGamePlayer.getFinalKills());
        }, 20);

        scoreboardBuilder.build().setScoreboardToPlayer(player);
    }

    public void setEndBoard() {
        Team winner = GamePlugin.getInstance().getCache().get("winner");

        scoreboardBuilder.scoreboardLine(8, "");
        scoreboardBuilder.scoreboardLine(7, "§fПобедитель игры:");
        scoreboardBuilder.scoreboardLine(6, " " + (winner != null ? winner.getColorName() + " §fКоманда" : "§cНеопределено"));
        scoreboardBuilder.scoreboardLine(5, "");
        scoreboardBuilder.scoreboardLine(4, "§fКарта: §a" + GamePlugin.getInstance().getService().getMapName());
        scoreboardBuilder.scoreboardLine(3, "§fСервер: §a" + PlazmixCoreApi.getCurrentServerName());
        scoreboardBuilder.scoreboardLine(2, "");
        scoreboardBuilder.scoreboardLine(1, "§dwww.plazmix.net");

        scoreboardBuilder.build().setScoreboardToPlayer(player);

    }
}
