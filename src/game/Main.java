package game;

import javax.swing.SwingUtilities;

import engine.Board;
import engine.Board.BoardBuilder;

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
        builder = builder.createDemoBoardBuild();
        board.buildBoard(builder);
        board.displayBoard();
        System.out.println(board);
      }
    });
  }

}
