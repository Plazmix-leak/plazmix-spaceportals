package net.plazmix.spaceportals.game.portal;

import lombok.Data;
import net.plazmix.PlazmixApi;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.inventory.BaseInventory;
import net.plazmix.spaceportals.game.player.InGamePlayer;
import net.plazmix.spaceportals.utils.GameLibrary;
import net.plazmix.spaceportals.utils.StringUtil;
import net.plazmix.utility.ItemUtil;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Data
public class Portal {

    private Location location;
    private Location to;

    private boolean active;

    private InGamePlayer who;
    private int time;

    public Portal(Location from, Location to) {
        from.setY(from.getY() + 1);
        this.location = from;
        this.to = to;
        this.active = false;
    }

    public void executeGui(InGamePlayer whoActivated) {
        this.who = whoActivated;

        if (active) {
            StringUtil.sendMessage(whoActivated.getPlayer(), "PORTAL_ALREADY_ACTIVATED");
            return;
        }

        BaseInventory inv = PlazmixApi.createSimpleInventory(3, StringUtil.format("§7Активировать портал за %s?", isTeamPortal() ? "25 ресурсов" : "5 алмазов"),
                (player, baseInventory) -> {
                    baseInventory.setClickItem(
                            12,
                            new ItemUtil.ItemBuilder(new ItemStack(Material.WOOL))
                                    .setName("§aДа!")
                                    .setDyeColor(DyeColor.GREEN)
                                    .build(),
                            (player1, inventoryClickEvent) -> {
                                if (isTeamPortal()) {
                                    activate(25, whoActivated.getTeam().name().toLowerCase() + "_crystal", player);
                                    return;
                                }

                                activate(5, GameLibrary.DIAMOND, player);
                            });

                    baseInventory.setClickItem(
                            16,
                            new ItemUtil.ItemBuilder(new ItemStack(Material.WOOL))
                                    .setName("§cНет.")
                                    .setDyeColor(DyeColor.RED)
                                    .build(),
                            (player1, inventoryClickEvent) -> player1.closeInventory());
                });

        inv.openInventory(who.getPlayer());
    }

    public void teleport(Player player) {
        if (active)
            player.teleport(to);
    }

    public void activate(int count, String res, Player player) {
        if (who.getResources(GameLibrary.GAME_CACHE.get(res)) < count) {
            player.closeInventory();
            StringUtil.sendMessage(player, "NOT_ENOUGH_RES_PORTAL");
            return;
        }

        who.takeResources(count, GameLibrary.GAME_CACHE.get(res));

        this.active = true;
        PortalListener.activatedPortals.add(this);
        GameSchedulers.runLater(20 * 60, this::deactivate);
    }

    public void deactivate() {
        this.active = false;

        PortalListener.activatedPortals.remove(this);
    }

    public boolean isTeamPortal() {
        return location.getWorld() == who.getTeam().getWorld();
    }
}
