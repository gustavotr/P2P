package view;

import core.Processo;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import util.Funcoes;

public class GUITelaInicial extends JPanel{

    private static final long serialVersionUID = 1L;
    private JLabel labelErro;
    private JButton buttonBuscar;
    private JLabel label;
    private JTextField busca;
    private JList<String> result;
    private int width;
    private int height;
    public static final int AGUARDANDO = 1;
    public static final int CONECTADO = 2;
    public static final int PRONTO = 3;
    private Processo processo;

    public GUITelaInicial(Processo processo, int width, int height, int status) {        
        this.processo = processo;
        this.width = width;
        this.height = height; 
        this.setSize(width, height);
        this.setVisible(true);
        
        switch (status) {
            case AGUARDANDO:  inicializando();
                     break;
            case CONECTADO:  initComponents();
                     break;
            case PRONTO: mostrarBusca();
                    break;
            default:
                    break;
        }
        
    }
    
    private void inicializando(){
    
        label = new JLabel("Aguardando a conexao dos peers");
        label.setBounds(20, height/2 + 50, width - 20, 40);
        label.setHorizontalAlignment(SwingConstants.CENTER);        
        this.add(label);
    }

    /**
     * Inicialização dos componentes da tela de início
     */
    private void initComponents() {
                                                        
        busca = new JTextField();
        busca.setBounds(width/2 - 200, height/2 - 50, 300, 20);
        this.add(busca);
        
        buttonBuscar = new JButton("Buscar");        
        buttonBuscar.setBounds(width/2 + 110, height/2 - 50, 100, 20);
        buttonBuscar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String buscar = busca.getText();
                processo.realizarBusca(buscar);
            }
        });
        this.add(buttonBuscar);

        labelErro = new JLabel();
        labelErro.setBounds(width/2, height*3/2, 200, 20);
        labelErro.setFont(new Font(null, Font.PLAIN, 10));
        this.add(labelErro); 
    }

    public JButton getButtonStart() {
        return buttonBuscar;
    }

    public JLabel getLabelErro() {
        return labelErro;
    }
    
    public String getBusca(){
        return busca.getText();
    }   

    private void mostrarBusca() {
        String str = processo.getStringBuscada();
        if(str.equals("")){ str = "Todos os arquivos"; }
        JLabel buscaRealizada = new JLabel(str);
        buscaRealizada.setBounds(10, 20, getWidth() - 20, 40);
        buscaRealizada.setHorizontalAlignment(SwingConstants.CENTER);
        buscaRealizada.setFont(new Font(null, Font.BOLD, 18));
        this.add(buscaRealizada);
        
        JLabel myPath = new JLabel(processo.getFolderPath());
        myPath.setBounds(10, 0, getWidth() - 10, 15);
        this.add(myPath);
        
        result = new JList<>(processo.getArquivosBuscados());  
        result.setPreferredSize(new Dimension(800,600));
        result.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
               if(!e.getValueIsAdjusting()){                    
                   try {
                       
                       // Pergunta ao tracker o locat do arquivo
                       String fileName = result.getSelectedValue();
                       String str = "Request: arquivo("+fileName+")";
                       byte[] buf = str.getBytes();
                       DatagramSocket socket = new DatagramSocket();
                       DatagramPacket pack = new DatagramPacket(buf, buf.length, processo.getTracker().getAddress(), processo.getTracker().getPort());
                       socket.send(pack);
                       
                       //recebe do tracker o local do arquivo
                       buf = new byte[32];
                       pack = new DatagramPacket(buf, buf.length);
                       socket.receive(pack);
                       
                       // -------- resposta com descriptografia
                       String data = new String(Funcoes.decrypt(processo.getTracker().getPublicteKey(), pack.getData()));
                       System.out.println("Tracker responde local do arquivo: "+data);                     
                       
                       //pede ao peer o arquivo
                       buf = new byte[1024];
                       String[] array = data.split(":");                      
                       buf = str.getBytes();
                       System.out.println("String "+str);
                       pack = new DatagramPacket(buf, buf.length, InetAddress.getByName(array[0]), Integer.parseInt(array[1]));
                       socket.send(pack);
                       
                       //recebe o arquivo do peer
                       buf = new byte[1024];
                       pack = new DatagramPacket(buf, buf.length);
                       socket.receive(pack);
                       data = new String(pack.getData());
                       //grava o arquivo na pasta do processo
                       Files.write(Paths.get(processo.getFolderPath() + "/" + fileName), buf);
//                       FileOutputStream fos = new FileOutputStream(processo.getFolderPath() + "/" + fileName);
//                       fos.write(buf);
//                       fos.close();
                       socket.close();
                   } catch (SocketException ex) {
                       Logger.getLogger(GUITelaInicial.class.getName()).log(Level.SEVERE, null, ex);
                   } catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException ex) {
                       Logger.getLogger(GUITelaInicial.class.getName()).log(Level.SEVERE, null, ex);
                   }
                    
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(width/4, height/4, width/2, height/2);
        this.add(scrollPane);
        
        scrollPane.setViewportView(result);
    }

}
