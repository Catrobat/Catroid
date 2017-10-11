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

package org.catrobat.catroid.uiespresso.content.brick.stage;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.WhenConditionBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.FileTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.StageMatchers;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;

@RunWith(AndroidJUnit4.class)
public class BroadcastBricksStageTest {

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProjectAndGetStartScriptWithImages("BroadcastBrickTest");
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testBroadcastBricksBasic() {
		byte[] red = {(byte) 237, (byte) 28, (byte) 36, (byte) 255};
		byte[] green = {(byte) 34, (byte) 177, (byte) 76, (byte) 255};

		onView(isFocusable())
				.check(matches(StageMatchers.isColorAtPx(red, 1, 1)));
		onView(isFocusable())
				.perform(click());
		onView(isFocusable())
				.check(matches(StageMatchers.isColorAtPx(green, 1, 1)));
	}

	private Script createProjectAndGetStartScriptWithImages(String projectName) {
		String defaultMessage = "defaultMessage";

		Project project = new Project(null, projectName);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();

		sprite.addScript(script);

		LookData redLookData = new LookData();
		String redImageName = "red_image.bmp";
		redLookData.setLookName(redImageName);
		sprite.getLookDataList().add(redLookData);

		LookData greenLookData = new LookData();
		String greenImageName = "green_image.bmp";
		greenLookData.setLookName(greenImageName);
		sprite.getLookDataList().add(greenLookData);

		script.addBrick(new PlaceAtBrick(0, 0));
		script.addBrick(new SetSizeToBrick(5000));
		Formula condition = new Formula(new FormulaElement(FormulaElement.ElementType.SENSOR,
				Sensors.COLLIDES_WITH_FINGER.name(), null));
		script.addBrick(new WhenConditionBrick(condition));
		script.addBrick(new BroadcastBrick(defaultMessage));
		script.addBrick(new BroadcastReceiverBrick(defaultMessage));
		script.addBrick(new NextLookBrick());
		sprite.addScript(script);

		project.getDefaultScene().addSprite(sprite);

		StorageHandler.getInstance().saveProject(project);
		File redImageFile = FileTestUtils.saveFileToProject(project.getName(), project.getDefaultScene().getName(),
				redImageName,
				org.catrobat.catroid.test.R.raw.red_image, InstrumentationRegistry.getContext(),
				FileTestUtils.FileTypes.IMAGE);

		File greenImageFile = FileTestUtils.saveFileToProject(project.getName(), project.getDefaultScene().getName(),
				greenImageName,
				org.catrobat.catroid.test.R.raw.green_image, InstrumentationRegistry.getContext(),
				FileTestUtils.FileTypes.IMAGE);

		redLookData.setLookFilename(redImageFile.getName());
		greenLookData.setLookFilename(greenImageFile.getName());

		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		return script;
	}
}
