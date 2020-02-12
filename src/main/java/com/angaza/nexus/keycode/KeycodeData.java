package com.angaza.nexus.keycode;


public final class KeycodeData {
    private final String type;
    private final String keycode;
    private final int messageId;
    private final long seconds;

    public static final String ADD = "ADD";
    public static final String UNLOCK = "UNLOCK";

    public final String getType() {
        return this.type;
    }

    public final String getKeycode() {
        return this.keycode;
    }

    public final int getMessageId() {
        return this.messageId;
    }

    public final long getSeconds() {
        return this.seconds;
    }

    public KeycodeData(String type, String keycode, int messageId, long seconds) {
        super();
        this.type = type;
        this.keycode = keycode;
        this.messageId = messageId;
        this.seconds = seconds;
    }
}
