package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import engine.Alliance;
import engine.Board;
import engine.Move;
import engine.Board.Tile;
import engine.pieces.Piece;
import engine.player.Player;
import utils.BoardUtils;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-16
 */
public class GUI {

  public int mx = 0;
  public int my = 0;

  private static JFrame frame;
  private final Board gameStateBoard;

  private final static String ART_DIR_PATH = "art/pieces/original/";
  private final static Color DARK_TILE_COLOR = new Color(50, 50, 50);
  private final static Color LIGHT_TILE_COLOR = new Color(200, 200, 200);
  private final static Color ENEMY_TILE_COLOR = new Color(200, 100, 120);
  private final static Color VALID_TILE_COLOR = new Color(130, 200, 120);
  private final static Color INVALID_TILE_COLOR = new Color(130, 150, 230);
  private final static Color ACTIVE_TILE_COLOR = new Color(230, 230, 120);

  private static MenuBarPanel menuBarPanel;
  private static MoveHistoryPanel moveHistoryPanel;
  private static BoardPanel boardPanel;

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

  public GUI(Board board) {
    gameStateBoard = board;
    frame = new JFrame("Game of Generals");
    frame.setLayout(new BorderLayout());
    frame.setSize(FRAME_DIMENSION);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setVisible(true);

    Container container = frame.getContentPane();
    container.setLayout(new BorderLayout());

    menuBarPanel = new MenuBarPanel();
    moveHistoryPanel = new MoveHistoryPanel();
    boardPanel = new BoardPanel();

    container.add(menuBarPanel, BorderLayout.NORTH);
    container.add(moveHistoryPanel, BorderLayout.WEST);
    container.add(boardPanel, BorderLayout.CENTER);
  }

  private class MenuBarPanel extends JPanel {

    public MenuBarPanel() {
      this.setLayout(new FlowLayout());
      this.setPreferredSize(MENU_BAR_PANEL_DIMENSION);
      JButton save = new JButton("Save");
      JButton load = new JButton("Load");
      JButton quit = new JButton("Quit");
      JButton undo = new JButton("Undo");
      JButton redo = new JButton("Redo");
      JButton surrender = new JButton("Surrender");
      JButton rules = new JButton("Game Rules");

      this.add(save);
      this.add(load);
      this.add(quit);
      this.add(undo);
      this.add(redo);
      this.add(surrender);
      this.add(rules);
    }
  }

  private class MoveHistoryPanel extends JPanel {

    private JTextArea moveHistoryTextArea = new JTextArea();

    public MoveHistoryPanel() {
      this.setLayout(new BorderLayout());
      this.setPreferredSize(MOVE_HISTORY_PANEL_DIMENSION);
      moveHistoryTextArea.setEditable(false);

      JLabel label = new JLabel("MOVE HISTORY");
      label.setHorizontalAlignment(JLabel.CENTER);
      label.setVerticalAlignment(JLabel.CENTER);
      label.setFont(new Font("SansSerif", Font.BOLD, 18));

      moveHistoryTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      moveHistoryTextArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

      // set scrollable vertically as needed
      JScrollPane moveHistoryVScrollable = new JScrollPane(moveHistoryTextArea);
      moveHistoryVScrollable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

      this.add(label, BorderLayout.NORTH);
      this.add(moveHistoryVScrollable, BorderLayout.CENTER);
    }

    public void addMoveHistory(String move) {
      moveHistoryTextArea.append(move);
    }
  }

  private class BoardPanel extends JPanel {

    private boolean enableHoverHighlight = true;
    private int activeTileId;
    private int hoveredTileId;
    private final List<TilePanel> boardTiles;
    private final List<Integer> candidateMoveTiles;
    private Map<String, Move> pieceMoves;
    private Map<String, Image> blackPieceIcons;
    private Map<String, Image> whitePieceIcons;

    public BoardPanel() {
      super(new GridLayout(BoardUtils.TILE_ROW_COUNT, BoardUtils.TILE_COLUMN_COUNT));
      this.boardTiles = new ArrayList<>();
      this.candidateMoveTiles = new ArrayList<>();
      preLoadImages();

      for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
        final TilePanel tilePanel = new TilePanel(this, i);
        this.boardTiles.add(tilePanel);
        add(tilePanel);
      }

