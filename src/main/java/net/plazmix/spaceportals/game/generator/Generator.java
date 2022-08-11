package net.plazmix.spaceportals.game.generator;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.spaceportals.game.generator.material.FakeCrystal;
import net.plazmix.spaceportals.game.team.GameTeam;
import org.bukkit.Location;

@Getter
public class Generator {

    private final GameTeam gameTeam;

    public Generator(@NonNull GameTeam gameTeam, @NonNull Location location) {
        this.gameTeam = gameTeam;
        FakeCrystal.create(location,
                () -> gameTeam.getGenerators().remove(this)
        );
    }
}
