package servlets;

import com.google.gson.Gson;
import constant.Constants;
import gamePack.ApiGame;
import gamePack.PlayerTurn;
import managers.BoardManager;
import utils.NoSuchBoardException;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ViewerRegisterServlet extends HttpServlet {
    private final String GAME_URL = "/pages/game/game.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BoardManager boardManager = ServletUtils.getBoardManager(getServletContext());
        String boardName = request.getParameter("boardToRegister");
        int numOfReg = 0;
        try {
            numOfReg = boardManager.getRegisteredPlayers(boardName);
        } catch (NoSuchBoardException e) {
            e.printStackTrace();
        }
        ApiGame game = null;
        try {
            if (numOfReg == 0 && boardManager.getBoardViewers(boardName).size() == 0)
                boardManager.play(boardName);
            game = boardManager.getGame(boardName);
            request.getSession(true).setAttribute("game", game);
            request.getSession(true).setAttribute("boardName", boardName);
            request.getSession(true).setAttribute("manager", boardManager);
            request.getSession(true).setAttribute("quitView", false);
            request.getSession(true).setAttribute("playerOne", null);
            request.getSession(true).setAttribute("playerTwo", null);
            boardManager.addViewer(boardName, SessionUtils.getUserName(request));
        } catch (NoSuchBoardException e) {
            e.printStackTrace();
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