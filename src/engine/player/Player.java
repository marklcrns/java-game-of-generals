package engine.player;

import engine.pieces.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.Alliance;
import engine.Board;
import engine.Move;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-20
 */
public class Player {
  private final Board board;
  private final Alliance alliance;
  private Map<Integer, Move> moveHistory;
  private List<Piece> ownedPieces;
  private boolean isMoveMaker = false;
  // TODO calculate total activate pieces left

  public Player(Board board, Alliance alliance) {
    this.board = board;
    this.alliance = alliance;
  }

  public void initPlayer() {
    ownedPieces = new ArrayList<>();
    moveHistory = new HashMap<Integer, Move>();
    collectPieces();
  }

  private void collectPieces() {
    for (int i = 0; i < board.getBoard().size(); i++) {
      if (board.getTile(i).isTileOccupied()) {
        if (board.getTile(i).getPiece().getPieceAlliance() == alliance) {
          ownedPieces.add(board.getTile(i).getPiece());
        }
      }
    }
  }

  public void makeMoveMaker(boolean isMoveMaker) {
    this.isMoveMaker = isMoveMaker;
  }

  public boolean makeMove(int pieceCoords, int destinationCoords) {
    if (this.board.isDebugMode()) {
      Map<String, Move> possiblePieceMoves = this.board.getTile(pieceCoords).getPiece().evaluateMoves(board);

      System.out.println(this.board.getTile(pieceCoords).getPiece().getPieceAlliance() +
                         " " + this.board.getTile(pieceCoords).getPiece().getRank());
      System.out.println("Candidate moves size: " + possiblePieceMoves.size());

      for (Map.Entry<String, Move> entry : possiblePieceMoves.entrySet()) {
        System.out.println(entry.getKey().toUpperCase() + ": " + entry.getValue());
      };
    }

    if (moveMakerCheck() && pieceOwnerCheck(pieceCoords)) {
      Move move = new Move(this, board, pieceCoords, destinationCoords);

      if (move.execute()) {
        if (board.isDebugMode())
          System.out.println(move);

        clearForwardMoveHistory(this.board.getCurrentTurn());
        recordMove(move);
        this.board.setLastMove(move);
        this.board.switchMoveMakerPlayer();
        this.board.incrementTurn();
        this.board.updateLastExecutedTurn(this.board.getCurrentTurn());

        return true;
      } else {
        this.board.setLastMove(move);
      }
    }
    return false;
  }

  public Alliance getAlliance() {
    return this.alliance;
  }

  public boolean isMoveMaker() {
    return isMoveMaker;
  }

  public boolean moveMakerCheck() {
    if (isMoveMaker)
      return true;
    else
      System.out.println("E: " + alliance +
                         " player is currently NOT the move maker");
      return false;
  }

  public boolean pieceOwnerCheck(int pieceCoords) {
    if (board.getTile(pieceCoords).getPiece().getPieceAlliance() == alliance)
      return true;
    else
      System.out.println("E: " + alliance +
                         " player DOES NOT own " +
                         board.getTile(pieceCoords).getPiece().getPieceAlliance() + " " +
                         board.getTile(pieceCoords).getPiece().getRank() + " at Tile " +
                         pieceCoords);
      return false;
  }

  private void recordMove(Move move) {
    moveHistory.put(move.getTurnId(), move);
  }

  public boolean undoLastMove() {
    int currentTurn = this.board.getCurrentTurn();

    if (getMoveFromHistory(currentTurn - 1) != null) {

      Move recentMove = getMoveFromHistory(currentTurn - 1);
      Move lastMove = getMoveFromHistory(currentTurn - 2);

      if (moveMakerCheck()) {
        int recentMoveOrigin = recentMove.getOriginCoords();
        int recentMoveDestination = recentMove.getDestinationCoords();
        if (recentMove.getMoveType() == "aggressive") {
          if (recentMove.getEliminatedPiece() == recentMove.getTargetPiece()) {
            // if target piece eliminated
            this.board.movePiece(recentMoveDestination, recentMove.getOriginCoords());
            this.board.insertPiece(recentMoveDestination, recentMove.getEliminatedPiece());
          } else {
            // if source piece eliminated
            this.board.insertPiece(recentMoveOrigin, recentMove.getEliminatedPiece());
          }
        } else if (recentMove.getMoveType() == "draw") {
          this.board.insertPiece(recentMoveDestination, recentMove.getTargetPiece());
          this.board.insertPiece(recentMoveOrigin, recentMove.getSourcePiece());
        } else {
          this.board.movePiece(recentMoveDestination, recentMoveOrigin);
        }

        this.board.setLastMove(lastMove);
        recentMove.undoExecution();
        board.switchMoveMakerPlayer();
        board.decrementTurn();

        if (this.board.isDebugMode()) {
          System.out.println("Undo successful. " + recentMove + "\n");
          System.out.println("Turn History Stack");
          if (board.getLastExecutedTurn() != 0) {
            for (int i = this.board.getLastExecutedTurn() - 1; 0 < i; i--) {
              System.out.println(getMoveFromHistory(i));
            }
          }
          System.out.println("\n");
        }

        return true;
      }
    }
    return false;
  }

