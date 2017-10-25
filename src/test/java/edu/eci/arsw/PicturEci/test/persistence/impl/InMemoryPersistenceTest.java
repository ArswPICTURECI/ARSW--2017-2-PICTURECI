/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.PicturEci.test.persistence.impl;


import edu.eci.arsw.model.User;
import edu.eci.arsw.persistence.UserPersistenceException;
import edu.eci.arsw.persistence.impl.InMemoryPicturEciPersistence;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hcadavid
 */
public class InMemoryPersistenceTest {
    
    @Test
    public void registerNewUser() throws UserPersistenceException{
        InMemoryPicturEciPersistence ipep= new InMemoryPicturEciPersistence();
        User us1=new User("Camilo");
        ipep.registerUser(us1);
        assertEquals(ipep.getUsers().get(0).getName(), "Camilo");
    }
    
    @Test
    public void registerUserWithRolAndRoom() throws UserPersistenceException{
        InMemoryPicturEciPersistence ipep= new InMemoryPicturEciPersistence();
        User us1=new User("Laura","dibuja",1);
        User us2=new User("Andres","dibuja",1);
        ipep.registerUser(us1);
        ipep.registerUser(us2);
        assertEquals(ipep.getUsers().get(0).getRol(), "dibuja");
        assertEquals(ipep.getUsers().get(1).getName(), "Andres");
    }
}
