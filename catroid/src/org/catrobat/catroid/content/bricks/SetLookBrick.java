/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.ui.fragment.LookFragment.OnLookDataListChangedAfterNewListener;

import java.util.List;

public class SetLookBrick extends BrickBaseType implements OnLookDataListChangedAfterNewListener {
	private static final long serialVersionUID = 1L;
	private LookData look;
	private transient View prototypeView;
	private transient LookData oldSelectedLook;
	private transient AdapterView<?> adapterView;

	public SetLookBrick() {
	}

	public void setLook(LookData lookData) {
		this.look = lookData;
	}

	public LookData getLook() {
		return this.look;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		SetLookBrick copyBrick = (SetLookBrick) clone();

		for (LookData data : sprite.getLookDataList()) {
			if (data.getAbsolutePath().equals(look.getAbsolutePath())) {
				copyBrick.look = data;
				break;
			}
		}
		return copyBrick;
	}

	public String getImagePath() {
		return look.getAbsolutePath();
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		final Brick brickInstance = this;
		view = View.inflate(context, R.layout.brick_set_look, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_set_look_checkbox);
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		final Spinner lookbrickSpinner = (Spinner) view.findViewById(R.id.brick_set_look_spinner);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			lookbrickSpinner.setClickable(true);
			lookbrickSpinner.setEnabled(true);
		} else {
			lookbrickSpinner.setClickable(false);
			lookbrickSpinner.setEnabled(false);
		}

		final ArrayAdapter<LookData> spinnerAdapter = createLookAdapter(context);

		SpinnerAdapterWrapper spinnerAdapterWrapper = new SpinnerAdapterWrapper(context, spinnerAdapter);

		lookbrickSpinner.setAdapter(spinnerAdapterWrapper);

		lookbrickSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					look = null;
				} else {
					look = (LookData) parent.getItemAtPosition(position);
					oldSelectedLook = look;
					adapterView = parent;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		setSpinnerSelection(lookbrickSpinner);

		if (ProjectManager.getInstance().getCurrentSprite().getName().equals(context.getString(R.string.background))) {
			TextView textField = (TextView) view.findViewById(R.id.brick_set_look_prototype_text_view);
			textField.setText(R.string.brick_set_background);
		}

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_set_look_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			Spinner lookBrickSpinner = (Spinner) view.findViewById(R.id.brick_set_look_spinner);
			TextView lookBrickTextView = (TextView) view.findViewById(R.id.brick_set_look_prototype_text_view);

			ColorStateList color = lookBrickTextView.getTextColors().withAlpha(alphaValue);
			lookBrickTextView.setTextColor(color);
			lookBrickSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}

			this.alphaValue = alphaValue;
		}

		return view;
	}

	private ArrayAdapter<LookData> createLookAdapter(Context context) {
		ArrayAdapter<LookData> arrayAdapter = new ArrayAdapter<LookData>(context, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		LookData dummyLookData = new LookData();
		dummyLookData.setLookName(context.getString(R.string.new_broadcast_message));
		arrayAdapter.add(dummyLookData);
		for (LookData lookData : ProjectManager.getInstance().getCurrentSprite().getLookDataList()) {
			arrayAdapter.add(lookData);
		}
		return arrayAdapter;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_set_look, null);
		if (ProjectManager.getInstance().getCurrentSprite().getName().equals(context.getString(R.string.background))) {
			TextView textField = (TextView) prototypeView.findViewById(R.id.brick_set_look_prototype_text_view);
			textField.setText(R.string.brick_set_background);
		}
		Spinner setLookSpinner = (Spinner) prototypeView.findViewById(R.id.brick_set_look_spinner);
		setLookSpinner.setFocusableInTouchMode(false);
		setLookSpinner.setFocusable(false);
		setLookSpinner.setEnabled(false);

		SpinnerAdapter setLookSpinnerAdapter = createLookAdapter(context);
		setLookSpinner.setAdapter(setLookSpinnerAdapter);
		setSpinnerSelection(setLookSpinner);
		return prototypeView;
	}

	@Override
	public Brick clone() {
		SetLookBrick clonedBrick = new SetLookBrick();
		clonedBrick.setLook(look);
		return clonedBrick;
		//test
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.setLook(sprite, look));
		return null;
	}

	private void setSpinnerSelection(Spinner spinner) {
		if (ProjectManager.getInstance().getCurrentSprite().getLookDataList().contains(look)) {
			oldSelectedLook = look;
			spinner.setSelection(ProjectManager.getInstance().getCurrentSprite().getLookDataList().indexOf(look) + 1, true);
		} else {
			if (spinner.getAdapter() != null && spinner.getAdapter().getCount() > 1) {
				if (ProjectManager.getInstance().getCurrentSprite().getLookDataList().indexOf(oldSelectedLook) >= 0) {
					spinner.setSelection(ProjectManager.getInstance().getCurrentSprite().getLookDataList()
							.indexOf(oldSelectedLook) + 1, true);
				} else {
					spinner.setSelection(1, true);
				}
			} else {
				spinner.setSelection(0, true);
			}
		}
	}

	private void setOnLookDataListChangedAfterNewListener(Context context) {
		ScriptActivity scriptActivity = (ScriptActivity) context;
		LookFragment lookFragment = (LookFragment) scriptActivity.getFragment(ScriptActivity.FRAGMENT_LOOKS);
		if (lookFragment != null) {
			lookFragment.setOnLookDataListChangedAfterNewListener(this);
		}
	}

	private class SpinnerAdapterWrapper implements SpinnerAdapter {

		protected Context context;
		protected ArrayAdapter<LookData> spinnerAdapter;

		private boolean isTouchInDropDownView;

		public SpinnerAdapterWrapper(Context context, ArrayAdapter<LookData> spinnerAdapter) {
			this.context = context;
			this.spinnerAdapter = spinnerAdapter;

			this.isTouchInDropDownView = false;
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
			LookData currentLook = spinnerAdapter.getItem(paramInt);
			if (!currentLook.getLookName().equals(context.getString(R.string.new_broadcast_message))) {
				oldSelectedLook = currentLook;
			}
			return spinnerAdapter.getItemId(paramInt);
		}

		@Override
		public boolean hasStableIds() {
			return spinnerAdapter.hasStableIds();
		}

		@Override
		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			if (isTouchInDropDownView) {
				isTouchInDropDownView = false;
				if (paramInt == 0) {
					switchToLookFragmentFromScriptFragment();
				}
			}
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

			dropDownView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
					isTouchInDropDownView = true;
					return false;
				}
			});

			return dropDownView;
		}

		private void switchToLookFragmentFromScriptFragment() {
			ScriptActivity scriptActivity = ((ScriptActivity) context);
			scriptActivity.switchToFragmentFromScriptFragment(ScriptActivity.FRAGMENT_LOOKS);

			setOnLookDataListChangedAfterNewListener(context);
		}
	}

	@Override
	public void onLookDataListChangedAfterNew(LookData lookData) {
		look = lookData;
		oldSelectedLook = lookData;
	}
}
