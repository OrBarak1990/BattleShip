package servlets;

import com.google.gson.Gson;
import constant.Constants;
import loader.GameValidationException;
import loader.LoadingException;
import loader.PathIsNotXmlFileException;
import managers.BoardManager;
import managers.ChatManager;
import managers.SingleChatEntry;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

@MultipartConfig
public class BoardUploadServlet  extends HttpServlet {
    private final String LOBBY_URL = "/pages/lobby.html";
    private final String SIGN_UP_URL = "/pages/login.html";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        BoardManager boardManager = ServletUtils.getBoardManager(getServletContext());
        String boardName = request.getParameter(Constants.BOARD_NAME);
        if(request.getSession(true).getAttribute(Constants.USERNAME).equals("null")){
            response.sendRedirect(SIGN_UP_URL);
        }
        if (boardName == null) {
            response.sendRedirect(LOBBY_URL);
        } else {
            boardName = boardName.trim();
            if (boardManager.isBoardExists(boardName)) {
                String errorMessage = "BoardName " + boardName + " already exists. Please enter a different board name.";
                String json = new Gson().toJson(errorMessage);
                PrintWriter out = response.getWriter();
                out.println(json);
                out.flush();
            } else {
                InputStream gameStream = SessionUtils.getBoardFile(request);
                try {

                    ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
                    List<SingleChatEntry> chat = chatManager.makeChatGame(boardName);
                    boardManager.addBoard(gameStream, boardName, SessionUtils.getUserName(request), chat);
                    request.getSession(true).setAttribute(Constants.BOARD_NAME, boardName);
                    System.out.println("On login, request URI is: " + request.getRequestURI());
                } catch (GameValidationException | LoadingException | PathIsNotXmlFileException e) {
                    String json = new Gson().toJson(e.getMessage());
                    PrintWriter out = response.getWriter();
                    out.println(json);
                    out.flush();
                }
            }
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}