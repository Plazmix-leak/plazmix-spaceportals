package net.plazmix.spaceportals.game.shop.objects;

import net.plazmix.PlazmixApi;
import net.plazmix.inventory.BaseInventory;
import net.plazmix.spaceportals.game.player.InGamePlayer;
import net.plazmix.spaceportals.game.shop.AbstractCategory;
import net.plazmix.spaceportals.game.team.GameTeam;
import net.plazmix.spaceportals.game.team.Team;
import net.plazmix.utility.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static net.plazmix.spaceportals.game.player.InGamePlayer.Inventory;

public class ArmorCategory extends AbstractCategory {
    public ArmorCategory(Team team, InGamePlayer inGamePlayer) {
        super(team, FIFTH_SLOT, inGamePlayer);
    }

    @Override
    public ItemStack icon() {
        return new ItemUtil.ItemBuilder(new ItemStack(Material.GOLD_CHESTPLATE))
                .setName("§fБроня")
                .setLore(
                        "Данная категория включает в себя",
                        "разнообразную броню, которые помогут",
                        "тебе в сражениях с игроками!",
                        "",
                        "§eНажмите, чтобы открыть категорию!"
                )
                .build();
    }

    @Override
    public void render(BaseInventory baseInventory) {
        Inventory inv = getInGamePlayer().getInventory();
        PlayerInventory pInv = getInGamePlayer().getPlayer().getInventory();
        GameTeam gameTeam = getInGamePlayer().getGameTeam();

        setSellableItem(
                baseInventory,
                29,
                20, CRYSTAL,
                PlazmixApi.newItemBuilder(Material.IRON_BOOTS)
                        .setUnbreakable(true)
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                        .build(),
                (itemStack, inventoryClickEvent) -> {
                    inv.setItemStack(
                            Inventory.LEGGINGS,
                            PlazmixApi.newItemBuilder(Material.IRON_LEGGINGS)
                                    .setUnbreakable(true)
                                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                                    .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                                    .build()
                    );

                    inv.setItemStack(Inventory.BOOTS, itemStack);
                });

        setSellableItem(
                baseInventory,
                30,
                7, DIAMOND,
                PlazmixApi.newItemBuilder(Material.DIAMOND_BOOTS)
                        .setUnbreakable(true)
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                        .build(),
                (itemStack, inventoryClickEvent) -> {

                    pInv.setLeggings(
                            PlazmixApi.newItemBuilder(Material.DIAMOND_LEGGINGS)
                                    .setUnbreakable(true)
                                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                                    .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                                    .build()
                    );

                    pInv.setBoots(itemStack);
                });

        switch (gameTeam.getUpgradeLevel(GameTeam.Upgrade.ARMOR)) {
            case 0: {
                setSellableItem(
                        baseInventory,
                        38,
                        2, ANTI_BODIES,
                        PlazmixApi.newItemBuilder(Material.ENCHANTED_BOOK)
                                .setName("§fЗащита I")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                                .build(),
                        (itemStack, inventoryClickEvent) -> gameTeam.incrementUpgrade(GameTeam.Upgrade.ARMOR));

                break;
            }

            case 1: {
                setSellableItem(
                        baseInventory,
                        38,
                        3, ANTI_BODIES,
                        PlazmixApi.newItemBuilder(Material.ENCHANTED_BOOK)
                                .setName("§fЗащита II")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                                .build(),
                        (itemStack, inventoryClickEvent) -> gameTeam.incrementUpgrade(GameTeam.Upgrade.ARMOR));
                break;
            }

            case 2: {
                setSellableItem(
                        baseInventory,
                        38,
                        5, ANTI_BODIES,
                        PlazmixApi.newItemBuilder(Material.ENCHANTED_BOOK)
                                .setName("§fЗащита III")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                                .build(),
                        (itemStack, inventoryClickEvent) -> gameTeam.incrementUpgrade(GameTeam.Upgrade.ARMOR));

                break;
            }
        }
    }
}
