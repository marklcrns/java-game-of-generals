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
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class Board {

  private static List<Tile> gameBoard;
  private static Player playerBlack;
  private static Player playerWhite;
  private static int blackPiecesLeft = 0;
  private static int whitePiecesLeft = 0;
  private BoardPanel boardPanel;
  private BoardBuilder customBuilder;
  private List<Tile> initBoardConfig;
  private Map<Integer, Piece> initBoardBuilderConfig;
  private boolean gameInitialized = false;
  private boolean gameStarted = false;
  private static boolean debugMode;
  private int currentTurn;
  private int lastExecutedTurn;
  private Move lastMove;
  private Alliance firstMoveMaker;
  private Alliance moveMaker;
  private Alliance endGameWinner;

  public Board() {}

  public Board(final Player playerBlack, final Player playerWhite) {
    playerBlack.setBoard(this);
    playerWhite.setBoard(this);
    this.playerBlack = playerBlack;
    this.playerWhite = playerWhite;
  }

  private void emptyBoard() {
    gameBoard = new ArrayList<>();
    // Add new empty Tiles in board
    for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
      // set territory
      if (i < BoardUtils.ALL_TILES_COUNT / 2)
        this.addTile(i, Alliance.BLACK , false);
      else
        this.addTile(i, Alliance.WHITE , false);
    }
  }

  public void buildBoard() {
    this.emptyBoard();
    BoardBuilder builder = this.customBuilder == null ?
      new BoardBuilder().createRandomBuild() : this.customBuilder;

    for (Map.Entry<Integer, Piece> entry : builder.boardConfig.entrySet()) {
      if (gameBoard.get(entry.getKey()).isTileEmpty()) {
        // insert piece to tile
        gameBoard.get(entry.getKey()).insert(entry.getValue());
      }
    };
    blackPiecesLeft = builder.getBlackPiecesCount();
    whitePiecesLeft = builder.getWhitePiecesCount();

    this.initBoardBuilderConfig = builder.getBoardConfig();
  }

  public void setBoardBuilder(BoardBuilder builder) {
    this.customBuilder = builder;
  }

  public void initGame() {
    this.buildBoard();
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
    setMoveMaker(playerWhite);

    displayBoard();

    if (isDebugMode())
      System.out.println("Board:\n" + this);
  }

  public void playerDoneArranging() {
    if (this.getBlackPlayer().isMoveMaker()) {
      setMoveMaker(playerWhite);
    } else {
      setMoveMaker(playerBlack);
    }
  }

  public void startGame() {
    this.gameStarted = true;
    this.gameInitialized = false;
    this.currentTurn = 1;
    this.lastExecutedTurn = 0;
    this.firstMoveMaker = getMoveMaker();

    this.initBoardConfig = new ArrayList<>();
    this.initBoardConfig.addAll(gameBoard);

    if (isDebugMode()) {
      System.out.println(this);
      System.out.println("CurrentTurn: " + currentTurn + "\n" +
                         "TotalPieces: " + (blackPiecesLeft + whitePiecesLeft) + "\n");
    }
  }

  public void resumeGame() {
    this.gameStarted = true;
    this.gameInitialized = false;
    this.currentTurn = 1;
    this.lastExecutedTurn = 0;
    this.firstMoveMaker = getMoveMaker();

    if (isDebugMode()) {
      System.out.println(this);
      System.out.println("CurrentTurn: " + currentTurn + "\n");
    }
  }

  public void restartGame() {
    buildBoard();
    this.gameStarted = false;
    this.gameInitialized = true;
    this.endGameWinner = null;
    setMoveMaker(playerWhite);
  }

  public boolean isGameInitialized() {
    return gameInitialized;
  }

  public boolean isGameStarted() {
    return gameStarted;
  }

  public List<Tile> getInitBoardConfig() {
    return this.initBoardConfig;
  }

  public Map<Integer, Piece> getBoardBuilderConfig() {
    try {
      return this.initBoardBuilderConfig;
    } catch(NullPointerException e) {
      System.out.println("Board error: initBoardBuilder was not initialized");
      return null;
    }
  }

  public void displayBoard() {
    this.boardPanel = new BoardPanel(this);
  }

  public BoardPanel getBoardPanel() {
    return this.boardPanel;
  }

  public void setDebugMode(boolean debug) {
    debugMode = debug;
  }

  public static boolean isDebugMode() {
    return debugMode;
  }

  public Tile getTile(int coordinates) {
    return gameBoard.get(coordinates);
  }

  public List<Tile> getBoard() {
    return gameBoard;
  }

  public boolean swapPiece(int sourceCoords, int targetCoords) {
    if (this.getTile(sourceCoords).isTileOccupied() &&
        this.getTile(targetCoords).isTileOccupied()) {
      Piece sourcePiece = this.getTile(sourceCoords).getPiece().makeCopy();
      Piece targetPiece = this.getTile(targetCoords).getPiece().makeCopy();
      sourcePiece.updateCoords(targetCoords);
      targetPiece.updateCoords(sourceCoords);
      this.getBoard().get(sourceCoords).replace(targetPiece);
      this.getBoard().get(targetCoords).replace(sourcePiece);

      return true;
    }
    return false;
  }

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

  public boolean movePiece(int sourceCoords, int targetCoords) {
    // insert copy of source piece into target tile
    if (this.getTile(targetCoords).isTileEmpty()) {
      Piece sourcePieceCopy = this.getTile(sourceCoords).getPiece().makeCopy();
      sourcePieceCopy.updateCoords(targetCoords);
      this.getTile(targetCoords).insert(sourcePieceCopy);
      // delete source piece
      this.getTile(sourceCoords).empty();

      return true;
    }
    return false;
  }

  public boolean insertPiece(int sourceCoords, Piece piece) {
    if (this.getTile(sourceCoords).isTileEmpty()) {
      piece.updateCoords(sourceCoords);
      this.getBoard().get(sourceCoords).insert(piece);
      this.getTile(sourceCoords).insert(piece);
      return true;
    }
    return false;
  }

  public boolean deletePiece(int pieceCoords) {
    if (this.getTile(pieceCoords).isTileOccupied()) {
      this.getTile(pieceCoords).empty();

      if (isDebugMode())
        System.out.println(this);

      return true;
    }
    return false;
  }

  private final void addTile(int tileId, Alliance territory, boolean occupied) {
    gameBoard.add(new Tile(tileId, territory, occupied));
  }

  public void switchMoveMakerPlayer() {
    if (this.getBlackPlayer().isMoveMaker()) {
      setMoveMaker(playerWhite);
    } else {
      setMoveMaker(playerBlack);
    }

    if (isDebugMode())
      System.out.println(this.getMoveMaker());
  }

  public void updateLastExecutedTurn(int turn) {
    this.lastExecutedTurn = turn;
  }

  public void incrementTurn() {
    this.currentTurn++;
    if (isDebugMode()) {
      System.out.println(this);
      System.out.println("Current Turn: " + currentTurn + "\n");
    }
  }

  public void decrementTurn() {
    this.currentTurn--;
    if (isDebugMode()) {
      System.out.println(this);
      System.out.println("Current Turn: " + currentTurn + "\n");
    }
  }

  public Alliance getFirstMoveMaker() {
    return this.firstMoveMaker;
  }

  public int getLastExecutedTurn() {
    return this.lastExecutedTurn;
  }

  public int getCurrentTurn() {
    return this.currentTurn;
  }

  public void setFirstMoveMaker(Alliance firstMoveMaker) {
    this.firstMoveMaker = firstMoveMaker;
  }

  public void setLastExecutedTurn(int lastExecutedTurn) {
    this.lastExecutedTurn = lastExecutedTurn;
  }

  public void setCurrentTurn(int turn) {
    this.currentTurn = turn;
  }

  public Move getLastMove() {
    return this.lastMove;
  }

  public void setLastMove(Move move) {
    this.lastMove = move;
  }

  public void addPlayerBlack(Player player) {
    player.setBoard(this);
    this.playerBlack = player;
  }

  public void addPlayerWhite(Player player) {
    player.setBoard(this);
    this.playerWhite = player;
  }

  public Player getPlayer(Alliance alliance) {
    if (alliance == Alliance.BLACK)
      return playerBlack;
    else
      return playerWhite;
  }

  public Player getBlackPlayer() {
    return playerBlack;
  }

  public Player getWhitePlayer() {
    return playerWhite;
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
      Piece pieceCopy = piece.makeCopy();
      int pieceInstanceCounter = countPieceInstances(piece.getRank(),
                                                     piece.getPieceAlliance());
      int randomEmptyTile;

      while (pieceInstanceCounter < piece.getLegalPieceInstanceCount()) {
        randomEmptyTile = Utils.getRandomWithExclusion(new Random(), from, to, occupiedTiles);
        pieceCopy.setPieceCoords(randomEmptyTile);
        // TODO: Fix to check if randomEmptyTile is empty
        if (builder.setPiece(pieceCopy)) {
          pieceCopy = piece.makeCopy();
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
