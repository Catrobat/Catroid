/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content;

import java.io.Serializable;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.NinePatchDrawable;
import android.util.Log;
import at.tugraz.ist.catroid.R;

public class Bubble implements Serializable {
	//private static final long serialVersionUID = 1L;

	private Activity activity;

	private String text;
	private Paint textPaint;

	private int textOffsetYStart;
	private int textOffsetYNext;

	private NinePatchDrawable bubble9Patch;
	private float bubbleHeight;
	private float bubbleWidth;
	private float bubbleDefaultHeight;
	private float bubbleDefaultWidth;
	private int bubblePosX;
	private int bubblePosY;

	private Costume costume;

	private Canvas canvas;

	public Bubble() {
		text = "";
	}

	public Bubble(String text, Costume costume, Activity activity) {
		this.activity = activity;

		this.text = text;
		textPaint = new Paint();
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(22);
		textPaint.setFakeBoldText(true);
		textPaint.setTypeface(Typeface.MONOSPACE);

		bubble9Patch = (NinePatchDrawable) activity.getResources().getDrawable(R.drawable.bubble);
		bubbleDefaultHeight = bubble9Patch.getIntrinsicHeight();
		bubbleDefaultWidth = bubble9Patch.getIntrinsicWidth();
		bubbleHeight = bubbleDefaultHeight;
		bubbleWidth = bubbleDefaultWidth;
		bubblePosX = 0;
		bubblePosY = 0;

		this.costume = costume;

		textOffsetYStart = 6;
		textOffsetYNext = 10;

	}

	public void init() {
		textPaint = new Paint();
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(22);
		textPaint.setFakeBoldText(true);
		textPaint.setTypeface(Typeface.MONOSPACE);

		bubble9Patch = (NinePatchDrawable) activity.getResources().getDrawable(R.drawable.bubble);
		bubbleDefaultHeight = bubble9Patch.getIntrinsicHeight();
		bubbleDefaultWidth = bubble9Patch.getIntrinsicWidth();
		bubbleHeight = bubbleDefaultHeight;
		bubbleWidth = bubbleDefaultWidth;
		bubblePosX = 0;
		bubblePosY = 0;
		textOffsetYStart = 6;
		textOffsetYNext = 10;
	}

	public void setCostume(Costume costume) {
		this.costume = costume;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
		textPaint = new Paint();
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(22);
		textPaint.setFakeBoldText(true);
		textPaint.setTypeface(Typeface.MONOSPACE);

		bubble9Patch = (NinePatchDrawable) activity.getResources().getDrawable(R.drawable.bubble);
		bubbleDefaultHeight = bubble9Patch.getIntrinsicHeight();
		bubbleDefaultWidth = bubble9Patch.getIntrinsicWidth();
		bubbleHeight = bubbleDefaultHeight;
		bubbleWidth = bubbleDefaultWidth;
		bubblePosX = 0;
		bubblePosY = 0;
		textOffsetYStart = 6;
		textOffsetYNext = 10;
	}

	public void drawBubble() {

		int costumePosX = costume.getDrawPositionX();
		int costumePosY = costume.getDrawPositionY();

		double costumeHeight = costume.getBitmap().getHeight();
		double costumeWidth = costume.getBitmap().getWidth();

		int boundLeft = costumePosX + (int) costumeWidth;
		int boundTop = costumePosY - (int) bubbleHeight;
		int boundRight = boundLeft + (int) bubbleWidth;
		int boundBottom = boundTop + (int) bubbleHeight;

		bubble9Patch.setBounds(boundLeft, boundTop, boundRight, boundBottom);
		bubble9Patch.draw(canvas);
	}

	public void drawText(String string, int textPosX, int textPosY) {

		int costumePosX = costume.getDrawPositionX();
		int costumePosY = costume.getDrawPositionY();

		double costumeHeight = costume.getBitmap().getHeight();
		double costumeWidth = costume.getBitmap().getWidth();

		int boundLeft = costumePosX + (int) costumeWidth;
		int boundTop = costumePosY - (int) bubbleHeight;
		canvas.drawText(string, boundLeft + textPosX, boundTop + textPosY, textPaint);

	}

	public void draw() {
		int numberOfCharacters = text.length();

		if (numberOfCharacters == 0) {
			return;

		} else if (numberOfCharacters >= 1 && numberOfCharacters <= 5) {
			// basic scaling
			textPaint.setTextAlign(Align.CENTER);
			Rect textBound = new Rect();
			textPaint.getTextBounds(text, 0, numberOfCharacters, textBound);

			bubbleHeight = bubbleDefaultHeight;
			bubbleWidth = bubbleDefaultWidth;
			drawBubble();

			int textPosX = (int) (bubbleDefaultWidth / 2);
			int textPosY = (int) (bubbleDefaultHeight / 2) - textOffsetYStart;
			drawText(text, textPosX, textPosY);

		} else {
			if (numberOfCharacters >= 6 && numberOfCharacters <= 13) {
				// x- scaling
				textPaint.setTextAlign(Align.CENTER);
				Rect textBound = new Rect();
				textPaint.getTextBounds(text, 0, text.length(), textBound);
				int newWidth = textBound.width();
				Rect textBoundQ = new Rect();
				textPaint.getTextBounds("12345", 0, 5, textBoundQ);
				int oldWidth = textBoundQ.width();
				int diff = newWidth - oldWidth;
				bubbleHeight = bubbleDefaultHeight;
				bubbleWidth = bubbleDefaultWidth + diff;
				drawBubble();

				int textPosX = (int) (bubbleWidth / 2);
				int textPosY = (int) (bubbleHeight / 2) - textOffsetYStart;
				drawText(text, textPosX, textPosY);

			} else if (numberOfCharacters >= 14) {
				// y- scaling
				textPaint.setTextAlign(Align.CENTER);
				Rect textBound = new Rect();
				textPaint.getTextBounds("12345671234567", 0, 14, textBound);
				int newWidth = textBound.width();
				Rect textBoundQ = new Rect();
				textPaint.getTextBounds("12345", 0, 5, textBoundQ);
				int oldWidth = textBoundQ.width();
				int diff = newWidth - oldWidth;
				bubbleHeight = bubbleDefaultHeight;
				bubbleWidth = bubbleDefaultWidth + diff;

				int tcol = numberOfCharacters / 14;
				if (numberOfCharacters % 14 > 0) {
					tcol++;
				}
				bubbleHeight = bubbleDefaultHeight + (tcol - 1) * (textOffsetYNext + (int) textPaint.getTextSize());
				bubbleWidth = bubbleDefaultWidth + diff;
				drawBubble();
				int charsToDraw = numberOfCharacters;
				String subString = "";
				int subStringStart = 0;
				int subStringEnd = 14;
				int initY = (int) (bubbleDefaultHeight / 2) - textOffsetYStart;
				int counterT = 1;

				while (counterT <= tcol) {

					if (subStringEnd > numberOfCharacters) {
						subStringEnd = numberOfCharacters;
					}
					subString = text.substring(subStringStart, subStringEnd);
					Log.v("SubString", subString + "::" + subStringStart + "::" + subStringEnd + "::" + charsToDraw);

					int textPosX = (int) (bubbleWidth / 2);
					int textPosY = initY + (counterT - 1) * (textOffsetYNext + (int) textPaint.getTextSize());
					drawText(subString, textPosX, textPosY);
					subStringStart = subStringEnd;
					subStringEnd = subStringStart + 14;
					counterT++;
					Log.v("SubString", subString + "::" + subStringStart + "::" + subStringEnd + "::" + charsToDraw);

				}

			}
		}
	}
}
