package engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import engine.Territory;
import engine.Board;
import engine.Move;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public abstract class Piece {

  public int pieceCoords;
  public int legalPieceInstanceCount;
  public String rank;
  public final Territory pieceAlliance;
  public final Map<String, Integer> mobility = Collections.unmodifiableMap(
      new HashMap<String, Integer>() {
    {
      put("u", -9);
      put("d", 9);
      put("l", -1);
      put("r", 1);
    }
  });
  private Collection<Move> legalMoves;

  Piece(final int pieceCoords, final Territory pieceAlliance) {
    this.pieceAlliance = pieceAlliance;
    this.pieceCoords = pieceCoords;
  }

  public int getCoords() {
    return this.pieceCoords;
  }

  public Territory getAlliance() {
    return this.pieceAlliance;
  }

  public Collection<Move> evalLegalMoves(Board board) {

    int candidateMoveCoordinate;
    final Collection<Move> legalMoves = new ArrayList<>();

    for (int i = 0; i < board.getBoard().size(); i++) {
      boolean upAdjacentTile = board.getTile(this.pieceCoords - mobility.get("u")).isTileOccupied();
      boolean downAdjacentTile = board.getTile(this.pieceCoords - mobility.get("d")).isTileOccupied();
      boolean leftAdjacentTile = board.getTile(this.pieceCoords - mobility.get("l")).isTileOccupied();
      boolean rightAdjacentTile = board.getTile(this.pieceCoords - mobility.get("r")).isTileOccupied();
    }

    return null;
  }

  public abstract String getRank();
  public abstract int getLegalPieceInstanceCount();
}
