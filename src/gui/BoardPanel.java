package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import engine.Alliance;
import engine.Board;
import engine.Move;
import engine.Board.Tile;
import engine.pieces.Piece;
import engine.player.Player;
import utils.BoardUtils;

/**
 * Board JPanel that displays the board game and all its components.
 *
 * Author: Mark Lucernas
 * Date: 2020-05-16
 */
public class BoardPanel extends JPanel {

  /** Reference to the Board engine */
  private static Board gameStateBoard;

  /** Menu bar and other buttons */
  private static JButton restartBtn, loadBtn, quitBtn, undoBtn,
                         redoBtn, saveBtn, surrenderBtn, rulesBtn,
                         doneArrangingBtn, startGameBtn;

  /** Player names assigned once game initialized. */
  private static JLabel playerBlackNameLbl, playerWhiteNameLbl;

  /** Reference to the menu bar JPanel inner class */
  private static MenuBarPanel menuBarPanel;

  /** Reference to the move history JPanel inner class */
  private static MoveHistoryPanel moveHistoryPanel;

  /** Reference to the inner board JPanel inner class */
  private static InnerBoardPanel boardPanel;

  /** Directory path to the piece images */
  private final static String PIECE_IMAGES_PATH = "art/pieces/original/";

  /** Default TilePanel colors  */
  private final static Color DARK_TILE_COLOR = new Color(50, 50, 50);
  private final static Color LIGHT_TILE_COLOR = new Color(200, 200, 200);

  /** TilePanel highlight colors */
  private final static Color ENEMY_TILE_COLOR = new Color(120, 0, 0);
  private final static Color VALID_TILE_COLOR = new Color(170, 210, 240);
  private final static Color INVALID_TILE_COLOR = new Color(125, 125, 125);
  private final static Color ACTIVE_TILE_COLOR = new Color(230, 210, 25);

  /** Board panel component dimensions */
  private final static Dimension FRAME_DIMENSION = new Dimension(1200, 835);
  private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(900, 800);
  private final static Dimension TILE_PANEL_DIMENSION = new Dimension(100, 100);
  private final static Dimension MENU_BAR_PANEL_DIMENSION =
    new Dimension(
      (int) FRAME_DIMENSION.getWidth(),
      (int) (FRAME_DIMENSION.getHeight() - BOARD_PANEL_DIMENSION.getHeight()));
  private final static Dimension MOVE_HISTORY_PANEL_DIMENSION =
    new Dimension(
      (int) (FRAME_DIMENSION.getWidth() - BOARD_PANEL_DIMENSION.getWidth()),
      (int) (FRAME_DIMENSION.getHeight() - MENU_BAR_PANEL_DIMENSION.getHeight()));

  /**
   * Constructor method that takes in Board engine as parameter.
   */
  public BoardPanel(final Board board) {
    this.gameStateBoard = board;
    this.setLayout(new BorderLayout());
    this.setVisible(false);

    // Initialize inner classes.
    boardPanel = new InnerBoardPanel();
    menuBarPanel = new MenuBarPanel();
    moveHistoryPanel = new MoveHistoryPanel();

    this.add(boardPanel, BorderLayout.CENTER);
    this.add(menuBarPanel, BorderLayout.NORTH);
    this.add(moveHistoryPanel, BorderLayout.WEST);
  }

  /**
   * Initialize BoardPanel by initializing InnerBoardPanel class.
   */
  public void initBoardPanel() {
    System.out.println("BoardPanel gameStateBoard:\n" + gameStateBoard);
    boardPanel.initInnerBoardPanel();
  }

  /**
   * Gets black player name JLabel
   * @return JLabel playerBlackNameLbl field.
   */
  public final JLabel getPlayerBlackNameLbl() {
    return playerBlackNameLbl;
  }

  /**
   * Gets white player name JLabel
   * @return JLabel playerWhiteNameLbl field.
   */
  public final JLabel getPlayerWhiteNameLbl() {
    return playerWhiteNameLbl;
  }

  /**
   * Set the visibility of the Player names in the menu bar panel.
   */
  public void setPlayerNamesVisibility(final boolean visibility) {
    playerBlackNameLbl.setVisible(visibility);
    playerWhiteNameLbl.setVisible(visibility);
  }

  /**
   * Gets the restart button of the menu bar panel.
   * @return JButton restartBtn MenuBarPanel field.
   */
  public final JButton getRestartBtn() {
    return restartBtn;
  }

  /**
   * Gets the load button of the menu bar panel.
   * @return JButton loadBtn MenuBarPanel field.
   */
  public final JButton getLoadBtn() {
    return loadBtn;
  }

  /**
   * Gets the quit button of the menu bar panel.
   * @return JButton quitBtn MenuBarPanel field.
   */
  public final JButton getQuitBtn() {
    return quitBtn;
  }

  /**
   * Gets the quit button of the menu bar panel.
   * @return JButton quitBtn MenuBarPanel field.
   */
  public final JButton getUndoBtn() {
    return undoBtn;
  }

