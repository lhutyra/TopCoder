/**
 * Created with IntelliJ IDEA.
 * User: movax
 * Date: 05.01.13
 * Time: 20:46
 * To change this template use File | Settings | File Templates.
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
        polygon[2] = 110;
        polygon[3] = 0;
        polygon[4] = 300;
        polygon[5] = 0;
        polygon[6] = 400;
        polygon[7] = 400;
        polygon[8] = 530;
        polygon[9] = 444;
        polygon[10] = 600;
        polygon[11] = 500;
        polygon[12] = 250;
        polygon[13] = 750;
        polygon[14] = 100;
        polygon[15] = 300;
        polygon[16] = 100;
        polygon[17] = 50;
        polygon[18] = 20;
        polygon[19] = 20;
        return polygon;

    }

    private Object getNextScanCoordinates(Object previousScan) {
        return null;
    }
}
