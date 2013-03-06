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
<<<<<<< HEAD
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
=======
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;
>>>>>>> refs/remotes/origin/master

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
<<<<<<< HEAD
=======
import android.widget.Toast;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
>>>>>>> refs/remotes/origin/master

public class TurnRightBrick implements Brick, OnClickListener {

	private static final long serialVersionUID = 1L;

	private Sprite sprite;

	private Formula degrees;

	private transient View view;

	public TurnRightBrick() {

	}

	public TurnRightBrick(Sprite sprite, double degreesValue) {
		this.sprite = sprite;
		degrees = new Formula(degreesValue);
	}

	public TurnRightBrick(Sprite sprite, Formula degreesFormula) {
		this.sprite = sprite;
		this.degrees = degreesFormula;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
<<<<<<< HEAD
	}

	@Override
	public void execute() {
		sprite.look.rotation = (sprite.look.rotation % 360f) - degrees.interpretFloat(sprite);
=======
>>>>>>> refs/remotes/origin/master
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_turn_right, null);

		TextView textDegrees = (TextView) view.findViewById(R.id.brick_turn_right_prototype_text_view);
		EditText editDegrees = (EditText) view.findViewById(R.id.brick_turn_right_edit_text);
		degrees.setTextFieldId(R.id.brick_turn_right_edit_text);
		degrees.refreshTextField(view);
		textDegrees.setVisibility(View.GONE);
		editDegrees.setVisibility(View.VISIBLE);
		editDegrees.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_turn_right, null);
	}

	@Override
	public Brick clone() {
		return new TurnRightBrick(getSprite(), degrees);
	}

	@Override
	public void onClick(View view) {
		FormulaEditorFragment.showFragment(view, this, degrees);
	}

	@Override
	public SequenceAction addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.turnRight(sprite, (float) degrees));
		return null;
	}

}
