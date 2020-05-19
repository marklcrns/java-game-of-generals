package engine.pieces;

import engine.Territory;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class Colonel extends Piece {

  private String rank = "C";
  private int legalPieceInstanceCount = 1;

  public Colonel(Territory pieceAlliance, int piecePosition) {
    super(piecePosition, pieceAlliance);
  }

  @Override
  public String getRank() {
    return this.rank;
  }

  @Override
  public final int getLegalPieceInstanceCount() {
    return this.legalPieceInstanceCount;
  }
}

