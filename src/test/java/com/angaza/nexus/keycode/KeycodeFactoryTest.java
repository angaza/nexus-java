package com.angaza.nexus.keycode;


import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.angaza.nexus.keycode.KeycodeProtocol;
import com.angaza.nexus.keycode.exceptions.UnsupportedProtocolException;
import com.angaza.nexus.keycode.full.FullMessage;
import com.angaza.nexus.keycode.small.AddCreditSmallMessage;
import com.angaza.nexus.keycode.small.UnlockSmallMessage;
import com.angaza.nexus.keycode.util.HexToByteArray;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AddCreditSmallMessage.class,
        FullMessage.class,
        KeycodeFactory.class,
        UnlockSmallMessage.class
})
public class KeycodeFactoryTest {

    private AddCreditSmallMessage addCreditSmallMessage;
    private FullMessage fullMessage;
    private UnlockSmallMessage unlockSmallMessage;

    @Before
    public void setUp() throws Exception {
        addCreditSmallMessage = mock(AddCreditSmallMessage.class);
        fullMessage = mock(FullMessage.class);
        unlockSmallMessage = mock(UnlockSmallMessage.class);

        mockStatic(AddCreditSmallMessage.class);
        mockStatic(FullMessage.class);
        mockStatic(UnlockSmallMessage.class);
    }

    @Test(expected = UnsupportedProtocolException.class)
    public void testUnlock_unsupported() throws Exception {
        KeycodeFactory.unlock(0, new byte[0], "TINY");
    }

    @Test
    public void testUnlock_small() throws Exception {
        whenNew(UnlockSmallMessage.class).withAnyArguments().thenReturn(unlockSmallMessage);
        when(unlockSmallMessage.toKeycode()).thenReturn("small");

        KeycodeMetadata output = KeycodeFactory.unlock(0,
                new HexToByteArray().convert("abababababababababababababababab"),
                KeycodeProtocol.SMALL);

        assertNull(output.getNewDisabledWhen());
        assertEquals("UNLOCK", output.getKeycodeData().getType());
        assertEquals("small", output.getKeycodeData().getKeycode());
        assertEquals(0, output.getKeycodeData().getMessageId());
        assertEquals(0, output.getKeycodeData().getSeconds());
    }

    @Test
    public void testUnlock_full() throws Exception {
        when(FullMessage.unlock(
                eq(0),
                any(byte[].class))).thenReturn(fullMessage);
        when(fullMessage.toKeycode()).thenReturn("full");

        KeycodeMetadata output = KeycodeFactory.unlock(0,
                new HexToByteArray().convert("abababababababababababababababab"),
                KeycodeProtocol.FULL);

        assertNull(output.getNewDisabledWhen());
        assertEquals("UNLOCK", output.getKeycodeData().getType());
        assertEquals("full", output.getKeycodeData().getKeycode());
        assertEquals(0, output.getKeycodeData().getMessageId());
        assertEquals(0, output.getKeycodeData().getSeconds());
    }

    @Test(expected = UnsupportedProtocolException.class)
    public void testAddCredit_unsupported() throws Exception {
        KeycodeFactory.addCredit(
                new Date(0L),
                0,
                new byte[0],
                "TINY",
                0L
        );
    }

    @Test
    public void testAddCredit_small() throws Exception {
        whenNew(AddCreditSmallMessage.class).withAnyArguments().thenReturn(addCreditSmallMessage);
        when(addCreditSmallMessage.toKeycode()).thenReturn("small");

        KeycodeMetadata output = KeycodeFactory.addCredit(
                new Date(2L),
                2,
                new HexToByteArray().convert("abababababababababababababababab"),
                KeycodeProtocol.SMALL,
                100
        );

        Date newDisabledWhen = output.getNewDisabledWhen();
        assertNotNull(newDisabledWhen);
        assertEquals(2 + 24 * 60 * 60 * 1000, newDisabledWhen.getTime());
        assertEquals("ADD", output.getKeycodeData().getType());
        assertEquals("small", output.getKeycodeData().getKeycode());
        assertEquals(2, output.getKeycodeData().getMessageId());
        assertEquals(24 * 60 * 60, output.getKeycodeData().getSeconds());
    }

    @Test
    public void testAddCredit_full() throws Exception {
        when(FullMessage.addCredit(
                eq(3),
                eq(2),
                any(byte[].class))).thenReturn(fullMessage);
        when(fullMessage.toKeycode()).thenReturn("full");

        KeycodeMetadata output = KeycodeFactory.addCredit(
                new Date(200L),
                3,
                new HexToByteArray().convert("abababababababababababababababab"),
                KeycodeProtocol.FULL,
                3602
        );

        Date newDisabledWhen = output.getNewDisabledWhen();
        assertNotNull(newDisabledWhen);
        assertEquals(200 + 2 * 60 * 60 * 1000, newDisabledWhen.getTime());
        assertEquals("ADD", output.getKeycodeData().getType());
        assertEquals("full", output.getKeycodeData().getKeycode());
        assertEquals(3, output.getKeycodeData().getMessageId());
        assertEquals(2 * 60 * 60, output.getKeycodeData().getSeconds());
    }
}
