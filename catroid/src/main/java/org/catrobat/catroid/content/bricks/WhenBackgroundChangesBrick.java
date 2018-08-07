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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenBackgroundChangesScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.SpinnerWithNewOption;
import org.catrobat.catroid.ui.recyclerview.dialog.NewLookDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.List;

import javax.annotation.Nonnull;

public class WhenBackgroundChangesBrick extends BrickBaseType implements ScriptBrick, NewItemInterface<LookData>,
		SpinnerWithNewOption.SpinnerSelectionListener<LookData> {

	private static final long serialVersionUID = 1L;

	private WhenBackgroundChangesScript script;

	private transient SpinnerWithNewOption<LookData> spinner;

	public WhenBackgroundChangesBrick() {
		this(new WhenBackgroundChangesScript());
	}

	public WhenBackgroundChangesBrick(@Nonnull WhenBackgroundChangesScript script) {
		script.setScriptBrick(this);
		commentedOut = script.isCommentedOut();
		this.script = script;
	}

	public LookData getLook() {
		return script.getLook();
	}

	public void setLook(LookData look) {
		script.setLook(look);
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		WhenBackgroundChangesBrick clone = (WhenBackgroundChangesBrick) super.clone();
		clone.script = (WhenBackgroundChangesScript) script.clone();
		clone.script.setScriptBrick(clone);
		clone.spinner = null;
		return clone;
	}

	@Override
	public Script getScript() {
		return script;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_when_background_changes_to;
	}

	@Override
	public View getView(final Context context) {
		super.getView(context);
		spinner = new SpinnerWithNewOption<>(R.id.brick_when_background_spinner, view,
				ProjectManager.getInstance().getCurrentSprite().getLookList(), this);
		spinner.setSelection(getLook());
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		super.getPrototypeView(context);
		return getView(context);
	}

	@Override
	public boolean onNewOptionClicked() {
		new NewLookDialogFragment(this,
				ProjectManager.getInstance().getCurrentlyEditedScene(),
				ProjectManager.getInstance().getCurrentSprite()) {

			@Override
			public void onCancel(DialogInterface dialog) {
				super.onCancel(dialog);
				spinner.setSelection(getLook());
			}
		}.show(((Activity) view.getContext()).getFragmentManager(), NewLookDialogFragment.TAG);
		return false;
	}

	@Override
	public void addItem(LookData item) {
		ProjectManager.getInstance().getCurrentSprite().getLookList().add(item);
		setLook(item);
		spinner.add(item);
	}

	@Override
	public void onItemSelected(LookData item) {
		setLook(item);
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetLookAction(sprite, getLook()));
		return null;
	}
}
