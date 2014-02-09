/*
 * TextureUtils
 *
 *  Created on: May 24, 2011
 *      Author: Dmytro Baryskyy
 */


package com.parrot.freeflight.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class TextureUtils 
{
	/**
	 * Finds greater nearest number that is power of 2
	 * @return long
	 */
	public static long roundPower2(final long x)
	{
		int rval=256;

		while(rval < x)
			rval <<= 1;

		return rval;
	}
	
	
	/**
	 * Makes a texture from any bitmap. 
	 * (Texture should have size that is power of 2) 
	 * @param bmp bitmap
	 * @return BitmapDrawable that has size that is power of 2. Bitmap is not stretched, free space 
	 * is filled with default color.
	 */
	public static Bitmap makeTexture(Resources res, Bitmap bmp) 
	{
		if (bmp == null) {
			throw new IllegalArgumentException("Bitmap can't be null");
		}
		
		int height = (int) roundPower2(bmp.getHeight());
		int width = (int) roundPower2(bmp.getWidth());
		
		Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(bmp, 0, 0, null);
		
		return result;
	}

}
