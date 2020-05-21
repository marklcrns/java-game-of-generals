package engine;

import engine.pieces.Piece;
import engine.player.Player;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class Move {
  private final Board board;
  private final Player player;
  private final int playerTurnId;
  private final Piece currentPiece;
  private final int originCoords;
  private String moveType;
  private int destinationCoords;
  private boolean isExecuted = false;

  public Move(final Board board,
              final Player player,
              final Piece piece,
              final int destinationCoords) {
    this.board = board;
    this.player = player;
    this.playerTurnId = player.getCurrentTurnId();
    this.currentPiece = piece;
    this.destinationCoords = destinationCoords;
    this.originCoords = piece.getCoords();
  }

  public void makeMove(int destinationCoords) {
    this.destinationCoords = destinationCoords;
  }

  public boolean execute() {
    switch (this.moveType) {
      case "aggressive":
        if (isTargetPieceEliminated(currentPiece, board.getTile(destinationCoords).getPiece()))
          board.getTile(destinationCoords).replacePiece(currentPiece);
        else
          board.getTile(currentPiece.getCoords()).clearTile();
        this.player.recordMove(this.player.getCurrentTurnId(), this);
        this.isExecuted = true;
        break;
      case "normal":
          board.getTile(destinationCoords).insertPiece(currentPiece);
        this.isExecuted = true;
        break;
      case "invalid":
        return false;
      default:
        return false;
    }
    this.isExecuted = true;
    return true;
  }

  public Move aggressive() {
    this.moveType = "aggressive";
    return this;
  }

  public Move normal() {
    this.moveType = "normal";
    return this;
  }

  public Move invalid() {
    this.moveType = "invalid";
    return this;
  }

  private boolean isTargetPieceEliminated(Piece aggressorPiece, Piece targetPiece) {
    if (aggressorPiece.getPowerLevel() > targetPiece.getPowerLevel()) {
      return true;
    }
    return false;
  }

  public Piece getCurrentPiece() {
    return this.currentPiece;
  }

  public int getDestinationCoords() {
    return this.destinationCoords;
  }

  public int getOriginCoords() {
    return this.originCoords;
  }

  public boolean undoExecution() {
    if (!this.isExecuted) {
      return false;
    } else {
      this.isExecuted = false;
    }
    return true;
  }

  public boolean redoExecution() {
    // TODO: implement
    return true;
  }

}
