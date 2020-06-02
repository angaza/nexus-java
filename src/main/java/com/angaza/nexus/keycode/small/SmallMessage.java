package com.angaza.nexus.keycode.small;


import com.angaza.nexus.keycode.exceptions.UnsupportedMessageIdException;
import com.angaza.nexus.keycode.exceptions.UnsupportedMessageTypeException;

public class SmallMessage extends BaseSmallMessage {
    public SmallMessage(
            int messageId,
            int typeCode,
            int body,
            byte[] secretKey) throws UnsupportedMessageIdException,
            UnsupportedMessageTypeException {
        super(checkMessageId(messageId), checkTypeCode(typeCode), body, secretKey);
    }

    private static int checkMessageId(int messageId) throws UnsupportedMessageIdException {
        if (messageId < 0 || messageId > 65534) {
            throw new UnsupportedMessageIdException("unsupported message ID");
        }
        return messageId;
    }

    private static int checkTypeCode(int typeCode) throws UnsupportedMessageTypeException {
        if (typeCode < 0 || typeCode > 2 || typeCode == 1) {
            throw new UnsupportedMessageTypeException("unsupported message type code");
        }
        return typeCode;
    }
}
