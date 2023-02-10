package lu.cascade.assessment.user.manager.api.security;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lu.cascade.assessment.user.manager.api.utils.UserManagerTechnicalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Service
@AllArgsConstructor
@Slf4j
public class AESHelper {
    private static SecretKeySpec secretKeySpec;
    private static final String ALGORITHM = "AES";

    @Value("app.security.secret-key")
    private final String secretKey;

    @PostConstruct
    public void prepareSecreteKey() {
        MessageDigest sha = null;
        try {
            byte[] key = secretKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithm", e);
            throw UserManagerTechnicalException.technicalbuilder().message("Error algorithm not recognized").build();
        }
    }

    public String encrypt(String strToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.error("Could not encrypt", e);
            throw UserManagerTechnicalException.technicalbuilder().message("Error occurred during encryption").build();
        }
    }

    public String decrypt(String strToDecrypt) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            log.error("Could not decrypt", e);
            throw UserManagerTechnicalException.technicalbuilder().message("Error occurred during decryption").build();
        }
    }
}