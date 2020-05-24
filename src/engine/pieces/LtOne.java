package engine.pieces;

import engine.Alliance;
import utils.BoardUtils;
import engine.player.Player;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class LtOne extends Piece {

  private final String rank = BoardUtils.LT_ONE_RANK;
  private final int powerLevel = 5;
  private int legalPieceInstanceCount = 1;

  public LtOne(Player pieceOwner, Alliance pieceAlliance, int piecePosition) {
    super(pieceOwner, piecePosition, pieceAlliance);
  }

  public LtOne(Piece piece) {
    super(piece.getPieceOwner(), piece.getCoords(), piece.getAlliance());
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

  @Override
  public final Piece makeCopy() {
    LtOne copy = new LtOne(this.pieceOwner, this.pieceAlliance, this.pieceCoords);
    return copy;
  }
}

