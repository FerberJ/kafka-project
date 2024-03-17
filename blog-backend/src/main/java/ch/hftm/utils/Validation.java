package ch.hftm.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Validation {
    // Get Hashcode of content with content and algorithem.
    // Algorithm can be for example "SHA-256"
    public String getHashCode(byte[] content, String algorithem) throws NoSuchAlgorithmException, IOException {
        var stream = new ByteArrayInputStream(content);
        MessageDigest digest = MessageDigest.getInstance(algorithem);
        byte[] buffer = new byte[8291];
        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1) {
            digest.update(buffer, 0, bytesRead);
        }
        byte[] hashBytes = digest.digest();
        String hashString = Base64.getEncoder().encodeToString(hashBytes);
        return hashString;
    }
}