  /**
   * Gets the redo button of the menu bar panel.
   * @return JButton redoBtn MenuBarPanel field.
   */
  public final JButton getRedoBtn() {
    return redoBtn;
  }

  /**
   * Gets the save button of the menu bar panel.
   * @return JButton saveBtn MenuBarPanel field.
   */
  public final JButton getSaveBtn() {
    return saveBtn;
  }

  /**
   * Gets the surrender button of the menu bar panel.
   * @return JButton surrenderBtn MenuBarPanel field.
   */
  public final JButton getSurrenderBtn() {
    return surrenderBtn;
  }

  /**
   * Gets the game rules button of the menu bar panel.
   * @return JButton gameRulesBtn MenuBarPanel field.
   */
  public final JButton getGameRulesBtn() {
    return rulesBtn;
  }

  /**
   * Gets the done arranging button of the move history panel.
   * @return JButton doneArrangingBtn MoveHistoryPanel field.
   */
  public final JButton getDoneArrangingBtn() {
    return doneArrangingBtn;
  }

  /**
   * Gets the start game button of the move history panel.
   * @return JButton startGameBtn MoveHistoryPanel field.
   */
  public final JButton getStartGameBtn() {
    return startGameBtn;
  }

  /**
   * Repaints BoardPanel colors and sets active tile to default null -1.
   */
  public final void repaintBoardPanel() {
    boardPanel.setActiveTile(-1);
    boardPanel.refreshInnerBoardPanelIcons();
    boardPanel.refreshTilesBackgroundColor();
  }

  /**
   * Repaints board panel and clear move history panel text area move entries.
   */
  public final void clearBoardPanel() {
    repaintBoardPanel();
    moveHistoryPanel.clearMoveHistory();
  }

  /**
   * Removes last executed move entry from move history text area.
   */
  public final void undoMoveHistoryUpdate() {
    moveHistoryPanel.removeMoveFromHistory(gameStateBoard.getLastMove());
  }

  /**
   * Reprint move entry executed by redo from move history text area.
   */
  public final void redoMoveHistoryUpdate() {
    moveHistoryPanel.appendToMoveHistory(gameStateBoard.getLastMove());
  }

  /**
   * Print opening message in move history text area.
   */
  public final void printOpeningMessage() {
    if (!gameStateBoard.isGameStarted())
      moveHistoryPanel.printOpeningMessage();
  }

  /**
   * BoardPanel Inner JPanel for menu bar.
   */
  public class MenuBarPanel extends JPanel {

    /** Menu bar buttons */
    private final JButton restart, load, quit, undo, redo, save, surrender, rules;

    /** Player names label */
    private final JLabel playerBlackName, playerWhiteName;

    /**
     * Constructor method that initializes this MenuBarPanel and all its components
     */
    public MenuBarPanel() {
      this.setLayout(new FlowLayout());
      this.setPreferredSize(MENU_BAR_PANEL_DIMENSION);

      playerBlackName = new JLabel("BLACK PLAYER:");
      playerBlackName.setFont(new Font("SansSerif", Font.BOLD, 16));
      playerBlackName.setVisible(false);
      this.add(playerBlackName);

      this.add(new JSeparator(SwingConstants.HORIZONTAL));
      this.add(new JSeparator(SwingConstants.HORIZONTAL));

      restart = new JButton("Restart");
      this.add(restart);
      load = new JButton("Load");
      this.add(load);
      quit = new JButton("Quit");
      this.add(quit);

      this.add(new JSeparator(SwingConstants.HORIZONTAL));
      this.add(new JSeparator(SwingConstants.HORIZONTAL));
      this.add(new JSeparator(SwingConstants.HORIZONTAL));

      undo = new JButton("Undo");
      this.add(undo);
      undo.setVisible(false);
      redo = new JButton("Redo");
      this.add(redo);
      redo.setVisible(false);

      this.add(new JSeparator(SwingConstants.HORIZONTAL));
      this.add(new JSeparator(SwingConstants.HORIZONTAL));
      this.add(new JSeparator(SwingConstants.HORIZONTAL));

      save = new JButton("Save");
      this.add(save);
      save.setVisible(false);
      surrender = new JButton("Surrender");
      this.add(surrender);
      surrender.setVisible(false);
      rules = new JButton("Game Rules");
      this.add(rules);
      rules.setVisible(false);

      this.add(new JSeparator(SwingConstants.HORIZONTAL));
      this.add(new JSeparator(SwingConstants.HORIZONTAL));

      playerWhiteName = new JLabel("WHITE PLAYER:");
      playerWhiteName.setFont(new Font("SansSerif", Font.BOLD, 16));
      playerWhiteName.setVisible(false);
      this.add(playerWhiteName);

      setAllButtons();
    }

