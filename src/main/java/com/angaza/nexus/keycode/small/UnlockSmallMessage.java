package com.angaza.nexus.keycode.small;


import com.angaza.nexus.keycode.exceptions.UnsupportedMessageDaysException;
import com.angaza.nexus.keycode.exceptions.UnsupportedMessageIdException;
import com.angaza.nexus.keycode.exceptions.UnsupportedMessageTypeException;

public class UnlockSmallMessage extends AddCreditSmallMessage {

    public UnlockSmallMessage(
            int messageId,
            byte[] secretKey) throws UnsupportedMessageDaysException, UnsupportedMessageIdException,
            UnsupportedMessageTypeException {
        super(messageId, UNLOCK_CONSTANT, secretKey);
    }

    public UnlockSmallMessage(
            int messageId,
            int days,
            byte[] secretKey) throws UnsupportedMessageDaysException, UnsupportedMessageIdException,
            UnsupportedMessageTypeException {
        super(messageId, -1, secretKey);
    }
}
