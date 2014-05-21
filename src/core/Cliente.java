/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gustavo
 */
public class Cliente implements Runnable{
    
    private Processo processo;

    public Cliente(Processo processo) {
        this.processo = processo;
        new Thread(this).start();
    }
    
    
    @Override
    public void run() {        
        while(true){
            if(processo.isInDownloadView()){
                try {
                    byte[] buf = new byte[1024];
                    DatagramSocket socket = processo.getSocketUnicast();                
                    System.out.println("Estou aqui! "+ socket.getLocalPort());
                    DatagramPacket pack = new DatagramPacket(buf, buf.length);
                    socket.receive(pack);

                    System.out.println("Cliente recebeu algo");
                    System.out.println(new String(pack.getData()));

                    String respostaEsperada = "Request: arquivo(";
                    String resposta = new String(pack.getData());
                    String data = resposta.substring(0, respostaEsperada.length());

                    if(data.equals(respostaEsperada)){
                        String busca = resposta.substring(respostaEsperada.length(),resposta.lastIndexOf(")"));
                        System.out.println("Requisicao de arquivo recebida");
                    }

                }catch (IOException ex) {                
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                }   
            }
        }
    }
    
}
