import gamePack.*;
import javafx.animation.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.util.Pair;
import java.util.LinkedList;
import java.util.List;

class BoardController {
    private ApiGame game;
    private Button[][] trackingButtons;
    private Label[][] subMarineLabels;
    private char[][] subMarineBoard, trackingBoard;
    private GridPane boards;
    private Cell mineCell;
    private BooleanProperty haveResult, attackPermission, minePermission, animationComplete;
    private StringProperty errorMassage;
    private Pair<List<Report>, Boolean> reports;
    private int mineAmount , check = 0;
    private HBox minesBox;
    private Label[] minesLabels;
    private static final char SUBMARINE_SQUARE = '#', HIT_SQUARE = 'X',
            MISSED_SQUARE = 'O', EMPTY_SQUARE = ' ';
    private boolean textVisibility = true, validAnimation = false;
    private PlayerTurn.Turn player;

    BoardController(ApiGame theGame, char[][] theBoard, char[][] trackBoard,
                    PlayerTurn.Turn thePlayer){
        animationComplete = new SimpleBooleanProperty(false);
        player = thePlayer;
        game = theGame;
        haveResult = new SimpleBooleanProperty(false);
        attackPermission = new SimpleBooleanProperty(false);
        attackPermission.addListener((observable, oldValue, newValue) -> {
            if(newValue)
                this.enableAll();
            else
                this.disableAll();
        });
        minePermission = new SimpleBooleanProperty(false);
        errorMassage = new SimpleStringProperty();
        subMarineBoard = theBoard;
        trackingBoard = trackBoard;
        boards = new GridPane();
        this.setSubMarineBoard(theBoard);
        this.setTrackingBoard(trackBoard);
        this.setCssBoard();
        boards.setAlignment(Pos.CENTER);
        boards.getStyleClass().add("gameBoard");
    }

    BoardController(ApiGame theGame, char[][] theBoard, char[][] trackBoard,
                    PlayerTurn.Turn thePlayer, boolean textVisibility, Cell mineCell){
        player = thePlayer;
        game = theGame;
        this.textVisibility = textVisibility;
        this.mineCell = mineCell;
        subMarineBoard = theBoard;
        trackingBoard = trackBoard;
        boards = new GridPane();
        this.setSubMarineBoard(theBoard);
        this.setTrackingBoard(trackBoard);
        this.setCssBoard();
        boards.setAlignment(Pos.CENTER);
        boards.getStyleClass().add("gameBoard");
    }

    boolean isTextVisibility() {
        return textVisibility;
    }

    Cell getMineCell() {
        return mineCell;
    }

    void markSquare(Cell target, boolean myTurn) {
        if(myTurn && mineCell == null) {
            trackingButtons[target.getX()][target.getY()].getStyleClass().clear();
            trackingButtons[target.getX()][target.getY()].setText(String.valueOf(trackingBoard[target.getX()][target.getY()]));
            trackingButtons[target.getX()][target.getY()].setStyle("-fx-background-color: yellow");

        }else{
            subMarineLabels[target.getX()][target.getY()].getStyleClass().clear();
            subMarineLabels[target.getX()][target.getY()].setText(String.valueOf(subMarineBoard[target.getX()][target.getY()]));
            subMarineLabels[target.getX()][target.getY()].setStyle("-fx-background-color: yellow");
        }
    }

    GridPane getBoards(){return boards;}

    BooleanProperty haveResultProperty() {
        return haveResult;
    }

    void update() {
        haveResult.setValue(false);
        errorMassageProperty().setValue("");
        animationComplete.setValue(false);
        disableAll();
    }

    BooleanProperty animationCompleteProperty() {
        return animationComplete;
    }

    boolean isValidAnimation() {
        return validAnimation;
    }

    void setValidAnimation(boolean validAnimation) {
        this.validAnimation = validAnimation;
    }

