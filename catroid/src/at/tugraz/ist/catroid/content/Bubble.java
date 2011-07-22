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
import at.tugraz.ist.catroid.R;

public class Bubble implements Serializable {
	//private static final long serialVersionUID = 1L;

	private Activity activity;

	private String text;
	private Paint textPaint;

	private int defaultEdgeX;
	private int defaultEdgeY;

	private NinePatchDrawable bubble9Patch;
	private float bubbleHeight;
	private float bubbleWidth;
	private float bubbleDefaultHeight;
	private float bubbleDefaultWidth;
	private int bubblePosX;
	private int bubblePosY;

	private Costume costume;

	private Canvas canvas;

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

		defaultEdgeX = 10;
		defaultEdgeY = 10;

	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
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
			textPaint.setTextAlign(Align.CENTER);
			Rect textBound = new Rect();
			textPaint.getTextBounds(text, 0, numberOfCharacters, textBound);
			//textHeight = textBound.height();
			//textWidth = textBound.height();
			bubbleHeight = bubbleDefaultHeight;
			bubbleWidth = bubbleDefaultWidth;
			drawBubble();
			int textPosX = (int) (bubbleWidth / 2);
			int textPosY = (int) (bubbleHeight / 2) - 10;
			drawText(text, textPosX, textPosY);
		} else {
			if (numberOfCharacters >= 6 && numberOfCharacters <= 13) {

				bubbleHeight = bubbleDefaultHeight;
				bubbleWidth = bubbleDefaultWidth;
				//drawBubble(canvas, costume);

			} else if (numberOfCharacters >= 14) {
				textPaint.setTextAlign(Align.RIGHT);
				//drawBubble(canvas, costume);
			}
		}
	}
}
