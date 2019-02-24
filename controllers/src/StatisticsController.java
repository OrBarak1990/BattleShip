import gamePack.ApiGame;
import gamePack.PlayerTurn;
import gamePack.SubMarine;
import gamePack.SubTracker;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.control.Label;

class StatisticsController {
    private ApiGame game;
    @FXML
    private VBox statisticBox;
    @FXML
    private List<Label> playerOneTrackers, playerTwoTrackers;
    private static final String REGULAR = "regular", LShape = "LShape",
        PLAYER_ONE = "player one ", PLAYER_TWO = "player two ",
            TURN_NUMBER = "Turn number ", PLAYING_TIME = "Playing time ";
    @FXML
    private Label playerOneScore, playerTwoScore,
            turnNumber, playingTime;


    StatisticsController(ApiGame theGame){
        game = theGame;
        playerOneTrackers = new LinkedList<>();
        playerTwoTrackers = new LinkedList<>();
        initializeTrackers(theGame.getTracker(PlayerTurn.Turn.ONE), playerOneTrackers);
        initializeTrackers(theGame.getTracker(PlayerTurn.Turn.TWO), playerTwoTrackers);
        setBox();
    }

    private void setBox() {
        statisticBox = new VBox();
        Label statistic = new Label("statistic");
        Label score = new Label("score");
        playerOneScore = new Label(PLAYER_ONE + "0");
        playerTwoScore = new Label(PLAYER_TWO + "0");
        Label ships = new Label("Ships");
        Label playerOne = new Label("player one");
        Label playerTwo = new Label("player two");
        turnNumber = new Label(TURN_NUMBER + "1");
        playingTime = new Label(PLAYING_TIME + "00:00");
        statisticBox.getChildren().addAll(statistic, score, playerOneScore,
                playerTwoScore, ships, playerOne);
        addTrackersLabels(playerOneTrackers);
        statisticBox.getChildren().addAll(playerTwo);
        addTrackersLabels(playerTwoTrackers);
        statisticBox.getChildren().addAll(turnNumber, playingTime);
    }

    private void addTrackersLabels(List<Label> trackers) {
        for(Label tracker : trackers){
            statisticBox.getChildren().add(tracker);
        }
    }

    private void initializeTrackers(List<SubTracker> theTrackers, List<Label> TrackerLabels) {
        String text;
        for (SubTracker tracker : theTrackers){
            if(tracker.getCategory() == SubMarine.Category.REGULAR)
                text = REGULAR;
            else
                text = LShape;
            text += (", length = " +
                    tracker.getLength() + ":  " + tracker.getAmount() + "\\" +
                    tracker.getLive());
            Label label = new Label(text);
            TrackerLabels.add(label);
            tracker.liveProperty().addListener((observable, oldValue, newValue) -> {
                String newString = label.getText().substring(0, label.getText().length()- 1);
                newString += String.valueOf(newValue);
                label.setText(newString);
            });
        }
    }

    VBox getStatisticBox() {
        return statisticBox;
    }

    private void setLabels(){
        playerOneScore.setText(PLAYER_ONE + game.getScoreNumber(PlayerTurn.Turn.ONE));
        playerTwoScore.setText(PLAYER_TWO + game.getScoreNumber(PlayerTurn.Turn.TWO));
        turnNumber.setText(TURN_NUMBER + game.getTurn());
        playingTime.setText(PLAYING_TIME + game.getElapsedTime());

    }

    void update(){
        setLabels();
    }


}
