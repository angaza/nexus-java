package com.angaza.nexus.keycode;


import java.io.IOException;

import com.angaza.nexus.keycode.exceptions.UnsupportedKeyMappingException;

public interface Message {
    String toKeycode() throws IOException, UnsupportedKeyMappingException;
}
