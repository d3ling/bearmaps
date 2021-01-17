package bearmaps.proj2ab;

import java.util.List;

public class KDTree {
    static boolean horizontal = true;
    Node kd;

    private class Node {
        private Point point;
        private Node left_child;
        private Node right_child;
        private boolean orientation;

        private Node(Point p, boolean orientation) {
            point = p;
            this.orientation = orientation;
            left_child = null;
            right_child = null;
        }
    }

    public KDTree(List<Point> points) {
        for (Point p : points) {
            kd = add(p, kd, horizontal);
        }
    }

    private Node add(Point p, Node n, boolean orientation) {
        if (n == null) {
            return new Node(p, orientation);
        }

        if (p.equals(n.point)) {
            return n;
        }

        if (comparePoints(p, n.point, orientation) < 0) {
            n.left_child = add(p, n.left_child, !orientation);
        } else {
            n.right_child = add(p, n.right_child, !orientation);
        }

        return n;
    }

    private int comparePoints(Point a, Point b, boolean orientation) {
        if (orientation == horizontal) {
            return Double.compare(a.getX(), b.getX());
        } else {
            return Double.compare(a.getY(), b.getY());
        }
    }

    /* Returns the closest point to the inputted coordinates */
    public Point nearest(double x, double y) {
        Point goal = new Point(x, y);
        return nearestHelper(kd, goal, kd.point);
    }

    private Point nearestHelper(Node n, Point goal, Point best) {
        if (n == null) {
            return best;
        }

        if (Point.distance(goal, n.point) < Point.distance(goal, best)) {
            best = n.point;
        }

        Node good_side;
        Node bad_side;
        if (comparePoints(goal, n.point, n.orientation) < 0) {
            good_side = n.left_child;
            bad_side = n.right_child;
        } else {
            good_side = n.right_child;
            bad_side = n.left_child;
        }

        best = nearestHelper(good_side, goal, best);

        double best_dist = Point.distance(goal, best);
        double bad_side_best_dist = perp_dist_sqd(goal, n.point, n.orientation);
        if (bad_side_best_dist < best_dist) {
            best = nearestHelper(bad_side, goal, best);
        }

        return best;
    }

    /* Returns squared distance to compare with the Point.distance method which
     * also returns squared distance
     * */
    private double perp_dist_sqd(Point a, Point b, boolean orientation) {
        if (orientation == horizontal) {
            return Math.pow(a.getX() - b.getX(), 2);
        } else {
            return Math.pow(a.getY() - b.getY(), 2);
        }
    }
}
