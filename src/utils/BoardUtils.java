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

  public static final String GENERAL_FIVE_RANK = "GeneralFive";
  public static final String GENERAL_FOUR_RANK = "GeneralFour";
  public static final String GENERAL_THREE_RANK = "GeneralThree";
  public static final String GENERAL_TWO_RANK = "GeneralTwo";
  public static final String GENERAL_ONE_RANK = "GeneralOne";
  public static final String COLONEL_RANK = "Colonel";
  public static final String LT_COLONEL_RANK = "LtCol";
  public static final String MAJOR_RANK = "Major";
  public static final String CAPTAIN_RANK = "Captain";
  public static final String LT_ONE_RANK = "LtOne";
  public static final String LT_TWO_RANK = "LtTwo";
  public static final String SERGEANT_RANK = "Sergeant";
  public static final String PRIVATE_RANK = "Private";
  public static final String FLAG_RANK = "Flag";
  public static final String SPY_RANK = "Spy";

  public static final int FIRST_ROW_INIT = 0;
  public static final int SECOND_ROW_INIT = FIRST_ROW_INIT + TILE_COLUMN_COUNT;

  public static final int LAST_ROW_INIT = (TILE_ROW_COUNT - 1) * TILE_COLUMN_COUNT;
  public static final int SECOND_TO_LAST_ROW_INIT = LAST_ROW_INIT - TILE_COLUMN_COUNT;

  private BoardUtils() {
    throw new RuntimeException("You cannot instantiate BoardUtils class");
  }
}
