package managers;
import gamePack.ApiGame;
import gamePack.PlayerTurn;
import loader.GameValidationException;
import loader.LoadingException;
import utils.NoSuchBoardException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BoardManager {

    private final Set<GameDetails> boardsSet;

    public BoardManager() {
        boardsSet = new HashSet<GameDetails>();
    }

    public void addBoard(InputStream board, String boardName, String user, List<SingleChatEntry> chat) throws GameValidationException, LoadingException, loader.PathIsNotXmlFileException {
        GameDetails game = new GameDetails(board, boardName, user, chat);
        game.uploadGame();
        boardsSet.add(game);
    }

    public void removeBoard(String boardName) {
        for(GameDetails gameDetails : boardsSet){
            if(gameDetails.getGameName().equals(boardName)) {
                boardsSet.remove(gameDetails);
                break;
            }
        }
    }

    public List<String> getBoardsNames() {
        List<String> names = new LinkedList<>();
        for(GameDetails gameDetails : boardsSet){
            names.add(gameDetails.getGameName());
        }
        return names;
    }

    public boolean isBoardExists(String boardName) {
        for(GameDetails gameDetails : boardsSet){
            if(gameDetails.getGameName().equals(boardName))
                return true;
        }
        return false;
    }

    public ApiGame getGame(String boardName) throws NoSuchBoardException {
        for(GameDetails gameDetails : boardsSet){
            if(gameDetails.getGameName().equals(boardName))
                return gameDetails.getGame();
        }
        throw  new NoSuchBoardException();
    }

    public int getRegisteredPlayers(String boardName) throws NoSuchBoardException {
        for (GameDetails gameDetails : boardsSet) {
            if (gameDetails.getGameName().equals(boardName))
                return gameDetails.getRegisteredPlayers();
        }
        throw new NoSuchBoardException();
    }

    public String getBoardUser(String boardName) throws NoSuchBoardException {
        for(GameDetails gameDetails : boardsSet){
            if(gameDetails.getGameName().equals(boardName))
                return gameDetails.getUploadUser();
        }
        throw  new NoSuchBoardException();
    }

    public List<String> getBoardsUsers() {
        List<String> users = new LinkedList<>();
        for(GameDetails gameDetails : boardsSet){
            users.add(gameDetails.getUploadUser());
        }
        return users;
    }

    public List<String> getBoardsSize() {
        List<String> sizes = new LinkedList<>();
        for(GameDetails gameDetails : boardsSet){
            sizes.add(gameDetails.getBoardSize());
        }
        return sizes;
    }

    public List<String> getBoardsType() {
        List<String> types = new LinkedList<>();
        for(GameDetails gameDetails : boardsSet){
            types.add(gameDetails.getBoardType());
        }
        return types;
    }

    public String getBoardType(String boardName) {
        for(GameDetails gameDetails : boardsSet){
            if (gameDetails.getGameName().equals(boardName))
                return gameDetails.getBoardType();
        }
        return null;
    }

    public List<String> getSignUpPlayers() {
        List<String> players = new LinkedList<>();
        for(GameDetails gameDetails : boardsSet){
            players.add(String.valueOf(gameDetails.getRegisteredPlayers()));
        }
        return players;
    }

    public void play(String boardName) {
        for (GameDetails gameDetails : boardsSet) {
            if (gameDetails.getGameName().equals(boardName))
                gameDetails.play();
        }
    }

    public List<String> getBoardViewers(String boardName) {
        for(GameDetails gameDetails : boardsSet){
            if (gameDetails.getGameName().equals(boardName))
                return gameDetails.getViewersNames();
        }
        return null;
    }

    public List<String> getViewers() {
        List<String> viewers = new LinkedList<>();
        for(GameDetails gameDetails : boardsSet){
            viewers.add(String.valueOf(gameDetails.getViewersNames().size()));
        }
        return viewers;
    }

    public List<String> getPlayers(String boardName) {
        for(GameDetails gameDetails : boardsSet){
            if (gameDetails.getGameName().equals(boardName))
                return gameDetails.getPlayersNames();
        }
        return null;
    }

    public void addPlayerName(String boardName, String playerName) {
        for(GameDetails gameDetails : boardsSet){
            if (gameDetails.getGameName().equals(boardName))
                gameDetails.addPlayerName(playerName);
        }
    }

    public void addViewer(String boardName, String viewer) {
        for(GameDetails gameDetails : boardsSet){
            if (gameDetails.getGameName().equals(boardName)) {
                gameDetails.addViewer(viewer);
            }
        }
    }

    public void removeViewer(String boardName, String viewer) {
        for(GameDetails gameDetails : boardsSet){
            if (gameDetails.getGameName().equals(boardName)) {
                gameDetails.removeViewer(viewer);
            }
        }
    }

    public void removePlayer(String boardName, String player) {
        for(GameDetails gameDetails : boardsSet){
            if (gameDetails.getGameName().equals(boardName)) {
                gameDetails.removePlayer(player);
            }
        }
    }

    public void makeSave(String boardName) {
        for(GameDetails gameDetails : boardsSet){
            if (gameDetails.getGameName().equals(boardName)) {
                gameDetails.makeSave();
            }
        }
    }

    public void saved(String boardName, PlayerTurn.Turn player) {
        for(GameDetails gameDetails : boardsSet){
            if (gameDetails.getGameName().equals(boardName)) {
                gameDetails.saved(player);
            }
        }
    }

    public boolean haveSaveToMake(String boardName, PlayerTurn.Turn player) {
        for(GameDetails gameDetails : boardsSet){
            if (gameDetails.getGameName().equals(boardName)) {
                return gameDetails.haveSaveToMake(player);
            }
        }
        return false;
    }
}
