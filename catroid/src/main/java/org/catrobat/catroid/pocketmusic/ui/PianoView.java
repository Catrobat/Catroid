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
package org.catrobat.catroid.pocketmusic.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.note.NoteName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;

public class PianoView extends ViewGroup {

	private List<TextView> whitePianoKeys = new ArrayList<>();
	private List<TextView> blackPianoKeys = new ArrayList<>();
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
		for (int i = 0; i < WHITE_KEY_COUNT; i++) {
			TextView whiteButton = new TextView(context);
			whiteButton.setBackgroundColor(ContextCompat.getColor(context, R.color.solid_white));

			whiteButton.setTextAlignment(TEXT_ALIGNMENT_VIEW_END);
			whiteButton.setTextColor(ContextCompat.getColor(context, R.color.solid_black));
			whiteButton.setText(String.valueOf(TrackRowView.getMidiValueForRow(TrackView.WHITE_KEY_INDICES[i])));

			whitePianoKeys.add(whiteButton);
			addView(whiteButton);
		}
		for (int i = 0; i < BLACK_KEY_COUNT; i++) {
			TextView blackButton = new TextView(context);
			blackButton.setBackgroundColor(ContextCompat.getColor(context, R.color.solid_black));

			blackButton.setTextAlignment(TEXT_ALIGNMENT_VIEW_END);
			blackButton.setTextColor(ContextCompat.getColor(context, R.color.solid_white));
			blackButton.setText(String.valueOf(TrackRowView.getMidiValueForRow(TrackView.BLACK_KEY_INDICES[i])));

			blackPianoKeys.add(blackButton);
			addView(blackButton);
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
						whitePianoKeys.get(i).setPadding(0, singleButtonHeight / 2
								- round(whitePianoKeys.get(i).getTextSize()) / 2, 0, 0);
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
						if (i == 0 || i == 3) {
							whitePianoKeys.get(i).setPadding(0, singleButtonHeight / 2
									- round(whitePianoKeys.get(i).getTextSize()) / 2, 0, 0);
						} else {
							whitePianoKeys.get(i).setPadding(0, round(oneAndAHalfButtonHeight) / 3 * 2
									- round(whitePianoKeys.get(i).getTextSize()) / 2, 0, 0);
						}
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
						whitePianoKeys.get(i).setPadding(0, doubleButtonHeight / 2
								- round(whitePianoKeys.get(i).getTextSize()) / 2, 0, 0);
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

				blackPianoKeys.get(i).setPadding(0, singleButtonHeight / 2
						- round(blackPianoKeys.get(i).getTextSize()) / 2, 0, 0);

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

	public void setKeyColor(NoteName note, boolean active) {
		int i = 0;
		for (; i <= TrackView.ROW_COUNT; i++) {
			NoteName tempNote = NoteName.getNoteNameFromMidiValue(TrackRowView.getMidiValueForRow(i));
			if (note.equals(tempNote)) {
				break;
			}
		}
		View noteView;
		Boolean isBlackKey = false;
		int index = Arrays.binarySearch(TrackView.BLACK_KEY_INDICES, i);
		if (index < 0) {
			index = Arrays.binarySearch(TrackView.WHITE_KEY_INDICES, i);
			if (index < 0) {
				return;
			}
			noteView = whitePianoKeys.get(index);
		} else {
			noteView = blackPianoKeys.get(index);
			isBlackKey = true;
		}
		if (active) {
			noteView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orange));
		} else {
			noteView.setBackgroundColor(ContextCompat.getColor(getContext(),
					isBlackKey ? R.color.solid_black : R.color.solid_white));
		}
	}

	private int roundUp(float floatValue) {
		return (int) Math.ceil(floatValue);
	}

	private int round(float floatValue) {
		return (int) (floatValue + 0.5f);
	}

	@VisibleForTesting
	public List<TextView> getWhitePianoKeys() {
		return whitePianoKeys;
	}

	@VisibleForTesting
	public List<TextView> getBlackPianoKeys() {
		return blackPianoKeys;
	}

	enum ButtonHeight {
		singleButtonHeight,
		oneAndAHalfButtonHeight,
		doubleButtonHeight
	}
}
