package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Marc on 2/9/17.
 */
@CommandMeta(aliases = "ot", description = "View the amount of time the server has been online")
public class OnlineCommand extends CoreCommand {

    public OnlineCommand() {
        super("online");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        long startTime = Core.getInstance().getStartTime();
        Calendar c = new GregorianCalendar();
        c.setTime(new Date(startTime));
        String date = formatDateDiff(c, new GregorianCalendar());
        sender.sendMessage(ChatColor.GREEN + "This server " + ChatColor.AQUA + "(" + Core.getInstanceName() + ") " +
                ChatColor.GREEN + "has been online for " + date + ".");
    }

    private static String formatDateDiff(Calendar fromDate, Calendar toDate) {
        boolean future = false;
        if (toDate.equals(fromDate)) {
            return "Now";
        }
        if (toDate.after(fromDate)) {
            future = true;
        }
        StringBuilder sb = new StringBuilder();
        int[] types = {1, 2, 5, 11, 12, 13};

        String[] names = {"Years", "Years", "Months", "Months", "Days",
                "Days", "Hours", "Hours", "Minutes", "Minutes", "Seconds",
                "Seconds"};

        int accuracy = 0;
        for (int i = 0; i < types.length; i++) {
            if (accuracy > 2) {
                break;
            }
            int diff = dateDiff(types[i], fromDate, toDate, future);
            if (diff > 0) {
                accuracy++;
                sb.append(" ").append(diff).append(" ").append(names[(i * 2)]);
            }
        }
        if (sb.length() == 0) {
            return "Now";
        }
        return sb.toString().trim();
    }

    private static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();
        while ((future) && (!fromDate.after(toDate)) || (!future)
                && (!fromDate.before(toDate))) {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            diff++;
        }
        diff--;
        fromDate.setTimeInMillis(savedDate);
        return diff;
    }
}
