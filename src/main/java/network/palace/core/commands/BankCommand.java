package network.palace.core.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.economy.BankMenu;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;

@CommandMeta(description = "Allows you to view your bank")
public class BankCommand extends CoreCommand {

    public BankCommand() {
        super("bank");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        BankMenu inv = new BankMenu(player);
        inv.openMenu();
    }

    @Override
    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {
        commandSender.sendMessage(ChatColor.RED + "This command can only be used by players");
    }

    @Override
    protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {
        commandSender.sendMessage(ChatColor.RED + "This command can only be used by players");
    }
}
