package net.plazmix.spaceportals.game.player;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.plazmix.PlazmixApi;
import net.plazmix.spaceportals.game.team.GameTeam;
import net.plazmix.spaceportals.game.team.Team;
import net.plazmix.spaceportals.utils.GameLibrary;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Data
public class InGamePlayer {

    private PlazmixUser plazmixUser;
    private Team team;
    private Inventory inventory;

    private int energy;
    private int maxedEnergy;

    private int multiplier;
    private int finalKills;

    public InGamePlayer(@NonNull PlazmixUser plazmixUser, @NonNull Team team) {
        this.plazmixUser = plazmixUser;
        this.team = team;

        this.energy = 0;
        this.maxedEnergy = 0;
        this.multiplier = 1;
        this.finalKills = 0;

        inventory = new Inventory(this);
        inventory.setDefaultInventory(team);
    }

    public static InGamePlayer of(Player player) {
        InGamePlayer[] ing = new InGamePlayer[]{null};

        GameLibrary.GAME.teams(gameTeam -> ing[0] = gameTeam.getPlayers()
                .stream()
                .parallel()
                .filter(inGamePlayer -> inGamePlayer.getPlayer().equals(player))
                .findFirst()
                .orElse(null));

        return ing[0];
    }

    public GameTeam getGameTeam() {
        return GameLibrary.GAME.getTeam(team);
    }

    public int getResources(ItemStack item) {
        ItemStack[] items = (ItemStack[]) Arrays.stream(getPlayer().getInventory().getContents())
                .filter(itemStack -> itemStack.getType() == item.getType())
                .toArray();

        int count = 0;

        for (ItemStack ite : items) {
            count = count + ite.getAmount();
        }

        return count;
    }

    public void takeResources(int count, ItemStack item) {
        ItemStack itemStack = Arrays.stream(getPlayer().getInventory().getContents())
                .filter(itemSt -> itemSt.getType() == item.getType())
                .findFirst()
                .orElse(null);

        if (itemStack == null)
            return;

        itemStack.setAmount(itemStack.getAmount() - count);
    }

    public Player getPlayer() {
        return plazmixUser.handle();
    }

    public void addEnergy(int add) {
        if (energy <= 0 && add > 0)
            getPlayer().setFoodLevel(20);


        this.energy = energy + (add * multiplier);
        this.maxedEnergy = maxedEnergy + energy;
        getPlayer().setLevel(energy);
    }

    public void removeEnergy(int removed) {
        this.energy = energy - removed;

        if (energy <= 0) {
            energy = 0;
            getPlayer().setFoodLevel(0);
        }

        getPlayer().setLevel(energy);
    }

    public void onKill() {
        GameLibrary.GAME.getTeamByPlayer(getPlayer()).addABodies(1);
        GameLibrary.getUserCache(getPlayer())
                .increment(GameLibrary.MySQLConstants.KILLS.getColumn());
    }

    public void onDeath() {
        multiplier = 1;

        GameLibrary.getUserCache(getPlayer())
                .increment(GameLibrary.MySQLConstants.DEATHS.getColumn());

        energy = (int) (energy - (energy * 0.1));

        GameTeam gameTeam = GameLibrary.GAME_CACHE.get(getTeam().name().toLowerCase());

        if (gameTeam.getGenerators().isEmpty())
            gameTeam.removePlayer(this);

        if (gameTeam.getPlayers().size() == 0)
            GameLibrary.GAME.removeTeam(team);
    }

