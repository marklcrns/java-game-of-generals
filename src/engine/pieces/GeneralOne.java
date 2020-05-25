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

  public GeneralOne(Player owner, Alliance alliance) {
    super(owner, alliance);
  }

  public GeneralOne(Player owner, Alliance alliance, int coords) {
    super(owner, alliance, coords);
  }

  public GeneralOne(Piece piece) {
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
    GeneralOne copy = new GeneralOne(this.pieceOwner, this.pieceAlliance, this.pieceCoords);
    return copy;
  }
}

