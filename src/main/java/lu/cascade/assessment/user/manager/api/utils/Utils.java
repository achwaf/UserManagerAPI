package lu.cascade.assessment.user.manager.api.utils;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Pattern;

@Slf4j
public class Utils {

    final static private String HASH_ALGORITHM = "SHA3-256";
    final static private String REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    final static private Pattern PATTERN_EMAIL = Pattern.compile(REGEX_EMAIL);
    final static public int ALLOWED_LENGTH_USERNAME= 50;
    final static public int ALLOWED_LENGTH_PASSWORD= 30;

    static public String fromBase64(String base64Encoded){
        byte[] decodedBytes = Base64.getDecoder().decode(base64Encoded);
        return new String(decodedBytes);
    }

    static public boolean isEmailValid(String email){
        return PATTERN_EMAIL.matcher(email).matches();
    }

    static public String hash(String value){
       return hash(value,"SomePepper");
    }

    static public String hash(String value, String secretKey){
        try {
            String prefixedValue = secretKey + value;
            final MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            final byte[] hashBytes = digest.digest(
                    prefixedValue.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        }catch (Exception ex){
            log.error("Couldn't hash the value [{}] using algorithm [{}] ",value, HASH_ALGORITHM,ex);
            throw UserManagerTechnicalException.technicalbuilder().message("Error during hash function").build();
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
