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
  private boolean isExecuted = false;
  private String moveType;
  private Piece sourcePieceCopy;
  private Piece targetPieceCopy;
  private Piece eliminatedPiece;

  public Move(final Player player,
              final Board board,
              final int sourceTileCoords,
              final int targetTileCoords) {
    this.player = player;
    this.board = board;
    this.sourceTileCoords = sourceTileCoords;
    this.targetTileCoords = targetTileCoords;
  }

  public void evaluateMove() {
    this.sourcePieceCopy = this.board.getTile(sourceTileCoords).getPiece().makeCopy();
    if (this.board.getTile(targetTileCoords).isTileOccupied())
      this.targetPieceCopy = this.board.getTile(targetTileCoords).getPiece().makeCopy();
    else
      this.targetPieceCopy = null;

    if (board.getTile(targetTileCoords).isTileOccupied())
      if (targetPieceCopy.getPieceAlliance() != sourcePieceCopy.getPieceAlliance())
        if (isSameRank() && isTargetPieceFlag())
          this.moveType = "aggressive";
        else if (isSameRank())
          this.moveType = "draw";
        else
          this.moveType = "aggressive";
      else
        this.moveType = "invalid";
    else
      this.moveType = "normal";
  }

  // TODO: adjust for edge out of bounds/wrapping  moves
  public boolean execute() {
    if (legalMoveCheck()) {
      switch (this.moveType) {

        case "aggressive":
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

        case "normal":
          if (isFlagSucceeded()) {
            System.out.println("\n" + sourcePieceCopy.getPieceAlliance() +
                " player WON!\n");
            board.setEndGameWinner(sourcePieceCopy.getPieceAlliance());
          }
          board.movePiece(sourceTileCoords, targetTileCoords);
          this.isExecuted = true;
          break;

        case "draw":
          board.getTile(sourceTileCoords).empty();
          board.getTile(targetTileCoords).empty();
          break;

        case "invalid":
          System.out.println("E: Invalid move");
          System.out.println(this.toString());
          this.turnId = board.getCurrentTurn();
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
    if (isSourcePieceFlag() && isTargetPieceFlag())
      return true;
    else if (sourcePieceCopy.getRank() == "Private" && targetPieceCopy.getRank() == "Spy")
      return true;
    else if (sourcePieceCopy.getRank() == "Spy" && targetPieceCopy.getRank() == "Private")
      return false;
    else if (sourcePieceCopy.getPowerLevel() > targetPieceCopy.getPowerLevel())
      return true;
    else
      return false;
  }

  private boolean isTargetPieceFlag() {
    if (targetPieceCopy.getRank() == "Flag")
      return true;
    else
      return false;
  }

  private boolean isSourcePieceFlag() {
    if (sourcePieceCopy.getRank() == "Flag")
      return true;
    else
      return false;
  }

  private boolean isFlagSucceeded() {
    if (sourcePieceCopy.getRank() == "Flag" &&
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
    if (eliminatedPiece != null) {
      return this.eliminatedPiece;
    }
    return null;
  }

  public Piece getSourcePiece() {
    return this.sourcePieceCopy;
  }

  public Piece getTargetPiece() {
    if (this.targetPieceCopy != null)
      return this.targetPieceCopy;

    return null;
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
    if (this.isExecuted) {
      return false;
    } else {
      this.isExecuted = true;
    }
    return true;
  }

  public boolean isMoveExecuted() {
    return this.isExecuted;
  }

  public void setExecutionState(boolean isExecuted) {
    this.isExecuted = isExecuted;
  }

  public void setMoveType(String moveType) {
    this.moveType = moveType;
  }

  public void setTurnId(int turnId) {
    this.turnId = turnId;
  }

  @Override
  public String toString() {
    Alliance sourcePieceAlliance = sourcePieceCopy == null ? null : sourcePieceCopy.getPieceAlliance();
    String sourcePiece = sourcePieceCopy == null ? "" : sourcePieceCopy.getRank() + " ";
    String targetPiece = targetPieceCopy == null ? "" : targetPieceCopy.getRank() + " ";

    if (isExecuted) {
      String superiorPieceAlliance = "";
      if (this.moveType == "aggressive") {
        superiorPieceAlliance = eliminatedPiece.getPieceAlliance() == Alliance.BLACK ?
          " " + Alliance.WHITE: " " + Alliance.BLACK;
      }
      return "Turn " + this.turnId + ": " +
        sourcePieceAlliance + " " +
        sourcePiece + " " +
        sourceTileCoords + " to " +
        targetPiece + targetTileCoords + " " +
        this.moveType + superiorPieceAlliance +
        " EXECUTED";
    } else {
      return "Turn " + this.turnId + ": " +
        sourcePieceAlliance + " " +
        sourcePiece + " " +
        sourceTileCoords + " to " +
        targetPiece + targetTileCoords + " ";
    }
  }
}
