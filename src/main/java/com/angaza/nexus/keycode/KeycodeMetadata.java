package com.angaza.nexus.keycode;


import java.util.Date;

public final class KeycodeMetadata {
    private final transient Date newDisabledWhen;
    private final transient KeycodeData keycodeData;

    public final Date getNewDisabledWhen() {
        return this.newDisabledWhen;
    }

    public final KeycodeData getKeycodeData() {
        return this.keycodeData;
    }

    public KeycodeMetadata(Date newDisabledWhen, KeycodeData keycodeData) {
        super();
        this.newDisabledWhen = newDisabledWhen;
        this.keycodeData = keycodeData;
    }
}
