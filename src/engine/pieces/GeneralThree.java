package engine.pieces;

import engine.Alliance;
import utils.BoardUtils;
import engine.player.Player;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class GeneralThree extends Piece {

  private final String rank = BoardUtils.GENERAL_THREE_RANK;
  private final int powerLevel = 12;
  private final int legalPieceInstanceCount = 1;

  public GeneralThree(Player owner, Alliance alliance) {
    super(owner, alliance);
  }

  public GeneralThree(Player owner, Alliance alliance, int coords) {
    super(owner, alliance, coords);
  }

  public GeneralThree(Piece piece) {
    super(piece.getPieceOwner(), piece.getPieceAlliance(), piece.getPieceCoords());
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
    GeneralThree copy = new GeneralThree(this.pieceOwner, this.pieceAlliance, this.pieceCoords);
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
