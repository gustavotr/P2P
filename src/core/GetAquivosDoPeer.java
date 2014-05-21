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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Arquivo;
import util.Funcoes;

/**
 *
 * @author Gustavo
 */
public class GetAquivosDoPeer extends Thread {
    
    private String msg;
    private InetAddress address;
    private int port;
    private DatagramSocket socketUnicast;
    private ArrayList<Arquivo> arquivosDoTracker;
    int idProcesso;

    public GetAquivosDoPeer(String msg, int idProcesso, InetAddress address, int port, ArrayList<Arquivo> array) {
        try {
            this.msg = msg;
            this.address = address;
            this.port = port;
            this.idProcesso = idProcesso;
            this.arquivosDoTracker = array;
            this.socketUnicast = new DatagramSocket();
            this.start();
        } catch (SocketException ex) {
            Logger.getLogger(GetAquivosDoPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Override
    public void run() {
        try {
            byte[] buf = msg.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length, address, port);
            socketUnicast.send(pack);
            String statusDoPedido = "empty";
            String statusFinal = Funcoes.to1024String("fim dos arquivos");
            while(!statusDoPedido.equals(statusFinal)){
                buf = new byte[1024];
                pack = new DatagramPacket(buf, buf.length);
                socketUnicast.receive(pack);
                statusDoPedido = new String(pack.getData());
                int indexDoArquivo = hasArquivo(statusDoPedido);
                if(indexDoArquivo >= 0){
                    if(!hasProcesso(idProcesso,indexDoArquivo)){
                        arquivosDoTracker.get(indexDoArquivo).addProcesso(idProcesso);
                    }
                }else{
                    arquivosDoTracker.add(new Arquivo(statusDoPedido, idProcesso));
                }
                
            }
            socketUnicast.close();            
            System.out.println("Terminou UNICAST");
//            for(int i = 0; i < arquivosDoTracker.size(); i++){
//                System.out.print(arquivosDoTracker.get(i).getNome());
//                System.out.println(arquivosDoTracker.get(i).getProcessos().toString());
//            }
            this.interrupt();
        } catch (IOException ex) {
            Logger.getLogger(GetAquivosDoPeer.class.getName()).log(Level.SEVERE, null, ex);
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
            String temp = Funcoes.to1024String(arquivosDoTracker.get(i).getNome());
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
