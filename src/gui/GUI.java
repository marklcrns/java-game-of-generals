package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

  private final static Color DARK_TILE_COLOR = new Color(50, 50, 50);
  private final static Color LIGHT_TILE_COLOR = new Color(200, 200, 200);
  private final static Color ENEMY_TILE_COLOR = new Color(200, 100, 120);
  private final static Color VALID_TILE_COLOR = new Color(130, 200, 120);
  private final static Color INVALID_TILE_COLOR = new Color(130, 150, 230);
  private final static Color ACTIVE_TILE_COLOR = new Color(230, 230, 120);

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

  private final static String ART_DIR_PATH = "art/pieces/original/";

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

    container.add(new MenuBarPanel(), BorderLayout.NORTH);
    container.add(new MoveHistoryPanel(), BorderLayout.WEST);
    container.add(new BoardPanel(), BorderLayout.CENTER);
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

      this.add(save);
      this.add(load);
      this.add(quit);
      this.add(undo);
      this.add(redo);
      this.add(surrender);
    }
  }

  private class MoveHistoryPanel extends JPanel {

    public MoveHistoryPanel() {
      this.setLayout(new FlowLayout());
      this.setPreferredSize(MOVE_HISTORY_PANEL_DIMENSION);
      JLabel label = new JLabel("Move History");
      JTextArea moveHistoryArea = new JTextArea();
      this.add(label);
      this.add(moveHistoryArea);
    }
  }

  private class BoardPanel extends JPanel {

    private boolean enableHoverHighlight = true;
    private int activeTileId;
    private final List<TilePanel> boardTiles;
    private final List<Integer> candidateMoveTiles;
    private Map<String, Move> pieceMoves;

    public BoardPanel() {
      super(new GridLayout(BoardUtils.TILE_ROW_COUNT, BoardUtils.TILE_COLUMN_COUNT));
      this.boardTiles = new ArrayList<>();
      this.candidateMoveTiles = new ArrayList<>();

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

      final Tile hoveredTile = gameStateBoard.getTile(tileId);

      if (hoveredTile.isTileOccupied()) {

        if (hoveredTile.getPiece().getAlliance() == gameStateBoard.getMoveMaker()) {
          pieceMoves = hoveredTile.getPiece().evaluateMoves(gameStateBoard);

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
        boardTiles.get(activeTileId).setIsTileActive(false);
        this.activeTileId = -1;
      }
    }

    private void setHoverHighlight(boolean enabled) {
      this.enableHoverHighlight = enabled;
    }

    private void refreshBoardPanel() {
      for (int i = 0; i < boardTiles.size(); i++) {
        boardTiles.get(i).assignTilePieceIcon(gameStateBoard);
        boardTiles.get(i).validate();
      }
      frame.repaint();
    }

  }

  private class TilePanel extends JPanel {

    private final int tileId;
    private boolean isTileActive = false;
    private boolean isCandidateMoveTile = false;
    private boolean isPieceHidden;

    TilePanel(final BoardPanel boardPanel,
              final int tileId) {
      super(new GridBagLayout());
      this.tileId = tileId;
      setPreferredSize(TILE_PANEL_DIMENSION);
      setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
      assignTileColor();
      assignTilePieceIcon(gameStateBoard);
      validate();

      // TODO:
      this.addMouseListener(new MouseListener() {

        @Override
        public void mouseClicked(MouseEvent e) {
          // TODO revise for efficiency
          if (isCandidateMoveTile) {
            final Piece activePiece = gameStateBoard.getTile(boardPanel.getActiveTileId()).getPiece();
            final Alliance activeTileAlliance = activePiece.getAlliance();
            final TilePanel activeTilePanel = boardPanel.getActiveTilePanel();

            Player player = gameStateBoard.getPlayer(activeTileAlliance);
            player.makeMove(activePiece.getCoords(), tileId);

            // refresh both tiles
            // activeTilePanel.assignTilePieceIcon(gameStateBoard);
            // activeTilePanel.validate();
            // assignTilePieceIcon(gameStateBoard);

            boardPanel.refreshBoardPanel();

            // TODO make method
            boardPanel.deactivateCurrentTile();
            boardPanel.setHoverHighlight(true);
            boardPanel.clearHighlights();

            return;
          }

          if (gameStateBoard.getTile(tileId).isTileOccupied()) {
            if (gameStateBoard.getTile(tileId).getPiece().getAlliance() == gameStateBoard.getMoveMaker()) {
              if (isTileActive) {
                boardPanel.deactivateCurrentTile();
                boardPanel.setHoverHighlight(true);
                boardPanel.clearHighlights();
              } else {
                boardPanel.setActiveTile(tileId);
                boardPanel.setHoverHighlight(false);
                boardPanel.clearHighlights();
                boardPanel.highlightPieceMoves(tileId);
                setBackground(ACTIVE_TILE_COLOR);
              }
            }
          } else {
            boardPanel.deactivateCurrentTile();
            boardPanel.setHoverHighlight(true);
            boardPanel.clearHighlights();
          }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
          if (boardPanel.enableHoverHighlight)
            boardPanel.highlightPieceMoves(tileId);
          // TODO Delete later
          System.out.println("ActiveTile: " + isTileActive);
          System.out.println("ID: " + boardPanel.getActiveTileId());
          System.out.println("CandidateTile: " + isCandidateMoveTile);
          System.out.println();
        }

        @Override
        public void mouseExited(MouseEvent e) {
          if (boardPanel.enableHoverHighlight)
            boardPanel.clearHighlights();
        }

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

      });
    } // TilePanel()

    // TODO: pre-load both images for better performance
    private void assignTilePieceIcon(Board board) {
      this.removeAll();
      final Tile currTile = board.getTile(tileId);
      final BufferedImage image;
      try {
        if (currTile.isTileOccupied()) {
          Alliance pieceAlliance = currTile.getPiece().getAlliance();
          String pieceRank = currTile.getPiece().getRank();
          String iconPath = ART_DIR_PATH + ("" + pieceAlliance).toLowerCase() +
            "/" + pieceAlliance + "_";

          if (pieceAlliance == gameStateBoard.getMoveMaker())
            iconPath += pieceRank + ".png";
          else
            iconPath += "HIDDEN.png";

          image = ImageIO.read(new File(iconPath));

          // Scale image
          Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);

          add(new JLabel(new ImageIcon(scaledImage)));
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private void setIsTileActive(boolean isTileActive) {
      this.isTileActive = isTileActive;
    }

    private void setIsCandidateMoveTile(boolean isCandidateMoveTile) {
      this.isCandidateMoveTile = isCandidateMoveTile;
    }

    private void setIsPieceHidden(boolean isPieceHidden) {
      this.isPieceHidden = isPieceHidden;
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
