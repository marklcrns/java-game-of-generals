package engine.pieces;

import java.util.ArrayList;
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

  public Alliance getAlliance() {
    return this.pieceAlliance;
  }

  public Map<Move, Integer> evaluateMoves(Board board) {

    int candidateMoveCoordinate;
    final Map<Move, Integer> legalMoves = new HashMap<>();

    final Tile upAdjacentTile = board.getTile(
        this.pieceCoords - mobility.get("u"));
    final Tile downAdjacentTilePiece = board.getTile(
        this.pieceCoords - mobility.get("d"));
    final Tile leftAdjacentTilePiece = board.getTile(
        this.pieceCoords - mobility.get("l"));
    final Tile rightAdjacentTilePiece = board.getTile(
        this.pieceCoords - mobility.get("r"));

    // TODO: adjust for edge out of bounds/wrapping  moves

    if (upAdjacentTile.isTileOccupied()) {
      if (upAdjacentTile.getPiece().getAlliance() != this.getAlliance())
        legalMoves.add(aggressiveMove(board, upAdjacentTile), 2);
      else
        legalMoves.add(invalidFriendlyMove(board, upAdjacentTile), 1);
    } else {
      legalMoves.add(normalMove(board, upAdjacentTile), 0);
    }

    if (downAdjacentTile.isTileOccdownied()) {
      if (downAdjacentTile.getPiece().getAlliance() != this.getAlliance())
        legalMoves.add(aggressiveMove(board, downAdjacentTile), 2);
      else
        legalMoves.add(invalidFriendlyMove(board, downAdjacentTile), 1);
    } else {
      legalMoves.add(normalMove(board, downAdjacentTile), 0);
    }

    if (leftAdjacentTile.isTileOccleftied()) {
      if (leftAdjacentTile.getPiece().getAlliance() != this.getAlliance())
        legalMoves.add(aggressiveMove(board, leftAdjacentTile), 2);
      else
        legalMoves.add(invalidFriendlyMove(board, leftAdjacentTile), 1);
    } else {
      legalMoves.add(normalMove(board, leftAdjacentTile), 0);
    }

    if (rightAdjacentTile.isTileOccrightied()) {
      if (rightAdjacentTile.getPiece().getAlliance() != this.getAlliance())
        legalMoves.add(aggressiveMove(board, rightAdjacentTile), 2);
      else
        legalMoves.add(invalidFriendlyMove(board, rightAdjacentTile), 1);
    } else {
      legalMoves.add(normalMove(board, rightAdjacentTile), 0);
    }

    return null;
  }

  public abstract String getRank();
  public abstract int getLegalPieceInstanceCount();
}
