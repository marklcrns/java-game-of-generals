package engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import engine.Alliance;
import engine.Board;
import engine.Move;
import engine.Board.Tile;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public abstract class Piece {

  public int pieceCoords;
  public int legalPieceInstanceCount;
  public String rank;
  public int powerLevel;
  public final Alliance pieceAlliance;
  public final Map<String, Integer> mobility = Collections.unmodifiableMap(
      new HashMap<String, Integer>() {
    {
      put("u", -9);
      put("d", 9);
      put("l", -1);
      put("r", 1);
    }
  });
  private Map<Move, Integer> legalMoves;

  Piece(final int pieceCoords, final Alliance pieceAlliance) {
    this.pieceAlliance = pieceAlliance;
    this.pieceCoords = pieceCoords;
  }

  public int getCoords() {
    return this.pieceCoords;
  }

  public Tile getTile(Board board) {
    return board.getTile(pieceCoords);
  }

  public Alliance getAlliance() {
    return this.pieceAlliance;
  }

  public Map<Move, Integer> evaluateMoves(Board board) {

    int candidateMoveCoordinates;
    final Map<Move, Integer> legalMoves = new HashMap<>();

    final int upAdjacentTileCoords = this.pieceCoords - mobility.get("u");
    final int downAdjacentTileCoords = this.pieceCoords - mobility.get("d");
    final int leftAdjacentTileCoords = this.pieceCoords - mobility.get("l");
    final int rightAdjacentTileCoords = this.pieceCoords - mobility.get("r");
    final Tile upAdjacentTile = board.getTile(upAdjacentTileCoords);
    final Tile downAdjacentTile = board.getTile(downAdjacentTileCoords);
    final Tile leftAdjacentTile = board.getTile(leftAdjacentTileCoords);
    final Tile rightAdjacentTile = board.getTile(rightAdjacentTileCoords);

    // TODO: adjust for edge out of bounds/wrapping  moves
    // TODO: implement move type to bo automatically detected
    if (upAdjacentTile.isTileOccupied()) {
      if (upAdjacentTile.getPiece().getAlliance() != this.getAlliance())
        legalMoves.add(new Move(board, this, upAdjacentTileCoords).aggressive(), 2);
      else
        legalMoves.add(new Move(board, this, upAdjacentTileCoords).invalid(), 1);
    } else {
      legalMoves.add(new Move(board, this, upAdjacentTileCoords).normal(), 0);
    }

    if (downAdjacentTile.isTileOccupied()) {
      if (downAdjacentTile.getPiece().getAlliance() != this.getAlliance())
        legalMoves.add(new Move(board, this , downAdjacentTileCoords).aggressive(), 2);
      else
        legalMoves.add(new Move(board, this, downAdjacentTileCoords).invalid(), 1);
    } else {
      legalMoves.add(new Move(board, this, downAdjacentTileCoords).normal(), 0);
    }

    if (leftAdjacentTile.isTileOccupied()) {
      if (leftAdjacentTile.getPiece().getAlliance() != this.getAlliance())
        legalMoves.add(new Move(board, this, leftAdjacentTileCoords).aggressive(), 2);
      else
        legalMoves.add(new Move(board, this, leftAdjacentTileCoords).invalid(), 1);
    } else {
      legalMoves.add(new Move(board, this, leftAdjacentTileCoords).normal(), 0);
    }

    if (rightAdjacentTile.isTileOccupied()) {
      if (rightAdjacentTile.getPiece().getAlliance() != this.getAlliance())
        legalMoves.add(new Move(board, this, rightAdjacentTileCoords).aggressive();, 2);
      else
        legalMoves.add(new Move(board, this, rightAdjacentTileCoords).invalid(), 1);
    } else {
      legalMoves.add(new Move(board, this, rightAdjacentTileCoords).normal(), 0);
    }

    return null;
  }

  public abstract String getRank();
  public abstract int getLegalPieceInstanceCount();
  public abstract int getPowerLevel();
}
