/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Arquivo;
import util.Funcoes;



/**
 *
 * @author Gustavo
 */
class SendFilesFromTracker implements Runnable{
    private int port;
    private InetAddress address;
    private ArrayList<Arquivo> arquivosDoTracker;
    private String busca;
    private DatagramSocket socketUnicast;

    public SendFilesFromTracker(String busca, InetAddress add, int port, ArrayList<Arquivo> array) {
        try {
            this.address = add;
            this.port = port;
            this.busca = busca;
            this.arquivosDoTracker = array;
            this.socketUnicast = new DatagramSocket();
            new Thread(this).start();
        } catch (SocketException ex) {
            Logger.getLogger(SendFilesFromTracker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void run() {
        while(arquivosDoTracker.size() == 0){
            try {
                System.out.println("arquivosDoTrackerVazio");
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(SendFilesFromTracker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
        try {              
            byte[] buf;
            DatagramPacket pack;
            for(int i = 0; i < arquivosDoTracker.size(); i++){                
                String fileName = arquivosDoTracker.get(i).getNome();                
                if(fileName.substring(0,busca.length()).equals(busca)){
                    String data = "fileName:"+arquivosDoTracker.get(i).getNome()+";";
                    buf = data.getBytes();
                    pack = new DatagramPacket(buf, buf.length, address, port);
                    socketUnicast.send(pack);
                }
            }
            String data = "status:"+Funcoes.END_OF_FILES+";";
            buf = data.getBytes();
            pack = new DatagramPacket(buf, buf.length, address, port);
            socketUnicast.send(pack);
            socketUnicast.close();
            Thread.currentThread().interrupt();
            System.out.println("Tracker terminou busca");
        } catch (IOException ex) {
            Logger.getLogger(GetFilesFromPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
