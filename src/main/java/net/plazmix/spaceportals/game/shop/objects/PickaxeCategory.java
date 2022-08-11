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

public class PickaxeCategory extends AbstractCategory {
    public PickaxeCategory(Team team, InGamePlayer inGamePlayer) {
        super(team, THIRD_SLOT, inGamePlayer);
    }

    @Override
    public ItemStack icon() {
        return new ItemUtil.ItemBuilder(new ItemStack(Material.GOLD_SWORD))
                .setName("§fКирка")
                .setLore(
                        "Данная категория включает в себя",
                        "улучшения кирки, которые помогут",
                        "тебе в добывать быстрее и больше!",
                        "",
                        "§eНажмите, чтобы открыть категорию!"
                )
                .build();
    }

    @Override
    public void render(BaseInventory baseInventory) {
        GameTeam gameTeam = getInGamePlayer().getGameTeam();

        switch (gameTeam.getUpgradeLevel(GameTeam.Upgrade.PICKAXE)) {
            case 0: {
                setSellableItem(
                        baseInventory,
                        29,
                        3, ANTI_BODIES,
                        PlazmixApi.newItemBuilder(Material.ENCHANTED_BOOK)
                                .setName("§fЭффективность I")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                                .build(),
                        (itemStack, inventoryClickEvent) -> gameTeam.incrementUpgrade(GameTeam.Upgrade.PICKAXE));

                break;
            }

            case 1: {
                setSellableItem(
                        baseInventory,
                        29,
                        3, ANTI_BODIES,
                        PlazmixApi.newItemBuilder(Material.ENCHANTED_BOOK)
                                .setName("§fЭффективность II")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                                .build(),
                        (itemStack, inventoryClickEvent) -> gameTeam.incrementUpgrade(GameTeam.Upgrade.PICKAXE));

                break;
            }

            case 2: {
                setSellableItem(
                        baseInventory,
                        29,
                        3, ANTI_BODIES,
                        PlazmixApi.newItemBuilder(Material.ENCHANTED_BOOK)
                                .setName("§fЭффективность III и Удача I")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                                .build(),
                        (itemStack, inventoryClickEvent) -> gameTeam.incrementUpgrade(GameTeam.Upgrade.PICKAXE));

                break;
            }

            case 3: {
                setSellableItem(
                        baseInventory,
                        29,
                        3, ANTI_BODIES,
                        PlazmixApi.newItemBuilder(Material.ENCHANTED_BOOK)
                                .setName("§fЭффективность IV")
                                .setLore("§cУбирает Удачу!")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                                .build(),
                        (itemStack, inventoryClickEvent) -> gameTeam.incrementUpgrade(GameTeam.Upgrade.PICKAXE));

                break;
            }
        }
    }
}
