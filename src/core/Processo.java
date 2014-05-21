/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JPanel;
import model.Peer;
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

    public Processo() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
        Random rnd = new Random();
        id = 10 + rnd.nextInt(89);
        knowTracker = false; 
        isReady = false;
        rsa = new RSA();
        initJFrame();
        
        
        new MultiCastPeer(this);
        new Thread(this).start();
        
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
        telaInicial = new GUITelaInicial(jFrame.getWidth(), jFrame.getHeight(),GUITelaInicial.AGUARDANDO);
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
            if(knowTracker){   
                if(!isReady){
                    telaInicial = new GUITelaInicial(jFrame.getWidth(), jFrame.getHeight(),GUITelaInicial.CONECTADO);
                    jFrame.getContentPane().removeAll();
                    jFrame.getContentPane().add(telaInicial);
                    jFrame.repaint();
                    isReady = true;
                }else{ //not ready
                    
                }
                
            }else{  //no tracker
                telaInicial = new GUITelaInicial(jFrame.getWidth(), jFrame.getHeight(),GUITelaInicial.AGUARDANDO);
                jFrame.getContentPane().removeAll();
                jFrame.getContentPane().add(telaInicial);
                jFrame.repaint();
            }  
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Processo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
    
}
