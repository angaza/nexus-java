package com.angaza.nexus.keycode.util;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public final class BitOutputStreamTest {
    @Test
    public final void toByteArray_empty() throws IOException {
        BitOutputStream stream = new BitOutputStream();
        byte[] result = stream.toByteArray();
        assertEquals(0, result.length);
    }

    @Test
    public final void write_singleByte_inputLessThanAvailable() throws IOException {
        BitOutputStream stream = new BitOutputStream();
        stream.write((byte) 160, 5);
        assertEquals((byte) 160, stream.toByteArray()[0]);
    }

    @Test
    public final void write_singleByte_inputMoreThanAvailable() throws IOException {
        BitOutputStream stream = new BitOutputStream();
        stream.write((byte) 160, 5);
        stream.write((byte) 161, 5);

        byte[] result = stream.toByteArray();

        assertEquals(2, result.length);
        assertEquals((byte) 165, result[0]);
        assertEquals((byte) 64, result[1]);
    }

    @Test
    public final void write_byteArray() throws IOException {
        BitOutputStream stream = new BitOutputStream();
        byte[] var2 = new byte[5];
        var2[0] = (byte) 76;
        var2[1] = (byte) 160;
        var2[2] = (byte) 211;
        stream.write(var2, 19);

        byte[] result = stream.toByteArray();

        assertEquals(3, result.length);
        assertEquals((byte) 76, result[0]);
        assertEquals((byte) 160, result[1]);
        assertEquals((byte) 192, result[2]);
    }
}

