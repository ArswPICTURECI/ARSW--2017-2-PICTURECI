/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.model;

/**
 *
 * @author daferrotru
 */
public class Player {

    private String name = null;
    private int rol;
    private int room;
    private int score;

    public Player(String name, int room, int rol) {
        this.name = name;
        this.rol = rol;
        this.room = room;
        this.score = 0;
    }
    public Player(String name, int room, int rol,int score) {
        this.name = name;
        this.rol = rol;
        this.room = room;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = this.score+score;
    }

    @Override
    public String toString() {
        return "User{" + "name=" + name + ", rol=" + rol + ", room=" + room + ", score=" + score + '}';
    }
}
