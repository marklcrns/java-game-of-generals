package game;

import javax.swing.SwingUtilities;

import engine.Alliance;
import engine.Board;
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
          Player player1 = new Player(Alliance.WHITE);
          Player player2 = new Player(Alliance.BLACK);
          board.setPlayerWhite(player1);
          board.setPlayerBlack(player2);

          board.setDebugMode(true);
          new MainFrame(board);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

}
