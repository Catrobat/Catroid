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

import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.NewSpriteDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class PointToBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;
	private Sprite pointedObject;
	private transient String oldSelectedObject;

	public PointToBrick(Sprite sprite, Sprite pointedSprite) {
		this.sprite = sprite;
		this.pointedObject = pointedSprite;
		this.oldSelectedObject = "";
	}

	public PointToBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.brick_point_to, null);

		setCheckboxView(R.id.brick_point_to_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		final Spinner spinner = (Spinner) view.findViewById(R.id.brick_point_to_spinner);
		spinner.setFocusableInTouchMode(false);
		spinner.setFocusable(false);
		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			spinner.setClickable(true);
			spinner.setEnabled(true);
		} else {
			spinner.setClickable(false);
			spinner.setEnabled(false);
		}

		final ArrayAdapter<String> spinnerAdapter = getArrayAdapterFromSpriteList(context);

		SpinnerAdapterWrapper spinnerAdapterWrapper = new SpinnerAdapterWrapper(context, spinner, spinnerAdapter);

		spinner.setAdapter(spinnerAdapterWrapper);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String itemSelected = parent.getSelectedItem().toString();

				if (itemSelected.equals(context.getString(R.string.new_broadcast_message))) {
					pointedObject = null;
				} else {
					final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance()
							.getCurrentProject().getSpriteList();

					for (Sprite sprite : spriteList) {
						String spriteName = sprite.getName();
						if (spriteName.equals(itemSelected)) {
							pointedObject = sprite;
							break;
						}
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		setSpinnerSelection(spinner);

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_point_to_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.brick_point_to, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new PointToBrick(sprite, pointedObject);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.pointTo(sprite, pointedObject));
		return null;
	}

	private void setSpinnerSelection(Spinner spinner) {
		final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();

		if (spriteList.contains(pointedObject)) {
			oldSelectedObject = pointedObject.getName();
			spinner.setSelection(
					((SpinnerAdapterWrapper) spinner.getAdapter()).getAdapter().getPosition(pointedObject.getName()),
					true);
		} else {
			if (spinner.getAdapter().getCount() > 1) {
				spinner.setSelection(
						((SpinnerAdapterWrapper) spinner.getAdapter()).getAdapter().getPosition(this.oldSelectedObject),
						true);
			} else {
				spinner.setSelection(0, true);
			}
		}
	}

	private ArrayAdapter<String> getArrayAdapterFromSpriteList(Context context) {
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		arrayAdapter.add(context.getString(R.string.new_broadcast_message));

		final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();

		for (Sprite sprite : spriteList) {
			String spriteName = sprite.getName();
			String temp = this.sprite.getName();
			if (!spriteName.equals(temp) && !spriteName.equals(context.getString(R.string.background))) {
				arrayAdapter.add(sprite.getName());
			}
		}

		return arrayAdapter;
	}

	private class SpinnerAdapterWrapper implements SpinnerAdapter {

		protected Context context;
		protected Spinner spinner;
		protected ArrayAdapter<String> spinnerAdapter;
		private DataSetObserver currentDataSetObserver;

		private boolean isTouchInDropDownView;

		public SpinnerAdapterWrapper(Context context, Spinner spinner, ArrayAdapter<String> spinnerAdapter) {
			this.context = context;
			this.spinner = spinner;
			this.spinnerAdapter = spinnerAdapter;

			this.isTouchInDropDownView = false;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver paramDataSetObserver) {
			currentDataSetObserver = paramDataSetObserver;
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
			String currentSpriteName = spinnerAdapter.getItem(paramInt).toString();
			if (!currentSpriteName.equals(context.getString(R.string.new_broadcast_message))) {
				oldSelectedObject = currentSpriteName;
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
					showNewSpriteDialog();
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
			spinnerAdapter = getArrayAdapterFromSpriteList(context);
			registerDataSetObserver(currentDataSetObserver);
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

		public ArrayAdapter<String> getAdapter() {
			return spinnerAdapter;
		}

		protected void showNewSpriteDialog() {
			NewSpriteDialog dialog = new NewSpriteDialog() {

				@Override
				protected boolean handleOkButton() {
					if (super.handleOkButton()) {
						String newSpriteName = (input.getText().toString()).trim();

						spinnerAdapter.add(newSpriteName);
						oldSelectedObject = newSpriteName;
						setSpinnerSelection(spinner);

						return true;
					}

					return false;
				}

				@Override
				public void onDismiss(DialogInterface dialog) {
					setSpinnerSelection(spinner);
					super.onDismiss(dialog);
				}
			};

			dialog.show(((ScriptActivity) context).getSupportFragmentManager(), NewSpriteDialog.DIALOG_FRAGMENT_TAG);
		}
	}
}
