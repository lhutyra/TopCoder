import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: CfA2
 * Date: 20.01.13
 * Time: 13:02
 * To change this template use File | Settings | File Templates.
 */
public class TestSimulator {

    public static void main(java.lang.String[] strings) {
        Scanner inScan = new Scanner(System.in);
        String betaAngle = inScan.nextLine();


        ISS spaceShipAlpha = new ISS();
        // System.out.println("Inside mine function");
        System.out.println(spaceShipAlpha.getInitialOrientation(Double.parseDouble(betaAngle)));

        System.out.flush();
        for(int i=0;i<92;i++)
        {
             //                System.out.println("Minute " + i);
            printISSSettings(spaceShipAlpha.getStateAtMinute(i));
        }
    }
    private static void printISSSettings(double[] settings)
    {
        System.out.println(1);
       for(int i=0;i<settings.length     ;i++)
       {
           System.out.println(settings[i]);
       }
    }




}
