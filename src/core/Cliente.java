/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            try {
                byte[] buf = new byte[1024];
                DatagramSocket socket = processo.getSocketUnicast();       
                DatagramPacket pack = new DatagramPacket(buf, buf.length);
                socket.receive(pack);

                String respostaEsperada = "Request: arquivo(";
                String resposta = new String(pack.getData());
                System.out.println("Cliente recebeu: "+resposta);
                
                String data = resposta.substring(0, respostaEsperada.length());

                if(data.equals(respostaEsperada)){
                    String fileName = resposta.substring(respostaEsperada.length(),resposta.lastIndexOf(")"));
                    Path path = Paths.get(processo.getFolderPath() + "/" + fileName);
                    byte[] bytes = Files.readAllBytes(path);
                    pack = new DatagramPacket(bytes, bytes.length, pack.getAddress(), pack. getPort());
                    socket.send(pack);
                }

            }catch (IOException ex) {                
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }   
            
        }
    }
    
}
