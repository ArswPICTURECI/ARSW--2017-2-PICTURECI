/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.cache;

import edu.eci.arsw.model.Game;
import edu.eci.arsw.model.Player;
import edu.eci.arsw.model.RandomGame;
import edu.eci.arsw.model.entities.DrawingGuess;
import edu.eci.arsw.model.entities.GameException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author rami
 */
@Service
public class PictureciInMemoryCache implements PictureciCache {

    private final ConcurrentMap<Integer, Game> gamesState = new ConcurrentHashMap<>();
    private int lastRandomRoom = 1;

    @Override
    public void createGame(int gameid, String word) throws CacheException {
        Game new_game = new Game(word);
        gamesState.putIfAbsent(gameid, new_game);
    }

    @Override
    public Game getGame(int gameid, int mode) throws CacheException {
        switch (mode) {
            case Game.NORMAL:
                if (gamesState.containsKey(gameid)) {
                    return gamesState.get(gameid);
                } else {
                    throw new CacheException("El juego no existe");
                }
            case Game.RANDOM:
                if (gamesState.containsKey((-1) * gameid)) {
                    return gamesState.get((-1) * gameid);
                } else {
                    throw new CacheException("El juego no existe");
                }
            default:
                throw new CacheException("Invalid State");
        }
    }

    @Override
    public void deleteGame(int gameid) throws CacheException {
        gamesState.remove(gameid);
    }

    @Override
    public void addPlayer(int gameid, Player player) throws CacheException {
        synchronized (gamesState) {
            if (gamesState.containsKey(gameid)) {
                try {
                    gamesState.get(gameid).addPlayer(player);
                } catch (GameException ex) {
                    Logger.getLogger(PictureciInMemoryCache.class.getName()).log(Level.SEVERE, null, ex);
                    throw new CacheException(ex.getMessage());
                }
            } else {
                throw new CacheException("NO hay juego");
            }
        }

    }

    @Override
    public List<Game> getAllGames() throws CacheException {
        return new ArrayList<>(gamesState.values());
    }

    @Override
    public List<Player> getAllPlayers() throws CacheException {
        List<Game> games = getAllGames();
        LinkedList<Player> players = new LinkedList<>();
        games.stream().forEach((g) -> {
            players.addAll(g.getPlayers());
        });
        return players;
    }

    @Override
    public void deletePlayer(int gameid, String player) throws CacheException {
        synchronized (gamesState) {
            if (gamesState.containsKey(gameid)) {
                try {
                    gamesState.get(gameid).deletePlayer(player);
                } catch (GameException ex) {
                    throw new CacheException("Partida: " + gameid + " - " + ex.getMessage());
                }
            } else if (gamesState.containsKey((-1) * gameid)) {
                try {
                    gamesState.get((-1) * gameid).deletePlayer(player);
                } catch (GameException ex) {
                    throw new CacheException("Partida Aleatoria: " + gameid + " - " + ex.getMessage());
                }
            } else {
                throw new CacheException("El juego " + gameid + " no existe");
            }
        }
    }

    @Override
    public int joinRandomGame(String user) throws CacheException {
        while (true) {
            Game randomgame = gamesState.get((-1) * lastRandomRoom);
            if (randomgame == null) {
                try {
                    randomgame = new RandomGame("perro");
                    randomgame.addPlayer(new Player(user, (-1) * lastRandomRoom, RandomGame.RANDOM_ROL));
                    gamesState.putIfAbsent((-1) * lastRandomRoom, randomgame);
                    break;
                } catch (GameException ex) {
                    throw new CacheException(ex.getMessage());
                }
            } else if (randomgame.ready()) {
                ++lastRandomRoom;
            } else {
                try {
                    randomgame.addPlayer(new Player(user, (-1) * lastRandomRoom, RandomGame.RANDOM_ROL));
                    break;
                } catch (GameException ex) {
                    throw new CacheException(ex.getMessage());
                }
            }
        }
        return gamesState.get((-1) * lastRandomRoom).getPlayerRol(user);
    }

    @Override
    public int currentRandomRoom() {
        return lastRandomRoom;
    }

    @Override
    public boolean checkIfReady(int gameid, int mode) throws CacheException {
        switch (mode) {
            case Game.NORMAL:
                if (gamesState.containsKey(gameid)) {
                    return gamesState.get(gameid).ready();
                } else {
                    throw new CacheException("El juego " + gameid + " no existe");
                }
            case Game.RANDOM:
                if (gamesState.containsKey((-1) * gameid)) {
                    return gamesState.get((-1) * gameid).ready();
                } else {
                    throw new CacheException("El juego aleatorio" + gameid + " no existe");
                }
            default:
                throw new CacheException("Invalid State game: " + gameid);
        }
    }

    @Override
    public boolean tryWord(int gameid, int mode, DrawingGuess attempt) throws CacheException {
        switch (mode) {
            case Game.RANDOM:
                synchronized (gamesState) {
                    if (gamesState.containsKey((-1) * gameid)) {
                        return gamesState.get((-1) * gameid).tryWord(attempt);
                    } else {
                        throw new CacheException("El Juego aleagorio: " + gameid + " no existe");
                    }
                }
            case Game.NORMAL:
                synchronized (gamesState) {
                    if (gamesState.containsKey(gameid)) {
                        return gamesState.get(gameid).tryWord(attempt);
                    } else {
                        throw new CacheException("El Juego: " + gameid + " no existe");
                    }
                }
            default:
                throw new CacheException("Invalid State");
        }
    }
}
