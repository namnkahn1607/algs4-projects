/* *****************************************************************************
 *  Name: Nguyen Le Nam Khanh
 *  Date: Jan 17, 2026
 *  Description: PointSET
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

public class PointSET {

    private final SET<Point2D> pointSet;

    // construct an empty set of points
    public PointSET() {
        this.pointSet = new SET<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return pointSet.isEmpty();
    }

    // number of points in the set
    public int size() {
        return pointSet.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("specified point is null");
        }

        pointSet.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("specified point is null");
        }

        return pointSet.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : pointSet) {
            p.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("specified rectangle is null");
        }

        Bag<Point2D> points = new Bag<>();
        for (Point2D p : pointSet) {
            if (rect.contains(p)) {
                points.add(p);
            }
        }

        return points;
    }

    // a nearest neighbor in the set to point p, null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("specified point is null");
        }

        if (isEmpty()) {
            return null;
        }

        Point2D nearest = null;
        double closestDistance = Double.POSITIVE_INFINITY;

        for (Point2D point : pointSet) {
            double currentDistance = point.distanceSquaredTo(p);
            if (currentDistance < closestDistance) {
                closestDistance = currentDistance;
                nearest = point;
            }
        }

        return nearest;
    }
}
