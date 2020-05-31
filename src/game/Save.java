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
 * Save game feature for the Board engine that saves the current in-game state.
 * Companion for Load class.
 *
 * Author: Mark Lucernas
 * Date: 2020-05-28
 */
public class Save {

  /** Save data path */
  private final String DATA_PATH = "data/save/";

  /** List of Tile the contains the initial board arrangement before game starts. */
  private List<Tile> boardConfig;

  /** Reference to the Board engine */
  private Board board;

  /**
   * Constructor method that takes in Board instance as parameter.
   * Timestamp ref: https://tecadmin.net/get-current-timestamp-in-java/
   */
  public Save(Board board) {
    this.board = board;
    this.boardConfig = board.getInitBoardConfig();

    Date date = new Date();
    long time = date.getTime();
    Timestamp ts = new Timestamp(time);

    // Set current date and timestamp as file output name.
    String filePath = DATA_PATH + (ts + "").replace(" ", "_") + ".txt";
    try {
      File outFile = new File(filePath);
      FileWriter fWriter = new FileWriter(outFile);
      PrintWriter pWriter = new PrintWriter (fWriter);
      // Write all necessary data into output file
      pWriter.println(getBoardConfigData());
      pWriter.println(getBoardStateData());
      pWriter.println(getBoardExecutions());
      pWriter.close();

      if (this.board.isDebugMode()) {
        System.out.println("File saved: " + outFile.getPath());
        System.out.println(getBoardConfigData());
        System.out.println(getBoardStateData());
        System.out.println(getBoardExecutions());
      }
    } catch (IOException e) {
      System.out.println("Save error: save unsuccessful.\npath: " + filePath);
    }
  }

  /**
   * Gets the initial Board configuration and convert into savable data string.
   * @return String of board config data.
   */
  public String getBoardConfigData() {
    String boardConfigData = "BoardConfig=" + this.boardConfig.size() + ";\n";
    return boardConfigData + convertBoardConfigToData(this.boardConfig);
  }

  /**
   * Gets the Board state and convert into savable data string.
   * @return String of board sate data.
   */
  public String getBoardStateData() {
    String boardStateData = "BoardStateData:\n";
    boardStateData += "firstMoveMaker=" + this.board.getFirstMoveMaker() + ";";
    boardStateData += "currentTurn=" + this.board.getCurrentTurn() + ";";
    boardStateData += "lastExecutedTurn=" + this.board.getLastExecutedTurn() + ";";
    boardStateData += "\n";

    return boardStateData;
  }

  /**
   * Gets all the Board execution from move history and convert into a savable
   * data string.
   * @return String data of all board executions.
   */
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

  /**
   * Board configuration to string data converter.
   * @param boardConfig List<Tile> initial board configuration to be converted.
   * @return String converted board configuration data.
   */
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

  /**
   * Move to string data converter.
   * @param move Move to be converted
   * @return String converted move data.
   */
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
