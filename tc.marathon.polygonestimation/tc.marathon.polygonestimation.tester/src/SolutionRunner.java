import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;

///import tc.marathon.polygonestimation.tester.GeometryUtils.DoublePoint;
//import tc.marathon.polygonestimation.tester.GeometryUtils.GeometryUtils.Point;

public class SolutionRunner {
    private static final int REQUEST_CMD = 1;
    private static final int ESTIMATE_CMD = 2;

    private static final int MAX_OUTPUT_VERTICES = 1000;

    private static Scanner scanner;
    private static PrintWriter writer;
    private static Process solution;

    private static int reqId = 0;

    static void handleRequest(int x1, int y1, int x2, int y2) throws Exception {
        if (reqId == TestCase.numReq && !VisParams.manual) {
            throw new Exception("Number of allowed requests exceeded.");
        }

        if (x1 < TestCase.MIN_COORD || x1 > TestCase.MAX_COORD) {
            throw new Exception("For request " + reqId + ", x1 is not between " + TestCase.MIN_COORD + " and "
                    + TestCase.MAX_COORD);
        }

        if (y1 < TestCase.MIN_COORD || y1 > TestCase.MAX_COORD) {
            throw new Exception("For request " + reqId + ", y1 is not between " + TestCase.MIN_COORD + " and "
                    + TestCase.MAX_COORD);
        }

        if (x2 < TestCase.MIN_COORD || x2 > TestCase.MAX_COORD) {
            throw new Exception("For request " + reqId + ", x2 is not between " + TestCase.MIN_COORD + " and "
                    + TestCase.MAX_COORD);
        }

        if (y2 < TestCase.MIN_COORD || y2 > TestCase.MAX_COORD) {
            throw new Exception("For request " + reqId + ", y2 is not between " + TestCase.MIN_COORD + " and "
                    + TestCase.MAX_COORD);
        }

        if (x1 == x2 && y1 == y2) {
            throw new Exception("For request " + reqId + ", (x1, y1) and (x2, y2) are the same points.");
        }

        reqId++;

        GeometryUtils.DoublePoint dp = GeometryUtils.castRay(TestCase.p, new GeometryUtils.Point(x1, y1), new GeometryUtils.Point(x2, y2));

        if (dp != null) {
            double dx = x2 - x1, dy = y2 - y1;
            double dst = Math.sqrt(dx * dx + dy * dy);
            dx /= dst;
            dy /= dst;

            double err = TestCase.D * TestCase.rnd.nextGaussian();
            dp.x += err * dx;
            dp.y += err * dy;
        }

        if (!VisParams.manual) {
            if (dp != null) {
                writer.println(2);
                writer.println(dp.x);
                writer.println(dp.y);
            } else {
                writer.println(0);
            }
            writer.flush();

            if (VisParams.debug) {
                System.out.println("Request: (" + x1 + ", " + y1 + ") -> (" + x2 + ", " + y2 + ")");
                System.out.print("Result: ");
                if (dp == null) {
                    System.out.println("no intersection");
                } else {
                    System.out.println("(" + dp.x + ", " + dp.y + ")");
                }
            }
        }

        if (VisParams.vis) {
            boolean finite = true;
            if (dp == null) {
                Polygon bound = new Polygon();
                bound.addPoint(TestCase.MIN_COORD-1, TestCase.MIN_COORD-1);
                bound.addPoint(TestCase.MIN_COORD-1, TestCase.MAX_COORD+1);
                bound.addPoint(TestCase.MAX_COORD+1, TestCase.MAX_COORD+1);
                bound.addPoint(TestCase.MAX_COORD+1, TestCase.MIN_COORD-1);
                dp = GeometryUtils.castRay(bound, new GeometryUtils.Point(x1, y1), new GeometryUtils.Point(x2, y2));
                finite = false;
            }
            Drawer.getDrawer().addRequest(x1, y1, dp.x, dp.y, finite);
        }
    }

