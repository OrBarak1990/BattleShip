import gamePack.ApiGame;
import gamePack.PlayerTurn;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PlayerInfoController {
    private ApiGame game;
    private BooleanProperty attackProperty, placeMineProperty;
    private PlayerTurn.Turn playerNumber;

    @FXML
    private VBox buttonsBox;

    @FXML
    private Label availableMines, averageTurn, missed, score, player, hits;


    void initializeControllers(ApiGame theGame, int playerNumber){
        attackProperty = new SimpleBooleanProperty(false);
        placeMineProperty = new SimpleBooleanProperty(false);
        game = theGame;
        player.setText("Player " + playerNumber);
        if(playerNumber == 1)
            this.playerNumber = PlayerTurn.Turn.ONE;
        else
            this.playerNumber = PlayerTurn.Turn.TWO;

        setLabels();

    }


    BooleanProperty attackProperty() {
        return attackProperty;
    }

    BooleanProperty placeMineProperty() {
        return placeMineProperty;
    }

    public void placeMineAction(ActionEvent event) {
        this.attackProperty.setValue(false);
        this.placeMineProperty.setValue(true);
    }

    public void attackAction(ActionEvent event) {
        this.placeMineProperty.setValue(false);
        this.attackProperty.setValue(true);
    }

    private void setLabels(){
        missed.textProperty().setValue("Missed " + String.valueOf(game.getMissedNumber(playerNumber)));
        availableMines.textProperty().setValue("Available Mines " + String.valueOf(game.getAvailableAmountOfMines(playerNumber)));
        averageTurn.textProperty().setValue("Average Turn " + game.getAverageTimePerAttack(playerNumber));
        hits.textProperty().setValue("Hits " + String.valueOf(game.getHitNumber(playerNumber)));
        score.textProperty().setValue("Score " + String.valueOf(game.getScoreNumber(playerNumber)));
    }

    void update() {
        setLabels();
        this.placeMineProperty.setValue(false);
        this.attackProperty.setValue(false);

    }

    String getPlayer(){
        if(playerNumber == PlayerTurn.Turn.ONE)
            return "1";
        else
            return "2";
    }

    String getScore(){
        return score.getText().substring(6);
    }

    String getHits(){
        return hits.getText().substring(5);
    }

    String getMissed(){
        return missed.getText().substring(7);
    }

    VBox getButtonsBox() {
        return buttonsBox;
    }
}
