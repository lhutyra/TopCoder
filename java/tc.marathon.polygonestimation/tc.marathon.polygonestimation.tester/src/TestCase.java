
import java.awt.*;
import java.security.SecureRandom;

public class TestCase {
    public static final int MIN_N = 10;
    public static final int MAX_N = 100;

    public static final int MIN_COORD = 0;
    public static final int MAX_COORD = 9999;

    public static final int MIN_D = 5;
    public static final int MAX_D = 50;

    public static final int MIN_P = 10;
    public static final int MAX_P = 100;

    // ===== //

    public static int numReq = 1000;
    public static double D, P;
    public static Polygon p;

    public static SecureRandom rnd;

    public static void generate() throws Exception {
        rnd = SecureRandom.getInstance("SHA1PRNG");

        rnd.setSeed(VisParams.seed);

        double x = rnd.nextDouble();
        D = MIN_D + (MAX_D - MIN_D) * x * x;
        P = MIN_P + (MAX_P - MIN_P) * rnd.nextDouble();

        int N = MIN_N + rnd.nextInt(MAX_N - MIN_N + 1);

        int[] X = new int[N];
        int[] Y = new int[N];

        for (int i=0; i < N; i++) {
            while (true) {
                X[i] = MIN_COORD + rnd.nextInt(MAX_COORD - MIN_COORD + 1);
                Y[i] = MIN_COORD + rnd.nextInt(MAX_COORD - MIN_COORD + 1);

                boolean ok = true;

                for (int j=0; j < i && ok; j++) {
                    if (X[i] == X[j] && Y[i] == Y[j]) {
                        ok = false;
                    }
                }

                GeometryUtils.Point pnt = new GeometryUtils.Point(X[i], Y[i]);

                for (int j=0; j < i && ok; j++) {
                    for (int k=0; k < j && ok; k++) {
                        GeometryUtils.Segment s = new GeometryUtils.Segment(new GeometryUtils.Point(X[j], Y[j]), new GeometryUtils.Point(X[k], Y[k]));
                        if (s.signOf(pnt) == 0) {
                            ok = false;
                        }
                    }
                }

                if (ok) {
                    break;
                }
            }
        }

        while (true) {
            boolean ok = true;
            GeometryUtils.Segment[] s = new GeometryUtils.Segment[N];
            for (int i=0; i < N; i++)
                s[i] = new GeometryUtils.Segment(new GeometryUtils.Point(X[i], Y[i]), new GeometryUtils.Point(X[(i+1)%N], Y[(i+1)%N]));

            for (int i=0; i < N-2 && ok; i++) {
                for (int j=i+2; j < N && ok; j++) {
                    if (i == 0 && j == N-1) {
                        break;
                    }
                    if (GeometryUtils.intersectSegments(s[i], s[j]).equals(GeometryUtils.IntersectionResult.SINGLE_POINT)) {
                        int l = i + 1, r = j;
                        while (l <= r) {
                            int tmp = X[l]; X[l] = X[r]; X[r] = tmp;
                            tmp = Y[l]; Y[l] = Y[r]; Y[r] = tmp;
                            l++; r--;
                            ok = false;
                        }
                    }
                }
            }

            if (ok) {
                break;
            }
        }

        p = new Polygon();
        for (int i=0; i < N; i++) {
            p.addPoint(X[i], Y[i]);
        }

        if (VisParams.vis) {
            Drawer.getDrawer().setHidden(p);
        }
    }
}
