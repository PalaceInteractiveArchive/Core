package network.palace.core.pathfinding;

import lombok.Getter;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;

public class Pathfinder {

    @Getter private final Map<Double, PathfindingTile> tiles = new HashMap<>();

    private Point startPos;
    private Point endPos;
    private World world;

    private PathfindingTile start;
    private PathfindingTile end;

    public Pathfinder(Point startPos, Point endPos, World world) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.world = world;
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
        List<PathfindingTile> pathTiles = new ArrayList<>();
        pathTiles.addAll(closedSet);
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

                    Point current = Point.of(centerX + x, centerY + y, centerZ + z);

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
        Block walkingOn = current.getLocation(world).getBlock();

        if (!canWalkOn(walkingOn)) return false;

        Block walkingThrough1 = walkingOn.getRelative(0, 1, 0), walkingThrough2 = walkingThrough1.getRelative(0, 1, 0);
        return canWalkThrough(walkingThrough1) && canWalkThrough(walkingThrough2);
    }

    private static boolean canWalkThrough(Block b) {
        switch (b.getType()) {
            case AIR:
            case LAVA:
            case STATIONARY_LAVA:
            case STATIONARY_WATER:
            case WATER:
            case PORTAL:
                return true;
            case IRON_DOOR:
            case WOODEN_DOOR:
            case WOOD_DOOR:
            case FENCE_GATE:
                if ((b.getData() & 16) != 16)
                    return true;
            default:
                return false;
        }
    }

    private boolean canWalkOn(Block b) {
        if (canWalkThrough(b)) return false;

        switch (b.getType()) {
            case LADDER:
            case WHEAT:
            case LONG_GRASS:
            case RAILS:
            case ACTIVATOR_RAIL:
            case DETECTOR_RAIL:
            case POWERED_RAIL:
            case CAULDRON:
            case YELLOW_FLOWER:
            case FLOWER_POT:
            case RED_ROSE:
            case CAKE_BLOCK:
            case CARPET:
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
