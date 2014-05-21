/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

/**
 *
 * @author Gustavo
 */
public class Funcoes {
    
     /**
     * Converte uma String em uma String que ocupa 1024 bytes de um byte array
     *
     * @param str recebe uma String como parâmetro.
     * 
     * @return retorna a nova String
     */
    public static final String to1024String(String str) {
        byte[] buf = new byte[1024];
        byte[] temp = str.getBytes();
        System.arraycopy(temp, 0, buf, 0, temp.length);
        return new String(buf);        
    }
    
    /**
     * Retorna o index de onde a chave comeca a ser gravada no byte array
     * @return index
     */
    public static final int getKeyIndex(){
        int index = 1024-256;
        return index;
    }
    
}
