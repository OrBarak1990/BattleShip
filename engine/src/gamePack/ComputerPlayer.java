package gamePack;

import java.io.Serializable;
import java.util.List;

public class ComputerPlayer implements Serializable{
    private char[][] trackingBoard, subMarinBoard;
    private static final char EMPTY_SQUARE  = ' ', NO_ACCESS = '-',
            HIT_SQUARE = 'X', MISSED_SQUARE = 'O', MINE_SQUARE = '@', SUBMARINE_SQUARE = '#';
    private Cell strikeStart, strikeEnd,
            futureMinePlacing, lastAttempt, rivalHit;
    private static final int MAX_MINES = 2, EMPTY = -1;
    private int currentNumOfMines = 0, boardSize;


    public ComputerPlayer(char[][] subMarinBoard, char[][] trackingBoard, int boardSize ){
        this.boardSize = boardSize ;
        this.subMarinBoard = new char[boardSize][boardSize];
        this.trackingBoard = new char[boardSize][boardSize];
        copyArray(subMarinBoard, this.subMarinBoard);
        copyArray(trackingBoard, this.trackingBoard);
        strikeStart = new Cell(EMPTY , EMPTY);
        strikeEnd = new Cell(EMPTY , EMPTY);
        futureMinePlacing = new Cell(EMPTY , EMPTY);
        lastAttempt = new Cell(EMPTY , EMPTY);
        rivalHit = new Cell(EMPTY , EMPTY);
    }

    public Game.DesiredMove getDesiredMove() {
        Game.DesiredMove result;
        if(this.placingMine()) {
            result = Game.DesiredMove.ADD_MINE;
        }
        else {
            result = Game.DesiredMove.ATTACK;
        }
        return result;
    }

    public Cell getMineSquareToAdd(){
        currentNumOfMines++;
        subMarinBoard[futureMinePlacing.getX()][futureMinePlacing.getY()] = MINE_SQUARE;
        lastAttempt.deepCopy(futureMinePlacing);
        return new Cell(futureMinePlacing.getX(), futureMinePlacing.getY());
    }

    public Cell getSquareToAttack() {
        this.setEmptySquare(lastAttempt);
        if(!this.isEmptySquare(strikeStart) && !this.isEmptySquare(strikeEnd)){
            if(strikeEnd.getX() == strikeStart.getX())
                this.attackByRowStrike();
            else
                this.attackByColumnStrike();
        }
        else if(!this.isEmptySquare(strikeStart)){
            this.attackAroundStart();
        }
        if(this.isEmptySquare(lastAttempt))
            this.attackByRandom();
        return new Cell(lastAttempt.getX(), lastAttempt.getY());
    }

    public void update(List<Report> theReport){
        for(Report report: theReport){
            if(report.isComputerMove())
                informAttackingPlayer(report.getResult());
            else
                informDefendingPlayer(report.getResult(), report.getTarget());
        }
    }

    private void informAttackingPlayer(ApiGame.Result update) {
        switch (update){
            case MISSED:
                trackingBoard[lastAttempt.getX()][lastAttempt.getY()] = MISSED_SQUARE;
                break;
            case HIT:
                handleHit();
                break;
            case SUNK:
                handleSunk();
                break;
            case MINE:
                trackingBoard[lastAttempt.getX()][lastAttempt.getY()] = NO_ACCESS;
                break;
            case BEEN_ATTACKED:
                trackingBoard[lastAttempt.getX()][lastAttempt.getY()] = NO_ACCESS;
                break;
        }
    }

    private void informDefendingPlayer(ApiGame.Result update, Cell cell) {
        switch (update) {
            case MISSED:
                this.subMarinBoard[cell.getX()][cell.getX()] = MISSED_SQUARE;
                break;
            case HIT:
                this.subMarinBoard[cell.getX()][cell.getX()] = HIT_SQUARE;
                rivalHit.deepCopy(cell);
                break;
            case SUNK:
                this.subMarinBoard[cell.getX()][cell.getX()] = HIT_SQUARE;
                break;
        }
    }

    private void copyArray(char[][] src, char[][] dst) {
        for(int i = 0; i < src.length;++i){
            for(int j = 0; j < src[i].length; ++j){
                dst[i][j] = src[i][j];
            }
        }
    }

    private boolean placingMine() {
        boolean placedMine= false;
        this.setEmptySquare(futureMinePlacing);
        if(!this.isEmptySquare(rivalHit) && currentNumOfMines < MAX_MINES){
            this.setEmptySquare(futureMinePlacing);
            futureMinePlacing = this.availableMinePlacing(rivalHit);
            this.setEmptySquare(rivalHit);
        }
        if(!this.isEmptySquare(futureMinePlacing))
            placedMine = true;
        return placedMine;
    }

