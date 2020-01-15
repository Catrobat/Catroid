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

package org.catrobat.catroid.ui.fragment;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.ui.PianoView;

import java.util.Locale;

public class PianoFragment {

	private final FormulaBrick formulaBrick;
	private final Brick.BrickField noteField;

	private TextView noteValueView;

	public PianoFragment(FormulaBrick formulaBrick, Brick.BrickField noteField) {
		this.formulaBrick = formulaBrick;
		this.noteField = noteField;
	}

	public View getView(final Context context) {
		View selectorView = View.inflate(context, R.layout.note_selector_view, null);

		Integer currentFieldValue = getCurrentBrickFieldValue(context, noteField);

		selectorView.setFocusableInTouchMode(true);
		selectorView.requestFocus();
		PianoView pianoView = selectorView.findViewById(R.id.piano_note_selector_view);
		pianoView.makePianoKeysClickable(SoundManager.getInstance().getMidiNotePlayer(), this);
		pianoView.updateMidiValuesContainingNote(
				new NoteName(currentFieldValue));
		noteValueView = selectorView.findViewById(R.id.note_value);
		noteValueView.setText(String.format(Locale.ENGLISH, "%d", currentFieldValue));
		noteValueView.setOnClickListener(view -> FormulaEditorFragment.showFragment(context, formulaBrick, noteField));

		ImageButton previousOctaveButton = selectorView.findViewById(R.id.piano_note_selector_previous_octave_button);
		previousOctaveButton.setOnClickListener(view -> {
			NoteName currentActiveNoteName =
					new NoteName(getCurrentBrickFieldValue(context, noteField));
			pianoView.previousOctave(currentActiveNoteName);
		});
		ImageButton nextOctaveButton = selectorView.findViewById(R.id.piano_note_selector_next_octave_button);
		nextOctaveButton.setOnClickListener(view -> {
			NoteName currentActiveNoteName =
					new NoteName(getCurrentBrickFieldValue(context, noteField));
			pianoView.nextOctave(currentActiveNoteName);
		});

		return selectorView;
	}

	public void updateFieldValue(int midiValue) {
		formulaBrick.setFormulaWithBrickField(noteField, new Formula(midiValue));
		noteValueView.setText(String.format(Locale.GERMAN, "%d", midiValue));
	}

	private Integer getCurrentBrickFieldValue(Context context, Brick.BrickField brickField) {
		int value = Integer.parseInt(
				formulaBrick.getFormulaWithBrickField(brickField).getTrimmedFormulaString(context).trim());

		if (value > NoteName.MAX_NOTE_MIDI) {
			value = NoteName.MAX_NOTE_MIDI;
		}
		if (value < NoteName.DEFAULT_NOTE_MIDI) {
			value = NoteName.DEFAULT_NOTE_MIDI;
		}
		formulaBrick.setFormulaWithBrickField(brickField, new Formula(value));
		return value;
	}
}
