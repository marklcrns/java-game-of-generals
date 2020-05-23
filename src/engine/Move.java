package engine;

import java.util.Map;

import engine.Board.Tile;
import engine.pieces.Piece;
import engine.player.Player;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class Move {
  private final Board board;
  private final Player player;
  private final int sourcePieceCoords;
  private final int targetPieceCoords;
  private final Piece sourcePieceCopy;
  private final Piece targetPieceCopy;
  private String moveType;
  private boolean isExecuted = false;

  public Move(final Player player,
              final Board board,
              final int sourcePieceCoords,
              final int targetPieceCoords) {
    this.player = player;
    this.board = board;
    this.sourcePieceCoords = sourcePieceCoords;
    this.targetPieceCoords = targetPieceCoords;
    this.sourcePieceCopy = board.getTile(sourcePieceCoords).getPiece().makeCopy();
    if (board.getTile(targetPieceCoords).isTileOccupied())
      this.targetPieceCopy = board.getTile(targetPieceCoords).getPiece().makeCopy();
    else
      this.targetPieceCopy = null;
    evaluateMove();
  }

  private void evaluateMove() {
    if (board.getTile(targetPieceCoords).isTileOccupied())
      if (targetPieceCopy.getAlliance() != sourcePieceCopy.getAlliance())
        if (isSameRank())
          moveType = "draw";
        else
          moveType = "aggressive";
      else
        moveType = "invalid";
    else
      moveType = "normal";
  }

  public boolean legalMoveCheck() {
    // check if one of possible piece moves
    Map<String, Move> possiblePieceMoves =
      this.board.getTile(sourcePieceCoords).getPiece().evaluateMoves(board);

    for (Map.Entry<String, Move> entry : possiblePieceMoves.entrySet()) {
      if (entry.getValue().getDestinationCoords() == targetPieceCoords) {
        return true;
      }
    };
    moveType = "invalid";

    // TODO: Tidy up
    String targetPiece;
    if (targetPieceCopy == null)
      targetPiece = "NONE";
    else
      targetPiece = targetPieceCopy.getRank();
    System.out.println(sourcePieceCopy.getAlliance() + " " +
        sourcePieceCopy.getRank() + " " +
        sourcePieceCoords + " to " +
        targetPiece + " " +
        targetPieceCoords + " " +
        this.moveType + " ILLEGAL MOVE");
    return false;
  }

  // TODO: adjust for edge out of bounds/wrapping  moves
  public boolean execute() {
    if (legalMoveCheck()) {
      switch (this.moveType) {
        case "aggressive":
          if (isTargetPieceEliminated()) {
            board.replacePiece(sourcePieceCoords, sourcePieceCopy);
            board.getTile(sourcePieceCoords).emptyTile();
          } else {
            board.getTile(sourcePieceCoords).emptyTile();
          }
          this.isExecuted = true;
          break;
        case "draw":
          board.getTile(sourcePieceCoords).emptyTile();
          board.getTile(targetPieceCoords).emptyTile();
          break;
        case "normal":
          board.movePiece(sourcePieceCoords, targetPieceCoords);
          this.isExecuted = true;
          break;
        case "invalid":
          System.out.println("Sorry, invalid move");
          System.out.println(this.toString());
          return false;
        default:
          return false;
      }
      this.isExecuted = true;
      return true;
    }
    return false;
  }

  // REMOVE THESE
  // public Move aggressive() {
  //   this.moveType = "aggressive";
  //   return this;
  // }
  // 
  // public Move draw() {
  //   this.moveType = "draw";
  //   return this;
  // }
  // 
  // public Move normal() {
  //   this.moveType = "normal";
  //   return this;
  // }
  // 
  // public Move invalid() {
  //   this.moveType = "invalid";
  //   return this;
  // }

  private boolean isSameRank() {
    if (sourcePieceCopy.getRank() == targetPieceCopy.getRank()) {
      return true;
    }
    return false;
  }

  private boolean isTargetPieceEliminated() {
    if (sourcePieceCopy.getPowerLevel() >
        targetPieceCopy.getPowerLevel()) {
      return true;
    }
    return false;
  }

  public Tile getCurrentTile() {
    return board.getTile(sourcePieceCoords);
  }

  public int getDestinationCoords() {
    return this.targetPieceCoords;
  }

  public int getOriginCoords() {
    return this.sourcePieceCoords;
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

  @Override
  public String toString() {
    String targetPiece;
    if (targetPieceCopy == null)
      targetPiece = "NONE";
    else
      targetPiece = targetPieceCopy.getRank();
    if (isExecuted)
      return sourcePieceCopy.getAlliance() + " " +
        sourcePieceCopy.getRank() + " " +
        sourcePieceCoords + " to " +
        targetPiece + " " +
        targetPieceCoords + " " +
        this.moveType + " EXECUTED";
    else
      return sourcePieceCopy.getAlliance() + " " +
        sourcePieceCopy.getRank() + " " +
        sourcePieceCoords + " to " +
        targetPiece + " " +
        targetPieceCoords + " ";
  }
}
