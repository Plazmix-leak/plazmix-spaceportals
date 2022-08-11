package net.plazmix.spaceportals.state;

import lombok.NonNull;
import net.plazmix.game.GameCache;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.type.StandardEndingState;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.game.utility.worldreset.GameWorldReset;
import net.plazmix.spaceportals.game.scoreboard.ScoreboardManager;
import net.plazmix.spaceportals.game.team.GameTeam;
import net.plazmix.spaceportals.utils.GameLibrary;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class EndingState extends StandardEndingState {
    public EndingState(GamePlugin plugin) {
        super(plugin, "Конец Игры");

        GameSetting.INTERACT_BLOCK.set(plugin.getService(), false);
    }

    @Override
    protected String getWinnerPlayerName() {
        return ((GameTeam) GameLibrary.GAME_CACHE.get("winner")).getColor().getLocName();
    }

    @Override
    protected void handleStart() {
        GameWorldReset.resetAllWorlds();

        GameSchedulers.runTimer(0, 20, () -> {
            GameTeam gameTeam = GameLibrary.GAME_CACHE.get("winner");

            gameTeam.getPlayers().forEach(inGamePlayer -> {
                Player winnerUser = inGamePlayer.getPlayer();

                if (winnerUser == null) {
                    return;
                }

                Firework firework = winnerUser.getWorld().spawn(winnerUser.getLocation(), Firework.class);
                FireworkMeta fireworkMeta = firework.getFireworkMeta();

                fireworkMeta.setPower(1);
                fireworkMeta.addEffect(FireworkEffect.builder()
                        .with(FireworkEffect.Type.STAR)
                        .withColor(Color.RED)
                        .withColor(Color.GREEN)
                        .withColor(Color.WHITE)
                        .build());

                firework.setFireworkMeta(fireworkMeta);

                GameUser gameUser = GameUser.from(inGamePlayer.getPlayer());
                GameCache cache = gameUser.getCache();

                int bestTime = cache.getInt(GameLibrary.MySQLConstants.BEST_TIME.getColumn());
                int maxedEnergy = cache.getInt(GameLibrary.MySQLConstants.BEST_ENERGY.getColumn());

                cache.increment(GameLibrary.MySQLConstants.WINS.getColumn());
                cache.increment(GameLibrary.MySQLConstants.GAMES.getColumn());

                if (GameLibrary.GAME.getTimed() < bestTime) {
                    cache.set(GameLibrary.MySQLConstants.BEST_TIME.getColumn(), GameLibrary.GAME.getTimed());
                }

                if (inGamePlayer.getMaxedEnergy() < maxedEnergy) {
                    cache.set(GameLibrary.MySQLConstants.BEST_ENERGY.getColumn(), inGamePlayer.getMaxedEnergy());
                }

                winnerUser.sendTitle("§a§lПОБЕДА", "§fВаша команда лучше всех!");
                winnerUser.sendMessage("§e+500 монет (победа).");
                gameUser.getPlazmixHandle().addCoins(500);

                winnerUser.sendMessage("§3+10 опыта");
                gameUser.getPlazmixHandle().addExperience(10);
            });
        });


        Bukkit.getOnlinePlayers().forEach(player -> {
            player.playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 2, 0);
            player.sendMessage(GameLibrary.PREFIX + "§aИгра окончена!");
        });
    }

    @Override
    protected void handleScoreboardSet(@NonNull Player player) {
        ScoreboardManager.get(player).setEndBoard();
    }

    @Override
    protected Location getTeleportLocation() {
        return plugin.getService().getMapWorld().getSpawnLocation().clone().add(0.5, 0, 0.5);
    }
}
