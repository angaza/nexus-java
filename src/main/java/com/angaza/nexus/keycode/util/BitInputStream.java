package com.angaza.nexus.keycode.util;


import java.io.IOException;

public final class BitInputStream {
    private int readBitCount;
    private final byte[] source;

    public final byte[] read(int totalBitCount) throws IOException {
        BitOutputStream bitOutput = new BitOutputStream();

        int proposedReadCount;
        for (int remainingBitCount = totalBitCount; remainingBitCount > 0;
             this.readBitCount += proposedReadCount) {
            int bitCountToNextByte = 8 - this.readBitCount % 8;
            boolean var6 = false;
            proposedReadCount = Math.min(remainingBitCount, bitCountToNextByte);
            byte byteToRead = (byte) (this.source[this.readBitCount / 8] << 8 - bitCountToNextByte);
            bitOutput.write(byteToRead, proposedReadCount);
            remainingBitCount -= proposedReadCount;
        }

        return bitOutput.toByteArray();
    }

    public BitInputStream(byte[] source) {
        super();
        this.source = source;
    }
}
