/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.utils;

import android.content.Context;
import android.os.AsyncTask;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.Translatable;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.commands.CommandFactory;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;

import java.util.ArrayList;
import java.util.List;

public class ProjectTranslationTask extends AsyncTask<Void, Void, Boolean> {

	private final Context context;
	private OnProjectTranslatedListener listener;

	private final ProjectTranslationInvoker projectTranslationInvoker = new ProjectTranslationInvoker();
	private final List<Scene> scenes = new ArrayList<>();
	private final List<Sprite> sprites = new ArrayList<>();
	private final List<UserVariable> userVariables = new ArrayList<>();
	private final List<UserList> userLists = new ArrayList<>();
	private final List<LookData> looks = new ArrayList<>();
	private final List<Formula> formulas = new ArrayList<>();
	private final List<Translatable> translatables = new ArrayList<>();

	public ProjectTranslationTask(Context context, OnProjectTranslatedListener listener) {
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		initProjectLists();
		initCommands();
		projectTranslationInvoker.translate();
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		listener.onProjectTranslated();
	}

	private void initProjectLists() {
		Project project = ProjectManager.getInstance().getCurrentProject();

		userVariables.addAll(project.getProjectVariables());
		userLists.addAll(project.getProjectLists());
		scenes.addAll(project.getSceneList());

		for (Scene scene : project.getSceneList()) {
			sprites.addAll(scene.getSpriteList());
			DataContainer dataContainer = scene.getDataContainer();

			for (Sprite sprite : scene.getSpriteList()) {
				userVariables.addAll(dataContainer.getOrCreateVariableListForSprite(sprite));
				userLists.addAll(dataContainer.getOrCreateUserListForSprite(sprite));
				looks.addAll(sprite.getLookDataList());

				for (Brick brick : sprite.getListWithAllBricks()) {
					if (brick instanceof FormulaBrick) {
						formulas.addAll(((FormulaBrick) brick).getFormulas());
						if (brick instanceof NoteBrick) {
							((NoteBrick) brick).setSprite(sprite);
						}
					}

					if (brick instanceof Translatable) {
						translatables.add((Translatable) brick);
					}
				}
			}
		}
	}

	private void initCommands() {
		projectTranslationInvoker.addCommand(CommandFactory.logCommand(scenes, sprites, looks, userVariables, userLists, translatables));
		projectTranslationInvoker.addCommand(CommandFactory.translateScenesCommand(scenes, context));
		projectTranslationInvoker.addCommand(CommandFactory.translateSpritesCommand(sprites, context));
		projectTranslationInvoker.addCommand(CommandFactory.translateLooksCommand(looks, context));
		projectTranslationInvoker.addCommand(CommandFactory.translateUserVariablesCommand(userVariables, formulas, context));
		projectTranslationInvoker.addCommand(CommandFactory.translateUserListsCommand(userLists, formulas, context));
		projectTranslationInvoker.addCommand(CommandFactory.translateTranslatablesCommand(translatables, context));
	}
}
