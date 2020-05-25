package game;

import javax.swing.SwingUtilities;

import engine.Alliance;
import engine.Board;
import engine.Board.BoardBuilder;
import engine.player.Player;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-16
 */
public class Main {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        Board board = new Board();
        BoardBuilder builder = new BoardBuilder();
        Player player1 = new Player(board, Alliance.WHITE);
        Player player2 = new Player(board, Alliance.BLACK);

        // TODO evaluate player owned pieces after building
        builder = builder.createRandomBuild();
        board.buildBoard(builder);

        board.addPlayerWhite(player1);
        board.addPlayerBlack(player2);

        board.setDebugMode(true);
        board.startGame();
      }
    });
  }

}
