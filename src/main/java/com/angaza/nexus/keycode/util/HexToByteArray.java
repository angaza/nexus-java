package com.angaza.nexus.keycode.util;


public final class HexToByteArray {
    public final byte[] convert(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        if (len == 0 || len % 2 != 0) {
            return null;
        }

        for (int i = 0; i < len; i += 2) {
            int highNibble = Character.digit(s.charAt(i), 16);
            int lowNibble = Character.digit(s.charAt(i + 1), 16);
            if (highNibble == -1 || lowNibble == -1) {
                return null;
            }
            data[i / 2] = (byte) ((highNibble << 4) + lowNibble);
        }
        return data;
    }
}
