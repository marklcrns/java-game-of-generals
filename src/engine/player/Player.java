package engine.player;

import engine.pieces.Piece;

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
  private int turnId;
  private boolean isMoveMaker = false;

  public Player(Board board, Alliance alliance) {
    this.board = board;
    this.alliance = alliance;
    this.turnId = 0;
  }

  private void initPlayer() {
    // TODO: initialize ownedPieces
  }

  public void makeReady() {
    this.isMoveMaker = true;
  }

  public void recordMove(int turn, Move move) {
    moveHistory.put(turn, move);
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
}
