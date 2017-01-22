package network.palace.core.pathfinding;

import lombok.Getter;

import lombok.Getter;

public class PathfindingTile {

    private final int STRAIGHT_SCORE = 10;
    private final int DIAGONAL_SCORE = 14;

    @Getter private Point point;
    @Getter private PathfindingTile parent;

    @Getter private int fScore;
    @Getter private int gScore;
    @Getter private int hScore;

    private boolean ordinalMovement;

    public PathfindingTile(Point point, PathfindingTile tile) {
        this.point = point;
        this.parent = tile;
    }

    public void updateScores(PathfindingTile start, PathfindingTile end) {
        PathfindingTile current = getParent();
        int combinedScore = 0;

        while(!current.equals(start) && current != null) {
            combinedScore += current.getGScore();
            current = current.getParent();
        }
        ordinalMovement = isOrdinalMovement(parent, this);
        if (ordinalMovement) {
            gScore = combinedScore + STRAIGHT_SCORE;
        } else {
            gScore = combinedScore + DIAGONAL_SCORE;
        }

        hScore = ((int) Math.ceil(end.getPoint().distanceSquared(point)));
        fScore = hScore + gScore;
    }

    private boolean isOrdinalMovement(PathfindingTile start, PathfindingTile destination) {
        Point startPoint = start.getPoint();
        Point endPoint = destination.getPoint();

        return startPoint.distanceSquared(endPoint) < 2;
    }

    public static double getUid(Point point, PathfindingTile parent) {
        return (point.getX() + point.getY() + point.getZ()) * (parent == null ? 1 : getUid(parent.getPoint(), parent.getParent()));
    }
}
