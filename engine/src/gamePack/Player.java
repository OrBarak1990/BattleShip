package gamePack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Player implements Serializable{
    private List<SubMarine> subMarines;
    private List<SubMarine> deadSubMarines;
    private List<Mine> mines;
    private char[][] trackingBoard, subMarinBoard;
    private int hitNumber = 0, missedNumber = 0, numberOfTurns = 0,
            mySunk = 0, initializedAmountOfMines = 0 ,boardSize, score = 0;
    private final int TOTAL_SUBMARINES, MINE_AMOUNT;
    private static final char EMPTY_SQUARE = ' ', MINE_SQUARE = '@',
            SUBMARINE_SQUARE = '#', HIT_SQUARE = 'X', MISSED_SQUARE = 'O';
    private long clock, averageTimePerAttack;
    private List<SubTracker> trackers;


    public Player(List<SubMarine> subMarines, List<SubTracker> trackers, int boardSize, int mineAmount){
        deadSubMarines = new LinkedList<>();
        this.subMarines = subMarines;
        this.trackers = trackers;
        TOTAL_SUBMARINES = subMarines.size();
        this.initializeBoards(boardSize);
        clock = 0;
        averageTimePerAttack = 0;
        MINE_AMOUNT = mineAmount;
        mines = new ArrayList<>(MINE_AMOUNT);
        this.boardSize = boardSize;
    }

    int getAvailableAmountOfMines(){return MINE_AMOUNT - initializedAmountOfMines;}

    int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    void addMine(Cell cell)throws TooManyMinesException, InvalidSquarePositionException {
        if(cell.getX() < 0 || cell.getX() >= boardSize)
            throw new InvalidSquarePositionException();
        else if(cell.getY() < 0 || cell.getY() >= boardSize)
            throw new InvalidSquarePositionException();
        if(MINE_AMOUNT == initializedAmountOfMines)
            throw  new TooManyMinesException();
        if(!legallyMinePosition(cell))
            throw  new InvalidSquarePositionException();
        mines.add(new Mine(cell));
        initializedAmountOfMines++;
        subMarinBoard[cell.getX()][cell.getY()] = MINE_SQUARE;
    }

    boolean allSubMarineHadSunk(){
        return TOTAL_SUBMARINES == mySunk;
    }

    int getMissedNumber() {
        return missedNumber;
    }

    int getHitNumber() {
        return hitNumber;
    }

    ApiGame.Result attacked(Cell cell){
        int x = cell.getX(), y = cell.getY();
        ApiGame.Result result;
        if(subMarinBoard[x][y] == SUBMARINE_SQUARE) {
            result = subMarineAttacked(cell);
            subMarinBoard[x][y] = HIT_SQUARE;
        }
        else if(subMarinBoard[x][y] == MINE_SQUARE)
        {
            mineAttacked(cell);
            result = ApiGame.Result.MINE;
            subMarinBoard[x][y] = HIT_SQUARE;
        }
        else if(subMarinBoard[x][y] == HIT_SQUARE || subMarinBoard[x][y] == MISSED_SQUARE)
        {
            result = ApiGame.Result.BEEN_ATTACKED;
        }
        else
        {
            result = ApiGame.Result.MISSED;
            subMarinBoard[x][y] = MISSED_SQUARE;
        }
        return result;
    }

    ApiGame.Result attackedByMine(Cell cell) {
        int x = cell.getX(), y = cell.getY();
        hitNumber++;
        ApiGame.Result result;
        if(subMarinBoard[x][y] == MINE_SQUARE){
            mineAttacked(cell);
            subMarinBoard[x][y] = HIT_SQUARE;
            trackingBoard[x][y] = MISSED_SQUARE;
            result = ApiGame.Result.MINE;
        }
        else{
            result = this.attacked(cell);
            trackingBoard[x][y] = HIT_SQUARE;
        }
        return result;
    }

    void updateAttackResult(Cell cell, ApiGame.Result result){
        if(result != ApiGame.Result.BEEN_ATTACKED &&
                result != ApiGame.Result.MISSED) {
            trackingBoard[cell.getX()][cell.getY()] = HIT_SQUARE;
            hitNumber++;
        }
        else if(result == ApiGame.Result.MISSED){
            trackingBoard[cell.getX()][cell.getY()] = MISSED_SQUARE;
            missedNumber++;
        }
    }

    void updateAttackByMineResult(Cell cell, ApiGame.Result result) {
        switch (result) {
            case MISSED:
                trackingBoard[cell.getX()][cell.getY()] = MISSED_SQUARE;
                break;
            case HIT:
                trackingBoard[cell.getX()][cell.getY()] = HIT_SQUARE;
                break;
            case SUNK:
                trackingBoard[cell.getX()][cell.getY()] = HIT_SQUARE;
                break;
            case MINE:
                trackingBoard[cell.getX()][cell.getY()] = HIT_SQUARE;
                subMarinBoard[cell.getX()][cell.getY()] = MISSED_SQUARE;
                break;
        }
    }

    void startClock(){
        clock = System.nanoTime();
        numberOfTurns++;
    }

    void endClock(){
        long pointTime = System.nanoTime();
        long elapsedTime = pointTime - clock;
        averageTimePerAttack = (averageTimePerAttack + elapsedTime)/numberOfTurns;
        clock = 0;
    }

    char[][] getSubMarinBoardBoard() {
        return subMarinBoard.clone();
    }

    char[][] getTrackingBoard() {
        return trackingBoard.clone();
    }

    String AverageTimePerAttack(){
        long minutes = TimeUnit.MINUTES.convert(averageTimePerAttack, TimeUnit.NANOSECONDS);
        long seconds = TimeUnit.SECONDS.convert(averageTimePerAttack, TimeUnit.NANOSECONDS);
        seconds -= minutes * 60;
        return timeFormat(minutes, seconds);
    }

    private ApiGame.Result subMarineAttacked(Cell cell) {
        ApiGame.Result result = ApiGame.Result.MISSED;
        boolean attack;
        for (SubMarine submarine : subMarines) {
            attack = submarine.attacked(cell);
            if (attack){
                result = ApiGame.Result.HIT;
                if (!submarine.isAlive()) {
                    this.updateTracker(submarine);
                    mySunk++;
                    result = ApiGame.Result.SUNK;
                }
                break;
            }
        }
        return result;
    }

    private void updateTracker(SubMarine submarine) {
        for (SubTracker tracker : trackers){
            if(tracker.equals(submarine.getCategory(), submarine.getLength()))
              tracker.decrease();
        }
    }

    private void mineAttacked(Cell cell) {
        boolean result;
        for (Mine mine: mines)
        {
            result = mine.attacked(cell);
            if(result) {
                mines.remove(mine);
                break;
            }
        }
    }

    private boolean legallyMinePosition(Cell cell) {
        boolean legally = true;
        int y = cell.getY(), x = cell.getX();
        if(y != 0)
            y--;
        if(x != 0)
            x--;
        int startX = x, startY = y;
        if(subMarinBoard[cell.getX()][cell.getY()] != EMPTY_SQUARE)
            legally = false;
        for(x =startX; x <= cell.getX() + 1 && x < this.boardSize && legally; ++x){
            for(y = startY; y <= cell.getY() + 1 && y < this.boardSize; ++y) {
                if(subMarinBoard[x][y] == SUBMARINE_SQUARE ||
                        subMarinBoard[x][y] == HIT_SQUARE){
                    legally = false;
                    break;
                }
            }
        }
        return legally;
    }

    private void initializeBoards(int boardSize){
        trackingBoard = new char[boardSize][boardSize];
        subMarinBoard= new char[boardSize][boardSize];
        for(int i = 0; i < boardSize; ++i){
            for(int j = 0; j < boardSize; ++j){
                trackingBoard[i][j] = EMPTY_SQUARE;
                subMarinBoard[i][j] = EMPTY_SQUARE;
            }
        }
        int length;
        SubMarine.Direction direction;
        for(SubMarine submarine: subMarines){
            Cell start = new Cell(submarine.getStart().getX(), submarine.getStart().getY());
            length = submarine.getLength();
            direction = submarine.getDirection();
            List<Cell> ship = fillBoardByDirection(direction, start, length);
            submarine.setShip(ship);
        }
    }

    private List<Cell> fillBoardByDirection(SubMarine.Direction direction, Cell start, int length) {
        List<Cell> ship;
        if(direction == SubMarine.Direction.ROW || direction == SubMarine.Direction.COLUMN)
            ship = new ArrayList<>(length);
        else
            ship = new ArrayList<>(length *2 -1);
        ship.add(new Cell(start.getX(),start.getY()));
        subMarinBoard[start.getX()][start.getY()] = SUBMARINE_SQUARE;
        switch (direction) {
            case ROW:
                fillToRight(ship, length);
                break;
            case COLUMN:
                fillToDown(ship, length);
                break;
            case DOWN_RIGHT:
                fillToUpp(ship, length);
                fillToRight(ship, length);
                break;
            case UP_RIGHT:
                fillToRight(ship, length);
                fillToDown(ship, length);
                break;
            case RIGHT_UP:
                fillToUpp(ship, length);
                fillToLeft(ship, length);
                break;
            case RIGHT_DOWN:
                fillToLeft(ship, length);
                fillToDown(ship, length);
                break;
        }
        return ship;
    }

    private void fillToUpp(List<Cell> ship, int length) {
        Cell travel = new Cell(ship.get(0).getX() - 1, ship.get(0).getY());
        while(--length > 0){
            subMarinBoard[travel.getX()][travel.getY()] = SUBMARINE_SQUARE;
            ship.add(new Cell(travel.getX(), travel.getY()));
            travel.setX(travel.getX() - 1);
        }
    }

    private void fillToLeft(List<Cell> ship, int length) {
        Cell travel = new Cell(ship.get(0).getX(), ship.get(0).getY() - 1);
        while(--length > 0){
            subMarinBoard[travel.getX()][travel.getY()] = SUBMARINE_SQUARE;
            ship.add(new Cell(travel.getX(), travel.getY()));
            travel.setY(travel.getY() - 1);
        }
    }

    private void fillToDown(List<Cell> ship, int length) {
        Cell travel = new Cell(ship.get(0).getX() + 1, ship.get(0).getY());
        while(--length > 0){
            subMarinBoard[travel.getX()][travel.getY()] = SUBMARINE_SQUARE;
            ship.add(new Cell(travel.getX(), travel.getY()));
            travel.setX(travel.getX() + 1);
        }
    }

    private void fillToRight(List<Cell> ship, int length) {
        Cell travel = new Cell(ship.get(0).getX(), ship.get(0).getY() + 1);
        while(--length > 0){
            subMarinBoard[travel.getX()][travel.getY()] = SUBMARINE_SQUARE;
            ship.add(new Cell(travel.getX(), travel.getY()));
            travel.setY(travel.getY() + 1);
        }
    }

    private String timeFormat(Long minutes, Long seconds){
        String minutesRepresentation, secondsRepresentation;
        if(minutes < 10){
            minutesRepresentation = String.format("0%d",minutes);
        }
        else
            minutesRepresentation = String.format("%d",minutes);
        if(seconds < 10)
            secondsRepresentation = String.format("0%d",seconds);
        else
            secondsRepresentation = String.format("%d",seconds);
        return String.format("%s:%s", minutesRepresentation, secondsRepresentation);
    }

    List<SubTracker> getTrackers() {
        return trackers;
    }

    int getScoreOfDeadShip(){
        int score = 0;
        for (SubMarine submarine : subMarines) {
            if (!submarine.isAlive()) {
                score = submarine.getScore();
                subMarines.remove(submarine);
                deadSubMarines.add(submarine);
                break;
            }
        }
        return score;
    }

    List<Cell> getShip(Cell target) {
        for(SubMarine deadSubmarine : deadSubMarines){
            if(deadSubmarine.cellInShip(target))
                return deadSubmarine.getShip();
        }
        return null;
    }
}
