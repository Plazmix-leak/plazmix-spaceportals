package net.plazmix.spaceportals.listener;

import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.spaceportals.game.team.GameTeam;
import net.plazmix.spaceportals.utils.GameLibrary;
import net.plazmix.utility.location.region.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RegionListener implements Listener {
    private final List<Block> blockList = new ArrayList<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        event.setExpToDrop(0);

        Block block = event.getBlock();
        GameTeam gameTeam = GameLibrary.GAME.getTeamByPlayer(event.getPlayer());

        if (block.getType() == gameTeam.getColor().getBreakBlock()) {
            event.setCancelled(true);
            block.setType(Material.BEDROCK);

            GameSchedulers.runLater(60, () -> block.setType(gameTeam.getColor().getBreakBlock()));

            ItemStack dropped = gameTeam.getColor().getDroppedItem();

            if (event.getPlayer().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                if (ThreadLocalRandom.current().nextDouble() <= 0.33) {
                    dropped.setAmount(2);
                }
            }

            GameLibrary.addItems(event.getPlayer(), dropped);
            return;
        }

        if (!blockList.contains(block))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        ItemStack itemStack = event.getItemInHand();

        if (itemStack == null)
            return;

        if
        (
                Arrays.stream(GameLibrary.GAME_TYPE.getTeams()).anyMatch(team ->
                        CuboidRegion.cubeRadius(new Location(team.getWorld(), 0, 0, 0), 10).contains(block))
                        ||
                        itemStack.isSimilar(GameLibrary.GAME_CACHE.get(GameLibrary.GENERATOR))
        ) {
            event.setCancelled(true);
            return;
        }

        blockList.add(block);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        List<Block> exploded = event.blockList();

        exploded.stream()
                .parallel()
                .filter(blockList::contains)
                .forEach(block -> block.setType(Material.AIR));
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (event.getNewState().getBlock().getType() == Material.SOIL) {
            event.setCancelled(true);
        }
    }
}
