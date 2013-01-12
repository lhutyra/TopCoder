/**
 * Created with IntelliJ IDEA.
 * User: CfA2
 * Date: 12.01.13
 * Time: 18:12
 * To change this template use File | Settings | File Templates.
 */

public class PenguinTiles {
    public int minMoves(String[] gameBoard) {

        if (gameBoard[gameBoard.length-1] != null
                && gameBoard[gameBoard.length-1].lastIndexOf('.') == gameBoard[gameBoard.length-1].length())
            return 0;

        for (int i = 0; i < gameBoard.length; i++) {
            if ((gameBoard[i].charAt(gameBoard[i].length()-1) == '.')&&i==gameBoard.length-1)
            {
                return 0;

            }
            else if((gameBoard[i].charAt(0) == '.')&&i==gameBoard.length-1)
            {
                 return 1;
            }

            else if((gameBoard[i].charAt(gameBoard[i].length()-1) == '.'))
            {
                return 1;
            }
        }
        return 2;

    }
}
