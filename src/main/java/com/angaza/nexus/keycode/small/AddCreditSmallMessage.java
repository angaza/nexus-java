package com.angaza.nexus.keycode.small;


import com.angaza.nexus.keycode.exceptions.UnsupportedMessageDaysException;
import com.angaza.nexus.keycode.exceptions.UnsupportedMessageIdException;
import com.angaza.nexus.keycode.exceptions.UnsupportedMessageTypeException;

public class AddCreditSmallMessage extends SmallMessage {
    protected static final int ADD_CREDIT_TYPE_CODE_CONSTANT = 0;
    protected static final int COARSE_DAYS_PER_INCREMENT_ID = 3;
    protected static final int UNLOCK_CONSTANT = Integer.MAX_VALUE;

    /**
     * @param messageId id number for this message
     * @param days      number of days of credit to add; unlock messages should use
     *                  {@value AddCreditSmallMessage#UNLOCK_CONSTANT}
     * @param secretKey secret hash key (16 bytes)
     * @throws UnsupportedMessageDaysException
     * @throws UnsupportedMessageTypeException
     */
    public AddCreditSmallMessage(
            int messageId,
            int days,
            byte[] secretKey) throws UnsupportedMessageDaysException, UnsupportedMessageIdException,
            UnsupportedMessageTypeException {
        super(
                messageId,
                ADD_CREDIT_TYPE_CODE_CONSTANT,
                AddCreditSmallMessage.generateBody(days),
                secretKey);
    }

    public static int generateBody(int days) throws UnsupportedMessageDaysException {
        int incrementId;
        if ((1 <= days) && (days <= 180)) {
            incrementId = days - 1;
        } else if ((181 <= days) && (days <= 405)) {
            incrementId = ((days - 181) / COARSE_DAYS_PER_INCREMENT_ID) + 180;
        } else if (days == AddCreditSmallMessage.UNLOCK_CONSTANT) {
            incrementId = 255;
        } else {
            throw new UnsupportedMessageDaysException("unsupported number of days");
        }

        return incrementId;
    }
}
