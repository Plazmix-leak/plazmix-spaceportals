package net.plazmix.spaceportals.game.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.plazmix.protocollib.entity.impl.FakePlayer;
import net.plazmix.spaceportals.SpacePortals;
import net.plazmix.spaceportals.game.portal.Portal;
import net.plazmix.spaceportals.utils.GameLibrary;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.location.LocationUtil;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public enum Team {

    RED("&c","Красные",
            ItemUtil.newBuilder(Material.WOOL)
                    .setDyeColor(DyeColor.RED)
                    .build(),
            Material.REDSTONE_BLOCK,
            GameLibrary.GAME_CACHE.get(GameLibrary.RED_CRYSTAL),
            Bukkit.getWorld("red"),
            new FakePlayer(
                    SpacePortals.getInstance().getConfig().getString("teams.red.shop.name"),
                    LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.red.shop.loc"))
            ),
            Color.RED,
            new Portal[]{
                    new Portal(
                            LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.red.portals.base")),
                            LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.red.portals.middle"))
                    ),
                    new Portal(
                            LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.red.portals.middle")),
                            LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.red.portals.base"))
                    )}
    ),
    BLUE("&9", "Синие",
            ItemUtil.newBuilder(Material.WOOL)
                    .setDyeColor(DyeColor.BLUE)
                    .build(),
            Material.LAPIS_ORE,
            GameLibrary.GAME_CACHE.get(GameLibrary.BLUE_CRYSTAL),
            Bukkit.getWorld("blue"),
            new FakePlayer(
                    SpacePortals.getInstance().getConfig().getString("teams.blue.shop.name"),
                    LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.blue.shop.loc"))
            ),
            Color.BLUE,
            new Portal[]{
                    new Portal(
                            LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.blue.portals.base")),
                            LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.blue.portals.middle"))
                    ),
                    new Portal(
                            LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.blue.portals.middle")),
                            LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.blue.portals.base"))
                    )}
    ),
    GREEN("&2", "Зелёные",
            ItemUtil.newBuilder(Material.WOOL)
                    .setDyeColor(DyeColor.GREEN)
                    .build(),
            Material.EMERALD_ORE,
            GameLibrary.GAME_CACHE.get(GameLibrary.GREEN_CRYSTAL),
            Bukkit.getWorld("green"),
            new FakePlayer(
                    SpacePortals.getInstance().getConfig().getString("teams.green.shop.name"),
                    LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.green.shop.loc"))
            ),
            Color.GREEN,
            new Portal[]{
                    new Portal(
                            LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.green.portals.base")),
                            LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.green.portals.middle"))
                    ),
                    new Portal(
                            LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.green.portals.middle")),
                            LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.green.portals.base"))
                    )}
    ),
    YELLOW("&e", "Жёлтые",
            ItemUtil.newBuilder(Material.WOOL)
                    .setDyeColor(DyeColor.YELLOW)
                    .build(),
            Material.GOLD_ORE,
            GameLibrary.GAME_CACHE.get(GameLibrary.YELLOW_CRYSTAL),
            Bukkit.getWorld("yellow"),
            new FakePlayer(
                    SpacePortals.getInstance().getConfig().getString("teams.yellow.shop.name"),
                    LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.yellow.shop.loc"))
            ),
            Color.YELLOW,
            new Portal[]{
                    new Portal(
                            LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.yellow.portals.base")),
                            LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.yellow.portals.middle"))
                            ),
                    new Portal(
                            LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.yellow.portals.middle")),
                            LocationUtil.stringToLocation(SpacePortals.getInstance().getConfig().getString("teams.yellow.portals.base"))
                    )}
    );

    private final String color;
    private final String locName;
    private final ItemStack menuItem;
    private final Material breakBlock;
    private final ItemStack droppedItem;
    private final World world;
    private final FakePlayer fakeEntity;
    private final Color chestplateColor;
    private final Portal[] portals;

    public String getColorName() {
        return color + locName;
    }
}
