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
import java.util.Vector;

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
	private Vector<String> textVector;
	private Paint textPaint;
	private int textSize;

	private int textOffsetYStart;
	private int textOffsetYNext;

	private NinePatchDrawable bubble9Patch;
	private float bubbleHeight;
	private float bubbleWidth;
	private float bubbleDefaultHeight;
	private float bubbleDefaultWidth;
	private int bubblePosX;
	private int bubblePosY;

	private float bubbleMaxWidth;
	private float textBoarder;
	private float textDefaultWidth;
	private float textMaxWidth;
	private float textStartheightOffset;

	private Costume costume;

	private Canvas canvas;

	public Bubble() {
		text = "";
	}

	public void init() {
		textPaint = new Paint();
		textVector = new Vector<String>();
		textSize = 22;
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(textSize);
		textPaint.setFakeBoldText(true);
		textPaint.setTypeface(Typeface.DEFAULT);
		textPaint.setTextAlign(Align.CENTER);

		bubble9Patch = (NinePatchDrawable) activity.getResources().getDrawable(R.drawable.speech_bubble);
		bubbleDefaultHeight = bubble9Patch.getIntrinsicHeight();
		bubbleDefaultWidth = bubble9Patch.getIntrinsicWidth();
		bubbleHeight = bubbleDefaultHeight;
		bubbleWidth = bubbleDefaultWidth;
		bubblePosX = 0;
		bubblePosY = 0;
		textOffsetYStart = 6;
		textOffsetYNext = 10;

		bubbleMaxWidth = (float) (1.4 * bubbleDefaultWidth);
		textStartheightOffset = 36;

		textBoarder = ((bubbleDefaultHeight - (bubbleDefaultHeight - textStartheightOffset)) - textSize) / 2;
		textDefaultWidth = bubbleDefaultWidth - 4 * textBoarder;
		textMaxWidth = bubbleMaxWidth - 4 * textBoarder;
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

	public int getTextWidth(String string) {
		Rect textBound = new Rect();
		textPaint.getTextBounds(string, 0, string.length(), textBound);
		return textBound.width();
	}

	public int getTextheight(String string) {
		Rect textBound = new Rect();
		textPaint.getTextBounds(string, 0, string.length(), textBound);
		return textBound.height();
	}

	public void textSplit() {

		String unsplit = text;
		String wordBuild = "";
		Vector<String> words = new Vector<String>();
		for (int i = 0; i < text.length(); i++) {
			wordBuild += unsplit.charAt(i);
			if (unsplit.charAt(i) == ' ') {
				words.add(wordBuild);
				wordBuild = "";
			}
		}
		words.add(wordBuild);

		String buildBuffer = "";
		String nextBuffer = "";
		String tempBuffer = "";
		String writeBuffer = "";
		buildBuffer = words.get(0);

		for (int i = 1; i < words.size(); i++) {
			nextBuffer = buildBuffer;

			if (getTextWidth(nextBuffer) > textMaxWidth) {
				tempBuffer = "" + nextBuffer.charAt(0);
				for (int k = 1; k < nextBuffer.length(); k++) {
					tempBuffer += nextBuffer.charAt(k);
					if (getTextWidth(tempBuffer) > textMaxWidth) {
						writeBuffer = tempBuffer.substring(0, tempBuffer.length() - 1);
						tempBuffer = "" + nextBuffer.charAt(k);
						textVector.add(writeBuffer);
					}
				}
				buildBuffer = tempBuffer;
				nextBuffer = buildBuffer;
			}

			nextBuffer += words.get(i);
			if (getTextWidth(nextBuffer) > textMaxWidth) {
				textVector.add(buildBuffer);
				buildBuffer = "";
				buildBuffer += words.get(i);
			} else {
				buildBuffer += words.get(i);
			}
		}
		textVector.add(buildBuffer);
	}

	public void draw() {
		init();
		int numberOfCharacters = text.length();

		if (numberOfCharacters == 0) {
			return;

		} else {
			float totalTextWidth = getTextWidth(text);

			if (totalTextWidth <= textDefaultWidth) {

				// basic scaling
				bubbleHeight = bubbleDefaultHeight;
				bubbleWidth = bubbleDefaultWidth;
				drawBubble();

				int textPosX = (int) (bubbleDefaultWidth / 2);
				int textPosY = (int) (bubbleDefaultHeight - textStartheightOffset - textBoarder);
				drawText(text, textPosX, textPosY);

			} else {

				if (totalTextWidth <= textMaxWidth) {
					// x- scaling
					bubbleHeight = bubbleDefaultHeight;
					bubbleWidth = 4 * textBoarder + totalTextWidth;
					drawBubble();

					int textPosX = (int) (bubbleWidth / 2);
					int textPosY = (int) (bubbleDefaultHeight - textStartheightOffset - textBoarder);
					drawText(text, textPosX, textPosY);

				} else if (totalTextWidth > textMaxWidth) {
					// x-scaling maxed & y- scaling

					textSplit();

					bubbleHeight = bubbleDefaultHeight + (textVector.size() - 1) * (textBoarder + textSize);
					bubbleWidth = 4 * textBoarder + textMaxWidth;
					drawBubble();

					int counterT = 0;
					while (counterT < textVector.size()) {
						int textPosX = (int) (bubbleWidth / 2);
						int textPosY = (int) ((bubbleDefaultHeight - textStartheightOffset - textBoarder) + (counterT * (textBoarder + textSize)));
						drawText(textVector.elementAt(counterT), textPosX, textPosY);
						counterT++;

					}

				}
			}
		}

	}
}
