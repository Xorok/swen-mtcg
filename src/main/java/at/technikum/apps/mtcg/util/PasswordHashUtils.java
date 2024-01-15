package at.technikum.apps.mtcg.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PasswordHashUtils {
    private final int iterations = 10000;
    private final int keyLength = 512; // key length in bits

    public record HashSaltTuple(String hash, byte[] salt) {
    }

    public HashSaltTuple hashPassword(String password) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return new HashSaltTuple(generateHash(password, salt), salt);
    }

    public boolean verifyPassword(String inputPassword, String storedHash, byte[] salt) throws Exception {
        String newHash = generateHash(inputPassword, salt);
        return newHash.equals(storedHash);
    }

    private String generateHash(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Hash the password using PBKDF2 with HmacSHA512
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        byte[] hash = factory.generateSecret(keySpec).getEncoded();

        return Base64.getEncoder().encodeToString(hash);
    }
}