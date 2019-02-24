package servlets;

import com.google.gson.Gson;
import constant.Constants;
import managers.ChatManager;
import managers.SingleChatEntry;
import utils.NoSuchBoardException;
import utils.ServletUtils;
import utils.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ChatUpdateServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
        String boardName = (String) request.getSession(true).getAttribute(Constants.NAME_BOARD);
        int chatVersion = Integer.parseInt(request.getParameter(Constants.CHAT_VERSION_PARAMETER));
        if (chatVersion > Constants.INT_PARAMETER_ERROR) {
            try (PrintWriter out = response.getWriter()) {
                List<SingleChatEntry> chatEntries = null;
                int newVersion;
                String versionJson;
                String chatEntriesJson;
                String jsonResponse;
                try {
                    chatEntries = chatManager.getChatEntries(chatVersion, boardName);
                    newVersion = chatManager.getVersion(boardName);
                    versionJson = new Gson().toJson(newVersion);
                    chatEntriesJson = new Gson().toJson(chatEntries);
                } catch (NoSuchBoardException e) {
                    versionJson = new Gson().toJson(0);
                    chatEntriesJson = new Gson().toJson("");
                    e.printStackTrace();
                }
                jsonResponse = "[" + versionJson + "," + chatEntriesJson + "]";
                out.print(jsonResponse);
                out.flush();
            } catch (IOException e) {
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