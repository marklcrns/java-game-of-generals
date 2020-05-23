package tests.engine;

import java.util.Map;

import engine.Board;
import engine.Board.BoardBuilder;
import engine.Board.Tile;
import utils.BoardUtils;
import engine.Alliance;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class BoardTest {

  private static Board board = new Board();
  private static BoardBuilder builder = new BoardBuilder();
  private static boolean territoryErrorDetected = false;
  private static boolean allianceErrorDetected = false;
  private static boolean emptyBoardErrorDetected = false;
  private static Map<Integer, String> tileTerritoryErrors;
  private static Map<Integer, String> tilePieceAllianceErrors;
  private static Map<Integer, String> emptyBoardTileErrors;

  public static void main(String[] args) {
    // TODO: exception handling
    territoryCheck();
    pieceAllianceCheck();
    emptyBoardCheck();
  }

  public static void territoryCheck() {
    for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
      final Tile currentTile = board.getTile(i);
      if ((i < BoardUtils.ALL_TILES_COUNT / 2 &&
          currentTile.getTerritory() != Alliance.BLACK) &&
          (i > BoardUtils.ALL_TILES_COUNT / 2 &&
          currentTile.getTerritory() != Alliance.WHITE)) {
        territoryErrorDetected = true;
        tileTerritoryErrors.put(i, "E: Tile alliance in enemy territory");
      }
    }

    if (territoryErrorDetected) {
      System.out.println("Board territory check FAILED");
      for (Map.Entry<Integer, String> entry : tileTerritoryErrors.entrySet()) {
        System.out.println("Tile" + entry.getKey() + ", " + entry.getValue());
      }
    } else {
      System.out.println("Board territory check ...PASSED");
    }
  }

  public static void pieceAllianceCheck() {
    board.buildBoard(builder.createDemoBoardBuild());
    for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
      final Tile currentTile = board.getTile(i);
      if (currentTile.isTileOccupied() &&
          currentTile.getTerritory() != currentTile.getPiece().getAlliance()) {
        allianceErrorDetected = true;
        tilePieceAllianceErrors.put(i, "E: " + currentTile.getPiece().getAlliance() +
            " " + currentTile.getPiece().getRank() +
            " on " + currentTile.getTerritory() + " Territory");
      }
    }

    if (allianceErrorDetected) {
      System.out.println("Board tile piece alliance  check FAILED");
      for (Map.Entry<Integer, String> entry : tilePieceAllianceErrors.entrySet()) {
        System.out.println("Tile" + entry.getKey() + ", " + entry.getValue());
      }
    } else {
      System.out.println("Board tile piece alliance check ...PASSED");
    }
  }

  public static void emptyBoardCheck() {
    // TODO: add test with for non-empty board
    board.emptyBoard();

    for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
      final Tile currentTile = board.getTile(i);
      if (currentTile.isTileOccupied()) {
        emptyBoardErrorDetected = true;
        emptyBoardTileErrors.put(i, "E: Tile is not empty");
      }
    }

    if (emptyBoardErrorDetected) {
      System.out.println("Empty board check FAILED");
      for (Map.Entry<Integer, String> entry : emptyBoardTileErrors.entrySet()) {
        System.out.println("Tile" + entry.getKey() + ", " + entry.getValue());
      }
    } else {
      System.out.println("Empty board check check ...PASSED");
    }
  }

}
