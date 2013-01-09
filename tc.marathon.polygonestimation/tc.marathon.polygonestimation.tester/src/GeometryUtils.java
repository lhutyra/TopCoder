import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeometryUtils {
    public static class Point {
        public int x, y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Segment {
        public Point p1, p2;
        int A, B, C;
        public Segment(Point p1, Point p2) {
            this.p1 = p1;
            this.p2 = p2;

            this.A = p2.y - p1.y;
            this.B = p1.x - p2.x;
            this.C = - A * p1.x - B * p1.y;
        }
        public int signOf(GeometryUtils.Point p) {
            int Z = p.x * A + p.y * B + C;
            if (Z > 0) return 1;
            if (Z < 0) return -1;
            return 0;
        }
    }

    private static class PolygonSegment extends Segment {
        int polygonId;

        public PolygonSegment(Point p1, Point p2, int polygonId) {
            super(p1, p2);
            this.polygonId = polygonId;
        }
    }

    public enum IntersectionResult {NOTHING, SINGLE_POINT, MANY_POINTS}

    private static IntersectionResult intersectLineSegments(int a, int b, int c, int d) {
        if (a > b) {
            int tmp = a; a = b; b = tmp;
        }
        if (c > d) {
            int tmp = c; c = d; d = tmp;
        }
        int left = Math.max(a, c);
        int right = Math.min(b, d);
        if (left < right) {
            return IntersectionResult.MANY_POINTS;
        }
        if (left == right) {
            return IntersectionResult.SINGLE_POINT;
        }
        return IntersectionResult.NOTHING;
    }

    public static IntersectionResult intersectSegments(Segment a, Segment b) {
        int Z1 = b.signOf(a.p1);
        int Z2 = b.signOf(a.p2);
        int Z3 = a.signOf(b.p1);
        int Z4 = a.signOf(b.p2);

        if (Z1 == 0 && Z2 == 0) {
            if (b.p1.x == b.p2.x) {
                return intersectLineSegments(a.p1.y, a.p2.y, b.p1.y, b.p2.y);
            } else {
                return intersectLineSegments(a.p1.x, a.p2.x, b.p1.x, b.p2.x);
            }
        } else {
            if (Z1 * Z2 <= 0 && Z3 * Z4 <= 0) {
                return IntersectionResult.SINGLE_POINT;
            } else {
                return IntersectionResult.NOTHING;
            }
        }
    }

    public static boolean isPolygon(Polygon p) {
        int N = p.npoints;
        int[] x = p.xpoints;
        int[] y = p.ypoints;
        Segment[] s = new Segment[N];
        for (int i=0; i < N; i++) {
            s[i] = new Segment(new Point(x[i], y[i]), new Point(x[(i+1)%N], y[(i+1)%N]));
            if (s[i].p1.x == s[i].p2.x && s[i].p1.y == s[i].p2.y) {
                return false;
            }
        }
        for (int i=0; i < N; i++) {
            for (int j=i+1; j < N; j++) {
                IntersectionResult exp = (j == i + 1 || j == N - 1 && i == 0 ? IntersectionResult.SINGLE_POINT : IntersectionResult.NOTHING);
                if (exp != intersectSegments(s[i], s[j])) {
                    return false;
                }
            }
        }
        return true;
    }

    public static double polygonArea(Polygon p) {
        double res = 0.0;
        int N = p.npoints;
        int[] x = p.xpoints;
        int[] y = p.ypoints;
        for (int i=0; i < N; i++) {
            res += (x[(i + 1) % N] - x[i]) * (y[i] + y[(i + 1) % N]);
        }
        return Math.abs(res) / 2.0;
    }

    private static class Line implements Comparable<Line> {
        double y1, y2;
        int polygonId;
        public Line(double y1, double y2, int polygonId) {
            this.y1 = y1;
            this.y2 = y2;
            this.polygonId = polygonId;
        }
        public int compareTo(Line other) {
            return Double.compare((y1 + y2) / 2.0, (other.y1 + other.y2) / 2.0);
        }
    }

    public static class DoublePoint {
        double x, y;
        public DoublePoint(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private static DoublePoint intersectionPoint(Segment s1, Segment s2) {
        return new DoublePoint(
            - ((double)s1.C * s2.B - (double)s2.C * s1.B) / ((double)s1.A * s2.B - (double)s2.A * s1.B),
            - ((double)s1.C * s2.A - (double)s2.C * s1.A) / ((double)s1.B * s2.A - (double)s2.B * s1.A)
        );
    }

    public static DoublePoint castRay(Polygon p, Point p1, Point p2) {
        int minX = p.xpoints[0], maxX = p.xpoints[0];
        int minY = p.ypoints[0], maxY = p.ypoints[0];
        int np = p.npoints;
        for (int i=1; i < np; i++) {
            minX = Math.min(minX, p.xpoints[i]);
            minY = Math.min(minY, p.ypoints[i]);
            maxX = Math.max(maxX, p.xpoints[i]);
            maxY = Math.max(maxY, p.ypoints[i]);
        }
        while (p2.x >= TestCase.MIN_COORD && p2.x <= TestCase.MAX_COORD
                && p2.y >= TestCase.MIN_COORD && p2.y <= TestCase.MAX_COORD) {
            p2.x += p2.x - p1.x;
            p2.y += p2.y - p1.y;
        }

        DoublePoint res = null;
        double bestDist = 1e100;

        Segment ray = new Segment(p1, p2);
        for (int i=0; i < np; i++) {
            Segment side = new Segment(new Point(p.xpoints[i], p.ypoints[i]),
                    new Point(p.xpoints[(i+1)%np], p.ypoints[(i+1)%np]));
            if (intersectSegments(ray, side).equals(IntersectionResult.SINGLE_POINT)) {
                DoublePoint cand = intersectionPoint(side, ray);
                double dist = (cand.x - p1.x) * (cand.x - p1.x) + (cand.y - p1.y) * (cand.y - p1.y);
                if (dist < bestDist) {
                    bestDist = dist;
                    res = cand;
                }
            }
        }

        return res;
    }

    private final static double EPS = 1e-8;

    public static double intersectionArea(Polygon p1, Polygon p2) {
        int n = p1.npoints;
        int m = p2.npoints;

        List<Double> events = new ArrayList<Double>();

        Segment[] segm1 = new Segment[n];
        for (int i=0; i < n; i++) {
            segm1[i] = new Segment(new Point(p1.xpoints[i], p1.ypoints[i]),
                    new Point(p1.xpoints[(i+1)%n], p1.ypoints[(i+1)%n]));
            events.add((double) p1.xpoints[i]);
        }

        Segment[] segm2 = new Segment[m];
        for (int i=0; i < m; i++) {
            segm2[i] = new Segment(new Point(p2.xpoints[i], p2.ypoints[i]),
                    new Point(p2.xpoints[(i+1)%m], p2.ypoints[(i+1)%m]));
            events.add((double) p2.xpoints[i]);
        }

        for (int i=0; i < n; i++) {
            for (int j=0; j < m; j++) {
                Segment s1 = segm1[i], s2 = segm2[j];
                if (intersectSegments(s1, s2) == IntersectionResult.SINGLE_POINT) {
                    events.add(- ((double)s1.C * s2.B - (double)s2.C * s1.B) / ((double)s1.A * s2.B - (double)s2.A * s1.B));
                }
            }
        }

        Collections.sort(events);

        List<PolygonSegment> segments = new ArrayList<PolygonSegment>();
        for (Segment s : segm1) {
            segments.add(new PolygonSegment(s.p1, s.p2, 1));
        }
        for (Segment s : segm2) {
            segments.add(new PolygonSegment(s.p1, s.p2, 2));
        }
        for (Segment s : segments) {
            if (s.p1.x > s.p2.x) {
                Point tmp = s.p1;
                s.p1 = s.p2;
                s.p2 = tmp;
            }
        }

        double res = 0.0;

        for (int i=0; i + 1 < events.size(); i++) {
            List<Line> lines = new ArrayList<Line>();
            double lx = events.get(i), rx = events.get(i + 1);
            if (rx <= lx + EPS) {
                continue;
            }
            for (PolygonSegment s : segments) {
                if (s.p1.x <= lx + EPS && rx - EPS <= s.p2.x) {
                    lines.add(new Line((- s.C - s.A * lx) / (double)s.B,
                            (- s.C - s.A * rx) / (double)s.B, s.polygonId));
                }
            }
            Collections.sort(lines);
            boolean insideP1 = false, insideP2 = false;
            for (int j=0; j + 1 < lines.size(); j++) {
                if (lines.get(j).polygonId == 1) {
                    insideP1 = !insideP1;
                } else {
                    insideP2 = !insideP2;
                }
                if (insideP1 && insideP2) {
                    res += (rx - lx) * (- lines.get(j).y1 - lines.get(j).y2 +
                            lines.get(j + 1).y1 + lines.get(j + 1).y2);
                }
            }
        }

        return Math.abs(res / 2.0);
    }
}