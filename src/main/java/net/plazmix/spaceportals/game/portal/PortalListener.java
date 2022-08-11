package net.plazmix.spaceportals.game.portal;

import net.plazmix.spaceportals.game.player.InGamePlayer;
import net.plazmix.spaceportals.utils.GameLibrary;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PortalListener implements Listener {

    public static List<Portal> activatedPortals = new ArrayList<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location loc = event.getTo();

        activatedPortals.forEach(portal -> {
            if (loc.equals(portal.getLocation()))
                portal.teleport(event.getPlayer());
        });
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        InGamePlayer iGP = InGamePlayer.of(event.getPlayer());

        if (iGP == null)
            return;

        if (!event.hasBlock())
            return;

        Block block = event.getClickedBlock();

        Arrays.stream(GameLibrary.GAME_TYPE.getTeams())
                .forEach(team ->
                        Arrays.stream(team.getPortals())
                                .forEach(portal -> {
                                    if (portal.getLocation().equals(block.getLocation())) {
                                        portal.executeGui(iGP);
                                    }
                                }));
    }
}
