package zzhao.code.security;

import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author zzhao
 * @version 2016年6月15日
 */
public class AES {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static final int DEFAULT_KEY_SIZE = 256;
    private static final int DEFAULT_ITERATION_COUNT = 10;

    private final int keySize;
    private final int iterationCount;
    private final Cipher cipher;

    public AES() {
        this(DEFAULT_KEY_SIZE);
    }

    public AES(int keySize) {
        this(keySize, DEFAULT_ITERATION_COUNT);
    }

    public AES(int keySize, int iterationCount) {
        this.keySize = keySize;
        this.iterationCount = iterationCount;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES not support");
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException("PKCS5Padding not support");
        }
    }

    public String encrypt(String salt, String iv, String passphrase, String plaintext) throws Exception {
        SecretKey key = generateKey(salt, passphrase);
        byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, key, iv, plaintext.getBytes(DEFAULT_CHARSET));
        return Base64.encodeBase64String(encrypted);
    }

    public String decrypt(String salt, String iv, String passphrase, String ciphertext) throws Exception {
        SecretKey key = generateKey(salt, passphrase);
        byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv, Base64.decodeBase64(ciphertext));
        return new String(decrypted, DEFAULT_CHARSET);
    }

    private byte[] doFinal(int encryptMode, SecretKey key, String iv, byte[] bytes) throws Exception {
        cipher.init(encryptMode, key, new IvParameterSpec(Hex.decodeHex(iv.toCharArray())));
        return cipher.doFinal(bytes);
    }

    private SecretKey generateKey(String salt, String passphrase) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), Hex.decodeHex(salt.toCharArray()), iterationCount, keySize);
        SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return key;
    }


    public static String randomByte(int length) {
        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return Hex.encodeHexString(salt);
    }

    public static void main(String[] args) {
        AES aes = new AES(128, 10);
        try {
            String key = "123456";
            String salt = "4f1c63caaf99ccc7cbeaeb37c9b734ed";
            // randomSalt(16);
            // System.out.println(salt);
            String iv = "bffed1ce277bee5355918a6f4dedace3";
            // randomSalt(16);
            // System.out.println(iv);
            String result = aes.decrypt(salt, iv, key, "dnDhYFABllGXwztIYAFchA==");
            System.out.println(result);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
