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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.IconsUtil;
import org.catrobat.catroid.utils.TextSizeUtil;

import java.util.List;

public class ThinkBubbleBrick extends FormulaBrick implements OnClickListener {
	private static final long serialVersionUID = 1L;
	protected int type = Constants.THINK_BRICK;
	private transient View prototypeView;

	public ThinkBubbleBrick() {
		addAllowedBrickField(BrickField.STRING);
	}

	public ThinkBubbleBrick(String text) {
		addAllowedBrickField(BrickField.STRING);
		initializeBrickFields(new Formula(text));
	}

	protected void initializeBrickFields(Formula text) {
		addAllowedBrickField(BrickField.STRING);
		setFormulaWithBrickField(BrickField.STRING, text);
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		int layoutId = type == Constants.SAY_BRICK ? R.layout.brick_say_bubble : R.layout.brick_think_bubble;
		int checkboxId = type == Constants.SAY_BRICK ? R.id.brick_say_bubble_checkbox : R.id.brick_think_bubble_checkbox;
		int editTextId = type == Constants.SAY_BRICK ? R.id.brick_say_bubble_edit_text : R.id.brick_think_bubble_edit_text;

		view = View.inflate(context, layoutId, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		if (type == Constants.SAY_BRICK) {
			IconsUtil.addIcon(context, (TextView) view.findViewById(R.id.brick_say_bubble_text_view),
					context.getString(R.string.category_looks));
		} else {
			IconsUtil.addIcon(context, (TextView) view.findViewById(R.id.brick_think_bubble_text_view),
					context.getString(R.string.category_looks));
		}

		setCheckboxView(checkboxId);

		TextView textField = (TextView) view.findViewById(editTextId);
		getFormulaWithBrickField(BrickField.STRING).setTextFieldId(editTextId);
		getFormulaWithBrickField(BrickField.STRING).refreshTextField(view);

		textField.setOnClickListener(this);
		TextSizeUtil.enlargeViewGroup((ViewGroup) view);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		int layoutId = type == Constants.SAY_BRICK ? R.layout.brick_say_bubble : R.layout.brick_think_bubble;
		int stringId = type == Constants.SAY_BRICK ? R.string.brick_say_bubble_default_value : R.string.brick_think_bubble_default_value;
		int prototypeTextViewId = type == Constants.SAY_BRICK ? R.id.brick_say_bubble_edit_text : R.id
				.brick_think_bubble_edit_text;

		prototypeView = View.inflate(context, layoutId, null);
		TextView textSpeak = (TextView) prototypeView.findViewById(prototypeTextViewId);
		textSpeak.setText(context.getString(stringId));
		return prototypeView;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		if (type == Constants.SAY_BRICK) {
			sequence.addAction(sprite.getActionFactory().createSayBubbleAction(sprite,
					getFormulaWithBrickField(BrickField.STRING)));
		} else {
			sequence.addAction(sprite.getActionFactory().createThinkBubbleAction(sprite,
					getFormulaWithBrickField(BrickField.STRING)));
		}
		return null;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.STRING);
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
	}
}
