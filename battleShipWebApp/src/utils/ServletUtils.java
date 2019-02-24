package utils;
/*import engine.chat.ChatManager;
import engine.users.UserManager;*/
import managers.BoardManager;
import managers.ChatManager;
import managers.UserManager;

import javax.servlet.ServletContext;

//import static chat.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {


    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String BOARD_MANAGER_ATTRIBUTE_NAME = "boardManager";
    private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";

    public static UserManager getUserManager(ServletContext servletContext) {
        if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
            servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
        }
        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }

    public static BoardManager getBoardManager(ServletContext servletContext) {
        if (servletContext.getAttribute(BOARD_MANAGER_ATTRIBUTE_NAME) == null) {
            servletContext.setAttribute(BOARD_MANAGER_ATTRIBUTE_NAME, new BoardManager());
        }
        return (BoardManager) servletContext.getAttribute(BOARD_MANAGER_ATTRIBUTE_NAME);
    }

    public static ChatManager getChatManager(ServletContext servletContext) {
	if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
	    servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
	}
	return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
    }
}
