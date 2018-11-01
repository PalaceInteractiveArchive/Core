package network.palace.core.holograms;

import network.palace.core.npc.AbstractMob;
import network.palace.core.npc.mob.MobArmorStand;
import network.palace.core.pathfinding.Point;

import java.util.Optional;

/**
 * @author Clutch
 * @since 4/29/2017
 */
public class Hologram {
    private static final double adjustHeight = 1.6888;
    private Optional<MobArmorStand> armorStand;
    private String text = "";

    public Hologram(Point point, String text) {
        point.add(0.0, -adjustHeight, 0.0);
        MobArmorStand hologram = new MobArmorStand(point, null, text);
        hologram.setVisible(false);
        hologram.setArms(false);
        hologram.setBasePlate(false);
        hologram.setGravity(false);
        hologram.setCustomNameVisible(true);
        this.armorStand = Optional.of(hologram);
    }

    public void create() {
        this.armorStand.ifPresent(AbstractMob::spawn);
        setText(text);
    }

    public void destroy() {
        this.armorStand.ifPresent(AbstractMob::despawn);
    }

    public void move(Point point) {
        this.armorStand.ifPresent(mobArmorStand -> {
            if (mobArmorStand.isSpawned()) mobArmorStand.move(point);
        });
    }

    public void setText(String text) {
        this.text = text;
        this.armorStand.ifPresent(mobArmorStand -> {
            mobArmorStand.setCustomName(text);
            if (mobArmorStand.isSpawned()) mobArmorStand.update();
        });
    }

    public String getText() {
        return text;
    }
}
