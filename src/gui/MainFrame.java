package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
  private BoardPanel boardPanel;
  private MainMenuPanel mainMenuPanel;
  private JLayeredPane layeredPane;
  private JPanel contentPane;

  // private JPanel mainPanel;
  private final static Dimension FRAME_DIMENSION = new Dimension(1200, 835);

  public MainFrame(Board board) {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(1, 1, 1, 1));

    layeredPane = new JLayeredPane();
    layeredPane.setPreferredSize(FRAME_DIMENSION);

    boardPanel = new BoardPanel(board);
    mainMenuPanel = new MainMenuPanel();

    fetchMainMenuButtons(mainMenuPanel);
    addButtonEventListeners();

    layeredPane.add(boardPanel, new Integer(1));
    layeredPane.add(mainMenuPanel, new Integer(2));

    boardPanel.setBounds(0, 0, FRAME_DIMENSION.width, FRAME_DIMENSION.height);
    mainMenuPanel.setBounds(0, 0, FRAME_DIMENSION.width, FRAME_DIMENSION.height);

    contentPane.add(layeredPane);
    this.add(contentPane);
  }

  private void addButtonEventListeners() {
    mainMenuStartBtn.addActionListener(new ActionListener() {

    @Override
    public void actionPerformed(ActionEvent e) {
      boardPanel.setVisible(true);
      mainMenuPanel.setVisible(false);

      // or
      // layeredPane.removeAll();
      // layeredPane.add(boardPanel);
    }

    });
  }

  private void fetchMainMenuButtons(MainMenuPanel mainMenu) {
    mainMenuStartBtn = mainMenu.getStartButton();
    // mainMenuStartBtn.setFocusable(false);
  }
}
