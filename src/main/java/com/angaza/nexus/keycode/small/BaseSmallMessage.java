package com.angaza.nexus.keycode.small;


import com.github.emboss.siphash.SipHash;
import com.github.emboss.siphash.SipKey;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import com.angaza.nexus.keycode.Message;
import com.angaza.nexus.keycode.exceptions.UnsupportedKeyMappingException;
import com.angaza.nexus.keycode.util.BitUtils;

/**
 * Keycode messages for small keypads; immutable.
 * <p>
 * All small protocol messages follow the following structure:
 * - 32-bit message ID
 * - 2-bit message type code
 * - 8-bit message body
 * - 12-bit message authentication code (MAC)
 * <p>
 * After compression, the message ID is trimmed to its 6-LSB bits, so the
 * compressed/transmitted message size is 28 bits (6+2+8+12).  Receivers of
 * a small protocol message must infer/expand the message ID to the 'full' 32
 * bit message ID; please see specification document for more details.
 * <p>
 * There are four types of small protocol messages, determined by the message
 * type code field:
 * Credit messages - message type codes 0, 2
 * Maintenance/Test messages - message type code 3
 * Message type code 1 is reserved for future use
 * <p>
 * Messages with type codes 0 and 2 (ie, credit messages) may be applied
 * exactly once to a given product over the lifetime of that product.  There
 * is no restriction on the number of times Maintenance and Test messages may
 * be applied.
 */
public class BaseSmallMessage implements Message {
    private final int messageId;
    private final int typeCode;
    private final int body;
    private final int mac;

    /**
     * Create a message per the small keypad protocol.  Such messages are
     * 15 digits long (when represented as decimal).
     *
     * @param messageId *Expanded* message id number for this message
     * @param typeCode  type code of this message (0-3)
     * @param body      integer representation of 8-bit body
     * @param secretKey secret hash key (16 bytes)
     */
    public BaseSmallMessage(int messageId, int typeCode, int body, byte[] secretKey) {
        this.messageId = messageId;
        this.body = body;
        this.typeCode = typeCode;
        this.mac = generateMac(messageId, typeCode, body, secretKey);
    }

    /**
     * Compute the internal truncated MAC bits for this message.
     * <p>
     * Generate a MAC for this message using the specified secret key.  The
     * MAC is a 'truncated_MAC', generated from the 12 MSB from the result of
     * a SipHash function applied to the message contents and secret key.  The
     * returned result will be a 12-bit bitstream.
     * <p>
     * MAC is generated using message ID, type code, and body byte.
     *
     * @param messageId expanded (32-bit) message ID of this message
     * @param typeCode  type code of this message (0-3)
     * @param body      integer representation of 8-bit body
     * @param secretKey 16-byte secret key, e.g. "\xff" * 16
     * @return packed form of the MAC generated using secret_key
     */
    public static int generateMac(int messageId, int typeCode, int body, byte[] secretKey) {
        final ByteBuffer buffer = ByteBuffer
                .allocate(6)
                .put((byte) (messageId & 0xFF))
                .put((byte) ((messageId & 0x0000FF00) >> 8))
                .put((byte) ((messageId & 0x00FF0000) >> 16))
                .put((byte) (messageId >> 24))
                .put((byte) typeCode)
                .put((byte) body);
        return (int) (SipHash.digest(new SipKey(secretKey), buffer.array()) >>> 52);
    }

    /**
     * 12-bit message that represents MAC
     */
    public byte[] macBits() {
        return ByteBuffer
                .allocate(2)
                .put((byte) (mac & 0xff))
                .put((byte) ((mac >> 8) & 0x0f))
                .array();
    }

    /**
     * Packed 16-bit message of:
     * message ID (6 bits)
     * typecode (2 bits)
     * body (8 bits)
     */
    public byte[] messageAndBodyBits() {
        // LSB 6 bits = 0x3F
        byte compressedId = (byte) (messageId & 0x3F);

        return ByteBuffer
                .allocate(2)
                .put((byte) (body & 0xff))
                .put((byte) ((compressedId << 2) | (typeCode & 0x03)))
                .array();
    }

