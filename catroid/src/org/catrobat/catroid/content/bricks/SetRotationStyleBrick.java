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
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.List;

public class SetRotationStyleBrick extends BrickBaseType {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;
	private transient Spinner spinner;
	private int selection;

	public SetRotationStyleBrick() {
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		view = View.inflate(context, R.layout.brick_set_rotation_style, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_set_rotation_style_normal_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		spinner = (Spinner) view.findViewById(R.id.brick_set_rotation_style_spinner);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			spinner.setClickable(true);
			spinner.setEnabled(true);
		} else {
			spinner.setClickable(false);
			spinner.setEnabled(false);
		}

		final ArrayAdapter<String> spinnerAdapter = createSpinnerAdapter(context);
		SpinnerAdapterWrapper spinnerAdapterWrapper = new SpinnerAdapterWrapper(context, spinnerAdapter);
		spinner.setAdapter(spinnerAdapterWrapper);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selection = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		spinner.setSelection(selection, true);

		return view;
	}

	private ArrayAdapter<String> createSpinnerAdapter(Context context) {
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		arrayAdapter.add(context.getString(R.string.brick_set_rotation_style_lr));
		arrayAdapter.add(context.getString(R.string.brick_set_rotation_style_normal));
		arrayAdapter.add(context.getString(R.string.brick_set_rotation_style_no));
		return arrayAdapter;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		SetRotationStyleBrick copyBrick = (SetRotationStyleBrick) clone();
		return copyBrick;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view != null) {
			View layout = view.findViewById(R.id.brick_set_rotation_style_normal_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			Spinner lookBrickSpinner = (Spinner) view.findViewById(R.id.brick_set_rotation_style_spinner);
			lookBrickSpinner.getBackground().setAlpha(alphaValue);
			this.alphaValue = alphaValue;
		}
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_set_rotation_style, null);

		spinner = (Spinner) prototypeView.findViewById(R.id.brick_set_rotation_style_spinner);
		spinner.setFocusableInTouchMode(false);
		spinner.setFocusable(false);
		spinner.setEnabled(false);

		SpinnerAdapter setLookSpinnerAdapter = createSpinnerAdapter(context);
		spinner.setAdapter(setLookSpinnerAdapter);
		spinner.setSelection(selection, true);
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new SetRotationStyleBrick();
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetRotationStyleAction(sprite,
				new Formula(selection)));
		return null;
	}

	private class SpinnerAdapterWrapper implements SpinnerAdapter {

		protected Context context;
		protected ArrayAdapter<String> spinnerAdapter;

		public SpinnerAdapterWrapper(Context context, ArrayAdapter<String> spinnerAdapter) {
			this.context = context;
			this.spinnerAdapter = spinnerAdapter;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver paramDataSetObserver) {
			spinnerAdapter.registerDataSetObserver(paramDataSetObserver);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver) {
			spinnerAdapter.unregisterDataSetObserver(paramDataSetObserver);
		}

		@Override
		public int getCount() {
			return spinnerAdapter.getCount();
		}

		@Override
		public Object getItem(int paramInt) {
			return spinnerAdapter.getItem(paramInt);
		}

		@Override
		public long getItemId(int paramInt) {
			return spinnerAdapter.getItemId(paramInt);
		}

		@Override
		public boolean hasStableIds() {
			return spinnerAdapter.hasStableIds();
		}

		@Override
		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			return spinnerAdapter.getView(paramInt, paramView, paramViewGroup);
		}

		@Override
		public int getItemViewType(int paramInt) {
			return spinnerAdapter.getItemViewType(paramInt);
		}

		@Override
		public int getViewTypeCount() {
			return spinnerAdapter.getViewTypeCount();
		}

		@Override
		public boolean isEmpty() {
			return spinnerAdapter.isEmpty();
		}

		@Override
		public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			View dropDownView = spinnerAdapter.getDropDownView(paramInt, paramView, paramViewGroup);

			dropDownView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
					return false;
				}
			});
			return dropDownView;
		}
	}
}

