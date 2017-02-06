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
import android.util.Log;
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
import org.catrobat.catroid.content.Translatable;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.IconsUtil;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class NoteBrick extends FormulaBrick implements OnClickListener, Translatable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = NoteBrick.class.getSimpleName();

	private transient View prototypeView;

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
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_note, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		IconsUtil.addIcon(context, (TextView) view.findViewById(R.id.brick_note_text_view),
				context.getString(R.string.category_looks));

		setCheckboxView(R.id.brick_note_checkbox);
		TextView textField = (TextView) view.findViewById(R.id.brick_note_edit_text);
		getFormulaWithBrickField(BrickField.NOTE).setTextFieldId(R.id.brick_note_edit_text);
		getFormulaWithBrickField(BrickField.NOTE).refreshTextField(view);

		textField.setOnClickListener(this);

		TextSizeUtil.enlargeViewGroup((ViewGroup) view);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_note, null);
		TextView textSpeak = (TextView) prototypeView.findViewById(R.id.brick_note_edit_text);
		textSpeak.setText(context.getString(R.string.brick_note_default_value));
		return prototypeView;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		return null;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.NOTE);
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
	}

	@Override
	public String translate(String templateName, Scene scene, Sprite sprite, Context context) {
		try {
			String key = templateName + Constants.TRANSLATION_NOTE;
			String value = getFormulaWithBrickField(Brick.BrickField.NOTE).interpretString(sprite);

			setFormulaWithBrickField(Brick.BrickField.NOTE,
					new Formula(Utils.getStringResourceByName(Utils.getStringResourceName(key, value), value, context)));

			return Utils.createStringEntry(key, value);
		} catch (InterpretationException e) {
			Log.e(TAG, "Could not set note formula: " + e.getMessage());
		}
		return null;
	}
}
