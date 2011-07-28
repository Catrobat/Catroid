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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.NinePatchDrawable;
import android.util.Log;
import at.tugraz.ist.catroid.R;

public class Bubble implements Serializable {
	//private static final long serialVersionUID = 1L;

	private Activity activity;

	private String text;
	private Vector<String> textVector;
	private Paint textPaint;
	private Paint debugPaint;
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

	Point leftStart;
	Point rightStart;
	private Canvas canvas;

	public Bubble() {
		text = "";
	}

	public void init() {
		debugPaint = new Paint();
		debugPaint.setColor(Color.RED);

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

	public void drawDebugPoint(float x, float y) {
		canvas.drawCircle(x, y, 3, debugPaint);
	}

	public void calculateStartPos() {

		Bitmap temp = null;
		double costumeHeight = costume.getBitmap().getHeight();
		double costumeWidth = costume.getBitmap().getWidth();
		leftStart = new Point(0, 0);
		rightStart = new Point(0, 0);
		double yq, xq, k, d;
		yq = 0;
		k = -3.46;
		d = costumeHeight / 2;
		double centerPointX = costumeWidth / 2;
		double centerPointY = costumeHeight / 2;

		xq = ((yq - d) / k) + (costumeWidth / 2);

		double x = 0, y = 0, x_1 = centerPointX, x_2 = xq, y_1 = centerPointY, y_2 = 0;
		boolean pointNotFound = true;
		x = xq;
		temp = costume.getBitmap();
		drawDebugPoint((float) (costume.getDrawPositionX() + x), costume.getDrawPositionY());
		int tempColor;
		while (pointNotFound) {
			y = y_1 + ((y_2 - y_1) / (x_2 - x_1)) * (x - x_1);

			if (x <= temp.getWidth() && y <= temp.getHeight()) {
				tempColor = temp.getPixel((int) x, (int) y);
				Log.v("COLOR", "::" + tempColor);
				if (tempColor == 0) {
					x--;
				} else {
					pointNotFound = false;
				}
			}

		}

		rightStart.x = (int) x + 10;
		rightStart.y = (int) (y_1 + ((y_2 - y_1) / (x_2 - x_1)) * ((rightStart.x - x_1)));
		int i = 76655;
		//		leftStart.x = (int) (x - (xq - x));
		//		leftStart.y = (int) y;

	}

	public void drawBubble() {
		calculateStartPos();
		int costumePosX = costume.getDrawPositionX();
		int costumePosY = costume.getDrawPositionY();
		int relPosX = rightStart.x;
		int relPosY = rightStart.y;

		//double costumeHeight = costume.getBitmap().getHeight();
		double costumeWidth = costume.getBitmap().getWidth();

		int boundLeft = costumePosX + relPosX;
		int boundTop = costumePosY - (int) bubbleHeight + relPosY;
		int boundRight = boundLeft + (int) bubbleWidth;
		int boundBottom = boundTop + (int) bubbleHeight;

		bubble9Patch.setBounds(boundLeft, boundTop, boundRight, boundBottom);
		bubble9Patch.draw(canvas);
	}

	public void drawText(String string, int textPosX, int textPosY) {
		int relPosY = rightStart.y;
		int costumePosX = costume.getDrawPositionX();
		int costumePosY = costume.getDrawPositionY();

		//double costumeHeight = costume.getBitmap().getHeight();
		double costumeWidth = rightStart.x;//costume.getBitmap().getWidth();

		int boundLeft = costumePosX + (int) costumeWidth;
		int boundTop = costumePosY - (int) bubbleHeight + relPosY;
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

		String oldTextBuffer = "";
		String newTextBuffer = "";
		String tempString = "";

		try {
			for (int i = 1; i <= words.size(); i++) {
				//nextBuffer = buildBuffer;
				newTextBuffer = oldTextBuffer + words.get(i - 1);

				if (getTextWidth(newTextBuffer) > textMaxWidth) {
					if (oldTextBuffer.length() != 0) {
						textVector.add(oldTextBuffer);

					}
					tempString = words.get(i - 1);
					if (getTextWidth(tempString) > textMaxWidth) {

						tempBuffer = "" + tempString.charAt(0);
						for (int k = 2; k <= tempString.length(); k++) {
							tempBuffer += tempString.charAt(k - 1);
							if (getTextWidth(tempBuffer) > textMaxWidth) { // !!!!!!
								writeBuffer = tempBuffer.substring(0, tempBuffer.length() - 1);
								tempBuffer = "" + tempBuffer.charAt(k - 1);
								textVector.add(writeBuffer);
							}
						}

						oldTextBuffer = tempBuffer;
					} else {
						oldTextBuffer = tempString;
					}

				} else {
					oldTextBuffer = newTextBuffer;
				}
			}
		} catch (Exception e) {

			int ie = 4;
			e.printStackTrace();
		}
		if (oldTextBuffer.length() != 0) {
			textVector.add(oldTextBuffer);
		}
		int i = 4;
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
