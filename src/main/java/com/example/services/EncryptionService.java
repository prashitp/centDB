package com.example.services;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Base64;

public class EncryptionService {

    Cipher encryptionCipher;
    Cipher decryptionCipher;

    @SneakyThrows
    public EncryptionService() {
        encryptionCipher = Cipher.getInstance("AES");
        decryptionCipher = Cipher.getInstance("AES");
    }

    @SneakyThrows
    public String encrypt(String input, SecretKey key) {
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] utf8 = input.getBytes("UTF8");
        byte[] enc = encryptionCipher.doFinal(utf8);
        return Base64.getEncoder().encodeToString(enc);
    }

    @SneakyThrows
    public String decrypt(String input, SecretKey key) {
        decryptionCipher.init(Cipher.DECRYPT_MODE, key);
        byte[] dec = Base64.getDecoder().decode(input);
        byte[] utf8 = decryptionCipher.doFinal(dec);
        return new String(utf8, "UTF8");
    }
}
