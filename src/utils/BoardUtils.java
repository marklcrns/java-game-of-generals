package utils;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-17
 */
public class BoardUtils {

  public static final int TILE_SPACING = 3;
  public static final int TILE_SIZE = 80;
  public static final int TILE_ROW_COUNT = 9;
  public static final int TILE_COL_COUNT = 8;
  public static final int ALL_TILES_COUNT = TILE_ROW_COUNT * TILE_COL_COUNT;
  public static final int BOARD_WIDTH = TILE_SIZE * TILE_ROW_COUNT;
  public static final int BOARD_HEIGHT = TILE_SIZE * TILE_COL_COUNT;

  public static final int FIRST_ROW_INIT = 0;
  public static final int SECOND_ROW_INIT = FIRST_ROW_INIT + TILE_ROW_COUNT;
  public static final int LAST_ROW_INIT = (TILE_COL_COUNT - 1) * TILE_ROW_COUNT;
  public static final int SECOND_TO_LAST_ROW_INIT = LAST_ROW_INIT - TILE_ROW_COUNT;

  private BoardUtils() {
    throw new RuntimeException("You cannot instantiate BoardUtils class");
  }
}