    void setCssBoard() {
        for(int row =0; row< subMarineBoard.length; ++row){
            for(int col = 0; col < subMarineBoard.length; ++col){
                if(textVisibility) {
                    subMarineLabels[row][col].setText(String.valueOf(subMarineBoard[row][col]));
                    trackingButtons[row][col].setText(String.valueOf(trackingBoard[row][col]));
                }
                if(subMarineBoard[row][col] == SUBMARINE_SQUARE){
                    subMarineLabels[row][col].getStyleClass().add("shipCell");
                }
                else if(subMarineBoard[row][col] == HIT_SQUARE){
                    subMarineLabels[row][col].getStyleClass().removeAll("shipCell");
                    subMarineLabels[row][col].getStyleClass().add("shipHit");
                }
                else if(subMarineBoard[row][col] == MISSED_SQUARE){
                    subMarineLabels[row][col].getStyleClass().add("shipMissed");
                }
                if(trackingBoard[row][col] == HIT_SQUARE){
                    trackingButtons[row][col].getStyleClass().add("shipHit");
                }
                else if(trackingBoard[row][col] == MISSED_SQUARE){
                    trackingButtons[row][col].getStyleClass().add("shipMissed");
                }
            }
        }
    }

    char[][] getSubMarineBoard() {
        return subMarineBoard;
    }

    char[][] getTrackingBoard() {
        return trackingBoard;
    }

    Pair<List<Report>, Boolean> getReports() {
        return reports;
    }

    BooleanProperty attackPermissionProperty() {
        return attackPermission;
    }

    BooleanProperty minePermissionProperty() {
        return minePermission;
    }

    void setErrorMassage() {
        this.errorMassage.set("");
    }

    StringProperty errorMassageProperty() {
        return errorMassage;
    }

    void setMines(int mineAmount){
        this.mineAmount = mineAmount;
        this.initializeMines();
    }

    HBox getMinesBox() {
        return minesBox;
    }

    void setTextVisibility(boolean textVisibility, boolean replayStatus) {
        this.textVisibility = textVisibility;
        for(int row = 0; row < subMarineLabels.length; ++row){
            for (int col = 0; col < subMarineLabels.length; ++col){
                if(textVisibility) {
                    subMarineLabels[row][col].setText(
                            String.valueOf(subMarineBoard[row][col]));
                    trackingButtons[row][col].setText(
                            String.valueOf(trackingBoard[row][col]));
                }
                else{
                    subMarineLabels[row][col].setText("");
                    trackingButtons[row][col].setText("");
                }
            }
        }
        if(!replayStatus) {
            for (Label minesLabel : minesLabels) {
                if (textVisibility)
                    minesLabel.setText("@");
                else
                    minesLabel.setText("");
            }
        }
    }

    PlayerTurn.Turn getPlayer() {
        return player;
    }

    private boolean placeMine(int row, int col) {
        mineCell = new Cell(row, col);
        List<Report> results;
        PlayerTurn.Turn currentTurn = game.getTurn();
        boolean succeed;
        try {
            game.endClock();
            results = game.addMine(mineCell);
            reports = new Pair<>(results, currentTurn != game.getTurn());
            this.setCssBoard();
            if(validAnimation && results.size() != 0){
                setAnimation(results);
            }
            succeed = true;
        } catch (InvalidSquarePositionException | TooManyMinesException e) {
            succeed = false;
            errorMassage.setValue("Please choose valid square");
        }finally {
            game.startClock();
        }
        return succeed;
    }

    private boolean attack(int row, int col) {
        mineCell = null;
        List<Report> results;
        PlayerTurn.Turn currentTurn = game.getTurn();
        boolean succeed;
        try {
            game.endClock();
            results = game.attack(new Cell(row, col));
            reports = new Pair<>(results, currentTurn != game.getTurn());
            this.setCssBoard();
            if(validAnimation){
                setAnimation(results);
            }
            succeed = true;
        } catch (WrongInputException e) {
            e.printStackTrace();
            succeed = false;
        }finally {
            game.startClock();
        }
        return succeed;
    }

