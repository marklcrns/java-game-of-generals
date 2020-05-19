package gui;

import javax.swing.SwingUtilities;

import engine.Board;

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
        GUI gui  = new GUI(board);
      }
    });
  }

}
