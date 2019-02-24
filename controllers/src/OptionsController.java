import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

public class OptionsController {
    private BooleanProperty gameExitProperty, gameEndProperty;

    @FXML
    private VBox box;

    @FXML
    private Button endGame, exit;

    @FXML
    private ComboBox cssStyle, animation;

    void initializeOptionsController() {
        gameExitProperty = new SimpleBooleanProperty(false);
        gameEndProperty = new SimpleBooleanProperty(false);
        cssStyle.getItems().addAll("default Style", "first skin Style", "second skin Style");
        cssStyle.setPromptText("Style [default]");
        animation.getItems().addAll("Animation On", "Animation Off");
        animation.setPromptText("Animation [Off]");
    }

    ComboBox getAnimation() {
        return animation;
    }

    ComboBox getCssStyle() {
        return cssStyle;
    }

    public void exitAction(ActionEvent event) {
        event.consume();
        this.gameExitProperty.setValue(true);
    }

    public void endGameAction(ActionEvent event) {
        event.consume();
        this.gameEndProperty.setValue(true);
    }


    Button getEndGame() {
        return endGame;
    }

    Button getExit() {
        return exit;
    }

    VBox getBox() {
        return box;
    }
}
