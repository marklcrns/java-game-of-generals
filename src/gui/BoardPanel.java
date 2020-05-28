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
 * Author: Mark Lucernas
 * Date: 2020-05-16
 */
public class BoardPanel extends JPanel {

  public int mx = 0;
  public int my = 0;

  private static Board gameStateBoard;
  private static JButton saveBtn, loadBtn, quitBtn, undoBtn,
                         redoBtn, surrenderBtn, rulesBtn,
                         doneArrangingBtn, startGameBtn;

  private static MenuBarPanel menuBarPanel;
  private static MoveHistoryPanel moveHistoryPanel;
  private static InnerBoardPanel boardPanel;

  private final static String ART_DIR_PATH = "art/pieces/original/";
  private final static Color DARK_TILE_COLOR = new Color(50, 50, 50);
  private final static Color LIGHT_TILE_COLOR = new Color(200, 200, 200);
  private final static Color ENEMY_TILE_COLOR = new Color(120, 0, 0);
  private final static Color VALID_TILE_COLOR = new Color(170, 210, 240);
  private final static Color INVALID_TILE_COLOR = new Color(125, 125, 125);
  private final static Color ACTIVE_TILE_COLOR = new Color(230, 210, 25);

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

  public BoardPanel(Board board) {
    gameStateBoard = board;
    this.setLayout(new BorderLayout());
    this.setVisible(true);

    menuBarPanel = new MenuBarPanel();
    moveHistoryPanel = new MoveHistoryPanel();
    boardPanel = new InnerBoardPanel();

    this.add(menuBarPanel, BorderLayout.NORTH);
    this.add(moveHistoryPanel, BorderLayout.WEST);
    this.add(boardPanel, BorderLayout.CENTER);
  }

  public final JButton getSaveBtn() {
    return saveBtn;
  }

  public final JButton getLoadBtn() {
    return loadBtn;
  }

  public final JButton getQuitBtn() {
    return quitBtn;
  }

  public final JButton getUndoBtn() {
    return undoBtn;
  }

  public final JButton getRedoBtn() {
    return redoBtn;
  }

  public final JButton getSurrenderBtn() {
    return surrenderBtn;
  }

  public final JButton getGameRulesBtn() {
    return rulesBtn;
  }

  public final JButton getDoneArrangingBtn() {
    return doneArrangingBtn;
  }

  public final JButton getStartGameBtn() {
    return startGameBtn;
  }

  public final void refreshBoardPanel() {
    boardPanel.setActiveTile(-1);
    boardPanel.refreshInnerBoardPanel();
    boardPanel.refreshInnerBoardPanelBackground();
    moveHistoryPanel.clearMoveHistory();
    if (!gameStateBoard.isGameStarted())
      moveHistoryPanel.printOpeningMessage();
  }

  private class MenuBarPanel extends JPanel {

    private JButton save, load, quit, undo, redo, surrender, rules;

    public MenuBarPanel() {
      this.setLayout(new FlowLayout());
      this.setPreferredSize(MENU_BAR_PANEL_DIMENSION);

      save = new JButton("Save");
      this.add(save);
      load = new JButton("Load");
      this.add(load);
      quit = new JButton("Quit");
      this.add(quit);

      this.add(new JSeparator(SwingConstants.HORIZONTAL));
      this.add(new JSeparator(SwingConstants.HORIZONTAL));
      this.add(new JSeparator(SwingConstants.HORIZONTAL));

      undo = new JButton("Undo");
      this.add(undo);
      redo = new JButton("Redo");
      this.add(redo);

      this.add(new JSeparator(SwingConstants.HORIZONTAL));
      this.add(new JSeparator(SwingConstants.HORIZONTAL));
      this.add(new JSeparator(SwingConstants.HORIZONTAL));

      surrender = new JButton("Surrender");
      this.add(surrender);
      rules = new JButton("Game Rules");
      this.add(rules);

      setAllButtons();
    }

    public void setAllButtons() {
      saveBtn = save;
      loadBtn = load;
      quitBtn = quit;
      undoBtn = undo;
      redoBtn = redo;
      surrenderBtn = surrender;
      rulesBtn = rules;
    }

  }