    /**
     * Fetch and hoist all menu bar buttons and labels up the parent BoardPanel
     * class for easy access.
     */
    public void setAllButtons() {
      playerBlackNameLbl = this.playerBlackName;
      playerWhiteNameLbl = this.playerWhiteName;
      restartBtn = this.restart;
      loadBtn = this.load;
      quitBtn = this.quit;
      undoBtn = this.undo;
      redoBtn = this.redo;
      saveBtn = this.save;
      surrenderBtn = this.surrender;
      rulesBtn = this.rules;
    }

  } // MenuBarPanel

  /**
   * BoardPanel inner JPanel class for move history
   */
  private class MoveHistoryPanel extends JPanel {

    /** Move history text area */
    private final JTextArea moveHistoryTextArea = new JTextArea();

    /** Move history opening message string */
    private String openingMessage;

    /**
     * Constructor method that initializes this MoveHistoryPanel.
     */
    public MoveHistoryPanel() {
      this.setLayout(new BorderLayout());
      this.setBorder(new EmptyBorder(0, 0, 0, 5));
      this.setPreferredSize(MOVE_HISTORY_PANEL_DIMENSION);
      moveHistoryTextArea.setEditable(false);

      final JLabel label = new JLabel("MOVE HISTORY");
      label.setHorizontalAlignment(JLabel.CENTER);
      label.setVerticalAlignment(JLabel.CENTER);
      label.setFont(new Font("SansSerif", Font.BOLD, 18));

      final JButton doneArrangingBtn = new JButton("Done Arranging");
      final JButton startGameBtn = new JButton("Start Game");
      setDoneArrangingBtn(doneArrangingBtn);
      setStartGameBtn(startGameBtn);

      final JPanel initGameButtonsPanel = new JPanel();
      initGameButtonsPanel.add(doneArrangingBtn);
      initGameButtonsPanel.add(startGameBtn);

      moveHistoryTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      moveHistoryTextArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

      // set scrollable vertically as needed
      final JScrollPane moveHistoryVScrollable = new JScrollPane(moveHistoryTextArea);
      moveHistoryVScrollable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

      this.add(label, BorderLayout.NORTH);
      this.add(moveHistoryVScrollable, BorderLayout.CENTER);
      this.add(initGameButtonsPanel, BorderLayout.SOUTH);

      printOpeningMessage();
    }

    /**
     * Appends String into move history text area.
     * @param text texts to append into text area.
     */
    public void appendTextToMoveHistory(final String text) {
      moveHistoryTextArea.append(text);
    }

    /**
     * Converts Move into string and appends to move history text area
     * @param move Move to append into text area.
     */
    public void appendToMoveHistory(final Move move) {
      moveHistoryTextArea.append(convertMoveToString(move));
    }

    /**
     * Converts Move to String object to append into move history text area.
     * @param move Move to be converted into String object.
     * @return String version of the move instance.
     */
    public String convertMoveToString(final Move move) {
      String moveString = "";

      if (move.getMoveType() == "aggressive") {
        final Alliance superiorPieceAlliance =
          move.getEliminatedPiece().getPieceAlliance() ==
          Alliance.BLACK ? Alliance.WHITE : Alliance.BLACK;

        moveString = "\nTurn " + move.getTurnId() + ": " + move.getOriginCoords() +
                   " to " + move.getDestinationCoords() + " " + superiorPieceAlliance;
      } else if (move.getMoveType() == "draw") {
        moveString = "\nTurn " + move.getTurnId() + ": " + move.getOriginCoords() +
                   " to " + move.getDestinationCoords() + " DRAW";
      } else if (move.getMoveType() == "normal") {
        moveString = "\nTurn " + move.getTurnId() + ": " + move.getOriginCoords() +
                   " to " + move.getDestinationCoords();
      } else {
        // TODO: Do not print INVALID moves in move history text area.
        // Instead show popup or flash board color.
        moveString = "\nTurn " + move.getTurnId() + ": " + move.getOriginCoords() +
                   " to " + move.getDestinationCoords() + " INVALID MOVE";
      }

      return moveString;
    }

    /**
     * Removes specific Move entry from move history text area.
     * @param move move entry to be removed from move history text area.
     */
    // TODO: Fix only removing invalid moves
    public void removeMoveFromHistory(final Move move) {
      if (move != null) {
        removeRecentInvalidMove();

        final String fullMoveHistory = moveHistoryTextArea.getText();
        final String moveString = convertMoveToString(move);

        clearMoveHistory();
        appendTextToMoveHistory(fullMoveHistory.replace(moveString, ""));
      }
    }

    /**
     * Removes invalid Move entry/entries from move history text area.
     */
    public void removeRecentInvalidMove() {
      if (gameStateBoard.getLastInvalidMove() != null) {
        final String fullMoveHistory = moveHistoryTextArea.getText();
        final String invalidMoveString = convertMoveToString(gameStateBoard.getLastInvalidMove());

        clearMoveHistory();
        appendTextToMoveHistory(fullMoveHistory.replace(invalidMoveString, ""));
      }
    }

    /**
     * Clears all text from move history text area.
     */
    public void clearMoveHistory() {
      moveHistoryTextArea.selectAll();
      moveHistoryTextArea.replaceSelection("");
    }

