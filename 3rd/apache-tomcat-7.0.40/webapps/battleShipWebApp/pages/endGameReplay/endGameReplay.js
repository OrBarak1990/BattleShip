var SAVE_URL = buildUrlWithContextPath("saveServlet");
function changeWindow() {
    window.location = '../lobby/lobby.html';
}

function updatePlayerNames(playersNames) {
    $("#playersNames").empty();
    var list;
    list = $('<ol type="I">');
    for (var i = 0; i < playersNames.length; ++i) {
        list.append($('<li>' + playersNames[i] + '</li>'));
    }
    list.append($('</ol>'));
    list.appendTo($("#playersNames"));
}

function updateViewersNames(viewersNames) {
    $("#viewersNames").empty();

    var list;
    list = $('<ol type="I">');
    for(var i = 0; i < viewersNames.length;++i){
        list.append($('<li>' + viewersNames[i] + '</li>'));
    }
    list.append($('</ol>'));
    list.appendTo($("#viewersNames"));
}

function updateMines(mines, drage) {
    var tdMines = "";
    $("#rowMine").empty();
    tdMines = $("#rowMine");
    var length = parseInt(mines);
    for(var item = 0; item < length; ++item){
        if(drage)
            tdMines.append($("<td class = 'mine' id= 'm" + item + "' draggable = true ></td>"));
        else
            tdMines.append($("<td class = 'mine' id= 'm" + item + "' draggable = false ></td>"));
    }
}

function updateSubBoard(subBoard) {
    var cellClass;
    for(var row = 0; row < subBoard.length; ++row){
        $("#s" + row).empty();
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
            // var data = $("<td class= '" + cellClass + "' row =" + row + " col =" + col + "> <span>" +  subBoard[row][col] + "</span></td>").appendTo($("#s" + row));
            var data = $("<td class= '" + cellClass + "' row =" + row + " col =" + col + "></td>").appendTo($("#s" + row));
        }
    }
}

function updateTrackBoard(trackBoard) {
    var chosen = getChosenOne("chooseCell");
    var chosenRow, chosenCol;
    if(chosen !== null){
        chosenRow = chosen.getAttribute("row");
        chosenCol = chosen.getAttribute("col");
    }
    var cellClass;
    for(var row = 0; row < trackBoard.length; ++row){
        $("#t" + row).empty();
        for(var col = 0; col < trackBoard.length; ++col){
            if(trackBoard[row][col] === 'X')
                cellClass = "shipHit";
            else if(trackBoard[row][col] === 'O')
                cellClass = "shipMissed";
            else
                cellClass = "emptyCell";
            // var data = $("<td class= '" + cellClass + "' row =" + row + " col =" + col + "> <span>" +  trackBoard[row][col] + "</span></td>").appendTo($("#t" + row));
            var data = $("<td class= '" + cellClass + "' row =" + row + " col =" + col + "></td>").appendTo($("#t" + row));
            if( chosen !== null  && chosenRow === row.toString() && chosenCol === col.toString())
                data[0].classList.add('chooseCell');
        }
    }
}

function updateStatistics(stat, playersNames) {
    if(stat[0] !== "waiting for a rival"){
        $("#turn")[0].classList.remove("gameNotStart") ;
    }

    var match = "Turn: " + stat[0];
    if(match !== $("#turn")[0].innerHTML.toString() && document.getElementById("chooseError").innerHTML === "<h6> this is not your turn!</h6>"){
        document.getElementById("chooseError").innerHTML = "";
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

function updatePlayerInfo(playerInfo) {
    $("#playerPos").text("Player: " + playerInfo[0]);
    $("#hits").text("Hits: " + playerInfo[1]);
    $("#score").text("Score: " + playerInfo[2]);
    $("#averagePlayTime").text("Average playing time: " + playerInfo[3]);
}


function getChosenOne(chosen) {
    var chosenElement = document.getElementsByClassName(chosen);
    var chosen = null;
    if(chosenElement.length !== 0)
        chosen = chosenElement[0];
    return chosen;
}
$(function () {
    $.ajax({
        url: SAVE_URL,
        data: {'load' : "end"},
        cache: false,
        timeout: 600000,
        success: function (data) {
            if (data.length === 7 || data.length === 8) {
                ($("#replayMassage")).html("<h2> Replay Mode</h2>");
                $("#options").hide();
                $("#replayOptions").show();
                replayMode = true;
                updateSubBoard(data[0]);
                updateTrackBoard(data[1]);
                updateMines(data[2], false);
                updateStatistics(data[3], data[5]);
                updatePlayerInfo(data[4]);
                updatePlayerNames(data[5]);
                updateViewersNames(data[6]);
            }else{
                ($("#replayError")).html("<h6> " + data + "</h6>");
            }
        }
    });
});



$(function () {
    $("#next").click(function () {
        document.getElementById("replayError").innerHTML = "";
        $.ajax({
            url: SAVE_URL,
            data: {'load' : "next"},
            cache: false,
            timeout: 600000,
            success: function (data) {
                if (data.length === 7 || data.length === 8) {
                    updateSubBoard(data[0]);
                    updateTrackBoard(data[1]);
                    updateMines(data[2], false);
                    updateStatistics(data[3], data[5]);
                    updatePlayerInfo(data[4]);
                    updatePlayerNames(data[5]);
                    updateViewersNames(data[6]);
                }else{
                    ($("#replayError")).html("<h6> " + data + "</h6>");
                }
            }
        });
    });

    $("#prev").click(function () {
        document.getElementById("replayError").innerHTML = "";
        $.ajax({
            url: SAVE_URL,
            data: {'load' : "prev"},
            cache: false,
            timeout: 600000,
            success: function (data) {
                if (data.length === 7 || data.length === 8) {
                    updateSubBoard(data[0]);
                    updateTrackBoard(data[1]);
                    updateMines(data[2], false);
                    updateStatistics(data[3], data[5]);
                    updatePlayerInfo(data[4]);
                    updatePlayerNames(data[5]);
                    updateViewersNames(data[6]);
                }else{
                    ($("#replayError")).html("<h6> " + data + "</h6>");
                }
            }
        });
    });

    $("#quitReplay").click(function () {
        document.getElementById("replayMassage").innerHTML = "";
        document.getElementById("replayError").innerHTML = "";
        document.getElementById("myTurnMassage").innerHTML = "";
        changeWindow();
    });
});