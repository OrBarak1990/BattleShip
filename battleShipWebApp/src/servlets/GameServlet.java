package servlets;

import com.google.gson.Gson;
import constant.Constants;
import gamePack.*;
import managers.BoardManager;
import utils.NoSuchBoardException;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class GameServlet extends HttpServlet {
    private ApiGame game;
    private List<Report> reports;
    private String errorMassage = "";

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        errorMassage = "";
        response.setContentType("text/html;charset=UTF-8");
        PlayerTurn.Turn player = (PlayerTurn.Turn) request.getSession(true).getAttribute("player");
        game = (ApiGame) request.getSession(true).getAttribute(Constants.GAME);
        String boardName = (String) request.getSession(true).getAttribute(Constants.NAME_BOARD);
        BoardManager manager = (BoardManager) request.getSession(true).getAttribute("manager");
        boolean quit = (Boolean.valueOf(request.getParameter("quit")));
        boolean viewQuit = (Boolean.valueOf(request.getParameter("viewQuit")));
        if(quit) {
            try {
                if (manager.getRegisteredPlayers(boardName) == 2 && player != game.getTurn())
                    errorResponse(response, "this is not your turn!");
                else
                    game.playerQuit();
            } catch (NoSuchBoardException e) {
                e.printStackTrace();
            }
        }else if(viewQuit){
            manager.removeViewer(boardName, SessionUtils.getUserName(request));
            try {
                if(manager.getRegisteredPlayers(boardName) == 0 && manager.getBoardViewers(boardName).size() == 0)
                    game.playerQuit();
                else{
                    request.getSession(true).setAttribute("quitView", true);
                }
            } catch (NoSuchBoardException e) {
                e.printStackTrace();
            }
        }else {
            try {
                if (!(manager.getRegisteredPlayers(boardName) == 2)) {
                    errorResponse(response, "waiting for a rival");
                } else {
                    if (player != game.getTurn()) {
                        errorResponse(response, "this is not your turn!");
                    } else {
                        String rowString = request.getParameter("row");
                        String colString = request.getParameter("col");
                        int row, col;
                        if (rowString != null && colString != null) {
                            row = Integer.parseInt(rowString);
                            col = Integer.parseInt(colString);
                            boolean attackMove = (Boolean.valueOf(request.getParameter("attack")));
                            if (attackMove) {
                                manager.makeSave(boardName);
                                attack(row, col);
                            }
                            else {
                                request.getSession(true).setAttribute("save", true);
                                placeMine(row, col);
                            }
                            if (!errorMassage.isEmpty()) {
                                errorResponse(response, errorMassage);
                            }
                        }
                    }
                }
            } catch (NoSuchBoardException e) {
                e.printStackTrace();
            }
        }
    }

    private void errorResponse(HttpServletResponse response, String errorMassage) {
        response.setContentType("application/json");
        String json = new Gson().toJson(errorMassage);
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.println(json);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void attack(int row, int col) {
        try {
            game.endClock();
            reports = game.attack(new Cell(row, col));
            if(reports.get(0).getResult() == ApiGame.Result.BEEN_ATTACKED)
                errorMassage = "you already make an attack on this square";
        } catch (WrongInputException e) {
            e.printStackTrace();
        }finally {
            game.startClock();
        }
    }

    private void placeMine(int row, int col) {
        try {
            game.endClock();
            reports = game.addMine(new Cell(row, col));
        } catch (InvalidSquarePositionException | TooManyMinesException e) {
            errorMassage = "Please choose valid square";
        }finally {
            game.startClock();
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
}
