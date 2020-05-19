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
import utils.BoardUtils;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class Board {

  private static List<Tile> gameBoard;

  public Board() {
    initEmptyBoard();

    final BoardBuilder builder = createStandardBoard();
    for (Map.Entry<Integer, Piece> entry : builder.boardConfig.entrySet()) {
      if (gameBoard.get(entry.getKey()).isTileEmpty()) {
        // insert piece to tile
        gameBoard.get(entry.getKey()).insertPiece(entry.getValue());
        // change tile state
        gameBoard.get(entry.getKey()).setOccupied(true);
      }
    };
  }

  public static BoardBuilder createStandardBoard() {
    BoardBuilder builder = new BoardBuilder();
    int[] row = {0, 8, 17, 26};
    // Black territory
    int boardOffset = 0;
    // row 0
    builder.setPiece(new GeneralTwo(Territory.BLACK, boardOffset + row[3] + 9));
    builder.setPiece(new Major(Territory.BLACK, boardOffset + row[0] + 8));
    builder.setPiece(new Private(Territory.BLACK, boardOffset + row[0] + 7));
    builder.setPiece(new Sergeant(Territory.BLACK, boardOffset + row[0] + 6));
    builder.setPiece(new LtOne(Territory.BLACK, boardOffset + row[0] + 5));
    builder.setPiece(new Private(Territory.BLACK, boardOffset + row[0] + 4));
    builder.setPiece(new Flag(Territory.BLACK, boardOffset + row[0] + 3));
    builder.setPiece(new LtTwo(Territory.BLACK, boardOffset + row[0] + 2));
    builder.setPiece(new Private(Territory.BLACK, boardOffset + row[0] + 1));
    // row 1
    builder.setPiece(new Spy(Territory.BLACK, boardOffset + row[1] + 8));
    builder.setPiece(new Private(Territory.BLACK, boardOffset + row[1] + 7));
    builder.setPiece(new Captain(Territory.BLACK, boardOffset + row[1] + 5));
    builder.setPiece(new Spy(Territory.BLACK, boardOffset + row[1] + 4));
    builder.setPiece(new Colonel(Territory.BLACK, boardOffset + row[1] + 3));
    builder.setPiece(new Private(Territory.BLACK, boardOffset + row[1] + 2));
    builder.setPiece(new LtCol(Territory.BLACK, boardOffset + row[1] + 1));
    // row 2
    builder.setPiece(new GeneralThree(Territory.BLACK, boardOffset + row[2] + 9));
    builder.setPiece(new Private(Territory.BLACK, boardOffset + row[2] + 6));
    builder.setPiece(new GeneralFour(Territory.BLACK, boardOffset + row[2] + 5));
    // row 3
    builder.setPiece(new GeneralOne(Territory.BLACK, boardOffset + row[3] + 3));
    builder.setPiece(new GeneralFive(Territory.BLACK, boardOffset + row[3] + 2));

    // White territory
    boardOffset = BoardUtils.ALL_TILES_COUNT / 2;
    // row 0
    builder.setPiece(new GeneralFive(Territory.WHITE, boardOffset + row[0] + 2));
    builder.setPiece(new GeneralOne(Territory.WHITE, boardOffset + row[0] + 3));
    // row 1
    builder.setPiece(new GeneralFour(Territory.WHITE, boardOffset + row[1] + 5));
    builder.setPiece(new Private(Territory.WHITE, boardOffset + row[1] + 6));
    builder.setPiece(new GeneralThree(Territory.WHITE, boardOffset + row[1] + 9));
    // row 2
    builder.setPiece(new LtCol(Territory.WHITE, boardOffset + row[2] + 1));
    builder.setPiece(new Private(Territory.WHITE, boardOffset + row[2] + 2));
    builder.setPiece(new Colonel(Territory.WHITE, boardOffset + row[2] + 3));
    builder.setPiece(new Spy(Territory.WHITE, boardOffset + row[2] + 4));
    builder.setPiece(new Captain(Territory.WHITE, boardOffset + row[2] + 5));
    builder.setPiece(new Private(Territory.WHITE, boardOffset + row[2] + 7));
    builder.setPiece(new Spy(Territory.WHITE, boardOffset + row[2] + 8));
    // row 3
    builder.setPiece(new Private(Territory.WHITE, boardOffset + row[3] + 1));
    builder.setPiece(new LtTwo(Territory.WHITE, boardOffset + row[3] + 2));
    builder.setPiece(new Flag(Territory.WHITE, boardOffset + row[3] + 3));
    builder.setPiece(new Private(Territory.WHITE, boardOffset + row[3] + 4));
    builder.setPiece(new LtOne(Territory.WHITE, boardOffset + row[3] + 5));
    builder.setPiece(new Sergeant(Territory.WHITE, boardOffset + row[3] + 6));
    builder.setPiece(new Private(Territory.WHITE, boardOffset + row[3] + 7));
    builder.setPiece(new Major(Territory.WHITE, boardOffset + row[3] + 8));
    builder.setPiece(new GeneralTwo(Territory.WHITE, boardOffset + row[3] + 9));
    return builder;
  }

  public static void initEmptyBoard() {
    final List<Tile> emptyBoard = new ArrayList<>();
    // Create board containing Tiles
    for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
      // set territory
      if (i < BoardUtils.ALL_TILES_COUNT / 2)
        emptyBoard.add(new Tile(Territory.BLACK, false));
      else
        emptyBoard.add(new Tile(Territory.WHITE, false));
    }

    gameBoard = emptyBoard;
  }

  public static class BoardBuilder {

    private Map<Integer, Piece> boardConfig;
    private Territory moveMaker;

    public BoardBuilder() {
      this.boardConfig = new HashMap<>();
    }

    public void setPiece(final Piece piece) {
      // checks if in the correct territory and piece legal count
      if (isPieceInCorrectTerritory(piece) &&
          isLegalPieceInstanceChecker(piece))
        boardConfig.put(piece.getCoords(), piece);
      // TODO: Throw wrong territory exception here
    }

    public boolean isPieceInCorrectTerritory(Piece piece) {
      if ((piece.getAlliance() == Territory.BLACK &&
            piece.getCoords() < BoardUtils.ALL_TILES_COUNT / 2) ||
          (piece.getAlliance() == Territory.WHITE &&
            piece.getCoords() > BoardUtils.ALL_TILES_COUNT / 2))
        return true;
      System.out.println(piece.getRank() + " at Tile " +
                         piece.getCoords() + " is in illegal territory");
      return false;
    }

    public boolean isLegalPieceInstanceChecker(Piece piece) {
      // TODO: check for territory as well
      final int legalPieceInstanceCount = piece.getLegalPieceInstanceCount();
      int pieceCounterBlack = 0;
      int pieceCounterWhite = 0;
      for (Map.Entry<Integer, Piece> entry : this.boardConfig.entrySet()) {
        if (piece.equals(entry.getValue())) {
          if (piece.getAlliance() == Territory.BLACK)
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

    public void setMoveMaker(Territory moveMaker) {
      this.moveMaker = moveMaker;
    }

  }

  public static class Tile {

    private final Territory alliance;
    private boolean occupied;
    private Piece piece;

    public Tile(Territory alliance, boolean occupied) {
      this.alliance = alliance;
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

    public Piece getPiece() {
      return this.piece;
    }

    public Territory getTerritory() {
      return this.alliance;
    }

    public boolean insertPiece(Piece piece) {
      if (!this.occupied) {
        this.piece = piece;
        return true;
      }
      return false;
    }
  }

  public Tile getTile(int coordinates) {
    return gameBoard.get(coordinates);
  }

  public List<Tile> getBoard() {
    return gameBoard;
  }

  @Override
  public String toString() {
    return null;
  }

}
