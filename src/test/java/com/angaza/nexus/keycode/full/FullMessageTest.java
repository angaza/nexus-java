package com.angaza.nexus.keycode.full;


import org.junit.Test;

import java.nio.ByteBuffer;

import com.angaza.nexus.keycode.exceptions.UnsupportedMessageIdException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FullMessageTest {
    private static final byte[] SECRET_KEY = ByteBuffer
            .allocate(16)
            .put((byte) 0xc4)
            .put((byte) 0xb8)
            .put((byte) '@')
            .put((byte) 'H')
            .put((byte) 0xcf)
            .put((byte) 0x04)
            .put((byte) '$')
            .put((byte) 0xa2)
            .put((byte) ']')
            .put((byte) 0xc5)
            .put((byte) 0xe9)
            .put((byte) 0xd3)
            .put((byte) 0xf0)
            .put((byte) 'g')
            .put((byte) '@')
            .put((byte) '6')
            .array();

    @Test(expected = UnsupportedMessageIdException.class)
    public void testAddCredit_invalidIdThrows() throws Exception {
        FullMessage.addCredit(
                65535,
                24 * 7,
                SECRET_KEY);
    }

    @Test(expected = UnsupportedMessageIdException.class)
    public void testSetCredit_invalidIdThrows() throws Exception {
        FullMessage.setCredit(
                65535,
                24 * 7,
                SECRET_KEY);
    }

    @Test(expected = UnsupportedMessageIdException.class)
    public void testAddCredit_negativeIdThrows() throws Exception {
        FullMessage.addCredit(
                -1,
                24 * 7,
                SECRET_KEY);
    }

    @Test
    public void testAddCredit_OK() throws Exception {

        FullMessage message = FullMessage.addCredit(
                42,
                24 * 7,
                SECRET_KEY);
        String keycode = message.toKeycode("", "", "", 3);

        assertEquals(message.secretKey, SECRET_KEY);

        // Header should start with add credit typecode
        assertTrue(message.header.substring(0, 1).equals("0"));
        assertEquals(message.header, "042");

        assertEquals(message.body, "00168");
        assertEquals(message.body.substring(message.body.length() - 3), "168");

        assertEquals(keycode, "18626101219303");
    }

    @Test
    public void testAddCredit_withSuffixPrefix_OK() throws Exception {

        FullMessage message = FullMessage.addCredit(
                42,
                24 * 7,
                SECRET_KEY);

        String prefix = "*";
        String suffix = "#";
        String keycode = message.toKeycode(prefix, suffix, "", 3);

        assertEquals(message.secretKey, SECRET_KEY);

        assertTrue(message.header.substring(0, 1).equals("0"));
        assertEquals(message.header, "042");

        assertEquals(message.body, "00168");
        assertEquals(message.body.substring(message.body.length() - 3), "168");

        assertEquals(keycode, prefix + "18626101219303" + suffix);
    }

    @Test
    public void testSetCredit_OK() throws Exception {

        FullMessage message = FullMessage.setCredit(
                242,
                24 * 7,
                SECRET_KEY);
        String keycode = message.toKeycode("", "", "", 3);

        assertEquals(message.secretKey, SECRET_KEY);

        // Header should start with set credit typecode
        assertTrue(message.header.substring(0, 1).equals("1"));
        assertEquals(message.header, "150"); // LSB 242 == Dec 50

        assertEquals(message.body, "00168");
        assertEquals(message.body.substring(message.body.length() - 3), "168");

        assertEquals(keycode, "84916574650252");
    }
}
