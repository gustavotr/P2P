/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 *
 * @author a1097075
 */
public class MulticastSocketP2P extends MulticastSocket{
    /**
     * Endereco do Multicast
     */
    private final String HOST = "229.10.10.100";
    /**
     * Timeout default do multicast
     */
    private final int TIMEOUT = 10000;
    /**
     * InetAddres do multicast
     */
    private final InetAddress group;

    /**
     * Numero da porta usada pelo multicast
     */
    public static final int MULTICAST_PORT = 5050;

    public MulticastSocketP2P() throws IOException {
        this(MULTICAST_PORT);
    }
    
    
    
    public MulticastSocketP2P(int port) throws IOException {
        super(port);
        group = InetAddress.getByName(HOST);                    
        this.setSoTimeout(TIMEOUT);
        this.joinGroup(group);
    }  

    public InetAddress getGroup() {
        return group;
    }

   public String getHOST() {
        return HOST;
    } 
    
    
    /**
     * Envia uma mensagem para o grupo multicast
     *
     * @param msg
     *
     */
    public void enviarMensagem(String msg) {  
        byte[] byteMsg = msg.getBytes();
        DatagramPacket msgOut = new DatagramPacket(byteMsg, byteMsg.length, group, MULTICAST_PORT);
        try {            
            this.send(msgOut);    
        } catch (IOException e) {
            System.out.println("Erro I/O: " + e.getLocalizedMessage());
        }
    }
    
}
