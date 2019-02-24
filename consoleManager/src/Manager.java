import gamePack.*;
import loader.GameValidationException;
import loader.LoadGame;
import loader.LoadingException;
import loader.PathIsNotXmlFileException;

import java.io.*;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

class Manager implements Serializable {
    private boolean haveSaving = false;
    private ApiGame game = new Game();
    private Cell currentCell;
    private boolean endGame = false;
    private List<String> saves = new LinkedList<>();

    void runGame(){
        Selection selection = null;
        while (!endGame){
            startingClock(selection);
            try {
                selection = this.getSelection();
                this.executeSelection(selection);
            } catch (WrongInputException e) {
                System.out.println(e.getMessage());
                selection = null;
            }
        }
    }

    private Selection getSelection() throws WrongInputException {
        this.showMenu();
        boolean approve;
        Selection theSelection = null;
        do {
            approve = true;
            int choice = 0;
            Scanner scanner = new Scanner(System.in);
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Please type in a valid number");
                approve = false;
                showMenu();
            }
            for (Selection selection : Selection.values()) {
                if (selection.getSelectionValue() == choice)
                    theSelection = selection;
            }
        }while (!approve);
        if(theSelection != Selection.EXIT_GAME)
            this.validationSelection(theSelection);
        return theSelection;
    }

    private void showMenu() {
        System.out.println("Please select on of the above: ");
        System.out.println("1.  load game file");
        System.out.println("11. play against computer");
        System.out.println("2.  start the game");
        System.out.println("3.  show game status");
        System.out.println("4.  play move");
        System.out.println("5.  show statistics");
        System.out.println("6.  end current game");
        System.out.println("7.  add mine");
        System.out.println("8.  exit game");
        System.out.println("9.  save game");
        System.out.println("10. load saving");
    }

    private void validationSelection(Selection theSelection) throws WrongInputException {
        if (theSelection == null)
            throw new WrongInputException("Please enter a number between 1 to 11");
        if (theSelection != Selection.LOAD_GAME && !game.isHaveWorkingFile())
            throw new WrongInputException("Please load a valid file before trying go forward");
        else if (game.isHaveWorkingFile() && !game.isInitializedGame() && theSelection != Selection.START_GAME &&
                theSelection != Selection.LOAD_GAME && theSelection != Selection.PLAY_AGAINST_COMPUTER)
            throw new WrongInputException("Please initialize game by loading a valid game file" +
                    "or press 2 for starting the game");
        else if (game.isHaveWorkingFile() && game.isInitializedGame() && (theSelection == Selection.START_GAME ||
                theSelection == Selection.LOAD_GAME || theSelection == Selection.PLAY_AGAINST_COMPUTER))
            throw new WrongInputException("You already start a game with valid game file");
    }

    private void startingClock(Selection selection) {
        if(game.isInitializedGame()){
            if(selection == Selection.START_GAME || selection == Selection.PLAY_MOVE || selection == Selection.PLAY_AGAINST_COMPUTER)
                game.startClock();
        }
    }

    private void executeSelection(Selection selection){
        switch (selection){
            case LOAD_GAME:
                loadFile();
                break;
            case PLAY_AGAINST_COMPUTER:
                adjustPlayers();
                break;
            case START_GAME:
                startPlay();
                break;
            case GAME_STATUS:
                this.gameStatus();
                break;
            case PLAY_MOVE:
                makeMove();
                break;
            case SHOW_STAT:
                this.showStatistics();
                break;
            case END_CURRENT_GAME:
                finishGame();
                this.clearGame();
                break;
            case ADD_MINE:
                placeMine();
                break;
            case EXIT_GAME:
                if(game.isInitializedGame())
                    finishGame();
                this.endGame = true;
                break;
            case SAVE_GAME:
                saveGame();
                break;
            case LOAD_SAVING:
                LoadGame();
                break;
        }
    }

    private void adjustPlayers() {
        int choice;
        PlayerTurn computerPlayer = null;
        Scanner scanner = new Scanner(System.in);
        System.out.println("pick:\n1.   for player 1 to be the computer\n" +
                "2.   for player 2 to be the computer");
        choice = scanner.nextInt();
        if(choice == 1)
            computerPlayer = new PlayerTurn(PlayerTurn.Turn.ONE);
        else if(choice == 2)
            computerPlayer = new PlayerTurn(PlayerTurn.Turn.TWO);
        this.initializeGame(computerPlayer);
    }

    private void initializeGame(PlayerTurn computerPlayer) {
        currentCell = new Cell(-1, -1);
        game.initialize(computerPlayer);
    }

    private void startPlay(){
        if(!game.isInitializedGame())
            this.initializeGame(null);
        else{
            List<Report> theReport = game.start();
            if(!theReport.isEmpty())
                this.updateResult(theReport);
        }
        game.setInitializedGame(true);
    }

    private void placeMine() {
        Cell mineCell = this.getMineSquareToAdd(game.getBoardSize());
        try {
            List<Report> theReport = game.addMine(mineCell);
            game.endClock();
            System.out.println("You successfully put a mine");
            this.updateResult(theReport);
            game.startClock();
        } catch (InvalidSquarePositionException e) {
            System.out.println("You asked to put mine in invalid square");
        } catch (TooManyMinesException e) {
            System.out.println("You already put the maximum amount of mines in board");
        }
    }

    private void clearGame() {
        game.setHaveWorkingFile(false);
        game.setInitializedGame(false);
        haveSaving = false;
        game = new Game();
    }

    private void attack(){
        try {
            List<Report> theReport = game.attack(currentCell);
            updateResult(theReport);
        }catch(WrongInputException e){
            System.out.println(e.getMessage());
        }
    }

    private void makeMove(){
        this.gameStatus();
        currentCell.deepCopy(this.getSquareToAttack(game.getBoardSize()));
        game.endClock();
        this.attack();
    }

    private void updateResult(List<Report> theReport){
        for(Report report : theReport){
            if(report.getResult() == ApiGame.Result.WIN)
                informWinner(report);
            else if(!report.isComputerMove())
                informAttackingPlayer(report.getResult(), new PlayerTurn(report.getPlayer()));
            else if(report.isComputerMove())
                informDefendingPlayer(report.getResult(), report.getTarget());
        }
        if(game.isInitializedGame())
            this.gameStatus();
    }

    private void showSubMarineBoard(char[][] subMarinBoard){
        int measure = subMarinBoard.length * 3;
        for(int i = 0; i < measure / 2 - 5; ++i)
            System.out.print(" ");
        System.out.println("SUBMARINE BOARD");
        System.out.print("  |");
        for(int i = 0; i < subMarinBoard.length; ++i)
            Print.printNumber(i+1, subMarinBoard.length);
        System.out.println();
        System.out.print(" ");
        for(int i = 0; i < subMarinBoard.length * 3 + 2; ++i)
            System.out.print("-");
        System.out.println();
        for(int i = 0; i < subMarinBoard.length; ++i) {
            Print.printNumber(i+1, subMarinBoard.length);
            for (int j = 0; j < subMarinBoard.length; ++j) {
                System.out.print(subMarinBoard[i][j] + " |");
            }
            System.out.println();
            System.out.print(" ");
            for(int j = 0; j < subMarinBoard.length * 3 + 2; ++j)
                System.out.print("-");
            System.out.println();
        }
        System.out.println();
    }

    private void gameStatus() {
        int boardSize = game.getBoardSize();
        Print.printSmallLineSeparator(boardSize);
        for (int i = 0; i < boardSize *3 +4; ++i)
            System.out.print(" ");
        System.out.println("player " + game.getTurn().toString());
        Print.printBoards(game.getSubMarinBoard(game.getTurn()), game.getTrackingBoard(game.getTurn()));
        System.out.println("Score = " + game.getScoreNumber(game.getTurn()));
        Print.printSmallLineSeparator(boardSize);
    }

    private void showStatistics(){
        int boardSize = game.getBoardSize();
        Print.printSmallLineSeparator(boardSize);
        System.out.println("Number of turns taking place = "
                + game.getTurnsNumber());
        System.out.println("Elapsed time = " + game.getElapsedTime());
        System.out.println("Player 1 Score = " + game.getScoreNumber(PlayerTurn.Turn.ONE));
        System.out.println("Player 2 Score = " + game.getScoreNumber(PlayerTurn.Turn.TWO));
        System.out.println("Player 1 hits = " + game.getHitNumber(PlayerTurn.Turn.ONE));
        System.out.println("Player 2 hits = " + game.getHitNumber(PlayerTurn.Turn.TWO));
        System.out.println("Player 1 missed " + game.getMissedNumber(PlayerTurn.Turn.ONE) +
                " number of times");
        System.out.println("Player 2 missed " + game.getMissedNumber(PlayerTurn.Turn.TWO) +
                " number of times");
        System.out.println("Player 1 average time per attack = " +
                game.getAverageTimePerAttack(PlayerTurn.Turn.ONE));
        System.out.println("Player 2 average time per attack = " +
                game.getAverageTimePerAttack(PlayerTurn.Turn.TWO));
        Print.printSmallLineSeparator(boardSize);
    }

    private void showFinalStatus(){
        int boardSize = game.getBoardSize();
        for (int i = 0; i < boardSize *3 / 2; ++i)
            System.out.print(" ");
        System.out.println("player " + 1);
        this.showSubMarineBoard(game.getSubMarinBoard(PlayerTurn.Turn.ONE));
        for (int i = 0; i < boardSize *3 / 2; ++i)
            System.out.print(" ");
        System.out.println("player " + 2);
        this.showSubMarineBoard(game.getSubMarinBoard(PlayerTurn.Turn.TWO));
        this.showStatistics();
    }

    private void finishGame(){
        saves = new LinkedList<>();
        game.endClock();
        PlayerTurn winPlayer = new PlayerTurn(game.getTurn());
        winPlayer.updateTurn();
        System.out.println("player " + winPlayer.toString() +" win the game!");
        this.showFinalStatus();
    }

    private void loadFile() {
        LoadGame file = new LoadGame();
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please enter the file path:");
            file.generate(scanner.nextLine());
            file.validGame();
            file.gameLoader(game);
        } catch (LoadingException e) {
            System.out.println("Not able to load XML file");
        } catch (PathIsNotXmlFileException e) {
            System.out.println("Path is not an XML");
        } catch (GameValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void saveGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please select file name to save");
        String path = scanner.nextLine();
        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(path))) {
            out.writeObject(this);
            haveSaving = true;
            out.flush();
            saves.add(path);
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException");
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

    private void LoadGame(){
        if(!haveSaving)
            System.out.println("Please saving before loading");
        else{
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please enter file name from the following to load:");
            System.out.println(saves.toString());
            String path = scanner.nextLine();
            if(!saves.contains(path))
                System.out.println("you try to load saving file that does not exist");
            else
                this.load(path);
        }
    }

    private void load(String path) {
        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(path))) {
             Manager manager = (Manager) in.readObject();
             this.getResources(manager);
        } catch (FileNotFoundException e) {
            System.out.println("you saving file does not exist");
        } catch (IOException e) {
            System.out.println("IOException");
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
        }
    }

    private ApiGame getGame() {
        return game;
    }

    private void getResources(Manager other){
        game.setHaveWorkingFile(other.getGame().isHaveWorkingFile());
        game.setInitializedGame(other.getGame().isInitializedGame());
        this.game = other.game;
        this.currentCell = other.currentCell;
        this.endGame = other.endGame;
    }

    private Cell getMineSquareToAdd(int boardSize){
        System.out.println("Please enter row and col on exact order, itch between "
                + 1 + " - " + boardSize + "for placing mine");
        Cell input = getSquareFromUser();
        input.setX(input.getX() - 1);
        input.setY(input.getY() - 1);
        return input;
    }

    private Cell getSquareToAttack(int boardSize){
        System.out.println("Please enter row and col on exact order, itch between "
                + 1 + " - " + boardSize + "for attacking attempt");
        Cell input = getSquareFromUser();
        input.setX(input.getX() - 1);
        input.setY(input.getY() - 1);
        return input;
    }

    private void informWinner(Report report) {
        if(report.isComputerMove())
            System.out.println("computer win the game!");
        else
            System.out.println("Player " + report.getPlayer().toString() + " win the game!");
        saves = new LinkedList<>();
        this.showFinalStatus();
        this.clearGame();
    }

    private void informAttackingPlayer(ApiGame.Result update, PlayerTurn player){
        switch (update){
            case MISSED:
                System.out.println("player " + player.toString() + " failed to hit a submarine");
                break;
            case HIT:
                System.out.println("player " + player.toString() + " successfully hit a submarine");
                break;
            case SUNK:
                System.out.println("player " + player.toString() + " successfully sunk a submarine");
                break;
            case MINE:
                System.out.println("player " + player.toString() + " Hit a mine");
                break;
            case BEEN_ATTACKED:
                System.out.println("player " + player.toString() +
                        " already had an attempt to hit this square. please select new square");
                break;
        }
    }

    private void informDefendingPlayer(ApiGame.Result update, Cell cell) {
        StringBuilder endMassage = new StringBuilder((cell.getX() + 1) + "," + (cell.getY() + 1) + ")");
        StringBuilder massage = new StringBuilder();
        switch (update){
            case MISSED:
                massage.append("computer failed to hit a submarine in (");
                break;
            case HIT:
                massage.append("computer successfully hit a submarine in (");
                break;
            case SUNK:
                massage.append("computer successfully hit a submarine in (");
                break;
            case MINE:
                massage.append("computer Hit a mine in (");
                break;
            case BEEN_ATTACKED:
                massage.append("computer already had an attempt to hit in(");
                break;
        }
        massage.append(endMassage);
        System.out.println(massage);
    }

    private Cell getSquareFromUser() {
        boolean goodInput;
        Scanner scanner = new Scanner(System.in);
        int x, y;
        do {
            x = -1;
            y = -1;
            goodInput = true;
            try {
                x = scanner.nextInt();
                y = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Please type in a valid numbers");
                goodInput = false;
                scanner = new Scanner(System.in);
            }
        }while(!goodInput);
        return new Cell(x, y);
    }
}

