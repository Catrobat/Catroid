/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class NoteBrick extends BrickBaseType implements OnClickListener, FormulaBrick {
	private static final long serialVersionUID = 1L;

	private Formula note;

	private transient View prototypeView;

	public NoteBrick() {

	}

	public NoteBrick(String note) {
		this.note = new Formula(note);
	}

	public NoteBrick(Formula note) {
		this.note = note;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		NoteBrick copyBrick = (NoteBrick) clone();
		return copyBrick;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_note, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_note_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textHolder = (TextView) view.findViewById(R.id.brick_note_prototype_text_view);
		TextView textField = (TextView) view.findViewById(R.id.brick_note_edit_text);
		textField.setText(note.interpretString(sprite));
		note.setTextFieldId(R.id.brick_note_edit_text);
		note.refreshTextField(view);

		textHolder.setVisibility(View.GONE);
		textField.setVisibility(View.VISIBLE);
		textField.setOnClickListener(this);

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_note_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView noteLabel = (TextView) view.findViewById(R.id.brick_note_text_view);
			TextView noteTextView = (TextView) view.findViewById(R.id.brick_note_edit_text);
			noteLabel.setTextColor(noteLabel.getTextColors().withAlpha(alphaValue));
			noteTextView.setTextColor(noteTextView.getTextColors().withAlpha(alphaValue));
			noteTextView.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_note, null);
		TextView textSpeak = (TextView) prototypeView.findViewById(R.id.brick_note_prototype_text_view);
		textSpeak.setText(note.interpretString(sprite));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new NoteBrick(this.note);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		return null;
	}

	@Override
	public Formula getFormula() {
		return note;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		switch (view.getId()) {
			case R.id.brick_note_edit_text:
				FormulaEditorFragment.showFragment(view, this, note);
				break;
			default:
				break;
		}
	}
}
