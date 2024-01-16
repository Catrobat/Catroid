/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenBackgroundChangesScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.NewOption;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageAttributes;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

@CatrobatLanguageBrick(command = "When background changes to")
public class WhenBackgroundChangesBrick extends BrickBaseType implements ScriptBrick,
		BrickSpinner.OnItemSelectedListener<LookData>,
		NewItemInterface<LookData>, UpdateableSpinnerBrick, CatrobatLanguageAttributes {

	private static final long serialVersionUID = 1L;

	private WhenBackgroundChangesScript script;

	private transient BrickSpinner<LookData> spinner;

	public WhenBackgroundChangesBrick() {
		this(new WhenBackgroundChangesScript());
	}

	public WhenBackgroundChangesBrick(@NonNull WhenBackgroundChangesScript script) {
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
	public Brick clone() throws CloneNotSupportedException {
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
	public int getPositionInScript() {
		return -1;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_when_background_changes_to;
	}

	@Override
	public View getView(final Context context) {
		super.getView(context);

		List<Nameable> items = new ArrayList<>();
		items.add(new NewOption(context.getString(R.string.new_option)));
		items.addAll(ProjectManager.getInstance().getCurrentlyEditedScene().getBackgroundSprite().getLookList());
		spinner = new BrickSpinner<>(R.id.brick_when_background_spinner, view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(getLook());

		return view;
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
		AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (!(activity instanceof SpriteActivity)) {
			return;
		}
		((SpriteActivity) activity).setCurrentSprite(
				ProjectManager.getInstance().getCurrentlyEditedScene().getBackgroundSprite());
		((SpriteActivity) activity).registerOnNewLookListener(this);
		((SpriteActivity) activity).handleAddBackgroundButton();
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable LookData item) {
		setLook(item);
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		super.setCommentedOut(commentedOut);
		getScript().setCommentedOut(commentedOut);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetLookAction(sprite, getLook()));
	}

	@Override
	public void addItem(LookData item) {
		spinner.add(item);
		spinner.setSelection(item);
	}

	@Override
	public List<Brick> getDragAndDropTargetList() {
		return getScript().getBrickList();
	}

	@Override
	public int getPositionInDragAndDropTargetList() {
		return -1;
	}

	@Override
	public void updateSelectedItem(Context context, int spinnerId, String itemName, int itemIndex) {
		if (spinner != null) {
			spinner.setSelection(itemName);
		}
	}

	@NonNull
	@Override
	public String serializeToCatrobatLanguage(int indentionLevel) {
		StringBuilder catrobatLanguage = getCatrobatLanguageParameterizedCall(indentionLevel, true);
		for (Brick brick : getScript().getBrickList()) {
			catrobatLanguage.append(brick.serializeToCatrobatLanguage(indentionLevel + 1));
		}
		getCatrobatLanguageBodyClose(catrobatLanguage, indentionLevel);
		return catrobatLanguage.toString();
	}

	@Override
	public void appendCatrobatLanguageArguments(StringBuilder brickBuilder) {
		brickBuilder.append("look: (");
		LookData lookData = getLook();
		if (lookData != null) {
			brickBuilder.append(CatrobatLanguageUtils.formatLook(lookData.getName()));
		}
		brickBuilder.append(')');
	}

	@Override
	protected Collection<String> getRequiredArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredArgumentNames());
		requiredArguments.add("look");
		return requiredArguments;
	}
}