    private boolean isEmptySquare(Cell cell) {
        return (cell.getX() == EMPTY);
    }

    private void setEmptySquare(Cell cell){
        cell.setX(EMPTY);
        cell.setY(EMPTY);
    }

    private Cell availableMinePlacing(Cell reference){
        int currentY = reference.getY(), currentX = reference.getX();
        Cell cell = new Cell(EMPTY, EMPTY);
        if(standInRules(currentX - 2, currentY)){
            cell.setX(currentX - 2);
            cell.setY(currentY);
        }
        else  if(standInRules(currentX + 2, currentY)){
            cell.setX(currentX + 2);
            cell.setY(currentY);
        }
        else  if(standInRules(currentX, currentY - 2)){
            cell.setX(currentX);
            cell.setY(currentY - 2);
        }
        else  if(standInRules(currentX, currentY + 2)){
            cell.setX(currentX);
            cell.setY(currentY + 2);
        }
        return cell;
    }

    private boolean squareIsClear(int x, int y) {
        return (x >= 0 && x < boardSize && y >= 0 && y < boardSize);
    }

    private boolean standInRules(int x, int y){
        boolean stand = true;
        for(int currentX = x-1; currentX < x + 1 && stand; ++currentX){
            for(int currentY = y-1; currentY < y + 1; ++currentY) {
                if(!(squareIsClear(x, y) && subMarinBoard[x][y] != SUBMARINE_SQUARE &&
                        subMarinBoard[x][y] != HIT_SQUARE)){
                    stand = false;
                    break;
                }
            }
        }
        return stand;
    }

    private void attackAroundStart() {
        boolean empty = false;
        int startY = strikeStart.getY(), startX = strikeStart.getX();
        if(startY != 0)
            startY--;
        if(startX != 0)
            startX--;
        for(int x = startX; x <= strikeStart.getX() + 1 && x < this.boardSize && !empty; ++x){
            for(int y = startY; y <= strikeStart.getY() + 1 && y < this.boardSize; ++y) {
                if(trackingBoard[x][y] == EMPTY_SQUARE &&
                        (x == strikeStart.getX() || y == strikeStart.getY())){
                    empty = true;
                    lastAttempt.setX(x);
                    lastAttempt.setY(y);
                    break;
                }
            }
        }
    }

    private void attackByRandom() {
        boolean found = false;
        for (int x = 0; x < boardSize && !found; ++x){
            for (int y = 0; y < boardSize; ++y) {
                if(trackingBoard[x][y] == EMPTY_SQUARE){
                    lastAttempt.setY(y);
                    lastAttempt.setX(x);
                    found = true;
                    break;
                }
            }
        }
    }

    private void attackByColumnStrike() {
        int x, y;
        y = strikeEnd.getY();
        if(strikeEnd.getX() + 1 < boardSize &&
                trackingBoard[strikeEnd.getX() + 1][y] == EMPTY_SQUARE){
            x = strikeEnd.getX() + 1;
            lastAttempt.setX(x);
            lastAttempt.setY(y);
        }
        else if(strikeStart.getX() - 1 < boardSize &&
                trackingBoard[strikeStart.getX()][y] == EMPTY_SQUARE){
            x = strikeStart.getX() - 1;
            lastAttempt.setX(x);
            lastAttempt.setY(y);
        }
    }

    private void attackByRowStrike() {
        int x, y;
        x = strikeEnd.getX();
        if(strikeEnd.getY() + 1 < boardSize &&
                trackingBoard[x][strikeEnd.getY() + 1] == EMPTY_SQUARE){
            y = strikeEnd.getY() + 1;
            lastAttempt.setX(x);
            lastAttempt.setY(y);
        }
        else if(strikeStart.getY() - 1 < boardSize &&
                trackingBoard[x][strikeStart.getY() - 1] == EMPTY_SQUARE){
            y = strikeStart.getY() - 1;
            lastAttempt.setX(x);
            lastAttempt.setY(y);
        }
    }

    private void handleHit() {
        trackingBoard[lastAttempt.getX()][lastAttempt.getY()] = HIT_SQUARE;
        if(this.isEmptySquare(strikeStart))
            strikeStart.deepCopy(lastAttempt);
        else if(isEmptySquare(strikeEnd)) {
            strikeEnd.deepCopy(lastAttempt);
        }
        if (!isEmptySquare(strikeEnd) && !isEmptySquare(strikeStart))
            handleStrikeHit();
    }

