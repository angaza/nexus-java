package com.angaza.nexus.keycode.full;


import com.github.emboss.siphash.SipHash;
import com.github.emboss.siphash.SipKey;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

import com.angaza.nexus.keycode.Message;
import com.angaza.nexus.keycode.util.BitUtils;

/**
 * Arbitrary Full keycode message; immutable.
 *
 * @see FullMessage
 */
public class BaseFullMessage implements Message {
    final String body;
    private final String mac;
    final String header;
    final byte[] secretKey;

    /**
     * @param fullId    integer value for the message ID
     * @param typeCode  integer value for the message type
     * @param body      string of arbitrary digits of message body
     * @param secretKey secret hash key (16 bytes)
     */
    public BaseFullMessage(int fullId, int typeCode, String body, byte[] secretKey) {
        this.body = body;
        int bodyInt = Integer.parseInt(body, 10);
        this.mac = BaseFullMessage.generateMac(fullId, typeCode, bodyInt, secretKey);
        this.header = new StringBuilder()
                .append(String.format(Locale.ENGLISH, "%01d", typeCode))
                // transmitted ID is 6-LSB (0x3F) of full ID
                .append(String.format(Locale.ENGLISH, "%02d", fullId & 0x3f))
                .toString();
        this.secretKey = secretKey;
    }

    /**
     * Generate the internal, *truncated* MAC digits for this message
     * MAC is generated over 9 total bytes:
     * 4 = fullId
     * 1 = typeCode
     * 4 = contents of body
     */
    public static String generateMac(int fullId, int typeCode, int bodyInt, byte[] secretKey) {
        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(fullId);
        buffer.put((byte) typeCode);
        buffer.putInt(bodyInt);
        long hash = SipHash.digest(new SipKey(secretKey), buffer.array());
        String hashIntString = String.format(Locale.ENGLISH, "%06d", hash & 0xffffffffL);
        return hashIntString.substring(hashIntString.length() - 6);
    }

    /**
     * Given a raw valid message string, obscure it, and return the resulting string. The purpose of
     * this process is to obscure the structure of a given message, which may improve perceived
     * security, and to somewhat reduce the occurrence of repeated keys, which may reduce entry
     * errors.
     * <p>
     * The full-protocol obscure is a bitwise operation designed to be
     * easily reversible; simply call the same function with sign=-1.
     *
     * @param digits string of 14 decimal digits
     * @return the protocol message
     */
    public static String obscure(String digits, int sign) throws IOException {
        // MAC digits are last 6 of digits, use as seed
        String macString = digits.substring(digits.length() - 6);
        int mac = Integer.parseInt(macString);
        byte[] macBits = ByteBuffer.allocate(4).putInt(mac).array();

        // [0, 255] values; one for each body digit
        // 8 body digits, 8 bytes (8 bits each), so 64 bits of output required
        byte[] randomBitsReversed = BitUtils.pseudorandomBits(macBits, 64);
        // Reverse result from pseudorandomBits to match the input array
        byte[] randomBits = BitUtils.repackToBufferBottom(randomBitsReversed, 64);

        // Create byte buffer where each of first 8 digits of input string has its own byte
        ByteBuffer bodyBitsBuffer = ByteBuffer.allocate(8);
        for (int i = 0; i < 8; i++) {
            bodyBitsBuffer.put((byte) Integer.parseInt(digits.substring(i, i + 1)));
        }
        byte[] bodyBits = bodyBitsBuffer.array();

        // Inject some deterministic randomness into our body digits by adding our
        // previously-generated pseudo-random values to each digit, modulo 10 to ensure that each
        // value is still between 0 and 9, inclusive.
        ByteBuffer outBitsBuffer = ByteBuffer.allocate(8);
        for (int i = 0; i < 8; i++) {
            int prValue = (int) (randomBits[i] & 0xffL) * sign;
            int outValue = bodyBits[i] + prValue;
            outValue %= 10;
            outBitsBuffer.put((byte) outValue);
        }
        byte[] outBits = outBitsBuffer.array();

        // Concatenate new obscured digits with original last 6 digits
        StringBuilder obscuredDigits = new StringBuilder();
        for (byte digit : outBits) {
            obscuredDigits.append(String.valueOf(digit));
        }
        obscuredDigits.append(macString);

        return obscuredDigits.toString();
    }

    /**
     * Render this message in keycode form.
     * <p>
     * The rendered message can be transferred to a human. For example:
     * <p>
     * <pre>
     *     {@code
     *     BaseFullMessage message = new BaseFullMessage(
     *             1223, 0, "00993",
     *             HexToByteArray.convert(
     *             "abababababababababababababababab"));
     *     message.toKeycode("*", "#", "-", 3);
     *     }
     * "*885-190-556-639-04#"
     * </pre>
     *
     * @param prefix    keycode start character, e.g., "*"
     * @param suffix    keycode end character, e.g., "#"
     * @param separator inter-group separating character, e.g., "-"
     * @param groupLen  number of characters in each separated group
     * @return the rendered keycode string
     */
    public String toKeycode(String prefix, String suffix, String separator, int groupLen) throws
            IOException {
        String rawKeycode = new StringBuilder()
                .append(header)
                .append(body)
                .append(mac)
                .toString();

        String keycode = obscure(rawKeycode, 1);

        StringBuilder formattedKeycode = new StringBuilder();

        for (int i = 0; i < keycode.length(); i += groupLen) {
            formattedKeycode.append(keycode.substring(i, Math.min(i + groupLen, keycode.length())));
            if (i < (keycode.length() - groupLen)) {
                formattedKeycode.append(separator);
            }
        }

        formattedKeycode.insert(0, prefix);
        formattedKeycode.append(suffix);

        return formattedKeycode.toString();

    }

    @Override
    public String toKeycode() throws IOException {
        return toKeycode("*", "#", " ", 3);
    }
}