  private class MoveHistoryPanel extends JPanel {

    private JTextArea moveHistoryTextArea = new JTextArea();
    private String openingMessage;

    public MoveHistoryPanel() {
      this.setLayout(new BorderLayout());
      this.setBorder(new EmptyBorder(0, 0, 0, 5));
      this.setPreferredSize(MOVE_HISTORY_PANEL_DIMENSION);
      moveHistoryTextArea.setEditable(false);

      JLabel label = new JLabel("MOVE HISTORY");
      label.setHorizontalAlignment(JLabel.CENTER);
      label.setVerticalAlignment(JLabel.CENTER);
      label.setFont(new Font("SansSerif", Font.BOLD, 18));

      JButton doneArrangingBtn = new JButton("Done Arranging");
      JButton startGameBtn = new JButton("Start Game");
      setDoneArrangingBtn(doneArrangingBtn);
      setStartGameBtn(startGameBtn);

      JPanel initGameButtonsPanel = new JPanel();
      initGameButtonsPanel.add(doneArrangingBtn);
      initGameButtonsPanel.add(startGameBtn);

      moveHistoryTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      moveHistoryTextArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

      // set scrollable vertically as needed
      JScrollPane moveHistoryVScrollable = new JScrollPane(moveHistoryTextArea);
      moveHistoryVScrollable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

      this.add(label, BorderLayout.NORTH);
      this.add(moveHistoryVScrollable, BorderLayout.CENTER);
      this.add(initGameButtonsPanel, BorderLayout.SOUTH);

      printOpeningMessage();
    }

    public void addMoveHistory(String move) {
      moveHistoryTextArea.append(move);
    }

    public void clearMoveHistory() {
      moveHistoryTextArea.selectAll();
      moveHistoryTextArea.replaceSelection("");
    }

    public void printOpeningMessage() {
      clearMoveHistory();
      openingMessage = "Welcome to the Game of the Generals!\n\n" +
                       "Please arrange your pieces however\n" +
                       "you like within your territory (" +
                       gameStateBoard.getMoveMaker() + ").\n\n" +
                       "Once you are ready, please click the\n" +
                       "'Start Game' button below.\n";
      moveHistoryTextArea.append(openingMessage);
    }

    public void setDoneArrangingBtn(JButton doneArranging) {
      doneArrangingBtn = doneArranging;
    }

    public void setStartGameBtn(JButton startGame) {
      startGameBtn = startGame;
    }
  }

  private class InnerBoardPanel extends JPanel {

    private boolean enableHoverHighlight = true;
    private int activeTileId = -1;
    private int hoveredTileId;
    private final List<TilePanel> boardTiles;
    private final List<Integer> candidateMoveTiles;
    private Map<String, Move> pieceMoves;
    private Map<String, Image> blackPieceIcons;
    private Map<String, Image> whitePieceIcons;

