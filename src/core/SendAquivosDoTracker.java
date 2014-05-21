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



/**
 *
 * @author Gustavo
 */
class SendAquivosDoTracker implements Runnable{
    private int port;
    private InetAddress address;
    private ArrayList<Arquivo> arquivosDoTracker;
    private String busca;
    private DatagramSocket socketUnicast;

    public SendAquivosDoTracker(String busca, InetAddress add, int port, ArrayList<Arquivo> array) {
        try {
            this.address = add;
            this.port = port;
            this.busca = busca;
            this.arquivosDoTracker = array;
            this.socketUnicast = new DatagramSocket();
            new Thread(this).start();
        } catch (SocketException ex) {
            Logger.getLogger(SendAquivosDoTracker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void run() {
        while(arquivosDoTracker.size() == 0){
            try {
                System.out.println("arquivosDoTrackerVazio");
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(SendAquivosDoTracker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
        try {              
            byte[] buf;
            DatagramPacket pack;
            for(int i = 0; i < arquivosDoTracker.size(); i++){                
                String fileName = arquivosDoTracker.get(i).getNome();                
                if(fileName.substring(0,busca.length()).equals(busca)){
                    buf = arquivosDoTracker.get(i).getNome().getBytes();
                    pack = new DatagramPacket(buf, buf.length, address, port);
                    socketUnicast.send(pack);
                }
            }
            buf = "fim dos arquivos".getBytes();
            pack = new DatagramPacket(buf, buf.length, address, port);
            socketUnicast.send(pack);
            socketUnicast.close();
            Thread.currentThread().interrupt();
            //System.out.println("Terminou UNICAST");
        } catch (IOException ex) {
            Logger.getLogger(GetAquivosDoPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
