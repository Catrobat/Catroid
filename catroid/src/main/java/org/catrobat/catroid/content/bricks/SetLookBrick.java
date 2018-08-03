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
import android.widget.Spinner;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.EventWrapper;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.SpinnerAdapterWithNewOption;
import org.catrobat.catroid.ui.recyclerview.dialog.NewLookDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.ArrayList;
import java.util.List;

public class SetLookBrick extends BrickBaseType implements
		SpinnerAdapterWithNewOption.OnNewOptionInDropDownClickListener, NewItemInterface<LookData> {

	private static final long serialVersionUID = 1L;

	protected LookData look;

	private transient int spinnerSelectionBuffer = 0;
	private transient Spinner spinner;
	private transient SpinnerAdapterWithNewOption spinnerAdapter;

	public SetLookBrick() {
	}

	public LookData getLook() {
		return look;
	}

	public void setLook(LookData look) {
		this.look = look;
	}

	private Spinner findSpinner(View view) {
		return view.findViewById(R.id.brick_set_look_spinner);
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		SetLookBrick clone = (SetLookBrick) super.clone();
		clone.spinner = null;
		clone.spinnerAdapter = null;
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_set_look;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		onViewCreated(view);
		spinner = findSpinner(view);
		spinnerAdapter = new SpinnerAdapterWithNewOption(view.getContext(), getLookNames());
		spinnerAdapter.setOnDropDownItemClickListener(this);

		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position != 0) {
					look = getLookByName(spinnerAdapter.getItem(position));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spinner.setSelection(spinnerAdapter.getPosition(look != null ? look.getName() : null));
		return view;
	}

	protected void onViewCreated(View view) {
		if (getSprite().isBackgroundSprite()) {
			((TextView) view.findViewById(R.id.brick_set_look_text_view)).setText(R.string.brick_set_background);
		}
	}

	private LookData getLookByName(String name) {
		for (LookData look : getSprite().getLookList()) {
			if (look.getName().equals(name)) {
				return look;
			}
		}
		return null;
	}

	private List<String> getLookNames() {
		List<String> lookNames = new ArrayList<>();
		for (LookData look : getSprite().getLookList()) {
			lookNames.add(look.getName());
		}
		return lookNames;
	}

	@Override
	public boolean onNewOptionInDropDownClicked(View v) {
		spinnerSelectionBuffer = spinner.getSelectedItemPosition();
		new NewLookDialogFragment(this,
				ProjectManager.getInstance().getCurrentlyEditedScene(),
				ProjectManager.getInstance().getCurrentSprite()) {

			@Override
			public void onCancel(DialogInterface dialog) {
				super.onCancel(dialog);
				spinner.setSelection(spinnerSelectionBuffer);
			}
		}.show(((Activity) v.getContext()).getFragmentManager(), NewLookDialogFragment.TAG);
		return false;
	}

	@Override
	public void addItem(LookData item) {
		ProjectManager.getInstance().getCurrentSprite().getLookList().add(item);
		spinnerAdapter.add(item.getName());
		look = item;
		spinner.setSelection(spinnerAdapter.getPosition(item.getName()));
	}

	@Override
	public View getPrototypeView(Context context) {
		View view = super.getPrototypeView(context);
		onPrototypeViewCreated(view);
		spinner = findSpinner(view);
		spinnerAdapter = new SpinnerAdapterWithNewOption(view.getContext(), getLookNames());
		spinner.setAdapter(spinnerAdapter);
		spinner.setSelection(spinnerAdapter.getPosition(look != null ? look.getName() : null));
		return view;
	}

	protected void onPrototypeViewCreated(View view) {
		if (getSprite().isBackgroundSprite()) {
			((TextView) view.findViewById(R.id.brick_set_look_text_view)).setText(R.string.brick_set_background);
		}
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetLookAction(sprite, look, EventWrapper.NO_WAIT));
		return null;
	}

	protected Sprite getSprite() {
		return ProjectManager.getInstance().getCurrentSprite();
	}
}
