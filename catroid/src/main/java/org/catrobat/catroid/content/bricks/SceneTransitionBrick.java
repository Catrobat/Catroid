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
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.recyclerview.dialog.NewSceneDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.List;

public class SceneTransitionBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;

	private String sceneForTransition;
	private transient String previouslySelectedScene;

	public SceneTransitionBrick(String scene) {
		this.sceneForTransition = scene;
	}

	@Override
	public Brick clone() {
		return new SceneTransitionBrick(sceneForTransition);
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {

		view = View.inflate(context, R.layout.brick_scene_transition, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);
		setCheckboxView(R.id.brick_scene_transition_checkbox);

		Spinner spinner = view.findViewById(R.id.brick_scene_transition_spinner);
		ArrayAdapter<String> spinnerAdapter = createSpinnerAdapter(context);
		SpinnerAdapterWrapper spinnerAdapterWrapper = new SpinnerAdapterWrapper(spinnerAdapter);

		spinner.setAdapter(spinnerAdapterWrapper);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					sceneForTransition = null;
				} else {
					sceneForTransition = (String) parent.getItemAtPosition(position);
					previouslySelectedScene = sceneForTransition;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		int spinnerPosition = spinnerAdapter.getPosition(sceneForTransition);
		if (spinnerPosition == -1) {
			if (spinnerAdapter.getCount() > 1) {
				spinner.setSelection(1, true);
			} else {
				spinner.setSelection(0, true);
			}
		} else {
			spinner.setSelection(spinnerPosition, true);
		}
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View view = View.inflate(context, R.layout.brick_scene_transition, null);
		Spinner spinner = view.findViewById(R.id.brick_scene_transition_spinner);

		ArrayAdapter<String> spinnerAdapter = createSpinnerAdapter(context);
		spinner.setAdapter(spinnerAdapter);

		int spinnerPosition = spinnerAdapter.getPosition(sceneForTransition);
		if (spinnerPosition == -1) {
			if (spinnerAdapter.getCount() > 1) {
				spinner.setSelection(1, true);
			} else {
				spinner.setSelection(0, true);
			}
		} else {
			spinner.setSelection(spinnerPosition, true);
		}
		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSceneTransitionAction(sceneForTransition));
		return null;
	}

	private ArrayAdapter<String> createSpinnerAdapter(Context context) {
		ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		adapter.add(context.getString(R.string.new_broadcast_message));
		List<String> sceneNames = ProjectManager.getInstance().getCurrentProject().getSceneNames();
		sceneNames.remove(ProjectManager.getInstance().getCurrentScene().getName());

		adapter.addAll(sceneNames);
		return adapter;
	}

	public String getSceneForTransition() {
		return sceneForTransition;
	}

	public void setSceneForTransition(String sceneToTransitionTo) {
		this.sceneForTransition = sceneToTransitionTo;
	}

	private class SpinnerAdapterWrapper implements SpinnerAdapter, NewItemInterface<Scene> {

		protected ArrayAdapter<String> spinnerAdapter;
		private boolean isTouchInDropDownView;

		SpinnerAdapterWrapper(ArrayAdapter<String> spinnerAdapter) {
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
			String currentScene = spinnerAdapter.getItem(paramInt);
			if (!currentScene.equals(spinnerAdapter.getContext().getString(R.string.new_broadcast_message))) {
				previouslySelectedScene = currentScene;
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
					sceneForTransition = previouslySelectedScene;
					showNewSceneDialog((Activity) paramViewGroup.getContext());
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

		@Override
		public boolean isEmpty() {
			return spinnerAdapter.isEmpty();
		}

		private void showNewSceneDialog(Activity activity) {
			NewSceneDialogFragment dialog = new NewSceneDialogFragment(this,
					ProjectManager.getInstance().getCurrentProject());
			dialog.show(activity.getFragmentManager(), NewSceneDialogFragment.TAG);
		}

		@Override
		public void addItem(Scene item) {
			previouslySelectedScene = sceneForTransition;
			sceneForTransition = item.getName();
			spinnerAdapter.notifyDataSetChanged();
		}
	}
}
