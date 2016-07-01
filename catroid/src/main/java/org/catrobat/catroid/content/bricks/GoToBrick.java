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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.controller.BackPackSpriteController;
import org.catrobat.catroid.utils.TextSizeUtil;

import java.util.ArrayList;
import java.util.List;

public class GoToBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;

	private Sprite destinationSprite;
	private transient String oldSelectedObject;
	private String touchPositionLabel;
	private String randomPositionLabel;

	private transient SpinnerAdapterWrapper spinnerAdapterWrapper;
	private int spinnerSelection;

	public GoToBrick() {
		this.spinnerSelection = 0;
		this.oldSelectedObject = "";
	}

	public GoToBrick(Sprite destinationSprite) {
		this.destinationSprite = destinationSprite;
		this.spinnerSelection = 0;
		this.oldSelectedObject = "";
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		return clone();
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_go_to, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		this.touchPositionLabel = context.getString(R.string.brick_go_to_touch_position);
		this.randomPositionLabel = context.getString(R.string.brick_go_to_random_position);

		setCheckboxView(R.id.brick_go_to_checkbox);

		final Spinner goToSpinner = (Spinner) view.findViewById(R.id.brick_go_to_spinner);

		final ArrayAdapter<String> spinnerAdapter = createArrayAdapter(context);

		spinnerAdapterWrapper = new SpinnerAdapterWrapper(context, goToSpinner, spinnerAdapter);

		goToSpinner.setAdapter(spinnerAdapterWrapper);

		goToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String itemSelected = parent.getSelectedItem().toString();

				if (itemSelected.equals(context.getString(R.string.brick_go_to_touch_position))) {
					spinnerSelection = BrickValues.GO_TO_TOUCH_POSITION;
				} else if (itemSelected.equals(context.getString(R.string.brick_go_to_random_position))) {
					spinnerSelection = BrickValues.GO_TO_RANDOM_POSITION;
				} else {
					final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance()
							.getCurrentScene().getSpriteList();

					for (Sprite sprite : spriteList) {
						String spriteName = sprite.getName();
						if (spriteName.equals(itemSelected)) {
							destinationSprite = sprite;
							spinnerSelection = BrickValues.GO_TO_OTHER_SPRITE_POSITION;
							break;
						}
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		setSpinnerSelection(goToSpinner);
		TextSizeUtil.enlargeViewGroup((ViewGroup) view);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_go_to, null);

		Spinner goToSpinner = (Spinner) prototypeView.findViewById(R.id.brick_go_to_spinner);

		SpinnerAdapter goToSpinnerAdapter = createArrayAdapter(context);

		goToSpinner.setAdapter(goToSpinnerAdapter);
		setSpinnerSelection(goToSpinner);

		return prototypeView;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createGoToAction(sprite, destinationSprite, spinnerSelection));

		return null;
	}

	private void setSpinnerSelection(Spinner spinner) {
		final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentScene()
				.getSpriteList();

		if (spinnerSelection == BrickValues.GO_TO_TOUCH_POSITION) {
			spinner.setSelection(0, true);
			oldSelectedObject = touchPositionLabel;
		} else if (spinnerSelection == BrickValues.GO_TO_RANDOM_POSITION) {
			spinner.setSelection(1, true);
			oldSelectedObject = randomPositionLabel;
		} else if (spriteList.contains(destinationSprite)) {
			oldSelectedObject = destinationSprite.getName();
			spinner.setSelection(
					((SpinnerAdapterWrapper) spinner.getAdapter()).getAdapter()
							.getPosition(destinationSprite.getName()), true);
		} else {
			if (oldSelectedObject != null && !oldSelectedObject.equals("")) {
				spinner.setSelection(
						((SpinnerAdapterWrapper) spinner.getAdapter()).getAdapter()
								.getPosition(this.oldSelectedObject), true);
			} else {
				spinner.setSelection(0, true);
			}
		}
	}

	private ArrayAdapter<String> createArrayAdapter(Context context) {
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item);

		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		arrayAdapter.add(context.getString(R.string.brick_go_to_touch_position));
		arrayAdapter.add(context.getString(R.string.brick_go_to_random_position));

		final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentScene()
				.getSpriteList();

		for (Sprite sprite : spriteList) {
			String spriteName = sprite.getName();
			String currentSprite = ProjectManager.getInstance().getCurrentSprite().getName();
			if (!spriteName.equals(currentSprite) && !spriteName.equals(context.getString(R.string.background))) {
				arrayAdapter.add(sprite.getName());
			}
		}

		return arrayAdapter;
	}

	public final class SpinnerAdapterWrapper implements SpinnerAdapter {
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
			String currentItemName = spinnerAdapter.getItem(paramInt);
			if (!(currentItemName.equals(context.getString(R.string.brick_go_to_touch_position))
					|| currentItemName.equals(context.getString(R.string.brick_go_to_random_position)))) {
				oldSelectedObject = currentItemName;
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
			spinnerAdapter = createArrayAdapter(context);
			registerDataSetObserver(currentDataSetObserver);
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

		public ArrayAdapter<String> getAdapter() {
			return spinnerAdapter;
		}
	}

	@Override
	public Brick clone() {
		return new GoToBrick(destinationSprite);
	}

	public Sprite getDestinationSprite() {
		return destinationSprite;
	}

	public void setDestinationSprite(Sprite destinationSprite) {
		this.destinationSprite = destinationSprite;
	}

	@Override
	public void storeDataForBackPack(Sprite sprite) {
		Sprite spriteToRestore = ProjectManager.getInstance().getCurrentSprite();
		Sprite backPackedSprite = BackPackSpriteController.getInstance().backpackHiddenSprite(getDestinationSprite());
		setDestinationSprite(backPackedSprite);
		ProjectManager.getInstance().setCurrentSprite(spriteToRestore);
	}
}
