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
import at.tugraz.ist.catroid.common.Values;

public class SpeechBubble implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG_DRAW = true;
	private String speechBubbleText = "";
	// TODO: add Bricks
	// TODO: load NinePatchDrawable with ID
	//private int speechBubblePictureID = 0;

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
	//private float pinPointWidth = 20;
	private float textSize;
	private Point pinPointRight = new Point(0, 0);
	private Point pinPointLeft = new Point(0, 0);

	private Vector<String> textGrid = new Vector<String>();

	// TODO: eliminate this 3 members

	Activity activity;
	Canvas canvas;
	Costume costume;

	// BUGS:
	// TODO: first BubbleDraw scalingbug
	// TODO: moving Bug (synch problem)
	// TODO: sameText think/say -> different textspace

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
		//this.speechBubblePictureID = speechBubblePictureID;
	}

	public synchronized void draw(Canvas canvas, Costume costume, Activity activity) {
		if (speechBubbleText.trim().length() != 0) {
			this.canvas = canvas;
			this.costume = costume;
			this.activity = activity;
			bubble9Patch = (NinePatchDrawable) activity.getResources().getDrawable(R.drawable.speech_bubble);
			speechBubblePicDefaultHeight = bubble9Patch.getIntrinsicHeight();
			speechBubblePicDefaultWidth = bubble9Patch.getIntrinsicWidth();
			speechBubblePicMaxWidth = 2 * speechBubblePicDefaultWidth;
			updateTextGrid();
			updateBubbleScaling();
			calculatePinPoints(costume);
			calculateDrawPosition();
			drawSpeechBubble(costume, canvas, activity);
			drawTextGrid();
		}
	}

	private void calculateDrawPosition() {

		if (pinPointRight.x + speechBubblePicWidth > Values.SCREEN_WIDTH) {
			bubble9Patch = (NinePatchDrawable) activity.getResources().getDrawable(R.drawable.mind_bubble_inv);
			position.x = (int) (pinPointLeft.x - speechBubblePicWidth);
			position.y = pinPointLeft.y;
			if (position.y - speechBubblePicHeight < 0) {
				position.y = (int) speechBubblePicHeight;
			}
		} else {
			bubble9Patch = (NinePatchDrawable) activity.getResources().getDrawable(R.drawable.speech_bubble);
			position.x = pinPointRight.x;
			position.y = pinPointRight.y;
			if (position.y - speechBubblePicHeight < 0) {
				position.y = (int) speechBubblePicHeight;
			}
		}

	}

	private void calculatePinPoints(Costume costume) {
		Bitmap costumeBitmap = costume.getBitmap();
		int costumeHeight = costumeBitmap.getHeight();
		int costumeWidth = costumeBitmap.getWidth();
		int costumePosX = costume.getDrawPositionX();
		int costumePosY = costume.getDrawPositionY();
		double costumeCenterPointX = costumeWidth / 2;
		double costumeCenterPointY = costumeHeight / 2;

		if (DEBUG_DRAW) {
			Point topLeft = new Point(costumePosX, costumePosY);
			Point topRight = new Point(costumePosX + costumeWidth, costumePosY);
			Point bottomLeft = new Point(costumePosX, costumePosY + costumeHeight);
			Point bottomRight = new Point(costumePosX + costumeWidth, costumePosY + costumeHeight);
			drawDebugPoint(bottomLeft);
			drawDebugPoint(bottomRight);
			drawDebugPoint(topLeft);
			drawDebugPoint(topRight);
			drawDebugLine(bottomLeft, bottomRight);
			drawDebugLine(bottomLeft, topLeft);
			drawDebugLine(topRight, topLeft);
			drawDebugLine(topRight, bottomRight);
			Point costumeCenter = new Point(costumePosX + (costumeWidth / 2), costumePosY + (costumeHeight / 2));
			drawDebugPoint(costumeCenter);
		}

		Point boarderPointRight = new Point();
		Point innerPointRight = new Point();
		double gradientRight = 0;
		double offsetRight = 0;
		gradientRight = -(Math.sqrt(3) / 2) / (0.5);
		offsetRight = costumeCenterPointY;
		boarderPointRight.x = (int) (((boarderPointRight.y - offsetRight) / gradientRight) + costumeCenterPointX);

		Point currentPointRight = new Point();
		for (currentPointRight.x = boarderPointRight.x; currentPointRight.x >= costumeCenterPointX; currentPointRight.x--) {
			currentPointRight.y = (int) (((currentPointRight.x - costumeCenterPointX) * gradientRight) + offsetRight);
			if (currentPointRight.x <= costumeWidth && currentPointRight.y <= costumeHeight) {
				if (costumeBitmap.getPixel(currentPointRight.x, currentPointRight.y) != 0) {
					innerPointRight.x = currentPointRight.x;
					innerPointRight.y = currentPointRight.y;
					break;
				}
			}
		}

		//int rightRelPosX = (int) (innerPointRight.x + pinPointWidth);
		//int rightRelPosY = (int) (((rightRelPosX - costumeCenterPointX) * gradientRight) + offsetRight);
		//pinPointRight.x = rightRelPosX + costumePosX;
		//pinPointRight.y = rightRelPosY + costumePosY;
		pinPointRight.x = boarderPointRight.x + costumePosX;
		pinPointRight.y = boarderPointRight.y + costumePosY;

		if (DEBUG_DRAW) {
			Point rightRealBoarderPoint = new Point(boarderPointRight.x + costumePosX, boarderPointRight.y
					+ costumePosY);
			Point rightAlphaBoardPoint = new Point(innerPointRight.x + costumePosX, innerPointRight.y + costumePosY);
			drawDebugPoint(rightRealBoarderPoint);
			drawDebugPoint(rightAlphaBoardPoint);
			Point costumeCenter = new Point(costumePosX + (costumeWidth / 2), costumePosY + (costumeHeight / 2));
			drawDebugPoint(costumeCenter);
			drawDebugLine(rightRealBoarderPoint, costumeCenter);
		}

		Point boarderPointLeft = new Point(0, 0);
		Point innerPointLeft = new Point(0, 0);
		double gradientLeft = 0;
		double offsetLeft = 0;
		gradientLeft = (Math.sqrt(3) / 2) / (0.5);
		offsetLeft = costumeCenterPointY;
		boarderPointLeft.x = (int) (((boarderPointLeft.y - offsetLeft) / gradientLeft) + costumeCenterPointX);

		Point currentPointLeft = new Point();

		for (currentPointLeft.x = boarderPointLeft.x; currentPointLeft.x <= costumeCenterPointX; currentPointLeft.x++) {
			currentPointLeft.y = (int) (((currentPointLeft.x - costumeCenterPointX) * gradientLeft) + offsetLeft);
			if (currentPointLeft.x <= costumeWidth && currentPointLeft.y <= costumeHeight && currentPointLeft.y > 0) {
				Log.v("POINT", "(" + currentPointLeft.x + "|" + currentPointLeft.y + ")");
				if (costumeBitmap.getPixel(currentPointLeft.x, currentPointLeft.y) != 0) {
					innerPointLeft.x = currentPointLeft.x;
					innerPointLeft.y = currentPointLeft.y;
					break;
				}
			}
		}

		//int leftRelPosX = (int) (innerPointRight.x + pinPointWidth);
		//int leftRelPosY = (int) (((leftRelPosX - costumeCenterPointX) * gradientLeft) + offsetLeft);

		//pinPointLeft.x = leftRelPosX + costumePosX;
		//pinPointLeft.y = leftRelPosY + costumePosY;
		pinPointLeft.x = boarderPointLeft.x + costumePosX;
		pinPointLeft.y = boarderPointLeft.y + costumePosY;

		if (DEBUG_DRAW) {

			Point leftRealBoarderPoint = new Point(boarderPointLeft.x + costumePosX, boarderPointLeft.y + costumePosY);
			Point leftAlphaBoardPoint = new Point(innerPointLeft.x + costumePosX, innerPointLeft.y + costumePosY);
			Point costumeCenter = new Point(costumePosX + (costumeWidth / 2), costumePosY + (costumeHeight / 2));

			drawDebugPoint(leftRealBoarderPoint);
			drawDebugPoint(leftAlphaBoardPoint);
			drawDebugPoint(costumeCenter);
			drawDebugPoint(new Point(costume.getDrawPositionX(), costume.getDrawPositionY()));
			drawDebugLine(leftRealBoarderPoint, costumeCenter);
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

	private void updateTextGrid() {
		textGrid.clear();

		String entireText = speechBubbleText.trim();
		Vector<String> wordVector = new Vector<String>();

		float textfieldMaxWidth = speechBubblePicMaxWidth - textOffsetLeft - textOffsetRight;
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

			if (calcTextWidth(newTextBuffer) > textfieldMaxWidth) {
				if (oldTextBuffer.length() != 0) {
					textGrid.add(oldTextBuffer);
				}
				newWord = wordVector.get(wordIndex);
				if (calcTextWidth(newWord) > textfieldMaxWidth) {

					splitWordBuffer = "";
					for (int charIndex = 0; charIndex < newWord.length(); charIndex++) {
						splitWordBuffer += newWord.charAt(charIndex);
						if (calcTextWidth(splitWordBuffer) > textfieldMaxWidth) {
							textGrid.add(splitWordBuffer.substring(0, splitWordBuffer.length() - 1));
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

	private void updateBubbleScaling() {

		speechBubblePicHeight = speechBubblePicDefaultHeight + (textGrid.size() - 1) * (textOffsetTop + textSize);

		if (textGrid.size() > 1) {
			speechBubblePicWidth = speechBubblePicMaxWidth;
		} else if (textGrid.size() == 1) {
			String textline = textGrid.elementAt(0);
			int textlineLength = calcTextWidth(textline);
			if (textlineLength <= speechBubblePicWidth - textOffsetLeft - textOffsetRight) {
				speechBubblePicWidth = speechBubblePicDefaultWidth;
			} else {
				speechBubblePicWidth = textlineLength + textOffsetLeft + textOffsetRight;
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