      setPreferredSize(BOARD_PANEL_DIMENSION);
      validate();

      // this.addMouseMotionListener(new MouseMotionListener() {
      //
      //   @Override
      //   public void mouseDragged(MouseEvent e) {}
      //
      //   @Override
      //   public void mouseMoved(MouseEvent e) {
      //     if (enableHoverHighlight) {
      //       mx = e.getX();
      //       my = e.getY();
      //
      //       clearHighlights();
      //       for (int y = 0; y < BoardUtils.TILE_ROW_COUNT; y++) {
      //         for (int x = 0; x < BoardUtils.TILE_COLUMN_COUNT; x++) {
      //           if (mx > x * TILE_PANEL_DIMENSION.getWidth() &&
      //               mx < (x + 1) * TILE_PANEL_DIMENSION.getWidth() &&
      //               my > y * TILE_PANEL_DIMENSION.getHeight() &&
      //               my < (y + 1) * TILE_PANEL_DIMENSION.getHeight()) {
      //
      //             highlightPieceMoves(y * 9 + x);
      //           }
      //         }
      //       }
      //
      //       frame.repaint();
      //     }
      //   }
      // });
    }

    private void highlightPieceMoves(int tileId) {

      final Tile sourceTile = gameStateBoard.getTile(tileId);

      if (sourceTile.isTileOccupied()) {

        if (sourceTile.getPiece().getAlliance() == gameStateBoard.getMoveMaker()) {
          pieceMoves = sourceTile.getPiece().evaluateMoves(gameStateBoard);

          for (Map.Entry<String, Move> entry : pieceMoves.entrySet()) {
            int destinationCoords = entry.getValue().getDestinationCoords();
            if (entry.getValue().getMoveType() == "aggressive" ||
                entry.getValue().getMoveType() == "draw") {
              boardTiles.get(destinationCoords).setBackground(ENEMY_TILE_COLOR);

            } else if (entry.getValue().getMoveType() == "normal") {
              boardTiles.get(destinationCoords).setBackground(VALID_TILE_COLOR);

            } else if (entry.getValue().getMoveType() == "invalid") {
              boardTiles.get(destinationCoords).setBackground(INVALID_TILE_COLOR);
            }

            candidateMoveTiles.add(destinationCoords);
            boardTiles.get(destinationCoords).setIsCandidateMoveTile(true);
          };
        }
      }
    }

    private int getActiveTileId() {
      return this.activeTileId;
    }

    private TilePanel getActiveTilePanel() {
      return boardTiles.get(this.activeTileId);
    }

    private void setActiveTile(int newActiveTile) {
      if (this.activeTileId != -1)
        boardTiles.get(this.activeTileId).setIsTileActive(false);

      this.activeTileId = newActiveTile;
      boardTiles.get(newActiveTile).setIsTileActive(true);
    }

    private void deactivateCurrentTile() {
      if (this.activeTileId != -1) {
        clearHighlights();
        boardTiles.get(activeTileId).setIsTileActive(false);
        this.activeTileId = -1;
      }
    }

    private void setHoverHighlight(boolean enabled) {
      this.enableHoverHighlight = enabled;
    }

    private void clearHighlights() {
      // remove candidate move tile highlights
      for (int i = 0; i < candidateMoveTiles.size(); i++) {
        boardTiles.get(candidateMoveTiles.get(i)).assignTileColor();
        boardTiles.get(candidateMoveTiles.get(i)).setIsCandidateMoveTile(false);
      }
      // remove active tile highlight
      if (this.activeTileId != -1)
        boardTiles.get(activeTileId).assignTileColor();

      frame.repaint();
    }

    private void refreshBoardPanel() {
      for (int i = 0; i < boardTiles.size(); i++) {
        // TODO: Pre load all images in boarddPanel as a field
        boardTiles.get(i).loadPieceIcons();
        boardTiles.get(i).assignTilePieceIcon();
        boardTiles.get(i).validate();
      }

      frame.repaint();
    }

