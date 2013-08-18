import java.lang.reflect.Array;
import java.util.Scanner;

public class Starter {
    public static void main(java.lang.String[] strings) {
        Scanner inScan = new Scanner(System.in);
        String paramD = inScan.nextLine();
        String paramP = inScan.nextLine();

         PolygonEstimationTest polEstInst = new PolygonEstimationTest();

         int[] estimationRes = polEstInst.estimate(Double.parseDouble(paramD), Double.parseDouble(paramP));

         System.out.println(2);
         System.out.println(10);
          for(int i=0;i<20;i++)
          System.out.println(estimationRes[i]);
          System.out.flush();

    }

}