package servlets;

import com.google.gson.Gson;
import constant.Constants;
import gamePack.ApiGame;
import gamePack.PlayerTurn;
import managers.BoardManager;
import managers.Saver;
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

public class PlayerUpdateServlet extends HttpServlet {


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            ApiGame game = (ApiGame) request.getSession(true).getAttribute("game");
            String boardName = (String) request.getSession(true).getAttribute(Constants.NAME_BOARD);
            BoardManager manager = (BoardManager) request.getSession(true).getAttribute("manager");
            PlayerTurn.Turn player = (PlayerTurn.Turn) request.getSession(true).getAttribute("player");
            if(game != null) {
                char[][] subBoard = game.getSubMarinBoard(player);
                char[][] trackBoard = game.getTrackingBoard(player);
                String playerMines = String.valueOf(game.getAvailableAmountOfMines(player));
                List<String> players = manager.getPlayers(boardName);
                List<String> statistics = new LinkedList<>();
                boolean gameOnAir = false;
                try {
                    if (manager.getRegisteredPlayers(boardName) == 2) {
                        if(request.getSession(true).getAttribute("playerOne") == null)
                            request.getSession(true).setAttribute("playerOne", manager.getPlayers(boardName).get(0));
                        if(request.getSession(true).getAttribute("playerTwo") == null)
                            request.getSession(true).setAttribute("playerTwo", manager.getPlayers(boardName).get(1));
                        gameOnAir = true;
                        if (PlayerTurn.Turn.ONE == game.getTurn()) {
                            statistics.add(players.get(0));
                        } else {
                            statistics.add(players.get(1));
                        }
                    } else
                        statistics.add("waiting for a rival");
                } catch (NoSuchBoardException e) {
                    e.printStackTrace();
                }
                statistics.add(String.valueOf(game.getScoreNumber(PlayerTurn.Turn.ONE)));
                statistics.add(String.valueOf(game.getScoreNumber(PlayerTurn.Turn.TWO)));
                statistics.add(String.valueOf(game.getTurnsNumber()));
                statistics.add(String.valueOf(game.getElapsedTime()));

                List<String> playerInfo = new LinkedList<>();
                playerInfo.add(SessionUtils.getUserName(request));
                playerInfo.add(String.valueOf(game.getHitNumber(player)));
                playerInfo.add(String.valueOf(game.getScoreNumber(player)));
                playerInfo.add(String.valueOf(game.getAverageTimePerAttack(player)));

                List<String> viewers = manager.getBoardViewers(boardName);

                String subBoardJson = new Gson().toJson(subBoard);
                String trackBoardJson = new Gson().toJson(trackBoard);
                String playerMinesJson = new Gson().toJson(playerMines);
                String statJson = new Gson().toJson(statistics);
                String infoJson = new Gson().toJson(playerInfo);
                String viewersJson = new Gson().toJson(viewers);
                String playersJson = new Gson().toJson(players);

                boolean gameStarted = false;
                if (game.haveWinner()) {
                    if (game.isGameStarted())
                        gameStarted = true;
                    String startJson = new Gson().toJson(gameStarted);
                    String allJson = "[" + subBoardJson + "," + trackBoardJson +
                            "," + playerMinesJson + "," + statJson +
                            "," + infoJson + "," + playersJson  +
                            "," + viewersJson + "," + startJson + "]";
                    if(gameOnAir)
                        makeSaveAdjustments(request, allJson, manager, player, boardName);
                    manager.removePlayer( boardName,   SessionUtils.getUserName(request));
                    out.println(allJson);
                    out.flush();
                } else {
                    String allJson = "[" + subBoardJson + "," + trackBoardJson +
                            "," + playerMinesJson + "," + statJson + "," + infoJson + "," + playersJson + "," + viewersJson + "]";
                    if(gameOnAir)
                        makeSaveAdjustments(request, allJson, manager, player, boardName);
                    out.println(allJson);
                    out.flush();
                }
            }
        }
    }

    private void makeSaveAdjustments(HttpServletRequest request, String allJson, BoardManager manager, PlayerTurn.Turn player, String boardName) {
        if(manager.haveSaveToMake(boardName, player)){
            Saver saver = (Saver) request.getSession(true).getAttribute("saver");
            saver.addPage(allJson);
            manager.saved(boardName, player);
        }else if((boolean) request.getSession(true).getAttribute("save")){
            Saver saver = (Saver) request.getSession(true).getAttribute("saver");
            saver.addPage(allJson);
            request.getSession(true).setAttribute("save", false);
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
