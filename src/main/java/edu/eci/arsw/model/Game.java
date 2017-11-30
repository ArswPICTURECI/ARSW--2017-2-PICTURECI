/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.model;

import com.gs.collections.impl.map.mutable.ConcurrentHashMap;
import edu.eci.arsw.model.entities.DrawingGuess;
import edu.eci.arsw.model.entities.GameException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rami
 */
public class Game {

    public static final int DIBUJAN = -1;
    public static final int ADIVINAN = -2;

    protected static final int MAX_DIB = 1;
    protected static final int MAX_ADV = 1;

    protected int count_dibujan;
    protected int count_adivinan;
    protected String word;
    protected String winner;

    private final ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();

    public Game(String word) {
        this.count_adivinan = 0;
        this.count_dibujan = 0;
        this.word = word;
        this.winner = "";
    }

    public void setPlayers() {

    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public int getCount_dibujan() {
        return count_dibujan;
    }

    public void setCount_dibujan(int count_dibujan) {
        this.count_dibujan = count_dibujan;
    }

    public int getCount_adivinan() {
        return count_adivinan;
    }

    public void setCount_adivinan(int count_adivinan) {
        this.count_adivinan = count_adivinan;
    }

    public boolean tryWord(DrawingGuess attempt) {
        return attempt.getPhrase().equalsIgnoreCase(this.word) || attempt.getPhrase().contains(this.word);
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void addPlayer(Player player) throws GameException {
        if (player.getRol() == DIBUJAN) {
            if (count_dibujan == MAX_DIB) {
                throw new GameException("Rol Dibujan lleno");
            }
            ++count_dibujan;
            players.putIfAbsent(player.getName(), player);
        } else if (player.getRol() == ADIVINAN) {
            if (count_adivinan == MAX_ADV) {
                throw new GameException("Rol Adivinan lleno");
            }
            ++count_adivinan;
            players.putIfAbsent(player.getName(), player);
        }
    }

    public void deletePlayer(String user) throws GameException {
        if (players.contains(user)) {
            int rol = players.get(user).getRol();
            if (rol == ADIVINAN) {
                --count_adivinan;
            } else if (rol == DIBUJAN) {
                --count_dibujan;
            }
            players.remove(user);
        } else {
            throw new GameException("Jugador " + user + " no se encuentra en la partida");
        }
    }

    public boolean ready() {
        return players.size() == (MAX_DIB + MAX_ADV);
    }
}
