package com.angaza.nexus.keycode.small;


import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.angaza.nexus.keycode.exceptions.UnsupportedMessageIdException;
import com.angaza.nexus.keycode.util.HexToByteArray;

import static org.junit.Assert.assertEquals;

public class UnlockSmallMessageTest {

    @Test
    public void testToKeycode_addUnlock() throws Exception {
        UnlockSmallMessage message = new UnlockSmallMessage(
                1,
                new HexToByteArray().convert("abababababababababababababababab"));

        ByteBuffer outputMessageBits = ByteBuffer
                .allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(message.messageAndBodyBits());

        Integer expected = Integer.parseInt("0000010011111111", 2);
        Integer actual = (outputMessageBits.getInt(0));
        assertEquals(expected, actual);
        String generatedKeycode = message.toKeycode();
        assertEquals("134 435 355 535 552", generatedKeycode);
    }
}
