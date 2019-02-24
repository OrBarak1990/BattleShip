var refreshRate = 2000;
var BOARD_GAME_URL = buildUrlWithContextPath("gameUpdateServlet");
var GAME_URL = buildUrlWithContextPath("gameServlet");
var SAVE_URL = buildUrlWithContextPath("saveServlet");
var CHAT_LIST_URL = buildUrlWithContextPath("chatUpdateServlet");
var minePermission = false, attackPermission = false ,replayMode = false;
var chatVersion = 0;

$(function () {
    setInterval(ajaxGameBoard, refreshRate);
    setInterval(ajaxChat, refreshRate);
});

function changeWindow(changeWindow) {
    if(changeWindow === true)
        window.location = '../endGame/endGame.jsp';
    else
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

function ajaxGameBoard() {
    $.ajax({
        url: BOARD_GAME_URL,
        timeout: 600000,
        cache: false,
        success: function (data) {
            if(replayMode){
                if((data[3])[0] === (data[4])[0])
                    ($("#myTurnMassage")).html("<h2> your turn to play</h2>");
            }
            else if(data.length === 8){
                updateSubBoard(data[0]);
                updateTrackBoard(data[1]);
                updateMines(data[2], true);
                updateStatistics(data[3], data[5]);
                updatePlayerInfo(data[4]);
                updatePlayerNames(data[5]);
                updateViewersNames(data[6]);
                setTimeout(changeWindow(data[7]), 100000);
            }
            else if(data.length === 7) {
                updateSubBoard(data[0]);
                updateTrackBoard(data[1]);
                updateMines(data[2], true);
                updateStatistics(data[3], data[5]);
                updatePlayerInfo(data[4]);
                updatePlayerNames(data[5]);
                updateViewersNames(data[6]);
            }
        },
        error: function(ts) { alert(ts.responseText) }
    });

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
            var data = $("<td class= '" + cellClass + "' row =" + row + " col =" + col + "></td>").appendTo($("#t" + row));
            if( chosen !== null  && chosenRow === row.toString() && chosenCol === col.toString())
                data[0].classList.add('chooseCell');
        }
    }

    $("td").on('click', function () {
        document.getElementById("chooseError").innerHTML = "";
        if(attackPermission && $(this).parent().parent().parent()[0] === document.getElementById("track")) {
            $(this).toggleClass('chooseCell');
            $(this).siblings().removeClass('chooseCell');
            var row = $(this)[0].getAttribute("row");
            var col = $(this)[0].getAttribute("col");
            $.ajax({
                type: "POST",
                url: GAME_URL,
                data: {'attack' : true,'row' : row, 'col' : col},
                cache: false,
                timeout: 600000,
                success: function (data) {
                    if (data !== null) {
                        ($("#chooseError")).html("<h6> " + data + "</h6>");
                    }
                }
            });
            $(this)[0].classList.remove("chooseCell");
            attackPermission = false;
        }
        else if(!attackPermission){
            ($("#chooseError")).html("<h6>please press attack button first</h6>");
        }
    });
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


$(function () {
   $("#quitGame").click(function () {
       $.ajax({
           type: "POST",
           url: GAME_URL,
           data: {'quit' : true},
           cache: false,
           timeout: 600000,
           success: function (data) {
               if (data !== null) {
                   ($("#chooseError")).html("<h6> " + data + "</h6>");
               }
           }
       });
   });
});



$(function () {
    $("#attack").click(function () {
        document.getElementById("chooseError").innerHTML = "";
        attackPermission = true;
        minePermission = false;
    });

    $("#placeMine").click(function () {
        document.getElementById("chooseError").innerHTML = "";
        attackPermission = false;
        minePermission = true;
    });
});


function getChosenOne(chosen) {
    var chosenElement = document.getElementsByClassName(chosen);
    var chosen = null;
    if(chosenElement.length !== 0)
        chosen = chosenElement[0];
    return chosen;
}

