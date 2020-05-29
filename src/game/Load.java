package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import engine.Alliance;
import engine.Board;
import engine.Move;
import engine.Board.BoardBuilder;
import engine.pieces.Piece;
import engine.player.Player;
import gui.MainFrame;
import utils.BoardUtils;
import utils.Utils;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-28
 */
public class Load {

  private static final String DATA_PATH = "data/save/";
  private final String fileName;
  private Map<Integer, Move> moveHistory;
  private Board board;
  private BoardBuilder builder;
  private Player playerBlack;
  private Player playerWhite;
  private Alliance firstMoveMaker;
  private int currentTurn;
  private int lastExecutedTurn;

  public static void main(String[] args) {
    Board board = new Board();
    Player player1 = new Player(Alliance.WHITE);
    Player player2 = new Player(Alliance.BLACK);
    board.addPlayerWhite(player1);
    board.addPlayerBlack(player2);
    board.setDebugMode(true);

    new Load(board, "2020-05-29_15:00:12.174.txt").loadSaveGame();
    new MainFrame(board);
  }

  public Load(Board board, String fileName) {
    this.board = board;
    this.fileName = fileName;
    this.moveHistory = new HashMap<Integer, Move>();
    this.builder = new BoardBuilder();
  }

  public static String[] getSaveList() {
    File saveListPathFiles = new File(DATA_PATH);
    String[] saveList = saveListPathFiles.list();

    return saveList;
  }

  public void loadSaveGame() {
    File saveFile = new File(DATA_PATH + fileName);

    String[] saveData = {};
    try {
      Scanner scan = new Scanner(saveFile);

      while (scan.hasNextLine()) {
        String scanLine = scan.nextLine();
        if (board.isDebugMode())
          System.out.println(scanLine);
        saveData = Utils.appendToStringArray(saveData, scanLine);
      }
      scan.close();

      if (parseSaveData(saveData)) {
        executeSaveData();
        this.board.startGame();
        if (this.board.isDebugMode())
          System.out.println("Game successfully loaded");
      }
    } catch (FileNotFoundException e) {
      System.out.println("Load error: Save data not found");
    }
  }

  public boolean parseSaveData(String[] saveData) {
    if (saveData.length != 0 && saveData != null) {
      boolean isBoardConfig = false;
      boolean isBoardStateData = false;
      boolean isBoardExecutions = false;
      for (int i = 0; i < saveData.length; i++) {

        if (saveData[i].contains("BoardConfig")) {
          isBoardConfig = true;
          i += 1;
        } else if (saveData[i].contains("BoardStateData")) {
          if (this.board.isDebugMode())
            System.out.println("BoardBuilder Loaded:\n" + builder);
          this.board.setBoardBuilder(builder);
          this.board.initGame();
          isBoardConfig = false;
          isBoardStateData = true;
          i += 1;
        } else if (saveData[i].contains("BoardExecutions")) {
          isBoardConfig = false;
          isBoardStateData = false;
          isBoardExecutions = true;
          i += 1;
        }

        if (isBoardConfig) {
          if (this.board.isDebugMode())
            System.out.println("\ndataLength=" + saveData[i].length() + "\nsaveData=" + saveData[i] + "\n");
          boardConfigDataParser(saveData[i]);
        } else if (isBoardStateData) {
          if (this.board.isDebugMode())
            System.out.println("\ndataLength=" + saveData[i].length() + "\nsaveData=" + saveData[i] + "\n");
          // TODO: implement
        } else if (isBoardExecutions) {
          if (this.board.isDebugMode())
            System.out.println("\ndataLength=" + saveData[i].length() + "\nsaveData=" + saveData[i] + "\n");
          boardExecutionsDataParser(saveData[i]);
        }

      }
      if (this.board.isDebugMode()) {
        System.out.println("Move history Loaded:\n");
        for (Map.Entry<Integer, Move> entry : moveHistory.entrySet()) {
          System.out.println("TurnId=" + entry.getKey() + ";Move=" + entry.getValue());
        }
      }
      return true;
    }
    return false;
  }