    /**
     * Sets or hoist done arranging button up to parent BoardPanel class for
     * easy access.
     */
    public void setDoneArrangingBtn(final JButton doneArranging) {
      doneArrangingBtn = doneArranging;
    }

    /**
     * Sets or hoist start game button up to parent BoardPanel class for
     * easy access.
     */
    public void setStartGameBtn(final JButton startGame) {
      startGameBtn = startGame;
    }

    /**
     * Clears move history text area and print opening message.
     */
    public void printOpeningMessage() {
      clearMoveHistory();
      String name = "Player";

      // Get current move maker Player name
      final Alliance moveMaker = gameStateBoard.getMoveMaker();

      if (gameStateBoard.getBlackPlayerName() != null && moveMaker == Alliance.BLACK)
        name = gameStateBoard.getBlackPlayerName();
      else if (gameStateBoard.getWhitePlayerName() != null && moveMaker == Alliance.WHITE)
        name = gameStateBoard.getWhitePlayerName();

      openingMessage = "Welcome " + name +
        ",\nto the Game of the Generals!\n\n" +
        "Please arrange your pieces however\n" +
        "you like within your territory (" +
        gameStateBoard.getMoveMaker() + ").\n\n" +
        "Once you are ready, please click the\n" +
        "'Start Game' button below.\n";

      moveHistoryTextArea.append(openingMessage);
    }

  } // MoveHistoryPanel

  /**
   * BoardPanel inner JPanel class that displays the inner board panel.
   */
  private class InnerBoardPanel extends JPanel {

    /** List of all TilePanel inner class instance. */
    private final List<TilePanel> boardTiles;

    /** List of all candidate move tiles of active piece tile. */
    private final List<Integer> candidateMoveTiles;

    /** HashMap of all current active piece Moves. */
    private Map<String, Move> currentPieceMoves;

    /** HasMap of all pre-loaded black pieces icons. */
    private Map<String, Image> blackPieceIcons;
    private Map<String, Image> whitePieceIcons;

    /** Hover highlight switch for highlighting candidate piece moves. */
    private boolean enableHoverHighlight = true;

    /** Hovered tile ID. */
    private int hoveredTileId;

    /** Current active tile ID. -1 if no active */
    private int activeTileId = -1;

    /**
     * Constructor method of InnerBoardPanel.
     */
    public InnerBoardPanel() {
      super(new GridLayout(BoardUtils.TILE_ROW_COUNT, BoardUtils.TILE_COLUMN_COUNT));
      this.boardTiles = new ArrayList<>();
      this.candidateMoveTiles = new ArrayList<>();

      preLoadImages();
      createTilePanels();

      setPreferredSize(BOARD_PANEL_DIMENSION);
      validate();
    }

    /**
     * Creates all TilePanel that will contain piece icons.
     */
    public void createTilePanels() {
      for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
        final TilePanel tilePanel = new TilePanel(i);
        this.boardTiles.add(tilePanel);
        add(tilePanel);
      }
    }

    /**
     * Method that initializes InnerBoardPanel by initializing all TilePanels.
     */
    public void initInnerBoardPanel() {
      for (int i = 0; i < boardTiles.size(); i++) {
        this.boardTiles.get(i).initTilePanel();
      }
    }

    /**
     * Highlights Tiles based on current mouse hovered piece candidate Moves
     * and move types. Also sets all highlighted tiles as candidate move tiles.
     */
    // TODO: add tileId on top left of tile panel
    private void highlightPieceMoves(final int tileId) {
      final Tile sourceTile = gameStateBoard.getTile(tileId);

      if (sourceTile.isTileOccupied()) {
        currentPieceMoves = sourceTile.getPiece().evaluateMoves(gameStateBoard);

        for (final Map.Entry<String, Move> entry : currentPieceMoves.entrySet()) {
          final int destinationCoords = entry.getValue().getDestinationCoords();

          if (entry.getValue().getMoveType() == "aggressive" ||
              entry.getValue().getMoveType() == "draw") {
            boardTiles.get(destinationCoords).setBackground(ENEMY_TILE_COLOR);

          } else if (entry.getValue().getMoveType() == "normal") {
            boardTiles.get(destinationCoords).setBackground(VALID_TILE_COLOR);

          } else if (entry.getValue().getMoveType() == "invalid") {
            boardTiles.get(destinationCoords).setBackground(INVALID_TILE_COLOR);
          }
          // Sets highlighted tile as candidate move tile.
          candidateMoveTiles.add(destinationCoords);
          boardTiles.get(destinationCoords).setIsCandidateMoveTile(true);
        };
      }
    }

    /**
     * Gets active tile ID.
     * @return int activeTileId field.
     */
    private int getActiveTileId() {
      return this.activeTileId;
    }

    /**
     * Gets the active tile TilePanel instance.
     * @return TilePanel of the active tile.
     */
    private TilePanel getActiveTilePanel() {
      return boardTiles.get(this.activeTileId);
    }

