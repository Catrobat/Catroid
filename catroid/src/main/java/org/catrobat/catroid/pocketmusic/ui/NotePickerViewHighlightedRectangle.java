/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.pocketmusic.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import org.catrobat.catroid.R;

import androidx.core.content.ContextCompat;

public class NotePickerViewHighlightedRectangle extends View {
	private static final int LINE_WIDTH = 8;

	private Paint paint;

	private Rect rect;

	private int rectTop = 0;
	private int rectBottom = 0;

	private int numberOfOctaves;
	private ScrollView mainScrollView;

	public NotePickerViewHighlightedRectangle(Context context) {
		super(context);
	}
	public NotePickerViewHighlightedRectangle(Context context, ScrollView mainScrollView,
			int numberOfOctaves) {
		super(context);
		this.mainScrollView = mainScrollView;
		this.numberOfOctaves = numberOfOctaves;

		initPaint();
		initRect();
		setMainScrollViewOnScrollListener();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				scrollMainScrollView(event.getY());
				setRectMeasuresByTouch(event.getY());
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				break;
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		rect.set(getHalfLineWidth(), rectTop, getWidth() - getHalfLineWidth(), rectBottom);
		canvas.drawRect(rect, paint);
	}

	private int getSafePaddedY(float y) {
		int maxY = getHeight() - getRectWidth();

		if (y < getHalfLineWidth()) {
			return getHalfLineWidth();
		}
		if (y > maxY) {
			return maxY;
		}
		return Math.round(y);
	}

	private void initPaint() {
		paint = new Paint();
		paint.setColor(ContextCompat.getColor(getContext(), R.color.note_picker_highlighted_rectangle));
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(LINE_WIDTH);
	}

	private void initRect() {
		int top = getSafePaddedY(0);
		int bottom = top + getRectWidth();

		rect = new Rect(getHalfLineWidth(), top, getWidth() - getHalfLineWidth(), bottom);
	}

	private int getRectWidth() {
		return getHeight() / numberOfOctaves;
	}

	private void setMainScrollViewOnScrollListener() {
		mainScrollView.setOnScrollChangeListener((view, x, y, oldX, oldY) -> {
			float currentProportion = (float) y / mainScrollView.getHeight() / numberOfOctaves;
			setRectMeasuresByMainViewScroll(currentProportion);
			invalidate();
		});
	}

	private void setRectMeasuresByTouch(float y) {
		rectTop = getSafePaddedY(y);
		rectBottom = rectTop + getRectWidth();
	}

	private void setRectMeasuresByMainViewScroll(float proportionOnScrollView) {
		int currentY = (int) (proportionOnScrollView * getHeight());
		rectTop = getSafePaddedY(currentY);
		rectBottom = rectTop + getRectWidth();
	}

	private void scrollMainScrollView(float y) {
		float currentProportion = y / getHeight() * numberOfOctaves;
		int mainScrollViewHeight = mainScrollView.getHeight();
		mainScrollView.scrollTo(0, Math.round(currentProportion * mainScrollViewHeight));
	}

	private int getHalfLineWidth() {
		return LINE_WIDTH / 2;
	}
}
