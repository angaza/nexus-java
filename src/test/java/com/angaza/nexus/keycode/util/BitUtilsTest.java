package com.angaza.nexus.keycode.util;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static com.angaza.nexus.keycode.util.BitUtils.repackToBufferBottom;

public class BitUtilsTest {
    @Test
    public void testRepackToBufferBottom_NoChange() throws Exception {
        byte[] input = ByteBuffer
                .allocate(2)
                .put((byte) 0xff)
                .put((byte) 0xff)
                .array();
        byte[] result = repackToBufferBottom(input, 16);
        for (int i = 0; i < input.length; i++) {
            assertEquals(result[i], input[i]);
        }
    }

    @Test
    public void testRepackToBufferBottom_simpleReverse() throws Exception {
        byte[] input = ByteBuffer
                .allocate(2)
                .put((byte) 0xff)
                .put((byte) 0x00)
                .array();
        byte[] expected = ByteBuffer
                .allocate(2)
                .put((byte) 0x00)
                .put((byte) 0xff)
                .array();
        byte[] result = repackToBufferBottom(input, 16);
        for (int i = 0; i < input.length; i++) {
            assertEquals(result[i], expected[i]);
        }
    }

    @Test
    public void testRepackToBufferBottom_complexReverse() throws Exception {
        byte[] input = ByteBuffer
                // 0111 0101 1010 0001
                .allocate(2)
                .put((byte) 0x75)
                .put((byte) 0xA1)
                .array();
        byte[] expected = ByteBuffer
                // 0001 1010 0101 0111
                .allocate(2)
                .put((byte) 0xA1)
                .put((byte) 0x75)
                .array();
        byte[] result = repackToBufferBottom(input, 16);
        for (int i = 0; i < input.length; i++) {
            assertEquals(result[i], expected[i]);
        }
    }

    @Test
    public void testRepackToBufferBottom_shiftOne() throws Exception {
        byte[] input = ByteBuffer
                // 15 input bits: 0b 1110 1010 0010 110x (equivalent hex representation: 0xEA2C)
                .allocate(2)
                .put((byte) 0xEA)
                .put((byte) 0x2C)
                .array();
        byte[] expected = ByteBuffer
                // 15 output bits: 0b x111 0101 0001 0110 (equivalent hex representation: 0x7516)
                // Should be reversed as well (0x1675)
                .allocate(2)
                .put((byte) 0x16)
                .put((byte) 0x75)
                .array();
        byte[] result = repackToBufferBottom(input, 15);
        for (int i = 0; i < input.length; i++) {
            assertEquals(result[i], expected[i]);
        }
    }

    @Test
    public void testRepackToBufferBottom_shiftTwo() throws Exception {
        byte[] input = ByteBuffer
                // 14 input bits: 0b 1110 1010 0010 11xx (equivalent hex representation: 0xEA2C)
                .allocate(2)
                .put((byte) 0xEA)
                .put((byte) 0x2C)
                .array();
        byte[] expected = ByteBuffer
                // 14 output bits: 0b xx11 1010 1000 1011 (equivalent hex representation: 0x3A8B)
                // Should be reversed as well (0x8B3A)
                .allocate(2)
                .put((byte) 0x8B)
                .put((byte) 0x3A)
                .array();
        byte[] result = repackToBufferBottom(input, 14);
        for (int i = 0; i < input.length; i++) {
            assertEquals(result[i], expected[i]);
        }
    }

    @Test
    public void testPseudorandomBits_variedSeeds_outputBitsAreExpected() throws Exception {
        List<String> seedBits = Arrays.asList(
                "7",
                "6",
                "",
                "8a91abff01",
                "6fa",
                "06fa"
        );

        List<String> expectedBins = Arrays.asList(
                "111010100010110",
                "000100001011100",
                "100011011100010",
                "000111010100001",
                "0000000010111001",
                "0000000010111001"
        );

        List<Integer> outputLengths = Arrays.asList(
                15,
                15,
                15,
                15,
                16,
                16
        );

        for (int i = 0; i < seedBits.size(); i++) {
            String seedBitsHex = seedBits.get(i);
            Integer numInputBytes = (int) Math.ceil(seedBitsHex.length() / 2.0);
            ByteBuffer byteBuffer = ByteBuffer.allocate(numInputBytes);
            if (numInputBytes > 0) {
                Long seedLong = Long.parseLong(seedBits.get(i), 16);
                while (seedLong > 0) {
                    byteBuffer.put((byte) (seedLong & 0xff));
                    seedLong >>= 8;
                }
            }
            byte[] output = BitUtils.pseudorandomBits(
                    byteBuffer.array(),
                    outputLengths.get(i)
            );
            Integer expectedInt = Integer.parseInt(expectedBins.get(i), 2);
            for (int j = 0; j < output.length; j++) {
                assertEquals(
                        String.format("Seed %s failed", seedBits.get(i)),
                        String.format("%02X", (byte) (expectedInt & 0xff)),
                        String.format("%02X", output[j]));
                expectedInt >>= 8;
            }
        }
    }
}

