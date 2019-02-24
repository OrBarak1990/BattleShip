package servlets;

import com.google.gson.Gson;
import constant.Constants;
import gamePack.ApiGame;
import gamePack.PlayerTurn;
import managers.BoardManager;
import utils.NoSuchBoardException;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class ViewerUpdateServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            ApiGame game = (ApiGame) request.getSession(true).getAttribute(Constants.GAME);
            String boardName = (String) request.getSession(true).getAttribute(Constants.NAME_BOARD);
            BoardManager manager = (BoardManager) request.getSession(true).getAttribute("manager");
            if (game != null) {
                if((boolean)request.getSession(true).getAttribute("quitView")){
                    String startJson = new Gson().toJson(false);
                    out.println(startJson);
                    out.flush();
                }else {
                    char[][] playerOneSubBoard = game.getSubMarinBoard(PlayerTurn.Turn.ONE);
                    char[][] playerTwoSubBoard = game.getSubMarinBoard(PlayerTurn.Turn.TWO);
                    char[][] playerOneTrackBoard = game.getTrackingBoard(PlayerTurn.Turn.ONE);
                    char[][] playerTwoTrackBoard = game.getTrackingBoard(PlayerTurn.Turn.TWO);
                    String playerOneMines = String.valueOf(game.getAvailableAmountOfMines(PlayerTurn.Turn.ONE));
                    String playerTwoMines = String.valueOf(game.getAvailableAmountOfMines(PlayerTurn.Turn.TWO));
                    List<String> statistics = new LinkedList<>();
                    try {
                        if (manager.getRegisteredPlayers(boardName) == 2) {
                            if(request.getSession(true).getAttribute("playerOne") == null)
                                request.getSession(true).setAttribute("playerOne", manager.getPlayers(boardName).get(0));
                            if(request.getSession(true).getAttribute("playerTwo") == null)
                                request.getSession(true).setAttribute("playerTwo", manager.getPlayers(boardName).get(1));
                            if (PlayerTurn.Turn.ONE == game.getTurn()) {
                                statistics.add(manager.getPlayers(boardName).get(0));
                            } else {
                                statistics.add(manager.getPlayers(boardName).get(1));
                            }
                        } else
                            statistics.add("game not started yet");
                    } catch (NoSuchBoardException e) {
                        e.printStackTrace();
                    }
                    statistics.add(String.valueOf(game.getScoreNumber(PlayerTurn.Turn.ONE)));
                    statistics.add(String.valueOf(game.getScoreNumber(PlayerTurn.Turn.TWO)));
                    statistics.add(String.valueOf(game.getTurnsNumber()));
                    statistics.add(String.valueOf(game.getElapsedTime()));
                    List<String> viewers = manager.getBoardViewers(boardName);
                    List<String> players = manager.getPlayers(boardName);
                    String playerOneSubBoardJson = new Gson().toJson(playerOneSubBoard);
                    String playerTwoSubBoardJson = new Gson().toJson(playerTwoSubBoard);
                    String playerOneTrackBoardJson = new Gson().toJson(playerOneTrackBoard);
                    String playerTwoTrackBoardJson = new Gson().toJson(playerTwoTrackBoard);
                    String playerOneMinesJson = new Gson().toJson(playerOneMines);
                    String playerTwoMinesJson = new Gson().toJson(playerTwoMines);
                    String statJson = new Gson().toJson(statistics);
                    String viewersJson = new Gson().toJson(viewers);
                    String playersJson = new Gson().toJson(players);
                    boolean gameStarted = false;
                    if (game.haveWinner()) {
                        if (game.isGameStarted())
                            gameStarted = true;
                        manager.removeViewer(boardName,  SessionUtils.getUserName(request));
                        String startJson = new Gson().toJson(gameStarted);
                        String allJson = "[" + playerOneSubBoardJson + "," + playerOneTrackBoardJson +
                                "," + playerOneMinesJson + "," + playerTwoSubBoardJson +
                                "," + playerTwoTrackBoardJson + "," + playerTwoMinesJson +
                                "," + statJson + "," + playersJson + "," + viewersJson +
                                "," + startJson + "]";
                        out.println(allJson);
                        out.flush();
                    } else {
                        String allJson = "[" + playerOneSubBoardJson + "," + playerOneTrackBoardJson +
                                "," + playerOneMinesJson + "," + playerTwoSubBoardJson +
                                "," + playerTwoTrackBoardJson + "," + playerTwoMinesJson +
                                "," + statJson + "," + playersJson + "," + viewersJson + "]";
                        out.println(allJson);
                        out.flush();
                    }
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
