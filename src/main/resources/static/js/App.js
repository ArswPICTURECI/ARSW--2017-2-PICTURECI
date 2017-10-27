/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



var app = (function () {

    var stompClient = null;

    var callback = function (lista) {

        $("#tablaUsers tbody").empty();
        lista.map(function (ur) {
            $(document).ready(function () {
                var markup = "<tr><td>" + ur.name + "</td><td>" + ur.rol + "</td><td>" + ur.sala + "</td></tr>";
                $("#tablaUsers tbody").append(markup);
            });
        }
        );
    };

    return{
        getUserName: function (userName) {
            if (userName !== "") {
                //$.get("/users/" + userName, callback);
                $.get("/users/" + userName);
            } else {
                alert("EL USUARIO NO PUEDE ESTAR VACIO");
            }
            ;
        },
        addUser: function (userName) {
            if (userName !== "") {
                var data = {"name": userName, "rol": "", "sala": 0};
                sessionStorage.setItem("currentuser", userName);
            } else {
                alert("El nombre del usuario no puede estar vacio");
            }

            return $.ajax({
                url: "/users/",
                type: 'POST',
                data: JSON.stringify(data),
                contentType: "application/json"
            }).then(function () {
                $.get("/users/", callback);
                alert("El usuario: " + userName + " se ha creado satisfactoriamente");
            },
                    function () {
                        alert("Error al registrar el nuevo Usuario");
                    }
            );
        },
        login: function (user) {
            if (user !== "") {
                $.get("/users/" + user, function (data) {
                    sessionStorage.setItem("currentuser", user);
                    location.href = "partida.html";
                }).fail(function () {
                    alert("El usuario " + user + " no está registrado");
                });
            } else {
                alert("El usuario no puede estar vacio!!");
            }
        },

        subscribe: function () {
            var socket = new SockJS('/stompendpoint');
            var gameid = sessionStorage.getItem("currentgame");
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                console.log('Connected: ' + frame);
                stompClient.subscribe('/topic/wupdate.' + gameid, function (eventbody) {
                    var new_word = eventbody.body;
                    $("#palabra").html("<h1>" + new_word + "</h1>");
                });
            });

        },

        connect: function () {
            var game = $("#topic").val();
            sessionStorage.setItem("currentgame", game);
            var game_ = {"used_words": [], "word": "dog", "winner": ""};
            $.ajax({
                url: "/pictureci/" + game,
                type: "POST",
                data: JSON.stringify(game_),
                contentType: "application/json"
            }).then(function () {
                app.rapida();
            });
        },

        attempt: function () {
            var cgame = sessionStorage.getItem("currentgame");
            var cuser = sessionStorage.getItem("currentuser");
            var att = {"username": cuser, "phrase": $("#guess_input").val()};
            return $.ajax({
                url: "/pictureci/" + cgame + "/guess",
                type: "POST",
                data: JSON.stringify(att),
                contentType: "application/json"
            });
        },
        queryUsers: function () {
            $.get("/users/", callback);
        },
        partida: function () {
            location.href = "partida.html";
        },
        rapida: function () {
            sessionStorage.setItem('sala', $("#topic").val());
            sessionStorage.setItem('rol', $("#rol").val());
            if (sessionStorage.getItem('rol') === 'Adivinar') {
                location.href = "rapidaAdivinador.html";

            } else {
                location.href = "rapidaDibujante.html";
            }
        },
        registro: function () {
            location.href = "registro.html";
        },
        principal: function () {
            location.href = "index.html";
        },
        inicioSesion: function () {
            location.href = "sesion.html";
        }
    };
})();