    static void handleEstimate(int[] X, int[] Y) throws Exception {
        int N = X.length;
        if (N < 3 || N > MAX_OUTPUT_VERTICES) {
            throw new Exception("Your estimate contains inappropriate amount of vertices: " + N + ".");
        }
        for (int i=0; i < N; i++) {
            if (X[i] < TestCase.MIN_COORD || X[i] > TestCase.MAX_COORD) {
                throw new Exception("In vertex " + i + " of your estimate, X is" +
                        "not between " + TestCase.MIN_COORD + " and " + TestCase.MAX_COORD);
            }
            if (Y[i] < TestCase.MIN_COORD || Y[i] > TestCase.MAX_COORD) {
                throw new Exception("In vertex " + i + " of your estimate, Y is" +
                        "not between " + TestCase.MIN_COORD + " and " + TestCase.MAX_COORD);
            }
        }
        Polygon p = new Polygon();
        for (int i=0; i < N; i++) {
            p.addPoint(X[i], Y[i]);
        }
        if (!GeometryUtils.isPolygon(p)) {
            throw new Exception("Your estimate is not a simple polygon.");
        }
        if (VisParams.vis) {
            Drawer.getDrawer().setEstimate(p);
        }

        double hiddenArea = GeometryUtils.polygonArea(TestCase.p);
        double estimateArea = GeometryUtils.polygonArea(p);
        double intersectionArea = GeometryUtils.intersectionArea(p, TestCase.p);

        if (VisParams.debug) {
            System.out.println("Guess polygon area = " + hiddenArea);
            System.out.println("Area of your estimate = " + estimateArea);
            System.out.println("Intersection area = " + intersectionArea);
        }

        double penalty = Math.pow(0.99, reqId / TestCase.P);

        if (VisParams.debug) {
            System.out.println("Request penalty multiplier = " + penalty);
        }

        double score = 1.0 - penalty * intersectionArea / Math.max(hiddenArea, estimateArea);
        System.out.println("Score = " + score);
    }

    public static void runTestCase() throws Exception {
        if (!VisParams.manual) {
            try {
                solution = Runtime.getRuntime().exec(VisParams.execCommand);
            }
            catch (Exception e) {
                throw new Exception("Unable to execute your solution using the provided" +
                        " command: " + VisParams.execCommand + ".");
            }

            scanner = new Scanner(new InputStreamReader(solution.getInputStream()));
            writer = new PrintWriter(solution.getOutputStream());
            new ErrorStreamRedirector(solution.getErrorStream()).start();

            writer.println(TestCase.D);
            writer.println(TestCase.P);
            writer.flush();

            while (true) {
                int cmd = scanner.nextInt();
                if (cmd == REQUEST_CMD) {
                    int x1 = scanner.nextInt();
                    int y1 = scanner.nextInt();
                    int x2 = scanner.nextInt();
                    int y2 = scanner.nextInt();
                    handleRequest(x1, y1, x2, y2);
                } else if (cmd == ESTIMATE_CMD) {
                    int N = scanner.nextInt();
                    int[] X = new int[N];
                    int[] Y = new int[N];
                    for (int i=0; i<N; i++) {
                        X[i] = scanner.nextInt();
                        Y[i] = scanner.nextInt();
                    }
                    handleEstimate(X, Y);
                    break;
                } else throw new Exception("Integer " + cmd + " does not " +
                        "correspond neither to request nor to making an estimate.");
            }
        }
    }
    public static void runTestCase(String execCommand) throws Exception {
        if (!VisParams.manual) {
            try {
                System.out.println("inside run test");
                String classPath =          "D:\\projects\\TopCoder\\TopCoder\\out\\production\\tc.marathon.polygonestimation\\"+execCommand;
                System.out.println(classPath);
                solution = Runtime.getRuntime().exec(classPath);
            }
            catch (Exception e) {
                throw new Exception("Unable to execute your solution using the provided" +
                        " command: " + execCommand + "." + e.getMessage());
            }

            scanner = new Scanner(new InputStreamReader(solution.getInputStream()));
            writer = new PrintWriter(solution.getOutputStream());
            new ErrorStreamRedirector(solution.getErrorStream()).start();

            writer.println(TestCase.D);
            writer.println(TestCase.P);
            writer.flush();

            while (true) {
                int cmd = scanner.nextInt();
                if (cmd == REQUEST_CMD) {
                    int x1 = scanner.nextInt();
                    int y1 = scanner.nextInt();
                    int x2 = scanner.nextInt();
                    int y2 = scanner.nextInt();
                    handleRequest(x1, y1, x2, y2);
                } else if (cmd == ESTIMATE_CMD) {
                    int N = scanner.nextInt();
                    int[] X = new int[N];
                    int[] Y = new int[N];
                    for (int i=0; i<N; i++) {
                        X[i] = scanner.nextInt();
                        Y[i] = scanner.nextInt();
                    }
                    handleEstimate(X, Y);
                    break;
                } else throw new Exception("Integer " + cmd + " does not " +
                        "correspond neither to request nor to making an estimate.");
            }
        }
    }

    public static void stopSolution() {
        if (solution != null) {
            try {
                solution.destroy();
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    private static class ErrorStreamRedirector extends Thread {
        BufferedReader reader;

        public ErrorStreamRedirector(InputStream is) {
            reader = new BufferedReader(new InputStreamReader(is));
        }

        public void run() {
            while (true) {
                String s;
                try {
                    s = reader.readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (s == null) {
                    break;
                }
                System.err.println(s);
            }
        }
    }
}