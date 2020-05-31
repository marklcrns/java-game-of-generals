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
 * Player class that holds all their respective owned pieces and keeps their
 * move history. Also this class executes the move of the each pieces in their
 * turn.
 * Has an undo and redo feature, which also updates move history as it executes.
 * All undid moves in the move history, if another move has been executed, will
 * be cleared from the history from the recently made move going forward and
 * can no longer be redo.
 *
 * Author: Mark Lucernas
 * Date: 2020-05-20
 */
public class Player {

  /** Reference to the Board. */
  private Board board;

  /** Alliance of the Player. */
  private Alliance alliance;

  /** Move history that holds all the successfully executed moves. */
  private Map<Integer, Move> moveHistory;

  /** List of Pieces owned by this Player instance */
  private List<Piece> ownedPieces;

  /** Checker if this Player is the move maker */
  private boolean isMoveMaker = false;

  // TODO calculate total activate pieces left

  /**
   * Constructor that takes in the Alliance of this Player instance.
   */
  public Player(Alliance alliance) {
    this.alliance = alliance;
  }

  /**
   * Sets the Board in which this Player will participate in.
   * Required before initializing this Player instance.
   * @param board the Board in which to participate in.
   * @return boolean true if successful, else false.
   */
  public boolean setBoard(Board board) {
    if (board.isPlayerExisting(this))
      return false;

    this.board = board;
    return true;
  }

  /**
   * Initialize this Player instance.
   * Initialize ownedPieces to empty List, moveHistory to empty
   * HashMap<Integer, Move>, then collect all Player's respective pieces.
   */
  public void initPlayer() {
    ownedPieces = new ArrayList<>();
    moveHistory = new HashMap<Integer, Move>();
    collectPieces();
  }

  /**
   * Collects all respective pieces of the same Alliance from Board.
   * Depends on Board instance reference.
   */
  private void collectPieces() {
    for (int i = 0; i < board.getBoard().size(); i++) {
      if (board.getTile(i).isTileOccupied()) {
        if (board.getTile(i).getPiece().getPieceAlliance() == alliance) {
          ownedPieces.add(board.getTile(i).getPiece());
        }
      }
    }
  }

  /**
   * Sets the move maker field to true or false.
   * @param isMoveMaker boolean is Player the move maker.
   */
  public void setMoveMaker(boolean isMoveMaker) {
    this.isMoveMaker = isMoveMaker;
  }

  /**
   * Method that moves a piece owned by this Player instance, records into move
   * history, and change the state of the board.
   * @param pieceCoords int coordinates of the owned piece to be moved.
   * @param destinationCoords int designation coordinates of the owned piece to
   * to be moved.
   * @return boolean true if successful, else false.
   */
  public boolean makeMove(int pieceCoords, int destinationCoords) {

    // Prints all possible moves in debug mode.
    if (this.board.isDebugMode()) {
      Map<String, Move> possiblePieceMoves = this.board.getTile(pieceCoords).getPiece().evaluateMoves(board);

      System.out.println(this.board.getTile(pieceCoords).getPiece().getPieceAlliance() +
                         " " + this.board.getTile(pieceCoords).getPiece().getRank());
      System.out.println("Candidate moves size: " + possiblePieceMoves.size());

      for (Map.Entry<String, Move> entry : possiblePieceMoves.entrySet()) {
        System.out.println(entry.getKey() + "=" + entry.getValue() + ";");
      };
      System.out.println("\n");
    }

    // Execute if Player's turn and owns the selected piece.
    if (isMoveMaker() && pieceOwnerCheck(pieceCoords)) {
      Move move = new Move(this, board, pieceCoords, destinationCoords);
      move.evaluateMove();

      // Execute if move is valid, else register move as recent invalid move.
      if (move.execute()) {

        // Record to move history
        clearForwardMoveHistory(this.board.getCurrentTurn());
        recordMove(move);

        // Change necessary states of the Board
        this.board.setLastMove(move);
        this.board.switchMoveMakerPlayer();
        this.board.incrementTurn();
        this.board.updateLastExecutedTurn(this.board.getCurrentTurn());

        if (board.isDebugMode())
          System.out.println(move);

        return true;
      } else {
        // Record the move as last and invalid move
        this.board.setLastMove(move);
        this.board.setLastInvalidMove(move);
      }
    }
    return false;
  }

  /**
   * Gets this Player Alliance.
   * @return Alliance
   */
  public Alliance getAlliance() {
    return this.alliance;
  }

