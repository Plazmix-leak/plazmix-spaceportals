package net.plazmix.spaceportals.mysql;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.user.GameUser;
import net.plazmix.spaceportals.utils.GameLibrary;

import java.util.Arrays;

public class GameMySQL extends GameMysqlDatabase {

    public GameMySQL() {
        super("SpacePortals", true);
    }

    @Override
    public void initialize() {
        Arrays.stream(GameLibrary.MySQLConstants.values())
                .forEach(gameLibrary ->
                        super.addColumn(
                                gameLibrary.getColumn(),
                                gameLibrary.getRowType(),
                                o -> o.getCache().getInt(gameLibrary.getColumn())
                        )
                );
    }

    @Override
    public void onJoinLoad(@NonNull GamePlugin gamePlugin, @NonNull GameUser gameUser) {
        loadPrimary(false, gameUser, gameUser.getCache()::set);
    }

    @Override
    public void onQuitSave(@NonNull GamePlugin plugin, @NonNull GameUser gameUser) {
        insert(false, gameUser);
    }
}
