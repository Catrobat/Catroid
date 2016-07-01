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

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.NewSceneDialog;
import org.catrobat.catroid.ui.dialogs.NewSpriteDialog;
import org.catrobat.catroid.utils.TextSizeUtil;

import java.util.ArrayList;
import java.util.List;

public class SceneTransitionBrick extends BrickBaseType implements NewSceneDialog.OnNewSceneListener {
	private static final long serialVersionUID = 1L;

	private String sceneForTransition;
	private transient String sceneContainingBrick = null;
	private transient String oldSelectedScene;

	public SceneTransitionBrick(String scene) {
		this.sceneForTransition = scene;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		SceneTransitionBrick copyBrick = (SceneTransitionBrick) clone();
		return copyBrick;
	}

	@Override
	public Brick clone() {
		return new SceneTransitionBrick(sceneForTransition);
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_scene_transition, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);
		setCheckboxView(R.id.brick_scene_transition_checkbox);

		final Spinner sceneSpinner = (Spinner) view.findViewById(R.id.brick_scene_transition_spinner);

		final ArrayAdapter<String> spinnerAdapter = createSceneAdapter(context);

		SpinnerAdapterWrapper spinnerAdapterWrapper = new SpinnerAdapterWrapper(context, spinnerAdapter);

		sceneSpinner.setAdapter(spinnerAdapterWrapper);

		sceneSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					sceneForTransition = null;
				} else {
					sceneForTransition = (String) parent.getItemAtPosition(position);
					oldSelectedScene = sceneForTransition;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		setSpinnerSelection(sceneSpinner);
		TextSizeUtil.enlargeViewGroup((ViewGroup) view);
		return view;
	}

	public void setSpinnerSelection() {
		try {
			final Spinner sceneSpinner = (Spinner) view.findViewById(R.id.brick_scene_transition_spinner);
			setSpinnerSelection(sceneSpinner);
		} catch (Exception e) {
			//Since this can happen quite often, we do not want to print an error to prevent Log spamming
			return;
		}
	}

	private void setSpinnerSelection(Spinner spinner) {
		List<String> sceneList = new ArrayList<>();
		sceneList.addAll(ProjectManager.getInstance().getCurrentProject().getSceneOrder());
		sceneList.remove(ProjectManager.getInstance().getCurrentScene().getName());

		if (sceneList.contains(sceneForTransition)) {
			oldSelectedScene = sceneForTransition;
			int pos = sceneList.indexOf(sceneForTransition) + 1;
			if (pos >= spinner.getCount()) {
				pos--;
			}
			spinner.setSelection(pos, true);
		} else {
			if (spinner.getAdapter() != null && spinner.getAdapter().getCount() > 1) {
				if (sceneList.indexOf(oldSelectedScene) >= 0) {
					spinner.setSelection(sceneList.indexOf(oldSelectedScene) + 1, true);
				} else {
					spinner.setSelection(1, true);
				}
			} else {
				spinner.setSelection(0, true);
			}
		}
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_scene_transition, null);
		Spinner sceneSpinner = (Spinner) prototypeView.findViewById(R.id.brick_scene_transition_spinner);

		SpinnerAdapter sceneSpinnerAdapter = createSceneAdapter(context);
		sceneSpinner.setAdapter(sceneSpinnerAdapter);
		setSpinnerSelection(sceneSpinner);
		return prototypeView;
	}

	private void setOnNewSceneListener(NewSceneDialog dialog) {
		if (dialog != null) {
			dialog.setOnNewSceneListener(this);
		}
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSceneTransitionAction(sceneForTransition));
		return null;
	}

	private ArrayAdapter<String> createSceneAdapter(Context context) {
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		String dummyScene = context.getString(R.string.new_broadcast_message);
		arrayAdapter.add(dummyScene);

		boolean currentScene = false;
		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			if (sceneContainingBrick == null) {
				currentScene = scene.getName().equals(ProjectManager.getInstance().getCurrentScene().getName());
			} else {
				currentScene = sceneContainingBrick.equals(scene.getName());
				ProjectManager.getInstance().setCurrentScene(ProjectManager.getInstance().getCurrentProject()
						.getSceneByName(sceneContainingBrick));
				sceneContainingBrick = null;
			}
			if (currentScene) {
				continue;
			}
			arrayAdapter.add(scene.getName());
		}

		return arrayAdapter;
	}

	@Override
	public void onNewScene(Scene scene) {
		sceneContainingBrick = ProjectManager.getInstance().getCurrentScene().getName();
		oldSelectedScene = this.sceneForTransition;
		this.sceneForTransition = scene.getName();
	}

	public String getSceneForTransition() {
		return sceneForTransition;
	}

	public void setSceneForTransition(String sceneForTransition) {
		this.sceneForTransition = sceneForTransition;
	}

	private class SpinnerAdapterWrapper implements SpinnerAdapter {

		protected Context context;
		protected ArrayAdapter<String> spinnerAdapter;

		private boolean isTouchInDropDownView;

		public SpinnerAdapterWrapper(Context context, ArrayAdapter<String> spinnerAdapter) {
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
			String currentScene = spinnerAdapter.getItem(paramInt);
			if (!currentScene.equals(context.getString(R.string.new_broadcast_message))) {
				oldSelectedScene = currentScene;
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
					switchToNewSceneDialogFromScriptFragment();
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

		private void switchToNewSceneDialogFromScriptFragment() {
			ScriptActivity activity = ((ScriptActivity) context);
			FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
			Fragment previousFragment;

			previousFragment = activity.getFragmentManager().findFragmentByTag(NewSpriteDialog.DIALOG_FRAGMENT_TAG);
			if (previousFragment != null) {
				fragmentTransaction.remove(previousFragment);
			}
			NewSceneDialog newSceneDialog = new NewSceneDialog(true, false);
			setOnNewSceneListener(newSceneDialog);
			newSceneDialog.show(fragmentTransaction, NewSpriteDialog.DIALOG_FRAGMENT_TAG);
		}
	}
}
