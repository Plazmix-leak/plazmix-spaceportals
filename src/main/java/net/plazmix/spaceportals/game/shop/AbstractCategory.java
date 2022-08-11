package net.plazmix.spaceportals.game.shop;

import lombok.Getter;
import net.plazmix.inventory.BaseInventory;
import net.plazmix.spaceportals.game.player.InGamePlayer;
import net.plazmix.spaceportals.game.team.Team;
import net.plazmix.spaceportals.utils.GameLibrary;
import net.plazmix.spaceportals.utils.StringUtil;
import net.plazmix.utility.ItemUtil;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

@Getter
public abstract class AbstractCategory {

    public static int FIRST_SLOT = 11;
    public static int SECOND_SLOT = 12;
    public static int THIRD_SLOT = 13;
    public static int FOURTH_SLOT = 14;
    public static int FIFTH_SLOT = 15;
    public static int SIXTH_SLOT = 16;
    public static int SEVENTH_SLOT = 17;

    public String CRYSTAL = "crystal";
    public String DIAMOND = "diamond";
    public String ANTI_BODIES = "anti_bodies";

    private final int slot;
    private final Team team;
    private final ItemStack itemStack;
    private final InGamePlayer inGamePlayer;

    public AbstractCategory(Team team, int slot, InGamePlayer inGamePlayer) {
        this.slot = slot;
        this.team = team;
        this.itemStack = icon();
        this.inGamePlayer = inGamePlayer;
    }

    public abstract ItemStack icon();

    public abstract void render(BaseInventory baseInventory);

    public void setSellableItem(BaseInventory baseInventory, int slot, int price, String resource, ItemStack itemStack,
                                BiConsumer<ItemStack, InventoryClickEvent> ifCorrect)
    {
        if (!(slot >= 29 && slot <= 35 || slot >= 38 && slot <= 44))
            throw new IllegalArgumentException("Slot dont available");

        ItemStack item = null;
        if (!resource.equals(ANTI_BODIES))
            item = GameLibrary.GAME_CACHE.get(resource.contains(CRYSTAL) ? (team.name().toLowerCase() + "_") : "" + resource);

        int value = item != null ? inGamePlayer.getResources(item) : inGamePlayer.getGameTeam().getAntiBodies();

        ItemStack finalItemStack = new ItemUtil.ItemBuilder(itemStack)
                .setLore(
                        StringUtil.format("§7Цена: %s",
                                inGamePlayer.getTeam().getColor() + (resource.contains(CRYSTAL) ? " кристаллов" : resource.contains(ANTI_BODIES) ? "антител" : "алмазов")
                        )
                )
                .build();

        baseInventory.setClickItem(slot, finalItemStack,
                (player, inventoryClickEvent) -> {
                    if (value < price)
                        return;

                    ifCorrect.accept(itemStack, inventoryClickEvent);
                });
    }
}
