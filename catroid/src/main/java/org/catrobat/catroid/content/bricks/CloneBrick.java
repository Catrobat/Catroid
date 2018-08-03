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

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.util.Collections;
import java.util.List;

public class CloneBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;

	private Sprite objectToClone;

	public CloneBrick() {
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_clone;
	}

	@Override
	public View getView(final Context context) {
		super.getView(context);
		setupValueSpinner(context);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View view = super.getPrototypeView(context);
		((Spinner) view.findViewById(R.id.brick_clone_spinner)).setAdapter(getSpinnerArrayAdapter(context));
		return view;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite thisObject, ScriptSequenceAction sequence) {
		Sprite s = (objectToClone != null) ? objectToClone : thisObject;
		sequence.addAction(thisObject.getActionFactory().createCloneAction(s));
		return Collections.emptyList();
	}

	private void setupValueSpinner(final Context context) {
		final Spinner valueSpinner = view.findViewById(R.id.brick_clone_spinner);
		final List<Sprite> spriteList = ProjectManager.getInstance().getCurrentlyEditedScene().getSpriteList();

		ArrayAdapter<String> valueAdapter = getSpinnerArrayAdapter(context);
		valueSpinner.setAdapter(valueAdapter);
		valueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedObject = valueSpinner.getSelectedItem().toString();

				objectToClone = null;
				for (Sprite sprite : spriteList) {
					if (sprite.getName().equals(selectedObject)) {
						objectToClone = sprite;
						break;
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		valueSpinner.setSelection(objectToClone != null ? valueAdapter.getPosition(objectToClone.getName()) : 0, true);
	}

	private ArrayAdapter<String> getSpinnerArrayAdapter(Context context) {
		ArrayAdapter<String> messageAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
		messageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		messageAdapter.add(context.getString(R.string.brick_clone_this));

		final List<Sprite> spriteList = ProjectManager.getInstance().getCurrentlyEditedScene().getSpriteList();

		for (Sprite sprite : spriteList) {
			if (sprite.getName().equals(context.getString(R.string.background))) {
				continue;
			}
			messageAdapter.add(sprite.getName());
		}

		return messageAdapter;
	}
}
