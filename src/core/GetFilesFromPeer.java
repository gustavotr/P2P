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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import model.Arquivo;
import util.Funcoes;

/**
 *
 * @author Gustavo
 */
public class GetFilesFromPeer extends Thread {
    
    private String msg;
    private InetAddress address;
    private int port;
    private DatagramSocket socketUnicast;
    private ArrayList<Arquivo> arquivosDoTracker;
    int idProcesso;
    private PrivateKey key;

    public GetFilesFromPeer(String msg, int idProcesso, InetAddress address, int port, ArrayList<Arquivo> array, PrivateKey key) {
        try {
            this.msg = msg;
            this.address = address;
            this.port = port;
            this.key = key;
            this.idProcesso = idProcesso;
            this.arquivosDoTracker = array;
            this.socketUnicast = new DatagramSocket();
            this.start();
        } catch (SocketException ex) {
            Logger.getLogger(GetFilesFromPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Override
    public void run() {
        try {
            // pede arquivos para o peer
            byte[] buf = Funcoes.encrypt(key, msg.getBytes());
            DatagramPacket pack = new DatagramPacket(buf, buf.length, address, port);
            socketUnicast.send(pack);
                      
            //recebe arquivos do peer
            String fileName = "empty";
            String[] array = new String[2];
            array[0] = "empty";
            array[1] = "empty";
            String statusFinal = Funcoes.END_OF_FILES;
            boolean go = true;
            while(go){
                buf = new byte[1024];
                pack = new DatagramPacket(buf, buf.length);
                socketUnicast.receive(pack);
                String data = new String(pack.getData());
                array = data.split(":");
                fileName = array[1].substring(0,array[1].lastIndexOf(";"));
                if(fileName.equals(statusFinal)){
                    go = false;
                }else{
                    int indexDoArquivo = hasArquivo(fileName);
                    if(indexDoArquivo >= 0){
                        if(!hasProcesso(idProcesso,indexDoArquivo)){
                            arquivosDoTracker.get(indexDoArquivo).addProcesso(idProcesso);
                        }
                    }else{
                        arquivosDoTracker.add(new Arquivo(fileName, idProcesso));
                    }
                }
                
            }
            socketUnicast.close();            
            System.out.println("Terminou UNICAST");
            for (Arquivo arquivosDoTracker1 : arquivosDoTracker) {
                System.out.print(arquivosDoTracker1.getNome());
                System.out.println(arquivosDoTracker1.getProcessos().toString());
            }
            this.interrupt();
        } catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException ex) {
            Logger.getLogger(GetFilesFromPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Procura um arquivo com o mesmo nome que o fornecido nos arquivos do tracker
     * @param nome
     * @return O indice do arquivo com o mesmo nome se achar o arquivo
     *   ou -1 se nao achar o arquivo
     */
    public int hasArquivo(String nome){
        for(int i = 0; i < arquivosDoTracker.size(); i++){
            String temp = arquivosDoTracker.get(i).getNome();
            if(temp.equals(nome)){
                return i;
            }
        }
        return -1;
    }

    private boolean hasProcesso(int idProcesso, int indexDoArquivo) {
        ArrayList<Integer> processos = arquivosDoTracker.get(indexDoArquivo).getProcessos();
        for(int i = 0; i < processos.size(); i++){
            int id = processos.get(i);
            if(id == idProcesso){
                return true;
            }
        }
        return false;
    }
    
    
    
    
    
    
}
