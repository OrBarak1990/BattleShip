  <%--Created by IntelliJ IDEA.--%>
  <%--User: or--%>
  <%--Date: 23/10/2017--%>
  <%--Time: 18:41--%>
  <%--To change this template use File | Settings | File Templates.--%>
<%--&ndash;%&gt;--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <%@ page import="constant.Constants" %>
    <%@ page import="utils.SessionUtils" %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>BattleShipLoginError</title>
        <link rel="stylesheet" href="../../common/bootstrap.min.css">
        <link rel="stylesheet" href="login.css">
    </head>
    <body class="gamePage">
        <%response.setHeader("Cache-control" , "no-cache, no-store, must-revalidate");%>
        <div class="container">
            <% String usernameFromSession = SessionUtils.getUserName(request);
            String usernameFromParameter = request.getParameter(Constants.USERNAME) != null ? request.getParameter(Constants.USERNAME) : "";
            if (usernameFromSession == null) {%>
                <h1>Welcome to the BattleShip game</h1>
                <br/>
                <h2>Please enter a unique user name:</h2>
                <form method="GET" action="login">
                    <input type="text" name="username" class=""/>
                    <input type="submit" value="Login"/>
                </form>
                <% Object errorMessage = request.getAttribute(Constants.USER_NAME_ERROR);
                if (errorMessage != null)%>
                    <span class="bg-danger" style="color:red;"><%=errorMessage%></span>
            <%}else{%>
                <h1>Welcome back, <%=usernameFromSession%></h1>
                <a href="../lobby/lobby.html">Click here to enter the lobby</a>
            <%}%>
        </div>
    </body>
</html>
