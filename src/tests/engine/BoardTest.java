package tests.engine;

import java.util.List;

import engine.Board;
import utils.BoardUtils;
import engine.Territory;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class BoardTest extends Board {

  private static boolean allianceErrorDetected = false;

  public static void main(String[] args) {
    // TODO: exception handilng
    allianceCheck();
  }

  public static void allianceCheck() {
    final List<Tile> board = initBoard();
    for (int i = 0; i < board.size(); i++) {
      if (board.get(i).getAlliance() == Territory.WHITE &&
          i < BoardUtils.ALL_TILES_COUNT / 2)
        allianceErrorDetected = true;
    }

    if (allianceErrorDetected == true)
      System.out.println("Board Alliance check FAILED");
    else
      System.out.println("Board Alliance check PASSED");
  }

}
