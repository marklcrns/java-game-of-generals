package engine;

import engine.pieces.Piece;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class Move {
  final Board board;
  final Piece currentPiece;
  final int destinationCoords;

  private Move(final Board board,
               final Piece piece,
               final int destinationCoords) {
    this.board = board;
    this.currentPiece = piece;
    this.destinationCoords = destinationCoords;
  }

  public void execute() {

  }

}
