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
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.EventWrapper;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.NewOption;
import org.catrobat.catroid.ui.recyclerview.dialog.NewLookDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.ArrayList;
import java.util.List;

public class SetLookBrick extends BrickBaseType implements NewItemInterface<LookData>, BrickSpinner
		.OnItemSelectedListener<LookData> {

	private static final long serialVersionUID = 1L;

	protected LookData look;

	private transient BrickSpinner<LookData> spinner;

	public SetLookBrick() {
	}

	public LookData getLook() {
		return look;
	}

	public void setLook(LookData look) {
		this.look = look;
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		SetLookBrick clone = (SetLookBrick) super.clone();
		clone.spinner = null;
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_set_look;
	}

	@Override
	public View getPrototypeView(Context context) {
		super.getPrototypeView(context);
		return getView(context);
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		onViewCreated(view);

		List<Nameable> items = new ArrayList<>();
		items.add(new NewOption(context.getString(R.string.new_option)));
		items.addAll(getSprite().getLookList());
		spinner = new BrickSpinner<>(R.id.brick_set_look_spinner, view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(look);

		return view;
	}

	protected void onViewCreated(View view) {
		if (getSprite().isBackgroundSprite()) {
			((TextView) view.findViewById(R.id.brick_set_look_text_view)).setText(R.string.brick_set_background);
		}
	}

	@Override
	public void onNewOptionSelected() {
		new NewLookFromBrickDialogFragment(this,
				ProjectManager.getInstance().getCurrentlyEditedScene(),
				getSprite())
				.show(((Activity) view.getContext()).getFragmentManager(), NewLookDialogFragment.TAG);
	}

	@Override
	public void addItem(LookData item) {
		getSprite().getLookList().add(item);
		spinner.add(item);
		spinner.setSelection(item);
	}

	@Override
	public void onStringOptionSelected(String string) {
	}

	@Override
	public void onItemSelected(@Nullable LookData item) {
		look = item;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetLookAction(sprite, look, EventWrapper.NO_WAIT));
		return null;
	}

	protected Sprite getSprite() {
		return ProjectManager.getInstance().getCurrentSprite();
	}

	public static class NewLookFromBrickDialogFragment extends NewLookDialogFragment {

		private SetLookBrick setLookBrick;

		public NewLookFromBrickDialogFragment() {
		}

		public NewLookFromBrickDialogFragment(SetLookBrick setLookBrick, Scene dstScene, Sprite dstSprite) {
			super(setLookBrick, dstScene, dstSprite);
			this.setLookBrick = setLookBrick;
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			super.onCancel(dialog);
			setLookBrick.spinner.setSelection(setLookBrick.look);
		}
	}
}
