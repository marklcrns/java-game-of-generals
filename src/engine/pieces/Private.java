package engine.pieces;

import engine.Alliance;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class Private extends Piece {

  private final String rank = "P";
  private final int powerLevel = 2;
  private int legalPieceInstanceCount = 6;

  public Private(Alliance pieceAlliance, int piecePosition) {
    super(piecePosition, pieceAlliance);
  }

  @Override
  public final String getRank() {
    return this.rank;
  }

  @Override
  public final int getLegalPieceInstanceCount() {
    return this.legalPieceInstanceCount;
  }

  @Override
  public final int getPowerLevel() {
    return this.powerLevel;
  }
}
