package net.plazmix.spaceportals.game;

import lombok.Getter;
import lombok.Setter;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.spaceportals.SpacePortals;
import net.plazmix.spaceportals.game.player.InGamePlayer;
import net.plazmix.spaceportals.game.shop.ShopGui;
import net.plazmix.spaceportals.game.shop.objects.ArmorCategory;
import net.plazmix.spaceportals.game.shop.objects.PickaxeCategory;
import net.plazmix.spaceportals.game.shop.objects.WeaponCategory;
import net.plazmix.spaceportals.game.team.GameTeam;
import net.plazmix.spaceportals.game.team.Team;
import net.plazmix.spaceportals.utils.GameLibrary;
import net.plazmix.spaceportals.utils.StringUtil;
import net.plazmix.utility.location.LocationUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Game {

    private @Getter @Setter GameTeam winner;
    private @Getter int timed;

    protected BukkitTask task;

    private Game() {}

    public static Game create() {
        return new Game();
    }

    public void start() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            InGamePlayer inGamePlayer = InGamePlayer.of(player);
            if (inGamePlayer == null) {
                teams(gameTeam -> {
                   if (gameTeam.isFull())
                       return;

                   gameTeam.addPlayer(new InGamePlayer(PlazmixUser.of(player), gameTeam.getColor()));
                });

                inGamePlayer = InGamePlayer.of(player);

                if (inGamePlayer == null)
                        throw new IllegalArgumentException("Could not load player " + player.getName());
            }

            inGamePlayer.onSpawn();

            player.teleport(new Location(inGamePlayer.getTeam().getWorld(), 0, 50, 0));
        });

        teams(gameTeam -> {
            gameTeam.startGenerate();
            gameTeam.getColor().getFakeEntity().setClickAction(player -> {
                InGamePlayer inGamePlayer = InGamePlayer.of(player);

                ShopGui.open(inGamePlayer,
                        new ArmorCategory(gameTeam.getColor(), inGamePlayer),
                        new PickaxeCategory(gameTeam.getColor(), inGamePlayer),
                        new WeaponCategory(gameTeam.getColor(), inGamePlayer)
                );
            });
            gameTeam.getColor().getFakeEntity().spawn();
        });

        timed = 0;
        task = GameSchedulers.runTimerAsync(
                20,
                0,
                () -> timed = timed + 1
        );
    }

    public void stop() {
        if (teams().size() > 2)
            throw new IllegalArgumentException("Teams > 2!!!");

        teams(gameTeam -> {
            gameTeam.stopGenerate();
            gameTeam.getColor().getFakeEntity().remove();
        });

        teams().stream().findFirst().ifPresent(this::setWinner);

        task.cancel();
    }

    public GameTeam getTeamByPlayer(Player player) {
        return InGamePlayer.of(player).getGameTeam();
    }

    public void addTeams(Team... teams) {
        if (teams == null)
            return;

        Arrays.stream(teams).forEach(team -> addTeam(team,
                LocationUtil.stringToLocation(
                        SpacePortals.getInstance().getConfig().getString("teams." + team.name().toLowerCase() + ".generator"))
                )
        );
    }

    public void addTeam(Team team, Location location) {
        GameLibrary.GAME_CACHE.set(team.name().toLowerCase(), new GameTeam(team, location));
    }

    public void removeTeam(Team team) {
        GameTeam gameTeam = getTeam(team);

        gameTeam.stopGenerate();
        gameTeam.getColor().getFakeEntity().remove();
        GameLibrary.GAME_CACHE.getMap().remove(team.name().toLowerCase());

        if (teams().size() == 1) {
            stop();
        }
    }

    public GameTeam getTeam(Team color) {
        return GameLibrary.GAME_CACHE.getOrDefault(color.name().toLowerCase() + "_team", () -> {
            throw new IllegalArgumentException(StringUtil.format("Could not find color with %s name", color.name().toLowerCase()));
        });
    }

    public GameTeam getTeam(String color) {
        return GameLibrary.GAME_CACHE.getOrDefault(color.toLowerCase() + "_team", () -> {
            throw new IllegalArgumentException(StringUtil.format("Could not find color with %s name", color.toLowerCase()));
        });
    }

    public Collection<GameTeam> teams() {
        return GameLibrary.GAME_CACHE.getMap()
                .entrySet()
                .stream()
                .parallel()
                .filter(entry -> entry.getKey().contains("_team"))
                .map(entry -> (GameTeam) entry.getValue())
                .collect(Collectors.toList());
    }

    public Collection<GameTeam> teams(Consumer<GameTeam> consumer) {
        Collection<GameTeam> gameTeams = new ArrayList<>();

        GameLibrary.GAME_CACHE.getMap()
                .entrySet()
                .stream()
                .parallel()
                .filter(entry -> entry.getKey().contains("_team"))
                .forEach(entry -> {
                    GameTeam gameTeam = (GameTeam) entry.getValue();

                    consumer.accept(gameTeam);
                    gameTeams.add(gameTeam);
                });

        return gameTeams;
    }
}
