package com.angaza.nexus.keycode;


import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.angaza.nexus.keycode.exceptions.UnsupportedKeyMappingException;
import com.angaza.nexus.keycode.exceptions.UnsupportedMessageDaysException;
import com.angaza.nexus.keycode.exceptions.UnsupportedMessageIdException;
import com.angaza.nexus.keycode.exceptions.UnsupportedMessageTypeException;
import com.angaza.nexus.keycode.exceptions.UnsupportedProtocolException;

import com.angaza.nexus.keycode.full.FullMessage;
import com.angaza.nexus.keycode.small.AddCreditSmallMessage;
import com.angaza.nexus.keycode.small.UnlockSmallMessage;

public class KeycodeFactory {

    private static final double SECONDS_PER_HOUR = 60 * 60;
    private static final double SECONDS_PER_DAY = SECONDS_PER_HOUR * 24;

    /**
     * @param clampedTime     the base time for calculating the new disabled time for the device;
     *                        this value equals to max(now, device's disableWhen)
     * @param messageId       the message id to be sent to the device
     * @param secretKey       the secret key of the device
     * @param keycodeProtocol the specific keycode protocol to use
     * @param seconds         the timeDelta (in seconds) to add so the new disabled time of the
     *                        device will be the device's notion of now + timeDelta (rounded
     *                        appropriately for the chosen protocol)
     * @return the keycode representation of the message
     * @throws UnsupportedProtocolException when key code is not able to be generated
     */

    public static KeycodeMetadata addCredit(
            Date clampedTime,
            int messageId,
            byte[] secretKey,
            String keycodeProtocol,
            long seconds
    ) throws IOException, UnsupportedKeyMappingException, UnsupportedMessageDaysException,
            UnsupportedMessageIdException, UnsupportedMessageTypeException,
            UnsupportedProtocolException {
        final String keycode;
        final long secondsSent;
        final Date newDisabledTime;
        final int days = (int) Math.ceil(seconds / SECONDS_PER_DAY);
        final int hours = (int) Math.ceil(seconds / SECONDS_PER_HOUR);
        if (keycodeProtocol == null) {
            throw new UnsupportedProtocolException();
        }
        Calendar calendar = Calendar.getInstance();
        switch (keycodeProtocol.toUpperCase(Locale.US)) {
            case KeycodeProtocol.SMALL:
                keycode = new AddCreditSmallMessage(messageId, days, secretKey).toKeycode();
                secondsSent = TimeUnit.DAYS.toSeconds(days);
                calendar.setTime(clampedTime);
                calendar.add(Calendar.DAY_OF_MONTH, days);
                newDisabledTime = calendar.getTime();
                break;
            case KeycodeProtocol.FULL:
                keycode = FullMessage.addCredit(messageId, hours, secretKey).toKeycode();
                secondsSent = TimeUnit.HOURS.toSeconds(hours);
                calendar.setTime(clampedTime);
                calendar.add(Calendar.HOUR_OF_DAY, hours);
                newDisabledTime = calendar.getTime();
                break;
            default:
                throw new UnsupportedProtocolException();
        }
        return new KeycodeMetadata(
                newDisabledTime,
                new KeycodeData(
                        KeycodeData.ADD,
                        keycode,
                        messageId,
                        secondsSent
                )
        );
    }

    public static KeycodeMetadata unlock(
            int messageId,
            byte[] secretKey,
            String keycodeProtocol
    ) throws IOException, UnsupportedKeyMappingException, UnsupportedMessageDaysException,
            UnsupportedMessageIdException, UnsupportedMessageTypeException,
            UnsupportedProtocolException {
        final String keycode;
        if (keycodeProtocol == null) {
            throw new UnsupportedProtocolException();
        }
        switch (keycodeProtocol.toUpperCase(Locale.US)) {
            case KeycodeProtocol.SMALL:
                keycode = new UnlockSmallMessage(messageId, secretKey).toKeycode();
                break;
            case KeycodeProtocol.FULL:
                keycode = FullMessage.unlock(messageId, secretKey).toKeycode();
                break;
            default:
                throw new UnsupportedProtocolException();
        }
        return new KeycodeMetadata(
                null,
                new KeycodeData(
                        KeycodeData.UNLOCK,
                        keycode,
                        messageId,
                        0
                )
        );
    }
}
