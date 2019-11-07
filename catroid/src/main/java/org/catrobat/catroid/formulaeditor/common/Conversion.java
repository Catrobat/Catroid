/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

package org.catrobat.catroid.formulaeditor.common;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badlogic.gdx.graphics.Pixmap;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class Conversion {
	public static final double TRUE = 1d;
	public static final double FALSE = 0d;

	@Nullable
	public static Double tryParseDouble(String argument) {
		try {
			return Double.parseDouble(argument);
		} catch (NumberFormatException numberFormatException) {
			return null;
		}
	}

	@ColorInt
	public static int tryParseColor(String string) {
		return tryParseColor(string, Color.BLACK);
	}

	@ColorInt
	private static int tryParseColor(String string, int defaultValue) {
		if (string != null && string.length() == 7 && string.matches("^#[0-9a-fA-F]+$")) {
			return Color.parseColor(string);
		} else {
			return defaultValue;
		}
	}

	@Nullable
	public static Double convertArgumentToDouble(Object argument) {
		if (argument == null) {
			return null;
		} else if (argument instanceof String) {
			return tryParseDouble((String) argument);
		} else {
			return (Double) argument;
		}
	}

	@NotNull
	public static Double booleanToDouble(boolean value) {
		if (value) {
			return TRUE;
		} else {
			return FALSE;
		}
	}

	public static boolean matchesColor(@NonNull ByteBuffer pixels, int color) {
		byte[] bytes = new byte[pixels.remaining()];
		pixels.get(bytes);

		for (int i = 0; i < bytes.length; i+=4) {
			int pixelColor = ((bytes[i] & 0xFF) << 16) | ((bytes[i + 1] & 0xFF) << 8) | bytes[i + 2] & 0xFF;
			if (compareColors(color, pixelColor)) {
				return true;
			}
		}
		return false;
	}

	public static boolean compareColors(int colorA, int colorB) {
		return (Color.red(colorA) & 0b11111000) == (Color.red(colorB) & 0b11111000) &&
		(Color.green(colorA) & 0b11111000) == (Color.green(colorB) & 0b11111000) &&
		(Color.blue(colorA) & 0b11110000) == (Color.blue(colorB) & 0b11110000);
	}

	public static Bitmap convertToBitmap(@NonNull Pixmap pixmap) {
		ByteBuffer buf = pixmap.getPixels();
		byte[] byteColors = new byte[buf.remaining()];
		buf.get(byteColors);
		buf.rewind();

		return convertToBitmap(byteColors, pixmap.getWidth(), pixmap.getHeight());
	}

	public static Bitmap convertToBitmap(@NonNull byte[] byteColors, int pixmapWidth, int pixmapHeight) {
		int[] colors = new int[byteColors.length / 4];
		for (int i = 0; i < colors.length; i++) {
			colors[i] = Color.argb(0xFF, byteColors[i * 4] & 0xFF,
					byteColors[i * 4 + 1] & 0xFF, byteColors[i * 4 + 2] & 0xFF);
		}

		return Bitmap.createBitmap(colors, 0, pixmapWidth,
				pixmapWidth, pixmapHeight, Bitmap.Config.ARGB_8888);
	}

	public static Bitmap flipBitmap(Bitmap bitmap) {
		Matrix m = new Matrix();
		m.preScale(1, -1);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
	}
}
