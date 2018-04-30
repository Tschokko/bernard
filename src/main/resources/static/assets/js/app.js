var ws;
var requestId = 1;

function setConnected (connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);

    $("#btnHello").prop("disabled", !connected);
    $("#btnInvalid").prop("disabled", !connected);
    $("#btnPing").prop("disabled", !connected);
    $("#btnEventProfileActivated").prop("disabled", !connected);

    if (connected) {
        $("#infoProgress").removeClass("bg-danger");
        $("#infoProgress").addClass("bg-success");
        $("#infoProgress").html("Connected");
    }
    else {
        $("#infoProgress").removeClass("bg-success");
        $("#infoProgress").addClass("bg-danger");
        $("#infoProgress").html("Disconnected");
    }
    $("#rawMessages").html("");
}

function connect () {
    ws = new WebSocket('ws://localhost:8080/ws');
    ws.onmessage = function(data){
        showGreeting(data.data);
    };
    ws.onclose = function (ev) {
        disconnect();
    };
    ws.onerror = function (ev) {
        console.log("WebSocket error: " + ev);
        disconnect();
    };
    setConnected(true);
}

function disconnect () {
    if (ws != null) {
        ws.close();
    }
    setConnected(false);
}

function sendName () {
    var data = JSON.stringify({'name': $("#name").val()})
    ws.send(data);
}

function showGreeting (message) {
    $("#rawMessages").append("<tr><td> " + message + "</td></tr>");

    parseIncomingMessage(message);
}



function addMessageToContainer (title, body, clazz) {
    //var html = $("#containerMessages").html();
    var cssClass = "alert-" + clazz;
    var html = "<div class=\"alert " + cssClass + " alert-dismissible show\" role=\"alert\">";
    html += "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\">\n" +
        "<span aria-hidden=\"true\">&times;</span>\n" +
        "</button>";
    html += "<h4 class=\"alert-heading\">" + title + "</h4>";
    // html += "<hr/>";
    html += "<p class=\"mb-0\">" + body + "</p>";
    $("#containerMessages").append(html);
}

function parseIncomingMessage (textMessage) {
    var rawMessage = JSON.parse(textMessage);
    if (rawMessage instanceof Array) {
        if (rawMessage[0] == 2) {
            addMessageToContainer("WELCOME", textMessage, "success");
        }
        else if (rawMessage[0] == 3) {
            addMessageToContainer("ABORT", textMessage, "danger");
        }
        else if (rawMessage[0] == 21) {
            addMessageToContainer("PUBLISHED", textMessage, "success");
        }
    }
}

function sendHello () {
    var deviceId = $("#inputDeviceId").val() + "@devices.iot.insys-icom.com";
    var helloMessage = [1, deviceId, {}];
    var data = JSON.stringify(helloMessage);
    ws.send(data);

    addMessageToContainer("HELLO", data, "info");
}

function sendInvalid () {
    var invalidMessage = [2, "Invalid", {}];
    var data = JSON.stringify(invalidMessage);
    ws.send(data);
    addMessageToContainer("WELCOME", data, "warning");
}

function sendPing () {
    var pingMessage = [4, {}];
    var data = JSON.stringify(pingMessage);
    ws.send(data);
    addMessageToContainer("PING", data, "dark");
}


function sendPublishProfileActivated () {
    var deviceId = $("#inputDeviceId").val();
    var timestamp = new Date();
    var publishMessage = [20, requestId, "events::devices", {"device_id": deviceId, "event": "PROFILE_ACTIVATED", "timestamp": timestamp.toISOString()}];
    var data = JSON.stringify(publishMessage);
    ws.send(data);

    requestId += 1;

    addMessageToContainer("PUBLISH", data, "info");
}

function dismissMessages () {
    $("#containerMessages").html("");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });

    $("#btnHello").click(function () {
        sendHello();
    });
    $("#btnInvalid").click(function () {
        sendInvalid();
    });
    $("#btnPing").click(function () {
        sendPing();
    });
    $("#btnEventProfileActivated").click(function () {
        sendPublishProfileActivated();
    });
    $("#btnDismissMessages").click(function () {
        dismissMessages();
    });
});
