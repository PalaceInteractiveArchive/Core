package network.palace.core.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;

@CommandMeta(description = "Hide username tags", rank = Rank.MOD)
public class ToggleTagCommand extends CoreCommand {

    /**
     * Instantiates a new Token command.
     */
    public ToggleTagCommand() {
        super("toggletag");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.getScoreboard().toggleTags();
        if (player.getScoreboard().getTagsVisible()) {
            player.sendMessage(ChatColor.GREEN + "Tags are now visible!");
        } else {
            player.sendMessage(ChatColor.RED + "Tags are no longer visible!");
        }
    }
}
