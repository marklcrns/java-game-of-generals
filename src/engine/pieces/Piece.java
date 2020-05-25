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
import engine.player.Player;
import utils.BoardUtils;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public abstract class Piece {

  public String rank;
  public int powerLevel;
  public int pieceCoords;
  public int legalPieceInstanceCount;
  public final Player pieceOwner;
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
  private Map<String, Move> moveSet;

  public Piece(final Player owner, final Alliance alliance) {
    this.pieceOwner = owner;
    this.pieceAlliance = alliance;
    this.pieceCoords = -1;
  }

  // TODO: remove pieceCoords and rely only to tileId
  public Piece(final Player owner, final Alliance alliance,
               final int coords) {
    this.pieceOwner = owner;
    this.pieceAlliance = alliance;
    this.pieceCoords = coords;
  }

  public Player getPieceOwner() {
    return this.pieceOwner;
  }

  public int getPieceCoords() {
    return this.pieceCoords;
  }

  public void setPieceCoords(int coords) {
    this.pieceCoords = coords;
  }

  public Tile getTile(Board board) {
    return board.getTile(pieceCoords);
  }

  public Alliance getPieceAlliance() {
    return this.pieceAlliance;
  }

  public void updateCoords(int newCoords) {
    this.pieceCoords = newCoords;
  }

  // TODO: relate piece to board automatically
  public Map<String, Move> evaluateMoves(Board board) {
    moveSet = new HashMap<String, Move>();

    // prevent out of bounds and board wrapping moves

    final int upAdjacentPieceCoords = this.pieceCoords + mobility.get("u");
    if (pieceCoords > BoardUtils.SECOND_ROW_INIT)
      moveSet.put("up", new Move(pieceOwner, board, pieceCoords, upAdjacentPieceCoords));

    final int downAdjacentPieceCoords = this.pieceCoords + mobility.get("d");
    if (pieceCoords < BoardUtils.LAST_ROW_INIT)
      moveSet.put("down", new Move(pieceOwner, board, pieceCoords, downAdjacentPieceCoords));

    final int leftAdjacentPieceCoords = this.pieceCoords + mobility.get("l");
    if (this.pieceCoords % 9 != 0)
      moveSet.put("left", new Move(pieceOwner, board, pieceCoords, leftAdjacentPieceCoords));

    final int rightAdjacentPieceCoords = this.pieceCoords + mobility.get("r");
    if (rightAdjacentPieceCoords % 9 != 0)
      moveSet.put("right", new Move(pieceOwner, board, pieceCoords, rightAdjacentPieceCoords));

    return moveSet;
  }

  public abstract String getRank();
  public abstract int getLegalPieceInstanceCount();
  public abstract int getPowerLevel();
  public abstract Piece makeCopy();
}
