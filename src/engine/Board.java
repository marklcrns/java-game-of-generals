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
  private boolean gameInitialized = false;
  private boolean gameStarted = false;
  private static boolean debugMode;
  private int currentTurn;
  private int lastExecutedTurn;
  private Move lastMove;
  private Alliance moveMaker;
  private Alliance endGameWinner;

  public Board() {
    initBoard();
  }

  public Board(final BoardBuilder builder) {
    this.buildBoard(builder);
  }

  public Board(final Player playerBlack, final Player playerWhite) {
    this.playerBlack = playerBlack;
    this.playerWhite = playerWhite;
    initBoard();
  }

  private void initBoard() {
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

  public void emptyBoard() {
    gameBoard = new ArrayList<>();
    // Add new Tiles in board
    for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
      // set territory
      if (i < BoardUtils.ALL_TILES_COUNT / 2)
        this.addTile(i, Alliance.BLACK , false);
      else
        this.addTile(i, Alliance.WHITE , false);
    }
  }

  public void buildBoard(BoardBuilder builder) {
    this.emptyBoard();

    for (Map.Entry<Integer, Piece> entry : builder.boardConfig.entrySet()) {
      if (gameBoard.get(entry.getKey()).isTileEmpty()) {
        // insert piece to tile
        gameBoard.get(entry.getKey()).insert(entry.getValue());
        // change tile state
        // TODO: make setOccupied(true) built into the insertPiece method
        gameBoard.get(entry.getKey()).setOccupied(true);
      }
    };

    blackPiecesLeft = builder.getBlackPiecesCount();
    whitePiecesLeft = builder.getWhitePiecesCount();
  }

  public boolean initGame() {
    // TODO throw appropriate exception
    if (playerBlack != null) {
      playerBlack.initPlayer();
    } else {
      System.out.println("E: BLACK player has not been assigned");
      return false;
    }

    if (playerWhite != null) {
      playerWhite.initPlayer();
    } else {
      System.out.println("E: WHITE player has not been assigned");
      return false;
    }

    this.gameInitialized = true;
    setMoveMaker(playerWhite);

    displayBoard();

    return true;
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

    if (isDebugMode()) {
      System.out.println(this);
      System.out.println("CurrentTurn: " + currentTurn + "\n");
    }
  }

  public boolean isGameInitialized() {
    return gameInitialized;
  }

  public boolean isGameStarted() {
    return gameStarted;
  }

  public void displayBoard() {
    new BoardPanel(this);
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

  public int getLastExecutedTurn() {
    return this.lastExecutedTurn;
  }

  public int getCurrentTurn() {
    return this.currentTurn;
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
    playerBlack = player;
  }

  public void addPlayerWhite(Player player) {
    playerWhite = player;
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
      BoardBuilder builder = new BoardBuilder();
      int[] row = {0, 8, 17, 26};
      // Black territory
      int boardOffset = 0;
      // row 0
      builder.setPiece(new GeneralTwo(playerBlack, Alliance.BLACK, boardOffset + row[0] + 9));
      builder.setPiece(new Major(playerBlack, Alliance.BLACK, boardOffset + row[0] + 8));
      builder.setPiece(new Private(playerBlack, Alliance.BLACK, boardOffset + row[0] + 7));
      builder.setPiece(new Sergeant(playerBlack, Alliance.BLACK, boardOffset + row[0] + 6));
      builder.setPiece(new LtOne(playerBlack, Alliance.BLACK, boardOffset + row[0] + 5));
      builder.setPiece(new Private(playerBlack, Alliance.BLACK, boardOffset + row[0] + 4));
      builder.setPiece(new Flag(playerBlack, Alliance.BLACK, boardOffset + row[0] + 3));
      builder.setPiece(new LtTwo(playerBlack, Alliance.BLACK, boardOffset + row[0] + 2));
      builder.setPiece(new Private(playerBlack, Alliance.BLACK, boardOffset + row[0] + 1));
      // row 1
      builder.setPiece(new Spy(playerBlack, Alliance.BLACK, boardOffset + row[1] + 8));
      builder.setPiece(new Private(playerBlack, Alliance.BLACK, boardOffset + row[1] + 7));
      builder.setPiece(new Captain(playerBlack, Alliance.BLACK, boardOffset + row[1] + 5));
      builder.setPiece(new Spy(playerBlack, Alliance.BLACK, boardOffset + row[1] + 4));
      builder.setPiece(new Colonel(playerBlack, Alliance.BLACK, boardOffset + row[1] + 3));
      builder.setPiece(new Private(playerBlack, Alliance.BLACK, boardOffset + row[1] + 2));
      builder.setPiece(new LtCol(playerBlack, Alliance.BLACK, boardOffset + row[1] + 1));
      // row 2
      builder.setPiece(new GeneralThree(playerBlack, Alliance.BLACK, boardOffset + row[2] + 9));
      builder.setPiece(new Private(playerBlack, Alliance.BLACK, boardOffset + row[2] + 6));
      builder.setPiece(new GeneralFour(playerBlack, Alliance.BLACK, boardOffset + row[2] + 5));
      // row 3
      builder.setPiece(new GeneralOne(playerBlack, Alliance.BLACK, boardOffset + row[3] + 3));
      builder.setPiece(new GeneralFive(playerBlack, Alliance.BLACK, boardOffset + row[3] + 2));
      builder.setPiece(new GeneralFive(playerWhite, Alliance.WHITE, boardOffset + row[3] + 1));

      // White territory
      boardOffset = BoardUtils.ALL_TILES_COUNT / 2;

      // row 0
      builder.setPiece(new GeneralFive(playerWhite, Alliance.BLACK, boardOffset + row[0] + 1));
      builder.setPiece(new GeneralFive(playerWhite, Alliance.WHITE, boardOffset + row[0] + 2));
      builder.setPiece(new GeneralOne(playerWhite, Alliance.WHITE, boardOffset + row[0] + 3));
      // row 1
      builder.setPiece(new GeneralFour(playerWhite, Alliance.WHITE, boardOffset + row[1] + 5));
      builder.setPiece(new Private(playerWhite, Alliance.WHITE, boardOffset + row[1] + 6));
      builder.setPiece(new GeneralThree(playerWhite, Alliance.WHITE, boardOffset + row[1] + 9));
      // row 2
      builder.setPiece(new LtCol(playerWhite, Alliance.WHITE, boardOffset + row[2] + 1));
      builder.setPiece(new Private(playerWhite, Alliance.WHITE, boardOffset + row[2] + 2));
      builder.setPiece(new Colonel(playerWhite, Alliance.WHITE, boardOffset + row[2] + 3));
      builder.setPiece(new Spy(playerWhite, Alliance.WHITE, boardOffset + row[2] + 4));
      builder.setPiece(new Captain(playerWhite, Alliance.WHITE, boardOffset + row[2] + 5));
      builder.setPiece(new Private(playerWhite, Alliance.WHITE, boardOffset + row[2] + 7));
      builder.setPiece(new Spy(playerWhite, Alliance.WHITE, boardOffset + row[2] + 8));
      // row 3
      builder.setPiece(new Private(playerWhite, Alliance.WHITE, boardOffset + row[3] + 1));
      builder.setPiece(new LtTwo(playerWhite, Alliance.WHITE, boardOffset + row[3] + 2));
      builder.setPiece(new Flag(playerWhite, Alliance.WHITE, boardOffset + row[3] + 3));
      builder.setPiece(new Private(playerWhite, Alliance.WHITE, boardOffset + row[3] + 4));
      builder.setPiece(new LtOne(playerWhite, Alliance.WHITE, boardOffset + row[3] + 5));
      builder.setPiece(new Sergeant(playerWhite, Alliance.WHITE, boardOffset + row[3] + 6));
      builder.setPiece(new Private(playerWhite, Alliance.WHITE, boardOffset + row[3] + 7));
      builder.setPiece(new Major(playerWhite, Alliance.WHITE, boardOffset + row[3] + 8));
      builder.setPiece(new Major(playerWhite, Alliance.WHITE, boardOffset + row[3] + 9));
      return builder;
    }

    public BoardBuilder createRandomBuild() {
      BoardBuilder builder = new BoardBuilder();
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

        setAllPieceInstanceRandomly(builder, unsetPiece, blackTerritoryBounds[0],
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
        if (debugMode)
          System.out.println("Inserting " + unsetPiece.getPieceAlliance() + " " +
                             unsetPiece.getRank() + "...");

        setAllPieceInstanceRandomly(builder, unsetPiece, whiteTerritoryBounds[0],
                                    whiteTerritoryBounds[1], occupiedTiles);
      }

      return builder;
    }

    public boolean setPiece(final Piece piece) {
      // checks if within bounds, correct territory, and piece legal count
      if (isPieceWithinBounds(piece) &&
          isPieceInCorrectTerritory(piece) &&
          isLegalPieceInstanceChecker(piece) &&
          isTileEmpty(piece.getPieceCoords())) {
        boardConfig.put(piece.getPieceCoords(), piece);

        if (isDebugMode())
          System.out.println(piece.getPieceAlliance() + " piece inserted at " +
                             piece.getPieceCoords());

        if (piece.getPieceAlliance() == Alliance.BLACK)
          this.blackPiecesCount++;
        else
          this.whitePiecesCount++;

        return true;
      }
      return false;
    }

    public void setAllPieceInstanceRandomly(BoardBuilder builder, Piece piece,
                                            int from, int to,
                                            int[] occupiedTiles) {
      if (isDebugMode())
        System.out.println("Placing " + piece.getPieceAlliance() + " " +
            piece.getRank() + " at " + piece.getPieceCoords() + " randomly...");

      Piece pieceCopy = piece.makeCopy();
      int pieceInstanceCounter = countPieceInstances(piece.getRank(),
                                                     piece.getPieceAlliance());
      int randomEmptyTile;

      while (pieceInstanceCounter < piece.getLegalPieceInstanceCount()) {
        randomEmptyTile = Utils.getRandomWithExclusion(new Random(), from, to, occupiedTiles);
        pieceCopy.setPieceCoords(randomEmptyTile);
        if (builder.setPiece(pieceCopy)) {
          pieceCopy = piece.makeCopy();
          Utils.appendToIntArray(occupiedTiles, randomEmptyTile);
          pieceInstanceCounter++;

          if (isDebugMode())
            System.out.println(piece.getPieceAlliance() + " " + piece.getRank() +
                " at " + piece.getPieceCoords() + " inserted");
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

    public void setOccupied(boolean occupied) {
      this.occupied = occupied;
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
