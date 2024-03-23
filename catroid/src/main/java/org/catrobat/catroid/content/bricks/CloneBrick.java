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
import org.catrobat.catroid.content.bricks.brickspinner.StringOption;
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParserUtils;
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@CatrobatLanguageBrick(command = "Create clone of")
public class CloneBrick extends BrickBaseType implements BrickSpinner.OnItemSelectedListener<Sprite> {

	private static final long serialVersionUID = 1L;

	private static final String ACTOR_OR_OBJECT_CATLANG_PARAMETER_NAME = "actor or object";

	private Sprite objectToClone;
	private transient BrickSpinner<Sprite> spinner;

	public CloneBrick() {
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_clone;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		List<Nameable> items = new ArrayList<>();
		items.add(new StringOption(context.getString(R.string.brick_clone_this)));
		items.addAll(ProjectManager.getInstance().getCurrentlyEditedScene().getSpriteList());
		items.remove(ProjectManager.getInstance().getCurrentlyEditedScene().getBackgroundSprite());
		items.remove(ProjectManager.getInstance().getCurrentSprite());

		spinner = new BrickSpinner<>(R.id.brick_clone_spinner, view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(objectToClone);

		return view;
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
		objectToClone = null;
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable Sprite item) {
		objectToClone = item;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		Sprite s = (objectToClone != null) ? objectToClone : sprite;
		sequence.addAction(sprite.getActionFactory().createCloneAction(s));
	}

	public Sprite getSelectedItem() {
		return objectToClone;
	}

	public void resetSpinner() {
		spinner.setSelection(0);
		objectToClone = null;
	}

	@Override
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if(name.equals(ACTOR_OR_OBJECT_CATLANG_PARAMETER_NAME)) {
			String currentObject = "yourself";
			if (objectToClone != null) {
				currentObject = CatrobatLanguageUtils.formatActorOrObject(objectToClone.getName());
			}
			return CatrobatLanguageUtils.getCatlangArgumentTuple(name, currentObject);
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
		if (!spriteName.equals("yourself")) {
			spriteName = CatrobatLanguageParserUtils.Companion.getAndValidateStringContent(spriteName);
			objectToClone = scene.getSprite(spriteName);
			if (objectToClone == null) {
				throw new CatrobatLanguageParsingException("No sprite with name " + spriteName + " found");
			}
		}
	}
}
