package gui;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import engine.Board;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-25
 */
// Ref: https://www.youtube.com/watch?v=KNGbmsq3huQ
public class MainFrame extends JFrame {

  private JButton mainMenuStartBtn;

  // private JPanel mainPanel;
  private final static Dimension FRAME_DIMENSION = new Dimension(1200, 835);

  public MainFrame(Board board) {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(1, 1, 1, 1));
    JLayeredPane layeredPane = new JLayeredPane();
    layeredPane.setPreferredSize(FRAME_DIMENSION);

    BoardPanel boardPanel = new BoardPanel(board);
    MainMenuPanel mainMenuPanel = new MainMenuPanel();
    fetchMainMenuButtons(mainMenuPanel);

    layeredPane.add(boardPanel, new Integer(2));
    layeredPane.add(mainMenuPanel, new Integer(1));

    boardPanel.setBounds(0, 0, FRAME_DIMENSION.width, FRAME_DIMENSION.height);
    mainMenuPanel.setBounds(0, 0, FRAME_DIMENSION.width, FRAME_DIMENSION.height);

    contentPane.add(layeredPane);
    this.add(contentPane);
  }

  private void fetchMainMenuButtons(MainMenuPanel mainMenu) {
    mainMenuStartBtn = mainMenu.getStartButton();
  }
}
