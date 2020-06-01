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
import gui.BoardPanel;
import utils.BoardUtils;
import utils.Utils;

/**
 * Load class that loads saved game state data into the board engine.
 *
 * Author: Mark Lucernas
 * Date: 2020-05-28
 */
public class Load {

  /** Saved game state data path. */
  private static final String DATA_PATH = "data/save/";

  /** Reference to the Board engine. */
  private final Board board;

  /** Reference to the BoardPanel */
  private final BoardPanel boardPanel;

  /** Stores the save data filename. */
  private final String fileName;

  /** Stores the move history of the saved game state. */
  private final Map<Integer, Move> moveHistory;

  /** Stores first move maker from the saved game state. */
  private Alliance firstMoveMaker;

  /** Stores the current turn ID from the saved game state. */
  private int currentTurn;

  /** Stores the last executed turn ID from the saved game state. */
  private int lastExecutedTurn;

  /** BoardBuilder instance for rebuilding saved game state. */
  private final BoardBuilder builder;

  /** Black player instance */
  private Player playerBlack;

  /** White player instance */
  private Player playerWhite;

  /**
   * Constructor method that takes in the Board engine the filename of the saved
   * game data.
   * @param board the Board engine
   * @param filename filename of the saved data.
   */
  public Load(final Board board, final String filename) {
    // TODO: Fix game not loading properly after being saved from a loaded game.
    this.board = board;
    this.boardPanel = board.getBoardPanel();
    this.playerBlack = this.board.getBlackPlayer();
    this.playerWhite = this.board.getWhitePlayer();

    this.fileName = filename;
    this.moveHistory = new HashMap<Integer, Move>();
    this.builder = new BoardBuilder();
  }

  /**
   * Gets the list of all existing saved game state data from save directory.
   * @return String[] of all saved data filenames.
   */
  public static String[] getSaveList() {
    final File saveListPathFiles = new File(DATA_PATH);
    final String[] saveList = saveListPathFiles.list();

    return saveList;
  }

  /**
   * Loads the save game data file and stores all data into this class fields
   * then reexecute all moves from history.
   */
  public void loadSaveGame() {
    final File saveFile = new File(DATA_PATH + fileName);

    String[] saveData = {};
    try {
      final Scanner scan = new Scanner(saveFile);

      while (scan.hasNextLine()) {
        final String scanLine = scan.nextLine();
        if (board.isDebugMode())
          System.out.println(scanLine);
        saveData = Utils.appendToStringArray(saveData, scanLine);
      }
      scan.close();

      // Parse saved data and execute.
      if (parseSaveData(saveData)) {
        this.board.startGame();

        this.board.getBoardPanel().clearBoardPanel();
        executeSaveData();
        if (this.board.isDebugMode())
          System.out.println("Game successfully loaded");
      }
    } catch (final FileNotFoundException e) {
      System.out.println("Load error: Save data not found");
    }
  }

