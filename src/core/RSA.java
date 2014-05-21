/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *
 * @author a1097075
 */
public class RSA {
        
    private final KeyPair keyPair;
    private final Cipher cipher;

    public RSA() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
        Security.addProvider(new BouncyCastleProvider());
        
        cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
        SecureRandom random = new SecureRandom();
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        
        generator.initialize(256, random);
        keyPair = generator.generateKeyPair();
        
        
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }
    
    public byte [] encrypt(PrivateKey key, byte [] data ) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        SecureRandom random = new SecureRandom();
        cipher.init(Cipher.ENCRYPT_MODE, key, random);
        byte[] cipherData = cipher.doFinal(data);
        return cipherData;
    }
    
    public byte [] decrypt(PrivateKey key, byte [] data ) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        SecureRandom random = new SecureRandom();
        cipher.init(Cipher.DECRYPT_MODE, key, random);
        byte[] cipherData = cipher.doFinal(data);
        return cipherData;
    }   
    
}
