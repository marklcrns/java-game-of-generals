package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

import engine.Alliance;
import engine.Board;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-25
 */
// Ref: https://www.youtube.com/watch?v=KNGbmsq3huQ
public class MainFrame {

  private Board gameStateBoard;
  private JFrame frame;

  private JButton doneArrangingBtn;
  private JButton startGameBtn;

  private JButton mainMenuStartBtn;
  private JButton mainMenuLoadBtn;
  private JButton mainMenuHowToPlayBtn;
  private JButton mainMenuQuitBtn;

  private JButton menuBarSaveBtn;
  private JButton menuBarLoadBtn;
  private JButton menuBarQuitBtn;
  private JButton menuBarUndoBtn;
  private JButton menuBarRedoBtn;
  private JButton menuBarSurrenderBtn;
  private JButton menuBarGameRulesBtn;

  private JPopupMenu mainMenuQuitPrompt;
  private JPopupMenu menuBarQuitPrompt;

  private BoardPanel boardPanel;
  private MainMenuPanel mainMenuPanel;
  private JLayeredPane layeredPane;
  private JPanel contentPane;

  // private JPanel mainPanel;
  private final static Dimension FRAME_DIMENSION = new Dimension(1200, 835);

  public MainFrame(Board board) {
    this.gameStateBoard = board;
    frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(1, 1, 1, 1));

    layeredPane = new JLayeredPane();
    layeredPane.setPreferredSize(FRAME_DIMENSION);

    boardPanel = new BoardPanel(board);
    mainMenuPanel = new MainMenuPanel();

    fetchMenuBarButtons(boardPanel);
    fetchMainMenuButtons(mainMenuPanel);
    fetchDoneArrangingBtn(boardPanel);
    fetchStartGameBtn(boardPanel);
    addMainMenuButtonsListeners();
    addMenuBarButtonsListeners();
    addDoneArrangingButtonListener();
    addStartGameButtonListener();

    layeredPane.add(boardPanel, new Integer(1));
    layeredPane.add(mainMenuPanel, new Integer(2));

    boardPanel.setBounds(0, 0, FRAME_DIMENSION.width, FRAME_DIMENSION.height);
    mainMenuPanel.setBounds(0, 0, FRAME_DIMENSION.width, FRAME_DIMENSION.height);

    createMainMenuQuitPopupMenu();
    createMenuBarQuitPopupMenu();

    contentPane.add(layeredPane);
    frame.add(contentPane);

