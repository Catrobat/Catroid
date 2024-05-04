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
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.NewOption;
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.CatrobatLanguageUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

@CatrobatLanguageBrick(command = "Point towards")
public class PointToBrick extends BrickBaseType implements BrickSpinner.OnItemSelectedListener<Sprite>,
		NewItemInterface<Sprite> {

	private static final long serialVersionUID = 1L;

	private static final String ACTOR_OR_OBJECT_CATLANG_PARAMETER_NAME = "actor or object";

	private Sprite pointedObject;

	private transient BrickSpinner<Sprite> spinner;

	public PointToBrick() {
	}

	public PointToBrick(Sprite pointedSprite) {
		this.pointedObject = pointedSprite;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		PointToBrick clone = (PointToBrick) super.clone();
		clone.spinner = null;
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_point_to;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		List<Nameable> items = new ArrayList<>();
		items.add(new NewOption(context.getString(R.string.new_option)));
		items.addAll(ProjectManager.getInstance().getCurrentlyEditedScene().getSpriteList());
		items.remove(ProjectManager.getInstance().getCurrentlyEditedScene().getBackgroundSprite());
		items.remove(ProjectManager.getInstance().getCurrentSprite());

		spinner = new BrickSpinner<>(R.id.brick_point_to_spinner, view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(pointedObject);

		return view;
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
		AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (!(activity instanceof SpriteActivity)) {
			return;
		}
		((SpriteActivity) activity).registerOnNewSpriteListener(this);
		((SpriteActivity) activity).handleAddSpriteButton();
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
	}

	@Override
	public void addItem(Sprite item) {
		spinner.add(item);
		spinner.setSelection(item);
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable Sprite item) {
		pointedObject = item;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPointToAction(sprite, pointedObject));
	}

	@Override
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if (name.equals(ACTOR_OR_OBJECT_CATLANG_PARAMETER_NAME)) {
			String actorOrObjectName = "";
			if (pointedObject != null) {
				actorOrObjectName = CatrobatLanguageUtils.formatActorOrObject(pointedObject.getName());
			}
			return CatrobatLanguageUtils.getCatlangArgumentTuple(name, actorOrObjectName);
		}
		return super.getArgumentByCatlangName(name);
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredCatlangArgumentNames());
		requiredArguments.add(ACTOR_OR_OBJECT_CATLANG_PARAMETER_NAME);
		return requiredArguments;
	}

	@Override
	public void setParameters(@NonNull Context context, @NonNull Project project, @NonNull Scene scene, @NonNull Sprite sprite, @NonNull Map<String, String> arguments) throws CatrobatLanguageParsingException {
		super.setParameters(context, project, scene, sprite, arguments);
		String spriteName = arguments.get(ACTOR_OR_OBJECT_CATLANG_PARAMETER_NAME);
		spriteName = CatrobatLanguageUtils.getAndValidateStringContent(spriteName);
		pointedObject = scene.getSprite(spriteName);
		if (pointedObject == null) {
			throw new CatrobatLanguageParsingException("No sprite found with name " + arguments.get(ACTOR_OR_OBJECT_CATLANG_PARAMETER_NAME) + " in scene " + scene.getName());
		}
	}
}
