/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.mididriver.MidiNotePlayer;
import org.catrobat.catroid.pocketmusic.mididriver.MidiRunnable;
import org.catrobat.catroid.pocketmusic.mididriver.MidiSignals;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.ui.fragment.PianoFragment;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PianoView extends ViewGroup {

	private Map<PianoKeyType, List<TextView>> pianoKeyTypeMap =
			new EnumMap<>(PianoKeyType.class);
	private static final PianoKeyHeight[] HEIGHT_DISTRIBUTION = new PianoKeyHeight[] {
			PianoKeyHeight.ONE_AND_A_HALF_KEY_HEIGHT,
			PianoKeyHeight.DOUBLE_KEY_HEIGHT,
			PianoKeyHeight.ONE_AND_A_HALF_KEY_HEIGHT,
			PianoKeyHeight.ONE_AND_A_HALF_KEY_HEIGHT,
			PianoKeyHeight.DOUBLE_KEY_HEIGHT,
			PianoKeyHeight.DOUBLE_KEY_HEIGHT,
			PianoKeyHeight.ONE_AND_A_HALF_KEY_HEIGHT,
			PianoKeyHeight.SINGLE_KEY_HEIGHT
	};
	private int margin;

	public PianoView(Context context) {
		this(context, null);
	}

	public PianoView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		margin = getResources().getDimensionPixelSize(R.dimen.pocketmusic_trackrow_margin);
		initializePianoKeys(context);
	}

	private void initializePianoKeys(Context context) {
		for (PianoKeyType type : PianoKeyType.values()) {
			pianoKeyTypeMap.put(type, new ArrayList<>());
			fillKeyList(context, type);
		}
	}

	private void fillKeyList(Context context, PianoKeyType type) {
		int correspondingMidiValue = NoteName.DEFAULT_NOTE_MIDI;
		int numbersOfKeysInList = type.getNumberOfKeys();

		for (int i = 0; i < numbersOfKeysInList; i++) {
			while (PianoKeyType.UNSIGNED.equals(type)
					!= new NoteName(correspondingMidiValue).isSigned()) {
				correspondingMidiValue++;
			}
			createAndAddPianoKeyFromMidiValue(context, type, correspondingMidiValue);
			correspondingMidiValue++;
		}
	}

	private void createAndAddPianoKeyFromMidiValue(Context context, PianoKeyType type,
			int correspondingMidiValue) {
		List<TextView> pianoKeys = getPianoKeysOfType(type);

		TextView pianoKey = new TextView(context);
		pianoKey.setBackgroundColor(ContextCompat.getColor(context, type.getBackgroundColor()));
		pianoKey.setTag(correspondingMidiValue);
		pianoKey.setText(new NoteName(correspondingMidiValue).getName());
		pianoKey.setTextColor(ContextCompat.getColor(context, type.getTextColor()));

		pianoKeys.add(pianoKey);
		addView(pianoKey);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		if (changed) {
			int verticalEndPointOfKey = getMeasuredWidth() - 4 * margin;

			int currentAvailableSpaceToKeys = getMeasuredHeight() - TrackView.ROW_COUNT * 2 * margin;
			float referenceHeightSingleKey = (float) currentAvailableSpaceToKeys / TrackView.ROW_COUNT;

			scaleAllPianoKeysOfTypeToLayout(PianoKeyType.UNSIGNED, referenceHeightSingleKey,
					currentAvailableSpaceToKeys, margin, margin, verticalEndPointOfKey);

			scaleAllPianoKeysOfTypeToLayout(PianoKeyType.SIGNED, referenceHeightSingleKey,
					currentAvailableSpaceToKeys, Math.round(referenceHeightSingleKey + margin),
					(int) (getMeasuredWidth() * 0.42f), verticalEndPointOfKey);
		}
	}

	private void scaleAllPianoKeysOfTypeToLayout(PianoKeyType type,
			float referenceHeightSingleKey, int currentAvailableSpaceToKeys,
			int verticalStartingPoint, int horizontalStartPoint, int verticalEndPoint) {

		float currentNumberOfSingleKeyHeights = 0f;

		List<TextView> pianoKeys = getPianoKeysOfType(type);

		for (int i = 0; i < pianoKeys.size(); i++) {

			PianoKeyHeight currentKeyHeight = PianoKeyType.SIGNED.equals(type)
					? PianoKeyHeight.SINGLE_KEY_HEIGHT : HEIGHT_DISTRIBUTION[i];

			horizontalStartPoint = scalePianoKeysToLayout(pianoKeys.get(i),
					verticalStartingPoint,
					horizontalStartPoint, verticalEndPoint, referenceHeightSingleKey,
					currentKeyHeight);

			if (PianoKeyType.SIGNED.equals(type)) {
				horizontalStartPoint += Math.round(referenceHeightSingleKey + 4 * margin);

				if (i == 1) {
					horizontalStartPoint += Math.round(referenceHeightSingleKey);
				}
			} else {
				currentNumberOfSingleKeyHeights += HEIGHT_DISTRIBUTION[i].getMultipleOfReferenceHeight();
				currentAvailableSpaceToKeys -= Math.round(referenceHeightSingleKey
						* HEIGHT_DISTRIBUTION[i].getMultipleOfReferenceHeight());
				horizontalStartPoint += 2 * margin;
				referenceHeightSingleKey = (float) currentAvailableSpaceToKeys
						/ (TrackView.ROW_COUNT - currentNumberOfSingleKeyHeights);
			}
		}
	}

	private int scalePianoKeysToLayout(TextView pianoKey, int horizontalStartingPoint,
			int verticalStartingPoint, int horizontalEndPoint,
			float currentReferenceHeightKey, PianoKeyHeight pianoKeyHeight) {

		int currentKeyHeight = Math.round((currentReferenceHeightKey + margin)
				* pianoKeyHeight.getMultipleOfReferenceHeight());
		int verticalEndPoint = verticalStartingPoint + currentKeyHeight;
		pianoKey.layout(horizontalStartingPoint, verticalStartingPoint,
				horizontalEndPoint, verticalEndPoint);
		setTextLayoutToPianoKey(pianoKey);

		return verticalEndPoint;
	}

	private void setTextLayoutToPianoKey(TextView pianoKey) {
		pianoKey.setGravity(Gravity.CENTER_VERTICAL);
		pianoKey.setPadding(15, 0, 0, 0);
	}

	public void makePianoKeysClickable(final MidiNotePlayer midiNotePlayer,
			final PianoFragment pianoFragment) {
		for (List<TextView> keysPerType : pianoKeyTypeMap.values()) {
			for (View view : keysPerType) {
				view.setOnClickListener(pianoKey -> onPianoKeyClick(midiNotePlayer,
						pianoFragment, pianoKey));
			}
		}
	}

	private void onPianoKeyClick(final MidiNotePlayer midiNotePlayer,
			final PianoFragment pianoFragment, View pianoKey) {
		int midiValueOfView = (Integer) pianoKey.getTag();
		NoteName clickedNote = new NoteName(midiValueOfView);
		Handler h = new Handler(Looper.getMainLooper());
		MidiRunnable midiRunnable = new MidiRunnable(MidiSignals.NOTE_ON,
				clickedNote, 250, h,
				midiNotePlayer, null);
		h.post(midiRunnable);
		updatePianoKeyColors(clickedNote);

		if (pianoFragment != null) {
			pianoFragment.updateFieldValue(midiValueOfView);
		}
	}

	private void updatePianoKeyColors(NoteName activeNote) {
		for (Map.Entry<TextView, PianoKeyType> entry : getAllPianoKeysWithType().entrySet()) {
			entry.getKey().setBackgroundColor(
					ContextCompat.getColor(getContext(), entry.getValue().getBackgroundColor()));
		}
		setPianoKeyColor(activeNote, true);
	}

	public void setPianoKeyColor(NoteName note, boolean active) {
		int i = 0;
		boolean foundNote = false;

		int totalNumberOfKeys = 0;

		for (PianoKeyType type : PianoKeyType.values()) {
			totalNumberOfKeys += type.getNumberOfKeys();
		}

		for (int counter = getCurrentStartingNote().getMidi();
				counter < getCurrentStartingNote().getMidi() + totalNumberOfKeys; counter++) {
			NoteName tempNote = new NoteName(counter);
			if (note.equals(tempNote)) {
				foundNote = true;
				break;
			}
			if (note.isSigned() == tempNote.isSigned()) {
				i++;
			}
		}

		if (!foundNote) {
			return;
		}
		PianoKeyType pianoKeyType = note.isSigned() ? PianoKeyType.SIGNED : PianoKeyType.UNSIGNED;

		View noteView = getPianoKeysOfType(pianoKeyType).get(i);
		if (active) {
			noteView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orange));
		} else {
			noteView.setBackgroundColor(ContextCompat.getColor(getContext(),
					pianoKeyType.getBackgroundColor()));
		}
	}

	public void nextOctave(NoteName currentActiveNote) {
		updateMidiValuesStartingAtNote(getNextStartingNote(), currentActiveNote);
	}

	public void previousOctave(NoteName currentActiveNote) {
		updateMidiValuesStartingAtNote(getPreviousStartingNote(), currentActiveNote);
	}

	public void updateMidiValuesContainingNote(NoteName currentActiveNote) {
		List<NoteName> octaveStarts = NoteName.getAllPossibleOctaveStarts();
		for (int i = octaveStarts.size() - 1; i >= 0; i--) {
			if (octaveStarts.get(i).getMidi() <= currentActiveNote.getMidi()) {
				updateMidiValuesStartingAtNote(octaveStarts.get(i), currentActiveNote);
				break;
			}
		}
	}

	private void updateMidiValuesStartingAtNote(NoteName startingNote, NoteName currentActiveNote) {
		int correspondingMidiValue = startingNote.getMidi();
		for (PianoKeyType type : PianoKeyType.values()) {
			updateMidiValueOfType(type, correspondingMidiValue);
		}
		updatePianoKeyColors(currentActiveNote);
	}

	private void updateMidiValueOfType(PianoKeyType type,
			int midiValueOfStart) {
		for (TextView pianoKey : getPianoKeysOfType(type)) {
			while (PianoKeyType.SIGNED.equals(type)
					!= new NoteName(midiValueOfStart).isSigned()) {
				midiValueOfStart++;
			}
			pianoKey.setTag(midiValueOfStart);
			pianoKey.setText(new NoteName(midiValueOfStart).getName());
			midiValueOfStart++;
		}
	}

	private NoteName getCurrentStartingNote() {
		return new NoteName(
				(int) getPianoKeysOfType(PianoKeyType.UNSIGNED).get(0).getTag());
	}

	private NoteName getNextStartingNote() {
		List<NoteName> octaveStarts = NoteName.getAllPossibleOctaveStarts();
		int currentIndex = octaveStarts.indexOf(getCurrentStartingNote());
		if (currentIndex >= 0 && currentIndex < octaveStarts.size() - 1) {
			return octaveStarts.get(currentIndex + 1);
		}
		return getCurrentStartingNote();
	}

	private NoteName getPreviousStartingNote() {
		List<NoteName> octaveStarts = NoteName.getAllPossibleOctaveStarts();
		int currentIndex = octaveStarts.indexOf(getCurrentStartingNote());
		if (currentIndex > 0 && currentIndex < octaveStarts.size()) {
			return octaveStarts.get(currentIndex - 1);
		}
		return getCurrentStartingNote();
	}

	private Map<TextView, PianoKeyType> getAllPianoKeysWithType() {
		Map<TextView, PianoKeyType> pianoKeys = new HashMap<>();
		for (PianoKeyType type : PianoKeyType.values()) {
			for (TextView pianoKey : getPianoKeysOfType(type)) {
				pianoKeys.put(pianoKey, type);
			}
		}
		return pianoKeys;
	}

	private List<TextView> getPianoKeysOfType(PianoKeyType type) {
		List<TextView> pianoKeys = pianoKeyTypeMap.get(type);
		if (pianoKeys == null) {
			return new ArrayList<>();
		}
		return pianoKeys;
	}
}
