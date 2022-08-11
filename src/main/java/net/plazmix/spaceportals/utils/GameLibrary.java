package net.plazmix.spaceportals.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import net.plazmix.PlazmixApi;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.GameCache;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.RemoteDatabaseRowType;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.hotbar.GameHotbar;
import net.plazmix.game.utility.hotbar.GameHotbarBuilder;
import net.plazmix.inventory.BaseInventory;
import net.plazmix.spaceportals.game.Game;
import net.plazmix.spaceportals.game.player.InGamePlayer;
import net.plazmix.spaceportals.game.team.GameTeam;
import net.plazmix.spaceportals.game.team.Team;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.function.Consumer;
import java.util.stream.Collectors;

@UtilityClass
public class GameLibrary {

    public String PREFIX = StringUtil.format("§5§nспайспарталз §7§l>< ");

    public Game GAME;
    public GameType GAME_TYPE;
    public GameCache GAME_CACHE = GamePlugin.getInstance().getCache();

    public static String RED_CRYSTAL = "red_crystal";
    public static String BLUE_CRYSTAL = "blue_crystal";
    public static String GREEN_CRYSTAL = "green_crystal";
    public static String YELLOW_CRYSTAL = "yellow_crystal";
    public static String DIAMOND = "diamond_res";
    public static String GENERATOR = "generator";

    public void createGame(GameType type) {
        GAME_TYPE = type;
        GAME = Game.create();
    }

    public void addItems(Player player, ItemStack... itemStacks) {
        PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            StringUtil.sendMessage(player, "INV_FULL");
            return;
        }

        inventory.addItem(itemStacks);
    }

    public GameCache getUserCache(Player player) {
        return GameUser.from(player).getCache();
    }

    public void initGuis() {
        GAME_CACHE.set("choose_gui",
                PlazmixApi.createSimpleInventory(
                        3,
                        "§7Выбор Команды",
                        (player, baseInventory) -> {
                            baseInventory.getInventoryUpdater().startUpdater(20);

                            int i = 2;
                            for (Team team : Team.values()) {
                                GameTeam gameTeam = GameLibrary.GAME
                                        .getTeam(team.getLocName().toLowerCase());

                                baseInventory.setClickItem(
                                        9 + i,
                                        ItemUtil.newBuilder(team.getMenuItem())
                                                .setName(team.getColorName())
                                                .addLore("")
                                                .setLore(
                                                        gameTeam.getPlayers()
                                                                .stream()
                                                                .map(inGamePlayer -> inGamePlayer.getPlazmixUser().getDisplayName())
                                                                .collect(Collectors.toList())
                                                )
                                                .build(),
                                        (player1, inventoryClickEvent) -> {
                                            player1.closeInventory();

                                            if (!gameTeam.addPlayer(new InGamePlayer(PlazmixUser.of(player1), team))) {
                                                StringUtil.sendMessage(player1,
                                                        o -> o.getMessage("TEAM_FULL").replace("%team%", team.getColorName()).toText()
                                                );
                                            }

                                            StringUtil.sendMessage(player1,
                                                    o -> o.getMessage("TEAM_JOIN").replace("%team%", team.getColorName()).toText()
                                            );
                                        });


                                i = i + 2;
                            }
                        }
                )
        );
    }

    public void initItems() {
        GAME_CACHE.set(RED_CRYSTAL,
                ItemUtil.newBuilder(Material.REDSTONE)
                        .setName("§cКристалл")
                        .setLore("§7Используйте его", "§7для покупки в магазине")
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .build()
        );

        GAME_CACHE.set(BLUE_CRYSTAL,
                ItemUtil.newBuilder(Material.REDSTONE)
                        .setName("§9Кристалл")
                        .setLore("§7Используйте его", "§7для покупки в магазине")
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .build()
        );

        GAME_CACHE.set(GREEN_CRYSTAL,
                ItemUtil.newBuilder(Material.REDSTONE)
                        .setName("§2Кристалл")
                        .setLore("§7Используйте его", "§7для покупки в магазине")
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .build()
        );

        GAME_CACHE.set(YELLOW_CRYSTAL,
                ItemUtil.newBuilder(Material.REDSTONE)
                        .setName("§eКристалл")
                        .setLore("§7Используйте его", "§7для покупки в магазине")
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .build()
        );

        GAME_CACHE.set(GENERATOR,
                ItemUtil.newBuilder(Material.DISPENSER)
                        .setName("§d§lГенератор")
                        .setLore("§7Нажмите по одной из платформ,", "§7на базе, чтобы установить")
                        .addEnchantment(Enchantment.LUCK, 5)
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                        .build()
        );

        GAME_CACHE.set(DIAMOND,
                ItemUtil.newBuilder(Material.DISPENSER)
                        .setName("§bАлмаз")
                        .setLore("§7Используйте его", "§7для покупки в магазине или", "§7для активации портала")
                        .addEnchantment(Enchantment.LUCK, 5)
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .build()
        );
    }

    public void initBars() {
        GAME_CACHE.set("choose_bar",
                GameHotbarBuilder.newBuilder()
                        .setMoveItems(false)
                        .addItem(
                                1,
                                ItemUtil.newBuilder()
                                        .setName("§bВыбрать Команду")
                                        .setLore("§7Нажмите, чтобы выбрать команду")
                                        .build(),
                                player1 ->
                                        GamePlugin.getInstance().getCache().get("choose_gui", BaseInventory.class)
                                                .openInventory(player1)
                        )
                        .addItem(
                                9,
                                ItemUtil.newBuilder()
                                        .setName("§cПокинуть Игру")
                                        .setLore("§7Нажмите, чтобы покинуть игру")
                                        .build(),
                                PlazmixCoreApi::redirectToLobby
                        )
                        .build()
        );
    }

    @AllArgsConstructor
    @FieldDefaults(makeFinal = true)
    public enum MySQLConstants {
        GAMES("games", RemoteDatabaseRowType.INT),
        WINS("wins", RemoteDatabaseRowType.INT),
        KILLS("kills", RemoteDatabaseRowType.INT),
        DEATHS("deaths", RemoteDatabaseRowType.INT),
        BEST_ENERGY("best_energy", RemoteDatabaseRowType.INT),
        BEST_TIME("best_time", RemoteDatabaseRowType.INT);

        private @Getter String column;
        private @Getter RemoteDatabaseRowType rowType;
    }


    @AllArgsConstructor
    @Getter
    public enum GameType {
        QUADRO("4x4", 16, 4,
                new Team[]{Team.BLUE, Team.RED, Team.GREEN, Team.YELLOW},
                gameUser -> GamePlugin.getInstance().getCache().get("choose_bar", GameHotbar.class)
                        .setHotbarTo(gameUser)
        ),
        TRIO("3x4", 12, 3,
                new Team[]{Team.BLUE, Team.RED, Team.GREEN},
                gameUser -> GamePlugin.getInstance().getCache().get("choose_bar", GameHotbar.class)
                        .setHotbarTo(gameUser)
        ),
        OCTO("2x8", 16, 2,
                new Team[]{Team.BLUE, Team.RED},
                gameUser -> GamePlugin.getInstance().getCache().get("choose_bar", GameHotbar.class)
                        .setHotbarTo(gameUser)
        );

        private final String locName;
        private final int players;
        private final int playersInTeams;
        private final Team[] teams;
        private final Consumer<Player> gameUserConsumer;
    }
}
