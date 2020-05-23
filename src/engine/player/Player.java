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
  private int turnId = 0;
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
        if (board.getTile(i).getPiece().getAlliance() == alliance) {
          ownedPieces.add(board.getTile(i).getPiece());
        }
      }
    }
  }

  public void setMoveMaker(boolean isMoveMaker) {
    this.isMoveMaker = isMoveMaker;
  }

  public boolean makeMove(int pieceCoords, int destinationCoords) {
    ////////// DELETE ME LATER //////////
    // Map<String, Move> possiblePieceMoves = this.board.getTile(pieceCoords).getPiece().evaluateMoves(board);
    // System.out.println("\n" + this.board.getTile(pieceCoords).getPiece().getAlliance()
    //     + " " + this.board.getTile(pieceCoords).getPiece().getRank());
    // System.out.println("evalMoves size: " + possiblePieceMoves.size());
    // for (Map.Entry<String, Move> entry : possiblePieceMoves.entrySet()) {
    //   System.out.println(entry.getKey() + ": " + entry.getValue());
    // };

    if (moveMakerCheck() && pieceOwnerCheck(pieceCoords)) {
      Move move = new Move(this, board, pieceCoords, destinationCoords);

      if (move.execute()) {
        recordMove(move);
        this.board.switchPlayerTurn();
        return true;
      }
    }
    return false;
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
    if (board.getTile(pieceCoords).getPiece().getAlliance() == alliance)
      return true;
    else
      System.out.println("E: " + alliance +
          " player DOES NOT own " +
          board.getTile(pieceCoords).getPiece().getAlliance() + " " +
          board.getTile(pieceCoords).getPiece().getRank() + " at Tile " +
          pieceCoords);
      return false;
  }

  private void recordMove(Move move) {
    moveHistory.put(this.turnId, move);
    this.turnId++;
  }

  public Move undoLastMove() {
    Move lastMove = this.moveHistory.get(this.turnId);
    this.moveHistory.remove(this.turnId);
    lastMove.undoExecution();
    this.turnId--;
    return lastMove;
    // TODO: decide whether to execute automatically
  }

  public int getCurrentTurnId() {
    return this.turnId;
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