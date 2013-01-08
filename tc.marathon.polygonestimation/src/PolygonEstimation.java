/**
 * User: movax
 * Date: 05.01.13
 * Time: 20:46
 */

public class PolygonEstimation {

    /**
     * public int[] estimate(double D, double P)
     * {
     * return null;
     * <p/>
     * }
     */

    public int[] estimate(double D, double P) {
      /*  # use this method only for local testing
        # for server testing, use RayCaster.sendRequest() instead
        sendRequest(x1, y1, x2, y2):
        printLine(1)
        printLine(x1)
        printLine(y1)
        printLine(x2)
        printLine(y2)

        flush(stdout)

        res = int(readLine())

        if (res == 0):
        return No Intersection

        X = double(readLine())
        Y = double(readLine())

        return Intersection Approximately at (X, Y)  */
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

    private Object getNextScanCoordinates(Object previousScan) {
        return null;
    }
}
