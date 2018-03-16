/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.app.Activity;
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
import org.catrobat.catroid.content.GroupSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.recyclerview.dialog.NewSpriteDialogWrapper;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.ArrayList;
import java.util.List;

public class PointToBrick extends BrickBaseType implements BrickWithSpriteReference {

	private static final long serialVersionUID = 1L;

	private Sprite pointedObject;
	private transient String oldSelectedObject;

	private transient SpinnerAdapterWrapper spinnerAdapterWrapper;

	public PointToBrick(Sprite pointedSprite) {
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

		view = View.inflate(context, R.layout.brick_point_to, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_point_to_checkbox);
		final Spinner spinner = (Spinner) view.findViewById(R.id.brick_point_to_spinner);

		final ArrayAdapter<String> spinnerAdapter = getArrayAdapterFromSpriteList(context);

		spinnerAdapterWrapper = new SpinnerAdapterWrapper(context, spinner, spinnerAdapter);

		spinner.setAdapter(spinnerAdapterWrapper);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String itemSelected = parent.getSelectedItem().toString();

				if (itemSelected.equals(context.getString(R.string.new_broadcast_message))) {
					pointedObject = null;
				} else {
					final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance()
							.getCurrentScene().getSpriteList();

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
	public View getPrototypeView(Context context) {
		View view = View.inflate(context, R.layout.brick_point_to, null);
		Spinner pointToSpinner = (Spinner) view.findViewById(R.id.brick_point_to_spinner);

		SpinnerAdapter pointToSpinnerAdapter = getArrayAdapterFromSpriteList(context);
		pointToSpinner.setAdapter(pointToSpinnerAdapter);
		setSpinnerSelection(pointToSpinner);
		return view;
	}

	@Override
	public Brick clone() {
		return new PointToBrick(pointedObject);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPointToAction(sprite, pointedObject));
		return null;
	}

	private void setSpinnerSelection(Spinner spinner) {
		final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentScene()
				.getSpriteList();

		if (spriteList.contains(pointedObject)) {
			oldSelectedObject = pointedObject.getName();
			spinner.setSelection(
					((SpinnerAdapterWrapper) spinner.getAdapter()).getAdapter().getPosition(pointedObject.getName()),
					true);
		} else {
			if (oldSelectedObject != null && !oldSelectedObject.equals("")) {
				spinner.setSelection(
						((SpinnerAdapterWrapper) spinner.getAdapter()).getAdapter().getPosition(this.oldSelectedObject),
						true);
			} else {
				if (spinner.getAdapter().getCount() > 1) {
					spinner.setSelection(1, true);
				} else {
					spinner.setSelection(0, true);
				}
			}
		}
	}

	private ArrayAdapter<String> getArrayAdapterFromSpriteList(Context context) {
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,
				android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		arrayAdapter.add(context.getString(R.string.new_broadcast_message));

		final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentScene()
				.getSpriteList();

		for (Sprite sprite : spriteList) {
			String spriteName = sprite.getName();
			String temp = ProjectManager.getInstance().getCurrentSprite().getName();
			if (!spriteName.equals(temp) && !spriteName.equals(context.getString(R.string.background)) && !(sprite instanceof GroupSprite)) {
				arrayAdapter.add(sprite.getName());
			}
		}

		return arrayAdapter;
	}

	public final class SpinnerAdapterWrapper implements SpinnerAdapter, NewItemInterface<Sprite> {

		protected Context context;
		protected Spinner spinner;
		protected ArrayAdapter<String> spinnerAdapter;
		private DataSetObserver currentDataSetObserver;

		private boolean isTouchInDropDownView;

		private SpinnerAdapterWrapper(Context context, Spinner spinner, ArrayAdapter<String> spinnerAdapter) {
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
			String currentSpriteName = spinnerAdapter.getItem(paramInt);
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

		private void showNewSpriteDialog() {
			NewSpriteDialogWrapper dialogWrapper = new NewSpriteDialogWrapper(
					this, ProjectManager.getInstance().getCurrentScene());
			dialogWrapper.showDialog(((Activity) context).getFragmentManager());
		}

		@Override
		public void addItem(Sprite item) {
			ProjectManager.getInstance().getCurrentScene().addSprite(item);
			pointedObject = item;
			spinnerAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public Sprite getSprite() {
		return pointedObject;
	}

	@Override
	public void setSprite(Sprite sprite) {
		this.pointedObject = sprite;
	}

	@Override
	public void storeDataForBackPack(Sprite sprite) {
	}
}
