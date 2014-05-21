/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JPanel;
import model.Peer;
import util.Funcoes;
import view.GUITelaInicial;

/**
 *
 * @author a1097075
 */
public class Processo implements Runnable {
    
    private int id;
    private Peer tracker;
    private RSA rsa;
    private Tracker myTracker;
    public boolean knowTracker;
    public boolean isReady;
    private JFrame jFrame;
    private JPanel telaInicial;
    private Vector<String> arquivosDoProcesso;
    private Vector<String> arquivosBuscados;
    private String folderPath;
    private boolean buscou;
    private String stringBuscada;
    private boolean changePanel;

    public Processo() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
        Random rnd = new Random();
        id = 10 + rnd.nextInt(89);
        knowTracker = false; 
        isReady = false;
        rsa = new RSA();
        buscou = false;
        changePanel = false;
        folderPath = "src/arquivos/processo"+(rnd.nextInt(4)+1);  
        setArquivos();
        initJFrame();        
        
        new MultiCastPeer(this);
        new Thread(this).start();
        
    }
    
    /**
     * Procura por todos os arquivos que estão na pasta do processo
     * e os adiciona ao Vector arquivosDoProcesso
     */
    public void setArquivos(){
        arquivosDoProcesso = new Vector<String>();
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        
            for(int i = 0; i < listOfFiles.length; i++){
                arquivosDoProcesso.add(listOfFiles[i].getName());
            }
    }

    public String getStringBuscada() {
        return stringBuscada;
    }

    public Vector<String> getArquivosBuscados() {
        return arquivosBuscados;
    }
    
    
    /**
     * inicializa a tela principal
     */
    private void initJFrame() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        jFrame = new JFrame();
        jFrame.setResizable(false);
        jFrame.setSize(800, 600);
        jFrame.setLocation(dim.width / 2 - jFrame.getWidth() / 2, dim.height / 2 - jFrame.getHeight() / 2);
        jFrame.setVisible(true);        
        jFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jFrame.setTitle("Processo P2P ID: " + id);               
        telaInicial = new GUITelaInicial(this,jFrame.getWidth(), jFrame.getHeight(),GUITelaInicial.AGUARDANDO);
        jFrame.getContentPane().add(telaInicial);
        jFrame.repaint();
    }

    public RSA getRsa() {
        return rsa;
    }
    
    public PublicKey getPublicKey(){
        return rsa.getKeyPair().getPublic();
    }
    
    public int getId() {
        return id;
    }
    
    public void setTheTracker(Peer peer){
        this.tracker = peer;        
        knowTracker = true;
        if(tracker.getId() == id){
            myTracker = new Tracker(this);
        }
        
        System.out.println(tracker.getSettings());
        
        //cliente = new Cliente(multi, this);
    } 

    @Override
    public void run() {
        while(true){
            if(knowTracker && isReady){
                if(!buscou && changePanel){ //nao fez uma busca ainda
                    telaInicial = new GUITelaInicial(this, jFrame.getWidth(), jFrame.getHeight(),GUITelaInicial.CONECTADO);
                    jFrame.getContentPane().removeAll();
                    jFrame.getContentPane().add(telaInicial);
                    jFrame.repaint();
                    changePanel = false;
                }else{ //buscou
                    if(changePanel){
                        telaInicial = new GUITelaInicial(this, jFrame.getWidth(), jFrame.getHeight(),GUITelaInicial.PRONTO);
                        jFrame.getContentPane().removeAll();
                        jFrame.getContentPane().add(telaInicial);
                        jFrame.repaint();
                        changePanel = false;
                    }
                }
                
            }else{  //no tracker
                if(changePanel){
                    telaInicial = new GUITelaInicial(this, jFrame.getWidth(), jFrame.getHeight(),GUITelaInicial.AGUARDANDO);
                    jFrame.getContentPane().removeAll();
                    jFrame.getContentPane().add(telaInicial);
                    jFrame.repaint();
                    changePanel = false;
                }
            }  
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Processo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void realizarBusca(String stringBuscada) {
        this.stringBuscada = stringBuscada;
        try {
            Vector<String> busca = new Vector<>();
            System.out.println("Peer comecou busca");
            String str = "Request: buscar("+stringBuscada+")";
            System.out.println(tracker.getAddress()+":"+tracker.getPort());
            byte[] buf = str.getBytes();
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket pack = new DatagramPacket(buf, buf.length, tracker.getAddress(), tracker.getPort());
            socket.send(pack);
            String statusFinal = Funcoes.END_OF_FILES;
            boolean go = true;
            String fileName = "empty";
            String[] array = new String[2];
            array[0] = "empty";
            array[1] = "empty";
            while(go){
                buf = new byte[1024];
                pack = new DatagramPacket(buf, buf.length);
                socket.receive(pack);                
                String data = new String(pack.getData());
                array = data.split(":");
                fileName = array[1].substring(0,array[1].lastIndexOf(";"));                
                if(!fileName.equals(statusFinal)){
                    busca.add(fileName);                        
                }else{
                    go = false;
                }
            }
            arquivosBuscados = busca;
            buscou = true;
            changePanel = true;
            socket.close();            
        } catch (SocketException ex) {
            Logger.getLogger(Processo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Processo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Se o tracker ja e conhecido, ele e atualizado
     * caso contrario seta um novo tracker
     * @param peer 
     */
    public void updateTracker(Peer peer) {
        if(knowTracker){
            if(tracker.getPort() != peer.getPort()){
                tracker.setPort(peer.getPort());
                enviarArquivosParaTracker();
                changePanel = true;
            }
            isReady = true;            
        }else{
            setTheTracker(peer);
        }
    }

    private void enviarArquivosParaTracker() {
        try {
            String str = "Peer "+id+" diz: oi tracker!";
            byte[] buf = str.getBytes();
            DatagramSocket socketUnicast = new DatagramSocket();
            DatagramPacket pack = new DatagramPacket(buf, buf.length, tracker.getAddress(), tracker.getPort());
            socketUnicast.send(pack);
            buf = new byte[1024];
            pack = new DatagramPacket(buf, buf.length);
            socketUnicast.receive(pack);
            String resposta = new String(pack.getData());
            resposta = resposta.substring(0,resposta.indexOf(";"));
            String respostaEsperada = "Request: getArquivos";
            System.out.println("UNICAST DO TRACKER <- " + resposta);
            System.out.println( new String("\tFrom: " + pack.getAddress().getHostAddress() + ":" + pack.getPort()) );
            if(resposta.equals(respostaEsperada)){
                Vector<String> arquivos = arquivosDoProcesso;
                for(int i = 0; i < arquivos.size(); i++){
                    String data = "fileName:"+arquivos.get(i)+";";
                    buf = data.getBytes();
                    pack = new DatagramPacket(buf, buf.length, pack.getAddress(), pack.getPort());
                    socketUnicast.send(pack);
                }
            }
            String data = "status:"+Funcoes.END_OF_FILES+";";
            buf = data.getBytes();
            pack = new DatagramPacket(buf, buf.length, pack.getAddress(), pack.getPort());
            socketUnicast.send(pack);
            socketUnicast.close();
        } catch (SocketException ex) {
            Logger.getLogger(Processo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Processo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
}
