/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.services;

import edu.eci.arsw.cache.CacheException;
import edu.eci.arsw.cache.PictureciCache;
import edu.eci.arsw.model.Game;
import edu.eci.arsw.model.Player;
import edu.eci.arsw.model.User;
import edu.eci.arsw.model.entities.DrawingGuess;
import edu.eci.arsw.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.eci.arsw.persistence.PicturEciPersistence;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daferrotru
 */
@Service
public class PicturEciServices {

    @Autowired
    PicturEciPersistence pep = null;

    @Autowired
    PictureciCache cache = null;

    public void createGame(int gameid, String word) throws CacheException {
        cache.createGame(gameid, word);
    }

    public Game getCurrentGame(int gameid, int mode) throws CacheException {
        return cache.getGame(gameid, mode);
    }

    public void registerUser(User user) throws PersistenceException {
        pep.registerUser(user);
    }

    public void removeFromCache(int gameid, int mode) throws CacheException {
        cache.deleteGame(gameid, mode);
    }

    public List<User> getAllUsers() {
        return pep.getAllUsers();
    }

    public User getUser(String userName) throws PersistenceException {
        return pep.getUser(userName);
    }

    public void addUser(User user) throws PersistenceException {
        pep.addUser(user);
    }

    public void addFinishedGame(int gameid, Game game) throws PersistenceException {
        pep.addFinishedGame(gameid, game);
    }

    public Game getFinishedGame(int gameid) throws PersistenceException {
        return pep.getFinishedGame(gameid);
    }

    public void addPlayer(int gameid, Player player) throws CacheException {
        cache.addPlayer(gameid, player);
    }

    public List<Game> getAllGames() throws CacheException {
        return cache.getAllGames();
    }

    //PLAYERS
    public List<Player> getAllPLayers() throws CacheException {
        return cache.getAllPlayers();
    }

    public boolean gameReady(int gameid, int mode) throws CacheException {
        return cache.checkIfReady(gameid, mode);
    }

    public List<Player> getPlayersFrom(int gameid, int mode) {
        try {
            return cache.getGame(gameid, mode).getPlayers();
        } catch (CacheException ex) {
            Logger.getLogger(PicturEciServices.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }

    public void deletePlayerFrom(int gameid, String player) throws CacheException {
        cache.deletePlayer(gameid, player);
    }

    public int joinRandomGame(String user) throws CacheException {
        return cache.joinRandomGame(user);
    }

    public int currentRandomRoom() throws CacheException {
        return cache.currentRandomRoom();
    }

    public boolean tryWord(int gameid, int mode, DrawingGuess attempt) throws CacheException {
        return cache.tryWord(gameid, mode, attempt);
    }
    
    //Finished Games
    public List<Game>getFinishedGames() throws PersistenceException{
        return pep.getFinishedGames();
    }
    
    
}
