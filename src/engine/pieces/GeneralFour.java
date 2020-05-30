package engine.pieces;

import engine.Alliance;
import utils.BoardUtils;
import engine.player.Player;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class GeneralFour extends Piece {

  private final String rank = BoardUtils.GENERAL_FOUR_RANK;
  private final int powerLevel = 13;
  private final int legalPieceInstanceCount = 1;

  public GeneralFour(Player owner, Alliance alliance) {
    super(owner, alliance);
  }

  public GeneralFour(Player owner, Alliance alliance, int coords) {
    super(owner, alliance, coords);
  }

  public GeneralFour(Piece piece) {
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
  public final Piece clone() {
    GeneralFour copy = new GeneralFour(this.pieceOwner, this.pieceAlliance, this.pieceCoords);
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

