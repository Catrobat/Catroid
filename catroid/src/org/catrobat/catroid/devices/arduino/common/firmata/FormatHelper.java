/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.devices.arduino.common.firmata;

import java.util.Locale;

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
        sb.append(Integer.toHexString(value).toUpperCase(Locale.getDefault()));
    }
    
    public String formatBinaryData(int[] binaryData, String delimiter, String prefix, String postfix) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < binaryData.length; i++) {
            formatBinary(binaryData[i], sb);
            if (i < binaryData.length-1) {
				sb.append(delimiter);
			}
        }
        sb.append(postfix);
        return sb.toString();
    }

    public String formatBinaryData(int[] binaryData) {
        return formatBinaryData(binaryData, SPACE, QUOTE, QUOTE);
    }

    public String formatBinaryData(byte[] binaryData, String delimiter, String prefix, String postfix) {
        int[] intArray = new int[binaryData.length];
        for (int i=0; i<binaryData.length; i++) {
			intArray[i] = (binaryData[i] & 0xff);
		}
        return formatBinaryData(intArray, delimiter, prefix, postfix);
    }

    public String formatBinaryData(byte[] binaryData) {
        return formatBinaryData(binaryData, SPACE, QUOTE, QUOTE);
    }
}