    private void setSubMarineBoard(char[][] board ){
        subMarineLabels = new Label[board.length][board.length];
        for(int row = 0; row < board.length; ++row) {
            for (int col = 0; col < board.length; ++col) {
                subMarineLabels[row][col] = this.generateLabelCell(row, col);
                boards.add(subMarineLabels[row][col], col, row + 1);
            }
        }
    }

    private void setTrackingBoard(char[][] board) {
        HBox hRow;
        trackingButtons = new Button[board.length][board.length];
        for(int row = 0; row < board.length; ++row) {
            hRow = new HBox();
            for (int col = 0; col < board.length; ++col) {
                trackingButtons[row][col] = this.generateButtonCell(row, col);
                //trackingButtons[row][col].setText(String.valueOf(board[row][col]));
                trackingButtons[row][col].setDisable(true);
                hRow.getChildren().add(trackingButtons[row][col]);
            }
            hRow.setPadding(new Insets(0,25,0,25));
            boards.add(hRow, board.length + 100, row + 1);
        }
    }

    private Button generateButtonCell(int row, int col) {
        Button cell = new Button();
        cell.setPrefSize(60.0D, 60.0D);
        cell.setAlignment(Pos.CENTER);
        cell.setMaxSize(90.0D, 90.0D);
        cell.setMinSize(30.0D, 30.0D);
        cell.setOnAction(e -> {
            if(trackingBoard[row][col] != EMPTY_SQUARE){
                errorMassage.setValue("Please choose free square");
            }
            else if(attackPermission.getValue()){
                boolean success = attack(row, col);
                if(success)
                    haveResult.setValue(true);
            }
        });
        return cell;
    }

    private Label generateLabelCell(int row, int col) {
        final Label cell = new Label();
        cell.setPrefSize(60.0D, 60.0D);
        cell.setAlignment(Pos.CENTER);
        cell.setMaxSize(90.0D, 90.0D);
        cell.setMinSize(30.0D, 30.0D);
        cell.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-radius: 35%");
        cell.setOnDragOver((event) -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        cell.setOnDragEntered((event) -> {
            if (event.getDragboard().hasString()) {
                cell.setStyle("-fx-background-insets: 70%");
            }
            event.consume();
        });

        cell.setOnDragExited((event) -> {
            cell.setStyle("-fx-background-color: none");
            cell.setStyle("-fx-border-color: gray; -fx-border-width: 1");
            event.consume();
        });

        cell.setOnDragDropped((event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString() && minePermission.getValue()) {
                success = placeMine(row, col);
                if(success) {
                    cell.getStyleClass().add("mine");
                    cell.setText(db.getString());
                }
            }
            else if (!minePermission.getValue())
                errorMassage.setValue("Please click on placeMine Button");
            event.setDropCompleted(success);
            event.consume();
        });

        return cell;
    }

    private void setAnimation(List<Report> reports) {
        for(Report report: reports){
            if(report.getPlayer() == player){
                switch (report.getResult()) {
                    case MISSED:
                        missedAnimation(report.getTarget());
                        break;
                    case HIT:
                        hitAnimation(report.getTarget());
                        break;
                    case SUNK:
                        sunkAnimation(report.getTarget());
                        break;
                    case MINE:
                        hitOnMineAnimation(report.getTarget());
                        break;
                }
            }
        }
    }

    private void sunkAnimation(Cell target) {
        List<Cell> ship = game.getShip(player, target);
        List<FadeTransition> fades = new LinkedList<>();
        for(Cell shipCell: ship){
            FadeTransition fade = new FadeTransition();
            fade.setNode(trackingButtons[shipCell.getX()][shipCell.getY()]);
            fade.setDuration(new Duration(1000));
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setCycleCount(2);
            fade.setAutoReverse(true);
            fades.add(fade);
        }

        fades.get(0).setOnFinished(e->animationComplete.setValue(true));
        for (FadeTransition fade : fades)
            fade.play();
    }

