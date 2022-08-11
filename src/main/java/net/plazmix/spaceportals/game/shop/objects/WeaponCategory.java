package net.plazmix.spaceportals.game.shop.objects;

import net.plazmix.PlazmixApi;
import net.plazmix.inventory.BaseInventory;
import net.plazmix.spaceportals.game.player.InGamePlayer;
import net.plazmix.spaceportals.game.shop.AbstractCategory;
import net.plazmix.spaceportals.game.team.GameTeam;
import net.plazmix.spaceportals.game.team.Team;
import net.plazmix.spaceportals.utils.GameLibrary;
import net.plazmix.utility.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

import static net.plazmix.spaceportals.game.player.InGamePlayer.Inventory;

public class WeaponCategory extends AbstractCategory {
    public WeaponCategory(Team team, InGamePlayer inGamePlayer) {
        super(team, FIRST_SLOT, inGamePlayer);
    }

    @Override
    public ItemStack icon() {
        return new ItemUtil.ItemBuilder(new ItemStack(Material.GOLD_SWORD))
                .setName("§fОружие")
                .setLore(
                        "Данная категория включает в себя",
                        "разнообразные оружие, которые помогут",
                        "тебе в сражениях с игроками!",
                        "",
                        "§eНажмите, чтобы открыть категорию!"
                )
                .build();
    }


    @Override
    public void render(BaseInventory baseInventory) {
        Player player = getInGamePlayer().getPlayer();
        Inventory inv = getInGamePlayer().getInventory();
        GameTeam gameTeam = getInGamePlayer().getGameTeam();

        setSellableItem(
                baseInventory,
                29,
                5, CRYSTAL,
                PlazmixApi.newItemBuilder(Material.STONE_SWORD)
                        .setUnbreakable(true)
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                        .build(),
                (itemStack, inventoryClickEvent) -> inv.setItemStack(Inventory.SWORD, itemStack));

        setSellableItem(
                baseInventory,
                30,
                10, CRYSTAL,
                PlazmixApi.newItemBuilder(Material.IRON_SWORD)
                        .setUnbreakable(true)
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                        .build(),
                (itemStack, inventoryClickEvent) -> inv.setItemStack(Inventory.SWORD, itemStack));

        setSellableItem(
                baseInventory,
                31,
                3, DIAMOND,
                PlazmixApi.newItemBuilder(Material.DIAMOND_SWORD)
                        .setUnbreakable(true)
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                        .build(),
                (itemStack, inventoryClickEvent) -> {

                    Arrays.stream(player.getInventory().getContents())
                            .filter(itemStack1 -> itemStack1.getType().name().contains("SWORD"))
                            .forEach(itemStack1 -> itemStack1.setAmount(0));

                    GameLibrary.addItems(player, itemStack);
                });

        setSellableItem(
                baseInventory,
                32,
                24, CRYSTAL,
                PlazmixApi.newItemBuilder(Material.BOW)
                        .setUnbreakable(true)
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                        .build(),
                (itemStack, inventoryClickEvent) -> GameLibrary.addItems(getInGamePlayer().getPlayer(), itemStack));

        setSellableItem(
                baseInventory,
                33,
                12, CRYSTAL,
                PlazmixApi.newItemBuilder(Material.ARROW)
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .setAmount(4)
                        .build(),
                (itemStack, inventoryClickEvent) -> GameLibrary.addItems(getInGamePlayer().getPlayer(), itemStack));

        setSellableItem(
                baseInventory,
                34,
                15, CRYSTAL,
                PlazmixApi.newItemBuilder(Material.FISHING_ROD)
                        .setUnbreakable(true)
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                        .build(),
                (itemStack, inventoryClickEvent) -> GameLibrary.addItems(getInGamePlayer().getPlayer(), itemStack));

        setSellableItem(
                baseInventory,
                35,
                5, CRYSTAL,
                PlazmixApi.newItemBuilder(Material.FLINT_AND_STEEL)
                        .setDurability(3)
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .build(),
                (itemStack, inventoryClickEvent) -> GameLibrary.addItems(getInGamePlayer().getPlayer(), itemStack));

        switch (gameTeam.getUpgradeLevel(GameTeam.Upgrade.WEAPON)) {
            case 0: {
                setSellableItem(
                        baseInventory,
                        38,
                        2, ANTI_BODIES,
                        PlazmixApi.newItemBuilder(Material.ENCHANTED_BOOK)
                                .setName("§fОстрота I")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                                .build(),
                        (itemStack, inventoryClickEvent) -> gameTeam.incrementUpgrade(GameTeam.Upgrade.WEAPON));

                break;
            }

            case 1: {
                setSellableItem(
                        baseInventory,
                        38,
                        3, ANTI_BODIES,
                        PlazmixApi.newItemBuilder(Material.ENCHANTED_BOOK)
                                .setName("§fОтдача I")
                                .setLore("§cУбирает Остроту!")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                                .build(),
                        (itemStack, inventoryClickEvent) -> gameTeam.incrementUpgrade(GameTeam.Upgrade.WEAPON));

                break;
            }

            case 2: {
                setSellableItem(
                        baseInventory,
                        38,
                        5, ANTI_BODIES,
                        PlazmixApi.newItemBuilder(Material.ENCHANTED_BOOK)
                                .setName("§fОстрота II")
                                .setLore("§cУбирает Отдачу!")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                                .build(),
                        (itemStack, inventoryClickEvent) -> gameTeam.incrementUpgrade(GameTeam.Upgrade.WEAPON));

                break;
            }

            default: {
                baseInventory.setOriginalItem(
                        38,
                        PlazmixApi.newItemBuilder(Material.BARRIER)
                                .setName("§cМаксимальный уровень")
                                .build()
                );
                break;
            }
        }
    }
}
