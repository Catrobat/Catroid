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

import java.util.List;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class TurnRightBrick extends BrickBaseType implements OnClickListener {
	private static final long serialVersionUID = 1L;

	private Sprite sprite;
	private Formula degrees;

	private transient View view;
	private transient View prototypeView;

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
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.brick_turn_right, null);

			checkbox = (CheckBox) view.findViewById(R.id.brick_turn_right_checkbox);
			final Brick brickInstance = this;

			checkbox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					checked = !checked;
					adapter.handleCheck(brickInstance, checked);
				}
			});
		}
		TextView textDegrees = (TextView) view.findViewById(R.id.brick_turn_right_prototype_text_view);
		EditText editDegrees = (EditText) view.findViewById(R.id.brick_turn_right_edit_text);
		degrees.setTextFieldId(R.id.brick_turn_right_edit_text);
		degrees.refreshTextField(view);

		TextView times = (TextView) view.findViewById(R.id.brick_turn_right_degree_text_view);

		if (degrees.isSingleNumberFormula()) {
			times.setText(view.getResources().getQuantityString(R.plurals.brick_turn_right_degree_plural,
					Utils.convertDoubleToPluralInteger(degrees.interpretFloat(sprite))));
		} else {

			// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
			// in hopefully all possible languages
			times.setText(view.getResources().getQuantityString(R.plurals.brick_turn_right_degree_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		textDegrees.setVisibility(View.GONE);
		editDegrees.setVisibility(View.VISIBLE);
		editDegrees.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_turn_right, null);
		TextView textDegrees = (TextView) prototypeView.findViewById(R.id.brick_turn_right_prototype_text_view);
		textDegrees.setText(String.valueOf(degrees.interpretFloat(sprite)));
		TextView times = (TextView) prototypeView.findViewById(R.id.brick_turn_right_degree_text_view);
		times.setText(context.getResources().getQuantityString(R.plurals.brick_turn_right_degree_plural,
				Utils.convertDoubleToPluralInteger(degrees.interpretFloat(sprite))));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new TurnRightBrick(getSprite(), degrees.clone());
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_turn_right_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, degrees);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.turnRight(sprite, degrees));
		return null;
	}
}