    private void handleStrikeHit() {
        if(!strikeEnd.equals(lastAttempt) && !strikeEnd.equals(lastAttempt))
            this.adjustLastAttemptToStrike();
        else
            this.adjustStartEndSquares();
        if(strikeEnd.getY() == strikeStart.getY())
            this.paddingLeftAndRight();
        else
            this.paddingUpAndDown();
    }

    private void paddingUpAndDown() {
        int x = strikeStart.getX();
        for(int y = strikeStart.getY(); y <= strikeEnd.getY(); ++y) {
            if (x + 1 < boardSize && trackingBoard[x + 1][y] != NO_ACCESS)
                trackingBoard[x + 1][y] = NO_ACCESS;
            if (x - 1 >= 0 && trackingBoard[x - 1][y] != NO_ACCESS)
                trackingBoard[x - 1][y] = NO_ACCESS;
        }
    }

    private void paddingLeftAndRight() {
        int y = strikeStart.getY();
        for (int x = strikeStart.getX(); x <= strikeEnd.getX(); ++x) {
            if (y + 1 < boardSize && trackingBoard[x][y + 1] != NO_ACCESS)
                trackingBoard[x][y + 1] = NO_ACCESS;
            if (y - 1 >= 0 && trackingBoard[x][y - 1] != NO_ACCESS)
                trackingBoard[x][y - 1] = NO_ACCESS;
        }
    }

    private void adjustStartEndSquares() {
        if(strikeEnd.getY() == strikeStart.getY()){
            if(strikeEnd.getX() < strikeStart.getX()){
                this.switchXCoordinate(strikeEnd, strikeStart);
            }
        }
        else{
            if(strikeEnd.getY() < strikeStart.getY()){
                this.switchYCoordinate(strikeEnd, strikeStart);
            }
        }
    }

    private void adjustLastAttemptToStrike() {
        if(strikeEnd.getY() == strikeStart.getY()){
            if(lastAttempt.getX() < strikeStart.getX()){
                this.switchXCoordinate(lastAttempt, strikeStart);
            }

            else if(lastAttempt.getX() > strikeEnd.getX()){
                this.switchXCoordinate(lastAttempt, strikeEnd);
            }
        }
        else {
            if(lastAttempt.getY() < strikeStart.getY()){
                this.switchYCoordinate(lastAttempt, strikeStart);
            }

            else if(lastAttempt.getY() > strikeStart.getY()){
                this.switchYCoordinate(lastAttempt,strikeEnd);
            }
        }

    }

    private void switchXCoordinate(Cell one, Cell two){
        int x = one.getX();
        one.setX(two.getX());
        two.setX(x);
    }

    private void switchYCoordinate(Cell one, Cell two){
        int y = one.getY();
        one.setY(two.getY());
        two.setY(y);
    }

    private void handleSunk() {
        this.handleHit();
        this.paddingAllSides();
        this.setEmptySquare(strikeEnd);
        this.setEmptySquare(strikeStart);
    }

    private void paddingAllSides() {
        if(this.isEmptySquare(strikeEnd))
            this.paddingSquare();
        else
            paddingSides();
    }

    private void paddingSquare() {
        int x = lastAttempt.getX() - 1, y = lastAttempt.getY() - 1;
        for(; x < lastAttempt.getX() + 1 && x < boardSize; ++x){
            for(; y < lastAttempt.getY() + 1 && y < boardSize; ++y) {
                if((y != lastAttempt.getY() || x != lastAttempt.getX()) &&
                        y >= 0 && x >= 0)
                    trackingBoard[x][y] = NO_ACCESS;
            }
        }
    }

    private void paddingSides(){
        if(strikeStart.getY() == strikeEnd.getY()){
            if(strikeEnd.getX() + 1 < boardSize)
                trackingBoard[strikeEnd.getX() + 1][strikeEnd.getY()] = NO_ACCESS;
            if(strikeStart.getX() - 1 >= 0)
                trackingBoard[strikeStart.getX() - 1][strikeStart.getY()] = NO_ACCESS;
        }
        else{
            if(strikeEnd.getY() + 1 < boardSize)
                trackingBoard[strikeEnd.getX()][strikeEnd.getY() + 1] = NO_ACCESS;
            if(strikeStart.getY() - 1 >= 0)
                trackingBoard[strikeStart.getX()][strikeStart.getY() - 1] = NO_ACCESS;
        }
    }
}
