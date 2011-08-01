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

public class SpeechBubble implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG_DRAW = true;
	private String speechBubbleText = "";
	// TODO: load NinePatchDrawable with ID
	private int speechBubblePictureID = 0;

	private Point position = new Point(0, 0);
	private float speechBubblePicHeight = 0;
	private float speechBubblePicWidth = 0;
	private float speechBubblePicDefaultHeight = 0;
	private float speechBubblePicDefaultWidth = 0;
	private float speechBubblePicMaxWidth = 0;

	NinePatchDrawable bubble9Patch = null;
	private float textOffsetLeft = 10;
	private float textOffsetRight = 10;
	private float textOffsetTop = 5;
	private float textOffsetBottom = 30;
	private Paint textPaint;
	private Paint debugPaint;
	private float textMaxWidth = 100;
	private float pinPointWidth = 20;
	private float textSize;

	private Vector<String> textGrid = new Vector<String>();

	// TODO: eliminate this 3 members

	Activity activity;
	Canvas canvas;
	Costume costume;

	public SpeechBubble() {
		textPaint = new Paint();
		debugPaint = new Paint();
		debugPaint.setColor(Color.RED);
		textSize = 20;
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(textSize);
		textPaint.setFakeBoldText(true);
		textPaint.setTypeface(Typeface.SANS_SERIF);
		textPaint.setTextAlign(Align.CENTER);
	}

	public synchronized void setSpeechBubble(String speechBubbleText, int speechBubblePictureID) {
		this.speechBubbleText = speechBubbleText;
		this.speechBubblePictureID = speechBubblePictureID;
		updateTextGrid();
		updateBubbleScaling();
	}

	public synchronized void draw(Canvas canvas, Costume costume, Activity activity) {
		this.canvas = canvas;
		this.costume = costume;
		bubble9Patch = (NinePatchDrawable) activity.getResources().getDrawable(R.drawable.speech_bubble);
		speechBubblePicDefaultHeight = bubble9Patch.getIntrinsicHeight();
		speechBubblePicDefaultWidth = bubble9Patch.getIntrinsicWidth();
		textMaxWidth = speechBubblePicDefaultWidth - textOffsetLeft - textOffsetRight;
		updateTextGrid();
		updateBubbleScaling();
		calculateDrawPosition(costume);
		drawSpeechBubble(costume, canvas, activity);
		drawTextGrid();
	}

	private void calculateDrawPosition(Costume costume) {
		Bitmap costumeBitmap = costume.getBitmap();
		int costumeHeight = costumeBitmap.getHeight();
		int costumeWidth = costumeBitmap.getWidth();
		int costumeX = costume.getDrawPositionX();
		int costumeY = costume.getDrawPositionY();
		Point boarderPoint = new Point();
		Point innerPoint = new Point();
		double gradient = 0;
		double offset = 0;
		double costumeCenterPointX = costumeWidth / 2;
		double costumeCenterPointY = costumeHeight / 2;

		gradient = -(Math.sqrt(3) / 2) / (0.5);
		offset = costumeCenterPointY;

		boarderPoint.x = (int) (((boarderPoint.y - offset) / gradient) + costumeCenterPointX);

		Point currentPoint = new Point();

		for (currentPoint.x = boarderPoint.x; currentPoint.x >= costumeCenterPointX; currentPoint.x--) {
			currentPoint.y = (int) (((currentPoint.x - costumeCenterPointX) * gradient) + offset);
			Log.v("Point", "(" + currentPoint.x + "," + currentPoint.y + ")");
			if (currentPoint.x <= costumeWidth && currentPoint.y <= costumeHeight) {
				if (costumeBitmap.getPixel(currentPoint.x, currentPoint.y) != 0) {
					innerPoint.x = currentPoint.x;
					innerPoint.y = currentPoint.y;
					break;
				}
			}
		}

		int relPosX = (int) (innerPoint.x + pinPointWidth);
		int relPosY = (int) (((relPosX - costumeCenterPointX) * gradient) + offset);
		position.x = relPosX + costumeX;
		position.y = relPosY + costumeY;

		if (DEBUG_DRAW) {
			Point topLeft = new Point(costumeX, costumeY);
			Point topRight = new Point(costumeX + costumeWidth, costumeY);
			Point bottomLeft = new Point(costumeX, costumeY + costumeHeight);
			Point bottomRight = new Point(costumeX + costumeWidth, costumeY + costumeHeight);
			drawDebugPoint(bottomLeft);
			drawDebugPoint(bottomRight);
			drawDebugPoint(topLeft);
			drawDebugPoint(topRight);
			drawDebugLine(bottomLeft, bottomRight);
			drawDebugLine(bottomLeft, topLeft);
			drawDebugLine(topRight, topLeft);
			drawDebugLine(topRight, bottomRight);
			Point realBoarderPoint = new Point(boarderPoint.x + costumeX, boarderPoint.y + costumeY);
			Point alphaBoardPoint = new Point(innerPoint.x + costumeX, innerPoint.y + costumeY);
			Point costumeCenter = new Point(costumeX + (costumeWidth / 2), costumeY + (costumeHeight / 2));
			drawDebugPoint(realBoarderPoint);
			drawDebugPoint(alphaBoardPoint);
			drawDebugPoint(costumeCenter);
			drawDebugLine(realBoarderPoint, costumeCenter);
		}
	}

	private void drawTextGrid() {
		int counterT = 0;
		while (counterT < textGrid.size()) {
			int textPosX = (int) (speechBubblePicWidth / 2);
			int textPosY = (int) ((-speechBubblePicHeight + textOffsetBottom + textOffsetTop) + (counterT * (textOffsetTop + textSize)));
			canvas.drawText(textGrid.elementAt(counterT), position.x + textPosX, position.y + textPosY, textPaint);

			if (DEBUG_DRAW) {
				int textWidth = calcTextWidth(textGrid.elementAt(counterT));
				int textHeight = (int) textSize;
				Point bottomLeft = new Point(position.x + textPosX - textWidth / 2, position.y + textPosY);
				Point bottomRight = new Point(position.x + textPosX + textWidth / 2, position.y + textPosY);
				Point topLeft = new Point(position.x + textPosX - textWidth / 2, position.y + textPosY - textHeight);
				Point topRight = new Point(position.x + textPosX + textWidth / 2, position.y + textPosY - textHeight);
				drawDebugLine(bottomLeft, bottomRight);
				drawDebugLine(bottomLeft, topLeft);
				drawDebugLine(topRight, topLeft);
				drawDebugLine(topRight, bottomRight);
			}
			counterT++;

		}

	}

	private void drawSpeechBubble(Costume costume, Canvas canvas, Activity activity2) {

		int boundLeft = position.x;
		int boundTop = (int) (position.y - speechBubblePicHeight);
		int boundRight = boundLeft + (int) speechBubblePicWidth;
		int boundBottom = boundTop + (int) speechBubblePicHeight;
		bubble9Patch.setBounds(boundLeft, boundTop, boundRight, boundBottom);
		bubble9Patch.draw(canvas);

		if (DEBUG_DRAW) {
			Point bottomLeft = new Point(position.x, position.y);
			Point bottomRight = new Point((int) (position.x + speechBubblePicWidth), position.y);
			Point topLeft = new Point(position.x, (int) (position.y - speechBubblePicHeight));
			Point topRight = new Point((int) (position.x + speechBubblePicWidth),
					(int) (position.y - speechBubblePicHeight));
			drawDebugPoint(bottomLeft);
			drawDebugPoint(bottomRight);
			drawDebugPoint(topLeft);
			drawDebugPoint(topRight);
			drawDebugLine(bottomLeft, bottomRight);
			drawDebugLine(bottomLeft, topLeft);
			drawDebugLine(topRight, topLeft);
			drawDebugLine(topRight, bottomRight);

			Point innerbottomLeft = new Point((int) (position.x + textOffsetLeft),
					(int) (position.y - textOffsetBottom));
			Point innerbottomRight = new Point((int) (position.x + speechBubblePicWidth - textOffsetRight),
					(int) (position.y - textOffsetBottom));
			Point innertopLeft = new Point((int) (position.x + textOffsetLeft), (int) (position.y
					- speechBubblePicHeight + textOffsetTop));
			Point innertopRight = new Point((int) (position.x + speechBubblePicWidth - textOffsetRight),
					(int) (position.y - speechBubblePicHeight + textOffsetTop));

			drawDebugLine(innerbottomLeft, innerbottomRight);
			drawDebugLine(innerbottomLeft, innertopLeft);
			drawDebugLine(innertopRight, innertopLeft);
			drawDebugLine(innertopRight, innerbottomRight);
		}

	}

	// TODO: add variable bubblewidth
	private void updateTextGrid() {
		textGrid.clear();

		String entireText = speechBubbleText.trim();
		Vector<String> wordVector = new Vector<String>();

		String wordBuffer = "";
		for (int charIndex = 0; charIndex < entireText.length(); charIndex++) {
			wordBuffer += entireText.charAt(charIndex);
			if (entireText.charAt(charIndex) == ' ') {
				wordVector.add(wordBuffer);
				wordBuffer = "";
			}
		}
		wordVector.add(wordBuffer);

		String oldTextBuffer = "";
		String newTextBuffer = "";
		String newWord = "";
		String splitWordBuffer = "";
		for (int wordIndex = 0; wordIndex < wordVector.size(); wordIndex++) {

			newTextBuffer = oldTextBuffer + wordVector.get(wordIndex);

			if (calcTextWidth(newTextBuffer) > textMaxWidth) {
				if (oldTextBuffer.length() != 0) {
					textGrid.add(oldTextBuffer);
					Log.v("TextSplit|add", oldTextBuffer);
				}
				newWord = wordVector.get(wordIndex);
				if (calcTextWidth(newWord) > textMaxWidth) {

					splitWordBuffer = "";
					for (int charIndex = 0; charIndex < newWord.length(); charIndex++) {
						splitWordBuffer += newWord.charAt(charIndex);
						if (calcTextWidth(splitWordBuffer) > textMaxWidth) {
							textGrid.add(splitWordBuffer.substring(0, splitWordBuffer.length() - 1));
							Log.v("TextSplit|add2", splitWordBuffer.substring(0, splitWordBuffer.length() - 1));
							splitWordBuffer = "" + splitWordBuffer.charAt(splitWordBuffer.length() - 1);
						}
					}

					oldTextBuffer = splitWordBuffer;
				} else {
					oldTextBuffer = newWord;
				}

			} else {
				oldTextBuffer = newTextBuffer;
			}
		}

		if (oldTextBuffer.length() != 0) {
			textGrid.add(oldTextBuffer);
			Log.v("TextSplit|add3", oldTextBuffer);
		}

		Log.v("TextSplit", textGrid.toString());
	}

	// TODO: add variable bubblewidth
	private void updateBubbleScaling() {
		int numberOfCharacters = speechBubbleText.trim().length();

		if (numberOfCharacters != 0) {

			speechBubblePicHeight = speechBubblePicDefaultHeight + (textGrid.size() - 1) * (textOffsetTop + textSize);

			if (calcTextWidth(speechBubbleText.trim()) <= speechBubblePicWidth - textOffsetLeft - textOffsetRight) {
				speechBubblePicWidth = speechBubblePicDefaultWidth;
			} else {
				speechBubblePicWidth = textOffsetLeft + textOffsetRight + textMaxWidth;
			}
		}
	}

	private int calcTextWidth(String text) {
		int lengthOfText = 0;
		if (text.length() > 0) {
			Rect textBounds = new Rect();
			textPaint.getTextBounds(text, 0, text.length(), textBounds);
			lengthOfText = textBounds.width();
		}
		return lengthOfText;

	}

	private void drawDebugPoint(Point a) {
		canvas.drawCircle(a.x, a.y, 2, debugPaint);
	}

	private void drawDebugLine(Point a, Point b) {
		canvas.drawLine(a.x, a.y, b.x, b.y, debugPaint);

	}

}
