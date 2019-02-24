package managers;

import javafx.util.Pair;
import utils.NoSuchBoardException;
import java.util.ArrayList;
import java.util.List;

public class ChatManager {
    private final List<Pair<String,List<SingleChatEntry>>> chatDataList;

    public ChatManager() {
        chatDataList = new ArrayList<>();
    }

    public void addChatString(String boardName, String chatString, String username) throws NoSuchBoardException {
        boolean found = false;
        for(Pair<String,List<SingleChatEntry>> pair : chatDataList){
            if(pair.getKey().equals(boardName)){
                pair.getValue().add(new SingleChatEntry(chatString, username));
                found = true;
                break;
            }
        }
        if(!found){
            throw new NoSuchBoardException();
        }
    }

    public List<SingleChatEntry> makeChatGame(String boardName){
        List<SingleChatEntry> entries = new ArrayList<>();
        chatDataList.add(new Pair(boardName, entries));
        return entries;
    }

    public List<SingleChatEntry> getChatEntries(int fromIndex, String boardName) throws NoSuchBoardException {
        int size = this.getVersion(boardName);
        if (fromIndex < 0 || fromIndex >= size) {
            fromIndex = 0;
        }
        for(Pair<String,List<SingleChatEntry>> pair : chatDataList){
            if(pair.getKey().equals(boardName)){
                return pair.getValue().subList(fromIndex, pair.getValue().size());
            }
        }
        throw new NoSuchBoardException();
    }

    public int getVersion(String boardName) throws NoSuchBoardException {
        for(Pair<String,List<SingleChatEntry>> pair : chatDataList){
            if(pair.getKey().equals(boardName)){
                return pair.getValue().size();
            }
        }
        throw new NoSuchBoardException();
    }
}
