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
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.brickspinner.SpinnerAdapterWithNewOption;
import org.catrobat.catroid.ui.recyclerview.dialog.NewSpriteDialogWrapper;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.List;

public class PointToBrick extends BrickBaseType implements
		BrickWithSpriteReference,
		SpinnerAdapterWithNewOption.OnNewOptionInDropDownClickListener,
		NewItemInterface<Sprite> {

	private static final long serialVersionUID = 1L;

	private Sprite pointedObject;

	private transient int spinnerSelectionBuffer = 0;
	private transient Spinner spinner;
	private transient SpinnerAdapterWithNewOption spinnerAdapter;

	public PointToBrick() {
	}

	public PointToBrick(Sprite pointedSprite) {
		this.pointedObject = pointedSprite;
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
	public Brick clone() {
		return new PointToBrick(pointedObject);
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {

		view = View.inflate(context, R.layout.brick_point_to, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);
		setCheckboxView(R.id.brick_point_to_checkbox);

		spinner = view.findViewById(R.id.brick_point_to_spinner);
		spinnerAdapter = new SpinnerAdapterWithNewOption(context, getSpriteNames());
		spinnerAdapter.setOnDropDownItemClickListener(this);

		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position != 0) {
					String spriteName = spinnerAdapter.getItem(position);
					pointedObject = ProjectManager.getInstance().getCurrentScene().getSprite(spriteName);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spinner.setSelection(spinnerAdapter.getPosition(pointedObject != null ? pointedObject.getName() : null));
		return view;
	}

	private List<String> getSpriteNames() {
		List<String> spriteNames = ProjectManager.getInstance().getCurrentScene().getSpriteNames();

		spriteNames.remove(ProjectManager.getInstance().getCurrentSprite().getName());
		spriteNames.remove(ProjectManager.getInstance().getCurrentScene().getBackgroundSprite().getName());

		return spriteNames;
	}

	@Override
	public boolean onNewOptionInDropDownClicked(View v) {
		spinnerSelectionBuffer = spinner.getSelectedItemPosition();
		new NewSpriteDialogWrapper(this, ProjectManager.getInstance().getCurrentScene()) {

			@Override
			public void onWorkflowCanceled() {
				super.onWorkflowCanceled();
				spinner.setSelection(spinnerSelectionBuffer);
			}
		}.showDialog(((Activity) v.getContext()).getFragmentManager());
		return false;
	}

	@Override
	public void addItem(Sprite item) {
		ProjectManager.getInstance().getCurrentScene().addSprite(item);
		spinnerAdapter.add(item.getName());
		pointedObject = item;
		spinner.setSelection(spinnerAdapter.getPosition(item.getName()));
	}

	@Override
	public View getPrototypeView(Context context) {
		View view = View.inflate(context, R.layout.brick_point_to, null);
		spinner = view.findViewById(R.id.brick_point_to_spinner);

		spinnerAdapter = new SpinnerAdapterWithNewOption(context, getSpriteNames());
		spinner.setAdapter(spinnerAdapter);
		spinner.setSelection(spinnerAdapter.getPosition(pointedObject != null ? pointedObject.getName() : null));
		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPointToAction(sprite, pointedObject));
		return null;
	}
}
