package engine.pieces;

import engine.Alliance;
import utils.BoardUtils;
import engine.player.Player;

/**
 * Private piece class that inherits from abstract Piece class.
 *
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class Private extends Piece {

  /** Rank of the piece */
  private final String rank = BoardUtils.PRIVATE_RANK;

  /** Power level of the piece to compare ranks */
  private final int powerLevel = 2;

  /** Allowed amount of piece instance owned by a Player in a single game */
  private final int legalPieceInstanceCount = 6;

  /**
   * Constructor that takes in the owner Player, and Alliance of this piece.
   * Sets pieceCoords to -1 temporarily.
   */
  public Private(final Player owner, final Alliance alliance) {
    super(owner, alliance);
  }

  /**
   * Constructor that takes in the owner Player, Alliance and coordinates of
   * this Piece.
   */
  public Private(final Player owner, final Alliance alliance,
                 final int coords) {
    super(owner, alliance, coords);
  }

  /**
   * Gets the current rank of this specific Piece instance.
   * @return String rank field.
   */
  @Override
  public final String getRank() {
    return this.rank;
  }

  /**
   * Gets the allowed legal instance per Player of this specific piece.
   * @return int legalPieceInstanceCount field.
   */
  @Override
  public final int getLegalPieceInstanceCount() {
    return this.legalPieceInstanceCount;
  }

  /**
   * Gets this Pieces instance power level.
   * @return int powerLevel field.
   */
  @Override
  public final int getPowerLevel() {
    return this.powerLevel;
  }

  /**
   * Create deep copy of this specific Piece instance.
   * @return Piece deep copy if this Piece instance.
   */
  @Override
  public final Piece clone() {
    final Private copy = new Private(this.pieceOwner, this.pieceAlliance, this.pieceCoords);
    return copy;
  }

  @Override
  public String toString() {
    return "piece=" + rank + ";powerLevel=" + powerLevel +
           ";pieceCoords=" + pieceCoords +
           ";legalPieceInstanceCount=" + legalPieceInstanceCount +
           ";pieceOwner=" + pieceOwner.getAlliance() +
           ";pieceAlliance=" + pieceAlliance;
  }
}