  public boolean redoLastMove() {
    int currentTurn = this.board.getCurrentTurn();

    if (getMoveFromHistory(currentTurn) != null) {
      Move nextMove = getMoveFromHistory(currentTurn);

      if (moveMakerCheck()) {
        int nextMoveOrigin = nextMove.getOriginCoords();
        int nextMoveDestination = nextMove.getDestinationCoords();
        if (nextMove.getMoveType() == "aggressive") {
          if (nextMove.getEliminatedPiece() == nextMove.getTargetPiece()) {
            // if target piece eliminated
            this.board.replacePiece(nextMoveDestination, nextMove.getSourcePiece());
            this.board.deletePiece(nextMoveOrigin);
          } else {
            // if source piece eliminated
            this.board.deletePiece(nextMoveOrigin);
          }
        } else if (nextMove.getMoveType() == "draw") {
          this.board.deletePiece(nextMoveOrigin);
          this.board.deletePiece(nextMoveDestination);
        } else {
          this.board.movePiece(nextMoveOrigin, nextMoveDestination);
        }

        this.board.setLastMove(nextMove);
        nextMove.redoExecution();
        board.switchMoveMakerPlayer();
        board.incrementTurn();

        if (this.board.isDebugMode()) {
          System.out.println("Redo successful. " + nextMove + "\n");
          System.out.println("Turn History Stack");
          if (board.getLastExecutedTurn() != 0) {
            for (int i = this.board.getLastExecutedTurn() - 1; 0 < i; i--) {
              System.out.println(getMoveFromHistory(i));
            }
          }
          System.out.println("\n");
        }

        return true;
      }
    }

    return false;
  }

  public Map<Integer, Move> getMoveHistory() {
    return this.moveHistory;
  }

  public Move getMoveFromHistory(int turnId) {
    Player opposingPlayer = this.alliance == Alliance.BLACK ?
      this.board.getWhitePlayer() : this.board.getBlackPlayer();

    if (opposingPlayer.moveHistory.get(turnId) != null)
      return opposingPlayer.getMoveFromHistory(turnId);
    else
      return this.moveHistory.get(turnId);
  }

  public void clearForwardMoveHistory(int startPointTurn) {
    Player lastMoveMakerPlayer = this.alliance == Alliance.BLACK ?
      this.board.getWhitePlayer() : this.board.getBlackPlayer();

    int moveHistorySize = this.moveHistory.size() + lastMoveMakerPlayer.moveHistory.size();
    for (int i = startPointTurn; i < moveHistorySize; i++) {

      if (lastMoveMakerPlayer.getMoveFromHistory(i) != null) {
        System.out.println(lastMoveMakerPlayer.getMoveFromHistory(i) + " REMOVING...");
        lastMoveMakerPlayer.moveHistory.remove(i);
      } else {
        System.out.println(this.getMoveFromHistory(i) + " REMOVING...");
        this.moveHistory.remove(i);
      }
    }
  }

  @Override
  public String toString() {
    String history = "";
    if (moveHistory.size() > 0) {
      history += alliance + " player move history:\n";
      for (Map.Entry<Integer, Move> entry : moveHistory.entrySet()) {
        history += "Turn " + entry.getKey() + ": " + entry.getValue() + "\n";
      };
    } else {
      history += alliance + " player did not make valid moves yet";
    }

    return history;
  }
}