    frame.pack();
    frame.setVisible(true);
  }

  private void addMainMenuButtonsListeners() {
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

    mainMenuQuitBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mainMenuQuitPrompt.setVisible(true);
        int quitPromptWidth = mainMenuQuitPrompt.getWidth() / 2;
        int quitPromptHeight = mainMenuQuitPrompt.getHeight() / 2;
        mainMenuQuitPrompt.show(frame, (FRAME_DIMENSION.width / 2) - quitPromptWidth,
                               (FRAME_DIMENSION.height / 2) - quitPromptHeight);
      }
    });
  }

  private void addMenuBarButtonsListeners() {
    menuBarQuitBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        menuBarQuitPrompt.setVisible(true);
        int quitPromptWidth = menuBarQuitPrompt.getWidth() / 2;
        int quitPromptHeight = menuBarQuitPrompt.getHeight() / 2;
        menuBarQuitPrompt.show(frame, (FRAME_DIMENSION.width / 2) - quitPromptWidth,
            (FRAME_DIMENSION.height / 2) - quitPromptHeight);
      }
    });

    menuBarUndoBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (gameStateBoard.getLastExecutedTurn() != 0) {
          boardPanel.undoMoveHistoryUpdate();
          if (gameStateBoard.getMoveMaker() == Alliance.BLACK)
            gameStateBoard.getBlackPlayer().undoLastMove();
          else
            gameStateBoard.getWhitePlayer().undoLastMove();

          boardPanel.refreshBoardPanel();
          frame.repaint();
        }
      }
    });

    menuBarRedoBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (gameStateBoard.getBlackPlayer().getMoveFromHistory(gameStateBoard.getCurrentTurn()) != null) {
          if (gameStateBoard.getMoveMaker() == Alliance.BLACK)
            gameStateBoard.getBlackPlayer().redoLastMove();
          else
            gameStateBoard.getWhitePlayer().redoLastMove();

          boardPanel.redoMoveHistoryUpdate();
          boardPanel.refreshBoardPanel();
          frame.repaint();
        }
      }
    });
  }

  private void addDoneArrangingButtonListener() {
    doneArrangingBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        gameStateBoard.playerDoneArranging();
        boardPanel.clearBoardPanel();
        boardPanel.printOpeningMessage();
      }

    });
  }

  private void addStartGameButtonListener() {
    startGameBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        gameStateBoard.startGame();
        doneArrangingBtn.setVisible(false);
        startGameBtn.setVisible(false);
        boardPanel.clearBoardPanel();
        frame.repaint();
      }

    });
  }

  private void fetchMainMenuButtons(MainMenuPanel mainMenuPanel) {
    mainMenuStartBtn = mainMenuPanel.getStartBtn();
    mainMenuLoadBtn = mainMenuPanel.getLoadBtn();
    mainMenuHowToPlayBtn = mainMenuPanel.getHowToPlayBtn();
    mainMenuQuitBtn = mainMenuPanel.getQuitBtn();
  }

  private void fetchMenuBarButtons(BoardPanel boardPanel) {
    menuBarSaveBtn = boardPanel.getSaveBtn();
    menuBarLoadBtn = boardPanel.getLoadBtn();
    menuBarQuitBtn = boardPanel.getQuitBtn();
    menuBarUndoBtn = boardPanel.getUndoBtn();
    menuBarRedoBtn = boardPanel.getRedoBtn();
    menuBarSurrenderBtn = boardPanel.getSurrenderBtn();
    menuBarGameRulesBtn = boardPanel.getGameRulesBtn();
  }

  private void fetchDoneArrangingBtn(BoardPanel boardPanel) {
    this.doneArrangingBtn = boardPanel.getDoneArrangingBtn();
  }

  private void fetchStartGameBtn(BoardPanel boardPanel) {
    this.startGameBtn = boardPanel.getStartGameBtn();
  }

  private void createMainMenuQuitPopupMenu() {
    mainMenuQuitPrompt = new JPopupMenu();
    mainMenuQuitPrompt.setLayout(new BorderLayout());
    mainMenuQuitPrompt.setBorder(new EmptyBorder(10, 10, 10, 10));

    JLabel quitMessageLbl = new JLabel("Are you sure you want to quit?");
    quitMessageLbl.setFont(new Font("TimesRoman", Font.PLAIN, 20));
    JPanel quitPromptOptionsPanel = new JPanel();
    JButton quitConfirmBtn = new JButton("Yes");
    JButton quitAbortBtn = new JButton("No");

    quitPromptOptionsPanel.add(quitConfirmBtn);
    quitPromptOptionsPanel.add(quitAbortBtn);

    mainMenuQuitPrompt.add(quitMessageLbl, BorderLayout.NORTH);
    mainMenuQuitPrompt.add(quitPromptOptionsPanel, BorderLayout.CENTER);

    quitConfirmBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });

    quitAbortBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mainMenuQuitPrompt.setVisible(false);
      }
    });
  }

  private void createMenuBarQuitPopupMenu() {
    menuBarQuitPrompt = new JPopupMenu();
    menuBarQuitPrompt.setLayout(new BorderLayout());
    menuBarQuitPrompt.setBorder(new EmptyBorder(10, 10, 10, 10));

    JLabel quitMessageLbl = new JLabel("Back to main menu?");
    quitMessageLbl.setFont(new Font("TimesRoman", Font.PLAIN, 20));
    JPanel menuBarQuitPromptOptionsPanel = new JPanel();
    JButton quitBackToMain = new JButton("Back to main");
    JButton quitConfirmBtn = new JButton("Quit");

    menuBarQuitPromptOptionsPanel.add(quitBackToMain);
    menuBarQuitPromptOptionsPanel.add(quitConfirmBtn);

    menuBarQuitPrompt.add(quitMessageLbl, BorderLayout.NORTH);
    menuBarQuitPrompt.add(menuBarQuitPromptOptionsPanel, BorderLayout.CENTER);

    quitConfirmBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });

    quitBackToMain.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        menuBarQuitPrompt.setVisible(false);
        boardPanel.setVisible(false);
        mainMenuPanel.setVisible(true);
        mainMenuPanel.getStartBtn().setText("Continue Game");
      }
    });
  }
}
