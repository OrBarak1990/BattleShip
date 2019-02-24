/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: Apache Tomcat/7.0.40
 * Generated at: 2017-01-27 22:39:17 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp.pages.loginerror;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import chat.utils.*;
import chat.constants.Constants;

public final class login_005fattempt_005fafter_005ferror_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final javax.servlet.jsp.JspFactory _jspxFactory =
          javax.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
  }

  public void _jspDestroy() {
  }

  public void _jspService(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)
        throws java.io.IOException, javax.servlet.ServletException {

    final javax.servlet.jsp.PageContext pageContext;
    javax.servlet.http.HttpSession session = null;
    final javax.servlet.ServletContext application;
    final javax.servlet.ServletConfig config;
    javax.servlet.jsp.JspWriter out = null;
    final java.lang.Object page = this;
    javax.servlet.jsp.JspWriter _jspx_out = null;
    javax.servlet.jsp.PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE html>\n");
      out.write("<html>\n");
      out.write("    \n");
      out.write("    \n");
      out.write("    <head>\n");
      out.write("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
      out.write("        <title>Online Chat</title>\n");
      out.write("<!--        Link the Bootstrap (from twitter) CSS framework in order to use its classes-->\n");
      out.write("        <link rel=\"stylesheet\" href=\"../../common/bootstrap.min.css\"/>\n");
      out.write("<!--        Link jQuery JavaScript library in order to use the $ (jQuery) method-->\n");
      out.write("<!--        <script src=\"script/jquery-2.0.3.min.js\"></script>-->\n");
      out.write("<!--        and\\or any other scripts you might need to operate the JSP file behind the scene once it arrives to the client-->\n");
      out.write("    </head>\n");
      out.write("    <body>\n");
      out.write("        <div class=\"container\">\n");
      out.write("            ");
 String usernameFromSession = SessionUtils.getUsername(request);
      out.write("\n");
      out.write("            ");
 String usernameFromParameter = request.getParameter(Constants.USERNAME) != null ? request.getParameter(Constants.USERNAME) : "";
      out.write("\n");
      out.write("            ");
 if (usernameFromSession == null) {
      out.write("\n");
      out.write("            <h1>Welcome to the Online Chat</h1>\n");
      out.write("            <br/>\n");
      out.write("            <h2>Please enter a unique user name:</h2>\n");
      out.write("            <form method=\"GET\" action=\"login\">\n");
      out.write("                <input type=\"text\" name=\"");
      out.print(Constants.USERNAME);
      out.write("\" value=\"");
      out.print(usernameFromParameter);
      out.write("\"/>\n");
      out.write("                <input type=\"submit\" value=\"Login\"/>\n");
      out.write("            </form>\n");
      out.write("            ");
 Object errorMessage = request.getAttribute(Constants.USER_NAME_ERROR);
      out.write("\n");
      out.write("            ");
 if (errorMessage != null) {
      out.write("\n");
      out.write("            <span class=\"bg-danger\" style=\"color:red;\">");
      out.print(errorMessage);
      out.write("</span>\n");
      out.write("            ");
 } 
      out.write("\n");
      out.write("            ");
 } else {
      out.write("\n");
      out.write("            <h1>Welcome back, ");
      out.print(usernameFromSession);
      out.write("</h1>\n");
      out.write("            <a href=\"../chatroom/chatroom.html\">Click here to enter the chat room</a>\n");
      out.write("            <br/>\n");
      out.write("            <a href=\"login?logout=true\" id=\"logout\">logout</a>\n");
      out.write("            ");
 }
      out.write("\n");
      out.write("        </div>\n");
      out.write("    </body>\n");
      out.write("</html>");
    } catch (java.lang.Throwable t) {
      if (!(t instanceof javax.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
