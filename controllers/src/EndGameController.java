import gamePack.PlayerTurn;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class EndGameController {

    private BooleanProperty closeButton;

    @FXML
    private Label playerWinScore, playerLooseScore, title;

    @FXML
    private VBox box;

    @FXML
    private Button close;

    void initializeEnd(PlayerTurn win, PlayerTurn loose,
                       int winScore, int looseScore){
        playerWinScore.setText("Player " + win.toString() + " Score " + winScore);
        playerLooseScore.setText("Player " + loose.toString() + " Score " +
                looseScore);
        title.setText("Player " + win.toString() + " Win! ");
        close.setAlignment(Pos.BOTTOM_RIGHT);
        closeButton = new SimpleBooleanProperty(false);
    }

    BooleanProperty closeButtonProperty() {
        return closeButton;
    }

    @FXML
    private void continueAction(ActionEvent event){
        closeButton.setValue(true);
    }

    public boolean isCloseButton() {
        return closeButton.get();
    }
}
