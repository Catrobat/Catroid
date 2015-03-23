package org.catrobat.catroid.devices.arduino.common.firmata;

/**
 * Helps to format values
 */
public class FormatHelper {

    // default delimiter
    private static final String HEX_PREFIX = "0x";

    public static final String SPACE = " ";
    public static final String QUOTE = "'";

    public String formatBinary(int value) {
        StringBuilder sb = new StringBuilder();
        formatBinary(value, sb);
        return sb.toString();
    }
    
    protected void formatBinary(int value, StringBuilder sb) {
        sb.append(HEX_PREFIX);
        sb.append(Integer.toHexString(value).toUpperCase());
    }
    
    public String formatBinaryData(int[] binaryData, String delimiter, String prefix, String postfix) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < binaryData.length; i++) {
            formatBinary(binaryData[i], sb);
            if (i < binaryData.length-1)
                sb.append(delimiter);
        }
        sb.append(postfix);
        return sb.toString();
    }

    public String formatBinaryData(int[] binaryData) {
        return formatBinaryData(binaryData, SPACE, QUOTE, QUOTE);
    }

    public String formatBinaryData(byte[] binaryData, String delimiter, String prefix, String postfix) {
        int[] intArray = new int[binaryData.length];
        for (int i=0; i<binaryData.length; i++)
            intArray[i] = (binaryData[i] & 0xff);
        return formatBinaryData(intArray, delimiter, prefix, postfix);
    }

    public String formatBinaryData(byte[] binaryData) {
        return formatBinaryData(binaryData, SPACE, QUOTE, QUOTE);
    }
}