    private void hitAnimation(Cell target) {
        RotateTransition rt = new RotateTransition();
        rt.setNode(trackingButtons[target.getX()][target.getY()]);
        rt.setFromAngle(0);
        rt.setToAngle(360);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.setCycleCount(1);
        rt.setDuration(new Duration(2000));
        rt.setOnFinished(e->animationComplete.setValue(true));
        rt.play();
    }

    private void missedAnimation(Cell target) {
        Timeline timeline = new Timeline();
        Duration time = new Duration(1000);
        KeyValue keyValue = new KeyValue(trackingButtons[target.getX()][target.getY()].translateXProperty(), 75);
        KeyFrame keyFrame = new KeyFrame(time, keyValue);
        timeline.getKeyFrames().add(keyFrame);
        keyValue = new KeyValue(trackingButtons[target.getX()][target.getY()].translateYProperty(), 75);
        keyFrame = new KeyFrame(time, keyValue);
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(2);
        timeline.setAutoReverse(true);
        timeline.setOnFinished(e->animationComplete.setValue(true));
        timeline.play();
    }

    private void hitOnMineAnimation(Cell target) {
        PauseTransition pt = new PauseTransition();
        pt.setDuration(new Duration(500));
        pt.setOnFinished(e ->
        {
            String style = trackingButtons[target.getX()][target.getY()].getStyleClass().get(1);
            trackingButtons[target.getX()][target.getY()].getStyleClass().remove(1);
            if (style.equals("mine"))
                trackingButtons[target.getX()][target.getY()].getStyleClass().add("shipHit");
            else
                trackingButtons[target.getX()][target.getY()].getStyleClass().add("mine");
            if(check == 4){
                trackingButtons[target.getX()][target.getY()].getStyleClass().add("shipHit");
                animationComplete.setValue(true);
                check = 0;
            }
            else {
                check++;
                pt.play();
            }
        });
        pt.play();
    }

    private void initializeMines() {
        minesLabels = new Label[mineAmount];
        minesBox = new HBox();
        for (int i = 0; i < minesLabels.length; i++) {
            minesLabels[i] = createMineLabel();
            minesBox.getChildren().add(minesLabels[i]);
        }
//        minesBox.getChildren().addAll(minesLabels);
        minesBox.setAlignment(Pos.CENTER);
    }

    private Label createMineLabel() {
        final Label label = new Label();
        label.setPrefSize(60.0D, 60.0D);
        label.setAlignment(Pos.CENTER);
        label.setMaxSize(90.0D, 90.0D);
        label.setMinSize(30.0D, 30.0D);
        label.getStyleClass().add("mine");
        label.setText("@");
        label.setOnDragDetected((event) -> {
            WritableImage snapshot = label.snapshot(new SnapshotParameters(), null);
            Dragboard db = label.startDragAndDrop(TransferMode.ANY);

            ClipboardContent content = new ClipboardContent();
            content.putString("");
            db.setContent(content);
            db.setDragView(snapshot, snapshot.getWidth() / 2, snapshot.getHeight() / 2);
            event.consume();
        });

        label.setOnDragDone((event) -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                label.setText("");
                haveResult.setValue(true);
                label.setVisible(false);
                if(validAnimation && reports.getKey().size() == 0){
                    animationComplete.setValue(true);
                }
            }
            event.consume();
        });
        return label;
    }

    private void disableAll() {
        for (Button[] trackingButton : trackingButtons) {
            for (int j = 0; j < trackingButtons.length; ++j) {
                trackingButton[j].setDisable(true);
            }
        }
    }

    private void enableAll() {
        for (Button[] trackingButton : trackingButtons) {
            for (int j = 0; j < trackingButtons.length; ++j) {
                trackingButton[j].setDisable(false);
            }
        }
    }
}
