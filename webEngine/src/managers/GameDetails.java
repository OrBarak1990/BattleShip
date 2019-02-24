package managers;

import gamePack.ApiGame;
import gamePack.Game;
import gamePack.PlayerTurn;
import loader.GameValidationException;
import loader.LoadGame;
import loader.LoadingException;
import loader.PathIsNotXmlFileException;
import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class GameDetails {
    private String path;
    private InputStream gameStream;
    private String gameName;
    private String uploadUser;
    private ApiGame game;
    private LoadGame loadGame;
    private List<String> playersNames, viewersNames;
    private boolean playerTwoSaved, playerOneSaved;
    private List<SingleChatEntry> chat;

    GameDetails(InputStream theStream, String gameName, String uploadUser, List<SingleChatEntry> chat) {
        this.chat = chat;
        //path = theStream.getAbsolutePath();
        gameStream = theStream;
        this.gameName = gameName;
        this.uploadUser = uploadUser;
        playersNames = new LinkedList<>();
        viewersNames = new LinkedList<>();
    }

    public int getRegisteredPlayers() {
        return playersNames.size();
    }

    String getUploadUser() {
        return uploadUser;
    }

    String getGameName() {
        return gameName;
    }

    public gamePack.ApiGame getGame() {
        return game;
    }

    String getBoardSize() {
        return String.valueOf(loadGame.getBoarsSize());
    }

    String getBoardType() {
        return loadGame.getBoardType();
    }

    List<String> getPlayersNames() {
        return playersNames;
    }

    List<String> getViewersNames() {
        return viewersNames;
    }

    void addPlayerName(String playerName) {
        playersNames.add(playerName);
    }

    void addViewer(String viewer) {
        viewersNames.add(viewer);
    }

    void removeViewer(String viewer) {
        viewersNames.remove(viewer);
    }

    void makeSave() {
        playerOneSaved = false;
        playerTwoSaved = false;
    }

    void saved(PlayerTurn.Turn player){
        if(player == PlayerTurn.Turn.ONE)
            playerOneSaved = true;
        else
            playerTwoSaved = true;
    }

    boolean haveSaveToMake(PlayerTurn.Turn player){
        if((player == PlayerTurn.Turn.ONE && !playerOneSaved) ||
                (player == PlayerTurn.Turn.TWO && !playerTwoSaved))
            return true;
        return false;

    }

    void removePlayer(String player) {
        playersNames.remove(player);
        if(playersNames.size() == 0)
            chat.clear();
    }

    private void loadingGame(){
        game = new Game();
        loadGame.gameLoader(game);
    }

    void uploadGame() throws GameValidationException, LoadingException, PathIsNotXmlFileException {
        loadingFile();
        makeErrorChecking();
    }

    void play(){
        loadingGame();
    }

    private void loadingFile() throws PathIsNotXmlFileException, LoadingException {
        loadGame = new LoadGame();
        loadGame.generate(gameStream);
    }

    private void makeErrorChecking() throws GameValidationException {
        loadGame.validGame();
    }

}
