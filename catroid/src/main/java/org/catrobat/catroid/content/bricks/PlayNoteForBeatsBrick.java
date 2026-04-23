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
package org.catrobat.catroid.content.bricks;

import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.strategy.ShowFormulaEditorStrategy;
import org.catrobat.catroid.content.strategy.ShowNotePickerFormulaEditorStrategy;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.UiUtils;

import androidx.appcompat.app.AppCompatActivity;

public class PlayNoteForBeatsBrick extends FormulaBrick {

	private final transient ShowFormulaEditorStrategy showFormulaEditorStrategy;

	public PlayNoteForBeatsBrick() {
		addAllowedBrickField(BrickField.NOTE_TO_PLAY, R.id.brick_play_note_for_beats_note_edit_text);
		addAllowedBrickField(BrickField.BEATS_TO_PLAY_NOTE, R.id.brick_play_note_for_beats_beats_edit_text);

		showFormulaEditorStrategy = new ShowNotePickerFormulaEditorStrategy();
	}

	public PlayNoteForBeatsBrick(int note, int pausedBeats) {
		this(new Formula(note), new Formula(pausedBeats));
	}

	public PlayNoteForBeatsBrick(Formula note, Formula beats) {
		this();
		setFormulaWithBrickField(BrickField.NOTE_TO_PLAY, note);
		setFormulaWithBrickField(BrickField.BEATS_TO_PLAY_NOTE, beats);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_play_note_for_beats;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		if (areAllBrickFieldsNumbers() && view.getId() == brickFieldToTextViewIdMap.get(BrickField.NOTE_TO_PLAY)) {
			ShowFormulaEditorStrategy.Callback callback = new PlayNoteForBeatsBrick.PlayNoteForBeatsBrickCallback(view);
			showFormulaEditorStrategy.showFormulaEditorToEditFormula(view, callback);
		} else {
			superShowFormulaEditor(view);
		}
	}

	private void superShowFormulaEditor(View view) {
		super.showFormulaEditorToEditFormula(view);
	}

	private boolean areAllBrickFieldsNumbers() {
		return isBrickFieldANumber(BrickField.NOTE_TO_PLAY)
				&& isBrickFieldANumber(BrickField.BEATS_TO_PLAY_NOTE);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory()
				.createPlayNoteForBeatsAction(sprite, sequence,
						getFormulaWithBrickField(BrickField.NOTE_TO_PLAY),
						getFormulaWithBrickField(BrickField.BEATS_TO_PLAY_NOTE)));
	}

	private final class PlayNoteForBeatsBrickCallback implements ShowFormulaEditorStrategy.Callback {
		private final View view;

		private PlayNoteForBeatsBrickCallback(View view) {
			this.view = view;
		}

		@Override
		public void showFormulaEditor(View view) {
			superShowFormulaEditor(view);
		}

		@Override
		public void setValue(int value) {
			setFormulaWithBrickField(BrickField.NOTE_TO_PLAY, new Formula(value));
			AppCompatActivity activity = UiUtils.getActivityFromView(view);
			notifyDataSetChanged(activity);
		}

		@Override
		public int getValue() {
			int note = 0;
			try {
				Formula formula = getFormulaWithBrickField(BrickField.NOTE_TO_PLAY);
				note = formula.interpretInteger(null);
			} catch (InterpretationException e) {
				return 0;
			}
			return note;
		}
	}
}
