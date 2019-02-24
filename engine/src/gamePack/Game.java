package gamePack;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Game implements Serializable, ApiGame{
    private PlayerTurn currentTurn, computerTurn = null, winPlayer = null;
    private Player one, two;
    private ComputerPlayer computer = null;
    private int boardSize, numberOfTurnsFromBeginning;
    private long startPlayingTime;
    private boolean haveWorkingFile = false, initializedGame = false;
    private StringProperty elapsedTime = new SimpleStringProperty("");
    private static final String PLAYING_TIME = "Playing time ";
    private Timer fromStart;
    private boolean gameStarted;
    private boolean winByQuit = false;
    private boolean hadMove = false;
//    private boolean playerOneAddedMine = false, playerTwoAddedMine = false;

    @Override
    public boolean isGameStarted(){return gameStarted;}

    @Override
    public PlayerTurn getWinPlayer() {
        return winPlayer;
    }

    @Override
    public boolean haveWinner() {
        if(winPlayer != null)
            return true;
        else
            return false;
    }

    @Override
    public boolean isFirstMoveDone() {
        return hadMove;
    }


    private void startTicking(){
        /*fromStart = new Timer(1000, e -> elapsedTime.setValue(PLAYING_TIME + this.getElapsedTime()));
        fromStart.start();*/
    }

    @Override
    public void initializeGame(Player one, Player two, int boardSize){
        this.one = one;
        this.two = two;
        this.boardSize = boardSize;
        haveWorkingFile = true;
    }

    @Override
    public StringProperty elapsedTimeProperty() {
        return elapsedTime;
    }

    @Override
    public void playerQuit() {
        winByQuit = true;
        if(gameStarted) {
            if (currentTurn.getTurn() == PlayerTurn.Turn.ONE)
                winPlayer = new PlayerTurn(PlayerTurn.Turn.TWO);
            else
                winPlayer = new PlayerTurn(PlayerTurn.Turn.ONE);
        }else{
            winPlayer = new PlayerTurn(PlayerTurn.Turn.TWO);
        }
    }

    @Override
    public boolean isPlayerQuit() {
        return winByQuit;
    }

    @Override
    public int getAvailableAmountOfMines(PlayerTurn.Turn player){
        if(player == PlayerTurn.Turn.ONE)
            return one.getAvailableAmountOfMines();
        else
            return two.getAvailableAmountOfMines();
    }

    @Override
    public boolean isHaveWorkingFile() {
        return haveWorkingFile;
    }

    @Override
    public void setHaveWorkingFile(boolean haveWorkingFile) {
        this.haveWorkingFile = haveWorkingFile;
    }

    @Override
    public boolean isInitializedGame() {
        return initializedGame;
    }

    @Override
    public void setInitializedGame(boolean initializedGame) {
        this.initializedGame = initializedGame;
    }

    @Override
    public char[][] getTrackingBoard(PlayerTurn.Turn player) {
        if(player == PlayerTurn.Turn.ONE)
            return one.getTrackingBoard();
        else
            return two.getTrackingBoard();
    }

    @Override
    public char[][] getSubMarinBoard(PlayerTurn.Turn player) {
        if(player == PlayerTurn.Turn.ONE)
            return one.getSubMarinBoardBoard();
        else
            return two.getSubMarinBoardBoard();
    }

    @Override
    public int getBoardSize() {
        return boardSize;
    }

    @Override
    public void startClock(){
        if (currentTurn.getTurn() == PlayerTurn.Turn.ONE)
            one.startClock();
        else
            two.startClock();
    }

    @Override
    public void endClock(){
        this.numberOfTurnsFromBeginning++;
        if(currentTurn.getTurn() == PlayerTurn.Turn.ONE)
            one.endClock();
        else
            two.endClock();
    }

    @Override
    public int getHitNumber(PlayerTurn.Turn player){
        int hitNumber;
        if(player == PlayerTurn.Turn.ONE)
            hitNumber = one.getHitNumber();
        else
            hitNumber = two.getHitNumber();
        return hitNumber;
    }

    @Override
    public int getScoreNumber(PlayerTurn.Turn player){
        int score;
        if(player == PlayerTurn.Turn.ONE)
            score = one.getScore();
        else
            score = two.getScore();
        return score;
    }

    @Override
    public int getTurnsNumber(){
        return this.numberOfTurnsFromBeginning;
    }

    @Override
    public String getElapsedTime(){
        long pointTime = System.nanoTime();
        if(startPlayingTime == 0){
            return "00:00";
        }
        long elapsedTime = pointTime - startPlayingTime;
        Long minutes = TimeUnit.MINUTES.convert(elapsedTime, TimeUnit.NANOSECONDS);
        Long seconds = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
        seconds -= minutes * 60;
        return timeFormat(minutes, seconds);
    }

    @Override
    public int getMissedNumber(PlayerTurn.Turn player) {
        int missedNumber;
        if(player == PlayerTurn.Turn.ONE)
            missedNumber = one.getMissedNumber();
        else
            missedNumber = two.getMissedNumber();
        return missedNumber;
    }

    @Override
    public String getAverageTimePerAttack(PlayerTurn.Turn player) {
        String average;
        if(player == PlayerTurn.Turn.ONE)
            average = one.AverageTimePerAttack();
        else
            average = two.AverageTimePerAttack();
        return average;
    }

    @Override
    public List<Cell> getShip(PlayerTurn.Turn player, Cell target) {
        if (player == PlayerTurn.Turn.ONE)
            return two.getShip(target);
        else
            return one.getShip(target);
    }

//    @Override
//    public boolean rivalLastMoveIsMine(PlayerTurn.Turn player) {
//        if(playerOneAddedMine && player == PlayerTurn.Turn.TWO){
//            playerOneAddedMine = false;
//            return true;
//        }else if(playerTwoAddedMine && player == PlayerTurn.Turn.ONE){
//            playerTwoAddedMine = false;
//            return true;
//        }
//        return false;
//    }

    @Override
    public void initialize(PlayerTurn compPlayer){
        gameStarted = true;
        numberOfTurnsFromBeginning = 0;
        startPlayingTime = System.nanoTime();
        this.startTicking();
        currentTurn = new PlayerTurn(PlayerTurn.Turn.ONE);
        if(compPlayer != null)
            this.initializeComputerMode(compPlayer);
    }

    @Override
    public List<SubTracker> getTracker(PlayerTurn.Turn player){
        if(player == PlayerTurn.Turn.ONE)
            return one.getTrackers();
        else
            return  two.getTrackers();
    }

    @Override
    public List<Report> start(){
        List<Report> theReport = new ArrayList<>();
        if(computerTurn != null && currentTurn.equals(computerTurn)) {
            this.startClock();
            this.aiMove(theReport);
            updateWinnerResult(theReport);
        }
        return theReport;
    }

    @Override
    public PlayerTurn.Turn getTurn(){return  currentTurn.getTurn();}

    @Override
    public List<Report> addMine(Cell cell)throws InvalidSquarePositionException, TooManyMinesException {
        hadMove = true;
        if(currentTurn.getTurn() == PlayerTurn.Turn.ONE) {
            one.addMine(cell);
//            playerOneAddedMine = true;
        }else {
            two.addMine(cell);
//            playerTwoAddedMine = true;
        }
        List<Report> theReport = new ArrayList<>();
        currentTurn.updateTurn();
        if( computerTurn != null && currentTurn.equals(computerTurn))
            this.aiMove(theReport);
        updateWinnerResult(theReport);
        return theReport;
    }

    @Override
    public List<Report> attack(Cell cell)throws WrongInputException {
        hadMove = true;
        if(cell.getX() < 0 || cell.getX() >= this.getBoardSize())
            throw new WrongInputException("this row does not exist in board");
        else if(cell.getY() < 0 || cell.getY() >= this.getBoardSize())
            throw new WrongInputException("this column does not exist in board");
        List<Report> theReport = new ArrayList<>();
        if(currentTurn.getTurn() == PlayerTurn.Turn.ONE)
            makeAttackMove(one, two, cell, theReport);
        else
            makeAttackMove(two, one, cell, theReport);
        if (theReport.get(0).getResult() == Result.MISSED || theReport.get(0).getResult() == Result.MINE)
            currentTurn.updateTurn();
        if (computerTurn(theReport.get(0).getResult()))
            this.aiMove(theReport);
        updateWinnerResult(theReport);

        return theReport;
    }

    private void updateWinnerResult(List<Report> theReport) {
        boolean computerTurn;
        if(one.allSubMarineHadSunk()) {
            computerTurn = this.computerTurn != null && this.computerTurn.getTurn() == PlayerTurn.Turn.ONE;
            theReport.add(new Report(computerTurn, Result.WIN, PlayerTurn.Turn.TWO));
            winPlayer = new PlayerTurn(PlayerTurn.Turn.TWO);
        }
        else if(two.allSubMarineHadSunk()) {
            computerTurn = this.computerTurn != null && this.computerTurn.getTurn() == PlayerTurn.Turn.TWO;
            theReport.add(new Report(computerTurn, Result.WIN, PlayerTurn.Turn.ONE));
            winPlayer = new PlayerTurn(PlayerTurn.Turn.ONE);
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

    private void makeAttackMove(Player attackingPlayer, Player defendingPlayer, Cell cell, List<Report> theReport) {
        Result result = defendingPlayer.attacked(cell);
        boolean computerPlay = computerTurn != null && currentTurn.equals(computerTurn);
        theReport.add(new Report(cell, computerPlay, result, currentTurn.getTurn()));
        if (result == Result.MINE) {
            Result mineResult = makeMineAttackMove(defendingPlayer, attackingPlayer, cell);
            computerPlay = (computerTurn != null) && !computerPlay;
            PlayerTurn mineAttack = currentTurn.getTurn() == PlayerTurn.Turn.ONE?
                    new PlayerTurn(PlayerTurn.Turn.TWO): new PlayerTurn(PlayerTurn.Turn.ONE);
            theReport.add(new Report(cell, computerPlay, mineResult, mineAttack.getTurn()));
        } else {
            updateSunkScore(result, defendingPlayer, attackingPlayer);
            attackingPlayer.updateAttackResult(cell, result);
        }
    }

    private boolean computerTurn(Result result) {
        return computerTurn!= null && currentTurn.equals(computerTurn) && (result == Result.MISSED || result == Result.MINE);
    }

    private Result makeMineAttackMove(Player defendingPlayer, Player attackingPlayer, Cell cell) {
        Result result = attackingPlayer.attackedByMine(cell);
        updateSunkScore(result, attackingPlayer, defendingPlayer);
        defendingPlayer.updateAttackByMineResult(cell, result);
        return result;
    }

    private void  initializeComputerMode(PlayerTurn compPlayer) {
        computerTurn = new PlayerTurn(compPlayer.getTurn());
        computer = new ComputerPlayer(this.getSubMarinBoard(computerTurn.getTurn()), this.getTrackingBoard(computerTurn.getTurn()), this.boardSize);
    }

    private void aiMove(List<Report> theReport) {
        hadMove = true;
        if (!theReport.isEmpty())
            computer.update(theReport);
        Cell cell;
        DesiredMove desiredMove;
        boolean haveOneMoreTurn;
        do {
            haveOneMoreTurn = false;
            desiredMove = computer.getDesiredMove();
            if (desiredMove == DesiredMove.ADD_MINE) {
                try {
                    cell = computer.getMineSquareToAdd();
                    if(currentTurn.getTurn() == PlayerTurn.Turn.ONE)
                        one.addMine(cell);
                    else
                        two.addMine(cell);
                } catch (InvalidSquarePositionException | TooManyMinesException e) {
                    e.printStackTrace();
                }
            } else if (desiredMove == DesiredMove.ATTACK) {
                cell = computer.getSquareToAttack();
                List<Report> compReport = new ArrayList<>();
                if(computerTurn.getTurn() == PlayerTurn.Turn.ONE)
                    makeAttackMove(one, two, cell, compReport);
                else
                    makeAttackMove(two, one, cell, compReport);
                if(compReport.get(0).getResult() != Result.MINE && compReport.get(0).getResult() != Result.MISSED)
                    haveOneMoreTurn = true;
                computer.update(compReport);
                theReport.addAll(compReport);
            }
        }while (haveOneMoreTurn);
        currentTurn.updateTurn();
    }

    private void updateSunkScore(Result result, Player defendingPlayer, Player attackingPlayer ){
        if(result == Result.SUNK){
            int score = defendingPlayer.getScoreOfDeadShip();
            attackingPlayer.setScore(attackingPlayer.getScore() + score);
        }
    }
}