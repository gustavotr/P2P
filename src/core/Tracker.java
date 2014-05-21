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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Arquivo;
import model.MulticastSocketP2P;
import model.Peer;
import util.Funcoes;

/**
 *
 * @author Gustavo
 */
public class Tracker extends Thread{
    
    private static ArrayList<Arquivo> arquivosDoTracker;
    private MulticastSocketP2P multicastSocket;
    private DatagramSocket socketUDP;
    private int idTracker;
    private Processo processo;

    /**
     * Numero da porta UDP do Tracker para receber requisicoes
     */
    public static final int UDPPort = 6066;
    
    /**
     * Construtor do Tracker
     * @param id recebe um int que e o id do processo que o gerou
     */
    public Tracker(Processo processo) {
        try {            
            this.processo = processo;
            idTracker = processo.getId();
            arquivosDoTracker = new ArrayList<>();
            multicastSocket = new MulticastSocketP2P();
            socketUDP  = new DatagramSocket(UDPPort);
            TrackerHello trackerHello = new TrackerHello();
            this.start();
        } catch (IOException ex) {
            Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }       
    
    @Override
    /**
     * Tracker fica escutando o Unicast 
     */
    public void run() {        
        while (true) { 
            try {
                byte buf[] = new byte[1024];
                DatagramPacket pack = new DatagramPacket(buf, buf.length);                
                socketUDP.receive(pack);
                String resposta = new String(pack.getData());
                InetAddress add = pack.getAddress();
                int port = pack.getPort();
               // System.out.println("UNICAST -> " + resposta);  
               // System.out.println( new String("\tFrom: " + add.getHostAddress() + ":" + port) );
                String respostaEsperada = Funcoes.to1024String("Peer XX diz: oi tracker!");
                if(resposta.substring(8).equals(respostaEsperada.substring(8))){
                    int id = Integer.parseInt(resposta.substring(5,7));
                    String saudacao = "oi peer " + id;
                    buf = saudacao.getBytes();
                    pack = new DatagramPacket(buf, buf.length, pack.getAddress(), pack.getPort());
                    socketUDP.send(pack); 
                    if(port != Tracker.UDPPort){
//                        GetAquivosDoPeer uni = new GetAquivosDoPeer("Request: getArquivos;", id, add, port, arquivosDoTracker);
                    }
                }
                respostaEsperada = Funcoes.to1024String("Request: buscar(");                
                if(resposta.substring(0,15).equals(respostaEsperada.substring(0,15))){
                    String busca = resposta.substring(16, resposta.lastIndexOf(')') );                     
                    if(port != Tracker.UDPPort){
//                        SendAquivosDoTracker uni = new SendAquivosDoTracker(busca, add, port, arquivosDoTracker);
                    }
                }
                respostaEsperada = Funcoes.to1024String("Request: arquivo(");                
                if(resposta.substring(0,16).equals(respostaEsperada.substring(0,16))){
                    String busca = resposta.substring(17, resposta.lastIndexOf(')') ); 
                    if(port != Tracker.UDPPort){
                        Peer peer = getFileLocation(busca);
                        String location = new String(peer.getAddress().getHostAddress() + ":" + peer.getPort());
                        buf = location.getBytes();
                        DatagramSocket socket = new DatagramSocket();
                        pack = new DatagramPacket(buf, buf.length, add, port);
                        socket.send(pack);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
            }
       }
        
    }

    private Peer getFileLocation(String busca) {
        Peer peer = null;
        for(int i = 0; i < arquivosDoTracker.size(); i++){   
            Arquivo temp = arquivosDoTracker.get(i);
            String nome = temp.getNome();
            nome = nome.substring(0, 4+nome.lastIndexOf(".") );
            if(nome.equals(busca)){
                for(int j = 0; j < MultiCastPeer.getPeers().size(); j++){
                    if(MultiCastPeer.getPeers().get(j).getId() == arquivosDoTracker.get(i).getProcessos().get(0)){
                        peer = MultiCastPeer.getPeers().get(j);
                        return peer;
                    }
                }
            }
        }
        return peer;
    }
 
     public class TrackerHello extends Thread{

        public TrackerHello() {
            this.start();
        }
        
         @Override
        public void run() {        
            while (!multicastSocket.isClosed()) { 
                try {
                    //System.out.println("\nTracker ativo!");
                    multicastSocket.enviarMensagem("eu sou o tracker! ID:"+idTracker+";",processo.getPublicKey());
                    this.sleep(5000);
//                    for(int i = 0; i < arquivosDoTracker.size(); i++){
//                        System.out.println(arquivosDoTracker.get(i).getNome());
//                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
                }
           }
           Thread.currentThread().interrupt();

        }
        
    }
}


