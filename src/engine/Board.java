package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import gui.GUI;
import utils.BoardUtils;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-18
 */
public class Board {

  private static List<Tile> gameBoard;
  private static Player playerBlack;
  private static Player playerWhite;
  private boolean debugMode;
  private int currentTurn;
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
        gameBoard.get(entry.getKey()).insertPiece(entry.getValue());
        // change tile state
        // TODO: make setOccupied(true) built into the insertPiece method
        gameBoard.get(entry.getKey()).setOccupied(true);
      }
    };
  }

  public boolean startGame() {
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

    setMoveMaker(playerWhite);
    this.currentTurn = 1;

    displayBoard();

    if (debugMode) {
      System.out.println(this);
      System.out.println("CurrentTurn: " + currentTurn + "\n");
    }

    return true;
  }

  public void displayBoard() {
    new GUI(this);
  }

  public void setDebugMode(boolean debug) {
    this.debugMode = debug;
  }

  public boolean isDebugMode() {
    return this.debugMode;
  }

  public Tile getTile(int coordinates) {
    return gameBoard.get(coordinates);
  }

  public List<Tile> getBoard() {
    return gameBoard;
  }

  public boolean replacePiece(int tileId, Piece piece) {
    if (this.getTile(tileId).isTileOccupied()) {
      // TODO: improve piece manipulation efficiency
      piece.updateCoords(tileId);
      this.getBoard().get(tileId).replacePiece(piece);
      this.getTile(tileId).replacePiece(piece);

      return true;
    }
    return false;
  }

  public boolean movePiece(int sourceCoords, int targetCoords) {
    // insert copy of source piece into target tile
    if (this.getTile(targetCoords).isTileEmpty()) {
      Piece pieceCopy = this.getTile(sourceCoords).getPiece().makeCopy();
      pieceCopy.updateCoords(targetCoords);
      this.getTile(targetCoords).insertPiece(pieceCopy);
      // delete source piece
      this.getTile(sourceCoords).emptyTile();

      return true;
    }
    return false;
  }

  public boolean deletePiece(int pieceCoords) {
    if (this.getTile(pieceCoords).isTileOccupied()) {
      this.getTile(pieceCoords).emptyTile();

      if (debugMode)
        System.out.println(this);

      return true;
    }
    return false;
  }

  private final void addTile(int tileId, Alliance territory, boolean occupied) {
    gameBoard.add(new Tile(tileId, territory, occupied));
  }

  public void switchPlayerTurn() {
    if (this.getBlackPlayer().isMoveMaker()) {
      setMoveMaker(playerWhite);
    } else {
      setMoveMaker(playerBlack);
    }
    this.currentTurn++;

    if (debugMode) {
      System.out.println(this);
      System.out.println("Current Turn: " + currentTurn + "\n");
    }
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
    for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i += 9) {
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

    public BoardBuilder() {
      this.boardConfig = new HashMap<>();
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

    public void setPiece(final Piece piece) {
      // checks if in the correct territory and piece legal count
      if (isPieceWithinBounds(piece) &&
          isPieceInCorrectTerritory(piece) &&
          isLegalPieceInstanceChecker(piece))
        boardConfig.put(piece.getCoords(), piece);
      // TODO: Throw wrong territory exception here
    }

    public boolean isPieceWithinBounds(Piece piece) {
      if (piece.getCoords() < BoardUtils.ALL_TILES_COUNT &&
          piece.getCoords() > 0) {
        return true;
      }

      System.out.println(piece.getAlliance() + " " +
                         piece.getRank() + " at Tile" +
                         piece.getCoords() + " is out of bounds." +
                         " Piece not inserted.");
      return false;
    }

    public boolean isPieceInCorrectTerritory(Piece piece) {
      if ((piece.getAlliance() == Alliance.BLACK &&
            piece.getCoords() < BoardUtils.ALL_TILES_COUNT / 2) ||
          (piece.getAlliance() == Alliance.WHITE &&
            piece.getCoords() > BoardUtils.ALL_TILES_COUNT / 2))
        return true;

      System.out.println(piece.getAlliance() + " " +
                         piece.getRank() + " at Tile " +
                         piece.getCoords() + " is in illegal territory." +
                         " Piece not inserted.");
      return false;
    }

    public boolean isLegalPieceInstanceChecker(Piece piece) {
      // TODO: NOT WORKING
      final int legalPieceInstanceCount = piece.getLegalPieceInstanceCount();
      int pieceCounterBlack = 0;
      int pieceCounterWhite = 0;
      for (Map.Entry<Integer, Piece> entry : this.boardConfig.entrySet()) {
        if (piece.getRank() == entry.getValue().getRank()) {
          if (piece.getAlliance() == Alliance.BLACK)
            pieceCounterBlack++;
          else
            pieceCounterWhite++;
        }

        if (pieceCounterBlack > legalPieceInstanceCount &&
            pieceCounterWhite > legalPieceInstanceCount) {
          System.out.println(piece.getRank() + " exceeded maximum instance." +
                             " Piece not inserted.");
          return false;
        }
      }
      return true;
    }

  }

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

    public boolean insertPiece(Piece piece) {
      if (isTileEmpty()) {
        this.piece = piece;
        this.occupied = true;
        return true;
      }
      return false;
    }

    public boolean replacePiece(Piece piece) {
      if (isTileOccupied()) {
        this.piece = piece;
        return true;
      }
      return false;
    }

    public boolean emptyTile() {
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
          this.piece.getAlliance() + " " + this.piece.getRank();
      else
        return "Tile " + this.tileId + " is empty";
    }
  }
}
