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
import android.content.Intent;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenNfcScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.NewOption;
import org.catrobat.catroid.content.bricks.brickspinner.StringOption;
import org.catrobat.catroid.ui.NfcTagsActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WhenNfcBrick extends ScriptBrickBaseType implements BrickSpinner.OnItemSelectedListener<NfcTagData> {

	private static final long serialVersionUID = 1L;

	private WhenNfcScript script;

	public WhenNfcBrick() {
		this(new WhenNfcScript());
	}

	public WhenNfcBrick(@NonNull WhenNfcScript script) {
		script.setScriptBrick(this);
		commentedOut = script.isCommentedOut();
		this.script = script;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		WhenNfcBrick clone = (WhenNfcBrick) super.clone();
		clone.script = (WhenNfcScript) script.clone();
		clone.script.setScriptBrick(clone);
		return clone;
	}

	@Override
	public Script getScript() {
		return script;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_when_nfc;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		List<Nameable> items = new ArrayList<>();
		items.add(new NewOption(context.getString(R.string.brick_when_nfc_edit_list_of_nfc_tags)));
		items.add(new StringOption(context.getString(R.string.brick_when_nfc_default_all)));
		items.addAll(currentSprite.getNfcTagList());
		BrickSpinner<NfcTagData> spinner = new BrickSpinner<>(R.id.brick_when_nfc_spinner, view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(script.getNfcTag());

		return view;
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
		if (view == null) {
			return;
		}
		Context context = view.getContext();
		if (context != null) {
			context.startActivity(new Intent(context, NfcTagsActivity.class));
		}
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
		script.setMatchAll(true);
		script.setNfcTag(null);
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable NfcTagData item) {
		script.setNfcTag(item);
		script.setMatchAll(false);
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(NFC_ADAPTER);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
	}
}
