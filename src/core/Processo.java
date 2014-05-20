/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Random;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JPanel;
import model.Peer;
import view.GUITelaInicial;

/**
 *
 * @author a1097075
 */
public class Processo {
    
    private int id;
    private Peer traker;
    private RSA rsa;
    private boolean knowTracker;
    private JFrame jFrame;

    public Processo() {
        Random rnd = new Random();
        id = 10 + rnd.nextInt(89);
        knowTracker = false;        
        
                
        initJFrame();
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
        JPanel telaInicial = new GUITelaInicial(jFrame.getWidth(), jFrame.getHeight());
        jFrame.add(telaInicial);
        jFrame.invalidate();
    }
    
    
    
    
}