  /**
   * Checks if this Player instance is currently the move maker.
   * @return boolean isMoveMaker field.
   */
  public boolean isMoveMaker() {
    if (isMoveMaker)
      return true;
    else
      System.out.println("E: " + alliance +
                         " player is currently NOT the move maker");
      return false;
  }

  /**
   * Checks if the passed in piece coordinates is owned by this Player instance.
   * @return boolean true if piece is owned by this Player, else false.
   */
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

  /**
   * Records Move into move history
   * @param move Move to record into moveHistory field.
   */
  private void recordMove(Move move) {
    moveHistory.put(move.getTurnId(), move);
  }

  /**
   * Undo last executed Move in the Board regardless of who's Player executed it.
   * @return boolean true if successful, else false.
   */
  public boolean undoLastMove() {
    int currentTurn = this.board.getCurrentTurn();

    // Ensures not to undo on Player's first turn.
    if (getMoveFromHistory(currentTurn - 1) != null) {

      Move recentMove = getMoveFromHistory(currentTurn - 1);
      Move lastMove = getMoveFromHistory(currentTurn - 2);

      if (isMoveMaker()) {
        int recentMoveOrigin = recentMove.getOriginCoords();
        int recentMoveDestination = recentMove.getDestinationCoords();

        // Reverse the most recent executed move
        if (recentMove.getMoveType() == "aggressive") {
          if (recentMove.getEliminatedPiece() == recentMove.getTargetPiece()) {
            // If target piece eliminated
            this.board.movePiece(recentMoveDestination, recentMove.getOriginCoords());
            this.board.insertPiece(recentMoveDestination, recentMove.getEliminatedPiece());
          } else {
            // If source piece eliminated
            this.board.insertPiece(recentMoveOrigin, recentMove.getEliminatedPiece());
          }
        } else if (recentMove.getMoveType() == "draw") {
          this.board.insertPiece(recentMoveDestination, recentMove.getTargetPiece());
          this.board.insertPiece(recentMoveOrigin, recentMove.getSourcePiece());
        } else {
          this.board.movePiece(recentMoveDestination, recentMoveOrigin);
        }

        // Sets the two move down the combined history of both Players as last move.
        this.board.setLastMove(lastMove);

        // Change execution state of the most recent move for redoing purposes.
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

  /**
   * Redo last executed Move in the Board regardless of who's Player executed it.
   * @return boolean true if successful, else false.
   */
  public boolean redoLastMove() {
    int currentTurn = this.board.getCurrentTurn();

    // Prevents redoing when theres nothing to redo
    if (getMoveFromHistory(currentTurn) != null) {
      Move nextMove = getMoveFromHistory(currentTurn);

      if (isMoveMaker()) {
        int nextMoveOrigin = nextMove.getOriginCoords();
        int nextMoveDestination = nextMove.getDestinationCoords();

        // Reexecute undid move from the move history that matches the current turn.
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

        // Sets last move as the recently reexecuted move
        this.board.setLastMove(nextMove);

        // Redo move execution state.
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

  /**
   * Gets this Player move history.
   */
  public Map<Integer, Move> getMoveHistory() {
    return this.moveHistory;
  }

  /**
   * Gets specific move from the combined move history of both Players in the
   * Board.
   * @param turnId int turn ID of the Move.
   * @return Move from the moveHistory field that matches the turnId.
   */
  public Move getMoveFromHistory(int turnId) {
    Player opposingPlayer = this.alliance == Alliance.BLACK ?
      this.board.getWhitePlayer() : this.board.getBlackPlayer();

    if (opposingPlayer.moveHistory.get(turnId) != null)
      return opposingPlayer.getMoveFromHistory(turnId);
    else
      return this.moveHistory.get(turnId);
  }

  /**
   * Clears move history going forward on both Players starting from the passed
   * in int staring point turn.
   * @param startPointTurn starting point turn from where to start clearing move
   * history forward.
   */
  public void clearForwardMoveHistory(int startPointTurn) {
    Player lastMoveMakerPlayer = this.alliance == Alliance.BLACK ?
      this.board.getWhitePlayer() : this.board.getBlackPlayer();

    int moveHistorySize = this.moveHistory.size() + lastMoveMakerPlayer.moveHistory.size();
    for (int i = startPointTurn; i < moveHistorySize; i++) {

      if (lastMoveMakerPlayer.getMoveFromHistory(i) != null) {
        if (this.board.isDebugMode())
          System.out.println(lastMoveMakerPlayer.getMoveFromHistory(i) + " REMOVING...");
        lastMoveMakerPlayer.moveHistory.remove(i);
      } else {
        if (this.board.isDebugMode())
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