  public boolean boardConfigDataParser(String saveData) {
    if (!saveData.isEmpty() && saveData != null) {
      String tileIdKey = "tileId=";
      String territoryKey = "territory=";
      String pieceRankKey = "piece=";
      String pieceAllianceKey = "pieceAlliance=";

      int tileIdIndex = saveData.indexOf(tileIdKey);
      int territoryIndex = saveData.indexOf(territoryKey);
      int pieceRankIndex = saveData.indexOf(pieceRankKey);
      int pieceAllianceIndex = saveData.indexOf(pieceAllianceKey);

      int tileId = Integer.parseInt(saveData.substring(
            tileIdIndex + tileIdKey.length(), territoryIndex - 1));
      String territory = saveData.substring(
          territoryIndex + territoryKey.length(), pieceRankIndex - 1);
      String pieceRank = saveData.substring(
          pieceRankIndex + pieceRankKey.length(), pieceAllianceIndex - 1);
      String pieceAlliance = saveData.substring(
          pieceAllianceIndex + pieceAllianceKey.length(), saveData.length() - 1);

      if (this.board.isDebugMode()) {
        System.out.println("tileIdIndex=" + tileIdIndex + ";tileId=" + tileId);
        System.out.println("territoryIndex=" + territoryIndex + ";territory=" + territory);
        System.out.println("pieceRankIndex=" + pieceRankIndex + ";piece=" + pieceRank);
        System.out.println("pieceAllianceIndex=" + pieceAllianceIndex + ";pieceAlliance=" + pieceAlliance);
      }

      Piece piece = null;
      if (territory.contains("BLACK") && !pieceRank.contains("null")) {
        piece = BoardUtils.pieceInstanceCreator(pieceRank, playerBlack, Alliance.BLACK);

        piece.setPieceCoords(tileId);
        builder.setPiece(piece);

        if (this.board.isDebugMode())
          System.out.println("Piece has been set");

      } else if (territory.contains("WHITE") && !pieceRank.contains("null")) {
        piece = BoardUtils.pieceInstanceCreator(pieceRank, playerWhite, Alliance.WHITE);

        piece.setPieceCoords(tileId);
        builder.setPiece(piece);

        if (this.board.isDebugMode())
          System.out.println("Piece has been set");
      }
      return true;
    }
    return false;
  }

  public boolean boardStateDataParser(String boardState) {
    if (!boardState.isEmpty() && boardState != null) {
      String firstMoveMakerKey = "firstMoveMaker=";
      String currentTurnKey = "currentTurn=";
      String lastExecutedTurnKey = "lastExecutedTurn=";

      // index
      int firstMoveMakerIndex = boardState.indexOf(firstMoveMakerKey);
      int currentTurnIndex = boardState.indexOf(currentTurnKey);
      int lastExecutedTurnIndex = boardState.indexOf(lastExecutedTurnKey);

      // value
      String firstMoveMaker = boardState.substring(
          firstMoveMakerIndex + firstMoveMakerKey.length(), currentTurnIndex);
      int currentTurn = Integer.parseInt(boardState.substring(
          currentTurnIndex + currentTurnKey.length(), lastExecutedTurnIndex));
      int lastExecutedTurn = Integer.parseInt(boardState.substring(
            lastExecutedTurnIndex + lastExecutedTurnKey.length(), boardState.length() - 1));

      if (firstMoveMaker.contains("BLACK"))
        this.firstMoveMaker = Alliance.BLACK;

      this.currentTurn = currentTurn;
      this.lastExecutedTurn = lastExecutedTurn;

      return true;
    }
    return false;
  }

