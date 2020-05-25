package engine.pieces;

import engine.Alliance;
import utils.BoardUtils;
import engine.player.Player;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class GeneralTwo extends Piece {

  private final String rank = BoardUtils.GENERAL_TWO_RANK;
  private final int powerLevel = 11;
  private int legalPieceInstanceCount = 1;

  public GeneralTwo(Player owner, Alliance alliance) {
    super(owner, alliance);
  }

  public GeneralTwo(Player owner, Alliance alliance, int coords) {
    super(owner, alliance, coords);
  }

  public GeneralTwo(Piece piece) {
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
    GeneralTwo copy = new GeneralTwo(this.pieceOwner, this.pieceAlliance, this.pieceCoords);
    return copy;
  }
}

