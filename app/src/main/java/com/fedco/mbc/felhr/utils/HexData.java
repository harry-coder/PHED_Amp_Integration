package com.fedco.mbc.felhr.utils;

public class HexData {
    private static final String HEXES = "0123456789ABCDEF";
    private static final String HEX_INDICATOR = "0x";
    private static final String SPACE = " ";

    private HexData() {
    }

    public static String hexToString(byte[] data) {
        if (data == null) {
            return null;
        }
        StringBuilder hex = new StringBuilder(data.length * 2);
        for (int i = 0; i <= data.length - 1; i++) {
            byte dataAtIndex = data[i];
            hex.append(HEX_INDICATOR);
            hex.append(HEXES.charAt((dataAtIndex & 240) >> 4)).append(HEXES.charAt(dataAtIndex & 15));
            hex.append(SPACE);
        }
        return hex.toString();
    }

    public static byte[] stringTobytes(String hexString) {
        String stringProcessed = hexString.trim().replaceAll(HEX_INDICATOR, "").replaceAll("\\s+", "");
        byte[] data = new byte[(stringProcessed.length() / 2)];
        int j = 0;
        for (int i = 0; i <= stringProcessed.length() - 1; i += 2) {
            data[j] = (byte) Integer.parseInt(stringProcessed.substring(i, i + 2), 16);
            j++;
        }
        return data;
    }

    public static String hex4digits(String id) {
        if (id.length() == 1) {
            return "000" + id;
        }
        if (id.length() == 2) {
            return "00" + id;
        }
        if (id.length() == 3) {
            return "0" + id;
        }
        return id;
    }
}
