package net.plazmix.spaceportals.game.team;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.spaceportals.SpacePortals;
import net.plazmix.spaceportals.game.generator.Generator;
import net.plazmix.spaceportals.game.player.InGamePlayer;
import net.plazmix.spaceportals.utils.GameLibrary;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Getter
public class GameTeam {

    private final List<InGamePlayer> players;
    private final List<Generator> generators;
    private final Map<Upgrade, Integer> upgrades;
    private final Team color;
    private final BukkitTask[] tasks;
    private @Setter int antiBodies;

    public GameTeam(@NonNull Team color, Location loc) {
        this.players = new ArrayList<>();
        this.generators = new ArrayList<>();
        this.upgrades = new HashMap<>();
        this.color = color;
        this.tasks = new BukkitTask[1];

        generators.add(new Generator(this, loc));
        Arrays.stream(Upgrade.values()).forEach(upgrade -> upgrades.put(upgrade, 0));
    }

    public boolean isFull() {
        return players.size() >= GameLibrary.GAME_TYPE.getPlayersInTeams();
    }

    public boolean addPlayer(@NonNull InGamePlayer inGamePlayer) {
        if (isFull())
            return false;

        players.add(inGamePlayer);
        return true;
    }

    public void removePlayer(@NonNull InGamePlayer inGamePlayer) {
        players.remove(inGamePlayer);

        GameLibrary.getUserCache(inGamePlayer.getPlayer()).increment(GameLibrary.MySQLConstants.GAMES.getColumn());
    }

    public boolean hasPlayer(@NonNull PlazmixUser user) {
        return players.stream().anyMatch(inGamePlayer -> inGamePlayer.getPlazmixUser().equals(user));
    }

    public void startGenerate() {
        tasks[0] = GameSchedulers.runTimerAsync(
                0,
                SpacePortals.getInstance()
                        .getConfig()
                        .getInt("game.generator"),
                () -> {
            if (generators.isEmpty())
                return;

            getPlayers()
                    .forEach(inGamePlayer -> {
                        if (inGamePlayer.getPlayer()
                                .getWorld()
                                .equals(color.getWorld()))
                            inGamePlayer.addEnergy(generators.size());
                    });


        });

        tasks[1] = GameSchedulers.runTimerAsync(
                0,
                20,
                () -> getPlayers()
                        .forEach(inGamePlayer -> inGamePlayer.removeEnergy(1))
        );
    }

    public void stopGenerate() {
        tasks[0].cancel();
        tasks[1].cancel();
    }

    public void addABodies(int added) {
        setAntiBodies(antiBodies + added);
    }

    public int getUpgradeLevel(Upgrade upgrade) {
        return upgrades.get(upgrade);
    }

    public void incrementUpgrade(Upgrade upgrade) {
        upgrades.put(upgrade, upgrades.get(upgrade) + 1);
    }

    public enum Upgrade {

        ARMOR,
        WEAPON,
        PICKAXE
    }
}
