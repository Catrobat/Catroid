/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class NoteBrick extends FormulaBrick implements OnClickListener {

	private static final long serialVersionUID = 1L;

	public NoteBrick() {
		addAllowedBrickField(BrickField.NOTE);
	}

	public NoteBrick(String note) {
		initializeBrickFields(new Formula(note));
	}

	public NoteBrick(Formula note) {
		initializeBrickFields(note);
	}

	private void initializeBrickFields(Formula note) {
		addAllowedBrickField(BrickField.NOTE);
		setFormulaWithBrickField(BrickField.NOTE, note);
	}

	@Override
	protected int getLayoutRes() {
		return R.layout.brick_note;
	}

	@Override
	public View onCreateView(final Context context) {
		super.onCreateView(context);

		TextView textField = (TextView) view.findViewById(R.id.brick_note_edit_text);
		getFormulaWithBrickField(BrickField.NOTE).setTextFieldId(R.id.brick_note_edit_text);
		getFormulaWithBrickField(BrickField.NOTE).refreshTextField(view);

		textField.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);
		TextView textSpeak = (TextView) prototypeView.findViewById(R.id.brick_note_edit_text);
		textSpeak.setText(context.getString(R.string.brick_note_default_value));
		return prototypeView;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		return null;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.NOTE);
	}
}
