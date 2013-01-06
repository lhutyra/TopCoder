import java.util.Scanner;

public class Starter {
    public static void main(java.lang.String[] strings) {

        Scanner inScan = new Scanner(System.in);
        // String  inScan.nextLine();
        // marathon.tco12onsite.
        //System.out.println("1");
        //System.out.println("0");
        //System.out.println("0");
        //System.out.println("300");
        //System.out.println("300");

        String paramD = inScan.nextLine();
        String paramP = inScan.nextLine();

        PolygonEstimation polEstInst = new PolygonEstimation();
        int[] estimationRes = polEstInst.estimate(Double.parseDouble(paramD), Double.parseDouble(paramP));
        System.out.println(2);
        System.out.println(10);
         for(int i=0;i<20;i++)
             System.out.println(estimationRes[i]);
       System.out.flush();

    }

}