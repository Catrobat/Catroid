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
package org.catrobat.catroid.test.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;

import java.io.File;
import java.io.IOException;

public final class TestUtils {

	public static final String DEFAULT_TEST_PROJECT_NAME = "testProject";

	public static final double DELTA = 0.00001;

	private TestUtils() {
		throw new AssertionError();
	}

	public static void deleteProjects(String... projectNames) throws IOException {
		for (String projectName : projectNames) {
			File projectDir = new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName);
			if (projectDir.exists() && projectDir.isDirectory()) {
				StorageOperations.deleteDir(projectDir);
			}
		}
	}

	public static Project createProjectWithLanguageVersion(float catrobatLanguageVersion, String projectName) {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		project.setCatrobatLanguageVersion(catrobatLanguageVersion);

		Sprite firstSprite = new SingleSprite("cat");
		Script testScript = new StartScript();
		Brick testBrick = new HideBrick();
		testScript.addBrick(testBrick);

		firstSprite.addScript(testScript);
		project.getDefaultScene().addSprite(firstSprite);

		XstreamSerializer.getInstance().saveProject(project);
		return project;
	}

	public static void removeFromPreferences(Context context, String key) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = preferences.edit();
		edit.remove(key);
		edit.commit();
	}

	public static Pixmap createRectanglePixmap(int width, int height, Color color) {
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fillRectangle(0, 0, width, height);
		return pixmap;
	}

	public static Project createProjectWithGlobalValues(String name, String spriteName, String valueName, Context context) {
		Project project = new Project(context, name);
		UserList firstUserList = new UserList(valueName);
		UserVariable firstUserVariable = new UserVariable(valueName);

		UserList secondUserList = new UserList(valueName + "_1");
		UserVariable secondUserVariable = new UserVariable(valueName + "_1");

		project.getDefaultScene().getProject().addUserList(firstUserList);
		project.getDefaultScene().getProject().addUserVariable(firstUserVariable);
		project.getDefaultScene().getProject().addUserList(secondUserList);
		project.getDefaultScene().getProject().addUserVariable(secondUserVariable);

		Sprite sprite = new SingleSprite(spriteName);
		Script script = new StartScript();

		SetVariableBrick variableBrick = new SetVariableBrick(new Formula(1), project.getUserVariable(valueName));
		AddItemToUserListBrick listBrick = new AddItemToUserListBrick(new Formula(1));
		script.addBrick(variableBrick);
		script.addBrick(listBrick);

		variableBrick = new SetVariableBrick(new Formula(1), project.getUserVariable(valueName + "_1"));
		listBrick = new AddItemToUserListBrick(new Formula(1));
		script.addBrick(variableBrick);
		script.addBrick(listBrick);

		sprite.addScript(script);
		project.getDefaultScene().getSpriteList().get(0).addScript(script);
		project.getDefaultScene().addSprite(sprite);

		return project;
	}

	public static Project createProjectWithSpriteValues(String name, String spriteName, String valueName, Context context) {
		Project project = new Project(context, name);
		Sprite sprite = new SingleSprite(spriteName);

		project = createSpriteVariableAndList(project, sprite, valueName);
		project = createSpriteVariableAndList(project, sprite, valueName + "_1");

		return project;
	}

	private static Project createSpriteVariableAndList(Project project, Sprite sprite, String name) {
		project.getDefaultScene().addSprite(sprite);

		UserVariable firstVariable = new UserVariable(name);
		UserVariable secondVariable = new UserVariable(name + "_1");

		project.getDefaultScene().getSprite(sprite.getName()).addUserVariable(firstVariable);
		project.getDefaultScene().getSprite(sprite.getName()).addUserVariable(secondVariable);

		SetVariableBrick firstVariableBrick = new SetVariableBrick(new Formula(1), firstVariable);
		SetVariableBrick secondVariableBrick = new SetVariableBrick(new Formula(1), secondVariable);
		AddItemToUserListBrick firstListBrick = new AddItemToUserListBrick(new Formula(1));
		AddItemToUserListBrick secondListBrick = new AddItemToUserListBrick(new Formula(1));

		Script firstScript = new StartScript();
		Script secondScript = new StartScript();

		firstScript.addBrick(firstVariableBrick);
		firstScript.addBrick(firstListBrick);
		secondScript.addBrick(secondVariableBrick);
		secondScript.addBrick(secondListBrick);

		sprite.addScript(secondScript);
		project.getDefaultScene().getSpriteList().get(0).addScript(firstScript);
		project.getDefaultScene().addSprite(sprite);

		return project;
	}
}
