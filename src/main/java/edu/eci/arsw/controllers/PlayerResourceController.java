/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.controllers;

import edu.eci.arsw.cache.CacheException;
import edu.eci.arsw.model.Game;
import edu.eci.arsw.model.Player;
import edu.eci.arsw.services.PicturEciServices;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author daferrotru
 */
@RestController
@RequestMapping(value = "/players")
public class PlayerResourceController {

    @Autowired
    PicturEciServices pes = null;

    @Autowired
    SimpMessagingTemplate msmt = null;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getPlayers() {
        try {
            return new ResponseEntity<>(pes.getAllPLayers(), HttpStatus.ACCEPTED);
        } catch (CacheException ex) {
            Logger.getLogger(PlayerResourceController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @RequestMapping(value = "/normalMode/dibujan-{user}", method = RequestMethod.POST)
    public ResponseEntity<?> postDibujanGameNormalMode(@PathVariable String user, @RequestBody Integer gameid) {
        try {
            //pes.addPlayer(gameid, new Player("guest", Game.DIBUJAN));
            pes.addPlayer(gameid, new Player(user, gameid, Game.DIBUJAN));
            boolean ready = pes.gameReady(gameid, Game.NORMAL);
            System.out.println("Jugador Agregado Sala (" + gameid + ") + : " + user + " Rol: Dibuja");
            if (ready) {
                Thread.sleep(1000);
                System.out.println("Game: " + gameid + " is ready");
                msmt.convertAndSend("/topic/ready." + gameid, Game.DIBUJAN);
            }
            return new ResponseEntity<>(ready, HttpStatus.OK);
        } catch (CacheException ex) {
            Logger.getLogger(PictureciResourceController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (InterruptedException ex) {
            Logger.getLogger(PlayerResourceController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/normalMode/adivinan-{user}", method = RequestMethod.POST)
    public ResponseEntity<?> postAdivinanGameNormalMode(@PathVariable String user, @RequestBody Integer gameid) {
        try {
            pes.addPlayer(gameid, new Player(user, gameid, Game.ADIVINAN));
            boolean ready = pes.gameReady(gameid, Game.NORMAL);
            System.out.println("Jugador Agregado Sala (" + gameid + ") + : " + user + " Rol: Adivina");
            if (ready) {
                Thread.sleep(1000);
                System.out.println("Game: " + gameid + " is ready");
                msmt.convertAndSend("/topic/ready." + gameid, Game.ADIVINAN);
            }
            return new ResponseEntity<>(ready, HttpStatus.OK);
        } catch (CacheException ex) {
            Logger.getLogger(PictureciResourceController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (InterruptedException ex) {
            Logger.getLogger(PlayerResourceController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @RequestMapping(value = "/randomGame/{user}", method = RequestMethod.PUT)
    public ResponseEntity<?> putPlayerInRandom(@PathVariable String user, @RequestBody Integer subscription) {
        try {
            int rol = pes.joinRandomGame(user);
            System.out.println("Usuario :" + user + " agregado a sala aleatoria");
            if (pes.gameReady(subscription, Game.RANDOM)) {
                Thread.sleep(1000);
                System.out.println("Game: " + subscription + " is ready");
                msmt.convertAndSend("/topic/rndready." + subscription, rol);
            }
            return new ResponseEntity<>(rol, HttpStatus.OK);
        } catch (CacheException ex) {
            Logger.getLogger(PlayerResourceController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (InterruptedException ex) {
            Logger.getLogger(PlayerResourceController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{gameid}/{user}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePlayerFromRoom(@PathVariable Integer gameid, @PathVariable String user) {
        try {
            pes.deletePlayerFrom(gameid, user);
            System.out.println("Desconectado el jugador: " + user + " - del juego: " + gameid);
            msmt.convertAndSend("/topic/disconnect." + gameid, gameid);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (CacheException ex) {
            Logger.getLogger(PlayerResourceController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{gameid}", method = RequestMethod.GET)
    public ResponseEntity<?> getPlayersGame(@PathVariable Integer gameid) {
        return new ResponseEntity<>(pes.getPlayersFrom(gameid, Game.NORMAL), HttpStatus.OK);
    }
    

    @RequestMapping(value = "/{players}", method = RequestMethod.PUT)
    public ResponseEntity<?> setScore(@PathVariable List users) {
        //sgsg
        return new ResponseEntity<>(pes.getPlayersFrom(1, Game.NORMAL), HttpStatus.OK);
    }
}