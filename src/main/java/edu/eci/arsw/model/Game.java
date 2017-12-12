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

    public static final int RANDOM = 99;
    public static final int NORMAL = 100;

    protected static final int MAX_DIB = 2;
    protected static final int MAX_ADV = 1;

    protected int count_dibujan;
    protected int count_adivinan;
    protected String word;
    protected int winner;

    protected ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();

    public Game(String word) {
        this.count_adivinan = 0;
        this.count_dibujan = 0;
        this.word = word;
        this.winner = DIBUJAN;
    }

    protected Game(Game game) {
        this.count_adivinan = game.getCount_adivinan();
        this.count_dibujan = game.getCount_dibujan();
        this.word = game.word;
        this.winner = game.winner;
        this.players = game.players;
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

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
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
    
    public void setPlayerScore(Player player) throws GameException{
        if(player.getRol()==DIBUJAN){
            player.setScore(100);            
        }else if (player.getRol()==ADIVINAN){
            player.setScore(50);        
        }      
    }

    public void deletePlayer(String user) throws GameException {
        if (players.containsKey(user)) {
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

    public int getPlayerRol(String user) {
        return players.get(user).getRol();
    }

    public boolean ready() {
        return players.size() == (MAX_DIB + MAX_ADV);
    }
}
