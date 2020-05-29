package game;

import javax.swing.SwingUtilities;

import engine.Alliance;
import engine.Board;
import engine.Board.BoardBuilder;
import engine.player.Player;
import gui.MainFrame;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-16
 */
public class Main {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {

          Board board = new Board();
          BoardBuilder builder = new BoardBuilder();
          Player player1 = new Player(board, Alliance.WHITE);
          Player player2 = new Player(board, Alliance.BLACK);
          board.addPlayerWhite(player1);
          board.addPlayerBlack(player2);
          board.setBoardBuilder(builder.createRandomBuild());

          board.setDebugMode(true);
          new MainFrame(board);

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

}