    /**
     * Obscure a small-protocol message (28 bits).
     * <p>
     * Given a non-obscured valid small-protocol message, obscure it,
     * and return the resulting obscured bits. The purpose of this process
     * is to obscure the structure of a given message, which may improve
     * perceived security, and to somewhat reduce the occurrence of repeated
     * keys, which may reduce entry errors.
     * <p>
     * The small-protocol obscure is a bitwise operation designed to be
     * easily reversible; simply call the same function (obscure) on the
     * obscured 28-bit message to get back the original (deobscured) message.
     *
     * @param bodyBits 16-bit portion of protocol message that represents message ID and body
     * @param macBits  12-bit portion of protocol message that represents MAC
     * @return the obscured 28-bit small protocol message
     */
    public static byte[] obscure(byte[] bodyBits, byte[] macBits) throws IOException {
        byte[] prngBits = BitUtils.pseudorandomBits(macBits, 16);
        byte[] xorBits = ByteBuffer
                .allocate(2)
                .put((byte) (bodyBits[0] ^ prngBits[0]))
                .put((byte) (bodyBits[1] ^ prngBits[1]))
                .array();
        ByteBuffer outputBits = ByteBuffer.allocate(4);
        outputBits.put(macBits[0]);
        outputBits.put((byte) ((macBits[1] & 0x0F) | ((xorBits[0] << 4) & 0xF0)));
        outputBits.put((byte) (((xorBits[0] & 0xF0) >> 4) | ((xorBits[1] & 0x0F) << 4)));
        outputBits.put((byte) (((xorBits[1] & 0xF0) >> 4)));

        byte[] output = outputBits.array();
        return output;
    }

    /**
     * Render this message in keycode form. The rendered message can be transferred to a human.
     *
     * @param prefix     keycode start character, e.g., "1"
     * @param separator  inter-group separating character, e.g., "-"
     * @param groupLen   number of characters in each separated group
     * @param keyMapping Four element list mapping indexes [0,1,2,3] to character keys
     * @return the rendered keycode string
     */
    public String toKeycode(String prefix, String separator, int groupLen, String[] keyMapping)
            throws IOException, UnsupportedKeyMappingException {
        if (keyMapping == null) {
            // Map 0 -> 2, 1 -> 3, 2 -> 4, 3 -> 5 to match physical device
            keyMapping = new String[]{"2", "3", "4", "5"};
        } else if (keyMapping.length != 4) {
            throw new UnsupportedKeyMappingException("Key mapping required for [0, 1, 2, 3]");
        }
        byte[] obscuredMessageBits = BaseSmallMessage.obscure(messageAndBodyBits(), macBits());
        ByteBuffer digits = ByteBuffer.allocate(14);
        // Convert three bytes to 12 digits
        for (int i = 0; i < 3; i++) {
            byte currentByte = obscuredMessageBits[i];
            digits.put((byte) (currentByte & 0x3));
            digits.put((byte) ((currentByte >> 2) & 0x3));
            digits.put((byte) ((currentByte >> 4) & 0x3));
            digits.put((byte) ((currentByte >> 6) & 0x3));
        }
        // Put the last two digits
        byte currentByte = obscuredMessageBits[3];
        digits.put((byte) (currentByte & 0x3));
        digits.put((byte) ((currentByte >> 2) & 0x3));
        byte[] digitsArray = digits.array();

        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        Integer separatorsAdded = 0;
        // Digits array is in reverse order
        for (int i = digitsArray.length - 1; i >= 0; i--) {
            if (((sb.length() - separatorsAdded) % groupLen) == 0) {
                sb.append(separator);
                separatorsAdded += 1;
            }
            String digit = String.valueOf(keyMapping[digitsArray[i]]);
            sb.append(digit);
        }
        return sb.toString();
    }

    @Override
    public String toKeycode() throws IOException, UnsupportedKeyMappingException {
        return toKeycode("1", " ", 3, null);
    }
}
