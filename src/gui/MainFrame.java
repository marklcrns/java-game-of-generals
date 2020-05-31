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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import engine.Alliance;
import engine.Board;
import game.Load;
import game.Save;

/**
 * The main class that serves as the JFrame that puts the BoardPanel and
 * MainMenuPanel together using JLayeredPane.
 *
 * Author: Mark Lucernas
 * Date: 2020-05-25
 */
public class MainFrame {

  /** Main JFrame dimension */
  private final static Dimension FRAME_DIMENSION = new Dimension(1200, 835);

  /** Reference to Board engine */
  private Board gameStateBoard;

  /** Reference to BoardPanel */
  private BoardPanel boardPanel;

  /** Reference to MainMenuPanel */
  private MainMenuPanel mainMenuPanel;

  /** Reference to main the JFrame JLayeredPanel */
  private JLayeredPane layeredPane;

  /** JPanel that serves as the main JFrame content pane */
  private JPanel contentPane;

  /** The main JFrame */
  private JFrame frame;

  /** MainMenuPanel buttons */
  private JButton doneArrangingBtn, startGameBtn;
  private JButton mainMenuStartBtn, mainMenuLoadBtn,
          mainMenuHowToPlayBtn, mainMenuQuitBtn;

  /** BoardPanel menu bar buttons and labels */
  private JButton menuBarRestartBtn, menuBarLoadBtn, menuBarQuitBtn,
          menuBarUndoBtn, menuBarRedoBtn, menuBarSaveBtn,
          menuBarSurrenderBtn, menuBarGameRulesBtn;
  private JLabel menuBarPlayerBlackLbl, menuBarPlayerWhiteLbl;

  /** Stores Player names */
  private String playerBlackName, playerWhiteName;

  /** JDialog as alternative popup menus */
  private JDialog playerAssignDialog, mainMenuLoadDialog, menuBarLoadDialog;

  /** Pop menu prompt for quit buttons */
  private JPopupMenu mainMenuQuitPopup, menuBarQuitPopup;

  /**
   * MainFrame constructor that takes in engine Board class.
   */
  public MainFrame(Board board) {
    this.gameStateBoard = board;
    frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(1, 1, 1, 1));

    layeredPane = new JLayeredPane();
    layeredPane.setPreferredSize(FRAME_DIMENSION);

    // Fetch main menu panel and all its components into this frame then add
    // listeners.
    mainMenuPanel = new MainMenuPanel();
    fetchMainMenuButtons(mainMenuPanel);
    addMainMenuButtonsListeners();
    createMainMenuQuitPopupMenu();
    createMainMenuLoadPopupMenu();

    // Add main menu at the top of the JLayeredPane
    layeredPane.add(mainMenuPanel, new Integer(2));
    mainMenuPanel.setBounds(0, 0, FRAME_DIMENSION.width, FRAME_DIMENSION.height);

