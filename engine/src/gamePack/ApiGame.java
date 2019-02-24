package gamePack;

import javafx.beans.property.StringProperty;
import java.io.Serializable;
import java.util.List;

public interface ApiGame extends Serializable {
    public PlayerTurn.Turn getTurn();
    public List<Report> start();
    public int getHitNumber(PlayerTurn.Turn player);
    public int getScoreNumber(PlayerTurn.Turn player);
    public int getTurnsNumber();
    public String getElapsedTime();
    public int getMissedNumber(PlayerTurn.Turn currentPlayer);
    public String getAverageTimePerAttack(PlayerTurn.Turn player);
    public List<Cell> getShip(PlayerTurn.Turn player, Cell target);

//    boolean rivalLastMoveIsMine(PlayerTurn.Turn player);

    public enum Result {MISSED, HIT, SUNK, MINE, BEEN_ATTACKED, WIN};
    public enum DesiredMove{ADD_MINE, ATTACK};
    public void initialize(PlayerTurn compPlayer);
    public boolean isGameStarted();
    public int getBoardSize();
    public void startClock();
    public void endClock();
    public char[][] getTrackingBoard(PlayerTurn.Turn player);
    public char[][] getSubMarinBoard(PlayerTurn.Turn player);
    public List<Report> attack(Cell cell)throws WrongInputException;
    public List<Report> addMine(Cell cell)throws InvalidSquarePositionException, TooManyMinesException;
    public boolean isHaveWorkingFile();
    public void setHaveWorkingFile(boolean haveWorkingFile);
    public boolean isInitializedGame();
    public void setInitializedGame(boolean initializedGame);
    public void initializeGame(Player one, Player two, int boardSize);
    public int getAvailableAmountOfMines(PlayerTurn.Turn player);
    public List<SubTracker> getTracker(PlayerTurn.Turn player);
    public StringProperty elapsedTimeProperty();
    public void playerQuit();
    public boolean isPlayerQuit();
    public PlayerTurn getWinPlayer();
    public boolean haveWinner();
    public boolean isFirstMoveDone();
}
