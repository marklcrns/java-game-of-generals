package engine;

import java.util.Map;

import engine.Board.Tile;
import engine.pieces.Piece;
import engine.player.Player;
import utils.BoardUtils;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class Move {
  private int turnId;
  private final Board board;
  private final Player player;
  private final int sourceTileCoords;
  private final int targetTileCoords;
  private final Piece sourcePieceCopy;
  private final Piece targetPieceCopy;
  private boolean isExecuted = false;
  private Piece eliminatedPiece;
  private String moveType;

  public Move(final Player player,
              final Board board,
              final int sourcePieceCoords,
              final int targetPieceCoords) {
    this.player = player;
    this.board = board;
    this.sourceTileCoords = sourcePieceCoords;
    this.targetTileCoords = targetPieceCoords;
    this.sourcePieceCopy = board.getTile(sourcePieceCoords).getPiece().makeCopy();
    if (board.getTile(targetPieceCoords).isTileOccupied())
      this.targetPieceCopy = board.getTile(targetPieceCoords).getPiece().makeCopy();
    else
      this.targetPieceCopy = null;
    evaluateMove();
  }

  private void evaluateMove() {
    if (board.getTile(targetTileCoords).isTileOccupied())
      if (targetPieceCopy.getPieceAlliance() != sourcePieceCopy.getPieceAlliance())
        if (isSameRank() && isTargetPieceFlag())
          moveType = "aggressive";
        else if (isSameRank())
          moveType = "draw";
        else
          moveType = "aggressive";
      else
        moveType = "invalid";
    else
      moveType = "normal";
  }

  // TODO: adjust for edge out of bounds/wrapping  moves
  public boolean execute() {
    if (legalMoveCheck()) {
      switch (this.moveType) {
        case "aggressive":

          // TODO end game
          if (isTargetPieceFlag()) {
            System.out.println("\n" + sourcePieceCopy.getPieceAlliance() +
                               " player WON!\n");
            board.setEndGameWinner(sourcePieceCopy.getPieceAlliance());
          } else if (isSourcePieceFlag() && !isTargetPieceFlag()){
            System.out.println("\n" + targetPieceCopy.getPieceAlliance() +
                               " player WON!\n");
            board.setEndGameWinner(targetPieceCopy.getPieceAlliance());
          }

          if (isTargetPieceEliminated()) {
            board.replacePiece(targetTileCoords, sourcePieceCopy);
            board.getTile(sourceTileCoords).empty();
            eliminatedPiece = targetPieceCopy;
          } else {
            board.getTile(sourceTileCoords).empty();
            eliminatedPiece = sourcePieceCopy;
          }
          this.isExecuted = true;
          break;
        case "draw":
          board.getTile(sourceTileCoords).empty();
          board.getTile(targetTileCoords).empty();
          break;
        case "normal":

          if (isFlagSucceeded()) {
            System.out.println("\n" + sourcePieceCopy.getPieceAlliance() +
                " player WON!\n");
            board.setEndGameWinner(sourcePieceCopy.getPieceAlliance());
          }

          board.movePiece(sourceTileCoords, targetTileCoords);

          this.isExecuted = true;
          break;
        case "invalid":
          System.out.println("E: Invalid move");
          System.out.println(this.toString());
          return false;
        default:
          return false;
      }
      this.turnId = board.getCurrentTurn();
      this.isExecuted = true;
      return true;
    }
    return false;
  }

  private boolean legalMoveCheck() {
    // check if one of possible piece moves
    Map<String, Move> possiblePieceMoves =
      this.board.getTile(sourceTileCoords).getPiece().evaluateMoves(board);

    for (Map.Entry<String, Move> entry : possiblePieceMoves.entrySet()) {
      if (entry.getValue().getDestinationCoords() == targetTileCoords) {
        return true;
      }
    };
    moveType = "invalid";

    // TODO: Tidy up
    String targetPiece;
    if (targetPieceCopy == null)
      targetPiece = "";
    else
      targetPiece = targetPieceCopy.getRank();
    System.out.println(sourcePieceCopy.getPieceAlliance() + " " +
        sourcePieceCopy.getRank() + " " +
        sourceTileCoords + " to " +
        targetPiece + " " +
        targetTileCoords + " " +
        this.moveType + " ILLEGAL MOVE");
    return false;
  }

  private boolean isSameRank() {
    if (sourcePieceCopy.getRank() == targetPieceCopy.getRank()) {
      return true;
    }
    return false;
  }

  private boolean isTargetPieceEliminated() {
    if (sourcePieceCopy.getRank() == "S" && targetPieceCopy.getRank() == "P")
      return false;
    else if (isSourcePieceFlag() && isTargetPieceFlag())
      return true;
    else if (sourcePieceCopy.getPowerLevel() > targetPieceCopy.getPowerLevel())
      return true;
    else
      return false;
  }

  private boolean isTargetPieceFlag() {
    if (targetPieceCopy.getRank() == "F")
      return true;
    else
      return false;
  }

  private boolean isSourcePieceFlag() {
    if (sourcePieceCopy.getRank() == "F")
      return true;
    else
      return false;
  }

  private boolean isFlagSucceeded() {
    if (sourcePieceCopy.getRank() == "F" &&
        board.getTile(targetTileCoords).isTileEmpty())
      if ((sourcePieceCopy.getPieceAlliance() == Alliance.BLACK &&
          targetTileCoords >= BoardUtils.LAST_ROW_INIT) ||
          (sourcePieceCopy.getPieceAlliance() == Alliance.WHITE &&
          targetTileCoords < BoardUtils.SECOND_ROW_INIT))
        return true;

    return false;
  }

  public Tile getCurrentTile() {
    return board.getTile(sourceTileCoords);
  }

  public int getDestinationCoords() {
    return this.targetTileCoords;
  }

  public int getOriginCoords() {
    return this.sourceTileCoords;
  }

  public String getMoveType() {
    return this.moveType;
  }

  public int getTurnId() {
    return this.turnId;
  }

  public Piece getEliminatedPiece() {
    if (isExecuted && eliminatedPiece != null) {
      return this.eliminatedPiece;
    }
    return null;
  }

  // TODO: implement
  public boolean undoExecution() {
    if (!this.isExecuted) {
      return false;
    } else {
      this.isExecuted = false;
    }
    return true;
  }

  // TODO: implement
  public boolean redoExecution() {
    return true;
  }

  @Override
  public String toString() {
    String targetPiece = targetPieceCopy == null ? "" : targetPieceCopy.getRank() + " ";
    if (isExecuted) {
      String superiorPieceAlliance = "";
      if (this.moveType == "aggressive") {
        superiorPieceAlliance = eliminatedPiece.getPieceAlliance() == Alliance.BLACK ?
          Alliance.WHITE + " ": Alliance.BLACK + " ";
      }
      return sourcePieceCopy.getPieceAlliance() + " " +
        sourcePieceCopy.getRank() + " " +
        sourceTileCoords + " to " +
        targetPiece + targetTileCoords + " " +
        superiorPieceAlliance + this.moveType +
        " EXECUTED";
    } else {
      return sourcePieceCopy.getPieceAlliance() + " " +
        sourcePieceCopy.getRank() + " " +
        sourceTileCoords + " to " +
        targetPiece + targetTileCoords + " ";
    }
  }
}
