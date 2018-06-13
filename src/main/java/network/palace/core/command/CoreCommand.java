package network.palace.core.command;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.plugin.Plugin;
import network.palace.core.utils.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CoreCommand is the superclass for any commands that are created to hook into the plugin system in core.
 * <p>
 * All commands are handled through three main methods, splitting the different sender types into different calls, and simplifying the method to just args and a sender.
 * <p>
 * Commands must throw exceptions in the event of a failure, and you can handle these exceptions using {@link #handleCommandException(CommandException, String[], org.bukkit.command.CommandSender)}.
 * <p>
 * You can also use our sub command system which supports tab completion and recursive sub-commands by using the constructor {@link #CoreCommand(String, CoreCommand...)}.
 * <p>
 * You can override tab-completion using {@link #handleTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, String, String[])}
 * <p>
 * If you require usage of a sub-command, please override {@link #isUsingSubCommandsOnly()} and have it return true.
 *
 * @see org.bukkit.command.CommandExecutor
 * @see org.bukkit.command.TabCompleter
 */
public abstract class CoreCommand implements CommandExecutor, TabCompleter {

    /**
     * Holds a list of the sub-commands bound to their names used for quick access.
     */
    private final Map<String, CoreCommand> subCommands = new HashMap<>();

    /**
     * Holds the name of this command.
     */
    @Getter private final String name;

    @Getter @Setter private CoreCommand superCommand = null;

    @Getter @Setter private Rank rank = Rank.SETTLER;

    @Getter @Setter private String description = "";

    /**
     * Main constructor without sub-commands.
     *
     * @param name The name of the command.
     */
    protected CoreCommand(String name) {
        this.name = name;
    }

    /**
     * Main constructor with sub-commands.
     *
     * @param name        The name of the command.
     * @param subCommands The sub-commands you wish to register.
     */
    protected CoreCommand(final String name, CoreCommand... subCommands) {
        this.name = name;
        registerSubCommand(subCommands);
    }

    /**
     * Registers sub commands with the command and re-creates the help command.
     *
     * @param subCommands The sub-commands you wish to register.
     */
    public final void registerSubCommand(CoreCommand... subCommands) {
        // Toss all the sub commands in the map
        for (CoreCommand subCommand : subCommands) {
            if (subCommand.getSuperCommand() != null)
                throw new IllegalArgumentException("The command you attempted to register already has a supercommand.");
            CommandMeta annotation = subCommand.getClass().getAnnotation(CommandMeta.class); // Get the commandMeta
            if (annotation != null) {
                subCommand.setDescription(annotation.description());
            }
            subCommand.setSuperCommand(this);
            this.subCommands.put(subCommand.getName(), subCommand);
        }
        // Add a provided help command
        regenerateHelpCommand();
    }

    public final void unregisterSubCommand(CoreCommand... subCommands) {
        for (CoreCommand subCommand : subCommands) {
            //if (!subCommand.getSuperCommand().equals(this)) continue;
            this.subCommands.remove(subCommand.getName());
            subCommand.setSuperCommand(null);
        }
        regenerateHelpCommand();
    }

    public final ImmutableList<CoreCommand> getSubCommands() {
        return ImmutableList.copyOf(this.subCommands.values());
    }

    public void regenerateHelpCommand() {
        if (subCommands.containsKey("help")) return;
        final CoreCommand superHelpCommand = this;
        CoreCommand help = new CoreCommand("help") {
            @Override
            public void handleCommandUnspecific(CommandSender sender, String[] args) {
                String name = CoreCommand.this.getFormattedName();
                StringBuilder msg = new StringBuilder(ChatColor.GREEN + MiscUtil.capitalizeFirstLetter(name) + " Commands:");
                if (!isUsingSubCommandsOnly()) {
                    msg.append("\n").append(ChatColor.GREEN).append("/").append(name.toLowerCase()).append(" ").append(ChatColor.AQUA).append("- ").append(CoreCommand.this.getDescription());
                }
                for (Map.Entry<String, CoreCommand> entry : subCommands.entrySet()) {
                    msg.append("\n").append(ChatColor.GREEN).append("/").append(entry.getValue().getFormattedName()).append(" ").append(ChatColor.AQUA).append("- ").append(entry.getValue().getDescription());
                }
                sender.sendMessage(msg.toString());
//                StringBuilder builder = new StringBuilder();
//                for (Map.Entry<String, CoreCommand> stringModuleCommandEntry : CoreCommand.this.subCommands.entrySet()) {
//                    builder.append(stringModuleCommandEntry.getKey()).append("|");
//                }
//                String s = msg;
                // Looks like this /name - [subcommand1|subcommand2|]
//                sender.sendMessage(ChatColor.GRAY + "/" + ChatColor.DARK_AQUA + superHelpCommand.getFormattedName() + ChatColor.GOLD + " - [" + s.substring(0, s.length() - 1) + "]");
            }
        };
        help.setSuperCommand(this);
        help.setDescription("Open the help menu");
        this.subCommands.put("help", help);
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // Handling commands can be done by the logic below, and all errors should be thrown using an exception.
        // If you wish to override the behavior of displaying that error to the player, it is discouraged to do that in
        // your command logic, and you are encouraged to use the provided method handleCommandException.
        try {
            // STEP ONE: Handle sub-commands
            CoreCommand subCommand = null;
            if (sender instanceof Player) {
                CPlayer player = Core.getPlayerManager().getPlayer(((Player) sender).getUniqueId());
                Rank requiredRank = Rank.SETTLER;
                if (getClass().isAnnotationPresent(CommandMeta.class)) {
                    CommandMeta annotation = getClass().getAnnotation(CommandMeta.class);
                    requiredRank = annotation.rank();
                }
                if (getClass().isAnnotationPresent(CommandPermission.class)) {
                    CommandPermission annotation = getClass().getAnnotation(CommandPermission.class);
                    requiredRank = annotation.rank();
                }
                if (player.getRank().getRankId() < requiredRank.getRankId())
                    throw new PermissionException();
            }
            // Check if we HAVE to use sub-commands (a behavior this class provides)
            if (isUsingSubCommandsOnly()) {
                // Check if there are not enough args for there to be a sub command
                if (args.length < 1) {
                    args = new String[1];
                    args[0] = "help";
                }
                // Also check if the sub command is valid by assigning and checking the value of the resolved sub command from the first argument.
                if ((subCommand = getSubCommandFor(args[0])) == null)
                    throw new ArgumentRequirementException("command.error.arguments.invalid");
            }
            if (subCommand == null && args.length > 0)
                subCommand = getSubCommandFor(args[0]); // If we're not requiring sub-commands but we can have them, let's try that
            // By now we have validated that the sub command can be executed if it MUST, now lets see if we can execute it
            // In this case, if we must execute the sub command, this check will always past. In cases where it's an option, this check will also pass.
            // That way, we can use this feature of sub commands without actually requiring it.
            if (subCommand != null) {
                String[] choppedArgs = args.length < 2 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
                preSubCommandDispatch(sender, choppedArgs, subCommand); // Notify the subclass that we are using a sub-command in case any staging needs to take place.
                subCommand.onCommand(sender, command, s, choppedArgs);
                try {
                    handlePostSubCommand(sender, args);
                } catch (EmptyHandlerException ignored) {
                }
                return true;
            }
            // Now that we've made it past the sub commands and permissions, STEP TWO: actually handle the command and it's args.
            try {
                if (sender instanceof Player) {
                    CPlayer player = Core.getPlayerManager().getPlayer((Player) sender);
                    if (player != null) handleCommand(player, args);
                } else if (sender instanceof ConsoleCommandSender) handleCommand((ConsoleCommandSender) sender, args);
                else if (sender instanceof BlockCommandSender) handleCommand((BlockCommandSender) sender, args);
            } catch (EmptyHandlerException e) {
                handleCommandUnspecific(sender, args); // We don't catch this because we would catch it and then immediately re-throw it so it could be caught by the below catch block (which handles the exception).
            }
        } // STEP THREE: Check for any command exceptions (intended) and any exceptions thrown in general and dispatch a call for an unhandled error to the handler.
        catch (CommandException ex) {
            handleCommandException(ex, args, sender);
        } catch (Exception e) {
            handleCommandException(new UnhandledCommandExceptionException(e), args, sender);
        }
        // STEP FOUR: Tell Bukkit we're done!
        return true;
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Security for tab complete
        if (sender instanceof Player) {
            CPlayer player = Core.getPlayerManager().getPlayer(((Player) sender).getUniqueId());
            Rank requiredRank = Rank.SETTLER;
            if (getClass().isAnnotationPresent(CommandMeta.class)) {
                CommandMeta annotation = getClass().getAnnotation(CommandMeta.class);
                requiredRank = annotation.rank();
            }
            if (getClass().isAnnotationPresent(CommandPermission.class)) {
                CommandPermission annotation = getClass().getAnnotation(CommandPermission.class);
                requiredRank = annotation.rank();
            }
            if (player.getRank().getRankId() < requiredRank.getRankId())
                return Collections.emptyList();
        }
        // Step one, check if we have to go a level deeper in the sub command system:
        if (args.length > 1) {
            //If so, check if there's an actual match for the sub-command to delegate to.
            CoreCommand possibleHigherLevelSubCommand;
            if ((possibleHigherLevelSubCommand = getSubCommandFor(args[0])) != null) {
                return possibleHigherLevelSubCommand.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
            }
            // NOW THINK. If there's not one, you'll reach this line, and exit this block of the if statement. The next statement is an else if, so it will skip that
            // And go to the very bottom "handleTabComplete."
        } else if (args.length == 1) { //So if we have exactly one argument, let's try and complete the sub-command for that argument
            // Grab some sub commands from the method we defined for this purpose
            List<CoreCommand> subCommandsForPartial = getSubCommandsForPartial(args[0]);
            // And if we found some
            if (subCommandsForPartial.size() != 0) {
                // Get the command names
                List<String> strings = subCommandsForPartial.stream().map(CoreCommand::getName).collect(Collectors.toList());
                List<String> strings1 = handleTabComplete(sender, command, alias, args);
                strings.addAll(strings1);
                // And return them
                return strings;
            }
            // Otherwise, head to the delegated call at the bottom.
        }
        return handleTabComplete(sender, command, alias, args);
    }

    /**
     * This method <b>should</b> be overridden by any sub-classes as the functionality it provides is limited.
     * <p>
     * The goal of this method should always be conveying an error message to a user in a friendly manner. The {@link CommandException} can be extended by your {@link Plugin} to provide extended functionality.
     * <p>
     * The {@code args} are the same args that would be passed to your handlers. Meaning, if this is a sub-command they will be cut to fit that sub-command, and if this is a root level command they will be all of the arguments.
     *
     * @param ex     The exception used to hold the error message and any other details about the failure. If there was an exception during the handling of the command this will be an {@link UnhandledCommandExceptionException}.
     * @param args   The arguments passed to the command.
     * @param sender The sender of the command, cannot be directly cast to {@link CPlayer}.
     */
    protected void handleCommandException(CommandException ex, String[] args, CommandSender sender) {
        //Get the friendly message if supported
        if (ex instanceof FriendlyException) {
            sender.sendMessage(Core.getLanguageFormatter().getFormat(sender, ((FriendlyException) ex).getFriendlyMessage()));
        } else {
            sender.sendMessage(ChatColor.RED + ex.getClass().getSimpleName() + ": " + ex.getMessage() + "!");
        }
        if (ex instanceof UnhandledCommandExceptionException) {
            ((UnhandledCommandExceptionException) ex).getCausingException().printStackTrace();
        }
    }

    protected void preSubCommandDispatch(CommandSender sender, String[] args, CoreCommand subCommand) {
    }

    public final CoreCommand getSubCommandFor(String s) {
        // If we have an exact match, case and all, don't waste the CPU cycles on the lower for loop.
        if (subCommands.containsKey(s)) return subCommands.get(s);
        // Otherwise, loop through the sub-commands and do a case insensitive check.
        for (String s1 : subCommands.keySet()) {
            if (s1.equalsIgnoreCase(s)) return subCommands.get(s1);
        }
        // And we didn't find anything, so let's return nothing.
        return null;
    }

    public final List<CoreCommand> getSubCommandsForPartial(String s) {
        List<CoreCommand> commands = new ArrayList<>(); // Create a place to hold our possible commands
        CoreCommand subCommand;
        if ((subCommand = getSubCommandFor(s)) != null) { // Check if we can get an exact sub-command
            commands.add(subCommand);
            return commands; // Exact sub-command is all we need.
        }
        String s2 = s.toUpperCase(); // Get the case-insensitive comparator.
        // We found one that starts with the argument.
        commands.addAll(subCommands.keySet().stream().filter(s1 -> s1.toUpperCase().startsWith(s2)).map(subCommands::get).collect(Collectors.toList()));
        return commands;
    }

    // Default behavior is to do nothing, these methods can be overridden by the sub-class.
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        throw new EmptyHandlerException();
    }

    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {
        throw new EmptyHandlerException();
    }

    protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {
        throw new EmptyHandlerException();
    }

    // Handles for all types in the event that no specific handler is overridden above.
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        throw new EmptyHandlerException();
    }

    protected void handlePostSubCommand(CommandSender sender, String[] args) throws CommandException {
        throw new EmptyHandlerException();
    }

    // Default behavior if we delegate the call to the sub-class
    protected List<String> handleTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (isUsingSubCommandsOnly() || subCommands.size() > 0) return Collections.emptyList();
        List<String> ss = new ArrayList<>(); // Create a list to put possible names
        String arg = args.length > 0 ? args[args.length - 1].toLowerCase() : ""; // Get the last argument
        for (Player player : Bukkit.getOnlinePlayers()) { // Loop through all the players
            String name1 = player.getName(); // Get this players name (since we reference it twice)
            if (name1.toLowerCase().startsWith(arg))
                ss.add(name1); // And if it starts with the argument we add it to this list
        }
        return ss; //Return what we found.
    }

    protected boolean isUsingSubCommandsOnly() {
        return false;
    }

    protected String getFormattedName() {
        return superCommand == null ? name : superCommand.getFormattedName() + " " + name;
    }

    @Override
    public String toString() {
        return "Command > " + getFormattedName();
    }
}
