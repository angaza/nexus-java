package com.angaza.nexus.keycode.full;


import java.util.Locale;

import com.angaza.nexus.keycode.exceptions.UnsupportedMessageIdException;

public class FullMessage extends BaseFullMessage {
    private static final int ADD_CREDIT_TYPE_CODE = 0;
    private static final int SET_CREDIT_TYPE_CODE = 1;
    private static final int UNLOCK_HOUR = 99999;

    public FullMessage(
            int fullId,
            int typeCode,
            String body,
            byte[] secretKey) throws UnsupportedMessageIdException {
        super(checkId(fullId), typeCode, body, secretKey);
    }

    private static int checkId(int fullId) throws UnsupportedMessageIdException {
        if (fullId < 0 || fullId > 65534) {
            throw new UnsupportedMessageIdException("unsupported message ID");
        }
        return fullId;
    }

    /**
     * Increase the device's enabled credit by a specified amount.
     *
     * @param id        message ID
     * @param hours     number of enabled hours to add to the device
     * @param secretKey device's secret key
     * @return message object for "add credit"
     */
    public static FullMessage addCredit(
            int id,
            int hours,
            byte[] secretKey) throws UnsupportedMessageIdException {
        return new FullMessage(
                id,
                ADD_CREDIT_TYPE_CODE,
                String.format(Locale.ENGLISH, "%05d", hours),
                secretKey);
    }

    /**
     * Set the device's enabled credit to specified amount.
     *
     * @param id        message ID
     * @param hours     number of enabled hours to set for a device
     * @param secretKey device's secret key
     * @return message object for "set credit"
     */
    public static FullMessage setCredit(
            int id,
            int hours,
            byte[] secretKey) throws UnsupportedMessageIdException {
        return new FullMessage(
                id,
                SET_CREDIT_TYPE_CODE,
                String.format(Locale.ENGLISH, "%05d", hours),
                secretKey);
    }

    /**
     * Unlock a device.
     *
     * @param id        message ID
     * @param secretKey device's secret key
     * @return message object for "unlock"
     */
    public static FullMessage unlock(
            int id,
            byte[] secretKey) throws UnsupportedMessageIdException {
        return new FullMessage(
                id,
                SET_CREDIT_TYPE_CODE,
                String.format(Locale.ENGLISH, "%05d", UNLOCK_HOUR),
                secretKey);
    }
}
