package engine.pieces;

import engine.Alliance;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class LtCol extends Piece {

  private final String rank = "LT_COL";
  private final int powerLevel = 8;
  private int legalPieceInstanceCount = 1;

  public LtCol(Alliance pieceAlliance, int piecePosition) {
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

