package engine.pieces;

import engine.Alliance;
import utils.BoardUtils;
import engine.player.Player;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class GeneralOne extends Piece {

  private final String rank = BoardUtils.GENERAL_ONE_RANK;
  private final int powerLevel = 10;
  private int legalPieceInstanceCount = 1;

  public GeneralOne(Player pieceOwner, Alliance pieceAlliance, int piecePosition) {
    super(pieceOwner, piecePosition, pieceAlliance);
  }

  public GeneralOne(Piece piece) {
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
    GeneralOne copy = new GeneralOne(this.pieceOwner, this.pieceAlliance, this.pieceCoords);
    return copy;
  }
}

