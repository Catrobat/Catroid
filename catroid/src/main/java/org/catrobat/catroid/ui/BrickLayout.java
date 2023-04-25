/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.catrobat.catroid.R;

import java.util.LinkedList;

/**
 * Author: Romain Guy
 * <p/>
 * Using example: <?xml version="4.0" encoding="utf-8"?> <com.example.android.layout.FlowLayout
 * xmlns:f="http://schemas.android.com/apk/res/org.apmem.android"
 * xmlns:android="http://schemas.android.com/apk/res/android" f:horizontalSpacing="6dip" f:verticalSpacing="12dip"
 * android:layout_width="wrap_content" android:layout_height="wrap_content" android:paddingLeft="6dip"
 * android:paddingTop="6dip" android:paddingRight="12dip"> <Button android:layout_width="wrap_content"
 * android:layout_height="wrap_content" f:layout_horizontalSpacing="32dip" f:layout_breakLine="true"
 * android:text="Cancel" />
 * <p/>
 * </com.example.android.layout.FlowLayout>
 */

public class BrickLayout extends ViewGroup {
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	private final int minTextFieldWidthDp = 100;
	private final int linesToAllocate = 10;
	private final int elementsToAllocatePerLine = 10;

	private int layoutDirection = 1;
	private int horizontalSpacing = 0;
	private int verticalSpacing = 0;
	private int orientation = 0;
	protected boolean debugDraw = true;
	protected boolean userBrick = false;
	protected boolean usePng = false;

	protected LinkedList<LineData> lines;

	public BrickLayout(Context context) {
		super(context);
		setLayoutDirection(context);
		allocateLineData();
		this.readStyleParameters(context, null);
	}

	public BrickLayout(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		setLayoutDirection(context);
		allocateLineData();
		this.readStyleParameters(context, attributeSet);
	}

	public BrickLayout(Context context, AttributeSet attributeSet, int defStyle) {
		super(context, attributeSet, defStyle);
		setLayoutDirection(context);
		allocateLineData();
		this.readStyleParameters(context, attributeSet);
	}

	protected void allocateLineData() {
		lines = new LinkedList<>();
		for (int i = 0; i < linesToAllocate; i++) {
			allocateNewLine();
		}
	}

	private void setLayoutDirection(Context context) {
		if (context.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
			layoutDirection = -1;
		}
	}

	protected LineData allocateNewLine() {
		LineData lineData = new LineData();
		for (int i = 0; i < elementsToAllocatePerLine; i++) {
			lineData.elements.add(new ElementData(null, 0, 0, 0, 0));
		}
		lines.add(lineData);
		return lineData;
	}

	public int getPaddingLeft(int widthMeasureSpec) {
		if (!usePng) {
			return super.getPaddingLeft() + (int) (MeasureSpec.getSize(widthMeasureSpec) * 0.14);
		} else {
			return super.getPaddingLeft();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec) - this.getPaddingRight() - this.getPaddingLeft(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

		int lineHeightWithVerticalSpacing = 0;
		int lineHeight = 0;
		int lineLengthWithHorizontalSpacing = 0;
		int lineLength = 0;

		int prevLinePosition = 0;

		int controlMaxLength = 0;
		int controlMaxHeight = 0;

		for (LineData lineData : lines) {
			lineData.allowableTextFieldWidth = 0;
			lineData.height = 0;
			lineData.numberOfTextFields = 0;
			lineData.totalTextFieldWidth = 0;
			for (ElementData elementData : lineData.elements) {
				elementData.height = 0;
				elementData.width = 0;
				elementData.posY = 0;
				elementData.posX = 0;
				elementData.view = null;
			}
		}

		LineData currentLine = lines.getFirst();

		// ************************ BEGIN PRE-LAYOUT (decide on a maximum width for text fields) ************************
		// 1. adding text to a text field never causes a line break
		// 2. text fields use as much space as possible
		// 3. on wider screens, line breaks are removed entirely and the layout is one line

		final int count = getChildCount();
		int elementInLineIndex = 0;

		int totalLengthOfContent = 0;
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == GONE) {
				continue;
			}

			int childSpacing = getHorizontalSpacing((LayoutParams) child.getLayoutParams());

			totalLengthOfContent += horizontalSpacing + childSpacing
					+ preLayoutMeasureWidth(child, sizeWidth, sizeHeight, modeWidth, modeHeight);
		}

