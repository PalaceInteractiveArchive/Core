package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.honor.TopHonorReport;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;
import java.util.List;

@CommandMeta(description = "View honor leaderboard")
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
            List<TopHonorReport> list = Core.getMongoHandler().getTopHonor(limit);
            list.sort((o1, o2) -> o2.getHonor() - o1.getHonor());

            StringBuilder msg = new StringBuilder(ChatColor.GOLD + "Honor Leaderboard: Top " + limit + " Players\n");

            for (int i = 0; i < list.size(); i++) {
                TopHonorReport report = list.get(i);
                Rank rank = Core.getMongoHandler().getRank(report.getName());
                msg.append(report.getPlace()).append(". ").append(rank.getTagColor()).append(report.getName()).append(": ")
                        .append(ChatColor.GOLD).append(format(report.getHonor())).append(ChatColor.GRAY).append(" (Level")
                        .append(" ").append(Core.getHonorManager().getLevel(report.getHonor()).getLevel()).append(")");
                if (i <= list.size() - 1) {
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

