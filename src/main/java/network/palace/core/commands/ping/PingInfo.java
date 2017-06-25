package network.palace.core.commands.ping;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;

@CommandMeta(description = "Get info about your ping")
public class PingInfo extends CoreCommand {

    public PingInfo() {
        super("info");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length == 0 || args[0].equals("1") || !this.isInt(args[0])) {
            player.sendMessage(ChatColor.YELLOW + "Ping is the time in milliseconds that it takes for information" +
                    " to get from our servers to you and back. The lower your ping, the better experience you will have.");
            player.sendMessage(ChatColor.GREEN + "Type " + ChatColor.YELLOW + "/ping info [page] " + ChatColor.GREEN + "for more info, up to page 4");
            return;
        }
        if (Integer.parseInt(args[0]) > 4) {
            player.sendMessage(ChatColor.GREEN + "That's it, there's only 4 pages!");
            return;
        }
        String msg = "";
        switch (args[0]) {
            case "2": {
                msg = "A higher ping is usually related to your geographical distance from our servers." +
                        " The further away from our servers you are, the higher your ping is." +
                        " Since our servers are hosted in the United States, international players tend to have higher pings.";
                break;
            }
            case "3": {
                msg = "Ping is measured in milliseconds, 1000ms equal 1 second." +
                        " If your ping is 50ms, it takes one twentieth of a second for data to travel from the servers to you and back." +
                        " Good connections have a steady ping, meaning your ping is almost always the same." +
                        " If your ping fluctuates, it's generally a problem with your ISP (Internet Service Provider).";
                break;
            }
            case "4": {
                msg = "Our servers are connected to 1 Gbps (Gigabit per second) uplinks and use industrial-grade networking connections." +
                        " Unless reported on Twitter by @PalaceDev, any connection issues are likely related to your personal network.";
            }
        }
        if (msg.isEmpty()) return;
        player.sendMessage(ChatColor.YELLOW + msg);
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

