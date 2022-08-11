package net.plazmix.spaceportals.state;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.state.GameState;
import net.plazmix.spaceportals.game.player.InGamePlayer;
import net.plazmix.spaceportals.game.team.GameTeam;
import net.plazmix.spaceportals.utils.GameLibrary;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class InGameState extends GameState {

    public InGameState(@NonNull GamePlugin plugin) {
        super(plugin, "Идёт Игра", false);
    }

    @Override
    protected void onStart() {
        GameLibrary.GAME.start();
    }

    @Override
    protected void onShutdown() {

    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) && !(event.getDamager() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        Player target = (Player) event.getEntity();

        GameTeam gameTeam = GameLibrary.GAME.getTeamByPlayer(damager);

        if (gameTeam.getPlayers().stream().anyMatch(inGamePlayer -> inGamePlayer.getPlayer().equals(target)))
            event.setCancelled(true);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        InGamePlayer.of(event.getPlayer()).onSpawn();
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (killer == null)
            return;

        event.setKeepInventory(true);

        GameTeam killerTeam = GameLibrary.GAME.getTeamByPlayer(killer);

        killerTeam.addABodies(1);
        killerTeam.getPlayers()
                .stream()
                .filter(inGamePlayer -> inGamePlayer.getPlayer().equals(killer))
                .forEach(InGamePlayer::onKill);

        InGamePlayer.of(player).onDeath();

        event.getDrops()
                .stream()
                .filter(itemStack -> itemStack.getType() == Material.DIAMOND || itemStack.getType().name().contains("DIAMOND_"))
                .forEach(itemStack -> {
                    if (itemStack.getType() == Material.DIAMOND) {
                        player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                        return;
                    }

                    itemStack.setAmount(0);
                });
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        event.setFoodLevel(20);
    }
}
