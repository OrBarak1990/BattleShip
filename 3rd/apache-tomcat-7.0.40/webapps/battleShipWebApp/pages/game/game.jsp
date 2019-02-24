<%--
  Created by IntelliJ IDEA.
  User: or
  Date: 16/10/2017
  Time: 17:43
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
        <link rel="stylesheet" href="game.css"/>
        <%--<link rel="stylesheet" href="skinTwo.css"/>--%>
        <script src="../../common/jquery-2.0.3.min.js"></script>
        <script src="../../common/context-path-helper.js"></script>
        <script src="game.js"></script>
    </head>
    <body class="gamePage">
        <div class="container">
            <%ApiGame game = (ApiGame) request.getSession(true).getAttribute("game");
            String boardName = (String) request.getSession(true).getAttribute("boardName");
            BoardManager manager = (BoardManager) request.getSession(true).getAttribute("manager");
            PlayerTurn.Turn player = (PlayerTurn.Turn) request.getSession(true).getAttribute("player");
            final String MINE = "mine", SHIP_HIT = "shipHit", SHIP_MISSED = "shipMissed", SHIP_CELL = "shipCell";%>
            <h1 align="center">Game: <%=boardName%></h1>
            <div class="col-md-2">
                <div class="row-md-4 gameInfo" id="stat">
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
                                out.println("waiting for a rival");
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
                <div id="chat">
                    <div class="content">
                        <br/>
                        <h3 align="center">Chat</h3>
                        <div id="chatWindow"></div>
                        <form id="chatform" method="POST" action="sendChat">
                            <input type="text" id="userstring" name="userstring"/>
                            <input type="submit" value="Send"/>
                        </form>
                    </div>
                </div>
            </div>
            <div class="col-md-8" id="tables">
                <div id="replayMassage"></div>
                <div id="myTurnMassage"></div>
                <div class="row-md-6">
                    <div class="col-md-6" id="subDiv">
                        <table oncontextmenu="return false " id="sub" class="gameBoard">
                            <%String cellClass;
                            char[][] subBoard = game.getSubMarinBoard(player);
                            for(int row = 0; row < subBoard.length; ++row){%>
                                <tr id= "s<%=row%>">
                                <%for(int col = 0; col < subBoard.length; ++col){
                                    if(subBoard[row][col] == 'X')
                                        cellClass = SHIP_HIT;
                                    else if(subBoard[row][col] == 'O')
                                        cellClass = SHIP_MISSED;
                                    else if(subBoard[row][col] == '@')
                                        cellClass = MINE;
                                    else if(subBoard[row][col] == '#')
                                        cellClass = SHIP_CELL;
                                    else
                                        cellClass = "emptyCell";%>
                                    <td class= "<%=cellClass%>"></td>
                                <%}%>
                                </tr>
                            <%}%>
                        </table>
                    </div>
                    <div class="col-md-6" id="tackingDiv">
                        <table oncontextmenu="return false " id="track" class="gameBoard">
                            <%char[][] trackBoard = game.getTrackingBoard(player);
                            for(int row = 0; row < trackBoard.length; ++row){%>
                            <tr id="t<%=row%>">
                                <%for(int col = 0; col < trackBoard.length; ++col){
                                    if(trackBoard[row][col] == 'X')
                                        cellClass = SHIP_HIT;
                                    else if(trackBoard[row][col] == 'O')
                                        cellClass = SHIP_MISSED;
                                    else
                                        cellClass = "";%>
                                <td class= "<%=cellClass%>" row = "<%=row%>" col = "<%=col%>"></td>
                                <%}%>
                            </tr>
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
                                <%int mineNumber = game.getAvailableAmountOfMines(player);
                                    for(int i = 0; i < mineNumber; ++i){%>
                                <td class= <%=MINE%> id="m<%=i%>" draggable = true ></td>
                                <%}%>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
            <div class="col-md-2" id="info">
                <div class="row-md-4 gameInfo" id="playerInfo">
                    <h3 align="center">PlayerInfo</h3>
                    <div id="playerPos">Player: <%out.println(player.toString());%></div>
                    <div id="hits">Hits: <%out.println(game.getHitNumber(player));%></div>
                    <div id="score">Score: <%out.println(game.getScoreNumber(player));%></div>
                    <div id="averagePlayTime">Average playing time: <%out.println(game.getAverageTimePerAttack(player));%></div>
                </div>
                <div class="row-md-4 gameInfo" id="options">
                    <h3 align="center">Options</h3>
                    <div class="buttonMargin" align="center" id="attack"><button>Attack</button></div>
                    <div class="buttonMargin" align="center" id="placeMine"><button>Place Mine</button></div>
                    <div class="buttonMargin" align="center" id="quitGame"><button>Quit Game</button></div>
                    <div class="buttonMargin" align="center" id="replayFromBegin"><button>Replay From Start</button></div>
                    <div class="buttonMargin" align="center" id="replayFromEnd"><button>Replay From End</button></div>
                </div>
                <div class="row-md-4 gameInfo" id="replayOptions">
                    <h3 align="center">Replay Options</h3>
                    <div class="buttonMargin" align="center" id="next"><button>Next</button></div>
                    <div class="buttonMargin" align="center" id="prev"><button>Prev</button></div>
                    <div class="buttonMargin" align="center" id="quitReplay"><button>Quit Repaly</button></div>
                </div>
                <div id="replayError"></div>
            </div>
        </div>
    </body>
</html>
