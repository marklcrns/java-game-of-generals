package utils;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-17
 */
public class BoardUtils {

  public static final int TILE_SPACING = 3;
  public static final int TILE_SIZE = 80;
  public static final int TILE_COLUMN_COUNT = 9;
  public static final int TILE_ROW_COUNT = 8;
  public static final int ALL_TILES_COUNT = TILE_COLUMN_COUNT * TILE_ROW_COUNT;
  public static final int BOARD_WIDTH = TILE_SIZE * TILE_COLUMN_COUNT;
  public static final int BOARD_HEIGHT = TILE_SIZE * TILE_ROW_COUNT;

  public static final String GENERAL_FIVE_RANK = "G5";
  public static final String GENERAL_FOUR_RANK = "G4";
  public static final String GENERAL_THREE_RANK = "G3";
  public static final String GENERAL_TWO_RANK = "G2";
  public static final String GENERAL_ONE_RANK = "G1";
  public static final String COLONEL_RANK = "C";
  public static final String LT_COLONEL_RANK = "LT_COL";
  public static final String MAJOR_RANK = "M";
  public static final String CAPTAIN_RANK = "CN";
  public static final String LT_ONE_RANK = "LT1";
  public static final String LT_TWO_RANK = "LT2";
  public static final String SERGEANT_RANK = "SN";
  public static final String SPY_RANK = "S";
  public static final String PRIVATE_RANK = "P";
  public static final String FLAG_RANK = "F";

  public static final int FIRST_ROW_INIT = 0;
  public static final int SECOND_ROW_INIT = FIRST_ROW_INIT + TILE_COLUMN_COUNT;

  public static final int LAST_ROW_INIT = (TILE_ROW_COUNT - 1) * TILE_COLUMN_COUNT;
  public static final int SECOND_TO_LAST_ROW_INIT = LAST_ROW_INIT - TILE_COLUMN_COUNT;

  private BoardUtils() {
    throw new RuntimeException("You cannot instantiate BoardUtils class");
  }
}
