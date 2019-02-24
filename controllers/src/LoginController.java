import gamePack.ApiGame;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import loader.GameValidationException;
import loader.LoadGame;
import loader.LoadingException;
import loader.PathIsNotXmlFileException;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginController {
    private BooleanProperty availableStart;
    private ApiGame game;
    private String path;
    private File file;
    private LoadGame load;

    @FXML
    private Button startGame;

    @FXML
    private Label status;

    @FXML
    private CheckBox fileOpen, fileLoad, errorsCheck, loadGame;

    @FXML
    private ProgressBar progressBar;

    public void setGame(ApiGame game) {
        this.game = game;
    }

    void initializeLogin(ApiGame theGame){
        game = theGame;
        availableStart = new SimpleBooleanProperty(false);
        startGame.setDisable(true);
        fileOpen.setDisable(true);
        fileLoad.setDisable(true);
        errorsCheck.setDisable(true);
        loadGame.setDisable(true);
    }

    boolean isAvailableStart() {
        return availableStart.get();
    }

    BooleanProperty availableStartProperty() {
        return availableStart;
    }

    void reloadGame(){
        availableStart.setValue(false);
        startGame.setDisable(true);
        loadGameWithTask();
    }

    @FXML
    private void loadGameEvent(ActionEvent event){
        openingFile();
        loadGameWithTask();
    }

    private void loadGameWithTask() {
        status.setText("");
        Task task = new Task<Void>() {
            @Override public Void call() {
                if(path != null) {
                    path = file.getAbsolutePath();
                    updateProgress(1, 4);
                    updateMessage("opening file succeed");
                    fileOpen.setSelected(true);
                }
                else{
                    updateMessage("opening file failed");
                    this.cancel(true);
                }
                if(!this.isCancelled())
                    thirdSleep();

                try {
                    loadingFile();
                    updateProgress(2, 4);
                    updateMessage("loading file succeed");
                    fileLoad.setSelected(true);
                } catch (PathIsNotXmlFileException | LoadingException e) {
                    updateMessage(e.getMessage());
                    this.cancel(true);
                }
                if(!this.isCancelled())
                    thirdSleep();

                try {
                    makeErrorChecking();
                    updateProgress(3, 4);
                    updateMessage("error checking succeed");
                    errorsCheck.setSelected(true);
                } catch (GameValidationException e) {
                    updateMessage(e.getMessage());
                    this.cancel(true);
                }
                if(!this.isCancelled())
                    thirdSleep();

                loadingGame();
                if(!this.isCancelled()) {
                    updateProgress(4, 4);
                    loadGame.setSelected(true);
                    updateMessage("loading Game succeed");
                }
                return null;
            }

            private void thirdSleep(){
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        progressBar.progressProperty().bind(task.progressProperty());
        status.textProperty().bind(task.messageProperty());
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(task);

        task.setOnSucceeded(new EventHandler<WorkerStateEvent>(){

            public void handle(WorkerStateEvent arg0) {
                status.textProperty().unbind();
                status.setText("Done");
                startGame.setDisable(false);
                status.textProperty().unbind();
                availableStart.setValue(true);
            }
        });

        task.setOnCancelled(new EventHandler<WorkerStateEvent>(){
            public void handle(WorkerStateEvent arg0) {
                status.textProperty().unbind();
                startGame.setDisable(true);
            }
        });
    }

    private void openingFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File With");
        file = fileChooser.showOpenDialog(new Stage());
        if(file != null)
            path = file.getAbsolutePath();
    }

    private void loadingFile() throws PathIsNotXmlFileException, LoadingException {
        load = new LoadGame();
        load.generate(path);
    }

    private void makeErrorChecking() throws GameValidationException {
        load.validGame();
    }

    private void loadingGame(){
        load.gameLoader(game);
    }
}
