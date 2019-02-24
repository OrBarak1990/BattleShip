package servlets;

import com.google.gson.Gson;
import constant.Constants;
import gamePack.ApiGame;
import managers.Saver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

public class SaveBoardServlet extends HttpServlet {
    private final static String BEGIN = "begin", NEXT = "next", PREV = "prev", END = "end";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        ApiGame game = (ApiGame) request.getSession(true).getAttribute(Constants.GAME);
        Saver saver = (Saver) request.getSession(true).getAttribute("saver");
        String page = request.getParameter("load");
        String json;
        if(!game.isGameStarted() || !game.isFirstMoveDone()){
            json = new Gson().toJson("no replay to show");
        }
        else if (page.equals(BEGIN)) {
            json = saver.getFirstPage();
        } else if (page.equals(END)) {
            json = saver.getLastPage();
        } else if (page.equals(PREV)) {
            try {
                json = saver.getPrevPage();

            } catch (NoSuchElementException e) {
                json = new Gson().toJson("this is the start of record");
            }
        } else{
            try {
                json = saver.getNextPage();
            } catch (NoSuchElementException e) {
                json = new Gson().toJson("this is the end of record");
            }
        }
        try (PrintWriter out = response.getWriter()) {
            out.println(json);
            out.flush();
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