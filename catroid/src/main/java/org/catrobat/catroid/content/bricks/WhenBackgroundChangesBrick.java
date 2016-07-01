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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenBackgroundChangesScript;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.ui.fragment.LookFragment.OnLookDataListChangedAfterNewListener;
import org.catrobat.catroid.utils.TextSizeUtil;

import java.util.List;

public class WhenBackgroundChangesBrick extends BrickBaseType implements
		ScriptBrick, OnLookDataListChangedAfterNewListener {
	private static final long serialVersionUID = 1L;
	private transient View prototypeView;
	private transient LookData oldSelectedLook;

	private WhenBackgroundChangesScript script;

	public WhenBackgroundChangesBrick() {
	}

	public WhenBackgroundChangesBrick(WhenBackgroundChangesScript script) {
		this.script = script;
	}

	public void setLook(LookData lookData) {
		getCastedScriptSafe().setLook(lookData);
	}

	public LookData getLook() {
		return getCastedScriptSafe().getLook();
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		WhenBackgroundChangesBrick copyBrick = (WhenBackgroundChangesBrick) clone();
		LookData look = getLook();

		if (look != null && look.isBackpackLookData) {
			copyBrick.setLook(look.clone());
			return copyBrick;
		}

		for (LookData data : sprite.getLookDataList()) {
			if (look != null && data != null && data.getAbsolutePath().equals(look.getAbsolutePath())) {
				copyBrick.setLook(data.clone());
				break;
			}
		}
		copyBrick.getLook().isBackpackLookData = false;
		return copyBrick;
	}

	@Override
	public Script getScriptSafe() {
		if (script == null) {
			this.script = new WhenBackgroundChangesScript();
		}
		return script;
	}

	private WhenBackgroundChangesScript getCastedScriptSafe() {
		return (WhenBackgroundChangesScript) getScriptSafe();
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_when_background_changes_to, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_when_background_checkbox);

		final Spinner lookbrickSpinner = (Spinner) view.findViewById(R.id.brick_when_background_spinner);

		final ArrayAdapter<LookData> spinnerAdapter = createLookAdapter(context);
		SpinnerAdapterWrapper spinnerAdapterWrapper = new SpinnerAdapterWrapper(context, spinnerAdapter);
		lookbrickSpinner.setAdapter(spinnerAdapterWrapper);
		lookbrickSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					setLook(null);
				} else {
					setLook((LookData) parent.getItemAtPosition(position));
					oldSelectedLook = getLook();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		setSpinnerSelection(lookbrickSpinner);
		TextSizeUtil.enlargeViewGroup((ViewGroup) view);

		return view;
	}

	private ArrayAdapter<LookData> createLookAdapter(Context context) {
		ArrayAdapter<LookData> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		LookData dummyLookData = new LookData();
		dummyLookData.setLookName(context.getString(R.string.new_broadcast_message));
		arrayAdapter.add(dummyLookData);
		for (LookData lookData : getSprite().getLookDataList()) {
			arrayAdapter.add(lookData);
		}
		return arrayAdapter;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_when_background_changes_to, null);
		Spinner setLookSpinner = (Spinner) prototypeView.findViewById(R.id.brick_when_background_spinner);

		SpinnerAdapter setLookSpinnerAdapter = createLookAdapter(context);
		setLookSpinner.setAdapter(setLookSpinnerAdapter);
		setSpinnerSelection(setLookSpinner);
		return prototypeView;
	}

	@Override
	public Brick clone() {
		WhenBackgroundChangesBrick clonedBrick = new WhenBackgroundChangesBrick();
		clonedBrick.setLook(getLook());
		return clonedBrick;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetLookAction(sprite, getLook()));
		return null;
	}

	private void setSpinnerSelection(Spinner spinner) {
		if (getSprite().getLookDataList().contains(getLook())) {
			oldSelectedLook = getLook();
			spinner.setSelection(getSprite().getLookDataList().indexOf(getLook()) + 1,
					true);
		} else {
			if (spinner.getAdapter() != null && spinner.getAdapter().getCount() > 1) {
				if (getSprite().getLookDataList().indexOf(oldSelectedLook) >= 0) {
					spinner.setSelection(getSprite().getLookDataList()
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
		setLook(lookData);
		oldSelectedLook = lookData;
	}

	@Override
	public void storeDataForBackPack(Sprite sprite) {
		if (getLook() == null) {
			return;
		}
		setLook(LookController.getInstance().backPackHiddenLook(this.getLook()));
		if (sprite != null && !sprite.getLookDataList().contains(getLook())) {
			sprite.getLookDataList().add(getLook());
		}
	}

	private Sprite getSprite() {
		return ProjectManager.getInstance().getCurrentScene().getSpriteList().get(0);
	}
}
