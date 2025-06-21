//package org.lechuck.personal_app;
//
//import java.security.NoSuchAlgorithmException;
//import javax.crypto.KeyGenerator;
//
//import java.util.Base64;
//
//public class key {
//    public static void main(String[] args) throws NoSuchAlgorithmException {
//        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
//        keyGen.init(256); // Ensure 256-bit key
//        byte[] key = keyGen.generateKey().getEncoded();
//        String base64Key = Base64.getEncoder().encodeToString(key);
//        System.out.println("Generated Key: " + base64Key);
//    }
//}