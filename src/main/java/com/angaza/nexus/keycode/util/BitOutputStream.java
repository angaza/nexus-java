package com.angaza.nexus.keycode.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class BitOutputStream {
    private final ByteArrayOutputStream byteStream = new ByteArrayOutputStream(32);
    private int bitsWritten;
    private int byteBuffer;

    public final byte[] toByteArray() throws IOException {
        ByteArrayOutputStream stream = this.byteStream;
        int bitsStillInBuffer = this.bitsWritten % 8;
        if (bitsStillInBuffer > 0) {
            stream = new ByteArrayOutputStream(this.byteStream.size() + 1);
            this.byteStream.writeTo((OutputStream) stream);
            stream.write(this.byteBuffer << 8 - bitsStillInBuffer);
        }

        byte[] var10000 = stream.toByteArray();
        return var10000;
    }

    public final void write(byte source, int bitsToAdd) {
        int bitsToNextByte = 8 - this.bitsWritten % 8;
        boolean var5 = false;
        int bitsToAddToBuffer = Math.min(bitsToNextByte, bitsToAdd);
        this.bitsWritten += bitsToAdd;
        this.byteBuffer <<= bitsToAddToBuffer;
        this.byteBuffer |= (source & 255) >> 8 - bitsToAddToBuffer;
        if (bitsToNextByte == bitsToAddToBuffer) {
            this.byteStream.write(this.byteBuffer);
            this.byteBuffer = source;
        }
    }

    public final void write(byte[] source, int bitsToAdd) {
        int byteIndex = 0;

        for (int bitsRemaining = bitsToAdd; bitsRemaining > 0; bitsRemaining -= 8) {
            byte var10001 = source[byteIndex++];
            byte var5 = 8;
            byte var8 = var10001;
            boolean var6 = false;
            int var9 = Math.min(bitsRemaining, var5);
            this.write(var8, var9);
        }
    }
}
