/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.net.InetAddress;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *
 * @author Gustavo
 */
public class Peer {
    
    private InetAddress address;
    private int id;
    private int port;
    private PrivateKey privateKey;
    private PublicKey publicteKey;

    /**
     * Construtora do Peer
     * @param id
     * @param key
     * @param address
     * @param port 
     */
    public Peer(int id, KeyPair key, InetAddress address, int port) {
        this.address = address;
        this.port = port;
        this.id = id;
        this.privateKey = key.getPrivate();
        this.publicteKey = key.getPublic();
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getId() {
        return id;
    }   

    public int getPort() {
        return port;
    }
    
    public String getSettings(){
        String settings = "ID: "+id+"; Address: "+address+":"+port;
        return settings;
    }
    
}
