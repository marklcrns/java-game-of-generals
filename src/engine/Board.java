package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import gui.BoardPanel;
import utils.BoardUtils;
import utils.Utils;

/**
 * Main class that orchestrates all the classes of the engine package and gui.
 * This class serves as the driver of the program that builds the board game
 * from scratch using internal and external classes.
 * Uses 1D array for as coordinate system. 9x8 board with 0 to 71 tile indices.
 * Contains BoardBuilder and Tile inner classes.
 *
 * Heavily inspired by https://github.com/amir650/BlackWidow-Chess

 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class Board {

  /** List of all Tiles that contains data of each piece  */
  private static List<Tile> gameBoard;

  /** Player instance that all contains all infos on black pieces */
  private static Player playerBlack;

  /** Player instance that all contains all infos on white pieces */
  private static Player playerWhite;

  /** Black player's name assigned when game initialized */
  private static String playerBlackName;

  /** White player's name assigned when game initialized */
  private static String playerWhiteName;

  /** Black pieces counter */
  private static int blackPiecesLeft = 0;

  /** White pieces counter */
  private static int whitePiecesLeft = 0;

  /** Board builder instance */
  private BoardBuilder customBuilder;

  /** BoardPanel gui instance */
  private BoardPanel boardPanel;

  /** Board initial configurations for saving game state */
  private List<Tile> initBoardConfig;

  /** Game initialization checker */
  private boolean gameInitialized = false;

  /** Game started checker */
  private boolean gameStarted = false;

  /** Debug mode toggle for debugging purposes */
  private static boolean debugMode;

  /** Current turn counter */
  private int currentTurn;

  /** Holds value of last executed turn. Only changes when making a move */
  private int lastExecutedTurn;

  /** Reference to most recent move */
  private Move lastMove;

  /** First move maker */
  private Alliance firstMoveMaker;

  /** Current move maker */
  private Alliance moveMaker;

  /** End game winner */
  private Alliance endGameWinner;

  /**
   * No argument constructor
   */
  public Board() {}

  /**
   * Constructor that takes in two Player instance.
   * @param playerBlack
   * @param playerWhite
   */
  public Board(final Player playerBlack, final Player playerWhite) {
    playerBlack.setBoard(this);
    playerWhite.setBoard(this);
    this.playerBlack = playerBlack;
    this.playerWhite = playerWhite;
  }

  /**
   * Method that empties board Tiles pieces.
   */
  private void emptyBoard() {
    gameBoard = new ArrayList<>();
    // Add new empty Tiles in board
    for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
      // Set Tile territory
      if (i < BoardUtils.ALL_TILES_COUNT / 2)
        this.addTile(i, Alliance.BLACK , false);
      else
        this.addTile(i, Alliance.WHITE , false);
    }
  }

  /**
   * Method that builds board pieces initial arrangement.
   * Depends on BoardBuilder inner class.
   */
  public void buildBoard() {
    this.emptyBoard();

    // Use custom build if exists, else randomly placed pieces build.
    BoardBuilder builder = this.customBuilder == null ?
      new BoardBuilder().createRandomBuild() : this.customBuilder;

    // Insert pieces to Board Tiles based on build config.
    for (Map.Entry<Integer, Piece> entry : builder.boardConfig.entrySet()) {
      // insert piece to Tile if empty
      if (gameBoard.get(entry.getKey()).isTileEmpty()) {
        gameBoard.get(entry.getKey()).insert(entry.getValue());
      }
    };
    blackPiecesLeft = builder.getBlackPiecesCount();
    whitePiecesLeft = builder.getWhitePiecesCount();
  }

  /**
   * Method that sets the builder for this board.
   * @param builder BoardBuilder instance.
   */
  public void setBoardBuilder(BoardBuilder builder) {
    this.customBuilder = builder;
  }

  /**
   * Method that initializes game. Enters initialize mode where players may
   * arrange their respective board pieces.
   */
  public void initGame() {
    buildBoard();

    // Initialize players
    try {
      playerBlack.initPlayer();
    } catch(NullPointerException e) {
      System.out.println("E: BLACK player has not been assigned");
    }
    try {
      playerWhite.initPlayer();
    } catch(NullPointerException e) {
      System.out.println("E: WHITE player has not been assigned");
    }

    this.gameInitialized = true;
    setMoveMaker(playerWhite); // TODO: Option to pick first move

    // Displays Board GUI
    displayBoard();

    if (isDebugMode())
      System.out.println("Board:\n" + this);
  }

  /**
   * Method that swtiches to opposing player a change to arrange pieces.
   */
  public void playerDoneArranging() {
    if (this.getBlackPlayer().isMoveMaker()) {
      setMoveMaker(playerWhite);
    } else {
      setMoveMaker(playerBlack);
    }
  }

  /**
   * Method that starts game. Disables initialize mode and enters actual game.
   * Pieces no longer allowed to be arranged in this state.
   */
  public void startGame() {
    this.gameStarted = true;
    this.gameInitialized = false;
    this.currentTurn = 1;
    this.lastExecutedTurn = 0;
    this.firstMoveMaker = getMoveMaker();

    // Save initial board arrangement for saving and loading game state.
    this.initBoardConfig = new ArrayList<>();
    this.initBoardConfig.addAll(gameBoard);

    if (isDebugMode()) {
      System.out.println(this);
      System.out.println("CurrentTurn: " + currentTurn + "\n" +
                         "TotalPieces: " + (blackPiecesLeft + whitePiecesLeft) + "\n");
    }
  }

  /**
   * Method that Resumes ongoing game state. Companion for Load class.
   */
  // TODO: Finish Load implementation.
  public void resumeGame() {
    this.gameStarted = true;
    this.gameInitialized = false;
    this.firstMoveMaker = getMoveMaker();

    if (isDebugMode()) {
      System.out.println("Game resumed");
    }
  }

  /**
   * Restarts and rebuild game with custom or randon build.
   */
  public void restartGame() {
    buildBoard();
    this.gameStarted = false;
    this.gameInitialized = true;
    this.endGameWinner = null;
    setMoveMaker(playerWhite);
  }

  /**
   * Game initialization checker.
   * @return boolean gameInitialized field.
   */
  public boolean isGameInitialized() {
    return gameInitialized;
  }

  /**
   * Game started checker.
   * @return boolean isGameStarted field.
   */
  public boolean isGameStarted() {
    return gameStarted;
  }

  /**
   * Gets List of Tile that contains initial board
   * pieces arrangement.
   * @return List<Tile> initBoardConfig field.
   */
  public List<Tile> getInitBoardConfig() {
    return this.initBoardConfig;
  }

  /**
   * Method that displays Board via GUI BoardPanel instance.
   */
  public void displayBoard() {
    this.boardPanel = new BoardPanel(this);
  }

  /**
   * Gets boardPanel field instance.
   * @return BoardPanel boardPanel field.
   */
  public BoardPanel getBoardPanel() {
    return this.boardPanel;
  }

  /**
   * Method that sets debug mode state.
   */
  public void setDebugMode(boolean debug) {
    debugMode = debug;
  }

  /**
   * Debug mode checker method. Used staticly by other classes.
   */
  public static boolean isDebugMode() {
    return debugMode;
  }

  /**
   * Gets specific tile from gameBoard field.
   * @param tileId tile number.
   * @return Tile from gameBoard field List.
   */
  public Tile getTile(int tileId) {
    return gameBoard.get(tileId);
  }

  /**
   * Gets current board state.
   * @return List<Tile> gameBoard field.
   */
  public List<Tile> getBoard() {
    return gameBoard;
  }

  /**
   * Swaps two pieces and update piece coordinates.
   * @param sourcePieceCoords source piece coordinates.
   * @param targetPieceCoords target piece coordinates.
   * @return boolean true if successful, else false.
   */
  public boolean swapPiece(int sourcePieceCoords, int targetPieceCoords) {
    if (this.getTile(sourcePieceCoords).isTileOccupied() &&
        this.getTile(targetPieceCoords).isTileOccupied()) {
      Piece sourcePiece = this.getTile(sourcePieceCoords).getPiece().clone();
      Piece targetPiece = this.getTile(targetPieceCoords).getPiece().clone();
      sourcePiece.updateCoords(targetPieceCoords);
      targetPiece.updateCoords(sourcePieceCoords);
      this.getBoard().get(sourcePieceCoords).replace(targetPiece);
      this.getBoard().get(targetPieceCoords).replace(sourcePiece);

      return true;
    }
    return false;
  }

  /**
   * Replaces Tile piece.
   * @param targetCoords target occupied tile to replace.
   * @param sourcePiece new Piece instance to replace with.
   * @return boolean true if successful, else false.
   */
  public boolean replacePiece(int targetCoords, Piece sourcePiece) {
    if (this.getTile(targetCoords).isTileOccupied()) {
      // TODO: improve piece manipulation efficiency
      sourcePiece.updateCoords(targetCoords);
      this.getBoard().get(targetCoords).replace(sourcePiece);
      this.getTile(targetCoords).replace(sourcePiece);

      return true;
    }
    return false;
  }

  /**
   * Moves piece from one Tile to another.
   * @param sourcePieceCoords source piece coordinates.
   * @param targetPieceCoords targetPiece coordinates.
   * @return boolean true if successful, else false.
   */
  public boolean movePiece(int sourcePieceCoords, int targetPieceCoords) {
    // insert copy of source piece into target tile
    if (this.getTile(targetPieceCoords).isTileEmpty()) {
      Piece sourcePieceCopy = this.getTile(sourcePieceCoords).getPiece().clone();
      sourcePieceCopy.updateCoords(targetPieceCoords);
      this.getTile(targetPieceCoords).insert(sourcePieceCopy);
      // delete source piece
      this.getTile(sourcePieceCoords).empty();

      return true;
    }
    return false;
  }

  /**
   * Inserts piece into an empty tile.
   * @param sourcePieceCoords source piece coordinates.
   * @param piece Piece instance to insert.
   * @return boolean true if successful, else false.
   */
  public boolean insertPiece(int sourcePieceCoords, Piece piece) {
    if (this.getTile(sourcePieceCoords).isTileEmpty()) {
      piece.updateCoords(sourcePieceCoords);
      this.getBoard().get(sourcePieceCoords).insert(piece);
      this.getTile(sourcePieceCoords).insert(piece);
      return true;
    }
    return false;
  }

  /**
   * Deletes occupied tile.
   * @param pieceCoords piece coordinates.
   * @return boolean true if successful, else false.
   */
  public boolean deletePiece(int pieceCoords) {
    if (this.getTile(pieceCoords).isTileOccupied()) {
      this.getTile(pieceCoords).empty();

      if (isDebugMode())
        System.out.println(this);

      return true;
    }
    return false;
  }

  /**
   * Method that adds Tile into gameBoard field.
   * @param tileId tile id.
   * @param territory tile territory Alliance.
   * @param occupied is tile occupied by a piece.
   */
  private final void addTile(int tileId, Alliance territory, boolean occupied) {
    gameBoard.add(new Tile(tileId, territory, occupied));
  }

  /**
   * Switches move maker to opposing player.
   */
  public void switchMoveMakerPlayer() {
    if (this.getBlackPlayer().isMoveMaker()) {
      setMoveMaker(playerWhite);
    } else {
      setMoveMaker(playerBlack);
    }

    if (isDebugMode())
      System.out.println(this.getMoveMaker());
  }

  /**
   * Setter method that sets last executed turn.
   * @param turn turn to replace the last executed.
   */
  public void updateLastExecutedTurn(int turn) {
    this.lastExecutedTurn = turn;
  }

  /**
   * Increments game turn.
   */
  public void incrementTurn() {
    this.currentTurn++;

    if (isDebugMode()) {
      System.out.println(this);
      System.out.println("Current Turn: " + currentTurn + "\n");
    }
  }

  /**
   * Decrements game turn.
   */
  public void decrementTurn() {
    this.currentTurn--;

    if (isDebugMode()) {
      System.out.println(this);
      System.out.println("Current Turn: " + currentTurn + "\n");
    }
  }

  /**
   * Gets first move maker.
   * @return Alliance firstMoveMaker field.
   */
  public Alliance getFirstMoveMaker() {
    return this.firstMoveMaker;
  }

  /**
   * Gets last executed turn.
   * @return int lastExecutedTurn field.
   */
  public int getLastExecutedTurn() {
    return this.lastExecutedTurn;
  }

  /**
   * Gets game current turn.
   * @return int currentTurn field.
   */
  public int getCurrentTurn() {
    return this.currentTurn;
  }

  /**
   * Sets first move maker.
   * @param firstMoveMaker first move maker Alliance.
   */
  public void setFirstMoveMaker(Alliance firstMoveMaker) {
    this.firstMoveMaker = firstMoveMaker;
  }

  /**
   * Sets last executed turn.
   * @param lastExecutedTurn last move execution turn.
   */
  public void setLastExecutedTurn(int lastExecutedTurn) {
    this.lastExecutedTurn = lastExecutedTurn;
  }

  /**
   * Sets current turn.
   * @param turn turn to replace the game current turn.
   */
  public void setCurrentTurn(int turn) {
    this.currentTurn = turn;
  }

  /**
   * Gets the most recent Move.
   * @return Move lastMove field.
   */
  public Move getLastMove() {
    return this.lastMove;
  }

  /**
   * Sets last Move field.
   * @param move last move.
   */
  public void setLastMove(Move move) {
    this.lastMove = move;
  }

  /**
   * Sets the required black Player instance.
   * @param player black Player instance.
   */
  public void setPlayerBlack(Player player) {
    player.setBoard(this);
    this.playerBlack = player;
  }

  /**
   * Sets the required white Player instance.
   * @param player white Player instance.
   */
  public void setPlayerWhite(Player player) {
    player.setBoard(this);
    this.playerWhite = player;
  }

  /**
   * Gets specific Player currently registered in this Board based on the
   * alliance.
   * @param alliance Alliance of the Player.
   * @return Player based on the alliance param.
   */
  public Player getPlayer(Alliance alliance) {
    if (alliance == Alliance.BLACK)
      return playerBlack;
    else
      return playerWhite;
  }

  /**
   * Gets the black Player.
   * @return Player playerBlack field.
   */
  public Player getBlackPlayer() {
    return playerBlack;
  }

  /**
   * Gets the white Player.
   * @return Player playerWhite field.
   */
  public Player getWhitePlayer() {
    return playerWhite;
  }

  /**
   * Gets the black player designated name.
   * @return String plaerBlackName field.
   */
  public String getBlackPlayerName() {
    return playerBlackName;
  }

  /**
   * Gets the white player designated name.
   * @return String playerWhiteName.
   */
  public String getWhitePlayerName() {
    return playerWhiteName;
  }

  public void setBlackPlayerName(String playerName) {
    playerBlackName = playerName;
  }

  public void setWhitePlayerName(String playerName) {
    playerWhiteName = playerName;
  }

  public Alliance getMoveMaker() {
    return this.moveMaker;
  }

  public boolean setMoveMaker(Player player) {
    if (!player.isMoveMaker()) {
      if (player.getAlliance() == Alliance.BLACK) {
        playerBlack.makeMoveMaker(true);
        playerWhite.makeMoveMaker(false);
        this.moveMaker = playerBlack.getAlliance();
      } else {
        playerBlack.makeMoveMaker(false);
        playerWhite.makeMoveMaker(true);
        this.moveMaker = playerWhite.getAlliance();
      }
      return true;
    }
    System.out.println("E: " + player.getAlliance() +
                       " player is already the move maker");
    return false;
  }

  public boolean isEndGame() {
    if (this.endGameWinner != null) {
      return true;
    }
    return false;
  }

  public Alliance getEndGameWinner() {
    return this.endGameWinner;
  }

  public boolean setEndGameWinner(Alliance endGameWinner) {
    if (endGameWinner != null) {
      this.endGameWinner = endGameWinner;
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    String debugBoard = "\n    0 1 2 3 4 5 6 7 8\n";
    debugBoard += "    _________________\n";
    for (int i = 0; i < BoardUtils.ALL_TILES_COUNT / 2; i += 9) {
      if (i < 10)
        debugBoard += " " + i + " |";
      else
        debugBoard += i + " |";
      for (int j = i; j < i + 9; j++) {
        if (this.getTile(j).isTileEmpty()) {
          debugBoard += "-";
        } else {
          String rank = this.getTile(j).getPiece().getRank();
          debugBoard += rank.substring(0, 1);
        }
        debugBoard += " ";
      }
      debugBoard += "\n";
    }

    debugBoard += "   |-----------------\n";

    for (int i = BoardUtils.ALL_TILES_COUNT / 2; i < BoardUtils.ALL_TILES_COUNT; i += 9) {
      if (i < 10)
        debugBoard += " " + i + " |";
      else
        debugBoard += i + " |";
      for (int j = i; j < i + 9; j++) {
        if (this.getTile(j).isTileEmpty()) {
          debugBoard += "-";
        } else {
          String rank = this.getTile(j).getPiece().getRank();
          debugBoard += rank.substring(0, 1);
        }
        debugBoard += " ";
      }
      debugBoard += "\n";
    }

    return debugBoard;
  }

  public static class BoardBuilder {

    private Map<Integer, Piece> boardConfig;
    private int blackPiecesCount;
    private int whitePiecesCount;

    public BoardBuilder() {
      this.boardConfig = new HashMap<>();
      this.blackPiecesCount = 0;
      this.whitePiecesCount = 0;
    }

    public int getBlackPiecesCount() {
      return blackPiecesCount;
    }

    public int getWhitePiecesCount() {
      return blackPiecesCount;
    }

    public BoardBuilder createDemoBoardBuild() {
      int[] row = {0, 8, 17, 26};
      // Black territory
      int boardOffset = 0;
      // row 0
      setPiece(new GeneralTwo(playerBlack, Alliance.BLACK, boardOffset + row[0] + 9));
      setPiece(new Major(playerBlack, Alliance.BLACK, boardOffset + row[0] + 8));
      setPiece(new Private(playerBlack, Alliance.BLACK, boardOffset + row[0] + 7));
      setPiece(new Sergeant(playerBlack, Alliance.BLACK, boardOffset + row[0] + 6));
      setPiece(new LtOne(playerBlack, Alliance.BLACK, boardOffset + row[0] + 5));
      setPiece(new Private(playerBlack, Alliance.BLACK, boardOffset + row[0] + 4));
      setPiece(new Flag(playerBlack, Alliance.BLACK, boardOffset + row[0] + 3));
      setPiece(new LtTwo(playerBlack, Alliance.BLACK, boardOffset + row[0] + 2));
      setPiece(new Private(playerBlack, Alliance.BLACK, boardOffset + row[0] + 1));
      // row 1
      setPiece(new Spy(playerBlack, Alliance.BLACK, boardOffset + row[1] + 8));
      setPiece(new Private(playerBlack, Alliance.BLACK, boardOffset + row[1] + 7));
      setPiece(new Captain(playerBlack, Alliance.BLACK, boardOffset + row[1] + 5));
      setPiece(new Spy(playerBlack, Alliance.BLACK, boardOffset + row[1] + 4));
      setPiece(new Colonel(playerBlack, Alliance.BLACK, boardOffset + row[1] + 3));
      setPiece(new Private(playerBlack, Alliance.BLACK, boardOffset + row[1] + 2));
      setPiece(new LtCol(playerBlack, Alliance.BLACK, boardOffset + row[1] + 1));
      // row 2
      setPiece(new GeneralThree(playerBlack, Alliance.BLACK, boardOffset + row[2] + 9));
      setPiece(new Private(playerBlack, Alliance.BLACK, boardOffset + row[2] + 6));
      setPiece(new GeneralFour(playerBlack, Alliance.BLACK, boardOffset + row[2] + 5));
      // row 3
      setPiece(new GeneralOne(playerBlack, Alliance.BLACK, boardOffset + row[3] + 3));
      setPiece(new GeneralFive(playerBlack, Alliance.BLACK, boardOffset + row[3] + 2));
      setPiece(new GeneralFive(playerWhite, Alliance.WHITE, boardOffset + row[3] + 1));

      // White territory
      boardOffset = BoardUtils.ALL_TILES_COUNT / 2;

      // row 0
      setPiece(new GeneralFive(playerWhite, Alliance.BLACK, boardOffset + row[0] + 1));
      setPiece(new GeneralFive(playerWhite, Alliance.WHITE, boardOffset + row[0] + 2));
      setPiece(new GeneralOne(playerWhite, Alliance.WHITE, boardOffset + row[0] + 3));
      // row 1
      setPiece(new GeneralFour(playerWhite, Alliance.WHITE, boardOffset + row[1] + 5));
      setPiece(new Private(playerWhite, Alliance.WHITE, boardOffset + row[1] + 6));
      setPiece(new GeneralThree(playerWhite, Alliance.WHITE, boardOffset + row[1] + 9));
      // row 2
      setPiece(new LtCol(playerWhite, Alliance.WHITE, boardOffset + row[2] + 1));
      setPiece(new Private(playerWhite, Alliance.WHITE, boardOffset + row[2] + 2));
      setPiece(new Colonel(playerWhite, Alliance.WHITE, boardOffset + row[2] + 3));
      setPiece(new Spy(playerWhite, Alliance.WHITE, boardOffset + row[2] + 4));
      setPiece(new Captain(playerWhite, Alliance.WHITE, boardOffset + row[2] + 5));
      setPiece(new Private(playerWhite, Alliance.WHITE, boardOffset + row[2] + 7));
      setPiece(new Spy(playerWhite, Alliance.WHITE, boardOffset + row[2] + 8));
      // row 3
      setPiece(new Private(playerWhite, Alliance.WHITE, boardOffset + row[3] + 1));
      setPiece(new LtTwo(playerWhite, Alliance.WHITE, boardOffset + row[3] + 2));
      setPiece(new Flag(playerWhite, Alliance.WHITE, boardOffset + row[3] + 3));
      setPiece(new Private(playerWhite, Alliance.WHITE, boardOffset + row[3] + 4));
      setPiece(new LtOne(playerWhite, Alliance.WHITE, boardOffset + row[3] + 5));
      setPiece(new Sergeant(playerWhite, Alliance.WHITE, boardOffset + row[3] + 6));
      setPiece(new Private(playerWhite, Alliance.WHITE, boardOffset + row[3] + 7));
      setPiece(new Major(playerWhite, Alliance.WHITE, boardOffset + row[3] + 8));
      setPiece(new Major(playerWhite, Alliance.WHITE, boardOffset + row[3] + 9));
      return this;
    }

    public BoardBuilder createRandomBuild() {
      int[] occupiedTiles = {};

      if (isDebugMode())
        System.out.println("Inserting random pieces...");

      // Black pieces
      int[] blackTerritoryBounds = {0, (BoardUtils.ALL_TILES_COUNT / 2) - 1};

      List<Piece> unsetBlackPieces = new ArrayList<>();
      unsetBlackPieces.add(new GeneralFive(playerBlack, Alliance.BLACK));
      unsetBlackPieces.add(new GeneralFour(playerBlack, Alliance.BLACK));
      unsetBlackPieces.add(new GeneralThree(playerBlack, Alliance.BLACK));
      unsetBlackPieces.add(new GeneralTwo(playerBlack, Alliance.BLACK));
      unsetBlackPieces.add(new GeneralOne(playerBlack, Alliance.BLACK));
      unsetBlackPieces.add(new Colonel(playerBlack, Alliance.BLACK));
      unsetBlackPieces.add(new LtCol(playerBlack, Alliance.BLACK));
      unsetBlackPieces.add(new Major(playerBlack, Alliance.BLACK));
      unsetBlackPieces.add(new Captain(playerBlack, Alliance.BLACK));
      unsetBlackPieces.add(new LtOne(playerBlack, Alliance.BLACK));
      unsetBlackPieces.add(new LtTwo(playerBlack, Alliance.BLACK));
      unsetBlackPieces.add(new Sergeant(playerBlack, Alliance.BLACK));
      unsetBlackPieces.add(new Private(playerBlack, Alliance.BLACK));
      unsetBlackPieces.add(new Flag(playerBlack, Alliance.BLACK));
      unsetBlackPieces.add(new Spy(playerBlack, Alliance.BLACK));

      for (Piece unsetPiece : unsetBlackPieces) {
        if (debugMode)
          System.out.println("Inserting " + unsetPiece.getPieceAlliance() + " " +
                             unsetPiece.getRank() + "...");
        setAllPieceInstanceRandomly(
            this, unsetPiece, blackTerritoryBounds[0],
            blackTerritoryBounds[1], occupiedTiles);
      }

      // White pieces
      int[] whiteTerritoryBounds = {BoardUtils.ALL_TILES_COUNT / 2,
        (BoardUtils.ALL_TILES_COUNT) - 1};

      List<Piece> unsetWhitePieces = new ArrayList<>();
      unsetWhitePieces.add(new GeneralFive(playerWhite, Alliance.WHITE));
      unsetWhitePieces.add(new GeneralFour(playerWhite, Alliance.WHITE));
      unsetWhitePieces.add(new GeneralThree(playerWhite, Alliance.WHITE));
      unsetWhitePieces.add(new GeneralTwo(playerWhite, Alliance.WHITE));
      unsetWhitePieces.add(new GeneralOne(playerWhite, Alliance.WHITE));
      unsetWhitePieces.add(new Colonel(playerWhite, Alliance.WHITE));
      unsetWhitePieces.add(new LtCol(playerWhite, Alliance.WHITE));
      unsetWhitePieces.add(new Major(playerWhite, Alliance.WHITE));
      unsetWhitePieces.add(new Captain(playerWhite, Alliance.WHITE));
      unsetWhitePieces.add(new LtOne(playerWhite, Alliance.WHITE));
      unsetWhitePieces.add(new LtTwo(playerWhite, Alliance.WHITE));
      unsetWhitePieces.add(new Sergeant(playerWhite, Alliance.WHITE));
      unsetWhitePieces.add(new Private(playerWhite, Alliance.WHITE));
      unsetWhitePieces.add(new Flag(playerWhite, Alliance.WHITE));
      unsetWhitePieces.add(new Spy(playerWhite, Alliance.WHITE));

      for (Piece unsetPiece : unsetWhitePieces) {
        setAllPieceInstanceRandomly(
            this, unsetPiece, whiteTerritoryBounds[0],
            whiteTerritoryBounds[1], occupiedTiles);
      }
      return this;
    }

    public boolean setPiece(final Piece piece) {
      // checks if within bounds, correct territory, and piece legal count
      if (isPieceWithinBounds(piece) &&
          isPieceInCorrectTerritory(piece) &&
          isLegalPieceInstanceChecker(piece) &&
          isTileEmpty(piece.getPieceCoords())) {
        boardConfig.put(piece.getPieceCoords(), piece);

        if (piece.getPieceAlliance() == Alliance.BLACK)
          this.blackPiecesCount++;
        else
          this.whitePiecesCount++;

        if (isDebugMode())
          System.out.println(
              piece.getPieceAlliance() + " " +
              piece.getRank() + " piece inserted at " +
              piece.getPieceCoords());

        return true;
      }
      return false;
    }

    public void setAllPieceInstanceRandomly(BoardBuilder builder, Piece piece,
                                            int from, int to,
                                            int[] occupiedTiles) {
      Piece pieceCopy = piece.clone();
      int pieceInstanceCounter = countPieceInstances(piece.getRank(),
                                                     piece.getPieceAlliance());
      int randomEmptyTile;

      while (pieceInstanceCounter < piece.getLegalPieceInstanceCount()) {
        randomEmptyTile = Utils.getRandomWithExclusion(new Random(), from, to, occupiedTiles);
        pieceCopy.setPieceCoords(randomEmptyTile);
        // TODO: Fix to check if randomEmptyTile is empty
        if (builder.setPiece(pieceCopy)) {
          pieceCopy = piece.clone();
          Utils.appendToIntArray(occupiedTiles, randomEmptyTile);
          pieceInstanceCounter++;
        }
      }
    }

    public int countPieceInstances(String rank, Alliance alliance) {
      int pieceInstanceCounter = 0;

      for (Map.Entry<Integer, Piece> entry : boardConfig.entrySet()) {
        if (entry.getValue().getRank() == rank &&
            entry.getValue().getPieceAlliance() == alliance)
          pieceInstanceCounter++;
      }

      return pieceInstanceCounter;
    }

    public boolean isPieceWithinBounds(Piece piece) {
      if (piece.getPieceCoords() < BoardUtils.ALL_TILES_COUNT &&
          piece.getPieceCoords() > 0) {
        return true;
      }

      if (isDebugMode())
        System.out.println(piece.getPieceAlliance() + " " +
                           piece.getRank() + " at Tile" +
                           piece.getPieceCoords() + " is out of bounds." +
                           " Piece not inserted.");
      return false;
    }

    public boolean isPieceInCorrectTerritory(Piece piece) {
      if ((piece.getPieceAlliance() == Alliance.BLACK &&
            piece.getPieceCoords() < BoardUtils.ALL_TILES_COUNT / 2) ||
          (piece.getPieceAlliance() == Alliance.WHITE &&
            piece.getPieceCoords() > BoardUtils.ALL_TILES_COUNT / 2)) {
        return true;
      }

      if (isDebugMode())
        System.out.println("E: " + piece.getPieceAlliance() + " " +
                           piece.getRank() + " at Tile " +
                           piece.getPieceCoords() + " is in illegal territory." +
                           " Piece not inserted.");
      return false;
    }

    public boolean isLegalPieceInstanceChecker(Piece piece) {
      int pieceInstanceCounter = 0;
      for (Map.Entry<Integer, Piece> entry : this.boardConfig.entrySet()) {
        if (piece.getRank() == entry.getValue().getRank() &&
            piece.getPieceAlliance() == entry.getValue().getPieceAlliance())
          pieceInstanceCounter++;
      }

      if (pieceInstanceCounter <= piece.getLegalPieceInstanceCount())
        return true;

      if (isDebugMode())
        System.out.println("E: " + piece.getRank() + " exceeded maximum instance." +
                           " Piece not inserted.");
      return false;
    }

    public boolean isTileEmpty(int coords) {
      if (!boardConfig.containsKey(coords))
        return true;

      if (isDebugMode())
        System.out.println("E: TIle " + coords + " is occupied");
      return false;
    }

    public Map<Integer, Piece> getBoardConfig() {
      try {
        return this.boardConfig;
      } catch(NullPointerException e) {
        System.out.println("BuilderBoard Error: Board config does not exist");
        return null;
      }

    }

    @Override
    public String toString() {
      String builder = "BoardBuilder boardConfig=" + boardConfig.size() + "\n";

      for (Map.Entry<Integer, Piece> entry : boardConfig.entrySet()) {
        builder += "tileId=" + entry.getKey() +
                   ";piece=" + entry.getValue().getRank() +
                   ";pieceAlliance=" + entry.getValue().getPieceAlliance() +
                   "\n";
      }

      return builder;
    }

  } // BoardBuilder

  public static class Tile {

    private final int tileId;
    private final Alliance territory;
    private boolean occupied;
    private Piece piece;

    public Tile(int tileId, Alliance territory, boolean occupied) {
      this.tileId = tileId;
      this.territory = territory;
      this.occupied = occupied;
    }

    public boolean isTileEmpty() {
      if (!this.occupied) {
        return true;
      }
      return false;
    }

    public boolean isTileOccupied() {
      if (this.occupied) {
        return true;
      }
      return false;
    }

    public int getTileId() {
      return this.tileId;
    }

    public Alliance getTerritory() {
      return this.territory;
    }

    public Piece getPiece() {
      return this.piece;
    }

    public boolean insert(Piece piece) {
      if (isTileEmpty()) {
        this.piece = piece;
        this.occupied = true;
        return true;
      }
      return false;
    }

    public boolean replace(Piece piece) {
      if (isTileOccupied()) {
        this.piece = piece;
        return true;
      }
      return false;
    }

    public boolean empty() {
      if (isTileOccupied()) {
        this.piece = null;
        this.occupied = false;
        return true;
      }
      return false;
    }

    @Override
    public String toString() {
      if (this.occupied)
        return "Tile " + this.tileId + " contains " +
          this.piece.getPieceAlliance() + " " + this.piece.getRank();
      else
        return "Tile " + this.tileId + " is empty";
    }
  }
}
