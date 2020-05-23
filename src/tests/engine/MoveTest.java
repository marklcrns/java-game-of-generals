package tests.engine;

import javax.swing.SwingUtilities;

import engine.Alliance;
import engine.Board;
import engine.Board.BoardBuilder;
import engine.Move;
import engine.player.Player;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-21
 */
public class MoveTest {
  private static BoardBuilder builder = new BoardBuilder();
  private static final Board board = new Board(builder.createDemoBoardBuild());
  private static final Player playerBlack = new Player(board, Alliance.BLACK);
  private static final Player playerWhite = new Player(board, Alliance.WHITE);

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        // board.displayBoard();
        aggressiveMoveTest();
      }
    });

    // TODO: exception handling
  }

  private static void aggressiveMoveTest() {
    System.out.println(board);

    board.addPlayerWhite(playerWhite);
    board.addPlayerBlack(playerBlack);

    board.startGame();

    playerWhite.makeMove(54, 45);
    playerBlack.makeMove(9, 18);
    playerWhite.makeMove(45, 36);
    playerBlack.makeMove(18, 27);
    playerWhite.makeMove(36, 27);

    System.out.println(board);

    System.out.println(playerWhite);
    System.out.println(playerBlack);
  }
}
