/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.stage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ShowBubbleActor extends Actor {
	ArrayList<String> bubbleValue;
	private Sprite sprite;
	private int type;
	private Image imageLeft;
	private Image imageRight;
	private Image image;
	private boolean drawRight = true;

	public ShowBubbleActor(String text, Sprite sprite, int type) {
		this.bubbleValue = Utils.formatStringForBubbleBricks(text);
		this.sprite = sprite;
		this.type = type;
		init();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		switchLogic();
		getImageForDraw().draw(batch, parentAlpha);
	}

	private void init() {
		imageRight = new Image(new Texture(drawBubbleOnCanvas(bubbleValue, true)));
		imageLeft = new Image(new Texture(drawBubbleOnCanvas(bubbleValue, false)));
		image = imageRight;
	}

	private Image getImageForDraw() {
		if (drawRight) {
			image.setX(sprite.look.getXInUserInterfaceDimensionUnit() + (sprite.look
					.getWidthInUserInterfaceDimensionUnit() / 2));
		} else {
			image.setX(sprite.look.getXInUserInterfaceDimensionUnit() - sprite.look
					.getWidthInUserInterfaceDimensionUnit() / 2 - image.getWidth());
		}
		image.setY(sprite.look.getYInUserInterfaceDimensionUnit() + (sprite.look
				.getHeightInUserInterfaceDimensionUnit() / 2));
		return image;
	}

	private void switchLogic() {
		if (drawRight && !drawRight()) {
			drawRight = false;
			image = imageLeft;
		}
		if (!drawRight && !drawLeft()) {
			drawRight = true;
			image = imageRight;
		}
	}

	private boolean drawRight() {
		return sprite.look.getX() + sprite.look.getWidthInUserInterfaceDimensionUnit() + image.getWidth()
				< (ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth / 2);
	}

	private boolean drawLeft() {
		return sprite.look.getX() - image.getWidth()
				> -(ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth / 2);
	}

	private Pixmap drawBubbleOnCanvas(ArrayList<String> lines, boolean right) {
		Paint paint = new Paint();
		paint.setTextSize(Constants.TEXT_SIZE_BUBBLE);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(android.graphics.Color.BLACK);
		int width = 0;
		int height = 30;
		int border = Constants.BORDER_THICKNESS_BUBBLES;
		float y = Constants.PADDING_TOP;
		ArrayList<Float> xPositions = new ArrayList<>();
		Rect temp = new Rect();

		//Calculate height and width of textbox plus lineheight
		for (String line : lines) {
			height += Constants.LINE_SPACING_BUBBLES;
			paint.getTextBounds(line, 0, line.length(), temp);
			height += temp.height();
			xPositions.add(paint.measureText(line));
			if (width < temp.width()) {
				width = temp.width();
			}
		}
		width += 55;
		if (width < 148) {
			width = 148;
		}
		float lineHeight = (height - 30) / lines.size();

		//Setup Bitmap and textbox
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		RectF rect = new RectF(0f, 0f, width, height);

		//Draw rounded textbox
		canvas.drawRoundRect(rect, 20, 20, paint);
		rect = new RectF(rect.left + border, rect.top + border, rect.right - border, rect.bottom - border);
		paint.setColor(android.graphics.Color.WHITE);
		canvas.drawRoundRect(rect, 15, 15, paint);
		paint.setColor(Color.BLACK);

		//Calculate x position for every line
		for (int i = 0; i < xPositions.size(); i++) {
			float x = ((float) width - xPositions.get(i)) / 2f;
			xPositions.set(i, x);
		}

		//Draw text in textbox
		int i = 0;
		for (String line : lines) {
			canvas.drawText(line, xPositions.get(i), y, paint);
			y += lineHeight;
			i++;
		}

		//Draw think bubbles or say triangle and convert to pixmap
		return getFinalBubble(width, height, bitmap, right);
	}

	private Pixmap getFinalBubble(int width, int height, Bitmap bitmap, boolean right) {
		Paint paint = new Paint();
		paint.setColor(android.graphics.Color.BLACK);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth(2);
		Bitmap tempBitmap = Bitmap.createBitmap(width, height + Constants.OFFSET_FOR_THINK_BUBBLES_AND_ARROW, Bitmap.Config.ARGB_8888);
		Canvas tempCanvas = new Canvas(tempBitmap);
		tempCanvas.drawBitmap(bitmap, 0, 0, null);

		if (type == Constants.SAY_BRICK) {
			tempCanvas.drawPath(getSayTrianglePath(tempBitmap.getHeight(), tempBitmap.getWidth(), right), paint);
			paint.setColor(android.graphics.Color.WHITE);
			tempCanvas.drawPath(getSayTrianglePathSmaller(tempBitmap.getHeight(), tempBitmap.getWidth(), right), paint);
		} else {
			Bitmap thinkBubbles = getThinkBubbles(right);
			int startPos = right ? 0 : tempBitmap.getWidth() - 2 * Constants.OFFSET_FOR_THINK_BUBBLES_AND_ARROW;
			tempCanvas.drawBitmap(thinkBubbles, startPos, bitmap.getHeight(), null);
		}

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		tempBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] bytes = stream.toByteArray();
		return new Pixmap(bytes, 0, bytes.length);
	}

	private Path getSayTrianglePath(int bitmapHeight, int bitmapWidth, boolean right) {
		int offset = right ? 0 : bitmapWidth;
		Path path = new Path();
		path.setFillType(Path.FillType.EVEN_ODD);
		path.moveTo(offset, bitmapHeight);
		path.lineTo(Math.abs(offset - 28), bitmapHeight - Constants.OFFSET_FOR_THINK_BUBBLES_AND_ARROW);
		path.lineTo(Math.abs(offset - 118), bitmapHeight - Constants.OFFSET_FOR_THINK_BUBBLES_AND_ARROW);
		path.lineTo(offset, bitmapHeight);
		path.close();
		return path;
	}

	private Path getSayTrianglePathSmaller(int bitmapHeight, int bitmapWidth, boolean right) {
		int offset = right ? 0 : bitmapWidth;
		Path path = new Path();
		path.moveTo(Math.abs(offset - 12), bitmapHeight - 9);
		path.lineTo(Math.abs(offset - 37), bitmapHeight - Constants.OFFSET_FOR_THINK_BUBBLES_AND_ARROW - 5);
		path.lineTo(Math.abs(offset - 116), bitmapHeight - Constants.OFFSET_FOR_THINK_BUBBLES_AND_ARROW - 5);
		path.lineTo(Math.abs(offset - 12), bitmapHeight - 9);
		path.close();
		return path;
	}

	private Bitmap getThinkBubbles(boolean right) {
		Bitmap bitmap = Bitmap.createBitmap(Constants.OFFSET_FOR_THINK_BUBBLES_AND_ARROW + 30, Constants.OFFSET_FOR_THINK_BUBBLES_AND_ARROW, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		int length = right ? bitmap.getWidth() : 0;
		for (int i = 0; i <= 3; i++) {
			paint.setColor(android.graphics.Color.BLACK);
			canvas.drawCircle(Math.abs(length - i * 15 - 10), i * 10, 10 - i, paint);
			paint.setColor(android.graphics.Color.WHITE);
			canvas.drawCircle(Math.abs(length - i * 15 - 10), i * 10, 7 - i, paint);
		}

		return bitmap;
	}
}
