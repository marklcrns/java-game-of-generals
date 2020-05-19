package utils;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-17
 */
public class BoardUtils {

  public final static int TILE_SPACING = 3;
  public final static int TILE_SIZE = 80;
  public final static int TILE_ROW_COUNT = 9;
  public final static int TILE_COL_COUNT = 8;
  public final static int ALL_TILES_COUNT = TILE_ROW_COUNT * TILE_COL_COUNT;
  public final static int BOARD_WIDTH = TILE_SIZE * TILE_ROW_COUNT;
  public final static int BOARD_HEIGHT = TILE_SIZE * TILE_COL_COUNT;

  private BoardUtils() {
    throw new RuntimeException("You cannot instantiate BoardUtils class");
  }

}
