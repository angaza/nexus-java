package com.angaza.nexus.keycode.small;


import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.angaza.nexus.keycode.exceptions.UnsupportedMessageDaysException;
import com.angaza.nexus.keycode.exceptions.UnsupportedMessageIdException;
import com.angaza.nexus.keycode.util.HexToByteArray;

import static org.junit.Assert.assertEquals;

public class AddCreditSmallMessageTest {

    @Test(expected = UnsupportedMessageIdException.class)
    public void testToKeycode_invalidIdThrows() throws Exception {
        AddCreditSmallMessage message = new AddCreditSmallMessage(
                65535,
                1,
                new HexToByteArray().convert("abababababababababababababababab"));
    }

    @Test(expected = UnsupportedMessageIdException.class)
    public void testToKeycode_negativeIdThrows() throws Exception {
        AddCreditSmallMessage message = new AddCreditSmallMessage(
                -1,
                1,
                new HexToByteArray().convert("abababababababababababababababab"));
    }

    @Test(expected = UnsupportedMessageDaysException.class)
    public void testToKeycode_invalidDaysThrows() throws Exception {
        AddCreditSmallMessage message = new AddCreditSmallMessage(
                0,
                406,
                new HexToByteArray().convert("abababababababababababababababab"));
    }

    @Test
    public void testToKeycode_add1Day() throws Exception {
        AddCreditSmallMessage message = new AddCreditSmallMessage(
                0,
                1,
                new HexToByteArray().convert("abababababababababababababababab"));

        ByteBuffer outputMessageBits = ByteBuffer
                .allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(message.messageAndBodyBits());

        Integer expected = Integer.parseInt("00000000000000000", 2);
        Integer actual = (outputMessageBits.getInt(0));
        assertEquals(expected, actual);
        String generatedKeycode = message.toKeycode();
        assertEquals("133 232 343 432 255", generatedKeycode);
    }

    @Test
    public void testToKeycode_add180Days() throws Exception {
        AddCreditSmallMessage message = new AddCreditSmallMessage(
                1,
                180,
                new HexToByteArray().convert("abababababababababababababababab"));

        ByteBuffer outputMessageBits = ByteBuffer
                .allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(message.messageAndBodyBits());

        Integer expected = Integer.parseInt("0000010010110011", 2);
        Integer actual = (outputMessageBits.getInt(0));
        assertEquals(expected, actual);
        String generatedKeycode = message.toKeycode();
        assertEquals("122 425 324 553 555", generatedKeycode);
    }

    @Test
    public void testToKeycode_add181Days() throws Exception {
        AddCreditSmallMessage message = new AddCreditSmallMessage(
                10,
                181,
                new HexToByteArray().convert("abababababababababababababababab"));

        ByteBuffer outputMessageBits = ByteBuffer
                .allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(message.messageAndBodyBits());

        Integer expected = Integer.parseInt("0010100010110100", 2);
        Integer actual = (outputMessageBits.getInt(0));
        assertEquals(expected, actual);
        String generatedKeycode = message.toKeycode();
        assertEquals("132 353 543 455 243", generatedKeycode);
    }

    @Test
    public void testToKeycode_add405Days() throws Exception {
        AddCreditSmallMessage message = new AddCreditSmallMessage(
                125,
                405,
                new HexToByteArray().convert("abababababababababababababababab"));

        ByteBuffer outputMessageBits = ByteBuffer
                .allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(message.messageAndBodyBits());

        Integer expected = Integer.parseInt("1111010011111110", 2);
        Integer actual = (outputMessageBits.getInt(0));
        assertEquals(expected, actual);
        String generatedKeycode = message.toKeycode();
        assertEquals("132 335 454 524 233", generatedKeycode);
    }

    @Test
    public void testToKeycode_largeMessageId() throws Exception {
        AddCreditSmallMessage message = new AddCreditSmallMessage(
                65234,
                405,
                new HexToByteArray().convert("abababababababababababababababab"));

        ByteBuffer outputMessageBits = ByteBuffer
                .allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(message.messageAndBodyBits());

        Integer expected = Integer.parseInt("0100100011111110", 2);
        Integer actual = (outputMessageBits.getInt(0));
        assertEquals(expected, actual);
        String generatedKeycode = message.toKeycode();
        assertEquals("143 235 545 435 454", generatedKeycode);
    }
}
