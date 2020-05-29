package utils;

import engine.Alliance;
import engine.pieces.Captain;
import engine.pieces.Colonel;
import engine.pieces.Flag;
import engine.pieces.GeneralFive;
import engine.pieces.GeneralFour;
import engine.pieces.GeneralOne;
import engine.pieces.GeneralThree;
import engine.pieces.GeneralTwo;
import engine.pieces.LtCol;
import engine.pieces.LtOne;
import engine.pieces.LtTwo;
import engine.pieces.Major;
import engine.pieces.Piece;
import engine.pieces.Private;
import engine.pieces.Sergeant;
import engine.pieces.Spy;
import engine.player.Player;

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

  public static Piece pieceInstanceCreator(String pieceRankName, Player owner,
                                           Alliance alliance) {
    Piece piece = null;

    if (pieceRankName.contains(GENERAL_FIVE_RANK))
      piece = new GeneralFive(owner, alliance);
    else if (pieceRankName.contains(GENERAL_FOUR_RANK))
      piece = new GeneralFour(owner, alliance);
    else if (pieceRankName.contains(GENERAL_THREE_RANK))
      piece = new GeneralThree(owner, alliance);
    else if (pieceRankName.contains(GENERAL_TWO_RANK))
      piece = new GeneralTwo(owner, alliance);
    else if (pieceRankName.contains(GENERAL_ONE_RANK))
      piece = new GeneralOne(owner, alliance);
    else if (pieceRankName.contains(COLONEL_RANK))
      piece = new Colonel(owner, alliance);
    else if (pieceRankName.contains(LT_COLONEL_RANK))
      piece = new LtCol(owner, alliance);
    else if (pieceRankName.contains(MAJOR_RANK))
      piece = new Major(owner, alliance);
    else if (pieceRankName.contains(CAPTAIN_RANK))
      piece = new Captain(owner, alliance);
    else if (pieceRankName.contains(LT_ONE_RANK))
      piece = new LtOne(owner, alliance);
    else if (pieceRankName.contains(LT_TWO_RANK))
      piece = new LtTwo(owner, alliance);
    else if (pieceRankName.contains(SERGEANT_RANK))
      piece = new Sergeant(owner, alliance);
    else if (pieceRankName.contains(PRIVATE_RANK))
      piece = new Private(owner, alliance);
    else if (pieceRankName.contains(FLAG_RANK))
      piece = new Flag(owner, alliance);
    else if (pieceRankName.contains(SPY_RANK))
      piece = new Spy(owner, alliance);

    // if (piece != null)
    //   System.out.println(piece);

    return piece;
  }

  private BoardUtils() {
    throw new RuntimeException("You cannot instantiate BoardUtils class");
  }
}
