/* Copyright 2016 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package org.catrobat.catroid.FaceRecognizer.env;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.Vector;

public class BorderedText {
    private final Paint interiorPaint;
    private final Paint exteriorPaint;

    private final float textSize;

    public BorderedText(final float textSize) {
        this(Color.WHITE, Color.BLACK, textSize);
    }

    public BorderedText(final int interiorColor, final int exteriorColor, final float textSize) {
        interiorPaint = new Paint();
        interiorPaint.setTextSize(textSize);
        interiorPaint.setColor(interiorColor);
        interiorPaint.setStyle(Paint.Style.FILL);
        interiorPaint.setAntiAlias(false);
        interiorPaint.setAlpha(255);

        exteriorPaint = new Paint();
        exteriorPaint.setTextSize(textSize);
        exteriorPaint.setColor(exteriorColor);
        exteriorPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        exteriorPaint.setStrokeWidth(textSize / 8);
        exteriorPaint.setAntiAlias(false);
        exteriorPaint.setAlpha(255);

        this.textSize = textSize;
    }

    public void setTypeface(Typeface typeface) {
        interiorPaint.setTypeface(typeface);
        exteriorPaint.setTypeface(typeface);
    }

    public void drawText(final Canvas canvas, final float posX, final float posY, final String text) {
        canvas.drawText(text, posX, posY, exteriorPaint);
        canvas.drawText(text, posX, posY, interiorPaint);
    }

    public void drawLines(Canvas canvas, final float posX, final float posY, Vector<String> lines) {
        int lineNum = 0;
        for (final String line : lines) {
            drawText(canvas, posX, posY - getTextSize() * (lines.size() - lineNum - 1), line);
            ++lineNum;
        }
    }

    public void setInteriorColor(final int color) {
        interiorPaint.setColor(color);
    }

    public void setExteriorColor(final int color) {
        exteriorPaint.setColor(color);
    }

    public float getTextSize() {
        return textSize;
    }

    public void setAlpha(final int alpha) {
        interiorPaint.setAlpha(alpha);
        exteriorPaint.setAlpha(alpha);
    }

    public void getTextBounds(
            final String line, final int index, final int count, final Rect lineBounds) {
        interiorPaint.getTextBounds(line, index, count, lineBounds);
    }

    public void setTextAlign(final Paint.Align align) {
        interiorPaint.setTextAlign(align);
        exteriorPaint.setTextAlign(align);
    }
}

