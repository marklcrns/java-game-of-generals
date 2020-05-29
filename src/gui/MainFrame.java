package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

import engine.Alliance;
import engine.Board;
import game.Load;
import game.Save;

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

  private JButton menuBarRestartBtn;
  private JButton menuBarLoadBtn;
  private JButton menuBarQuitBtn;
  private JButton menuBarUndoBtn;
  private JButton menuBarRedoBtn;
  private JButton menuBarSaveBtn;
  private JButton menuBarSurrenderBtn;
  private JButton menuBarGameRulesBtn;

  private JDialog mainMenuLoadDialog;
  private JDialog menuBarLoadDialog;
  private JPopupMenu mainMenuQuitPopup;
  private JPopupMenu menuBarQuitPopup;


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

    mainMenuPanel = new MainMenuPanel();

    fetchMainMenuButtons(mainMenuPanel);
    addMainMenuButtonsListeners();
    createMainMenuQuitPopupMenu();
    createMainMenuLoadPopupMenu();

    layeredPane.add(mainMenuPanel, new Integer(2));
    mainMenuPanel.setBounds(0, 0, FRAME_DIMENSION.width, FRAME_DIMENSION.height);


    contentPane.add(layeredPane);
    frame.add(contentPane);

    frame.pack();
    frame.setVisible(true);
  }

  private void addMainMenuButtonsListeners() {
    mainMenuStartBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        gameStateBoard.initGame();
        boardPanel = gameStateBoard.getBoardPanel();
        boardPanel.initBoardPanel();

        fetchMenuBarButtons(boardPanel);
        fetchDoneArrangingBtn(boardPanel);
        fetchStartGameBtn(boardPanel);

        addMenuBarButtonsListeners();
        addDoneArrangingButtonListener();
        addStartGameButtonListener();

        createMenuBarQuitPopupMenu();

        layeredPane.add(boardPanel, new Integer(1));
        boardPanel.setBounds(0, 0, FRAME_DIMENSION.width, FRAME_DIMENSION.height);

        boardPanel.setVisible(true);
        mainMenuPanel.setVisible(false);
      }
    });

    mainMenuLoadBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mainMenuLoadDialog.setVisible(true);
      }
    });

    mainMenuQuitBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mainMenuQuitPopup.setVisible(true);
        int quitPopupWidth = mainMenuQuitPopup.getWidth() / 2;
        int quitPopupHeight = mainMenuQuitPopup.getHeight() / 2;
        mainMenuQuitPopup.show(frame, (FRAME_DIMENSION.width / 2) - quitPopupWidth,
                               (FRAME_DIMENSION.height / 2) - quitPopupHeight);
      }
    });
  }

  private void addMenuBarButtonsListeners() {
    menuBarRestartBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        gameStateBoard.restartGame();
        boardPanel.clearBoardPanel();
        boardPanel.printOpeningMessage();

        menuBarUndoBtn.setVisible(false);
        menuBarRedoBtn.setVisible(false);
        menuBarSaveBtn.setVisible(false);
        menuBarSurrenderBtn.setVisible(false);
        menuBarGameRulesBtn.setVisible(false);

        doneArrangingBtn.setVisible(true);
        startGameBtn.setVisible(true);
        frame.repaint();
      }
    });

    menuBarQuitBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        menuBarQuitPopup.setVisible(true);
        int quitPopupWidth = menuBarQuitPopup.getWidth() / 2;
        int quitPopupHeight = menuBarQuitPopup.getHeight() / 2;
        menuBarQuitPopup.show(frame, (FRAME_DIMENSION.width / 2) - quitPopupWidth,
            (FRAME_DIMENSION.height / 2) - quitPopupHeight);
      }
    });

    menuBarUndoBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (gameStateBoard.getBlackPlayer().getMoveFromHistory(gameStateBoard.getCurrentTurn() - 1) != null) {
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

    menuBarSaveBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (gameStateBoard.isGameStarted())
          new Save(gameStateBoard);
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
        menuBarUndoBtn.setVisible(true);
        menuBarRedoBtn.setVisible(true);
        menuBarSaveBtn.setVisible(true);
        menuBarSurrenderBtn.setVisible(true);
        menuBarGameRulesBtn.setVisible(true);

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
    menuBarRestartBtn = boardPanel.getRestartBtn();
    menuBarLoadBtn = boardPanel.getLoadBtn();
    menuBarQuitBtn = boardPanel.getQuitBtn();
    menuBarUndoBtn = boardPanel.getUndoBtn();
    menuBarRedoBtn = boardPanel.getRedoBtn();
    menuBarSaveBtn = boardPanel.getSaveBtn();
    menuBarSurrenderBtn = boardPanel.getSurrenderBtn();
    menuBarGameRulesBtn = boardPanel.getGameRulesBtn();
  }

  private void fetchDoneArrangingBtn(BoardPanel boardPanel) {
    this.doneArrangingBtn = boardPanel.getDoneArrangingBtn();
  }

  private void fetchStartGameBtn(BoardPanel boardPanel) {
    this.startGameBtn = boardPanel.getStartGameBtn();
  }

  private void createMainMenuLoadPopupMenu() {
    JLabel loadMessageLbl = new JLabel("Load saved game");
    loadMessageLbl.setFont(new Font("TimesRoman", Font.PLAIN, 20));

    String[] saveList  = Load.getSaveList();

    JComboBox<String> loadComboBox = new JComboBox<>(saveList);
    loadComboBox.setSelectedIndex(saveList.length - 1);
    loadComboBox.setEditable(true);

    JPanel loadActionsPanel = new JPanel();
    JButton loadConfirmBtn = new JButton("Load");
    JButton loadAbortBtn = new JButton("Abort");
    loadActionsPanel.add(loadConfirmBtn);
    loadActionsPanel.add(loadAbortBtn);

    // options pane
    // Ref: https://stackoverflow.com/a/40200144/11850077
    Object[] options = new Object[] {};
    JOptionPane loadOptionsPane = new JOptionPane("Load Saved Game",
                                      JOptionPane.PLAIN_MESSAGE,
                                      JOptionPane.DEFAULT_OPTION,
                                      null, options, null);

    loadOptionsPane.add(loadComboBox);
    loadOptionsPane.add(loadActionsPanel);

    mainMenuLoadDialog = new JDialog();
    mainMenuLoadDialog.getContentPane().add(loadOptionsPane);
    mainMenuLoadDialog.pack();
    mainMenuLoadDialog.setVisible(false);


    loadComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // JComboBox<String> cb = (JComboBox<String>) e.getSource();
        // String loadSelected = (String) cb.getSelectedItem();

        // System.out.println(loadSelected);
      }
    });

    loadConfirmBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String loadSelected = (String) loadComboBox.getSelectedItem();

        if (gameStateBoard.isDebugMode())
          System.out.println("Loading " + loadSelected.replace(".txt", "..."));

        new Load(gameStateBoard, loadSelected).loadSaveGame();
        mainMenuLoadDialog.setVisible(false);

        boardPanel = gameStateBoard.getBoardPanel();
        boardPanel.initBoardPanel();

        fetchMenuBarButtons(boardPanel);
        fetchDoneArrangingBtn(boardPanel);
        fetchStartGameBtn(boardPanel);

        addMenuBarButtonsListeners();
        addDoneArrangingButtonListener();
        addStartGameButtonListener();

        createMenuBarQuitPopupMenu();

        layeredPane.add(boardPanel, new Integer(1));
        boardPanel.setBounds(0, 0, FRAME_DIMENSION.width, FRAME_DIMENSION.height);

        boardPanel.setVisible(true);
        mainMenuPanel.setVisible(false);
      }
    });

    loadAbortBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
      }
    });
  }

  private void createMainMenuQuitPopupMenu() {
    mainMenuQuitPopup = new JPopupMenu();
    mainMenuQuitPopup.setLayout(new BorderLayout());
    mainMenuQuitPopup.setBorder(new EmptyBorder(10, 10, 10, 10));

    JLabel quitMessageLbl = new JLabel("Are you sure you want to quit?");
    quitMessageLbl.setFont(new Font("TimesRoman", Font.PLAIN, 20));
    JPanel quitPopupOptionsPanel = new JPanel();
    JButton quitConfirmBtn = new JButton("Yes");
    JButton quitAbortBtn = new JButton("No");

    quitPopupOptionsPanel.add(quitConfirmBtn);
    quitPopupOptionsPanel.add(quitAbortBtn);

    mainMenuQuitPopup.add(quitMessageLbl, BorderLayout.NORTH);
    mainMenuQuitPopup.add(quitPopupOptionsPanel, BorderLayout.CENTER);

    quitConfirmBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });

    quitAbortBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mainMenuQuitPopup.setVisible(false);
      }
    });
  }

  private void createMenuBarQuitPopupMenu() {
    menuBarQuitPopup = new JPopupMenu();
    menuBarQuitPopup.setLayout(new BorderLayout());
    menuBarQuitPopup.setBorder(new EmptyBorder(10, 10, 10, 10));

    JLabel quitMessageLbl = new JLabel("Back to main menu?");
    quitMessageLbl.setFont(new Font("TimesRoman", Font.PLAIN, 20));
    JPanel menuBarQuitPopuptOptionsPanel = new JPanel();
    JButton quitBackToMain = new JButton("Back to main");
    JButton quitConfirmBtn = new JButton("Quit");

    menuBarQuitPopuptOptionsPanel.add(quitBackToMain);
    menuBarQuitPopuptOptionsPanel.add(quitConfirmBtn);

    menuBarQuitPopup.add(quitMessageLbl, BorderLayout.NORTH);
    menuBarQuitPopup.add(menuBarQuitPopuptOptionsPanel, BorderLayout.CENTER);

    quitConfirmBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });

    quitBackToMain.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        menuBarQuitPopup.setVisible(false);
        boardPanel.setVisible(false);
        mainMenuPanel.setVisible(true);
      }
    });
  }
}
