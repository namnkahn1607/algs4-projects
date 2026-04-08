/* *****************************************************************************
 *  Name: Nguyen Le Nam Khanh
 *  Date: Jan 17, 2026
 *  Description: KdTree (Refactored with RectHV and recursive operations)
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private static final boolean VERTICAL = false;
    private static final boolean HORIZONTAL = true;
    private static final RectHV UNIT_SQUARE = new RectHV(0.0, 0.0, 1.0, 1.0);

    private static class Node {
        private Point2D key;
        private Node left;
        private Node right;
        private RectHV rect;

        public Node(Point2D key, RectHV rect) {
            this.key = key;
            this.rect = rect;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;
    private int size;

    // construct an empty tree of points
    public KdTree() {
        this.root = null;
        this.size = 0;
    }

    // is the tree empty?
    public boolean isEmpty() {
        return root == null;
    }

    // number of points in the tree
    public int size() {
        return size;
    }

    // add the point to the tree (if it is not already in the tree)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("point cannot be null");
        }

        root = insert(root, p, VERTICAL, UNIT_SQUARE);
    }

    private Node insert(Node h, Point2D p, boolean orientation, RectHV rect) {
        if (h == null) {
            size++;
            return new Node(p, rect);
        }

        int cmpX = compareX(h.key, p);
        int cmpY = compareY(h.key, p);

        if (cmpX == 0 && cmpY == 0) {
            return h;
        }

        int cmp = orientation == VERTICAL ? cmpX : cmpY;

        if (cmp > 0) {
            RectHV leftRect = getLeftRect(h.rect, h.key, orientation);
            h.left = insert(h.left, p, !orientation, leftRect);
        }
        else {
            RectHV rightRect = getRightRect(h.rect, h.key, orientation);
            h.right = insert(h.right, p, !orientation, rightRect);
        }

        return h;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("point cannot be null");
        }

        return contains(root, p, VERTICAL);
    }

    private boolean contains(Node h, Point2D p, boolean orientation) {
        if (h == null) {
            return false;
        }

        int cmpX = compareX(h.key, p);
        int cmpY = compareY(h.key, p);

        if (cmpX == 0 && cmpY == 0) {
            return true;
        }

        int cmp = orientation == HORIZONTAL ? cmpY : cmpX;

        if (cmp > 0) {
            return contains(h.left, p, !orientation);
        }
        else {
            return contains(h.right, p, !orientation);
        }
    }

    // draw all points to standard draw
    public void draw() {
        draw(root, VERTICAL);
    }

    private void draw(Node h, boolean orientation) {
        if (h == null) {
            return;
        }

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        h.key.draw();

        StdDraw.setPenRadius();
        if (orientation == VERTICAL) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(h.key.x(), h.rect.ymin(), h.key.x(), h.rect.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(h.rect.xmin(), h.key.y(), h.rect.xmax(), h.key.y());
        }

        draw(h.left, !orientation);
        draw(h.right, !orientation);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("specified rectangle is null");
        }

        Bag<Point2D> points = new Bag<>();
        range(root, rect, points);
        return points;
    }

    private void range(Node h, RectHV rect, Bag<Point2D> points) {
        if (h == null) {
            return;
        }

        if (!rect.intersects(h.rect)) {
            return;
        }

        if (rect.contains(h.key)) {
            points.add(h.key);
        }

        range(h.left, rect, points);
        range(h.right, rect, points);
    }

    private class Nearest {
        Point2D point = null;
        double closestDistance = Double.POSITIVE_INFINITY;
    }

    // a nearest neighbor in the tree to point p, null if the tree is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("specified point is null");
        }

        if (isEmpty()) {
            return null;
        }

        Nearest best = new Nearest();
        nearest(root, p, best, VERTICAL);
        return best.point;
    }

    private void nearest(Node h, Point2D query, Nearest best, boolean orientation) {
        if (h == null) {
            return;
        }

        if (h.rect.distanceSquaredTo(query) >= best.closestDistance) {
            return;
        }

        double distToCurr = query.distanceSquaredTo(h.key);
        if (distToCurr < best.closestDistance) {
            best.closestDistance = distToCurr;
            best.point = h.key;
        }

        double diff = orientation == VERTICAL ? query.x() - h.key.x() : query.y() - h.key.y();
        Node near, far;

        if (diff < 0) {
            near = h.left;
            far = h.right;
        }
        else {
            near = h.right;
            far = h.left;
        }

        nearest(near, query, best, !orientation);

        if (diff * diff < best.closestDistance) {
            nearest(far, query, best, !orientation);
        }
    }

    private RectHV getLeftRect(RectHV parentRect, Point2D splitter, boolean orientation) {
        if (orientation == VERTICAL) {
            return new RectHV(parentRect.xmin(), parentRect.ymin(),
                              splitter.x(), parentRect.ymax());
        }
        else {
            return new RectHV(parentRect.xmin(), parentRect.ymin(),
                              parentRect.xmax(), splitter.y());
        }
    }

    private RectHV getRightRect(RectHV parentRect, Point2D splitter, boolean orientation) {
        if (orientation == VERTICAL) {
            return new RectHV(splitter.x(), parentRect.ymin(),
                              parentRect.xmax(), parentRect.ymax());
        }
        else {
            return new RectHV(parentRect.xmin(), splitter.y(),
                              parentRect.xmax(), parentRect.ymax());
        }
    }

    private int compareX(Point2D p1, Point2D p2) {
        return Double.compare(p1.x(), p2.x());
    }

    private int compareY(Point2D p1, Point2D p2) {
        return Double.compare(p1.y(), p2.y());
    }
}
