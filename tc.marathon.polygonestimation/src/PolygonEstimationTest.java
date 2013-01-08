import java.util.Scanner;

/**
 * User: movax
 * Date: 08.01.13
 * Time: 20:56
 */
public class PolygonEstimationTest {

        public int sendRequest(int x1,int y1,int x2,int y2)
        {
            Scanner inScan = new Scanner(System.in);
            System.out.println(1);
            System.out.println(x1);
            System.out.println(y1);
            System.out.println(x2);
            System.out.println(y2);
            String scanResult = inScan.nextLine();
            return Integer.parseInt(scanResult);
        }
    public int[] estimate(double D, double P)
    {
        for(int i=0;i<4;i++)
            for(int j=0;j<5;j++)
            {
                int x1=i+50;
                int y1=0;
                int x2=999;
                int y2=i+50;
            if(sendRequest(x1,y1,x2,y2)>0)
            {
                //intersection
                //add to array
            }
            }
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
 }

