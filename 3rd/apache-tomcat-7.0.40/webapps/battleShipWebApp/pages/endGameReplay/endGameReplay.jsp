<%--
  Created by IntelliJ IDEA.
  User: or
  Date: 28/10/2017
  Time: 15:33
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<%@ page import="managers.BoardManager" %>
<%@ page import="gamePack.ApiGame" %>
<%@ page import="gamePack.PlayerTurn" %>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>GameReplay</title>
    <link rel="stylesheet" href="../../common/bootstrap.min.css"/>
    <link rel="stylesheet" href="../game/game.css"/>
    <%--<link rel="stylesheet" href="skinTwo.css"/>--%>
    <script src="../../common/jquery-2.0.3.min.js"></script>
    <script src="../../common/context-path-helper.js"></script>
    <script src="endGameReplay.js"></script>
</head>
<body class="gamePage">
<div class="container">
    <%ApiGame game = (ApiGame) request.getSession(true).getAttribute("game");
        String boardName = (String) request.getSession(true).getAttribute("boardName");
        BoardManager manager = (BoardManager) request.getSession(true).getAttribute("manager");
        PlayerTurn.Turn player = (PlayerTurn.Turn) request.getSession(true).getAttribute("player");%>
    <h1 align="center">Game: <%=boardName%></h1>
    <div class="col-md-2">
        <div class="row-md-4 gameInfo" id="stat">
            <h3 align="center">Statistics</h3>
            <div id="turn" class="gameNotStart">Turn:</div>
            <div class="label">Game Type: <%out.println(manager.getBoardType(boardName));%></div>
            <div id="scoreOne" class="label">></div>
            <br/>
            <div id="scoreTwo" class="label"></div>
            <div id="turnNumber" class="label"></div>
            <div id="playTime" class="label"></div>
        </div>
        <div class="sidebar">
            <h4>Players</h4>
            <div class="clearfix">
                <div class="input">
                    <ul id="playersNames">
                    </ul>
                </div>
            </div>
            <h4>Viewers</h4>
            <div class="clearfix">
                <div class="input">
                    <ul id="viewersNames">
                    </ul>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-8" id="tables">
        <div id="replayMassage"></div>
        <div id="myTurnMassage"></div>
        <div class="row-md-6">
            <div class="col-md-6" id="subDiv">
                <table oncontextmenu="return false " id="sub" class="gameBoard">
                    <%char[][] subBoard = game.getSubMarinBoard(player);
                    for(int row = 0; row < subBoard.length; ++row){%>
                    <tr id= "s<%=row%>"></tr>
                    <%}%>
                </table>
            </div>
            <div class="col-md-6" id="tackingDiv">
                <table oncontextmenu="return false " id="track" class="gameBoard">
                    <%char[][] trackBoard = game.getTrackingBoard(player);
                        for(int row = 0; row < trackBoard.length; ++row){%>
                    <tr id="t<%=row%>"></tr>
                    <%}%>
                </table>
                <div class="bg-danger" id="chooseError"></div>
            </div>
        </div>
        <div id="minePlace" class="row-md-6">
            <div class="col-md-6">
                <br/>
                <table oncontextmenu="return false " id="mine">
                    <tr id="rowMine">
                    </tr>
                </table>
            </div>
        </div>
    </div>
    <div class="col-md-2" id="info">
        <div class="row-md-4 gameInfo" id="playerInfo">
            <h3 align="center">PlayerInfo</h3>
            <div id="playerPos"></div>
            <div id="hits"></div>
            <div id="score"></div>
            <div id="averagePlayTime"></div>
        </div>
        <div class="row-md-4 gameInfo" id="replayOptions">
            <h3 align="center">Replay Options</h3>
            <div class="buttonMargin" align="center" id="next"><button>Next</button></div>
            <div class="buttonMargin" align="center" id="prev"><button>Prev</button></div>
            <div class="buttonMargin" align="center" id="quitReplay"><button>Back To Lobby</button></div>
        </div>
        <div id="replayError"></div>
    </div>
</div>
</body>
</html>