  public boolean boardExecutionsDataParser(String dataExecution) {
    if (!dataExecution.isEmpty() && dataExecution != null) {
      String turnIdKey = "turnId=";
      String moveTypeKey = "moveType=";
      String sourceAllianceKey = "sourceAlliance=";
      String sourcePieceRankKey = "sourcePiece=";
      String targetPieceKey = "targetPiece=";
      String originCoordsKey = "originCoords=";
      String destinationCoordsKey = "destinationCoords=";
      String isExecutedKey = "isExecuted=";

      int turnIdIndex = dataExecution.indexOf(turnIdKey);
      int moveTypeIndex = dataExecution.indexOf(moveTypeKey);
      int sourceAllianceIndex = dataExecution.indexOf(sourceAllianceKey);
      int sourcePieceRankIndex = dataExecution.indexOf(sourcePieceRankKey);
      int targetPieceIndex = dataExecution.indexOf(targetPieceKey);
      int originCoordsIndex = dataExecution.indexOf(originCoordsKey);
      int destinationCoordsIndex = dataExecution.indexOf(destinationCoordsKey);
      int isExecutedIndex = dataExecution.indexOf(isExecutedKey);

      int turnId = Integer.parseInt(dataExecution.substring(
          turnIdIndex + turnIdKey.length(), moveTypeIndex - 1));
      String moveType = dataExecution.substring(
          moveTypeIndex + moveTypeKey.length(), sourceAllianceIndex - 1);
      String sourceAlliance = dataExecution.substring(
          sourceAllianceIndex + sourceAllianceKey.length(), sourcePieceRankIndex - 1);
      String sourcePieceRank = dataExecution.substring(
          sourcePieceRankIndex + sourcePieceRankKey.length(), targetPieceIndex - 1);
      String targetPiece = dataExecution.substring(
          targetPieceIndex + targetPieceKey.length(), originCoordsIndex - 1);
      int originCoords = Integer.parseInt(dataExecution.substring(
          originCoordsIndex + originCoordsKey.length(), destinationCoordsIndex - 1));
      int destinationCoords = Integer.parseInt(dataExecution.substring(
          destinationCoordsIndex + destinationCoordsKey.length(), isExecutedIndex - 1));
      boolean isExecuted = Boolean.parseBoolean(dataExecution.substring(
          isExecutedIndex + isExecutedKey.length(), dataExecution.length() - 1));

      if (this.board.isDebugMode()) {
        System.out.println("turnIdIndex=" + turnIdIndex + ";turnId=" + turnId);
        System.out.println("moveTypeIndex=" + moveTypeIndex + ";moveType=" + moveType);
        System.out.println("sourceAllianceIndex=" + sourceAllianceIndex + ";sourceAlliance=" + sourceAlliance);
        System.out.println("sourcePieceIndex=" + sourcePieceRankIndex + ";sourcePiece=" + sourcePieceRank);
        System.out.println("targetPieceIndex=" + targetPieceIndex + ";targetPiece=" + targetPiece);
        System.out.println("originCoordsIndex=" + originCoordsIndex + ";originCoords=" + originCoords);
        System.out.println("destinationCoordsIndex=" + destinationCoordsIndex + ";destinationCoords=" + destinationCoords);
        System.out.println("isExecutedIndex=" + isExecutedIndex + ";isExecuted=" + isExecuted);
      }

      Player player = sourceAlliance.contains("BLACK") ? playerBlack : playerWhite;
      Move move = new Move(player, this.board, originCoords, destinationCoords);

      move.setTurnId(turnId);
      move.setMoveType(moveType);
      if (isExecuted)
        move.setExecutionState(true);

      moveHistory.put(turnId, move);

      return true;
    }
    return false;
  }

  public void executeSaveData() {
    this.board.setFirstMoveMaker(this.firstMoveMaker);
    this.board.setCurrentTurn(this.currentTurn);
    this.board.setLastExecutedTurn(this.lastExecutedTurn);

    for (Map.Entry<Integer, Move> entry : moveHistory.entrySet()) {
      if (entry.getValue().isMoveExecuted()) {
        entry.getValue().evaluateMove();
        entry.getValue().execute();
      }
    }
  }

}