    private void preLoadImages() {
      // Pre-load piece image
      Map<String, Image> blackPieceIcons = new HashMap<>();
      Map<String, Image> whitePieceIcons = new HashMap<>();

      final String blackPiecesPath = ART_DIR_PATH + "black/";
      final String whitePiecesPath = ART_DIR_PATH + "white/";
      final String[] blackPathNames;
      final String[] whitePathNames;
      BufferedImage bufferedImage;
      Image image;

      File blackImageFiles = new File(blackPiecesPath);
      File whiteImageFiles = new File(whitePiecesPath);

      blackPathNames = blackImageFiles.list();
      whitePathNames = whiteImageFiles.list();

      // TODO Improve code redundancy
      // load black images
      for (String pathFile : blackPathNames) {
        try {
          String strippedAlliancePathName = pathFile.replaceAll("^BLACK_", "");
          String pathNameRank = strippedAlliancePathName.replaceAll(".png$", "");

          bufferedImage = ImageIO.read(new File(blackPiecesPath + pathFile));
          image = bufferedImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);

          blackPieceIcons.put(pathNameRank, image);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      // load white images
      for (String pathFile : whitePathNames) {
        try {
          String strippedAlliancePathName = pathFile.replaceAll("^WHITE_", "");
          String pathNameRank = strippedAlliancePathName.replaceAll(".png$", "");

          bufferedImage = ImageIO.read(new File(whitePiecesPath + pathFile));
          image = bufferedImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);

          whitePieceIcons.put(pathNameRank, image);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      this.blackPieceIcons = blackPieceIcons;
      this.whitePieceIcons = whitePieceIcons;
    }

    private Map<String, Image> getBlackPieceIcons() {
      return blackPieceIcons;
    }

    private Map<String, Image> getWhitePieceIcons() {
      return whitePieceIcons;
    }

    private int getHoveredTileId() {
      return this.hoveredTileId;
    }

    private void setHoveredTileId(int tileId) {
      this.hoveredTileId = tileId;
    }

  }

  private class TilePanel extends JPanel {

    private final int tileId;
    private final BoardPanel boardPanel;
    private boolean isTileActive = false;
    private boolean isCandidateMoveTile = false;
    private Image iconHidden;
    private Image iconNormal;

    TilePanel(final BoardPanel boardPanel,
              final int tileId) {
      super(new GridBagLayout());
      this.tileId = tileId;
      this.boardPanel = boardPanel;
      setPreferredSize(TILE_PANEL_DIMENSION);
      setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));

      loadPieceIcons();
      assignTilePieceIcon();

      assignTileColor();
      validate();

      this.addMouseListener(new MouseListener() {

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
          if (boardPanel.enableHoverHighlight)
            boardPanel.highlightPieceMoves(tileId);
          boardPanel.setHoveredTileId(tileId);
        }

        @Override
        public void mouseExited(MouseEvent e) {
          if (boardPanel.enableHoverHighlight)
            boardPanel.clearHighlights();
        }

        @Override
        public void mousePressed(MouseEvent e) {
          if (gameStateBoard.getTile(tileId).isTileOccupied() && !isCandidateMoveTile) {
            if (gameStateBoard.getTile(tileId).getPiece().getAlliance() == gameStateBoard.getMoveMaker()) {

              boardPanel.clearHighlights();

              if (isTileActive) {
                boardPanel.deactivateCurrentTile();
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

        @Override
        public void mouseReleased(MouseEvent e) {
          if (isCandidateMoveTile && boardPanel.getHoveredTileId() == tileId) {
            final Piece activePiece = gameStateBoard.getTile(boardPanel.getActiveTileId()).getPiece();

            Player player = gameStateBoard.getPlayer(activePiece.getAlliance());
            player.makeMove(activePiece.getCoords(), tileId);

            Move lastMove = gameStateBoard.getLastMove();

            // TODO specify piece alliance in aggressive. specify draw
            if (lastMove.getMoveType() == "aggressive") {
              Alliance superiorPieceAlliance =
                lastMove.getEliminatedPiece().getAlliance() == Alliance.BLACK ?
                Alliance.WHITE : Alliance.BLACK;

              moveHistoryPanel.addMoveHistory("\nTurn " + lastMove.getTurnId() +
                                              ": " + lastMove.getOriginCoords() +
                                              " to " + lastMove.getDestinationCoords() +
                                              " " + superiorPieceAlliance);
            } else if (lastMove.getMoveType() == "draw") {
              moveHistoryPanel.addMoveHistory("\nTurn " + lastMove.getTurnId() +
                                              ": " + lastMove.getOriginCoords() +
                                              " to " + lastMove.getDestinationCoords() +
                                              " DRAW");
            } else {
              moveHistoryPanel.addMoveHistory("\nTurn " + lastMove.getTurnId() +
                                              ": " + lastMove.getOriginCoords() +
                                              " to " + lastMove.getDestinationCoords());
            }

            boardPanel.refreshBoardPanel();
            boardPanel.deactivateCurrentTile();
            boardPanel.setHoverHighlight(true);
          }
        }
      });
    }

    private void loadPieceIcons() {
      // Pre-load piece image
      if (gameStateBoard.getTile(tileId).isTileOccupied()) {
        final Tile currTile = gameStateBoard.getTile(tileId);
        Alliance pieceAlliance = currTile.getPiece().getAlliance();
        String pieceRank = currTile.getPiece().getRank();

        if (pieceAlliance == Alliance.BLACK) {
          this.iconNormal = boardPanel.getBlackPieceIcons().get(pieceRank);
          this.iconHidden = boardPanel.getBlackPieceIcons().get("HIDDEN");
        } else {
          this.iconNormal = boardPanel.getWhitePieceIcons().get(pieceRank);
          this.iconHidden = boardPanel.getWhitePieceIcons().get("HIDDEN");
        }
      }
    }

    private void assignTilePieceIcon() {
      this.removeAll();

      if (gameStateBoard.getTile(tileId).isTileOccupied()) {
        final Tile currTile = gameStateBoard.getTile(tileId);

        // Load normal icon if isMoveMaker, else hidden icon
        if (currTile.getPiece().getAlliance() == gameStateBoard.getMoveMaker())
          add(new JLabel(new ImageIcon(iconNormal)));
        else
          add(new JLabel(new ImageIcon(iconHidden)));
      }
    }

    private void setIsTileActive(boolean isTileActive) {
      this.isTileActive = isTileActive;
    }

    private void setIsCandidateMoveTile(boolean isCandidateMoveTile) {
      this.isCandidateMoveTile = isCandidateMoveTile;
    }

    private void assignTileColor() {
      // Checkered  board
      // setBackground(this.tileId % 2 != 0 ? LIGHT_TILE_COLOR : DARK_TILE_COLOR);

      // Solid colored board on respective territories
      if (gameStateBoard.getTile(tileId).getTerritory() == Alliance.BLACK)
        setBackground(DARK_TILE_COLOR);
      else
        setBackground(LIGHT_TILE_COLOR);
    }
  } // TilePanel

  //////////////// SCRAP CODES ////////////////////
  // public class Board extends JPanel {
  //
  //   private static final int TILE_SPACING = 3;
  //   private static final int TILE_SIZE = 80;
  //   private static final int TILE_COUNT_ROW = 9;
  //   private static final int TILE_COUNT_COL = 8;
  //   private static final int BOARD_WIDTH = TILE_SIZE * TILE_COUNT_ROW;
  //   private static final int BOARD_HEIGHT = TILE_SIZE * TILE_COUNT_COL;
  //   private final Color BOARD_COLOR = new Color(187, 159, 72);
  //   private final Color TILE_COLOR = new Color(204, 184, 114);
  //
  //   @Override
  //   public void paintComponent(Graphics g) {
  //     g.setColor(BOARD_COLOR);
  //     g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
  //     g.setColor(TILE_COLOR);
  //     for (int x = 0; x < 9; x++) {
  //       for (int y = 0; y < 8; y++) {
  //         if (mx >= TILE_SPACING + x * TILE_SIZE && my < TILE_SPACING + y * TILE_SIZE - TILE_SPACING)
  //           g.setColor(Color.RED);
  //         g.fillRect(TILE_SPACING + x * TILE_SIZE,  // x-coord
  //                    TILE_SPACING + y * TILE_SIZE,  // y-coord
  //                    TILE_SIZE - 2 * TILE_SPACING,  // horizontal size
  //                    TILE_SIZE - 2 * TILE_SPACING); // vertical size
  //       }
  //     }
  //   }
  // } // Board

} // GUI
