package network.palace.core.events;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.Bukkit;


public class CommandListener implements Listener {
    JavaPlugin plugin;

    public CommandListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void preProcessCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().startsWith("/minecraft:kill")) {
            if (!event.getMessage().matches(".*r=[1-9].*")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cYou cannot kill entities without a radius!");
            }
        }
    }

    @EventHandler
    public void redstoneChanges(BlockRedstoneEvent e){
        Block block = e.getBlock();
        if(e.getOldCurrent() > 0 || e.getNewCurrent() == 0) return;
        if (!(block.getState() instanceof CommandBlock)) return;

        CommandBlock cb = (CommandBlock)block.getState();
        try {
            String[] args = cb.getCommand().split(" ");
            if (args[0].toLowerCase().equals("minecraft:kill") && !cb.getCommand().matches(".*r=[1-9].*")) {
                setCommandBlock(cb, "", String.join(" ", args));
                return;
            }

            if (!args[0].toLowerCase().equals("kill")) return;

            String cmd = cb.getCommand().matches(".*r=[1-9].*") ? "minecraft:" + cb.getCommand() : "";
            setCommandBlock(cb, cmd, String.join(" ", args));
        } catch (Exception ignored) { }
    }

    public void setCommandBlock(CommandBlock cb, String command, String oldCommand) {
        cb.setCommand(command);
        cb.update();

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            cb.setCommand(String.join(" ", oldCommand));
            cb.update();
        }, 5L);
    }}

