/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var app = (function () {

    var numero;

    var callback = function (lista) {

        $("#tablaUsers tbody").empty();
        lista.map(function (ur) {
            $(document).ready(function () {
                var markup = "<tr><td>" + ur.name + "</td></tr>";
                $("#tablaUsers tbody").append(markup);
            });
        }
        );
    };
    callbackPlayers = function (lista) {
        $("#tabla tbody").empty();
        lista.map(function (ur) {
            $(document).ready(function () {
                //var markup = "<tr><td>" + ur.name + "</td><td>" + ur.room + "</td><td>" + getRolString(ur.rol) + "</td><td>" + ur.score + "</td></tr>";
                var markup = "<tr><td>" + ur.name + "</td><td>" + ur.room + "</td><td>" + getRolString(ur.rol) + "</td></tr>";
                $("#tabla tbody").append(markup);
            });
        }
        );
    };
    callbackFinishedGames = function (lista) {
        $("#tablaFinishedGames tbody").empty();
        lista.map(function (ur) {
            $(document).ready(function () {
                var markup = "<tr><td>" + ur.room + "</td><td>" + ur.winner + "</td><td>" + getFinishedGamePlayers(ur.players) + "</td></tr>";
                $("#tablaFinishedGames tbody").append(markup);
            });
        }
        );
    };

    function getFinishedGamePlayers(l) {
        var res = [];
        l.map(function (pl) {
            var players = "{" + pl.name + " jugó con rol " + getRolString(pl.rol) + " y con puntaje de " + pl.score + "}";
            res.push(players);
        });
        return res;
    }
    ;

    function getRolString(num) {
        if (num === -1) {
            return "Dibujar";
        } else if (num === -2) {
            return "Adivinar";
        } else {
            return "N/A";
        }
    }
    ;

    timeOut = function () {
        var mode = sessionStorage.getItem("mode");
        if (mode === "normal") {
            var gameid = sessionStorage.getItem("currentgame");
            return $.ajax({
                url: "/pictureci/normalMode/" + gameid + "/timeout",
                type: "PUT"
            });
        } else {
            var gameid = sessionStorage.getItem("currentrandomid");
            return $.ajax({
                url: "/pictureci/randomMode/" + gameid + "/timeout",
                type: "PUT"
            });
        }
    };

    putGame = function () {
        var gameid = $("#topic").val();
        sessionStorage.setItem("currentgame", gameid);
        var word = "perro";
        return $.ajax({
            url: "/pictureci/normalMode/" + gameid,
            type: "PUT",
            data: word, //"{"name":x.name,"bvjdfnv":jhvsbdj}
            contentType: "application/json"
        });
    };

    connectPlayerRandom = function () {
        var user = sessionStorage.getItem("currentuser");
        return $.ajax({
            url: "/players/randomGame/" + user,
            type: "PUT",
            data: sessionStorage.getItem("currentrandomid"),
            contentType: "application/json",
            success: function (data) {
                sessionStorage.setItem("rol", getRolString(data));
                $("#iniciarpartidabtn").prop("disabled", true);
                $("#regresarbtn").prop("disabled", true);
            },
            error: function () {
                document.getElementById('messageCancel').style.visibility = 'hidden';
                document.getElementById("cancelqueuebtn").style.visibility = 'hidden';
            }
        });
    };

    connectPlayer = function () {
        sessionStorage.setItem("rol", $("#rol").val());
        var rol = $("#rol").val();
        var user = sessionStorage.getItem("currentuser");
        var gameid = sessionStorage.getItem("currentgame");
        if (rol === "Adivinar") {
            return $.ajax({
                url: "/players/normalMode/adivinan-" + user,
                type: "POST",
                data: gameid,
                contentType: "application/json",
                success: function () {
                    $("#iniciarpartidabtn").prop("disabled", true);
                    $("#regresarbtn").prop("disabled", true);
                    //$("#cancelqueuebtn").prop("disabled", false);
                },
                error: function () {
                    alert("Los jugadores con Rol Adivinan estan llenos");
                    document.getElementById('messageCancel').style.visibility = 'hidden';
                    document.getElementById("cancelqueuebtn").style.visibility = 'hidden';

                }
            });
        } else {
            return $.ajax({
                url: "/players/normalMode/dibujan-" + user,
                type: "POST",
                data: gameid,
                contentType: "application/json",
                success: function () {
                    $("#iniciarpartidabtn").prop("disabled", true);
                    $("#regresarbtn").prop("disabled", true);
                    //$("#cancelqueuebtn").prop("disabled", false);
                },
                error: function () {
                    alert("Los jugadores con Rol Dibujan estan llenos");
                    document.getElementById('messageCancel').style.visibility = 'hidden';
                    document.getElementById("cancelqueuebtn").style.visibility = 'hidden';
                }
            });
        }
    };

    subscribeRandom = function () {
        return $.get("/pictureci/random", function (data) {
            sessionStorage.setItem("currentrandomid", data);
            var gameid = data;
            console.info('Connecting to WS...');
            var socket = new SockJS('/stompendpoint');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                console.log('Connected to room ' + gameid + ': ' + frame);
                document.getElementById("messageCancel").innerHTML = "Esperando a que se conecten los demas usuarios... ";
                document.getElementById("cancelqueuebtn").innerHTML = "<button type='button' onclick='app.cancelQueueRandom()'>Cancelar Suscripcion al juego</button><br>";
                stompClient.subscribe('/topic/rndready.' + gameid, function (data) {
                    var rol = sessionStorage.getItem("rol");
                    if (rol === null) {
                        if (data.body === -1) {
                            location.href = "rapidaDibujante.html";
                        } else {
                            location.href = "rapidaAdivinador.html";
                        }
                    } else {
                        if (rol === "Dibujar") {
                            location.href = "rapidaDibujante.html";
                        } else {
                            location.href = "rapidaAdivinador.html";
                        }
                    }
                });
                stompClient.subscribe('/topic/disconnectrandom.' + gameid, function () {
                });
            });
        });
    };

    return{
        getUserName: function (userName) {
            if (userName !== "") {
                $.get("/users/" + userName);
            } else {
                alert("EL USUARIO NO PUEDE ESTAR VACIO");
            }
        },
        addUser: function (userName, pwd) {
            if (userName !== "") {
                var data = {"name": userName, "password": pwd};
                sessionStorage.setItem("currentuser", userName);

            } else {
                alert("El nombre del usuario no puede estar vacio");
            }
            return $.ajax({
                url: "/users",
                type: 'POST',
                data: JSON.stringify(data),
                contentType: "application/json"
            }).then(function () {
                $.get("/users", callback);
                alert("El usuario: " + userName + " se ha creado satisfactoriamente");
            },
                    function () {
                        alert("Error al registrar el nuevo Usuario");
                    }
            );
        },
        login: function (user, password) {
            if (user !== "" && password !== "") {
                $.get("/users/" + user, function (data) {
                    if (password === password) {
                        sessionStorage.setItem("currentuser", user);
                        sessionStorage.currentplayerName = user;
                        //createPlayer(sessionStorage.currentplayerName);
                        app.backToGameMode();

                    } else {
                        alert("CONTRASEÑA INVALIDA");
                    }
                }).fail(function () {
                    alert("El usuario " + user + " no está registrado");
                });
            } else if (user !== "" && password === "") {
                alert("Debe Digitar una contraseña");
            } else {
                alert("El usuario no puede estar vacio!!");
            }
        },
        cancelQueue: function () {
            document.getElementById("messageCancel").innerHTML = "";
            document.getElementById("cancelqueuebtn").innerHTML = "";
            return $.ajax({
                url: "/players/" + sessionStorage.getItem("currentgame") + "/" + sessionStorage.getItem("currentuser"),
                type: 'DELETE',
                success: function () {
                    $("#iniciarpartidabtn").prop("disabled", false);
                    $("#regresarbtn").prop("disabled", false);
                    $("#cancelqueuebtn").prop("disabled", true);
                }
            });
        },
        cancelQueueRandom: function () {
            document.getElementById("messageCancel").innerHTML = "";
            document.getElementById("cancelqueuebtn").innerHTML = "";
            return $.ajax({
                url: "/players/" + sessionStorage.getItem("currentrandomid") + "/" + sessionStorage.getItem("currentuser"),
                type: 'DELETE',
                success: function () {
                    $("#iniciarpartidabtn").prop("disabled", false);
                    $("#regresarbtn").prop("disabled", false);
                    $("#cancelqueuebtn").prop("disabled", true);
                }
            });
        },
        subscribeToWinner: function () {
            console.info('Connecting to WS...');
            var socket = new SockJS('/stompendpoint');
            var gameid;
            var prefix;

            if (sessionStorage.getItem("mode") === "normal") {
                gameid = sessionStorage.getItem("currentgame");
                prefix = "/topic/winner.";
            } else {
                gameid = sessionStorage.getItem("currentrandomid");
                prefix = "/topic/winner-";
            }

            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                console.log('Connected to game ' + gameid + ': ' + frame);
                stompClient.subscribe(prefix + gameid, function (eventbody) {
                    alert("Ganador: " + eventbody.body);
                    //Correccion para salir despues de que alguien gana
                    //No estoy seguro si la alerta se le muestra a todos los usuarios
                    location.href = "GameMode.html";
                });
            });
        },
        subscribeToRoom: function () {
            console.info('Connecting to WS...');
            var socket = new SockJS('/stompendpoint');
            var gameid = sessionStorage.getItem("currentgame");
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                console.log('Connected to room ' + gameid + ': ' + frame);
                document.getElementById("messageCancel").innerHTML = "Esperando a que se conecten los demas usuarios... ";
                document.getElementById("cancelqueuebtn").innerHTML = "<button type='button' onclick='app.cancelQueue()'>Cancelar Suscripcion al juego</button><br>";
                stompClient.subscribe('/topic/ready.' + gameid, function () {
                    app.makeGame();
                });
                stompClient.subscribe('/topic/disconnect.' + gameid, function () {
                });
            });
        },
        connectToNormalGame: function () {
            putGame().then(function () {
                app.subscribeToRoom();
            }).then(function () {
                connectPlayer();
            });
        },
        connectToRandomGame: function () {
            subscribeRandom().then(function () {
                connectPlayerRandom();
            });
        },
        makeGame: function () {
            if (sessionStorage.getItem("rol") === "Adivinar") {
                location.href = "rapidaAdivinador.html";
            } else {
                location.href = "rapidaDibujante.html";
            }
        },
        disconnect: function () {
            if (stompClient !== null) {
                stompClient.disconnect();
            }
            setConnected(false);
            console.log("Disconnected from game: " + sessionStorage.getItem("currentgame"));
        },
        attempt: function () {
            var mode = sessionStorage.getItem("mode");
            var cuser = sessionStorage.getItem("currentuser");
            var att = {"username": cuser, "phrase": $("#guess_input").val(), "timer": numero};
            if (mode === "normal") {
                var cgame = sessionStorage.getItem("currentgame");
                return $.ajax({
                    url: "/pictureci/normalMode/" + cgame + "/guess",
                    type: "POST",
                    data: JSON.stringify(att),
                    contentType: "application/json"
                });
            } else {
                var cgame = sessionStorage.getItem("currentrandomid");
                return $.ajax({
                    url: "/pictureci/randomMode/" + cgame + "/guess",
                    type: "POST",
                    data: JSON.stringify(att),
                    contentType: "application/json"
                });
            }
        },
        tempo: function () {
            numero = parseInt($("#Restante").text());
            var tiempo = setInterval(function () {
                numero = numero - 1;
                $("#Restante").text(numero);
                if (numero === 0) {
                    $("#Restante").text("LA PARTIDA HA FINALIZADO");
                    clearInterval(tiempo);
                    timeOut();
                }
            }, 1000);
        },
        queryUsers: function () {
            $.get("/users/", callback);
        },
        queryPlayers: function () {
            $.get("/players/" + $("#topic").val(), callbackPlayers);
        },
        queryGames: function () {
            $.get("/pictureci/finishedGames", callbackFinishedGames);
        },
        finishedGames: function () {
            location.href = "finishedGames.html";
        },
        registro: function () {
            location.href = "registerUser.html";
        },
        principal: function () {
            location.href = "index.html";
        },
        inicioSesion: function () {
            location.href = "sesion.html";
        },
        normalGame: function () {
            location.href = "partidaNormal.html";
        },
        randomGame: function () {
            location.href = "partidaRandom.html";
        },
        backToGameMode: function () {
            location.href = "GameMode.html";
        }
    };
})();