package managers;

public class SingleChatEntry {
    private final String chatString;
    private final String username;

    public SingleChatEntry(String chatString, String username) {
        this.chatString = chatString;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return (username != null ? username + ": " : "") + chatString;
    }
}
