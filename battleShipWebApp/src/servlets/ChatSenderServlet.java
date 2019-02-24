package servlets;

import constant.Constants;
import managers.ChatManager;
import utils.NoSuchBoardException;
import utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ChatSenderServlet extends HttpServlet{
    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        String userName = (String) request.getSession(true).getAttribute(Constants.USERNAME);
        String boardName = (String) request.getSession(true).getAttribute(Constants.NAME_BOARD);
        String text = request.getParameter(Constants.CHAT_PARAMETER);
        ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
        if(text != null && !text.isEmpty()){
            try {
                chatManager.addChatString(boardName, text, userName);
            } catch (NoSuchBoardException e) {
                e.printStackTrace();
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
}
