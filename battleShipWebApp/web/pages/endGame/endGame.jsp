<%--
  Created by IntelliJ IDEA.
  User: or
  Date: 22/10/2017
  Time: 17:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <%@ page import="constant.Constants" %>
    <%@ page import="managers.BoardManager" %>
    <%@ page import="gamePack.ApiGame" %>
    <%@ page import="gamePack.PlayerTurn" %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>endGame</title>
        <link rel="stylesheet" href="../../common/bootstrap.min.css"/>
        <link rel="stylesheet" href="endGame.css"/>
        <script src="../../common/jquery-2.0.3.min.js"></script>
        <script src="../../common/context-path-helper.js"></script>
        <script src="endGame.js"></script>
    </head>
    <body class="gamePage">
        <div class="container">
            <%ApiGame game = (ApiGame) request.getSession(true).getAttribute("game");
                String userName = (String) request.getSession(true).getAttribute(Constants.USERNAME);
                String playerOne = (String) request.getSession(true).getAttribute("playerOne");
                String playerTwo = (String) request.getSession(true).getAttribute("playerTwo");
                PlayerTurn.Turn winner, looser;
            String winnerName, looserName;
                if(game.getWinPlayer().getTurn() == PlayerTurn.Turn.ONE) {
                    winner = PlayerTurn.Turn.ONE;
                    looser = PlayerTurn.Turn.TWO;
                    winnerName = playerOne;
                    looserName = playerTwo;
                }else{
                    winner = PlayerTurn.Turn.TWO;
                    looser = PlayerTurn.Turn.ONE;
                    winnerName = playerTwo;
                    looserName = playerOne;
                }
            %>
            <h1 align="center"><%=winnerName%> win the game</h1>
            <%if(game.isPlayerQuit())%>
                <h2 align="center"><%= looserName + " quit from game"%></h2>
            <h2 align="left"><%=winnerName%> score: <%=game.getScoreNumber(winner)%></h2>
            <h2 align="left"><%=looserName%> score: <%=game.getScoreNumber(looser)%></h2>
        </div>
        <div id="replayEndOptions">
            <%if(userName.equals(winnerName) || userName.equals(looserName)) {%>
                    <div class="row-md-4 gameInfo" id="replayOptions">
                    <h3 align="center">Replay</h3>
                    <div class="buttonMargin" align="center" id="replay"><button>Go To Replay</button></div>
                    </div>
            <%}%>
        </div>
        <div class="container" id = "options">
            <a href="../lobby/lobby.html" class="btn btn-info btn-lg active" role="button">back to lobby</a>
        </div>
    </body>
</html>
