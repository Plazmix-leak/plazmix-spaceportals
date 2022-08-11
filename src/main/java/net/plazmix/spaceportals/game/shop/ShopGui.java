package net.plazmix.spaceportals.game.shop;

import net.plazmix.PlazmixApi;
import net.plazmix.inventory.BaseInventory;
import net.plazmix.spaceportals.game.player.InGamePlayer;
import net.plazmix.spaceportals.game.team.Team;

import java.util.Arrays;

public class ShopGui {

    public final BaseInventory inventory;

    private ShopGui(InGamePlayer inGamePlayer, AbstractCategory... abstractCategories) {
        inventory = PlazmixApi.createSimpleInventory(
                6,
                "§7Торговец",
                (player, baseInventory) -> Arrays.stream(abstractCategories).forEach(s -> s.render(baseInventory))
        );
        inventory.openInventory(inGamePlayer.getPlayer());
    }

    public static void open(InGamePlayer inGamePlayer, AbstractCategory... abstractCategories) {
        new ShopGui(inGamePlayer, abstractCategories);
    }
}
