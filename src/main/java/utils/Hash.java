package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Contains methods to hash text - fix: {@link Input#password(String)}
 * @deprecated Do not use in production
 * TODO: Secure implementation
 */
@Deprecated
public final class Hash {

    /**
     * {@link #text(String)}
     */
    public static String text(final byte[] arr) {
        return text(new String(arr));
    }

    /**
     * {@link #text(String)}
     */
    public static String text(final char[] arr) {
        return text(new String(arr));
    }

    /**
     * Hashes the inputted text (SHA-512).
     * @param text the text to hash
     * @return returns the hashed versions of the text
     */
    public static String text(final String text) {
        try {
            final MessageDigest m = MessageDigest.getInstance("SHA-512");
            m.reset();
            m.update(text.getBytes());
            final StringBuilder builder = new StringBuilder();
            for (final byte i : m.digest()) builder.append(String.format("%02x", i));
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            return text;
        }
    }
}