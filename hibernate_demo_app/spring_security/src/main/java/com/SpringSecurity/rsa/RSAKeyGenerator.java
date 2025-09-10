package com.SpringSecurity.rsa;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Utility class for generating RSA key pairs for JWT token signing and verification
 * This class generates a 2048-bit RSA key pair and saves them as Base64 encoded strings
 */
public class RSAKeyGenerator {
    
    public static void main(String[] args) {
        try {
            generateRSAKeys();
        } catch (Exception e) {
            System.err.println("Error generating RSA keys: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generates RSA key pair and saves them to files
     * @throws NoSuchAlgorithmException if RSA algorithm is not available
     * @throws IOException if there's an error writing to files
     */
    public static void generateRSAKeys() throws NoSuchAlgorithmException, IOException {
        System.out.println("Generating RSA Key Pair...");
        
        // Generate RSA key pair with 2048-bit key size
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        
        // Convert keys to Base64 strings
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        
        // Save keys to files
        Files.write(Paths.get("rsa-private-key.txt"), privateKeyString.getBytes());
        Files.write(Paths.get("rsa-public-key.txt"), publicKeyString.getBytes());
        
        System.out.println("RSA Keys generated successfully!");
        System.out.println("Files created:");
        System.out.println("- rsa-private-key.txt");
        System.out.println("- rsa-public-key.txt");
        
        System.out.println("\n=== PRIVATE KEY (Base64) ===");
        System.out.println(privateKeyString);
        
        System.out.println("\n=== PUBLIC KEY (Base64) ===");
        System.out.println(publicKeyString);
        
        System.out.println("\n=== APPLICATION.PROPERTIES CONFIGURATION ===");
        System.out.println("# RSA Private Key for signing JWT tokens");
        System.out.println("app-jwt-private-key=" + privateKeyString);
        System.out.println("# RSA Public Key for verifying JWT tokens");
        System.out.println("app-jwt-public-key=" + publicKeyString);
        
        System.out.println("\n=== KEY INFORMATION ===");
        System.out.println("Algorithm: RSA");
        System.out.println("Key Size: 2048 bits");
        System.out.println("Private Key Format: PKCS#8");
        System.out.println("Public Key Format: X.509");
        System.out.println("Usage: RS256 JWT signing/verification");
    }
    
    /**
     * Generates RSA key pair and returns them as Base64 strings
     * @return String array with [0] = private key, [1] = public key
     * @throws NoSuchAlgorithmException if RSA algorithm is not available
     */
    public static String[] generateRSAKeysAsStrings() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        
        String privateKeyString = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String publicKeyString = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        
        return new String[]{privateKeyString, publicKeyString};
    }
    
    /**
     * Generates RSA key pair with custom key size
     * @param keySize the key size in bits (recommended: 2048 or 4096)
     * @return String array with [0] = private key, [1] = public key
     * @throws NoSuchAlgorithmException if RSA algorithm is not available
     */
    public static String[] generateRSAKeysAsStrings(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        
        String privateKeyString = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String publicKeyString = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        
        return new String[]{privateKeyString, publicKeyString};
    }
}