    // Add everything into the frame.
    contentPane.add(layeredPane);
    frame.add(contentPane);
    frame.pack();
    frame.setVisible(true);
  }

  /**
   * Adds each main menu buttons a listeners.
   */
  private void addMainMenuButtonsListeners() {

    // Main menu start button action listener to hide main menu and load board panel.
    mainMenuStartBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Initialize game and load BoardPanel.
        gameStateBoard.initGame();
        boardPanel = gameStateBoard.getBoardPanel();
        boardPanel.initBoardPanel();

        // Fetch and add listeners to all BoardPanel buttons.
        fetchMenuBarComponents(boardPanel);
        fetchDoneArrangingBtn(boardPanel);
        fetchStartGameBtn(boardPanel);

        addMenuBarButtonsListeners();
        addDoneArrangingButtonListener();
        addStartGameButtonListener();

        createPlayerNameAssignDialog();
        createMenuBarQuitPopupMenu();

        // Add BoardPanel into JLayeredPane below the MainMenuPanel.
        layeredPane.add(boardPanel, new Integer(1));
        boardPanel.setBounds(0, 0, FRAME_DIMENSION.width, FRAME_DIMENSION.height);

        // Hide MainMenuPanel and set BoardPanel visible.
        mainMenuPanel.setVisible(false);
        boardPanel.setVisible(true);
        playerAssignDialog.setVisible(true);
      }
    });

    // Load button action listener.
    mainMenuLoadBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mainMenuLoadDialog.setVisible(true);
      }
    });

    // Main menu quit button action listener to show quit dialog.
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

    mainMenuHowToPlayBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // TODO: implement.
      }
    });
  }

  /**
   * Adds all BoardPanel menu bar buttons event listeners.
   */
  private void addMenuBarButtonsListeners() {

    // Restart button action listener.
    menuBarRestartBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        // Restart board engine and clear board panel.
        gameStateBoard.restartGame();
        boardPanel.clearBoardPanel();
        boardPanel.printOpeningMessage();

        // Go into arrange mode
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

    // Menu bar quit button action listener.
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

    // Undo button action listener that undo most recent move execution.
    menuBarUndoBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (gameStateBoard.getBlackPlayer().getMoveFromHistory(gameStateBoard.getCurrentTurn() - 1) != null) {
          boardPanel.undoMoveHistoryUpdate();
          if (gameStateBoard.getMoveMaker() == Alliance.BLACK)
            gameStateBoard.getBlackPlayer().undoLastMove();
          else
            gameStateBoard.getWhitePlayer().undoLastMove();

          boardPanel.repaintBoardPanel();
          frame.repaint();
        }
      }
    });

    // Redo button action listener that redo move execution
    menuBarRedoBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (gameStateBoard.getBlackPlayer().getMoveFromHistory(gameStateBoard.getCurrentTurn()) != null) {
          if (gameStateBoard.getMoveMaker() == Alliance.BLACK)
            gameStateBoard.getBlackPlayer().redoLastMove();
          else
            gameStateBoard.getWhitePlayer().redoLastMove();

          boardPanel.redoMoveHistoryUpdate();
          boardPanel.repaintBoardPanel();
          frame.repaint();
        }
      }
    });

    // Save button action listener that saves current game state into text file.
    menuBarSaveBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (gameStateBoard.isGameStarted())
          new Save(gameStateBoard);
      }
    });
  }

  /**
   * Adds BoardPanel done arranging button an action listener. This switches the
   * current move maker to the opposing Player. Will remain in arrange mode
   * until the game starts..
   */
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

  /**
   * Adds BoardPanel start game button an cation listener. When triggered, game
   * would no longer be in arrange mode and starts making turns.
   */
  private void addStartGameButtonListener() {
    startGameBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Shows in-game menu buttons.
        menuBarUndoBtn.setVisible(true);
        menuBarRedoBtn.setVisible(true);
        menuBarSaveBtn.setVisible(true);
        menuBarSurrenderBtn.setVisible(true);
        menuBarGameRulesBtn.setVisible(true);
        boardPanel.setPlayerNamesVisibility(true);

        // Start game
        gameStateBoard.startGame();
        doneArrangingBtn.setVisible(false);
        startGameBtn.setVisible(false);
        boardPanel.clearBoardPanel();
        frame.repaint();
      }

    });
  }

  /**
   * Fetch all MainMenuPanel buttons.
   * @param mainMenuPanel the MainMenuPanel instance.
   */
  private void fetchMainMenuButtons(MainMenuPanel mainMenuPanel) {
    mainMenuStartBtn = mainMenuPanel.getStartBtn();
    mainMenuLoadBtn = mainMenuPanel.getLoadBtn();
    mainMenuHowToPlayBtn = mainMenuPanel.getHowToPlayBtn();
    mainMenuQuitBtn = mainMenuPanel.getQuitBtn();
  }

  /**
   * Fetch all BoardPanel inner MenuBarPanel buttons.
   * @param boardPanel the BoardPanel instance.
   */
  private void fetchMenuBarComponents(BoardPanel boardPanel) {
    menuBarPlayerBlackLbl = boardPanel.getPlayerBlackNameLbl();
    menuBarPlayerWhiteLbl = boardPanel.getPlayerWhiteNameLbl();
    menuBarRestartBtn = boardPanel.getRestartBtn();
    menuBarLoadBtn = boardPanel.getLoadBtn();
    menuBarQuitBtn = boardPanel.getQuitBtn();
    menuBarUndoBtn = boardPanel.getUndoBtn();
    menuBarRedoBtn = boardPanel.getRedoBtn();
    menuBarSaveBtn = boardPanel.getSaveBtn();
    menuBarSurrenderBtn = boardPanel.getSurrenderBtn();
    menuBarGameRulesBtn = boardPanel.getGameRulesBtn();
  }

  /**
   * Fetch BoardPanel inner MoveHistoryPanel done arranging button.
   * @param boardPanel the BoardPanel instance.
   */
  private void fetchDoneArrangingBtn(BoardPanel boardPanel) {
    this.doneArrangingBtn = boardPanel.getDoneArrangingBtn();
  }

  /**
   * Fetch BoardPanel inner MoveHistoryPanel start game button.
   * @param boardPanel the BoardPanel instance.
   */
  private void fetchStartGameBtn(BoardPanel boardPanel) {
    this.startGameBtn = boardPanel.getStartGameBtn();
  }

  /**
   * Create MainMenuPanel load button pop up menu. Uses JDialog instead of
   * JPopupMenu.
   */
  private void createMainMenuLoadPopupMenu() {
    JLabel loadMessageLbl = new JLabel("Load saved game");
    loadMessageLbl.setFont(new Font("TimesRoman", Font.PLAIN, 20));

    // Get all existing saved game.
    String[] saveList  = Load.getSaveList();

    // Append all save list Strings into JComboBox.
    JComboBox<String> loadComboBox = new JComboBox<>(saveList);
    loadComboBox.setSelectedIndex(saveList.length - 1);
    loadComboBox.setEditable(true);

    // Load actions panel for confirm and abort buttons.
    JPanel loadActionsPanel = new JPanel();
    JButton loadConfirmBtn = new JButton("Load");
    JButton loadAbortBtn = new JButton("Abort");
    loadActionsPanel.add(loadConfirmBtn);
    loadActionsPanel.add(loadAbortBtn);

    // Load options pane
    // Ref: https://stackoverflow.com/a/40200144/11850077
    Object[] options = new Object[] {};
    JOptionPane loadOptionsPane = new JOptionPane(loadMessageLbl,
                                      JOptionPane.PLAIN_MESSAGE,
                                      JOptionPane.DEFAULT_OPTION,
                                      null, options, null);

    // Add combo box and actions panel into options pane.
    loadOptionsPane.add(loadComboBox);
    loadOptionsPane.add(loadActionsPanel);

    // initialize load JDialog and mount the options pane
    mainMenuLoadDialog = new JDialog();
    mainMenuLoadDialog.getContentPane().add(loadOptionsPane);
    mainMenuLoadDialog.pack();
    mainMenuLoadDialog.setVisible(false);

    // Adds confirm button action listener to selected saved game from combo box.
    loadConfirmBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String loadSelected = (String) loadComboBox.getSelectedItem();

        if (gameStateBoard.isDebugMode())
          System.out.println("Loading " + loadSelected.replace(".txt", "..."));

        // TODO: reimplement to be able to pass into Board as argument.
        // Load selected saved game.
        new Load(gameStateBoard, loadSelected).loadSaveGame();
        mainMenuLoadDialog.setVisible(false);

        // Initialize board panel.
        boardPanel = gameStateBoard.getBoardPanel();
        boardPanel.initBoardPanel();

        // Fetch BoardPanel menu bar buttons and add listeners
        fetchMenuBarComponents(boardPanel);
        fetchDoneArrangingBtn(boardPanel);
        fetchStartGameBtn(boardPanel);

        addMenuBarButtonsListeners();
        addDoneArrangingButtonListener();
        addStartGameButtonListener();

        createMenuBarQuitPopupMenu();

        // Add BoardPanel to JLayeredPane and hide MainManuPanel
        layeredPane.add(boardPanel, new Integer(1));
        boardPanel.setBounds(0, 0, FRAME_DIMENSION.width, FRAME_DIMENSION.height);
        mainMenuPanel.setVisible(false);
        boardPanel.setVisible(true);
      }
    });

    // Adds abort button action listener.
    loadAbortBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
      }
    });
  }

  /**
   * Creates MainMenuPanel quit button popup menu for confirmation.
   */
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

    // Adds confirm button action listener to exit program.
    quitConfirmBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });

    // Adds confirm button to hide popup menu.
    quitAbortBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mainMenuQuitPopup.setVisible(false);
      }
    });
  }

  /**
   * Creates player assignment dialog for MainMenuPanel start game button.
   */
  public void createPlayerNameAssignDialog() {
    JLabel playerAssignLbl = new JLabel("Please assign player names");
    playerAssignLbl.setFont(new Font("TimesRoman", Font.PLAIN, 20));

    JPanel playerBlackPanel = new JPanel();
    JPanel playerWhitePanel = new JPanel();
    menuBarPlayerBlackLbl = new JLabel("Black Player");
    menuBarPlayerWhiteLbl = new JLabel("White Player");

    JTextField playerBlackTextField = new JTextField(8);
    playerBlackPanel.add(menuBarPlayerBlackLbl);
    playerBlackPanel.add(playerBlackTextField);

    JTextField playerWhiteTextField = new JTextField(8);
    playerWhitePanel.add(menuBarPlayerWhiteLbl);
    playerWhitePanel.add(playerWhiteTextField);

    JPanel playerAssignActionsPanel = new JPanel();
    JButton playerAssignConfirmBtn = new JButton("Confirm");
    JButton playerAssignAbortBtn = new JButton("Abort");
    playerAssignActionsPanel.add(playerAssignConfirmBtn);
    playerAssignActionsPanel.add(playerAssignAbortBtn);

    // options pane
    // Ref: https://stackoverflow.com/a/40200144/11850077
    Object[] options = new Object[] {};
    JOptionPane playerAssignOptionsPane = new JOptionPane(playerAssignLbl,
                                      JOptionPane.PLAIN_MESSAGE,
                                      JOptionPane.DEFAULT_OPTION,
                                      null, options, null);

    playerAssignOptionsPane.add(playerBlackPanel);
    playerAssignOptionsPane.add(playerWhitePanel);
    playerAssignOptionsPane.add(playerAssignActionsPanel);

    playerAssignDialog = new JDialog();
    playerAssignDialog.getContentPane().add(playerAssignOptionsPane);
    playerAssignDialog.pack();
    playerAssignDialog.setVisible(false);

    // Adds confirm button action listener to pass along the input players name
    // to the board engine and board panel to update the board.
    playerAssignConfirmBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        playerBlackName = playerBlackTextField.getText();
        playerWhiteName = playerWhiteTextField.getText();

        if (gameStateBoard.isDebugMode()) {
          System.out.println("Black player: " + playerBlackName + " registered");
          System.out.println("White player: " + playerWhiteName + " registered");
        }

        // Sets player names in both Board and BoardPanel.
        gameStateBoard.setBlackPlayerName(playerBlackName);
        gameStateBoard.setWhitePlayerName(playerWhiteName);
        boardPanel.getPlayerBlackNameLbl().setText(
            "BLACK PLAYER: " + playerBlackName);
        boardPanel.getPlayerWhiteNameLbl().setText(
            "WHITE PLAYER: " + playerWhiteName);

        // Hide player assign dialog and update opening message to greet the
        // move maker player with the input name.
        playerAssignDialog.setVisible(false);
        boardPanel.clearBoardPanel();
        boardPanel.printOpeningMessage();
      }
    });

    // Adds abort button action listener to exit back to main menu.
    playerAssignAbortBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boardPanel.setVisible(false);
        mainMenuPanel.setVisible(true);
        playerAssignDialog.setVisible(false);
      }
    });
  }

  // Creates BoardPanel quit button pop up menu confirmation.
  private void createMenuBarQuitPopupMenu() {
    menuBarQuitPopup = new JPopupMenu();
    menuBarQuitPopup.setLayout(new BorderLayout());
    menuBarQuitPopup.setBorder(new EmptyBorder(10, 10, 10, 10));

    JLabel quitMessageLbl = new JLabel("Back to main menu?");
    quitMessageLbl.setFont(new Font("TimesRoman", Font.PLAIN, 20));
    JPanel menuBarQuitPopupOptionsPanel = new JPanel();
    JButton quitBackToMain = new JButton("Back to main");
    JButton quitConfirmBtn = new JButton("Quit");

    menuBarQuitPopupOptionsPanel.add(quitBackToMain);
    menuBarQuitPopupOptionsPanel.add(quitConfirmBtn);

    menuBarQuitPopup.add(quitMessageLbl, BorderLayout.NORTH);
    menuBarQuitPopup.add(menuBarQuitPopupOptionsPanel, BorderLayout.CENTER);

    // Adds confirm button action listener to exit the program.
    quitConfirmBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });

    // Adds back to main action listener to exit back to main menu.
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
