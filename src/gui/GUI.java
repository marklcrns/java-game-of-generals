package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import engine.Board;
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
  private final BoardPanel boardPanel;
  private final static Dimension FRAME_DIMENSION = new Dimension(1000, 900);
  private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(800, 800);
  private final static Dimension TILE_PANEL_DIMENSION = new Dimension(300, 300);
  private final static Color BOARD_COLOR = new Color(187, 159, 72);
  private final static String ART_DIR_PATH = "art/pieces/original/";
  private final static Color DARK_TILE_COLOR = new Color(166, 99, 57);
  private final static Color LIGHT_TILE_COLOR = new Color(200, 130, 90);
  private final static Color ENEMY_TILE_COLOR = new Color(230, 70, 120);
  private final static Color ALLY_TILE_COLOR = new Color(70, 120, 230);
  private final static Color INVALID_TILE_COLOR = new Color(100, 100, 100);

  public GUI(Board board) {
    gameStateBoard = board;
    frame = new JFrame("Game of Generals");
    frame.setSize(FRAME_DIMENSION);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setVisible(true);

    this.boardPanel = new BoardPanel();
    frame.add(this.boardPanel, BorderLayout.CENTER);
  }

  private class BoardPanel extends JPanel {

    private final List<TilePanel> boardTiles;

    public BoardPanel() {
      super(new GridLayout(BoardUtils.TILE_COL_COUNT, BoardUtils.TILE_ROW_COUNT));
      this.boardTiles = new ArrayList<>();

      for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
        final TilePanel tilePanel = new TilePanel(this, i);
        this.boardTiles.add(tilePanel);
        add(tilePanel);
      }

      setPreferredSize(BOARD_PANEL_DIMENSION);
      validate();
      this.addMouseMotionListener(new MouseMotionListener() {
        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
          mx = e.getX();
          my = e.getY();

          frame.repaint();
          System.out.println("x: " + mx + ", y: " + my);
        }
      });

      // TODO: frame.addMouseListener(new MouseListener);

    }

  } // BoardPanel

  private class TilePanel extends JPanel {

    private final int tileId;

    TilePanel(final BoardPanel boardPanel,
              final int tileId) {
      super(new GridBagLayout());
      this.tileId = tileId;
      setPreferredSize(TILE_PANEL_DIMENSION);
      assignTileColor();
      assignTilePieceIcon(gameStateBoard);
      validate();
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
          Image scaledImage = image.getScaledInstance(110, 110, Image.SCALE_SMOOTH);

          add(new JLabel(new ImageIcon(scaledImage)));
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private void assignTileColor() {
      setBackground(this.tileId % 2 != 0 ? LIGHT_TILE_COLOR : DARK_TILE_COLOR);
    }

    public void setTileColor(Color color) {
      setBackground(color);
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
