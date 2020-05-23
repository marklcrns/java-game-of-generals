package engine.pieces;

import engine.Alliance;
import engine.player.Player;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class Major extends Piece {

  private final String rank = "M";
  private final int powerLevel = 7;
  private int legalPieceInstanceCount = 1;

  public Major(Player pieceOwner, Alliance pieceAlliance, int piecePosition) {
    super(pieceOwner, piecePosition, pieceAlliance);
  }

  public Major(Piece piece) {
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
    Major copy = new Major(this.pieceOwner, this.pieceAlliance, this.pieceCoords);
    return copy;
  }
}

