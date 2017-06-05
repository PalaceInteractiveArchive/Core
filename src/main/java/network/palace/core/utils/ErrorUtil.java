package network.palace.core.utils;

import network.palace.core.Core;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.Rank;
import network.palace.core.plugin.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Innectic
 * @since 6/1/2017
 */
public class ErrorUtil {
    /**
     * Display a runtime error to all Wizard+ on that server
     *
     * @param e the exception thrown
     * @param plugin the plugin the error occurred on
     */
    public static void displayError(Exception e, JavaPlugin plugin) {
        if (shouldStop()) return;
        String exceptionMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
        ArrayList<String> errorInfo = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            StackTraceElement element = e.getStackTrace()[i];
            errorInfo.add(element.getClassName() + "." + element.getMethodName() + "()" + ":" + element.getLineNumber());
        }
        errorInfo.add("\n");
        FormattedMessage message = new FormattedMessage(exceptionMessage).color(ChatColor.RED);
        message.multilineTooltip(ChatColor.RED + "Details (" + plugin.getName() + ")", Arrays.toString(errorInfo.toArray()));
        Core.getPlayerManager().getOnlinePlayers().stream().filter(player -> player.getRank().getRankId() >= Rank.WIZARD.getRankId()).forEach(message::send);
        if (plugin instanceof Plugin) ((Plugin) plugin).getRollbarHandler().error(e);
        else Core.getInstance().getRollbarHandler().error(e);
    }

    public static void displayError(Exception e) {
        if (shouldStop()) return;
        String exceptionMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
        ArrayList<String> errorInfo = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            StackTraceElement element = e.getStackTrace()[i];
            errorInfo.add(element.getClassName() + "." + element.getMethodName() + "()" + ":" + element.getLineNumber());
        }
        errorInfo.add("\n");
        FormattedMessage message = new FormattedMessage(exceptionMessage).color(ChatColor.RED);
        message.multilineTooltip(ChatColor.RED + "Details (Core)", Arrays.toString(errorInfo.toArray()));
        Core.getPlayerManager().getOnlinePlayers().stream().filter(player -> player.getRank().getRankId() >= Rank.WIZARD.getRankId()).forEach(message::send);
        Core.getInstance().getRollbarHandler().error(e);
    }

    public static void displayError(String error) {
        if (shouldStop()) return;
        FormattedMessage message = new FormattedMessage(error).color(ChatColor.RED);
        message.multilineTooltip(ChatColor.RED + "Details (Core)");
        Core.getPlayerManager().getOnlinePlayers().stream().filter(player -> player.getRank().getRankId() >= Rank.WIZARD.getRankId()).forEach(message::send);
        Core.getInstance().getRollbarHandler().error(error);
    }

    public static void displayError(String error, JavaPlugin plugin) {
        if (shouldStop()) return;
        FormattedMessage message = new FormattedMessage(error).color(ChatColor.RED);
        message.multilineTooltip(ChatColor.RED + "Details (" + plugin.getName() + ")");
        Core.getPlayerManager().getOnlinePlayers().stream().filter(player -> player.getRank().getRankId() >= Rank.WIZARD.getRankId()).forEach(message::send);
        Core.getInstance().getRollbarHandler().error(error);
    }

    private static boolean shouldStop() {
        return Core.isDashboardAndSqlDisabled();
    }
}
