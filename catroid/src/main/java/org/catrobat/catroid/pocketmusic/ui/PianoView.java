/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.note.NoteName;

import java.util.ArrayList;
import java.util.List;

public class PianoView extends ViewGroup {

	private List<View> whitePianoKeys = new ArrayList<>();
	private List<View> blackPianoKeys = new ArrayList<>();
	private static final int WHITE_KEY_COUNT = 8;
	private static final int BLACK_KEY_COUNT = 5;
	private static final ButtonHeight[] HEIGHT_DISTRIBUTION = new ButtonHeight[] {
			ButtonHeight.oneAndAHalfButtonHeight,
			ButtonHeight.doubleButtonHeight,
			ButtonHeight.oneAndAHalfButtonHeight,
			ButtonHeight.oneAndAHalfButtonHeight,
			ButtonHeight.doubleButtonHeight,
			ButtonHeight.doubleButtonHeight,
			ButtonHeight.oneAndAHalfButtonHeight,
			ButtonHeight.singleButtonHeight
	};
	private int margin;
	private int currentHeight;

	public PianoView(Context context) {
		this(context, null);
	}

	public PianoView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		margin = getResources().getDimensionPixelSize(R.dimen.pocketmusic_trackrow_margin);

		for (int i = 0; i < WHITE_KEY_COUNT + BLACK_KEY_COUNT; i++) {
			NoteName noteName = NoteName.getNoteNameFromMidiValue(NoteName.C1.getMidi() + i);
			View button = new PianoKey(context, noteName);
			if (noteName.isBlackKey()) {
				button.setBackgroundColor(ContextCompat.getColor(context, R.color.solid_black));
				blackPianoKeys.add(button);
			} else {
				button.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
				whitePianoKeys.add(button);
			}
		}
		for (View view : whitePianoKeys) {
			addView(view);
		}
		for (View view : blackPianoKeys) {
			addView(view);
		}
		currentHeight = 0;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		if (changed) {

			int collectiveButtonHeight = getMeasuredHeight() - TrackView.ROW_COUNT * 2 * margin;
			float currentButtonCount = 0f;

			currentHeight = margin;

			int rightside = getMeasuredWidth() - 4 * margin;

			for (int i = 0; i < WHITE_KEY_COUNT; i++) {

				int singleButtonHeight = round((float) collectiveButtonHeight / (TrackView.ROW_COUNT
						- currentButtonCount));

				float oneAndAHalfButtonHeight = 1.5f * singleButtonHeight + margin;
				int doubleButtonHeight = 2 * singleButtonHeight + 2 * margin;

				switch (HEIGHT_DISTRIBUTION[i]) {
					case singleButtonHeight:
						whitePianoKeys.get(i).layout(
								margin,
								currentHeight,
								rightside,
								currentHeight + singleButtonHeight
						);
						currentHeight += singleButtonHeight;
						collectiveButtonHeight -= round(singleButtonHeight);
						currentButtonCount += 1f;
						break;
					case oneAndAHalfButtonHeight:
						whitePianoKeys.get(i).layout(
								margin,
								currentHeight,
								rightside,
								currentHeight + round(oneAndAHalfButtonHeight)
						);
						currentHeight += round(oneAndAHalfButtonHeight);
						collectiveButtonHeight -= round(singleButtonHeight * 1.5f);
						currentButtonCount += 1.5f;
						break;
					case doubleButtonHeight:
						whitePianoKeys.get(i).layout(
								margin,
								currentHeight,
								rightside,
								currentHeight + doubleButtonHeight
						);
						currentHeight += doubleButtonHeight;
						collectiveButtonHeight -= singleButtonHeight * 2;
						currentButtonCount += 2f;
						break;
				}
				currentHeight += 2 * margin;
			}

			collectiveButtonHeight = getMeasuredHeight() - TrackView.ROW_COUNT * 2 * margin;
			int singleButtonHeight = roundUp((float) collectiveButtonHeight / TrackView.ROW_COUNT);

			collectiveButtonHeight -= singleButtonHeight;
			currentButtonCount = 1f;

			currentHeight = singleButtonHeight + margin;

			for (int i = 0; i < BLACK_KEY_COUNT; i++) {

				singleButtonHeight = roundUp((float) collectiveButtonHeight / (TrackView.ROW_COUNT - currentButtonCount));

				blackPianoKeys.get(i).layout(
						(int) (getMeasuredWidth() * 0.42f),
						currentHeight,
						rightside,
						currentHeight + singleButtonHeight + 4 * margin
				);

				currentHeight += 2 * singleButtonHeight + 4 * margin;
				collectiveButtonHeight -= 2 * singleButtonHeight;
				currentButtonCount += 2f;

				if (i == 1) {
					currentHeight += singleButtonHeight + 2 * margin;
					collectiveButtonHeight -= singleButtonHeight;
					currentButtonCount += 1f;
				}
			}
		}
	}

	private int roundUp(float floatValue) {
		return (int) Math.ceil(floatValue);
	}

	private int round(float floatValue) {
		return (int) (floatValue + 0.5f);
	}

	enum ButtonHeight {
		singleButtonHeight,
		oneAndAHalfButtonHeight,
		doubleButtonHeight
	}
}