		int childSpacing = 0;
		int combinedLengthOfPreviousLines = 0;
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == GONE) {
				continue;
			}

			LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
			childSpacing = getHorizontalSpacing(layoutParams);
			int childWidth;

			if (userBrick) {
				childWidth = child.getMeasuredWidth();
			} else {
				childWidth = preLayoutMeasureWidth(child, sizeWidth, sizeHeight, modeWidth, modeHeight);
			}

			childWidth += childSpacing;
			lineLength = lineLengthWithHorizontalSpacing + childWidth;
			lineLengthWithHorizontalSpacing = lineLength + horizontalSpacing;

			boolean newLine;
			if (userBrick) {
				newLine = lineLengthWithHorizontalSpacing >= sizeWidth;
			} else {
				newLine = layoutParams.newLine && (totalLengthOfContent - combinedLengthOfPreviousLines) > sizeWidth;
			}

			newLine = newLine || layoutParams.forceNewLine;

			boolean lastChildWasSpinner = false;
			if (i > 0) {
				lastChildWasSpinner = getChildAt(i - 1) instanceof Spinner;
			}

			newLine = newLine || child instanceof Spinner || lastChildWasSpinner;

			if (newLine) {
				int endingWidthOfLineMinusFields = (lineLength - (childWidth + horizontalSpacing + currentLine.totalTextFieldWidth));
				float allowableWidth = (float) (sizeWidth - (endingWidthOfLineMinusFields)) / currentLine.numberOfTextFields;
				currentLine.allowableTextFieldWidth = (int) Math.floor(allowableWidth) - childSpacing;

				currentLine = getNextLine(currentLine);

				combinedLengthOfPreviousLines += (lineLength - (childWidth + horizontalSpacing));
				lineLength = childWidth;
				lineLengthWithHorizontalSpacing = lineLength + horizontalSpacing;

				elementInLineIndex = 0;
			}

			getElement(currentLine, elementInLineIndex).view = child;
			elementInLineIndex++;

			if (layoutParams.editText) {
				currentLine.totalTextFieldWidth += childWidth;
				currentLine.numberOfTextFields++;
			}
		}

		int endingWidthOfLineMinusFields = (lineLength - currentLine.totalTextFieldWidth);
		float allowableWidth = (float) (sizeWidth - endingWidthOfLineMinusFields) / currentLine.numberOfTextFields;
		currentLine.allowableTextFieldWidth = (int) Math.floor(allowableWidth) - childSpacing;

		for (LineData lineData : lines) {
			for (ElementData elementData : lineData.elements) {
				if (elementData.view != null) {
					LayoutParams layoutParams = (LayoutParams) elementData.view.getLayoutParams();
					if (layoutParams.editText && lineData.allowableTextFieldWidth > 0) {
						((TextView) elementData.view).setMaxWidth(lineData.allowableTextFieldWidth);
					}
				}
			}
		}

		// ************************ BEGIN LAYOUT ************************
		lineLengthWithHorizontalSpacing = 0;

		currentLine = lines.getFirst();

		boolean firstLine = true;
		for (LineData line : lines) {
			boolean newLine = !firstLine;
			for (ElementData element : line.elements) {
				View child = element.view;
				if (child == null || child.getVisibility() == GONE) {
					continue;
				}

				if (child instanceof Spinner) {
					child.measure(MeasureSpec.makeMeasureSpec(sizeWidth, MeasureSpec.EXACTLY), MeasureSpec
							.makeMeasureSpec(sizeHeight, modeHeight == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST
									: modeHeight));
				} else {
					child.measure(MeasureSpec.makeMeasureSpec(sizeWidth,
							modeWidth == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : modeWidth), MeasureSpec
							.makeMeasureSpec(sizeHeight, modeHeight == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST
									: modeHeight));
				}

				LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

				int horizontalSpacing = this.getHorizontalSpacing(layoutParams);
				int verticalSpacing = this.getVerticalSpacing(layoutParams);

				int childWidth = child.getMeasuredWidth();
				int childHeight = child.getMeasuredHeight();

				lineLength = lineLengthWithHorizontalSpacing + childWidth;
				lineLengthWithHorizontalSpacing = lineLength + horizontalSpacing;

				if (newLine) {
					newLine = false;
					prevLinePosition = prevLinePosition + lineHeightWithVerticalSpacing;

					currentLine = getNextLine(currentLine);

					lineHeight = childHeight;
					lineLength = childWidth;
					lineHeightWithVerticalSpacing = childHeight + verticalSpacing;
					lineLengthWithHorizontalSpacing = lineLength + horizontalSpacing;
				}

				lineHeightWithVerticalSpacing = Math.max(lineHeightWithVerticalSpacing, childHeight + verticalSpacing);
				lineHeight = Math.max(lineHeight, childHeight);

				currentLine.height = lineHeight;

				int posX = getPaddingLeft(widthMeasureSpec) + lineLength - childWidth;
				int posY = getPaddingTop() + prevLinePosition;

				element.posX = posX;
				element.posY = posY;
				element.width = childWidth;
				element.height = childHeight;
				element.editText = layoutParams.editText;

				controlMaxLength = Math.max(controlMaxLength, lineLength);
				controlMaxHeight = prevLinePosition + lineHeight;
			}
			firstLine = false;
		}

		int x = controlMaxLength;
		int y = controlMaxHeight;

		y += getPaddingTop() + getPaddingBottom();

		int centerVertically = 0;
		if (y < getSuggestedMinimumHeight()) {
			centerVertically = (getSuggestedMinimumHeight() - y) / 2;
		}

		y = Math.max(y, getSuggestedMinimumHeight());

		for (LineData lineData : lines) {
			for (ElementData elementData : lineData.elements) {
				if (elementData.view != null) {
					int centerVerticallyWithinLine = 0;

					if (elementData.editText && elementData.view instanceof TextView) {
						TextView editText = (TextView) elementData.view;
						centerVerticallyWithinLine = Math.round(editText.getPaddingBottom() * 0.5f);
					}
					if (elementData.height < lineData.height) {
						centerVerticallyWithinLine = Math.round((lineData.height - elementData.height) * 0.5f);
					}

					elementData.posY += centerVertically + centerVerticallyWithinLine;
					LayoutParams layoutParams = (LayoutParams) elementData.view.getLayoutParams();
					layoutParams.setPosition(elementData.posX, elementData.posY);
				}
			}
		}

		this.setMeasuredDimension(resolveSize(x, widthMeasureSpec), resolveSize(y, heightMeasureSpec));
		this.applyLayoutDirection();
	}

	private void applyLayoutDirection() {
		// Deal with RTL languages, mirror whole view group, and then mirror its separate elements back to have text
		// the right way round. RTL layoutDirection = -1, LTR layoutDirection = 1
		this.setScaleX(layoutDirection);
		for (LineData line : lines) {
			for (ElementData element : line.elements) {
				View child = element.view;
				if (child != null && child.getVisibility() != GONE) {
					child.setScaleX(layoutDirection);
				}
			}
		}
	}

	private int preLayoutMeasureWidth(View child, int sizeWidth, int sizeHeight, int modeWidth, int modeHeight) {
		if (child instanceof Spinner) {
			child.measure(MeasureSpec.makeMeasureSpec(sizeWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
					sizeHeight, modeHeight == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : modeHeight));
		} else {
			child.measure(MeasureSpec.makeMeasureSpec(sizeWidth, modeWidth == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST
					: modeWidth), MeasureSpec.makeMeasureSpec(sizeHeight,
					modeHeight == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : modeHeight));
		}

		Resources resources = getResources();
		LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

		int childWidth = child.getMeasuredWidth();
		if (layoutParams.editText) {
			childWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minTextFieldWidthDp,
					resources.getDisplayMetrics());
		}
		if (child instanceof Spinner) {
			childWidth = sizeWidth;
		}
		return childWidth;
	}

	protected LineData getNextLine(LineData currentLine) {
		int index = lines.indexOf(currentLine) + 1;
		if (index < lines.size()) {
			return lines.get(index);
		} else {
			return allocateNewLine();
		}
	}

	protected ElementData getElement(LineData currentLine, int elementInLineIndex) {
		if (elementInLineIndex < currentLine.elements.size()) {
			return currentLine.elements.get(elementInLineIndex);
		} else {
			ElementData elementData = new ElementData(null, 0, 0, 0, 0);
			currentLine.elements.add(elementData);
			return elementData;
		}
	}

	protected int getHorizontalSpacing(LayoutParams layoutParams) {
		int horizontalSpacing;
		if (layoutParams.horizontalSpacingSpecified()) {
			horizontalSpacing = layoutParams.horizontalSpacing;
		} else {
			horizontalSpacing = this.horizontalSpacing;
		}
		return horizontalSpacing;
	}

	protected int getVerticalSpacing(LayoutParams layoutParams) {
		int verticalSpacing;
		if (layoutParams.verticalSpacingSpecified()) {
			verticalSpacing = layoutParams.verticalSpacing;
		} else {
			verticalSpacing = this.verticalSpacing;
		}
		return verticalSpacing;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
			child.layout(layoutParams.positionX, layoutParams.positionY,
					layoutParams.positionX + child.getMeasuredWidth(),
					layoutParams.positionY + child.getMeasuredHeight());
		}
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		boolean more = super.drawChild(canvas, child, drawingTime);
		this.drawDebugInfo(canvas, child);
		return more;
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
		return layoutParams instanceof LayoutParams;
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
		return new LayoutParams(getContext(), attributeSet);
	}

	@Override
	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParameters) {
		return new LayoutParams(layoutParameters);
	}

	private void readStyleParameters(Context context, AttributeSet attributeSet) {
		TypedArray styledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.BrickLayout);
		try {
			horizontalSpacing = styledAttributes.getDimensionPixelSize(R.styleable.BrickLayout_horizontalSpacing, 0);
			verticalSpacing = styledAttributes.getDimensionPixelSize(R.styleable.BrickLayout_verticalSpacing, 0);
			orientation = styledAttributes.getInteger(R.styleable.BrickLayout_orientation, HORIZONTAL);
			debugDraw = styledAttributes.getBoolean(R.styleable.BrickLayout_debugDraw, false);
			userBrick = styledAttributes.getBoolean(R.styleable.BrickLayout_userDefinedBrick, false);
			usePng = styledAttributes.getBoolean(R.styleable.BrickLayout_usePng, false);
		} finally {
			styledAttributes.recycle();
		}
	}

	public void drawDebugInfo(Canvas canvas, View child) {
		if (!debugDraw) {
			return;
		}

		Paint childPaint = this.createPaint(0xffffff00);
		Paint layoutPaint = this.createPaint(0xff00ff00);
		Paint newLinePaint = this.createPaint(0xffff0000);

		LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

		if (layoutParams.horizontalSpacing > 0) {
			float x = child.getRight();
			float y = child.getTop() + child.getHeight() / 2.0f;
			canvas.drawLine(x, y, x + layoutParams.horizontalSpacing, y, childPaint);
			canvas.drawLine(x + layoutParams.horizontalSpacing - 4.0f, y - 4.0f, x + layoutParams.horizontalSpacing, y,
					childPaint);
			canvas.drawLine(x + layoutParams.horizontalSpacing - 4.0f, y + 4.0f, x + layoutParams.horizontalSpacing, y,
					childPaint);
		} else if (this.horizontalSpacing > 0) {
			float x = child.getRight();
			float y = child.getTop() + child.getHeight() / 2.0f;
			canvas.drawLine(x, y, x + this.horizontalSpacing, y, layoutPaint);
			canvas.drawLine(x + this.horizontalSpacing - 4.0f, y - 4.0f, x + this.horizontalSpacing, y, layoutPaint);
			canvas.drawLine(x + this.horizontalSpacing - 4.0f, y + 4.0f, x + this.horizontalSpacing, y, layoutPaint);
		}

		if (layoutParams.verticalSpacing > 0) {
			float x = child.getLeft() + child.getWidth() / 2.0f;
			float y = child.getBottom();
			canvas.drawLine(x, y, x, y + layoutParams.verticalSpacing, childPaint);
			canvas.drawLine(x - 4.0f, y + layoutParams.verticalSpacing - 4.0f, x, y + layoutParams.verticalSpacing,
					childPaint);
			canvas.drawLine(x + 4.0f, y + layoutParams.verticalSpacing - 4.0f, x, y + layoutParams.verticalSpacing,
					childPaint);
		} else if (this.verticalSpacing > 0) {
			float x = child.getLeft() + child.getWidth() / 2.0f;
			float y = child.getBottom();
			canvas.drawLine(x, y, x, y + this.verticalSpacing, layoutPaint);
			canvas.drawLine(x - 4.0f, y + this.verticalSpacing - 4.0f, x, y + this.verticalSpacing, layoutPaint);
			canvas.drawLine(x + 4.0f, y + this.verticalSpacing - 4.0f, x, y + this.verticalSpacing, layoutPaint);
		}

		if (layoutParams.newLine) {
			if (orientation == HORIZONTAL) {
				float x = child.getLeft();
				float y = child.getTop() + child.getHeight() / 2.0f;
				canvas.drawLine(x, y - 6.0f, x, y + 6.0f, newLinePaint);
			} else {
				float x = child.getLeft() + child.getWidth() / 2.0f;
				float y = child.getTop();
				canvas.drawLine(x - 6.0f, y, x + 6.0f, y, newLinePaint);
			}
		}
	}

	protected Paint createPaint(int color) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setStrokeWidth(2.0f);
		return paint;
	}

	protected class LineData {
		public int totalTextFieldWidth;
		public int allowableTextFieldWidth;
		public int numberOfTextFields;
		public int height;
		public LinkedList<ElementData> elements;

		public LineData() {
			elements = new LinkedList<ElementData>();
		}
	}

	protected class ElementData {
		public int posX;
		public int posY;
		public int height;
		public int width;
		public boolean editText;
		public View view;

		public ElementData(View view, int posX, int posY, int childWidth, int childHeight) {
			this.posX = posX;
			this.posY = posY;
			this.height = childHeight;
			this.width = childWidth;
			this.view = view;
		}
	}

	public static class LayoutParams extends ViewGroup.LayoutParams {
		private static final int NO_SPACING = -1;

		private int positionX;
		private int positionY;
		private int horizontalSpacing = NO_SPACING;
		private int verticalSpacing = NO_SPACING;
		private boolean newLine = false;
		private boolean forceNewLine = false;
		private boolean editText = false;

		public enum InputType {
			NUMBER, TEXT
		}

		public LayoutParams(Context context, AttributeSet attributeSet) {
			super(context, attributeSet);
			this.readStyleParameters(context, attributeSet);
		}

		public LayoutParams(int width, int height) {
			super(width, height);
		}

		public LayoutParams(ViewGroup.LayoutParams layoutParams) {
			super(layoutParams);
		}

		public boolean horizontalSpacingSpecified() {
			return horizontalSpacing != NO_SPACING;
		}

		public boolean verticalSpacingSpecified() {
			return verticalSpacing != NO_SPACING;
		}

		public void setPosition(int x, int y) {
			this.positionX = x;
			this.positionY = y;
		}

		public void setHorizontalSpacing(int horizontalSpacing) {
			this.horizontalSpacing = horizontalSpacing;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public void setEditText(boolean editText) {
			this.editText = editText;
		}

		public void setNewLine(boolean newLine) {
			this.newLine = newLine;
		}

		public boolean getNewLine() {
			return newLine;
		}

		private void readStyleParameters(Context context, AttributeSet attributeSet) {
			TypedArray styledAttributes = context.obtainStyledAttributes(attributeSet,
					R.styleable.BrickLayout_Layout);

			try {
				horizontalSpacing = styledAttributes.getDimensionPixelSize(
						R.styleable.BrickLayout_Layout_layout_horizontalSpacing, NO_SPACING);
				verticalSpacing = styledAttributes.getDimensionPixelSize(
						R.styleable.BrickLayout_Layout_layout_verticalSpacing, NO_SPACING);
				newLine = styledAttributes.getBoolean(R.styleable.BrickLayout_Layout_layout_newLine, false);
				forceNewLine = styledAttributes.getBoolean(R.styleable.BrickLayout_Layout_layout_forceNewLine, false);
				editText =
						styledAttributes.getBoolean(R.styleable.BrickLayout_Layout_layout_editText,
								false);
			} finally {
				styledAttributes.recycle();
			}
		}
	}
}
