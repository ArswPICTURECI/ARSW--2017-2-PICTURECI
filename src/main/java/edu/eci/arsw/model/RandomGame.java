/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.model;

import edu.eci.arsw.model.entities.GameException;
import java.util.Random;

/**
 *
 * @author rami
 */
public class RandomGame extends Game {
    
    public static final int RANDOM_ROL = -3;
    
    private final Random random_rol;
    
    public RandomGame(String word) {
        super(word);
        random_rol = new Random();
    }
    
    @Override
    public void addPlayer(Player player) throws GameException {
        if (player.getRol() != -3) {
            throw new GameException("El jugador no deberia tener un rol asignado");
        }
        if (count_adivinan < MAX_ADV && count_dibujan == MAX_DIB) {
            player.setRol(ADIVINAN);
            ++count_adivinan;
            players.putIfAbsent(player.getName(), player);
            System.out.println(player);
        } else if (count_adivinan == MAX_ADV && count_dibujan < MAX_DIB) {
            player.setRol(DIBUJAN);
            ++count_dibujan;
            player.setScore(30);
            players.putIfAbsent(player.getName(), player);
            System.out.println(player);
        } else if (count_adivinan < MAX_ADV && count_dibujan < MAX_DIB) {
            if (random_rol.nextBoolean()) {
                player.setRol(ADIVINAN);
                ++count_adivinan;
            } else {
                player.setRol(DIBUJAN);
                player.setScore(30);
                ++count_dibujan;
            }
            players.putIfAbsent(player.getName(), player);
            System.out.println(player);
        } else {
            throw new GameException("Invalid state");
        }
    }
}
