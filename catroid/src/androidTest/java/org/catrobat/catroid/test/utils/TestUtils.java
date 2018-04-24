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

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.util.SparseArray;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.utils.NotificationData;
import org.catrobat.catroid.utils.StatusBarNotificationManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class TestUtils {

	public static final String DEFAULT_TEST_PROJECT_NAME = "testProject";

	public static final double DELTA = 0.00001;

	private TestUtils() {
		throw new AssertionError();
	}

	public static void deleteProjects(String... projectNames) throws IOException {
		for (String projectName : projectNames) {
			File projectDir = new File(Constants.DEFAULT_ROOT_DIRECTORY, projectName);
			if (projectDir.exists() && projectDir.isDirectory()) {
				StorageHandler.deleteDir(projectDir);
			}
		}
	}

	public static Project createTestProjectOnLocalStorageWithCatrobatLanguageVersionAndName(
			float catrobatLanguageVersion, String name) {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), name);
		project.setCatrobatLanguageVersion(catrobatLanguageVersion);

		Sprite firstSprite = new SingleSprite("cat");
		Script testScript = new StartScript();
		Brick testBrick = new HideBrick();
		testScript.addBrick(testBrick);

		firstSprite.addScript(testScript);
		project.getDefaultScene().addSprite(firstSprite);

		StorageHandler.getInstance().saveProject(project);
		return project;
	}

	public static List<Brick> createTestProjectWithWrongIfClauseReferences() {
		ProjectManager projectManager = ProjectManager.getInstance();

		String corruptProjectName = "corruptProject";

		Project project = new Project(InstrumentationRegistry.getTargetContext(), corruptProjectName);
		Sprite firstSprite = new SingleSprite("corruptReferences");

		Script testScript = new StartScript();

		ArrayList<Brick> brickList = new ArrayList<Brick>();

		IfLogicBeginBrick ifBeginBrick = new IfLogicBeginBrick(0);
		IfLogicElseBrick ifElseBrick = new IfLogicElseBrick(ifBeginBrick);
		ifElseBrick.setIfBeginBrick(null);

		IfLogicBeginBrick ifBeginBrickNested = new IfLogicBeginBrick(0);
		//reference shouldn't be null:
		IfLogicElseBrick ifElseBrickNested = new IfLogicElseBrick(ifBeginBrickNested);
		ifElseBrickNested.setIfBeginBrick(null);
		//reference shouldn't be null + wrong ifElseBrickReference:
		IfLogicEndBrick ifEndBrickNested = new IfLogicEndBrick(ifElseBrick, ifBeginBrickNested);
		ifEndBrickNested.setIfBeginBrick(null);

		//reference to wrong ifBegin and ifEnd-Bricks:
		IfLogicEndBrick ifEndBrick = new IfLogicEndBrick(ifElseBrickNested, ifBeginBrickNested);

		brickList.add(ifBeginBrick);
		brickList.add(new ShowBrick());
		brickList.add(ifElseBrick);
		brickList.add(new ComeToFrontBrick());
		brickList.add(ifBeginBrickNested);
		brickList.add(new ComeToFrontBrick());
		brickList.add(ifElseBrickNested);
		brickList.add(new ShowBrick());
		brickList.add(ifEndBrickNested);
		brickList.add(ifEndBrick);

		for (Brick brick : brickList) {
			testScript.addBrick(brick);
		}

		firstSprite.addScript(testScript);

		project.getDefaultScene().addSprite(firstSprite);

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		return brickList;
	}

	public static Project createTestProjectOnLocalStorageWithCatrobatLanguageVersion(float catrobatLanguageVersion) {
		return createTestProjectOnLocalStorageWithCatrobatLanguageVersionAndName(catrobatLanguageVersion,
				DEFAULT_TEST_PROJECT_NAME);
	}

	public static void cancelAllNotifications(Context context) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		@SuppressWarnings("unchecked")
		SparseArray<NotificationData> notificationMap = (SparseArray<NotificationData>) Reflection.getPrivateField(
				StatusBarNotificationManager.class, StatusBarNotificationManager.getInstance(), "notificationDataMap");
		if (notificationMap == null) {
			return;
		}

		for (int i = 0; i < notificationMap.size(); i++) {
			notificationManager.cancel(notificationMap.keyAt(i));
		}

		notificationMap.clear();
	}

	public static void removeFromPreferences(Context context, String key) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = preferences.edit();
		edit.remove(key);
		edit.commit();
	}

	public static Project createProjectWithOldCollisionFormulas(String name, Context context, String firstSprite,
			String secondSprite, String thirdSprite, String collisionTag) {
		Project project = new Project(context, name);
		project.setCatrobatLanguageVersion(0.992f);
		Sprite sprite1 = new Sprite(firstSprite);
		Sprite sprite2 = new Sprite(secondSprite);
		Sprite sprite3 = new Sprite(thirdSprite);

		Script firstScript = new StartScript();

		FormulaElement element1 = new FormulaElement(FormulaElement.ElementType.COLLISION_FORMULA, firstSprite + " "
				+ collisionTag + " " + thirdSprite, null);
		Formula formula1 = new Formula(element1);
		IfLogicBeginBrick ifBrick = new IfLogicBeginBrick(formula1);

		firstScript.addBrick(ifBrick);
		sprite1.addScript(firstScript);

		project.getDefaultScene().addSprite(sprite1);
		project.getDefaultScene().addSprite(sprite2);
		project.getDefaultScene().addSprite(sprite3);

		ProjectManager projectManager = ProjectManager.getInstance();
		projectManager.setCurrentProject(project);
		return project;
	}

	public static Pixmap createRectanglePixmap(int width, int height, Color color) {
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fillRectangle(0, 0, width, height);
		return pixmap;
	}
}
