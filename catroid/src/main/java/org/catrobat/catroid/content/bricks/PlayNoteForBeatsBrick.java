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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.PianoFragment;

public class PlayNoteForBeatsBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public PlayNoteForBeatsBrick() {
		addAllowedBrickField(BrickField.MIDI_NOTE, R.id.brick_play_note_edit_note);
		addAllowedBrickField(BrickField.DURATION_IN_BEATS, R.id.brick_play_note_edit_duration);
	}

	public PlayNoteForBeatsBrick(Formula noteFormula) {
		this();
		setFormulaWithBrickField(BrickField.MIDI_NOTE, noteFormula);
	}

	public PlayNoteForBeatsBrick(int noteMidi) {
		this(new Formula(noteMidi));
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_play_note_for_beats;
	}

	@Override
	public View getCustomView(Context context) {
		return new PianoFragment(this, BrickField.MIDI_NOTE).getView(context);
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		if (view.getId() == R.id.brick_play_note_edit_duration) {
			super.showFormulaEditorToEditFormula(view);
		} else {
			FormulaEditorFragment.showCustomFragment(view.getContext(), this, BrickField.MIDI_NOTE);
		}
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPlayNoteForBeatsAction(sprite,
				getFormulaWithBrickField(BrickField.MIDI_NOTE),
				getFormulaWithBrickField(BrickField.DURATION_IN_BEATS)));
	}
}
