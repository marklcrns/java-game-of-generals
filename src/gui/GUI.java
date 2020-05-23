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
  // TODO: set movemaker
  private Alliance moveMaker;

  private final static Color DARK_TILE_COLOR = new Color(166, 99, 57);
  private final static Color LIGHT_TILE_COLOR = new Color(200, 130, 90);
  private final static Color ENEMY_TILE_COLOR = new Color(230, 70, 120);
  private final static Color VALID_TILE_COLOR = new Color(70, 120, 230);
  private final static Color INVALID_TILE_COLOR = new Color(100, 100, 100);

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

      this.add(save);
      this.add(load);
      this.add(quit);
      this.add(undo);
      this.add(redo);
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
      if (gameStateBoard.getTile(tileId).isTileOccupied()) {
        pieceMoves = gameStateBoard.getTile(tileId).getPiece().evaluateMoves(gameStateBoard);

        System.out.println("TILE: " + tileId);

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
        // TODO: DELETE ME LATER
        // for (int i = 0; i < candidateMoveTiles.size(); i++) {
        //   System.out.println(candidateMoveTiles.get(i));
        // }
        // System.out.println();
      }
    }

    private void clearHighlights() {
      for (int i = 0; i < candidateMoveTiles.size(); i++) {
        boardTiles.get(candidateMoveTiles.get(i)).assignTileColor();
        boardTiles.get(candidateMoveTiles.get(i)).setIsCandidateMoveTile(false);
      }
    }

    private int getActiveTileId() {
      return this.activeTileId;
    }

    private void setActiveTile(int newActiveTile) {
      if (this.activeTileId != -1)
        boardTiles.get(this.activeTileId).setIsTileActive(false);

      this.activeTileId = newActiveTile;
      boardTiles.get(newActiveTile).setIsTileActive(true);
    }

    private void deactivateCurrentTile() {
      boardTiles.get(activeTileId).setIsTileActive(false);
      this.activeTileId = -1;
    }

    private void setHoverHighlight(boolean enabled) {
      this.enableHoverHighlight = enabled;
    }
  }

  private class TilePanel extends JPanel {

    private final int tileId;
    private boolean isTileActive = false;
    private boolean isCandidateMoveTile = false;

    TilePanel(final BoardPanel boardPanel,
              final int tileId) {
      super(new GridBagLayout());
      this.tileId = tileId;
      setPreferredSize(TILE_PANEL_DIMENSION);
      assignTileColor();
      assignTilePieceIcon(gameStateBoard);
      validate();

      // TODO:
      this.addMouseListener(new MouseListener() {

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {
          if (boardPanel.enableHoverHighlight)
            boardPanel.highlightPieceMoves(tileId);
          System.out.println("ActiveTile: " + isTileActive);
          System.out.println("CandidateTile: " + isCandidateMoveTile);
          System.out.println();
        }

        @Override
        public void mouseExited(MouseEvent e) {
          if (boardPanel.enableHoverHighlight)
            boardPanel.clearHighlights();
        }

        @Override
        public void mousePressed(MouseEvent e) {
          if (isCandidateMoveTile) {
            System.out.println("Move executed");
          }

          if (gameStateBoard.getTile(tileId).isTileOccupied()) {
            if (isTileActive) {
              boardPanel.deactivateCurrentTile();
              boardPanel.setHoverHighlight(true);
              boardPanel.clearHighlights();
            } else {
              boardPanel.setActiveTile(tileId);
              boardPanel.setHoverHighlight(false);
              boardPanel.clearHighlights();
              boardPanel.highlightPieceMoves(tileId);
            }
          } else {
            boardPanel.deactivateCurrentTile();
            boardPanel.setHoverHighlight(true);
            boardPanel.clearHighlights();
          }
        }

        @Override
        public void mouseReleased(MouseEvent e) {}

      });
    } // TilePanel()

    private void assignTilePieceIcon(Board board) {
      this.removeAll();
      final Tile currTile = board.getTile(tileId);
      final BufferedImage image;
      try {
        if (currTile.isTileOccupied()) {
          String pieceAlliance = "" + currTile.getPiece().getAlliance();
          String pieceRank = currTile.getPiece().getRank();
          // TODO: Modify to blank if other player's turn
          // Load image. Blank if BLACK alliance
          if (pieceAlliance.equals("BLACK")) {
            image = ImageIO.read(
                new File(ART_DIR_PATH +
                  pieceAlliance.toLowerCase() + "/" +
                  pieceAlliance + "_HIDDEN.png"));
          } else {
            image = ImageIO.read(
                new File(ART_DIR_PATH +
                  pieceAlliance.toLowerCase() + "/" +
                  pieceAlliance + "_" +
                  pieceRank + ".png"));
          }
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

    private void assignTileColor() {
      setBackground(this.tileId % 2 != 0 ? LIGHT_TILE_COLOR : DARK_TILE_COLOR);
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
