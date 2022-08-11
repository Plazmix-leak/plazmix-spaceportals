package net.plazmix.spaceportals.state;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.type.StandardWaitingState;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.spaceportals.game.scoreboard.ScoreboardManager;
import net.plazmix.spaceportals.utils.GameLibrary;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WaitingState extends StandardWaitingState {

    private final Location teleport;

    public WaitingState(@NonNull GamePlugin plugin, @NonNull Location teleport) {
        super(plugin, "Ожидание Игроков");

        this.teleport = teleport;
        GameSetting.BLOCK_BREAK.set(plugin.getService(), false);
    }

    @Override
    protected Location getTeleportLocation() {
        return teleport;
    }

    @Override
    protected void handleEvent(@NonNull PlayerJoinEvent event) {
        int online = Bukkit.getOnlinePlayers().size();
        int maxOnline = getPlugin().getService().getMaxPlayers();

        GameSchedulers.runLater(10, () -> {
            ScoreboardManager.get(event.getPlayer())
                    .setWaitingBoard(timerStatus);

            GameLibrary.GAME_TYPE.getGameUserConsumer().accept(event.getPlayer());
        });

        event.setJoinMessage(GameLibrary.PREFIX + PlazmixUser.of(event.getPlayer()).getDisplayName() + " §fподключился к игре! §7(" + online + "/" + maxOnline + ")");

        if (online >= GameLibrary.GAME_TYPE.getPlayersInTeams() / 2 && !timerStatus.isLived())
            timerStatus.runTask();
    }

    @Override
    protected void handleEvent(@NonNull PlayerQuitEvent event) {
        int online = Bukkit.getOnlinePlayers().size() - 1;
        int maxOnline = getPlugin().getService().getMaxPlayers();

        event.setQuitMessage(GameLibrary.PREFIX + PlazmixUser.of(event.getPlayer()).getDisplayName() + " §fпокинул игру! §7(" + online + "/" + maxOnline + ")");

        if (online < maxOnline && timerStatus.isLived())
            timerStatus.cancelTask();
    }

    @Override
    protected void handleTimerUpdate(@NonNull TimerStatus timerStatus) {

    }
}
