package network.palace.core.utils;

import network.palace.core.Core;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;

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
     */
    public static void displayError(Exception e) {
        String exceptionMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
        ArrayList<String> errorInfo = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            StackTraceElement element = e.getStackTrace()[i];
            errorInfo.add(element.getClassName() + "." + element.getMethodName() + "()" + ":" + element.getLineNumber());
        }
        errorInfo.add("\n");
        FormattedMessage message = new FormattedMessage(exceptionMessage).color(ChatColor.RED);
        message.multilineTooltip(ChatColor.RED + "Details", Arrays.toString(errorInfo.toArray()));
        Core.getPlayerManager().getOnlinePlayers().stream().filter(player -> player.getRank().getRankId() >= Rank.WIZARD.getRankId()).forEach(message::send);
    }
}
