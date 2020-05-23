package engine.pieces;

import engine.Alliance;
import engine.player.Player;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class GeneralThree extends Piece {

  private final String rank = "G3";
  private final int powerLevel = 12;
  private final int legalPieceInstanceCount = 1;

  public GeneralThree(Player pieceOwner, Alliance pieceAlliance, int piecePosition) {
    super(pieceOwner, piecePosition, pieceAlliance);
  }

  public GeneralThree(Piece piece) {
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
    GeneralThree copy = new GeneralThree(this.pieceOwner, this.pieceAlliance, this.pieceCoords);
    return copy;
  }
}
