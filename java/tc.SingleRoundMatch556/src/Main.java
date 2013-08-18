/**
 * Created with IntelliJ IDEA.
 * User: CfA2
 * Date: 12.01.13
 * Time: 18:07
 * To change this template use File | Settings | File Templates.
 */
import java.lang.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] params)
    {
        Scanner inScan = new Scanner(System.in);


        String[] gamein = new String[2];
        gamein[0]="PP";
        gamein[1]="P."  ;
        PenguinTiles gameImpl = new PenguinTiles();
        System.out.println(gameImpl.minMoves(gamein));
    }
}
