import gamePack.ApiGame;
import gamePack.PlayerTurn;
import gamePack.Report;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class GameGui  extends Application {
    private GameController gameControl = new GameController();
    private PlayerInfoController playerOneController, playerTwoController;
    private BoardController playerOneBoard, playerTwoBoard;
    private Scene playerOneScene, playerTwoScene, loginScene;
    private List<Scene> replayScenes = new LinkedList<>();
    private ListIterator<Scene> replayIterator = replayScenes.listIterator();
    private LoginController loginController;
    private Stage primaryStage;
    private StatisticsController statistics;
    private ReplayController lastReplay;
    private List <ReplayController> replays = new LinkedList<>();
    private static final String FIRST_CSS = "first skin Style", SECOND_CSS = "second skin Style",
            DEFAULT_CSS = "default Style", ANIMATION_ON = "Animation On";
    private String chosenStyle = DEFAULT_CSS;
    private OptionsController options;
    private boolean lastReplayIsPrev = false, lastReplayIsNext = false;


    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            setLoginScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setTitle("SubMarine");

        primaryStage.setScene(loginScene);

        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void setLoginScene() throws IOException {
        URL url = this.getClass().getResource("fxml/TaskLogin.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(url);
        Parent loginComponent = fxmlLoader.load(url.openStream());
        loginController = fxmlLoader.getController();
        loginController.initializeLogin(gameControl.getEngine());
        loginController.availableStartProperty().addListener((observable, oldValue, newValue) ->{
            if(newValue && loginController.isAvailableStart())
                this.startGame();
        });
        loginScene = new Scene(loginComponent);
    }

    private void setOptions() {
        URL url = this.getClass().getResource("fxml/Options.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(url);
        try {
            Parent OptionsComponent = fxmlLoader.load(url.openStream());
            options = fxmlLoader.getController();
            options.initializeOptionsController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPlayerScene(int playerNumber) throws IOException {
        URL url = this.getClass().getResource("fxml/PlayerInfo.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(url);
        Parent infoComponent = fxmlLoader.load(url.openStream());
        PlayerInfoController controller = fxmlLoader.getController();
        controller.initializeControllers(gameControl.getEngine(), playerNumber);
        BoardController board;
        if(playerNumber == 1) {
            board = new BoardController(gameControl.getEngine(), gameControl.getEngine().getSubMarinBoard(PlayerTurn.Turn.ONE),
                    gameControl.getEngine().getTrackingBoard(PlayerTurn.Turn.ONE), PlayerTurn.Turn.ONE);
            board.setMines(gameControl.getEngine().getAvailableAmountOfMines(PlayerTurn.Turn.ONE));
        }else {
            board = new BoardController(gameControl.getEngine(), gameControl.getEngine().getSubMarinBoard(PlayerTurn.Turn.TWO),
                    gameControl.getEngine().getTrackingBoard(PlayerTurn.Turn.TWO), PlayerTurn.Turn.TWO);
            board.setMines(gameControl.getEngine().getAvailableAmountOfMines(PlayerTurn.Turn.TWO));
        }
        BorderPane gameBorder = new BorderPane();
        ScrollPane boxScroll = new ScrollPane();
        boxScroll.setContent(board.getBoards());
        boxScroll.setFitToWidth(true);
        boxScroll.setFitToHeight(true);
        gameBorder.setRight(infoComponent);
        gameBorder.setCenter(boxScroll);
        gameBorder.setBottom(board.getMinesBox());
        Scene scene = new Scene(gameBorder, 1080.0D, 630.0D);
        if(playerNumber== 1){
            playerOneController = controller;
            playerOneBoard = board;
            playerOneScene = scene;
        }
        else{
            playerTwoController = controller;
            playerTwoBoard = board;
            playerTwoScene = scene;
        }
    }

    private void setListeners(PlayerInfoController controller, BoardController board) {
        board.minePermissionProperty().bind(controller.placeMineProperty());
        board.attackPermissionProperty().bind(controller.attackProperty());
        options.getExit().setOnAction(e-> primaryStage.close());
        options.getEndGame().setOnAction(e-> {
            gameControl.getEngine().playerQuit();
            if (replayScenes.size() == 0)
                this.addReplayToScenes(playerOneController, playerOneBoard, null);
            else
                lastReplay.getNext().setDisable(true);
            setEndGameScene();
        });
        options.getCssStyle().setOnAction(e -> changeStyle((String) options.getCssStyle().getValue()));
        options.getAnimation().setOnAction(e -> setAnimation((String) options.getAnimation().getValue()));

        board.errorMassageProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, newValue, ButtonType.OK);
                alert.show();
                board.setErrorMassage();
            }
        });

        board.haveResultProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && !board.isValidAnimation())
                moveHandle(controller, board);
        });

        board.animationCompleteProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                moveHandle(controller, board);
            }
        });
    }

    private void changeStyle(String style) {
        if(style.equals(FIRST_CSS) && !chosenStyle.equals(FIRST_CSS)){
            playerOneScene.getStylesheets().setAll("Css/skinOne.css");
            playerTwoScene.getStylesheets().setAll("Css/skinOne.css");
            loginScene.getStylesheets().setAll("Css/skinOne.css");
            ListIterator<ReplayController> itr = replays.listIterator(0);
            ReplayController replay;
            for(Scene scene : replayScenes){
                scene.getStylesheets().clear();
                replay = itr.next();
                replay.getBoards().setTextVisibility(false, true);
                scene.getStylesheets().setAll("Css/skinOne.css");
                replay.setMarkSquares();
            }
            playerTwoBoard.setTextVisibility(false, false);
            playerOneBoard.setTextVisibility(false, false);
            chosenStyle = FIRST_CSS;
        }
        else if(style.equals(SECOND_CSS)&& !chosenStyle.equals(SECOND_CSS)){
            playerOneScene.getStylesheets().setAll("Css/skinTwo.css");
            playerTwoScene.getStylesheets().setAll("Css/skinTwo.css");
            loginScene.getStylesheets().setAll("Css/skinTwo.css");
            ListIterator<ReplayController> itr = replays.listIterator(0);
            ReplayController replay;
            for(Scene scene : replayScenes){
                scene.getStylesheets().clear();
                replay = itr.next();
                replay.getBoards().setTextVisibility(false, true);
                scene.getStylesheets().setAll("Css/skinTwo.css");
                replay.setMarkSquares();
            }
            playerTwoBoard.setTextVisibility(false, false);
            playerOneBoard.setTextVisibility(false, false);
            chosenStyle = SECOND_CSS;
        }else if(!chosenStyle.equals(DEFAULT_CSS)){
            playerOneScene.getStylesheets().remove(0);
            playerTwoScene.getStylesheets().remove(0);
            loginScene.getStylesheets().remove(0);
            for(Scene scene : replayScenes){
                scene.getStylesheets().remove(0);
            }
            playerTwoBoard.setTextVisibility(true, false);
            playerOneBoard.setTextVisibility(true, false);
        }
    }

    private void moveHandle(PlayerInfoController controller, BoardController board) {
        Pair<List<Report>, Boolean> pair = new Pair<>(board.getReports().getKey(),
                board.getReports().getValue());
        statistics.update();
        if(board == playerOneBoard)
            playerTwoBoard.setCssBoard();
        else
            playerOneBoard.setCssBoard();
        playerOneBoard.update();
        playerTwoBoard.update();
        controller.update();
        addReplayToScenes(controller, board, pair.getKey());
        if(pair.getKey().size() > 0 && pair.getKey().get(pair.getKey().size() -1).getResult()== ApiGame.Result.WIN)
            setEndGameScene();
        if(pair.getValue())
            this.changeScenes();
    }

    private void setEndGameScene() {
        URL url = this.getClass().getResource("fxml/EndGame.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(url);
        try {
            Parent endGameComponent = fxmlLoader.load(url.openStream());
            EndGameController end = fxmlLoader.getController();
            end.initializeEnd(gameControl.getEngine().getWinPlayer(),
                    gameControl.getLoosePlayer(), gameControl.getWinnerScore(), gameControl.getLooseScore());
            end.closeButtonProperty().addListener((observable, oldValue, newValue) ->{
                primaryStage.setScene(replayIterator.previous());
                primaryStage.sizeToScene();
                primaryStage.centerOnScreen();
            });
            Scene endGame = new Scene(endGameComponent);
            setCss(endGame);
            this.centerStage();
            primaryStage.setScene(endGame);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addReplayToScenes(PlayerInfoController controller, BoardController board, List<Report> reports) {
        URL url = this.getClass().getResource("fxml/GameReplay.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(url);
        try {
            fxmlLoader.load(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        BoardController replayBoard = new BoardController(gameControl.getEngine(),
                board.getSubMarineBoard(), board.getTrackingBoard(),
                board.getPlayer(), board.isTextVisibility(), board.getMineCell());
        ReplayController replay = fxmlLoader.getController();
        replay.initializer(controller.getPlayer(), controller.getScore(), controller.getHits(),
                controller.getMissed(), replayBoard);
        replay.markResults(reports);
        if(reports == null){
            replay.getPrev().setDisable(true);
            replay.getNext().setDisable(true);
        }
        else{
            if(replayScenes.size() == 0)
                replay.getPrev().setDisable(true);
            if(reports.size() > 0 && reports.get(reports.size() -1).getResult()== ApiGame.Result.WIN)
                replay.getNext().setDisable(true);
        }
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(replay.getBorderPane());
        Scene scene = new Scene(scrollPane, 1080.0D, 630.0D);
        setCss(scene);
        replayIterator.add(scene);
        lastReplay = replay;
        setReplayListeners(replay);
        replays.add(replay);
    }

    private void setReplayListeners(ReplayController replay) {
        replay.getNext().setOnAction(e-> {
            if(lastReplayIsPrev)
                replayIterator.next();
            Scene scene= replayIterator.next();
            primaryStage.setScene(scene);
            lastReplayIsNext = true;
            lastReplayIsPrev = false;
        });
        replay.getPrev().setOnAction(e-> {
            if(lastReplayIsNext)
                replayIterator.previous();
            Scene scene= replayIterator.previous();
            primaryStage.setScene(scene);
            lastReplayIsPrev = true;
            lastReplayIsNext = false;
        });
        replay.getStartOver().setOnAction(e-> this.reloadGame());
        replay.getLoadGame().setOnAction(e-> setNewGame());
        replay.getExit().setOnAction(e-> primaryStage.close());
    }

    private void setNewGame(){
        replayScenes = new LinkedList<>();
        replayIterator = replayScenes.listIterator();
        loginController = null;
        chosenStyle = DEFAULT_CSS;
        try {
            this.setLoginScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setScene(loginScene);
    }

    private void reloadGame() {
        replayScenes = new LinkedList<>();
        replayIterator = replayScenes.listIterator();
        chosenStyle = DEFAULT_CSS;
        replays = new LinkedList<>();
        loginController.reloadGame();
    }

    private void startGame() {
        try {
            statistics = new StatisticsController(gameControl.getEngine());
            setOptions();
            setPlayerScene(2);
            setPlayerScene(1);
            this.setListeners(playerOneController, playerOneBoard);
            this.setListeners(playerTwoController, playerTwoBoard);
            gameControl.getEngine().initialize(null);
            this.centerStage();
            gameControl.getEngine().startClock();
            this.changeScenes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeScenes() {
        if(primaryStage.getScene().equals(playerOneScene)) {
            ((BorderPane)playerTwoScene.getRoot()).setLeft(statistics.getStatisticBox());
            playerTwoController.getButtonsBox().getChildren().add(options.getBox());
            primaryStage.setScene(playerTwoScene);
        }
        else{
            ((BorderPane)playerOneScene.getRoot()).setLeft(statistics.getStatisticBox());
            playerOneController.getButtonsBox().getChildren().add(options.getBox());
            primaryStage.setScene(playerOneScene);
        }
    }

    private void centerStage(){
        primaryStage.setWidth(playerOneScene.getWidth());
        primaryStage.setHeight(playerOneScene.getHeight());
        primaryStage.centerOnScreen();
    }

    private void setCss(Scene scene){
        if(chosenStyle.equals(FIRST_CSS)){
            scene.getStylesheets().add("Css/skinOne.css");
        }else if(chosenStyle.equals(SECOND_CSS)){
            scene.getStylesheets().add("Css/skinTwo.css");
        }
    }

    private void setAnimation(String animation) {
        if(animation.equals(ANIMATION_ON)){
            playerTwoBoard.setValidAnimation(true);
            playerOneBoard.setValidAnimation(true);
        }
        else{
            playerTwoBoard.setValidAnimation(false);
            playerOneBoard.setValidAnimation(false);
        }
    }
}
