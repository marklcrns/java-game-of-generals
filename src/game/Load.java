package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import engine.Board;
import engine.Board.BoardBuilder;
import engine.Board.Tile;
import engine.player.Player;
import utils.Utils;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-28
 */
public class Load {

  private static final String DATA_PATH = "data/save/";
  private static BoardBuilder builder;
  private Player playerBlack;
  private Player playerWhite;
  private static Board board;
  private final String fileName;


  private Load(Board board, String fileName) {
    this.board = board;
    this.fileName = fileName;
  }

  public static String[] getSaveList() {
    File saveListPathFiles = new File(DATA_PATH);
    String[] saveList = saveListPathFiles.list();

    return saveList;
  }

  public static void loadSaveGame(String saveFileName) {
    File saveFile = new File(DATA_PATH + saveFileName);

    String[] saveData = {};
    try {
      Scanner scan = new Scanner(saveFile);

      while (scan.hasNextLine()) {
        String scanLine = scan.nextLine();

        if (board.isDebugMode())
          System.out.println(scanLine);

        Utils.appendToStringArray(saveData, scanLine);
      }

      parseSaveData(saveData);
      scan.close();
    } catch (FileNotFoundException e) {
      System.out.println("Load error: Save data not found");
    }
  }

  public static void parseSaveData(String[] saveData) {
    boolean isBoardConfig = false;
    boolean isFirstMoveMaker = false;
    boolean isBoardExecutions = false;
    for (int i = 0; i < saveData.length; i++) {

      System.out.println(saveData[i]);

      if (saveData[i].contains("BoardConfig")) {
        isBoardConfig = true;
      } else if (saveData[i].contains("FirstMoveMaker")) {
        isBoardConfig = false;
        isFirstMoveMaker = true;
      } else if (saveData[i].contains("BoardExecutions")) {
        isBoardConfig = false;
        isFirstMoveMaker = false;
        isBoardExecutions = true;
      }

      if (isBoardConfig) {
        int tileIdIndex = saveData[i].indexOf("tileId=");
        int territoryIndex = saveData[i].indexOf("territory=");
        int pieceIndex = saveData[i].indexOf("piece=");
        int pieceAllianceIndex = saveData[i].indexOf("pieceAlliance=");

        int tileId = Integer.parseInt(saveData[i].substring(tileIdIndex + 7, territoryIndex - 1));
        String territory = saveData[i].substring(territoryIndex + 10, pieceIndex - 1);
        String piece = saveData[i].substring(pieceIndex + 6, pieceAllianceIndex - 1);
        String pieceAlliance = saveData[i].substring(pieceAllianceIndex + 14, saveData[i].length() - 2);

        System.out.println("tileId: " + tileId);
        System.out.println("territory: " + territory);
        System.out.println("piece: " + piece);
        System.out.println("pieceAlliance: " + pieceAlliance);

      } else if (isFirstMoveMaker) {

      } else if (isBoardExecutions) {

      }
    }
  }

  public void executeSaveData() {}


}
