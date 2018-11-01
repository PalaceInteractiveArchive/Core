package network.palace.core.holograms;

import network.palace.core.npc.mob.MobArmorStand;
import network.palace.core.pathfinding.Point;

/**
 * @author Clutch
 * @since 4/29/2017
 */
public class Hologram {
    private static final double adjustHeight = 1.6888;
    private MobArmorStand armorStand;
    private String text = "";

    public Hologram(Point point, String text) {
        adjust(point);
        armorStand = new MobArmorStand(point, null, text);
        armorStand.setVisible(false);
        armorStand.setArms(false);
        armorStand.setBasePlate(false);
        armorStand.setGravity(false);
        armorStand.setCustomNameVisible(true);
    }

    public void create() {
        armorStand.spawn();
    }

    public void destroy() {
        armorStand.despawn();
    }

    public void move(Point point) {
        adjust(point);
        if (armorStand.isSpawned())
            armorStand.move(point);
    }

    public void setText(String text) {
        this.text = text;
        armorStand.setCustomName(text);
        if (armorStand.isSpawned())
            armorStand.update();
    }

    public String getText() {
        return text;
    }

    private void adjust(Point p) {
        p.add(0.0, -adjustHeight, 0.0);
    }
}
