/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

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
    public static int getKeyIndex(){
        int index = 1024-256;
        return index;
    }
    
    public static byte [] encrypt(Key key, byte [] data ) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException{
        Security.addProvider(new BouncyCastleProvider());
        
        SecureRandom random = new SecureRandom();
        Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, key, random);
        byte[] cipherData = cipher.doFinal(data);
        return cipherData;
    }
    
    public static byte [] decrypt(Key key, byte [] data ) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException{
        Security.addProvider(new BouncyCastleProvider());
        
        SecureRandom random = new SecureRandom();
        Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, key, random);
        byte[] cipherData = cipher.doFinal(data);
        return cipherData;
    }
    
}
