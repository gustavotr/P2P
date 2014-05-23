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
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
    private TrackerHello trackerHello;
    private int UDPPort;
    private KeyPair keyPair;
    
    /**
     * Construtor do Tracker
     * @param processo recebe o processo que o gerou
     */
    public Tracker(Processo processo) {
        try {            
            //this.processo = processo;
            idTracker = processo.getId();
            keyPair = processo.getKeyPair();
            arquivosDoTracker = new ArrayList<>();
            multicastSocket = new MulticastSocketP2P();
            socketUDP  = new DatagramSocket();
            UDPPort = socketUDP.getLocalPort();            
            trackerHello = new TrackerHello();
            this.start();
        } catch (IOException ex) {
            Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }     
        
    @Override
    /**
     * Tracker fica escutando o Unicast por requisicoes dos Peers
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
                String respostaEsperada = "diz: oi tracker!";
                String data = resposta.substring(8,8+respostaEsperada.length());
                if(data.equals(respostaEsperada)){
                    int id = Integer.parseInt(resposta.substring(5,7));                    
                    if(port != UDPPort){
//                        System.out.println("Tracker status: Receber arquivos");
//                        System.out.println("\tFrom: "+add+":"+port);
                        GetFilesFromPeer uni = new GetFilesFromPeer(Funcoes.GET_ARQUIVOS, id, add, port, arquivosDoTracker, keyPair.getPrivate());
                    }
                }else{
                    respostaEsperada = "Request: buscar("; 
                    data = resposta.substring(0,respostaEsperada.length());
                    if(data.equals(respostaEsperada)){
                        String busca = resposta.substring(16, resposta.lastIndexOf(')') );                     
                        if(port != UDPPort){
                           //System.out.println("Tracker status: Processar busca");
                            SendFilesFromTracker uni = new SendFilesFromTracker(busca, add, port, arquivosDoTracker, keyPair.getPrivate());
                        }
                    }else{
                        respostaEsperada = "Request: arquivo("; 
                        //System.out.println("Tracker receberu requisicao de arquivo");
                        data = resposta.substring(0,respostaEsperada.length());
                        if(data.equals(respostaEsperada)){
                            String busca = resposta.substring(17, resposta.lastIndexOf(')') ); 
                            if(port != UDPPort){
                                Peer peer = getFileLocation(busca);
                                String location = new String(peer.getAddress().getHostAddress() + ":" + peer.getPort());
                                buf = Funcoes.encrypt(keyPair.getPrivate(), location.getBytes());
                                pack = new DatagramPacket(buf, buf.length, add, port);
                                socketUDP.send(pack);
                            }
                        }
                    }
                }
            } catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException ex) {
                Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
            }
       }
        
    }
    
    public PublicKey getPublicKey(){
        return keyPair.getPublic();
    }
    
    public InetAddress getAddress(){
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int getUDPPort() {
        return UDPPort;
    }
    
    private Peer getFileLocation(String busca) {        
        for (Arquivo temp : arquivosDoTracker) {
            String nome = temp.getNome();
            if (nome.equals(busca)) {
                for(Peer peer : MultiCastPeer.getPeers()){
                    if(peer.getId() == temp.getProcessos().get(0)){
                        return peer;
                    }
                }
            }
        }
        return null;
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
                    multicastSocket.enviarMensagem(Funcoes.TRACKER_HELLO+idTracker+";Porta:"+UDPPort+";",keyPair.getPublic());
                    this.sleep(4000);
//                    for(int i = 0; i < arquivosDoTracker.size(); i++){
//                        System.out.println(arquivosDoTracker.get(i).getNome());
//                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
                }
           }
        }
        
    }
}


