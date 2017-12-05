/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.persistence.impl;

import edu.eci.arsw.model.FinishedGame;
import edu.eci.arsw.model.Game;
import edu.eci.arsw.model.Player;
import edu.eci.arsw.model.User;
//import edu.eci.arsw.persistence.DrawingNotFoundException;
//import edu.eci.arsw.persistence.DrawingPersistenceException;
//import java.util.HashMap;
import org.springframework.stereotype.Service;
import edu.eci.arsw.persistence.PicturEciPersistence;
import edu.eci.arsw.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 *
 * @author daferrotru
 */
@Service
public class InMemoryPicturEciPersistence implements PicturEciPersistence {

    private final ConcurrentMap<String, User> users = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<Game> finishedGames = new ConcurrentLinkedDeque<>();

    public InMemoryPicturEciPersistence() {
        User u1 = new User("Daniel", "123");
        User u2 = new User("Camilo", "123");
        User u3 = new User("Ana", "123");

        users.putIfAbsent("Daniel", u1);
        users.putIfAbsent("Camilo", u2);
        users.putIfAbsent("Ana", u3);
    }

    @Override
    public void registerUser(User user) throws PersistenceException {
        synchronized (users) {
            if (users.get(user.getName()) == null) {
                users.put(user.getName(), user);
            } else {
                throw new PersistenceException("Username is already taken");
            }
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(String username) throws PersistenceException {
        User user = users.get(username);
        if (user != null) {
            return user;
        } else {
            throw new PersistenceException("Username: " + username + " was not found");
        }
    }

    @Override
    public void addUser(User user) throws PersistenceException {
        users.putIfAbsent(user.getName(), user);

    }

    @Override
    public void addFinishedGame(int gameid, Game game) throws PersistenceException {
        finishedGames.add(new FinishedGame(game, gameid));
    }

    @Override
    public Game getFinishedGame(int gameid) throws PersistenceException {
        throw new UnsupportedOperationException("Por implementar");
    }

    @Override
    public List<Game> getFinishedGames() throws PersistenceException {
        return finishedGames.stream().collect(Collectors.toList());
    }

    @Override
    public void setScore(Player player) throws PersistenceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
