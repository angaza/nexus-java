package com.angaza.nexus.keycode.util;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public final class HexToByteArrayTest {
    @Test
    public final void convert_nullWhenEmptyString() {
        HexToByteArray toByteArray = new HexToByteArray();
        assertNull(toByteArray.convert(""));
    }

    @Test
    public final void convert_nullWhenOddLengthString() {
        HexToByteArray toByteArray = new HexToByteArray();
        assertNull(toByteArray.convert("a"));
    }

    @Test
    public final void convert_nullWhenInvalid() {
        HexToByteArray toByteArray = new HexToByteArray();
        assertNull(toByteArray.convert("rr"));
    }

    @Test
    public final void convert_validLength2() {
        HexToByteArray toByteArray = new HexToByteArray();
        byte[] answer = toByteArray.convert("ab");
        assertEquals((long) 1, (long) (answer != null ? answer.length : null));
        assertEquals((byte) -85, (byte) (answer != null ? answer[0] : null));
    }

    @Test
    public final void convert_validLength32() {
        HexToByteArray toByteArray = new HexToByteArray();
        byte[] answer = toByteArray.convert("abababababababababababababababab");
        assertEquals((long) 16, (long) (answer != null ? answer.length : null));
        int i = 0;

        for (byte var4 = 16; i < var4; ++i) {
            assertEquals((byte) -85, (byte) (answer != null ? answer[i] : null));
        }

    }
}
