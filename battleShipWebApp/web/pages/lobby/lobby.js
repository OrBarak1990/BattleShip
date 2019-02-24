var refreshRate = 2000;
var USER_LIST_URL = buildUrlWithContextPath("playersList");
var board_LIST_URL = buildUrlWithContextPath("boardsList");
var BOARD_UPLOAD_URL = buildUrlWithContextPath("boardUpload");
var REGISTER_PLAYER_URL = buildUrlWithContextPath("playerRegister");
var REGISTER_VIEWER_URL = buildUrlWithContextPath("viewerRegister");
var BOARD_DELETE_URL = buildUrlWithContextPath("boardDelete");
var USE_DELETE_URL = buildUrlWithContextPath("userDelete");

$(function() {
    $.ajaxSetup({cache: false});
    setInterval(ajaxPlayersList, refreshRate);
    setInterval(ajaxBoardsList, refreshRate);
});

function ajaxPlayersList() {
    $.ajax({
        url: USER_LIST_URL,
        success: function(users) {
            refreshPlayersList(users);
        }
    });
}

function refreshPlayersList(users) {
    $("#playersList").empty();
    $.each(users || [], function(index, username) {
        $('<li>' + username + '</li>').appendTo($("#playersList"));
    });
}

function ajaxBoardsList() {
    $.ajax({
        url: board_LIST_URL,
        success: function(data) {
            refreshBoardsList(data);
        }
    });
}

function getChosenOne(chosen) {
    var chosenElement = document.getElementsByClassName(chosen);
    var text = "";
    if(chosenElement.length !== 0)
        text = chosenElement[0].innerText;
    return text;
}

function getContentById(chosen) {
    var chosenElement = document.getElementById(chosen).innerHTML;
    var text = "";
    if(chosenElement.length !== 0)
        text = chosenElement;
    return text;
}

function refreshBoardsList(data) {
    var text = getChosenOne("special");
    document.getElementById("buttonErrors").innerHTML = "";
    $("#boardsList").empty();
    var names = data[0], users = data[1], size = data[2], type = data[3], signUp = data[4], viewers = data[5];
    for(var i = 0; i < names.length;++i){
        var row = $('<li><h3>' + names[i] + '</h3><ol type="I">'
            +'<li>' + "userName: " + users[i] + '</li>'
            +'<li>' + "board size: " + size[i] + '</li>'
            +'<li>' + "game type: " + type[i] + '</li>'
            +'<li>' + "registare: " + signUp[i] + '</li>'
            +'<li>' + "viewers: " + viewers[i] + '</li>'
            +'</ol> </li>').appendTo($("#boardsList"));
        if( text.length !== 0  && names[i] === text)
            row.find('h3').filter(':first').addClass('special');
        if(signUp[i] === "2"){
            row.find('h3').filter(':first').addClass('full');
            row.find('h3').filter(':first')[0].innerHTML = names[i];// + "(full- not available)";
        }
    }

    $("li").on('click', function () {
        document.getElementById("buttonErrors").innerHTML = "";
        $(this).find('h3').filter(':first').addClass('special');
        $(this).siblings().find('h3').removeClass('special');
    });
}

$(document).ready(function () {
    $("#fileName").click(function (event) {
        document.getElementById("errorFileName").innerHTML = "";
    });
    $("#file").click(function () {
        document.getElementById("errorFile").innerHTML = "";
    });
    $("#btnSubmit").click(function (event) {
        event.preventDefault();
        var authorize = true;
        if($("#fileName").val().trim().length === 0){
            ($("#errorFileName")).html("<h6>please enter file name before submit</h6>");
            authorize = false;
        }
        if($("#file").val().trim().length === 0){
            ($("#errorFile")).html("<h6>please enter file before submit</h6>");
            authorize = false;
        }

        if(authorize) {
            var fd = new FormData();
            var file_data = $('input[type="file"]')[0].files;
            for (var i = 0; i < file_data.length; i++) {
                fd.append("file", file_data[i]);
            }
            var other_data = $('form').serializeArray();
            $.each(other_data, function (key, input) {
                fd.append(input.name, input.value);
            });
            $.ajax({
                type: "POST",
                url: BOARD_UPLOAD_URL,
                data: fd,
                //data: {'gameFile' : file_data},
                processData: false,
                contentType: false,
                cache: false,
                timeout: 600000,
                success: function (data) {
                    if(data !== null){
                        ($("#uploadError")).html("<h6> " + data + "</h6>");
                    }
                },
                error: function (e) {
                    alert("error");
                }
            });
        }
    });
});

$(function() {
    document.getElementById("buttonErrors").innerHTML = "";
    $("#enterGame").click(function (event) {
        var full = getChosenOne("full");
        var text = getChosenOne("special");
        if(full.length !== 0 && full === text){
            $("#buttonErrors").html("<h6>" + full + " is full</h6>");
        }
        else if(text.length === 0){
            ($("#buttonErrors")).html("<h6>please choose game before submit</h6>");
        }else{
            $.ajax({
                type: "POST",
                url: REGISTER_PLAYER_URL,
                data: {'boardToRegister' : text},
                cache: false,
                timeout: 600000,
                success: function (data) {
                    window.location = '../game/game.jsp';
                }
            });
        }
    })

    $("#viewGame").click(function (event) {
        var text = getChosenOne("special");
        if(text.length === 0){
            ($("#buttonErrors")).html("<h6>please choose game before submit</h6>");
        }else{
            $.ajax({
                type: "POST",
                url: REGISTER_VIEWER_URL,
                data: {'boardToRegister' : text},
                cache: false,
                timeout: 600000,
                success: function (data) {
                    window.location = '../viewer/viewer.jsp';
                }
            });
        }
    })



    $("#deleteGame").click(function () {
        document.getElementById("buttonErrors").innerHTML = "";
        var text = getChosenOne("special");
        if(text.length === 0){
            ($("#buttonErrors")).html("<h6>please choose board to delete</h6>");
        }else {
            $.ajax({
                url: BOARD_DELETE_URL,
                data: {'boardToDelete' : text},
                cache: false,
                timeout: 600000,
                success: function (data) {
                    if (data !== null) {
                        ($("#buttonErrors")).html("<h6> " + data + "</h6>");
                    }
                },
                error: function (e) {
                    alert(e.toString());
                }
            });
        }
    });

    $("#logout").click(function () {
        localStorage.setItem('logout-event', 'logout' + Math.random());
        document.getElementById("buttonErrors").innerHTML = "";
        $.ajax({
            url: USE_DELETE_URL,
            cache: false,
            timeout: 600000,
            success: function (data) {
                if(data.length === 0)
                    window.location = "../login/login.html";
                else
                    ($("#buttonErrors")).html("<h6> " + data + "</h6>");
            }
        });
    });

    window.addEventListener('storage', function(event){
        if (event.key === 'logout-event') {
            window.location = "../login/login.html";
        }
    });
});
