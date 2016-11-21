package com.storeelf.util;

import java.security.Key;
//import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.codec.Base64;

public class SecurityUtils {

	public static String symmetricEncrypt(String text, String secretKey) {
		if(StringUtils.isNotBlank(text)){
	        byte[] raw;
	        String encryptedString = null;
	        SecretKeySpec skeySpec;
	        byte[] encryptText = text.getBytes();
	        Cipher cipher;
	        try {
	            raw = Base64.decode(secretKey);
	            skeySpec = new SecretKeySpec(raw, "AES");
	            cipher = Cipher.getInstance("AES");
	            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	            encryptedString = Base64.encodeToString(cipher.doFinal(encryptText));
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	            return "STOREELF_ENCRYPTION_ERROR";
	        }
	        return encryptedString;
		}else{
			return "STOREELF_ENCRYPTION_ERROR";
		}
    }

	 public static String symmetricDecrypt(String text, String secretKey) {
         Cipher cipher;
         String encryptedString = null;
         byte[] encryptText = null;
         byte[] raw;
         SecretKeySpec skeySpec;
         try {
             raw = Base64.decode(secretKey);
             skeySpec = new SecretKeySpec(raw, "AES");
             encryptText = Base64.decode(text);
             cipher = Cipher.getInstance("AES");
             cipher.init(Cipher.DECRYPT_MODE, skeySpec);
             encryptedString = new String(cipher.doFinal(encryptText));
         } catch (Exception e) {
             e.printStackTrace();
             return "STOREELF_DECRYPTION_ERROR";
         }
         return encryptedString;
     }

	public static String encrypt(String inputText, String key){
		try {
	         // Create key and cipher
	         Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
	         Cipher cipher = Cipher.getInstance("AES");

	         // encrypt the text
	         cipher.init(Cipher.ENCRYPT_MODE, aesKey);
	         byte[] encrypted				= cipher.doFinal(inputText.getBytes());
	         byte[] base64EncryptedString	= org.apache.commons.codec.binary.Base64.encodeBase64(encrypted);
	         return new String(base64EncryptedString);
	      }catch(Exception e) {
	         e.printStackTrace();
	      }
		return null;
	}

	public static String decrypt(String inputText, String key){
		try {
	         // Create key and cipher
	         Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
	         Cipher cipher = Cipher.getInstance("AES");

	         // decrypt the text
	         cipher.init(Cipher.DECRYPT_MODE, aesKey);
	         byte[] base64String	= org.apache.commons.codec.binary.Base64.decodeBase64(inputText.getBytes());

	         return new String(cipher.doFinal(base64String));
	      }catch(Exception e) {
	         e.printStackTrace();
	      }
		return null;
	}

	 public static Key generateKey(int bitStrength) {
		 try{
			 KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			 keyGen.init(bitStrength); // for example
			 SecretKey secretKey = keyGen.generateKey();
			 return secretKey;
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 return null;
	 }

	 public static String generateBase64Key(int bitStrength) {
		 try{
			 KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			 keyGen.init(bitStrength); // for example
			 SecretKey secretKey = keyGen.generateKey();

			 byte[] base64EncodedKey = Base64.encode(secretKey.getEncoded());
			 return new String(base64EncodedKey);
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 return null;
	 }
}
