import java.util.Scanner;

/**
 * User: movax
 * Date: 08.01.13
 * Time: 20:56
 */
public class PolygonEstimationTest {
    private final static int FOUND_FLAG = 2;

    public double[] sendRequestTEST(int x1, int y1, int x2, int y2) {
        Scanner inScan = new Scanner(System.in);
        System.out.println(1);
        System.out.println(x1);
        System.out.println(y1);
        System.out.println(x2);
        System.out.println(y2);
        String scanResult = inScan.nextLine();
        if (Integer.parseInt(scanResult) == FOUND_FLAG) {
            //read x,y.
            double[] coordinates = new double[2];
            coordinates[0] = Double.parseDouble(inScan.nextLine());
            coordinates[1] = Double.parseDouble(inScan.nextLine());
            return coordinates;
        }
        return new double[0];
    }

    public int[] estimate(double D, double P) {
        sendBottomTopRays();
        //return Intersection Approximately at (X, Y)  */
        int[] polygon = new int[20];

        polygon[0] = 0;
        polygon[1] = 0;
        polygon[2] = 1100;
        polygon[3] = 0;
        polygon[4] = 3000;
        polygon[5] = 0;
        polygon[6] = 4000;
        polygon[7] = 4000;
        polygon[8] = 5300;
        polygon[9] = 4440;
        polygon[10] = 6000;
        polygon[11] = 5000;
        polygon[12] = 2500;
        polygon[13] = 7500;
        polygon[14] = 1000;
        polygon[15] = 3000;
        polygon[16] = 1000;
        polygon[17] = 500;
        polygon[18] = 200;
        polygon[19] = 200;
        return polygon;
    }

    private void sendBottomTopRays() {

        for (int i = 0; i < 10; i++) {
            double[] scanResult = sendRequestTEST((i + 1) * 100, 0, (i + 1) * 100, 999);
            if (scanResult.length > 1) {
                System.out.println("x:" + scanResult[0]);
                System.out.println("y:" + scanResult[1]);
            }
        }
    }
}