    public InnerBoardPanel() {
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

    // TODO: add tileId on top left of tile panel
    private void highlightPieceMoves(int tileId) {

      final Tile sourceTile = gameStateBoard.getTile(tileId);

      if (sourceTile.isTileOccupied()) {
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

    private int getActiveTileId() {
      return this.activeTileId;
    }

    private TilePanel getActiveTilePanel() {
      return boardTiles.get(this.activeTileId);
    }

    private void setActiveTile(int newActiveTile) {
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

    private void deactivateActiveTile() {
      if (this.activeTileId != -1) {
        clearHighlights();
        boardTiles.get(activeTileId).setIsTileActive(false);
        this.activeTileId = -1;
      }
    }

    private void deactivateTile(int tileId) {
      if (boardTiles.get(tileId).isTileActive()) {
        boardTiles.get(tileId).deactivateTile();
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
    }

    private void refreshInnerBoardPanel() {
      for (int i = 0; i < boardTiles.size(); i++) {
        boardTiles.get(i).loadPieceIcons();
        boardTiles.get(i).assignTilePieceIcon();
        boardTiles.get(i).validate();
      }
    }

    private void refreshInnerBoardPanelBackground() {
      for (int i = 0; i < boardTiles.size(); i++) {
        boardTiles.get(i).assignTileColor();
        boardTiles.get(i).validate();
      }
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
    private final InnerBoardPanel boardPanel;
    private boolean isTileActive = false;
    private boolean isCandidateMoveTile = false;
    private Image iconHidden;
    private Image iconNormal;

    TilePanel(final InnerBoardPanel boardPanel,
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

      // TODO: improve READABILITY
      this.addMouseListener(new MouseListener() {

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
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

        @Override
        public void mouseExited(MouseEvent e) {
          if (gameStateBoard.getEndGameWinner() == null &&
              gameStateBoard.isGameStarted()) {
            if (isOccupyingPieceOwnedByMoveMaker()) {
              if (boardPanel.enableHoverHighlight)
                boardPanel.clearHighlights();
            }
          }
        }

        @Override
        public void mousePressed(MouseEvent e) {
          if (gameStateBoard.getEndGameWinner() == null &&
              gameStateBoard.isGameStarted()) {
            if (isOccupyingPieceOwnedByMoveMaker()) {
              if (gameStateBoard.getTile(tileId).isTileOccupied() && !isCandidateMoveTile) {
                if (gameStateBoard.getTile(tileId).getPiece().getPieceAlliance() ==
                    gameStateBoard.getMoveMaker()) {

                  boardPanel.clearHighlights();

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
          } else if (gameStateBoard.isGameInitialized() &&
                    !gameStateBoard.isGameStarted()) {
              if ((gameStateBoard.getMoveMaker() == Alliance.BLACK &&
                   tileId < BoardUtils.ALL_TILES_COUNT / 2) ||
                  (gameStateBoard.getMoveMaker() == Alliance.WHITE &&
                   tileId >= BoardUtils.ALL_TILES_COUNT / 2 )) {

              // pre-game active tile highlights
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

        @Override
        public void mouseReleased(MouseEvent e) {
          // TODO: CLEANUP create method for endgame check
          if (gameStateBoard.getEndGameWinner() == null &&
              gameStateBoard.isGameStarted()) {
            if (isOccupyingPieceOwnedByMoveMaker() || isCandidateMoveTile) {
              if (isCandidateMoveTile && boardPanel.getHoveredTileId() == tileId) {
                final Piece activePiece = gameStateBoard.getTile(boardPanel.getActiveTileId()).getPiece();

                Player player = gameStateBoard.getPlayer(activePiece.getPieceAlliance());
                player.makeMove(activePiece.getPieceCoords(), tileId);

                if (gameStateBoard.getLastMove() != null) {
                  Move lastMove = gameStateBoard.getLastMove();

                  if (lastMove.getMoveType() == "aggressive") {
                    Alliance superiorPieceAlliance =
                      lastMove.getEliminatedPiece().getPieceAlliance() ==
                      Alliance.BLACK ? Alliance.WHITE : Alliance.BLACK;

                    moveHistoryPanel.addMoveHistory("\nTurn " + lastMove.getTurnId() +
                        ": " + lastMove.getOriginCoords() +
                        " to " + lastMove.getDestinationCoords() +
                        " " + superiorPieceAlliance);
                  } else if (lastMove.getMoveType() == "draw") {
                    moveHistoryPanel.addMoveHistory("\nTurn " + lastMove.getTurnId() +
                        ": " + lastMove.getOriginCoords() +
                        " to " + lastMove.getDestinationCoords() +
                        " DRAW");
                  } else if (lastMove.getMoveType() == "normal") {
                    moveHistoryPanel.addMoveHistory("\nTurn " + lastMove.getTurnId() +
                        ": " + lastMove.getOriginCoords() +
                        " to " + lastMove.getDestinationCoords());
                  } else {
                    // TODO: Fix invalid move movehistory register
                    moveHistoryPanel.addMoveHistory("\nTurn " + lastMove.getTurnId() +
                        ": " + lastMove.getOriginCoords() +
                        " to " + lastMove.getDestinationCoords() + " INVALID MOVE");
                  }
                }

                if (gameStateBoard.isEndGame()) {
                  String endGameMessage = "GAME OVER, " +
                    gameStateBoard.getEndGameWinner() + " PLAYER WON!";
                  String separator = "\n**********************************\n";
                  moveHistoryPanel.addMoveHistory("\n" + separator +
                                                  endGameMessage + separator);
                  boardPanel.refreshInnerBoardPanelBackground();
                }

                boardPanel.refreshInnerBoardPanel();
                boardPanel.deactivateActiveTile();
                boardPanel.setHoverHighlight(true);
              }
            }
          } else if (gameStateBoard.isGameInitialized() &&
                      !gameStateBoard.isGameStarted()) {
            if ((gameStateBoard.getMoveMaker() == Alliance.BLACK &&
                 tileId < BoardUtils.ALL_TILES_COUNT / 2) ||
                (gameStateBoard.getMoveMaker() == Alliance.WHITE &&
                 tileId >= BoardUtils.ALL_TILES_COUNT / 2 )) {
              int hoveredTileId = boardPanel.getHoveredTileId();
              int activeTileId = boardPanel.getActiveTileId();

              if (gameStateBoard.isDebugMode()) {
                System.out.println("hoveredTileId=" + hoveredTileId +
                                   ";activeTileId=" + activeTileId +
                                   ";currenttileId=" + tileId);
              }

              if (hoveredTileId == tileId && hoveredTileId != activeTileId &&
                  activeTileId != -1) {

                if (gameStateBoard.getTile(tileId).isTileEmpty()) {
                  gameStateBoard.movePiece(activeTileId, tileId);

                  if (gameStateBoard.isDebugMode())
                    System.out.println(gameStateBoard.getTile(tileId).getPiece().getRank() +
                        " at " + activeTileId + " moved to " + tileId + "\n");
                } else {
                  gameStateBoard.swapPiece(activeTileId, tileId);

                  if (gameStateBoard.isDebugMode())
                    System.out.println(gameStateBoard.getTile(tileId).getPiece().getRank() +
                        " at " + activeTileId + " swapped with " +
                        gameStateBoard.getTile(activeTileId).getPiece().getRank() +
                        " at " + tileId + "\n");
                }


                boardPanel.refreshInnerBoardPanel();
                boardPanel.deactivateActiveTile();
              }
            }
          }
        }
      });
    }

    private void loadPieceIcons() {
      // Pre-load piece image
      if (gameStateBoard.getTile(tileId).isTileOccupied()) {
        final Tile currTile = gameStateBoard.getTile(tileId);
        Alliance pieceAlliance = currTile.getPiece().getPieceAlliance();
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

    private boolean isOccupyingPieceOwnedByMoveMaker() {
      if (gameStateBoard.getTile(tileId).isTileOccupied()) {
        if (gameStateBoard.getTile(tileId).getPiece().getPieceAlliance() ==
            gameStateBoard.getMoveMaker())
          return true;
      }
      return false;
    }

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
      }
    }

    private void setIsTileActive(boolean isTileActive) {
      this.isTileActive = isTileActive;
    }

    private void setIsCandidateMoveTile(boolean isCandidateMoveTile) {
      this.isCandidateMoveTile = isCandidateMoveTile;
    }

    private boolean isTileActive() {
      return this.isTileActive;
    }

    private void deactivateTile() {
      this.isTileActive = false;
    }

    private void assignTileColor() {
      if (gameStateBoard.getEndGameWinner() == null) {
        // Checkered  board
        // setBackground(this.tileId % 2 != 0 ? LIGHT_TILE_COLOR : DARK_TILE_COLOR);

        // Solid colored board on respective territories
        if (gameStateBoard.getTile(tileId).getTerritory() == Alliance.BLACK)
          setBackground(DARK_TILE_COLOR);
        else
          setBackground(LIGHT_TILE_COLOR);
      } else {
        setBackground(
            gameStateBoard.getEndGameWinner() == Alliance.BLACK ?
            DARK_TILE_COLOR : LIGHT_TILE_COLOR);
      }
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
