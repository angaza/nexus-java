package com.angaza.nexus.keycode;


import java.util.Date;

import org.junit.Test;

import com.angaza.nexus.keycode.KeycodeProtocol;
import com.angaza.nexus.keycode.full.FullMessage;
import com.angaza.nexus.keycode.small.AddCreditSmallMessage;
import com.angaza.nexus.keycode.small.UnlockSmallMessage;
import com.angaza.nexus.keycode.util.HexToByteArray;

import static org.junit.Assert.assertEquals;

public class KeycodeFactoryREADMETest {
    protected byte[] secretKey = new HexToByteArray().convert("deadbeefdeadbeefdeadbeefdeadbeef");

    @Test
    public void testAddCredit_full() throws Exception {
        int messageId = 42;
        long seconds = 7 /* days */ * 24 /* hours */ * 60 /* minutes */ * 60 /* seconds */;
        Date clampedTime = new Date();
        KeycodeMetadata output = KeycodeFactory.addCredit(
                clampedTime,
                messageId,
                secretKey,
                KeycodeProtocol.FULL,
                seconds
        );
        String keycode = output.getKeycodeData().getKeycode();
        assertEquals("*599 791 493 194 43#", keycode);
    }

    @Test
    public void testSetCredit_full() throws Exception {
        int messageId = 43;
        int hours = 14 /* days */ * 24 /* hours */;
        FullMessage message = FullMessage.setCredit(messageId, hours, secretKey);
        String keycode = message.toKeycode();
        assertEquals("*272 511 292 039 01#", keycode);
    }

    @Test
    public void testUnlock_full() throws Exception {
        int messageId = 44;
        KeycodeMetadata output = KeycodeFactory.unlock(messageId, secretKey, KeycodeProtocol.FULL);
        String keycode = output.getKeycodeData().getKeycode();
        assertEquals("*578 396 697 305 45#", keycode);
    }

    @Test
    public void testAddCredit_small() throws Exception {
        int messageId = 31;
        long seconds = 7 /* days */ * 24 /* hours */ * 60 /* minutes */ * 60 /* seconds */;
        Date clampedTime = new Date();
        KeycodeMetadata output = KeycodeFactory.addCredit(
                clampedTime,
                messageId,
                secretKey,
                KeycodeProtocol.SMALL,
                seconds
        );
        String keycode = output.getKeycodeData().getKeycode();
        assertEquals("154 535 324 353 534", keycode);
    }

    @Test
    public void testUnlock_small() throws Exception {
        int messageId = 32;
        KeycodeMetadata output = KeycodeFactory.unlock(messageId, secretKey, KeycodeProtocol.SMALL);
        String keycode = output.getKeycodeData().getKeycode();
        assertEquals("153 233 555 553 342", keycode);
    }
}
