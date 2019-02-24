var refreshRate = 2000;
var BOARD_GAME_URL = buildUrlWithContextPath("viewerUpdateServlet");
var GAME_URL = buildUrlWithContextPath("gameServlet");
var minePermission = false, attackPermission = false;
$(function () {
    setInterval(ajaxGameBoard, refreshRate);
});

function changeWindow(changeWindow) {
    if(changeWindow === true)
        window.location = '../endGame/endGame.jsp';
    else
        window.location = '../lobby/lobby.html';
}

function updatePlayerNames(playersNames) {
    $("#playersList").empty();
    var list;
    list = $('<ol type="I">');
    for(var i = 0; i < playersNames.length;++i){
        list.append($('<li>' + playersNames[i] + '</li>'));
    }
    list.append($('</ol>'));
    list.appendTo($("#playersList"));

    $("#playerOneName").empty();
    $("#playerTwoName").empty();
    if (playersNames.length === 0) {
        $("#playerOneName").text("Player One: " + "waiting for player");
        $("#playerTwoName").text("Player Two: " + "waiting for player");
    } else if (playersNames.length === 1) {
        $("#playerOneName").text("Player One: " + playersNames[0]);
        $("#playerTwoName").text("Player Two: " + "waiting for player");
    }else {
        $("#playerOneName").text("Player One: " + playersNames[0]);
        $("#playerTwoName").text("Player Two: " + playersNames[1]);
    }
}

function updateViewersNames(viewersNames) {
    $("#viewersList").empty();

    var list;
    list = $('<ol type="I">');
    for(var i = 0; i < viewersNames.length;++i){
        list.append($('<li>' + viewersNames[i] + '</li>'));
    }
    list.append($('</ol>'));
    list.appendTo($("#viewersList"));
}

function ajaxGameBoard() {
    $.ajax({
        url: BOARD_GAME_URL,
        timeout: 600000,
        cache: false,
        success: function (data) {
            if(data === false){
                setTimeout(changeWindow(data[0]), 100000);
            }
            else if(data.length === 10){
                updateSubBoard(data[0], 1);
                updateTrackBoard(data[1], 1);
                updateMines(data[2], 1);
                updateSubBoard(data[3], 2);
                updateTrackBoard(data[4], 2);
                updateMines(data[5], 2);
                updateStatistics(data[6], data[7]);
                updatePlayerNames(data[7]);
                updateViewersNames(data[8]);
                setTimeout(changeWindow(data[9]), 100000);
            }
            else if(data.length === 9) {
                updateSubBoard(data[0], 1);
                updateTrackBoard(data[1], 1);
                updateMines(data[2], 1);
                updateSubBoard(data[3], 2);
                updateTrackBoard(data[4], 2);
                updateMines(data[5], 2);
                updateStatistics(data[6], data[7]);
                updatePlayerNames(data[7]);
                updateViewersNames(data[8]);
            }
        },
        error: function(ts) { alert(ts.responseText) }
    });

}

function updateSubBoard(subBoard, player) {
    var cellClass;
    for(var row = 0; row < subBoard.length; ++row){
        if(player === 1)
            $("#sOne" + row).empty();
        else
            $("#sTwo" + row).empty();
        for(var col = 0; col < subBoard.length; ++col){
            if(subBoard[row][col] === 'X')
                cellClass = "shipHit";
            else if(subBoard[row][col] === 'O')
                cellClass = "shipMissed";
            else if(subBoard[row][col] === '@')
                cellClass = "mine";
            else if(subBoard[row][col] === '#')
                cellClass = "shipCell";
            else
                cellClass = "emptyCell";
            if(player === 1)
                var data = $("<td class= '" + cellClass + "' row =" + row + " col =" + col + "></td>").appendTo($("#sOne" + row));
            else
                var data = $("<td class= '" + cellClass + "' row =" + row + " col =" + col + "></td>").appendTo($("#sTwo" + row));
        }
    }
}

function updateTrackBoard(trackBoard, player) {
    var cellClass;
    for(var row = 0; row < trackBoard.length; ++row){
        if(player === 1)
            $("#tOne" + row).empty();
        else
            $("#tTwo" + row).empty();
        for(var col = 0; col < trackBoard.length; ++col){
            if(trackBoard[row][col] === 'X')
                cellClass = "shipHit";
            else if(trackBoard[row][col] === 'O')
                cellClass = "shipMissed";
            else
                cellClass = "emptyCell";
            if(player === 1)
                var data = $("<td class= '" + cellClass + "' row =" + row + " col =" + col + "></td>").appendTo($("#tOne" + row));
            else
                var data = $("<td class= '" + cellClass + "' row =" + row + " col =" + col + "></td>").appendTo($("#tTwo" + row));

        }
    }
}

function updateMines(mines, player) {
    var tdMines = "";
    if(player === 1) {
        $("#mOne").empty();
        tdMines = $("#mOne");
    }
    else {
        $("#mTwo").empty();
        tdMines = $("#mTwo");
    }
    var length = parseInt(mines);
    for(var item = 0; item < length; ++item){
        tdMines.append($("<td class = 'mine'></td>"));
    }
}

function updateStatistics(stat, playersNames) {
    if(stat[0] !== "game not started yet"){
        $("#turn")[0].classList.remove("gameNotStart") ;
    }

    $("#turn").text("Turn: " + stat[0]);
    if (playersNames.length === 0) {
        $("#scoreOne").text("player one score: " + stat[1]);
        $("#scoreTwo").text("player two score: " + stat[2]);
    } else if (playersNames.length === 1) {
        $("#scoreOne").text(playersNames[0] + " score: " + stat[1]);
        $("#scoreTwo").text("player two score: " + stat[2]);
    }else {
        $("#scoreOne").text(playersNames[0] + " score: " + stat[1]);
        $("#scoreTwo").text(playersNames[1] + " score: " + stat[2]);
    }
    $("#turnNumber").text("turn number: " + stat[3]);
    $("#playTime").text("playing time: " + stat[4]);
}


$(function () {
    $("#quitGame").click(function () {
        $.ajax({
            type: "POST",
            url: GAME_URL,
            data: {'viewQuit' : true},
            cache: false,
            timeout: 600000
        });
    });
});

