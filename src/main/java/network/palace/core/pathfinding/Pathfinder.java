package network.palace.core.pathfinding;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.material.Door;
import org.bukkit.material.Gate;

import java.util.*;

public class Pathfinder {

    @Getter private final Map<Double, PathfindingTile> tiles = new HashMap<>();

    private Point endPos;

    private PathfindingTile start;
    private PathfindingTile end;

    public Pathfinder(Point startPos, Point endPos) {
        this.endPos = endPos;
        this.start = tileFrom(startPos);
        this.end = tileFrom(endPos);
    }

    public List<PathfindingTile> solvePath(int range) {
        tiles.clear();
        Set<PathfindingTile> closedSet = new LinkedHashSet<>();
        Set<PathfindingTile> openSet = new LinkedHashSet<>();
        closedSet.add(start);
        PathfindingTile current = start;
        while (!current.getPoint().equals(endPos)) {
            List<PathfindingTile> tilesAdjacent = getTilesAdjacent(current);
            tilesAdjacent.removeAll(closedSet);
            openSet.addAll(tilesAdjacent);
            if (tilesAdjacent.size() == 0 && closedSet.size() == 0) return null;
            PathfindingTile chosen = null;
            for (PathfindingTile pathTile : openSet) {
                if (chosen == null) {
                    chosen = pathTile;
                    continue;
                }
                if (pathTile.getFScore() < chosen.getFScore()) chosen = pathTile;
            }
            if (chosen == null) return null;
            current = chosen;
            openSet.remove(current);
            closedSet.add(current);
            if (range != -1 && closedSet.size() == range) break;
        }
        List<PathfindingTile> pathTiles = new ArrayList<>(closedSet);
        Collections.reverse(pathTiles);
        return pathTiles;
    }

    private List<PathfindingTile> getTilesAdjacent(PathfindingTile tile) {
        List<PathfindingTile> pathTiles = new ArrayList<>();
        Point center = tile.getPoint();
        double centerX = center.getX(), centerY = center.getY(), centerZ = center.getZ();
        for (int x = -1; x < 1; x++) {
            for (int y = -1; y < 1; y++) {
                for (int z = -1; z < 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    Point current = Point.of(centerX + x, centerY + y, centerZ + z, endPos.getWorld());
                    if (!canWalk(current)) continue;
                    PathfindingTile pathTile = tileFrom(current, tile);
                    pathTile.updateScores(start, end);
                    pathTiles.add(pathTile);
                }
            }
        }
        return pathTiles;
    }

    private boolean canWalk(Point current) {
        Block walkingOn = current.getLocation(endPos.getWorld()).getBlock();
        if (!canWalkOn(walkingOn)) return false;
        Block walkingThrough1 = walkingOn.getRelative(0, 1, 0), walkingThrough2 = walkingThrough1.getRelative(0, 1, 0);
        return canWalkThrough(walkingThrough1) && canWalkThrough(walkingThrough2);
    }

    private static boolean canWalkThrough(Block b) {
        switch (b.getType()) {
            case AIR:
            case LAVA:
            case WATER:
            case NETHER_PORTAL:
                return true;
            case IRON_DOOR:
            case DARK_OAK_DOOR:
            case ACACIA_DOOR:
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case OAK_DOOR:
            case SPRUCE_DOOR:
                Door door = (Door) b.getState();
                return door.isOpen();
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case OAK_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
                Gate gate = (Gate) b.getState();
                return gate.isOpen();
            default:
                return false;
        }
    }

    private boolean canWalkOn(Block b) {
        if (canWalkThrough(b)) return false;

        switch (b.getType()) {
            case LADDER:
            case WHEAT:
            case TALL_GRASS:
            case RAIL:
            case ACTIVATOR_RAIL:
            case DETECTOR_RAIL:
            case POWERED_RAIL:
            case CAULDRON:
            case SUNFLOWER:
            case FLOWER_POT:
            case ROSE_RED:
            case CAKE:
            case BLACK_CARPET:
            case BLUE_CARPET:
            case CYAN_CARPET:
            case BROWN_CARPET:
            case GRAY_CARPET:
            case GREEN_CARPET:
            case LIGHT_BLUE_CARPET:
            case LIGHT_GRAY_CARPET:
            case LIME_CARPET:
            case MAGENTA_CARPET:
            case ORANGE_CARPET:
            case PINK_CARPET:
            case PURPLE_CARPET:
            case RED_CARPET:
            case WHITE_CARPET:
            case YELLOW_CARPET:
                return false;
            default:
                return true;
        }
    }

    private PathfindingTile tileFrom(Point point, PathfindingTile parent) {
        Double uidFor = PathfindingTile.getUid(point, parent);
        if (tiles.containsKey(uidFor)) return tiles.get(uidFor);
        PathfindingTile pathTile = new PathfindingTile(point, parent);
        tiles.put(uidFor, pathTile);
        return pathTile;
    }

    private PathfindingTile tileFrom(Point point) {
        return tileFrom(point, null);
    }
}
