package core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.MulticastSocketP2P;
import model.Peer;
import util.Funcoes;

public class MultiCastPeer extends Thread {

    private MulticastSocketP2P multicastSocket;    
    private static Processo processo;
    private static ArrayList<Peer> peers;

    /**
     * Construtora do Multicast por processo.
     *
     * @param processo Recebe o processo como parâmetro.
     */
    public MultiCastPeer(Processo processo) {
        try {
            this.processo = processo;
            multicastSocket = new MulticastSocketP2P();
            //System.out.println("Meu ID: "+processo.getId());
            this.start();
        } catch (IOException ex) {
            Logger.getLogger(MultiCastPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override    
    /**
     * Verifica se o Tracker esta online
     */
    public void run() {        
        while (true) {
            if (!processo.knowTracker) {
                System.out.println(eleicao());
            }else{  
                try {                                        
                    byte[] buf = new byte[1024];
                    DatagramPacket pack = new DatagramPacket(buf, buf.length);
                    multicastSocket.setSoTimeout(5000);
                    multicastSocket.receive(pack);
                    byte[] messgage = Arrays.copyOfRange(buf, 0, Funcoes.getKeyIndex() - 1);
                    String resposta = new String(messgage);
                    System.out.println("MULTiCAST <- " + resposta);
                    System.out.println( new String("\tFrom: " + pack.getAddress().getHostAddress() + ":" + pack.getPort()) );
                    this.sleep(1000);
                } catch(SocketTimeoutException ex){
                    System.out.println("Tracker caiu!");                    
                    processo.knowTracker = false;                    
                }catch (SocketException ex) {
                    Logger.getLogger(MultiCastPeer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(MultiCastPeer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public Processo getProcesso() {
        return processo;
    }
    
    public String eleicao(){
        return eleicao(4);
    }

    public static ArrayList<Peer> getPeers() {
        return peers;
    }
    
        
    /**
     * Funcao que aguarda a conexao de 4 peers
     * e faz uma eleicao para saber quem sera o tracker
     * @return uma string com os dados do tracker eleito
     */
    public String eleicao(int peersConnected){
        
        peers = new ArrayList<>();
        
        String peer = "Peer id:"+processo.getId()+";";
        multicastSocket.enviarMensagem(peer, processo.getPublicKey());
        
        while(peers.size() < peersConnected){
            
            byte[] buff = new byte[1024];
            DatagramPacket pack = new DatagramPacket(buff, buff.length);
            
            try {
                multicastSocket.receive(pack);
            }catch (SocketTimeoutException ex){                
                return eleicao(peers.size());
            }catch (IOException ex) {
                Logger.getLogger(MultiCastPeer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //Testa se a mesnsagem recebida e de um peer novo            
            
            byte[] messgage = Arrays.copyOfRange(buff, 0, Funcoes.getKeyIndex() - 1);                       
            byte[] key = Arrays.copyOfRange(buff, Funcoes.getKeyIndex(), buff.length);
            String resposta = new String(messgage);
            try {
                //PrivateKey keyRecebida = KeyFactory.getInstance("RSA", "BC").generatePrivate(new PKCS8EncodedKeySpec(key));
                PublicKey keyRecebida = KeyFactory.getInstance("RSA", "BC").generatePublic(new X509EncodedKeySpec(key));
                //System.out.println("PrivateKey recebida: " + Arrays.toString(keyRecebida.getEncoded()));
                //System.out.println("PrivateKey processo: " + Arrays.toString(processo.getPublicKey().getEncoded()));
                
                
                String respostaEsperada = Funcoes.to1024String("Peer id:");
                
                if(resposta.substring(0,7).equals(respostaEsperada.substring(0,7))){                   
                   int tempID = Integer.parseInt(resposta.substring(8,10));               
                   if(!peersHasID(tempID)){
                       Peer newPeer = new Peer(tempID, keyRecebida, pack.getAddress(),pack.getPort());
                       peers.add(newPeer);
                       multicastSocket.enviarMensagem(peer, processo.getPublicKey());
                       System.out.println("Novo peer: "+newPeer.getSettings());
                   }
                }
                
                
                
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(MultiCastPeer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchProviderException ex) {
                Logger.getLogger(MultiCastPeer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeySpecException ex) {
                Logger.getLogger(MultiCastPeer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            
            
            //Testa se a mensagem recebida e de um tracker
            //caso o processo tenha sido adicionado depois de uma eleicao ja feita
            
            //respostaEsperada = Funcoes.to1024String("eu sou o tracker! ID:");
            //resposta = new String(pack.getData());
            
//            if(resposta.substring(0,20).equals(respostaEsperada.substring(0, 20))){
//                int tempID = Integer.parseInt(resposta.substring(21,23));
//                Peer tempPeer = new Peer(tempID, pack.getAddress(), pack.getPort());
//                processo.setTheTracker(tempPeer);
//                return "Tracker("+tempPeer.getSettings()+")";
//            }
            
            
        }
        
        //System.out.println("Peers adicionados");

        Peer peerEleito = peers.get(0);
        int idProcessoEleito = peerEleito.getId(); 

        for (int i = 1; i < this.peers.size(); i++) {
            if (idProcessoEleito < peers.get(i).getId()) {
                peerEleito = peers.get(i);
                idProcessoEleito = peerEleito.getId();               
            }
        }

        processo.setTheTracker(peerEleito);
        String tracker = "Tracker("+peerEleito.getSettings()+")";

        return tracker;
    }
    
    private boolean peersHasID(int tempID) {
        for(int i = 0; i < peers.size(); i++){
            if(peers.get(i).getId() == tempID){
                return true;
            }
        }
        return false;
    }
}
