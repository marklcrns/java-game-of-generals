package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.pieces.Captain;
import engine.pieces.Colonel;
import engine.pieces.Flag;
import engine.pieces.GeneralFive;
import engine.pieces.GeneralFour;
import engine.pieces.GeneralOne;
import engine.pieces.GeneralThree;
import engine.pieces.GeneralTwo;
import engine.pieces.LtCol;
import engine.pieces.LtOne;
import engine.pieces.LtTwo;
import engine.pieces.Major;
import engine.pieces.Piece;
import engine.pieces.Private;
import engine.pieces.Sergeant;
import engine.pieces.Spy;
import gui.GUI;
import utils.BoardUtils;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class Board {

  private static List<Tile> gameBoard;

  public Board() {
    initBoard();
  }

  private void initBoard() {
    gameBoard = new ArrayList<>();
    // Add new empty Tiles in board
    for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
      // set territory
      if (i < BoardUtils.ALL_TILES_COUNT / 2)
        this.addTile(i, Alliance.BLACK , false);
      else
        this.addTile(i, Alliance.WHITE , false);
    }
  }

  public Board(final BoardBuilder builder) {
    this.emptyBoard();

    for (Map.Entry<Integer, Piece> entry : builder.boardConfig.entrySet()) {
      if (gameBoard.get(entry.getKey()).isTileEmpty()) {
        // insert piece to tile
        gameBoard.get(entry.getKey()).insertPiece(entry.getValue());
        // change tile state
        // TODO: make setOccupied(true) built into the insertPiece method
        gameBoard.get(entry.getKey()).setOccupied(true);
      }
    };
  }

  public void emptyBoard() {
    gameBoard = new ArrayList<>();
    // Add new Tiles in board
    for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
      // set territory
      if (i < BoardUtils.ALL_TILES_COUNT / 2)
        this.addTile(i, Alliance.BLACK , false);
      else
        this.addTile(i, Alliance.WHITE , false);
    }
  }

  public Board buildBoard(BoardBuilder builder) {
    this.emptyBoard();

    for (Map.Entry<Integer, Piece> entry : builder.boardConfig.entrySet()) {
      if (gameBoard.get(entry.getKey()).isTileEmpty()) {
        // insert piece to tile
        gameBoard.get(entry.getKey()).insertPiece(entry.getValue());
        // change tile state
        // TODO: make setOccupied(true) built into the insertPiece method
        gameBoard.get(entry.getKey()).setOccupied(true);
      }
    };

    return this;
  }

  public void displayBoard() {
    new GUI(this);
  }

  public Tile getTile(int coordinates) {
    return gameBoard.get(coordinates);
  }

  private final void addTile(int tileId, Alliance territory, boolean occupied) {
    gameBoard.add(new Tile(tileId, territory, occupied));
  }

  public List<Tile> getBoard() {
    return gameBoard;
  }

  @Override
  public String toString() {
    return null;
  }


  public static class BoardBuilder {

    private Map<Integer, Piece> boardConfig;
    private Alliance moveMaker;

    public BoardBuilder() {
      this.boardConfig = new HashMap<>();
    }

    public BoardBuilder createDemoBoardBuild() {
      BoardBuilder builder = new BoardBuilder();
      int[] row = {0, 8, 17, 26};
      // Black territory
      int boardOffset = 0;
      // row 0
      builder.setPiece(new GeneralTwo(Alliance.BLACK, boardOffset + row[0] + 9));
      builder.setPiece(new Major(Alliance.BLACK, boardOffset + row[0] + 8));
      builder.setPiece(new Private(Alliance.BLACK, boardOffset + row[0] + 7));
      builder.setPiece(new Sergeant(Alliance.BLACK, boardOffset + row[0] + 6));
      builder.setPiece(new LtOne(Alliance.BLACK, boardOffset + row[0] + 5));
      builder.setPiece(new Private(Alliance.BLACK, boardOffset + row[0] + 4));
      builder.setPiece(new Flag(Alliance.BLACK, boardOffset + row[0] + 3));
      builder.setPiece(new LtTwo(Alliance.BLACK, boardOffset + row[0] + 2));
      builder.setPiece(new Private(Alliance.BLACK, boardOffset + row[0] + 1));
      // row 1
      builder.setPiece(new Spy(Alliance.BLACK, boardOffset + row[1] + 8));
      builder.setPiece(new Private(Alliance.BLACK, boardOffset + row[1] + 7));
      builder.setPiece(new Captain(Alliance.BLACK, boardOffset + row[1] + 5));
      builder.setPiece(new Spy(Alliance.BLACK, boardOffset + row[1] + 4));
      builder.setPiece(new Colonel(Alliance.BLACK, boardOffset + row[1] + 3));
      builder.setPiece(new Private(Alliance.BLACK, boardOffset + row[1] + 2));
      builder.setPiece(new LtCol(Alliance.BLACK, boardOffset + row[1] + 1));
      // row 2
      builder.setPiece(new GeneralThree(Alliance.BLACK, boardOffset + row[2] + 9));
      builder.setPiece(new Private(Alliance.BLACK, boardOffset + row[2] + 6));
      builder.setPiece(new GeneralFour(Alliance.BLACK, boardOffset + row[2] + 5));
      // row 3
      builder.setPiece(new GeneralOne(Alliance.BLACK, boardOffset + row[3] + 3));
      builder.setPiece(new GeneralFive(Alliance.BLACK, boardOffset + row[3] + 2));
      builder.setPiece(new GeneralFive(Alliance.WHITE, boardOffset + row[3] + 1));

      // White territory
      boardOffset = BoardUtils.ALL_TILES_COUNT / 2;
      // row 0
      builder.setPiece(new GeneralFive(Alliance.BLACK, boardOffset + row[0] + 1));
      builder.setPiece(new GeneralFive(Alliance.WHITE, boardOffset + row[0] + 2));
      builder.setPiece(new GeneralOne(Alliance.WHITE, boardOffset + row[0] + 3));
      // row 1
      builder.setPiece(new GeneralFour(Alliance.WHITE, boardOffset + row[1] + 5));
      builder.setPiece(new Private(Alliance.WHITE, boardOffset + row[1] + 6));
      builder.setPiece(new GeneralThree(Alliance.WHITE, boardOffset + row[1] + 9));
      // row 2
      builder.setPiece(new LtCol(Alliance.WHITE, boardOffset + row[2] + 1));
      builder.setPiece(new Private(Alliance.WHITE, boardOffset + row[2] + 2));
      builder.setPiece(new Colonel(Alliance.WHITE, boardOffset + row[2] + 3));
      builder.setPiece(new Spy(Alliance.WHITE, boardOffset + row[2] + 4));
      builder.setPiece(new Captain(Alliance.WHITE, boardOffset + row[2] + 5));
      builder.setPiece(new Private(Alliance.WHITE, boardOffset + row[2] + 7));
      builder.setPiece(new Spy(Alliance.WHITE, boardOffset + row[2] + 8));
      // row 3
      builder.setPiece(new Private(Alliance.WHITE, boardOffset + row[3] + 1));
      builder.setPiece(new LtTwo(Alliance.WHITE, boardOffset + row[3] + 2));
      builder.setPiece(new Flag(Alliance.WHITE, boardOffset + row[3] + 3));
      builder.setPiece(new Private(Alliance.WHITE, boardOffset + row[3] + 4));
      builder.setPiece(new LtOne(Alliance.WHITE, boardOffset + row[3] + 5));
      builder.setPiece(new Sergeant(Alliance.WHITE, boardOffset + row[3] + 6));
      builder.setPiece(new Private(Alliance.WHITE, boardOffset + row[3] + 7));
      builder.setPiece(new Major(Alliance.WHITE, boardOffset + row[3] + 8));
      builder.setPiece(new GeneralTwo(Alliance.WHITE, boardOffset + row[3] + 9));
      return builder;
    }

    public void setPiece(final Piece piece) {
      // checks if in the correct territory and piece legal count
      if (isPieceInCorrectTerritory(piece) &&
          isLegalPieceInstanceChecker(piece))
        boardConfig.put(piece.getCoords(), piece);
      // TODO: Throw wrong territory exception here
    }

    public boolean isPieceInCorrectTerritory(Piece piece) {
      if ((piece.getAlliance() == Alliance.BLACK &&
            piece.getCoords() < BoardUtils.ALL_TILES_COUNT / 2) ||
          (piece.getAlliance() == Alliance.WHITE &&
            piece.getCoords() > BoardUtils.ALL_TILES_COUNT / 2))
        return true;
      System.out.println(piece.getAlliance() + " " +
                         piece.getRank() + " at Tile " +
                         piece.getCoords() + " is in illegal territory." +
                         " Piece not inserted.");
      return false;
    }

    public boolean isLegalPieceInstanceChecker(Piece piece) {
      // TODO: check for territory as well
      final int legalPieceInstanceCount = piece.getLegalPieceInstanceCount();
      int pieceCounterBlack = 0;
      int pieceCounterWhite = 0;
      for (Map.Entry<Integer, Piece> entry : this.boardConfig.entrySet()) {
        if (piece.equals(entry.getValue())) {
          if (piece.getAlliance() == Alliance.BLACK)
            pieceCounterBlack++;
          else
            pieceCounterWhite++;
        }

        if (pieceCounterBlack > legalPieceInstanceCount &&
            pieceCounterWhite > legalPieceInstanceCount) {
          System.out.println(piece.getRank() + " exceeded maximum instance");
          return false;
        }
      }
      return true;
    }

    public void setMoveMaker(Alliance moveMaker) {
      this.moveMaker = moveMaker;
    }

  }


  public static class Tile {

    private final int tileId;
    private final Alliance territory;
    private boolean occupied;
    private Piece piece;

    public Tile(int tileId, Alliance territory, boolean occupied) {
      this.tileId = tileId;
      this.territory = territory;
      this.occupied = occupied;
    }

    public boolean isTileEmpty() {
      if (!this.occupied) {
        return true;
      }
      return false;
    }

    public boolean isTileOccupied() {
      if (this.occupied) {
        return true;
      }
      return false;
    }

    public void setOccupied(boolean occupied) {
      this.occupied = occupied;
    }

    public int getTileId() {
      return this.tileId;
    }

    public Alliance getTerritory() {
      return this.territory;
    }

    public Piece getPiece() {
      return this.piece;
    }

    public boolean insertPiece(Piece piece) {
      if (!this.occupied) {
        this.piece = piece;
        return true;
      }
      return false;
    }

    public boolean replacePiece(Piece piece) {
      if (this.occupied) {
        this.piece = piece;
        return true;
      }
      return false;
    }

    public void clearTile() {
      this.piece = null;
      this.occupied = false;
    }

    @Override
    public String toString() {
      if (this.occupied)
        return "Tile " + this.tileId + " contains " +
          this.piece.getAlliance() + " " + this.piece.getRank();
      else
        return "Tile " + this.tileId + " is empty";
    }
  }
}