    /**
     * Sets the active tile. Also toggles on and off corresponding TilePanel as
     * active or not active.
     * @param newActiveTile new active tile id
     */
    private void setActiveTile(final int newActiveTile) {
      if (newActiveTile == -1) {
        if (this.activeTileId != -1) {
          boardTiles.get(this.activeTileId).setIsTileActive(false);
          this.activeTileId = -1;
        }
      } else {
        if (this.activeTileId == -1) {
          this.activeTileId = newActiveTile;
          boardTiles.get(newActiveTile).setIsTileActive(true);
        } else {
          boardTiles.get(this.activeTileId).setIsTileActive(false);
          this.activeTileId = newActiveTile;
          boardTiles.get(newActiveTile).setIsTileActive(true);
        }
      }
    }

    /**
     * Deactivates currently active TilePanel and sets activeTileId to -1.
     */
    private void deactivateActiveTile() {
      if (this.activeTileId != -1) {
        clearHighlights();
        boardTiles.get(activeTileId).setIsTileActive(false);
        this.activeTileId = -1;
      }
    }

    /**
     * Deactivate specific tile.
     * @param tileId tile id of the tile to be deactivated.
     */
    private void deactivateTile(final int tileId) {
      if (boardTiles.get(tileId).isTileActive()) {
        boardTiles.get(tileId).deactivateTile();
      }
    }

    /**
     * Enables or disables TilePanel hover highlight.
     */
    private void setHoverHighlight(final boolean enabled) {
      this.enableHoverHighlight = enabled;
    }

    /**
     * Clears all highlights of candidate move tiles and active tile.
     */
    private void clearHighlights() {
      // remove candidate move tile highlights
      for (int i = 0; i < candidateMoveTiles.size(); i++) {
        boardTiles.get(candidateMoveTiles.get(i)).assignTileColor();
        boardTiles.get(candidateMoveTiles.get(i)).setIsCandidateMoveTile(false);
      }
      // remove active tile highlight
      if (this.activeTileId != -1)
        boardTiles.get(activeTileId).assignTileColor();
    }

    /**
     * Refresh tile pieces icons.
     */
    private void refreshInnerBoardPanelIcons() {
      for (int i = 0; i < boardTiles.size(); i++) {
        boardTiles.get(i).loadPieceIcons();
        boardTiles.get(i).assignTilePieceIcon();
        boardTiles.get(i).validate();
      }
    }

    /**
     * Refresh all instance of TilePanel background color.
     */
    private void refreshTilesBackgroundColor() {
      for (int i = 0; i < boardTiles.size(); i++) {
        boardTiles.get(i).assignTileColor();
        boardTiles.get(i).validate();
      }
    }

    /**
     * Pre-loads piece image icons and store into HashMaps separately by piece
     * Alliance. Also hard-coded to scale to fit into TilePanel dimensions.
     */
    private void preLoadImages() {
      // Pre-load piece image
      final Map<String, Image> blackPieceIcons = new HashMap<>();
      final Map<String, Image> whitePieceIcons = new HashMap<>();

      final String blackPiecesPath = PIECE_IMAGES_PATH + "black/";
      final String whitePiecesPath = PIECE_IMAGES_PATH + "white/";
      final String[] blackPathNames;
      final String[] whitePathNames;
      BufferedImage bufferedImage;
      Image image;

      final File blackImageFiles = new File(blackPiecesPath);
      final File whiteImageFiles = new File(whitePiecesPath);

      blackPathNames = blackImageFiles.list();
      whitePathNames = whiteImageFiles.list();

      // TODO Improve code redundancy
      // load black images
      for (final String pathFile : blackPathNames) {
        try {
          final String strippedAlliancePathName = pathFile.replaceAll("^BLACK_", "");
          final String pathNameRank = strippedAlliancePathName.replaceAll(".png$", "");

          bufferedImage = ImageIO.read(new File(blackPiecesPath + pathFile));
          image = bufferedImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);

          blackPieceIcons.put(pathNameRank, image);
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }

      // load white images
      for (final String pathFile : whitePathNames) {
        try {
          final String strippedAlliancePathName = pathFile.replaceAll("^WHITE_", "");
          final String pathNameRank = strippedAlliancePathName.replaceAll(".png$", "");

          bufferedImage = ImageIO.read(new File(whitePiecesPath + pathFile));
          image = bufferedImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);

          whitePieceIcons.put(pathNameRank, image);
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }

      this.blackPieceIcons = blackPieceIcons;
      this.whitePieceIcons = whitePieceIcons;
    }

    /**
     * Gets all pre-loaded black piece icons.
     * @return Map<String, Image> blackPieceIcons field.
     */
    private Map<String, Image> getBlackPieceIcons() {
      return blackPieceIcons;
    }

    /**
     * Gets all pre-loaded white piece icons.
     * @return Map<String, Image> whitePieceIcons field.
     */
    private Map<String, Image> getWhitePieceIcons() {
      return whitePieceIcons;
    }

