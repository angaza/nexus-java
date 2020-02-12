package com.angaza.nexus.keycode.small;


import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import com.angaza.nexus.keycode.util.HexToByteArray;

import static org.junit.Assert.assertEquals;

public class SmallMessageTest {

    @Test
    public void testGenerateMac_withZeroIdAndBody() throws Exception {
        assertEquals(
                Integer.parseInt("100100001111", 2),
                SmallMessage.generateMac(
                        0,
                        0,
                        0,
                        new HexToByteArray().convert("abababababababababababababababab")));
    }

    @Test
    public void testCompressedMessage_withZeroIdAndBody() throws Exception {
        SmallMessage message = new SmallMessage(
                0,
                0,
                0,
                new HexToByteArray().convert("abababababababababababababababab"));

        byte[] expected = ByteBuffer.allocate(2)
                .put((byte) 0x00)
                .put((byte) 0x00)
                .array();

        for (int i = 0; i < expected.length; i++) {
            assertEquals(
                    message.messageAndBodyBits()[i],
                    expected[i]);
        }
    }

    @Test
    public void testCompressedMessage_with180DayAdd() throws Exception {
        SmallMessage message = new SmallMessage(
                1,
                0,
                179,
                new HexToByteArray().convert("abababababababababababababababab"));

        byte[] expected = ByteBuffer.allocate(2)
                .put((byte) 0xB3)
                .put((byte) 0x04)
                .array();

        for (int i = 0; i < expected.length; i++) {
            assertEquals(
                    message.messageAndBodyBits()[i],
                    expected[i]);
        }
        String generatedKeycode = message.toKeycode();
        assertEquals("122 425 324 553 555", generatedKeycode);
        String[] keyMapping = new String[]{"0", "1", "2", "3"};
        generatedKeycode = message.toKeycode("*", "", 1, keyMapping);
        assertEquals("*00203102331333", generatedKeycode);
    }

    @Test
    public void testToKeycode_withValidInputs() throws Exception {
        SmallMessage message = new SmallMessage(
                100,
                0,
                10,
                new HexToByteArray().convert("ffffffffffffffffffffffffffffffff"));

        List<String> prefixes = Arrays.asList("*", "4", "4", "4", "4");
        List<String> separators = Arrays.asList(" ", " ", "-", " ", " ");
        List<Integer> groupLens = Arrays.asList(3, 3, 3, 4, 2);
        List<String> expectedKeycodes = Arrays.asList(
                "*52 424 422 522 322",
                "452 424 422 522 322",
                "452-424-422-522-322",
                "4524 2442 2522 322",
                "45 24 24 42 25 22 32 2"
        );

        for (int i = 0; i < expectedKeycodes.size(); i++) {
            String generatedKeycode = message.toKeycode(prefixes.get(i), separators.get(i), groupLens.get(i), null);
            assertEquals(expectedKeycodes.get(i), generatedKeycode);
        }
    }
}
