package game;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import engine.Alliance;
import engine.Board;
import engine.Move;
import engine.Board.Tile;
import engine.player.Player;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-28
 */
public class Save {

  private List<Tile> boardConfig;
  private Board board;
  private final String DATA_PATH = "data/save/";

  // ref: Timstammp = https://tecadmin.net/get-current-timestamp-in-java/
  public Save(Board board) {
    this.board = board;
    this.boardConfig = board.getInitBoardConfig();

    Date date = new Date();
    long time = date.getTime();
    Timestamp ts = new Timestamp(time);

    String filePath = DATA_PATH + (ts + "").replace(" ", "_") + ".txt";
    try {
      File outFile = new File(filePath);
      FileWriter fWriter = new FileWriter(outFile);
      PrintWriter pWriter = new PrintWriter (fWriter);
      pWriter.println(getBoardConfigData());
      pWriter.println(getFirstMoveMaker());
      pWriter.println(getBoardExecutions());
      pWriter.close();
      System.out.println("File saved: " + outFile.getPath());

      if (this.board.isDebugMode()) {
        System.out.println(getBoardConfigData());
        System.out.println(getFirstMoveMaker());
        System.out.println(getBoardExecutions());
      }
    } catch (IOException e) {
      System.out.println("Save error: save unsuccessful.\npath: " + filePath);
    }
  }

  public String getBoardConfigData() {
    String boardConfigData = "BoardConfig=" + this.boardConfig.size() + ";\n";
    return boardConfigData + convertBoardConfigToData(this.boardConfig);
  }

  public String getFirstMoveMaker() {
    return "FirstMoveMaker=" + this.board.getFirstMoveMaker() + "\n";
  }

  public String getBoardExecutions() {

    Player playerBlack = this.board.getBlackPlayer();
    Player playerWhite = this.board.getWhitePlayer();
    Alliance firstMoveMaker = this.board.getFirstMoveMaker();

    Map<Integer, Move> blackMoveHistory = playerBlack.getMoveHistory();
    Map<Integer, Move> whiteMoveHistory = playerWhite.getMoveHistory();

    int moveHistorySize = blackMoveHistory.size() + whiteMoveHistory.size();

    String data = "BoardExecutions=" + moveHistorySize + ";\n";

    for (int i = 1; i < moveHistorySize + 1; i++) {
      if (firstMoveMaker == Alliance.BLACK) {
        if (blackMoveHistory.get(i) != null)
          data += convertMoveToData(blackMoveHistory.get(i));
        if (whiteMoveHistory.get(i + 1) != null)
          data += convertMoveToData(whiteMoveHistory.get(i + 1));
      } else {
        if (whiteMoveHistory.get(i) != null)
          data += convertMoveToData(whiteMoveHistory.get(i));
        if (blackMoveHistory.get(i + 1) != null)
          data += convertMoveToData(blackMoveHistory.get(i + 1));
      }
    }

    return data;
  }

  public String convertBoardConfigToData(List<Tile> boardConfig) {
    String boardConfigData = "";

    for (int i = 0; i < boardConfig.size(); i++) {
      boardConfigData +=
        "tileId=" + boardConfig.get(i).getTileId() + ";" +
        "territory=" + boardConfig.get(i).getTerritory() + ";" +
        "piece=" + (boardConfig.get(i).isTileOccupied() ? boardConfig.get(i).getPiece().getRank() : "null") + ";" +
        "pieceAlliance=" + (boardConfig.get(i).isTileOccupied() ? boardConfig.get(i).getPiece().getPieceAlliance() : "null") + ";" +
        "\n";
    }
    return boardConfigData;
  }

  public String convertMoveToData(Move move) {
    String moveData = "";

    moveData +=
      "turnId=" + move.getTurnId() + ";" +
      "moveType=" + move.getMoveType() + ";" +
      "sourceAlliance=" + move.getSourcePiece().getPieceAlliance() + ";" +
      "sourcePiece=" + move.getSourcePiece().getRank() + ";" +
      "targetPiece=" + (move.getTargetPiece() != null ? move.getTargetPiece().getRank() : "null") + ";" +
      "originCoords=" + move.getOriginCoords() + ";" +
      "destinationCoords=" + move.getDestinationCoords() + ";" +
      "isExecuted=" + move.isMoveExecuted() + ";" +
      "\n";
    return moveData;
  }

}