  /**
   * Parse saved game state data and stores all data into this class fields.
   * @return boolean true if successful, else false.
   */
  public boolean parseSaveData(final String[] saveData) {
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
            System.out.println("\ndataLength=" + saveData[i].length() +
                               "\nsaveData=" + saveData[i] + "\n");
          boardConfigDataParser(saveData[i]);
        } else if (isBoardStateData) {
          if (this.board.isDebugMode())
            System.out.println("\ndataLength=" + saveData[i].length() +
                               "\nsaveData=" + saveData[i] + "\n");
          boardStateDataParser(saveData[i]);
        } else if (isBoardExecutions) {
          if (this.board.isDebugMode())
            System.out.println("\ndataLength=" + saveData[i].length() +
                               "\nsaveData=" + saveData[i] + "\n");
          boardExecutionsDataParser(saveData[i]);
        }

      }
      if (this.board.isDebugMode()) {
        System.out.println("Move history Loaded:\n");
        for (final Map.Entry<Integer, Move> entry : moveHistory.entrySet()) {
          System.out.println("TurnId=" + entry.getKey() +
                             ";Move=" + entry.getValue());
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Board config data parser that reads a line from saved game state data and
   * restore into board config field.
   * @param saveData String data from saved game state data under board config.
   * @return boolean true if successful, else false.
   */
  public boolean boardConfigDataParser(final String saveData) {
    if (!saveData.isEmpty() && saveData != null) {
      final String tileIdKey = "tileId=";
      final String territoryKey = "territory=";
      final String pieceRankKey = "piece=";
      final String pieceAllianceKey = "pieceAlliance=";

      // Index
      final int tileIdIndex = saveData.indexOf(tileIdKey);
      final int territoryIndex = saveData.indexOf(territoryKey);
      final int pieceRankIndex = saveData.indexOf(pieceRankKey);
      final int pieceAllianceIndex = saveData.indexOf(pieceAllianceKey);

      // Value
      final int tileId = Integer.parseInt(saveData.substring(
            tileIdIndex + tileIdKey.length(), territoryIndex - 1));
      final String territory = saveData.substring(
          territoryIndex + territoryKey.length(), pieceRankIndex - 1);
      final String pieceRank = saveData.substring(
          pieceRankIndex + pieceRankKey.length(), pieceAllianceIndex - 1);
      final String pieceAlliance = saveData.substring(
          pieceAllianceIndex + pieceAllianceKey.length(), saveData.length() - 1);

      if (this.board.isDebugMode()) {
        System.out.println("tileIdIndex=" + tileIdIndex +
                           ";tileId=" + tileId);
        System.out.println("territoryIndex=" + territoryIndex +
                           ";territory=" + territory);
        System.out.println("pieceRankIndex=" + pieceRankIndex +
                           ";piece=" + pieceRank);
        System.out.println("pieceAllianceIndex=" + pieceAllianceIndex +
                           ";pieceAlliance=" + pieceAlliance);
      }

      // Rebuild board configuration.
      Piece piece = null;
      if (territory.contains("BLACK") && !pieceRank.contains("null")) {
        piece = BoardUtils.pieceInstanceCreator(
            pieceRank, playerBlack, Alliance.BLACK);

        piece.setPieceCoords(tileId);
        builder.setPiece(piece);

        if (this.board.isDebugMode())
          System.out.println("Piece has been set");

      } else if (territory.contains("WHITE") && !pieceRank.contains("null")) {
        piece = BoardUtils.pieceInstanceCreator(
            pieceRank, playerWhite, Alliance.WHITE);

        piece.setPieceCoords(tileId);
        builder.setPiece(piece);

        if (this.board.isDebugMode())
          System.out.println("Piece has been set");
      }
      return true;
    }
    return false;
  }

  /**
   * Board state data parser that reads a line from saved game state data and
   * restore into corresponding field.
   * @param boardState String data from saved game state data under board state.
   * @return boolean true if successful, else false.
   */
  public boolean boardStateDataParser(final String boardState) {
    if (!boardState.isEmpty() && boardState != null) {
      final String firstMoveMakerKey = "firstMoveMaker=";
      final String currentTurnKey = "currentTurn=";
      final String lastExecutedTurnKey = "lastExecutedTurn=";

      // index
      final int firstMoveMakerIndex = boardState.indexOf(firstMoveMakerKey);
      final int currentTurnIndex = boardState.indexOf(currentTurnKey);
      final int lastExecutedTurnIndex = boardState.indexOf(lastExecutedTurnKey);

      // value
      final String firstMoveMaker = boardState.substring(
          firstMoveMakerIndex + firstMoveMakerKey.length(), currentTurnIndex - 1);
      final int currentTurn = Integer.parseInt(boardState.substring(
          currentTurnIndex + currentTurnKey.length(), lastExecutedTurnIndex - 1));
      final int lastExecutedTurn = Integer.parseInt(boardState.substring(
            lastExecutedTurnIndex + lastExecutedTurnKey.length(), boardState.length() - 1));

      if (firstMoveMaker.contains("BLACK"))
        this.firstMoveMaker = Alliance.BLACK;
      else
        this.firstMoveMaker = Alliance.WHITE;

      this.currentTurn = currentTurn;
      this.lastExecutedTurn = lastExecutedTurn;
      return true;
    }
    return false;
  }

  /**
   * Board execution data parser that reads a line from the saved game state
   * data and restore into move history field.
   * @param String data execution from the saved game state data under board
   * execution.
   * @return boolean true if successful, else false.
   */
  public boolean boardExecutionsDataParser(final String dataExecution) {
    if (!dataExecution.isEmpty() && dataExecution != null) {
      final String turnIdKey = "turnId=";
      final String moveTypeKey = "moveType=";
      final String sourceAllianceKey = "sourceAlliance=";
      final String sourcePieceRankKey = "sourcePiece=";
      final String targetPieceKey = "targetPiece=";
      final String originCoordsKey = "originCoords=";
      final String destinationCoordsKey = "destinationCoords=";
      final String isExecutedKey = "isExecuted=";

      // Index
      final int turnIdIndex = dataExecution.indexOf(turnIdKey);
      final int moveTypeIndex = dataExecution.indexOf(moveTypeKey);
      final int sourceAllianceIndex = dataExecution.indexOf(sourceAllianceKey);
      final int sourcePieceRankIndex = dataExecution.indexOf(sourcePieceRankKey);
      final int targetPieceIndex = dataExecution.indexOf(targetPieceKey);
      final int originCoordsIndex = dataExecution.indexOf(originCoordsKey);
      final int destinationCoordsIndex = dataExecution.indexOf(destinationCoordsKey);
      final int isExecutedIndex = dataExecution.indexOf(isExecutedKey);

      // Value
      final int turnId = Integer.parseInt(dataExecution.substring(
          turnIdIndex + turnIdKey.length(), moveTypeIndex - 1));
      final String moveType = dataExecution.substring(
          moveTypeIndex + moveTypeKey.length(), sourceAllianceIndex - 1);
      final String sourceAlliance = dataExecution.substring(
          sourceAllianceIndex + sourceAllianceKey.length(), sourcePieceRankIndex - 1);
      final String sourcePieceRank = dataExecution.substring(
          sourcePieceRankIndex + sourcePieceRankKey.length(), targetPieceIndex - 1);
      final String targetPiece = dataExecution.substring(
          targetPieceIndex + targetPieceKey.length(), originCoordsIndex - 1);
      final int originCoords = Integer.parseInt(dataExecution.substring(
          originCoordsIndex + originCoordsKey.length(), destinationCoordsIndex - 1));
      final int destinationCoords = Integer.parseInt(dataExecution.substring(
          destinationCoordsIndex + destinationCoordsKey.length(), isExecutedIndex - 1));
      final boolean isExecuted = Boolean.parseBoolean(dataExecution.substring(
          isExecutedIndex + isExecutedKey.length(), dataExecution.length() - 1));

      if (this.board.isDebugMode()) {
        System.out.println("turnIdIndex=" + turnIdIndex +
                           ";turnId=" + turnId);
        System.out.println("moveTypeIndex=" + moveTypeIndex +
                           ";moveType=" + moveType);
        System.out.println("sourceAllianceIndex=" + sourceAllianceIndex +
                           ";sourceAlliance=" + sourceAlliance);
        System.out.println("sourcePieceIndex=" + sourcePieceRankIndex +
                           ";sourcePiece=" + sourcePieceRank);
        System.out.println("targetPieceIndex=" + targetPieceIndex +
                           ";targetPiece=" + targetPiece);
        System.out.println("originCoordsIndex=" + originCoordsIndex +
                           ";originCoords=" + originCoords);
        System.out.println("destinationCoordsIndex=" + destinationCoordsIndex +
                           ";destinationCoords=" + destinationCoords);
        System.out.println("isExecutedIndex=" + isExecutedIndex +
                           ";isExecuted=" + isExecuted);
      }

      // Recreate move.
      final Player player = sourceAlliance.contains("BLACK") ? playerBlack : playerWhite;
      final Move move = new Move(player, this.board, originCoords, destinationCoords);

      // Reexecute executable Move.
      move.setTurnId(turnId);
      move.setMoveType(moveType);
      if (isExecuted)
        move.setExecutionState(true);

      // Store into moveHistory field.
      moveHistory.put(turnId, move);

      return true;
    }
    return false;
  }

  /**
   * Executes loaded saved game state data if ready.
   * @return boolean true if successful, else false.
   */
  public boolean executeSaveData() {

    if (isReadyToExecute()) {
      this.board.setFirstMoveMaker(this.firstMoveMaker);
      // this.board.setCurrentTurn(this.currentTurn);
      // this.board.setLastExecutedTurn(this.lastExecutedTurn);

      for (final Map.Entry<Integer, Move> entry : moveHistory.entrySet()) {
        if (entry.getValue().isMoveExecuted()) {

          if (this.board.getMoveMaker() == Alliance.BLACK)
            playerBlack.makeMove(entry.getValue());
          else
            playerWhite.makeMove(entry.getValue());

          this.board.getBoardPanel().getMoveHistoryPanel().appendToMoveHistory(
              entry.getValue());

          System.out.println(this.board);

          if (this.board.isDebugMode())
            System.out.println("Executing turn " + entry.getKey() + ". Successful");
        }
      }
      return true;
    } else {
      if (this.board.isDebugMode())
        System.out.println("E: Loading saved game failed");
    }
    return false;
  }

  /**
   * Checks if all data from saved game state has been loaded and ready to be
   * executed.
   * @return boolean if ready to execute, else false.
   */
  public boolean isReadyToExecute() {
    if (this.firstMoveMaker != null &&
        this.currentTurn > 0 &&
        this.lastExecutedTurn > 0 &&
        builder != null &&
        moveHistory != null)
      return true;

    if (this.board.isDebugMode()) {
      System.out.println(
          "First move maker: " + (this.firstMoveMaker != null ? this.firstMoveMaker : "null") + "\n" +
          "Current Turn: " + this.currentTurn + "\n" +
          "Last executed turn: " + this.lastExecutedTurn + "\n" +
          "Builder: " + (this.builder != null ? "Loaded" : "null") + "\n" +
          "Move history: " + (this.moveHistory != null ? "Loaded" : "null"));
    }
    return false;
  }

} // Load
