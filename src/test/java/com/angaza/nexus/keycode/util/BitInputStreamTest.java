package com.angaza.nexus.keycode.util;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class BitInputStreamTest {
    @Test
    public final void read_incomplete_byte() throws IOException {
        byte[] result = new byte[32];
        result[0] = (byte) 161;
        result[1] = (byte) 217;
        BitInputStream stream = new BitInputStream(result);
        result = stream.read(10);
        assertEquals((byte) 161, result[0]);
        assertEquals((byte) 192, result[1]);
    }

    @Test
    public final void read() throws IOException {
        byte[] result = new byte[32];
        result[0] = (byte) 161;
        result[1] = (byte) 239;
        result[2] = (byte) 2;
        BitInputStream stream = new BitInputStream(result);
        result = stream.read(24);
        assertEquals((byte) 161, result[0]);
        assertEquals((byte) 239, result[1]);
        assertEquals((byte) 2, result[2]);
    }
}