    public void onSpawn() {
        ItemUtil.ItemBuilder chestplate = PlazmixApi.newItemBuilder(inventory.getItemStack(Inventory.CHESTPLATE));
        ItemUtil.ItemBuilder leggings = PlazmixApi.newItemBuilder(inventory.getItemStack(Inventory.LEGGINGS));
        ItemUtil.ItemBuilder boots = PlazmixApi.newItemBuilder(inventory.getItemStack(Inventory.BOOTS));

        if (getGameTeam().getUpgradeLevel(GameTeam.Upgrade.ARMOR) != 0) {
            chestplate.addEnchantment(
                    Enchantment.PROTECTION_ENVIRONMENTAL,
                    getGameTeam().getUpgradeLevel(GameTeam.Upgrade.ARMOR) - 1
            );

            leggings.addEnchantment(
                    Enchantment.PROTECTION_ENVIRONMENTAL,
                    getGameTeam().getUpgradeLevel(GameTeam.Upgrade.ARMOR) - 1
            );

            boots.addEnchantment(
                    Enchantment.PROTECTION_ENVIRONMENTAL,
                    getGameTeam().getUpgradeLevel(GameTeam.Upgrade.ARMOR) - 1
            );
        }

        getPlayer().getInventory().setChestplate(chestplate.build());
        getPlayer().getInventory().setLeggings(leggings.build());
        getPlayer().getInventory().setBoots(boots.build());

        ItemUtil.ItemBuilder sword = PlazmixApi.newItemBuilder(inventory.getItemStack(Inventory.SWORD));
        ItemUtil.ItemBuilder pickaxe = PlazmixApi.newItemBuilder(inventory.getItemStack(Inventory.PICKAXE));

        switch (getGameTeam().getUpgradeLevel(GameTeam.Upgrade.WEAPON)) {
            case 1: {
                sword.addEnchantment(Enchantment.DAMAGE_ALL, getGameTeam().getUpgradeLevel(GameTeam.Upgrade.WEAPON) - 1);
                break;
            }
            case 2: {
                sword.addEnchantment(Enchantment.KNOCKBACK, getGameTeam().getUpgradeLevel(GameTeam.Upgrade.WEAPON) - 1);
                break;
            }

            case 3: {
                sword.addEnchantment(Enchantment.DAMAGE_ALL, getGameTeam().getUpgradeLevel(GameTeam.Upgrade.WEAPON) - 2);
                break;
            }

            default: break;
        }

        switch (getGameTeam().getUpgradeLevel(GameTeam.Upgrade.PICKAXE)) {
            case 1:
            case 2: {
                sword.addEnchantment(Enchantment.DIG_SPEED, getGameTeam().getUpgradeLevel(GameTeam.Upgrade.PICKAXE) - 1);
                break;
            }

            case 3: {
                sword.addEnchantment(Enchantment.DIG_SPEED, getGameTeam().getUpgradeLevel(GameTeam.Upgrade.PICKAXE) - 1);
                sword.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 0);
                break;
            }

            case 4: {
                sword.addEnchantment(Enchantment.DIG_SPEED, getGameTeam().getUpgradeLevel(GameTeam.Upgrade.PICKAXE) - 1);
                sword.removeEnchantment(Enchantment.LOOT_BONUS_BLOCKS);
                break;
            }

            default: break;
        }

        GameLibrary.addItems(getPlayer(), sword.build(), pickaxe.build());
    }

    public static class Inventory {

        public static int HELMET = 0;
        public static int CHESTPLATE = 1;
        public static int LEGGINGS = 2;
        public static int BOOTS = 3;

        public static int SWORD = 4;
        public static int PICKAXE = 5;

        private @Getter @Setter InGamePlayer inGamePlayer;
        private final ItemStack[] itemStacks;

        public Inventory(InGamePlayer inGamePlayer) {
            this.inGamePlayer = inGamePlayer;
            this.itemStacks = new ItemStack[]{};
        }

        public ItemStack getItemStack(int i) {
            return itemStacks[i];
        }

        public void setItemStack(int i, ItemStack itemStack) {
            itemStacks[i] = itemStack;
        }

        public void setDefaultInventory(Team team) {
            setItemStack(
                    HELMET,
                    new ItemStack(Material.GLASS)
            );
            setItemStack(
                    CHESTPLATE,
                    new ItemUtil.ItemBuilder(new ItemStack(Material.LEATHER_CHESTPLATE))
                            .setLeatherColor(team.getChestplateColor())
                            .setUnbreakable(true)
                            .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                            .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                            .build()
            );
            setItemStack(
                    LEGGINGS,
                    new ItemUtil.ItemBuilder(new ItemStack(Material.GOLD_LEGGINGS))
                            .setUnbreakable(true)
                            .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                            .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                            .build()
            );
            setItemStack(
                    BOOTS,
                    new ItemUtil.ItemBuilder(new ItemStack(Material.GOLD_BOOTS))
                            .setUnbreakable(true)
                            .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                            .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                            .build()
            );
        }
    }
}
