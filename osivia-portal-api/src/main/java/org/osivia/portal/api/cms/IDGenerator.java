package org.osivia.portal.api.cms;

import java.util.UUID;

import org.springframework.stereotype.Service;

/**
 * @author Cédric Krommenhoek
 */

public class IDGenerator {

    /** Base 62. */
    private static final int BASE_62 = 62;

    /** Base 62 alphabet conversion table. */
    private static final char[] TO_BASE_62 = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * Generate link identifier.
     * 
     * @return link identifier
     */
    public static String generateId() {
        // UUID
        UUID uuid = UUID.randomUUID();

        // Most significant bits
        long mostSignificantBits = Math.abs(uuid.getMostSignificantBits());
        String mostSignificantBitsBase64 = convertLongToBase62(mostSignificantBits);

        // Least significant bits
        long leastSignificantBits = Math.abs(uuid.getLeastSignificantBits());
        String leastSignificantBitsBase64 = convertLongToBase62(leastSignificantBits);

        StringBuilder builder = new StringBuilder();
        builder.append(mostSignificantBitsBase64);
        builder.append("-");
        builder.append(leastSignificantBitsBase64);
        return builder.toString();
    }

    /**
     * Convert long to base 62.
     * 
     * @param value long value
     * @return base 62 representation
     */
    private static String convertLongToBase62(long value) {
        // Exponent
        int exponent = 1;
        while (Math.pow(BASE_62, exponent) < value) {
            exponent++;
        }

        // Base 62 array
        int[] base62Array = new int[exponent];
        long reminder = value;
        for (int i = (exponent - 1); i >= 0; i--) {
            long pow = Double.valueOf(Math.pow(BASE_62, i)).longValue();
            long div = reminder / pow;
            long mod = reminder % pow;

            base62Array[exponent - i - 1] = Long.valueOf(div).intValue();
            reminder = mod;
        }

        StringBuilder builder = new StringBuilder();
        for (int base62 : base62Array) {
            char c = TO_BASE_62[base62];
            builder.append(c);
        }

        return builder.toString();
    }


 

}
