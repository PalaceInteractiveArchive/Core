package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.honor.TopHonorReport;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

@CommandMeta(description = "View honor leaderboard")
@CommandPermission(rank = Rank.SETTLER)
public class TopHonorCommand extends CoreCommand {
    private final DecimalFormat format = new DecimalFormat("#,###");

    public TopHonorCommand() {
        super("tophonor");
    }

    @Override
    protected void handleCommandUnspecific(final CommandSender sender, final String[] args) throws CommandException {
        sender.sendMessage(ChatColor.GREEN + "Gathering leaderboard data...");
        Core.runTaskAsynchronously(() -> {
            int limit = 10;
            if (args.length > 0 && MiscUtil.checkIfInt(args[0]) && (limit = Integer.parseInt(args[0])) > 10) {
                limit = 10;
            }
            HashMap<Integer, TopHonorReport> map = Core.getMongoHandler().getTopHonor(limit);
            StringBuilder msg = new StringBuilder(ChatColor.GOLD + "Honor Leaderboard: Top " + limit + " Players\n");
            ArrayList<TopHonorReport> list = new ArrayList<>(map.values());
            for (int i = 0; i < map.size(); i++) {
                TopHonorReport report = list.get(i);
                msg.append(report.getPlace()).append(". ").append(report.getName()).append(": ").append(format(report.getHonor()));
                if (i <= map.size() - 1) {
                    msg.append("\n").append(ChatColor.GOLD);
                }
            }
            sender.sendMessage(msg.toString());
        });
    }

    private String format(int i) {
        return this.format.format(i);
    }
}

