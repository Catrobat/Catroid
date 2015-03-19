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

package org.catrobat.catroid.ui.bricks;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.fragment.LookFragment;

/**
 * Brick View Factory for SetLookBrick.
 * Created by IllyaBoyko on 12/03/15.
 */
public class SetLookBrickViewProvider extends BrickViewProvider {
	public SetLookBrickViewProvider(Context context, LayoutInflater inflater) {
		super(context, inflater);
	}

	public View createSetLookBrickView(final SetLookBrick brick, ViewGroup parent) {
		View view = inflateBrickView(parent, R.layout.brick_set_look);

		final Spinner lookbrickSpinner = (Spinner) view.findViewById(R.id.brick_set_look_spinner);
		lookbrickSpinner.setFocusableInTouchMode(false);
		lookbrickSpinner.setFocusable(false);

		SpinnerAdapter spinnerAdapterWrapper = new SpinnerAdapter() {

			protected ArrayAdapter<LookData> spinnerAdapter = createLookAdapter(context);

			private boolean isTouchInDropDownView = false;

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
					brick.setOldSelectedLook(currentLook);
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

				dropDownView.setOnTouchListener(new View.OnTouchListener() {
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

				setOnLookDataListChangedAfterNewListener(context, brick);
			}
		};

		lookbrickSpinner.setAdapter(spinnerAdapterWrapper);

		lookbrickSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					brick.setLook(null);
				} else {
					LookData look = (LookData) parent.getItemAtPosition(position);
					brick.setLook(look);
					brick.setOldSelectedLook(look);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		setSpinnerSelection(lookbrickSpinner, brick);

		if (ProjectManager.getInstance().getCurrentSprite().getName().equals(context.getString(R.string.background))) {
			((TextView) view.findViewById(R.id.brick_set_look_label)).setText(R.string.brick_set_background);
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


	private void setSpinnerSelection(Spinner spinner, SetLookBrick brick) {
		if (ProjectManager.getInstance().getCurrentSprite().getLookDataList().contains(brick.getLook())) {
			brick.setOldSelectedLook(brick.getLook());
			spinner.setSelection(ProjectManager.getInstance().getCurrentSprite().getLookDataList().indexOf(brick.getLook()) + 1, true);
		} else {
			if (spinner.getAdapter() != null && spinner.getAdapter().getCount() > 1) {
				if (ProjectManager.getInstance().getCurrentSprite().getLookDataList().indexOf(brick.getOldSelectedLook()) >= 0) {
					spinner.setSelection(ProjectManager.getInstance().getCurrentSprite().getLookDataList()
							.indexOf(brick.getOldSelectedLook()) + 1, true);
				} else {
					spinner.setSelection(1, true);
				}
			} else {
				spinner.setSelection(0, true);
			}
		}
	}

	private void setOnLookDataListChangedAfterNewListener(Context context, final SetLookBrick brick) {
		ScriptActivity scriptActivity = (ScriptActivity) context;
		LookFragment lookFragment = (LookFragment) scriptActivity.getFragment(ScriptActivity.FRAGMENT_LOOKS);
		if (lookFragment != null) {
			lookFragment.setOnLookDataListChangedAfterNewListener(new LookFragment.OnLookDataListChangedAfterNewListener() {
				@Override
				public void onLookDataListChangedAfterNew(LookData lookData) {
					brick.setLook(lookData);
					brick.setOldSelectedLook(lookData);
				}
			});
		}
	}

}