$(function () {
    document.ondragstart = function(event) {
        document.getElementById("chooseError").innerHTML = "";
        event.dataTransfer.setData("Text", event.target.id);
    };

    document.ondragover = function(event) {
        if ( event.target.parentNode.id.charAt(0) === "s" )
            event.preventDefault();
    };

    document.ondrop = function(event) {
        event.preventDefault();
        if ( minePermission && event.target.parentNode.id.charAt(0) === "s" ) {
            var dataId = event.dataTransfer.getData("Text");
            var row = event.target.getAttribute("row");
            var col = event.target.getAttribute("col");
            $.ajax({
                url: GAME_URL,
                data: {'attack' : false,'row' : row, 'col' : col},
                cache: false,
                timeout: 600000,
                success: function (data) {
                    if (data.length !== 0) {
                        ($("#chooseError")).html("<h6> " + data + "</h6>");
                    }
                    else{
                        document.getElementById(dataId).parentNode.removeChild(document.getElementById(dataId));
                    }
                }
            });
            minePermission = false;
        }else if(!minePermission){
            ($("#chooseError")).html("<h6>please press place mine button first</h6>");
        }
    };
});

$(function () {
    $("#replayOptions").hide();
    $("#replayFromBegin").click(function () {
       $.ajax({
           url: SAVE_URL,
           data: {'load' : "begin"},
           cache: false,
           timeout: 600000,
           success: function (data) {
               if (data.length === 7) {
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
                   ($("#replayError")).html("<h5> " + data + "</h5>");
               }
           }
       });

    });

    $("#replayFromEnd").click(function () {
        $.ajax({
            url: SAVE_URL,
            data: {'load' : "end"},
            cache: false,
            timeout: 600000,
            success: function (data) {
                if (data.length === 7) {
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
                    ($("#replayError")).html("<h5> " + data + "</h5>");
                }
            }
        });
    });

    $("#next").click(function () {
        document.getElementById("replayError").innerHTML = "";
        $.ajax({
            url: SAVE_URL,
            data: {'load' : "next"},
            cache: false,
            timeout: 600000,
            success: function (data) {
                if (data.length === 7) {
                    updateSubBoard(data[0]);
                    updateTrackBoard(data[1]);
                    updateMines(data[2], false);
                    updateStatistics(data[3], data[5]);
                    updatePlayerInfo(data[4]);
                    updatePlayerNames(data[5]);
                    updateViewersNames(data[6]);
                }else{
                    ($("#replayError")).html("<h5> " + data + "</h5>");
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
                if (data.length === 7) {
                    updateSubBoard(data[0]);
                    updateTrackBoard(data[1]);
                    updateMines(data[2], false);
                    updateStatistics(data[3], data[5]);
                    updatePlayerInfo(data[4]);
                    updatePlayerNames(data[5]);
                    updateViewersNames(data[6]);
                }else{
                    ($("#replayError")).html("<h5> " + data + "</h5>");
                }
            }
        });
    });

    $("#quitReplay").click(function () {
        document.getElementById("replayMassage").innerHTML = "";
        document.getElementById("replayError").innerHTML = "";
        document.getElementById("myTurnMassage").innerHTML = "";
        $("#options").show();
        $("#replayOptions").hide();
        replayMode = false;
    });
});


$(function () {
    $("#chatform").submit(function() {
        event.preventDefault();
        $.ajax({
            data: $(this).serialize(),
            url: this.action,
            timeout: 2000,
            process: false
        });
        $("#userstring").val("");
    });
});

function ajaxChat() {
    $.ajax({
        url: CHAT_LIST_URL,
        data: "chatversion=" + chatVersion,
        dataType: 'json',
        success: function(data) {
            var version = data[0], entries = data[1];
            if (version !== chatVersion) {
                chatVersion = version;
                appendToChatArea(entries);
            }
        }
    });
}

function createChatEntry (entry){
    entry.chatString = entry.chatString.replace (":)", "<span class='smiley'></span>");
    return $("<span class=\"success\">").append(entry.username + "> " + entry.chatString);
}

function appendChatEntry(index, entry){
    var entryElement = createChatEntry(entry);
    $("#chatWindow").append(entryElement).append("<br>");
}

function appendToChatArea(entries) {
    $.each(entries || [], appendChatEntry);
    var scroller = $("#chatWindow");
    var height = scroller[0].scrollHeight - $(scroller).height();
    $(scroller).stop().animate({ scrollTop: height }, "slow");

}