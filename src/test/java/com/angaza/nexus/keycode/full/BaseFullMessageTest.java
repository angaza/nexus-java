package com.angaza.nexus.keycode.full;


import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import com.angaza.nexus.keycode.util.HexToByteArray;

import static org.junit.Assert.assertEquals;

public class BaseFullMessageTest {

    @Test
    public void testGenerateMac_correct() throws Exception {
        assertEquals(
                "663904",
                BaseFullMessage.generateMac(
                        1223,
                        0,
                        Integer.parseInt("00993", 10),
                        new HexToByteArray().convert("abababababababababababababababab")));
    }

    @Test
    public void testObscure_specValuesOK() throws Exception {
        List<String> inputs = Arrays.asList(
                "12345678901250",
                "12345678901241",
                "00000000524232",
                "00000000445755");

        List<String> expected = Arrays.asList(
                "57458927901250",
                "05094833901241",
                "57396884524232",
                "03605158445755");

        for (int i = 0; i < inputs.size(); i++) {
            assertEquals(
                    expected.get(i),
                    BaseFullMessage.obscure(inputs.get(i), 1)
            );
        }
    }

    @Test
    public void testKeycode_valuesAndFormattingOK() throws Exception {
        BaseFullMessage message = new BaseFullMessage(
                1223,
                0,
                "00993",
                new HexToByteArray().convert("abababababababababababababababab"));

        List<String> prefixes = Arrays.asList("", "*", "*");
        List<String> suffixes = Arrays.asList("", "#", "#");
        List<String> separators = Arrays.asList("", "-", "-");
        List<Integer> groupLens = Arrays.asList(3, 3, 4);

        List<String> expectedKeycodes = Arrays.asList(
                "88519055663904",
                "*885-190-556-639-04#",
                "*8851-9055-6639-04#"
        );

        for (int i = 0; i < expectedKeycodes.size(); i++) {
            String generatedKeycode = message.toKeycode(
                    prefixes.get(i),
                    suffixes.get(i),
                    separators.get(i),
                    groupLens.get(i));
            assertEquals(expectedKeycodes.get(i), generatedKeycode);
        }
    }
}
