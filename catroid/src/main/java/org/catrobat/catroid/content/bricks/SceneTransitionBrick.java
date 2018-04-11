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
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.brickspinner.SpinnerAdapterWithNewOption;
import org.catrobat.catroid.ui.recyclerview.dialog.NewSceneDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.List;

public class SceneTransitionBrick extends BrickBaseType implements
		SpinnerAdapterWithNewOption.OnNewOptionInDropDownClickListener,
		NewItemInterface<Scene> {

	private static final long serialVersionUID = 1L;

	private String sceneForTransition;

	private transient int spinnerSelectionBuffer = 0;
	private transient Spinner spinner;
	private transient SpinnerAdapterWithNewOption spinnerAdapter;

	public SceneTransitionBrick(String scene) {
		this.sceneForTransition = scene;
	}

	public String getSceneForTransition() {
		return sceneForTransition;
	}

	public void setSceneForTransition(String sceneForTransition) {
		this.sceneForTransition = sceneForTransition;
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

		spinner = view.findViewById(R.id.brick_scene_transition_spinner);

		List<String> sceneNames = ProjectManager.getInstance().getCurrentProject().getSceneNames();
		sceneNames.remove(ProjectManager.getInstance().getCurrentScene().getName());

		spinnerAdapter = new SpinnerAdapterWithNewOption(context, sceneNames);
		spinnerAdapter.setOnDropDownItemClickListener(this);

		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position != 0) {
					sceneForTransition = spinnerAdapter.getItem(position);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spinner.setSelection(spinnerAdapter.getPosition(sceneForTransition));
		return view;
	}

	@Override
	public boolean onNewOptionInDropDownClicked(View v) {
		spinnerSelectionBuffer = spinner.getSelectedItemPosition();
		new NewSceneDialogFragment(this, ProjectManager.getInstance().getCurrentProject()) {

			@Override
			public void onCancel(DialogInterface dialog) {
				super.onCancel(dialog);
				spinner.setSelection(spinnerSelectionBuffer);
			}
		}.show(((Activity) v.getContext()).getFragmentManager(), NewSceneDialogFragment.TAG);
		return false;
	}

	@Override
	public void addItem(Scene item) {
		ProjectManager.getInstance().getCurrentProject().addScene(item);
		spinnerAdapter.add(item.getName());
		sceneForTransition = item.getName();
		spinner.setSelection(spinnerAdapter.getPosition(item.getName()));
	}

	@Override
	public View getPrototypeView(Context context) {
		View view = View.inflate(context, R.layout.brick_scene_transition, null);
		spinner = view.findViewById(R.id.brick_scene_transition_spinner);

		List<String> sceneNames = ProjectManager.getInstance().getCurrentProject().getSceneNames();
		sceneNames.remove(ProjectManager.getInstance().getCurrentScene().getName());

		spinnerAdapter = new SpinnerAdapterWithNewOption(context, sceneNames);
		spinner.setAdapter(spinnerAdapter);
		spinner.setSelection(spinnerAdapter.getPosition(sceneForTransition));
		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSceneTransitionAction(sceneForTransition));
		return null;
	}
}
