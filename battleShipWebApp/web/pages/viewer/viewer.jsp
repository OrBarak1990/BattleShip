<%--
  Created by IntelliJ IDEA.
  User: or
  Date: 26/10/2017
  Time: 12:02
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<%@ page import="constant.Constants" %>
<%@ page import="managers.BoardManager" %>
<%@ page import="utils.ServletUtils" %>
<%@ page import="gamePack.ApiGame" %>
<%@ page import="gamePack.PlayerTurn" %>
<%@ page import="utils.NoSuchBoardException" %>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Game</title>
    <link rel="stylesheet" href="../../common/bootstrap.min.css"/>
    <link rel="stylesheet" href="../game/game.css"/>
    <link rel="stylesheet" href="viewer.css"/>
    <%--<link rel="stylesheet" href="skinTwo.css"/>--%>
    <script src="../../common/jquery-2.0.3.min.js"></script>
    <script src="../../common/context-path-helper.js"></script>
    <script src="viewer.js"></script>
</head>
<body class="gamePage">
<div class="container">
    <%ApiGame game = (ApiGame) request.getSession(true).getAttribute("game");
        String boardName = (String) request.getSession(true).getAttribute("boardName");
        BoardManager manager = (BoardManager) request.getSession(true).getAttribute("manager");
        final String MINE = "mine", SHIP_HIT = "shipHit", SHIP_MISSED = "shipMissed", SHIP_CELL = "shipCell";%>
    <h1 align="center">Game: <%=boardName%></h1>
    <div class="col-md-2">
        <h3 align="center">registerPlayers</h3>
        <div class="row-md-4 gameInfo" id="playersList"></div>
        <h3 align="center">viewers</h3>
        <div class="row-md-4 gameInfo" id="viewersList"></div>
    </div>
    <div class="col-md-8 allBoards">
        <div id="PlayerOneTables">
            <h2 id="playerOneName"></h2>
            <table oncontextmenu="return false " class="gameBoard table-game">
                <%String cellClassOne;
                    char[][] subBoardOne = game.getSubMarinBoard(PlayerTurn.Turn.ONE);
                    for(int row = 0; row < subBoardOne.length; ++row){%>
                <tr id= "sOne<%=row%>">
                    <%for(int col = 0; col < subBoardOne.length; ++col){
                        if(subBoardOne[row][col] == 'X')
                            cellClassOne = SHIP_HIT;
                        else if(subBoardOne[row][col] == 'O')
                            cellClassOne = SHIP_MISSED;
                        else if(subBoardOne[row][col] == '@')
                            cellClassOne = MINE;
                        else if(subBoardOne[row][col] == '#')
                            cellClassOne = SHIP_CELL;
                        else
                            cellClassOne = "emptyCell";%>
                    <td class= "<%=cellClassOne%>"></td>
                    <%}%>
                </tr>
                <%}%>
            </table>
            <table oncontextmenu="return false " class="gameBoard ">
                <%char[][] trackBoardOne = game.getTrackingBoard(PlayerTurn.Turn.ONE);
                    for(int row = 0; row < trackBoardOne.length; ++row){%>
                <tr id="tOne<%=row%>">
                    <%for(int col = 0; col < trackBoardOne.length; ++col){
                        if(trackBoardOne[row][col] == 'X')
                            cellClassOne = SHIP_HIT;
                        else if(trackBoardOne[row][col] == 'O')
                            cellClassOne = SHIP_MISSED;
                        else
                            cellClassOne = "";%>
                    <td class= "<%=cellClassOne%>" row = "<%=row%>" col = "<%=col%>"></td>
                    <%}%>
                </tr>
                <%}%>
            </table>
            <div id="minePlaceOne" class="col-md-6 mine-game">
                <br/>
                <table oncontextmenu="return false ">
                    <tr id="mOne">
                        <%int mineNumberOne = game.getAvailableAmountOfMines(PlayerTurn.Turn.ONE);
                            for(int i = 0; i < mineNumberOne; ++i){%>
                        <td class= <%=MINE%>></td>
                        <%}%>
                    </tr>
                </table>
            </div>
            <br/><br/><br/>
        </div>
        <div id="PlayerTwoTables">
            <br/>
            <div><h2 id="playerTwoName"></h2></div>
            <table oncontextmenu="return false " class="gameBoard table-game">
                <%String cellClassTwo;
                    char[][] subBoardTwo = game.getSubMarinBoard(PlayerTurn.Turn.ONE);
                    for(int row = 0; row < subBoardTwo.length; ++row){%>
                <tr id= "sTwo<%=row%>">
                    <%for(int col = 0; col < subBoardTwo.length; ++col){
                        if(subBoardTwo[row][col] == 'X')
                            cellClassTwo = SHIP_HIT;
                        else if(subBoardTwo[row][col] == 'O')
                            cellClassTwo = SHIP_MISSED;
                        else if(subBoardTwo[row][col] == '@')
                            cellClassTwo = MINE;
                        else if(subBoardTwo[row][col] == '#')
                            cellClassTwo = SHIP_CELL;
                        else
                            cellClassTwo = "emptyCell";%>
                    <td class= "<%=cellClassTwo%>"></td>
                    <%}%>
                </tr>
                <%}%>
            </table>
            <table oncontextmenu="return false " class="gameBoard ">
                <%char[][] trackBoardTwo = game.getTrackingBoard(PlayerTurn.Turn.ONE);
                    for(int row = 0; row < trackBoardTwo.length; ++row){%>
                <tr id="tTwo<%=row%>">
                    <%for(int col = 0; col < trackBoardTwo.length; ++col){
                        if(trackBoardTwo[row][col] == 'X')
                            cellClassTwo = SHIP_HIT;
                        else if(trackBoardTwo[row][col] == 'O')
                            cellClassTwo = SHIP_MISSED;
                        else
                            cellClassTwo = "";%>
                    <td class= "<%=cellClassTwo%>" row = "<%=row%>" col = "<%=col%>"></td>
                    <%}%>
                </tr>
                <%}%>
            </table>
            <div id="minePlaceTwo" class="col-md-6 mine-game">
                <br/>
                <table oncontextmenu="return false ">
                    <tr id="mTwo">
                        <%int mineNumberTwo = game.getAvailableAmountOfMines(PlayerTurn.Turn.TWO);
                            for(int i = 0; i < mineNumberTwo; ++i){%>
                        <td class= <%=MINE%>></td>
                        <%}%>
                    </tr>
                </table>
            </div>
        </div>
    </div>
    <div class="col-md-2" id="info">
        <div class="row-md-4 gameInfo" id="viewerStat">
            <h3 align="center">Statistics</h3>
            <div id="turn" class="gameNotStart">Turn: <%
                try {
                    if(manager.getRegisteredPlayers(boardName) == 2){
                        if(PlayerTurn.Turn.ONE == game.getTurn()) {
                            out.println("player one:" + manager.getPlayers(boardName).get(0));
                        } else {
                            out.println("player two:" + manager.getPlayers(boardName).get(1));
                        }
                    }
                    else
                        out.println("game not started yet");
                } catch (NoSuchBoardException e) {
                    e.printStackTrace();
                }%>
            </div>
            <div class="label">Game Type: <%out.println(manager.getBoardType(boardName));%></div>
            <div id="scoreOne" class="label">player one score: <%out.println(game.getScoreNumber(PlayerTurn.Turn.ONE));%></div>
            <br/>
            <div id="scoreTwo" class="label">player two score: <%out.println(game.getScoreNumber(PlayerTurn.Turn.TWO));%></div>
            <div id="turnNumber" class="label">turn number: <%out.println(game.getTurnsNumber());%></div>
            <div id="playTime" class="label">playing time: <%out.println(game.getElapsedTime());%></div>
        </div>
        <div class="row-md-4 gameInfo" id="viewerOptions">
            <h3 align="center">Options</h3>
            <div class="buttonMargin" align="center" id="quitGame"><button>Quit Game</button></div>
        </div>
    </div>
</div>
</body>
</html>
