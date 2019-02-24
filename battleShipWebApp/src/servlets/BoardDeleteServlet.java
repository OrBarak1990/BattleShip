package servlets;
import com.google.gson.Gson;
import constant.Constants;
import managers.BoardManager;
import utils.NoSuchBoardException;
import utils.ServletUtils;
import utils.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@MultipartConfig
public class BoardDeleteServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BoardManager boardManager = ServletUtils.getBoardManager(getServletContext());
        String boardName = request.getParameter(Constants.BOARD_DELETE);
        String user = "";
        try {
            user = boardManager.getBoardUser(boardName);
        } catch (NoSuchBoardException e) {
            e.printStackTrace();
        }
        try {
            if(user.equals(SessionUtils.getUserName(request)) && boardManager.getRegisteredPlayers(boardName) == 0){
                response.setContentType("text/html;charset=UTF-8");
                boardManager.removeBoard(boardName);
            }else {
                try (PrintWriter out = response.getWriter()) {
                    response.setContentType("application/json");
                    String json = new Gson().toJson("you are not authorize to delete this board");
                    out.println(json);
                    out.flush();
                }
            }
        } catch (NoSuchBoardException e) {
            e.printStackTrace();
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