    /**
     * Gets currently hovered tile ID.
     * @return int hoveredTileId field.
     */
    private int getHoveredTileId() {
      return this.hoveredTileId;
    }

    /**
     * Sets hovered tile ID.
     * @param tileId tile Id being hovered.
     */
    private void setHoveredTileId(final int tileId) {
      this.hoveredTileId = tileId;
    }
  } // InnerBoardPanel

  /**
   * BoardPanel inner JPanel class that displays tiles and piece icons
   */
  private class TilePanel extends JPanel {

    /** Tile panel ID */
    private final int tileId;

    /** Checks if this TilePanel is active or has been clicked */
    private boolean isTileActive = false;

    /** Checks if this TilePanel is a candidate move tile */
    private boolean isCandidateMoveTile = false;

    /** Current contained piece hidden icon */
    private Image iconHidden;

    /** Current contained piece normal icon */
    private Image iconNormal;

    /**
     * Constructor method that takes in tile ID.
     */
    public TilePanel(final int tileId) {
      super(new GridBagLayout());
      this.tileId = tileId;
      setPreferredSize(TILE_PANEL_DIMENSION);
      setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
    }

    /**
     * Load and assign all piece icons, tile colors and event listeners
     */
    public void initTilePanel() {
      loadPieceIcons();
      assignTilePieceIcon();
      assignTileColor();
      addTilePanelListener();
      validate();
    }

    /**
     * Adds this TilePanel mouse listeners for board interaction.
     */
    // TODO: improve READABILITY
    private void addTilePanelListener() {
      this.addMouseListener(new MouseListener() {

        @Override
        public void mouseClicked(final MouseEvent e) {
        }

        /**
         * Highlights hovered TilePanel and all candidate move tiles if
         * TilePanel contains piece. Disables highlight when game concludes or
         * has a winner.
         */
        @Override
        public void mouseEntered(final MouseEvent e) {
          if (gameStateBoard.getEndGameWinner() == null &&
              gameStateBoard.isGameStarted()) {
            if (isOccupyingPieceOwnedByMoveMaker() || isCandidateMoveTile) {
              if (boardPanel.enableHoverHighlight)
                boardPanel.highlightPieceMoves(tileId);

              boardPanel.setHoveredTileId(tileId);
            }
          } else if (gameStateBoard.isGameInitialized() &&
              !gameStateBoard.isGameStarted()) {
            if ((gameStateBoard.getMoveMaker() == Alliance.BLACK &&
                 tileId < BoardUtils.ALL_TILES_COUNT / 2) ||
                (gameStateBoard.getMoveMaker() == Alliance.WHITE &&
                 tileId >= BoardUtils.ALL_TILES_COUNT / 2 )) {

              boardPanel.setHoveredTileId(tileId);
            }
          }
        }

        /**
         * Removes all highlights as mouse leaves TilePanel. Disables highlight
         * when game concludes or has a winner.
         */
        @Override
        public void mouseExited(final MouseEvent e) {
          if (gameStateBoard.getEndGameWinner() == null &&
              gameStateBoard.isGameStarted()) {
            if (isOccupyingPieceOwnedByMoveMaker()) {
              if (boardPanel.enableHoverHighlight)
                boardPanel.clearHighlights();
            }
          }
        }

        /**
         * Highlights pressed TilePanel and set as active. Active tile that have
         * been toggled on will remain to be highlighted as well as the
         * corresponding candidate move tiles until the active TilePanel have
         * pressed one more time or pressed another TilePanel to toggle off.
         *
         * Also, when TilePanel is active, hover highlight is disabled and the
         * highlighted candidate move tiles will listens for a mouse press to
         * send signal to board engine to move the piece contained in the active
         * TilePanel to the direction of the clicked candidate move tile.
         *
         * Furthermore, if game arrange mode will only activate tile and will
         * not highlight piece candidate move tiles.
         *
         * Listener disables once the game concludes or has game winner.
         */
        @Override
        public void mousePressed(final MouseEvent e) {
          // Ensures TilePanel can only be pressed if game is ongoing
          if (gameStateBoard.getEndGameWinner() == null &&
              gameStateBoard.isGameStarted()) {
            // Ensures TilePanel can only be clicked by respective player.
            if (isOccupyingPieceOwnedByMoveMaker()) {
              // Ensures TilePanel will only activate if theres an occupying piece
              if (gameStateBoard.getTile(tileId).isTileOccupied() && !isCandidateMoveTile) {
                // Ensures TilePanel can only be clicked by the move maker
                if (gameStateBoard.getTile(tileId).getPiece().getPieceAlliance() ==
                    gameStateBoard.getMoveMaker()) {

                  boardPanel.clearHighlights();

                  // Toggle off TilePanel if active, else activate and lock the highlights
                  if (isTileActive) {
                    boardPanel.deactivateActiveTile();
                    boardPanel.setHoverHighlight(true);
                  } else {
                    boardPanel.setActiveTile(tileId);
                    boardPanel.setHoverHighlight(false);
                    boardPanel.highlightPieceMoves(tileId);
                    setBackground(ACTIVE_TILE_COLOR);
                  }
                }
              }
            }
            // If game is in arrange mode or not yet started.
          } else if (gameStateBoard.isGameInitialized() &&
                    !gameStateBoard.isGameStarted()) {
              if ((gameStateBoard.getMoveMaker() == Alliance.BLACK &&
                   tileId < BoardUtils.ALL_TILES_COUNT / 2) ||
                  (gameStateBoard.getMoveMaker() == Alliance.WHITE &&
                   tileId >= BoardUtils.ALL_TILES_COUNT / 2 )) {

              // Arrange mode active tile highlights.
              if (isTileActive) {
                boardPanel.deactivateActiveTile();
              } else if (boardPanel.getActiveTileId() != -1 &&
                        gameStateBoard.getTile(boardPanel.getActiveTileId()).isTileOccupied()) {
                boardPanel.deactivateTile(tileId);
              } else if (gameStateBoard.getTile(tileId).isTileOccupied()) {
                boardPanel.setActiveTile(tileId);
                setBackground(ACTIVE_TILE_COLOR);
              }
            }
          }
        }

        /**
         * Listener that gives signal to game engine to execute if theres an
         * active tile and the highlighted candidate piece move tiles has been
         * released from being clicked.
         */
        @Override
        public void mouseReleased(final MouseEvent e) {
          // Ensures TilePanel can only be pressed if game is ongoing.
          if (gameStateBoard.getEndGameWinner() == null &&
              gameStateBoard.isGameStarted()) {
            // Ensures TilePanel can only be clicked by respective player or is
            // a candidate piece move tile.
            if (isOccupyingPieceOwnedByMoveMaker() || isCandidateMoveTile) {
              // Ensures when mouse is released the hovered TilePanel is still
              // within the TilePanel that has been pressed, and is a candidate
              // move tile.
              if (isCandidateMoveTile && boardPanel.getHoveredTileId() == tileId) {
                // Get active Tile piece
                final Piece activePiece = gameStateBoard.getTile(boardPanel.getActiveTileId()).getPiece();

                // Get player and execute move.
                final Player player = gameStateBoard.getPlayer(activePiece.getPieceAlliance());
                player.makeMove(activePiece.getPieceCoords(), tileId);

                // The executed move will now be the last move after being
                // executed. Append move to mov history panel.
                if (gameStateBoard.getLastMove() != null) {
                  final Move lastMove = gameStateBoard.getLastMove();
                  moveHistoryPanel.appendToMoveHistory(lastMove);
                }

                // If game has concluded or has a winner, announce to move
                // history text area.
                if (gameStateBoard.isEndGame()) {
                  final String endGameMessage = "GAME OVER, " +
                    gameStateBoard.getEndGameWinner() + " PLAYER WON!";
                  final String separator = "\n**********************************\n";
                  moveHistoryPanel.appendTextToMoveHistory("\n" + separator +
                                                  endGameMessage + separator);
                  boardPanel.refreshTilesBackgroundColor();
                }

                // Refresh BoardPanel
                boardPanel.refreshInnerBoardPanelIcons();
                boardPanel.deactivateActiveTile();
                boardPanel.setHoverHighlight(true);
              }
            }
            // If game is in arrange mode, move piece freely without restriction.
          } else if (gameStateBoard.isGameInitialized() &&
                      !gameStateBoard.isGameStarted()) {
            // Ensures TilePanel can only be activated by the move maker and
            // within Players respective territory.
            if ((gameStateBoard.getMoveMaker() == Alliance.BLACK &&
                 tileId < BoardUtils.ALL_TILES_COUNT / 2) ||
                (gameStateBoard.getMoveMaker() == Alliance.WHITE &&
                 tileId >= BoardUtils.ALL_TILES_COUNT / 2 )) {
              final int hoveredTileId = boardPanel.getHoveredTileId();
              final int activeTileId = boardPanel.getActiveTileId();

              if (gameStateBoard.isDebugMode()) {
                System.out.println("hoveredTileId=" + hoveredTileId +
                                   ";activeTileId=" + activeTileId +
                                   ";currenttileId=" + tileId);
              }

              // If theres an active tile and another TilePanel have been clicked
              if (hoveredTileId == tileId && hoveredTileId != activeTileId &&
                  activeTileId != -1) {
                // If TilePanel is empty moved the active tile piece over it
                if (gameStateBoard.getTile(tileId).isTileEmpty()) {
                  gameStateBoard.movePiece(activeTileId, tileId);

                  if (gameStateBoard.isDebugMode())
                    System.out.println(gameStateBoard.getTile(tileId).getPiece().getRank() +
                        " at " + activeTileId + " moved to " + tileId + "\n");
                  // Else, TilePanel is occupied and swapped with active tile piece.
                } else {
                  gameStateBoard.swapPiece(activeTileId, tileId);

                  if (gameStateBoard.isDebugMode())
                    System.out.println(gameStateBoard.getTile(tileId).getPiece().getRank() +
                        " at " + activeTileId + " swapped with " +
                        gameStateBoard.getTile(activeTileId).getPiece().getRank() +
                        " at " + tileId + "\n");
                }

                // Refresh BoardPanel and disables active if piece have been moved.
                boardPanel.refreshInnerBoardPanelIcons();
                boardPanel.deactivateActiveTile();
              }
            }
          }
        }
      });
    }

    /**
     * Loads the occupying piece icons into TilePanel.
     */
    private void loadPieceIcons() {
      // Pre-load piece image
      if (gameStateBoard.getTile(tileId).isTileOccupied()) {
        final Tile currTile = gameStateBoard.getTile(tileId);
        final Alliance pieceAlliance = currTile.getPiece().getPieceAlliance();
        final String pieceRank = currTile.getPiece().getRank();

        if (pieceAlliance == Alliance.BLACK) {
          this.iconNormal = boardPanel.getBlackPieceIcons().get(pieceRank);
          this.iconHidden = boardPanel.getBlackPieceIcons().get("Hidden");
        } else {
          this.iconNormal = boardPanel.getWhitePieceIcons().get(pieceRank);
          this.iconHidden = boardPanel.getWhitePieceIcons().get("Hidden");
        }

        if (gameStateBoard.isDebugMode() && gameStateBoard.isGameInitialized())
          System.out.println("Tile " + tileId + " icons loaded");
      } else {
        if (gameStateBoard.isDebugMode() && gameStateBoard.isGameInitialized())
          System.out.println("Tile " + tileId + " icons empty");
      }
    }

    /**
     * Checks if occupying piece is owned by the current move maker.
     */
    private boolean isOccupyingPieceOwnedByMoveMaker() {
      if (gameStateBoard.getTile(tileId).isTileOccupied()) {
        if (gameStateBoard.getTile(tileId).getPiece().getPieceAlliance() ==
            gameStateBoard.getMoveMaker())
          return true;
      }
      return false;
    }

    /**
     * Assign tile piece icon to display in this TilePanel if occupied.
     */
    private void assignTilePieceIcon() {
      this.removeAll();

      if (gameStateBoard.getTile(tileId).isTileOccupied()) {
        final Tile currTile = gameStateBoard.getTile(tileId);

        if (gameStateBoard.getEndGameWinner() == null) {
          // Load normal icon if isMoveMaker, else hidden icon
          if (currTile.getPiece().getPieceAlliance() == gameStateBoard.getMoveMaker())
            add(new JLabel(new ImageIcon(iconNormal)));
          else
            add(new JLabel(new ImageIcon(iconHidden)));

        } else {
          add(new JLabel(new ImageIcon(iconNormal)));
        }
        if (gameStateBoard.isDebugMode() && gameStateBoard.isGameInitialized())
          System.out.println("Tile " + tileId + " piece icon assigned");
      } else {
        if (gameStateBoard.isDebugMode() && gameStateBoard.isGameInitialized())
          System.out.println("Tile " + tileId + " piece icon NOT assigned");
      }
    }

    /**
     * Sets the state of this TilePanel.
     * @param isTileActive state of tile.
     */
    private void setIsTileActive(final boolean isTileActive) {
      this.isTileActive = isTileActive;
    }

    /**
     * Sets the if this TilePanel is a candidate move tile.
     * @param isCandidateMoveTile is tile is a candidate piece move tile.
     */
    private void setIsCandidateMoveTile(final boolean isCandidateMoveTile) {
      this.isCandidateMoveTile = isCandidateMoveTile;
    }

    /**
     * Checks if this TilePanel is active.
     * @return boolean isTileActive field.
     */
    private boolean isTileActive() {
      return this.isTileActive;
    }

    /**
     * Deactivate this TilePanel.
     */
    private void deactivateTile() {
      this.isTileActive = false;
    }

    /**
     * Assign TilePanel color.
     */
    private void assignTileColor() {
      // If game is ongoing, displays separate colors for each territory, else
      // the game has concluded and paints all TilePanel the color of the winner
      // player.
      if (gameStateBoard.getEndGameWinner() == null) {
        // Black territory color
        if (gameStateBoard.getTile(tileId).getTerritory() == Alliance.BLACK)
          setBackground(DARK_TILE_COLOR);
        // White territory color
        else
          setBackground(LIGHT_TILE_COLOR);

        if (gameStateBoard.isDebugMode() && gameStateBoard.isGameInitialized())
          System.out.println("Tile " + tileId + " color assigned");

      } else {
        setBackground(gameStateBoard.getEndGameWinner() == Alliance.BLACK ?
            DARK_TILE_COLOR : LIGHT_TILE_COLOR);

        if (gameStateBoard.isDebugMode())
          System.out.println("Tile " + tileId + " color assigned");
      }
    }

  } // TilePanel

} // BoardPanel
