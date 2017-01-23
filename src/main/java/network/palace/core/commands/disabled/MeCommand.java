package network.palace.core.commands.disabled;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.npc.NPCFactory;
import network.palace.core.npc.mob.MobNPCChicken;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;

import java.util.HashSet;

// TODO REVERT BACK LOL
@CommandMeta(description = "Disable me command")
public class MeCommand extends CoreCommand {

    private NPCFactory npcFactory = new NPCFactory();

    public MeCommand() {
        super("me");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Point point = Point.of(player.getLocation());
        MobNPCChicken chicken = npcFactory.createNPC(MobNPCChicken.class, point, player.getLocation().getWorld(), new HashSet<>(), "TEST");
        chicken.spawn();
        player.sendMessage("Spawned chicken");
    }
}
