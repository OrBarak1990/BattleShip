import gamePack.ApiGame;
import gamePack.Cell;
import gamePack.PlayerTurn;
import gamePack.Report;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;

public class ReplayController {
    private BoardController boards;
    private List<Pair<Cell, Boolean>> marks = new LinkedList<>();

    @FXML
    private BorderPane borderPane;
    @FXML
    private Label playerValue;
    @FXML
    private Label scoreValue;
    @FXML
    private Label hitsValue;
    @FXML
    private Label missedValue;

    @FXML
    private Button loadGame, startOver, prev, next, exit;

    void initializer(String player, String score, String hits, String missed,
                            BoardController theBoards){
        playerValue.textProperty().setValue(player);
        scoreValue.textProperty().setValue(score);
        hitsValue.textProperty().setValue(hits);
        missedValue.textProperty().setValue(missed);
        boards = theBoards;
        borderPane.setCenter(boards.getBoards());
        borderPane.autosize();
        borderPane.getStyleClass().setAll("root");
    }

    BorderPane getBorderPane() {
        return borderPane;
    }

    void markResults(List<Report> reports) {
        PlayerTurn.Turn playerTurn = playerValue.getText().equals("1") ? PlayerTurn.Turn.ONE : PlayerTurn.Turn.TWO;
        if (reports != null && reports.size() != 0) {
            for (Report report : reports) {
                if (report.getResult() != ApiGame.Result.WIN &&
                        report.getResult() != ApiGame.Result.BEEN_ATTACKED){
                    boards.markSquare(report.getTarget(), playerTurn == report.getPlayer());
                    marks.add(new Pair<>(report.getTarget(), playerTurn == report.getPlayer()));
                }
            }
        }
        else if(boards.getMineCell() != null &&
                boards.getSubMarineBoard()[boards.getMineCell().getX()][boards.getMineCell().getY()] == '@'){
            boards.markSquare(boards.getMineCell(), true);
            marks.add(new Pair<>(boards.getMineCell(), true));
        }
    }

    Button getPrev() {
        return prev;
    }

    Button getNext() {
        return next;
    }

    Button getLoadGame() {
        return loadGame;
    }

    Button getStartOver() {
        return startOver;
    }

    Button getExit() {
        return exit;
    }

    BoardController getBoards() {
        return boards;
    }

    void setMarkSquares() {
        for(Pair<Cell, Boolean> pair : marks){
            boards.markSquare(pair.getKey(), pair.getValue());
        }
    }
}
