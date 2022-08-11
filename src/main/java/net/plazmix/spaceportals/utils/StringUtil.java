package net.plazmix.spaceportals.utils;

import lombok.experimental.UtilityClass;
import net.plazmix.PlazmixApi;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.utility.player.LocalizationPlayer;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@UtilityClass
public class StringUtil {

    public String format(String input, Object... value) {
        return value == null ? color(input) : String.format(color(input), value);
    }

    public String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String fixDouble(int i, double d) {
        return String.format("%." + i + "f", d);
    }

    public void sendMessage(Player player, String msg) {
        PlazmixUser.of(player).localization().sendMessage(msg);
    }

    public void sendMessage(Player player, LocalizationPlayer.MessageHandler<Object, LocalizationPlayer> messageHandler) {
        PlazmixUser.of(player).localization().sendMessage(messageHandler);
    }
}
