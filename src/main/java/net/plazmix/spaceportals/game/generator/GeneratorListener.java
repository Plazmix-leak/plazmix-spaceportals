package net.plazmix.spaceportals.game.generator;

import net.plazmix.spaceportals.game.team.GameTeam;
import net.plazmix.spaceportals.utils.GameLibrary;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GeneratorListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!event.hasBlock())
            return;

        if (!event.hasItem())
            return;

        ItemStack item = event.getItem();

        if (!item.isSimilar(GameLibrary.GAME_CACHE.get(GameLibrary.GENERATOR)))
            return;

        Block block = event.getClickedBlock();

        if (block.getType() != Material.REDSTONE_BLOCK)
            return;

        Player player = event.getPlayer();
        GameTeam team = GameLibrary.GAME.getTeamByPlayer(player);

        if (team == null)
            return;

        Location loc = block.getLocation();

        loc.setY(block.getY() + 1);

        if (!team.getColor().getWorld().equals(player.getWorld()))
            return;

        team.getGenerators().add(new Generator(team, loc));
    }
}
