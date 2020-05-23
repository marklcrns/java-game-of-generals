package engine.pieces;

import engine.Alliance;
import engine.player.Player;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class Spy extends Piece {

  private final String rank = "S";
  private final int powerLevel = 999;
  private int legalPieceInstanceCount = 2;

  public Spy(Player pieceOwner, Alliance pieceAlliance, int piecePosition) {
    super(pieceOwner, piecePosition, pieceAlliance);
  }

  public Spy(Piece piece) {
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
    Spy copy = new Spy(this.pieceOwner, this.pieceAlliance, this.pieceCoords);
    return copy;
  }
}

