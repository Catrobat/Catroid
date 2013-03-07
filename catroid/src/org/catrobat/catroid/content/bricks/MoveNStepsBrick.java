/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
<<<<<<< HEAD
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
=======
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;
import org.catrobat.catroid.utils.Utils;
>>>>>>> origin/master

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;import java.util.List;

public class MoveNStepsBrick implements Brick, OnClickListener {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private Formula steps;

	private transient View view;

	public MoveNStepsBrick() {

	}

	public MoveNStepsBrick(Sprite sprite, double stepsValue) {
		this.sprite = sprite;
		steps = new Formula(stepsValue);
	}

	public MoveNStepsBrick(Sprite sprite, Formula steps) {
		this.sprite = sprite;

		this.steps = steps;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_move_n_steps, null);

		TextView text = (TextView) view.findViewById(R.id.brick_move_n_steps_prototype_text_view);
		EditText edit = (EditText) view.findViewById(R.id.brick_move_n_steps_edit_text);

<<<<<<< HEAD
		steps.setTextFieldId(R.id.brick_move_n_steps_edit_text);
		steps.refreshTextField(view);
=======
		edit.setText(String.valueOf(steps));
		TextView times = (TextView) view.findViewById(R.id.brick_move_n_steps_step_text_view);
		times.setText(view.getResources().getQuantityString(R.plurals.brick_move_n_step_plural,
				Utils.convertDoubleToPluralInteger(steps)));
>>>>>>> origin/master

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);
		edit.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.brick_move_n_steps, null);
		TextView times = (TextView) view.findViewById(R.id.brick_move_n_steps_step_text_view);
		times.setText(view.getResources().getQuantityString(R.plurals.brick_move_n_step_plural,
				Utils.convertDoubleToPluralInteger(steps)));
		return view;
	}

	@Override
	public Brick clone() {
		return new MoveNStepsBrick(getSprite(), steps);
	}

	@Override
	public void onClick(View view) {
		FormulaEditorFragment.showFragment(view, this, steps);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.moveNSteps(sprite, steps));
		return null;
	}
}
