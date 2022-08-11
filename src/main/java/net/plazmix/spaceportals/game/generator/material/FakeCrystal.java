package net.plazmix.spaceportals.game.generator.material;

import lombok.NonNull;
import net.plazmix.protocollib.entity.FakeBaseEntity;
import net.plazmix.spaceportals.utils.GameLibrary;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class FakeCrystal extends FakeBaseEntity {

    public FakeCrystal(@NonNull Location location) {
        super(EntityType.ENDER_CRYSTAL, location);
    }

    public static void create(Location loc, Runnable action) {
        new FakeCrystal(loc).set(action);
    }

    protected void set(Runnable action) {
        this.setClickAction(player -> {
            this.remove();

            action.run();
            GameLibrary.addItems(player, GameLibrary.GAME_CACHE.get(GameLibrary.GENERATOR));
        });

        this.spawn();
    }
}
