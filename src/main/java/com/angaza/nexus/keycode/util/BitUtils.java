package com.angaza.nexus.keycode.util;


import com.github.emboss.siphash.SipHash;
import com.github.emboss.siphash.SipKey;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BitUtils {
    /**
     * Repack a byte buffer, effectively swapping the order of most and least significant bits and
     * bytes. The input byte buffer is interpreted with the most significant bits at the *bottom* of
     * the buffer (ie, index zero), and the output will pack the most significant bits at the
     * *top* of the buffer (ie, the largest index). For example, given an input string of bits
     * and representation:
     * <p>
     * 15 input bits: 0b 1110 1010 0010 110x (equivalent hex representation: 0xEA2C)
     * <p>
     * and an `inputBuffer` representation of:
     * <p>
     * inputBuffer: [ (index 0): 0xEA, (index 1): 0x2C ]
     * numInputBits: 15
     * <p>
     * `repackToBufferBottom` returns the same 15 bits, but packed to the buffer bottom:
     * <p>
     * 15 output bits: 0b x111 0101 0001 0110 (equivalent hex representation: 0x7516)
     * <p>
     * and an output buffer of [ (index 0): 0x16, (index 1): 0x75 ]
     *
     * @param inputBuffer  byte buffer containing the bits to repack
     * @param numInputBits number of significant bits in the input byte buffer
     * @return repacked byte buffer
     */
    public static byte[] repackToBufferBottom(byte[] inputBuffer, int numInputBits) {

        // First, shift bits by a certain number of spaces
        int spacesToShift = inputBuffer.length * 8 - numInputBits;
        ByteBuffer shiftedBuffer = ByteBuffer.allocate(inputBuffer.length);
        for (int j = 0; j < inputBuffer.length; j++) {
            byte bShift = (byte) ((inputBuffer[j] & 0xff) >> spacesToShift);
            byte currentByte;
            if (j > 0) {
                byte previousByte = (byte) (inputBuffer[j - 1] & 0xff);
                byte previousByteShifted = (byte) (previousByte << (8 - spacesToShift));
                currentByte = (byte) ((bShift | previousByteShifted) & 0xff);
            } else {
                currentByte = bShift;
            }
            shiftedBuffer.put(currentByte);
        }

        // Then reverse byte order in the buffer
        ByteBuffer output = ByteBuffer.allocate(inputBuffer.length);
        for (int i = inputBuffer.length - 1; i >= 0; i--) {
            output.put(shiftedBuffer.array()[i]);
        }
        return output.array();
    }

    /**
     * Given some bits, compute arbitrarily many new pseudorandom bits.
     * <p>
     * This routine provides a deterministic source of pseudorandom bits from a
     * seed, which is useful in performing "interleaving" to obscure the structure
     * of a keycode message.
     * <p>
     * Given the same seed, the same bits will be provided in the same order on
     * every call. The approach taken is a lot like key derivation in standard
     * crypto: we use SipHash and apply a simplified HKDF approach akin to:
     * <p>
     * http://tools.ietf.org/html/draft-krawczyk-hkdf-01
     *
     * @param seedBits     arbitrary input bits
     * @param outputLength number of pseudorandom output bits to return
     * @return deterministically computed pseudorandom bits, left-aligned to the nearest byte
     *         boundary
     */
    public static byte[] pseudorandomBits(byte[] seedBits, int outputLength) throws IOException {
        byte[] fixedKey = new byte[16];
        Integer iterations = (int) (Math.ceil(outputLength / 64.0) * 64);
        BitOutputStream outBits = new BitOutputStream();
        for (int i = 0; i < iterations; i++) {
            BitOutputStream bytesToDigest = new BitOutputStream();
            bytesToDigest.write((byte) i, Byte.SIZE);
            // Our input seed bits are in the reverse order of what SipHash.digest expects, so here,
            // we reverse the byte order.
            byte[] reversedSeedBits = BitUtils.repackToBufferBottom(seedBits, seedBits.length * 8);
            bytesToDigest.write(reversedSeedBits, reversedSeedBits.length * Byte.SIZE);
            Long hash = SipHash.digest(new SipKey(fixedKey), bytesToDigest.toByteArray());
            outBits.write(
                    ByteBuffer.allocate(Long.SIZE / Byte.SIZE)
                            .order(ByteOrder.LITTLE_ENDIAN)
                            .putLong(hash)
                            .array(),
                    Long.SIZE);
        }
        BitInputStream result = new BitInputStream(outBits.toByteArray());
        byte[] truncatedResult = result.read(outputLength);
        return BitUtils.repackToBufferBottom(truncatedResult, outputLength);
    }
}
