package net.plazmix.spaceportals;

import lombok.NonNull;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.installer.GameInstaller;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.spaceportals.game.generator.GeneratorListener;
import net.plazmix.spaceportals.state.EndingState;
import net.plazmix.spaceportals.state.InGameState;
import net.plazmix.spaceportals.state.WaitingState;
import net.plazmix.spaceportals.listener.RegionListener;
import net.plazmix.spaceportals.mysql.GameMySQL;
import net.plazmix.spaceportals.utils.GameLibrary;
import net.plazmix.spaceportals.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/*  Leaked by https://t.me/leak_mine
    - Все слитые материалы вы используете на свой страх и риск.

    - Мы настоятельно рекомендуем проверять код плагинов на хаки!
    - Список софта для декопиляции плагинов:
    1. Luyten (последнюю версию можно скачать можно тут https://github.com/deathmarine/Luyten/releases);
    2. Bytecode-Viewer (последнюю версию можно скачать можно тут https://github.com/Konloch/bytecode-viewer/releases);
    3. Онлайн декомпиляторы https://jdec.app или http://www.javadecompilers.com/

    - Предложить свой слив вы можете по ссылке @leakmine_send_bot или https://t.me/leakmine_send_bot
*/

public class SpacePortals extends GamePlugin {
    @Override
    public GameInstallerTask getInstallerTask() {
        return new SpacePortalsInstaller(this);
    }

    @Override
    protected void handleEnable() {
        service.setGameName("SpacePortals");
        service.setServerMode("SpacePortals");

        if (!getFile().exists())
            saveDefaultConfig();

        GameLibrary.createGame(GameLibrary.GameType.valueOf(getConfig().getString("mode").toLowerCase()));

        service.setMaxPlayers(GameLibrary.GAME_TYPE.getPlayers());

        service.addGameDatabase(new GameMySQL());
        service.registerState(
                new WaitingState(this, new Location(Bukkit.getWorld("lobby"), 0, 100, 0))
        );

        service.registerState(
                new InGameState(this)
        );

        service.registerState(
                new EndingState(this)
        );
        Bukkit.getPluginManager().registerEvents(new RegionListener(), this);
        Bukkit.getPluginManager().registerEvents(new GeneratorListener(), this);

        GameInstaller.create().executeInstall(getInstallerTask());

        GameLibrary.initBars();
        GameLibrary.initItems();
        GameLibrary.initGuis();

        GameSchedulers.runTimer(0, 10,
                () -> getServer().getWorlds().forEach(world -> {
                    world.setStorm(false);
                    world.setThundering(false);
                    world.setWeatherDuration(0);
                    world.setTime(1200);
                }));

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    protected void handleDisable() {
        broadcastMessage(StringUtil.format("&cАрена %s перезагружается!", PlazmixCoreApi.getCurrentServerName()));
    }

    private static class SpacePortalsInstaller extends GameInstallerTask {

        public SpacePortalsInstaller(@NonNull GamePlugin plugin) {
            super(plugin);
        }

        @Override
        protected void handleExecute(@NonNull Actions actions, @NonNull Settings settings) {
            settings.setCenter(plugin.getService().getMapWorld().getSpawnLocation());
            settings.setRadius(100);
        }
    }
}
