/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.brick;

import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

/**
 * 
 * @author Daniel Burtscher
 * 
 */
public class PlaceAtBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {

	private Solo solo;
	private Project project;
	private PlaceAtBrick placeAtBrick;

	public PlaceAtBrickTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	@Smoke
	public void testPlaceAtBrick() throws InterruptedException {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 5, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 4, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));

		assertEquals("Wrong Brick instance.", projectBrickList.get(1), adapter.getChild(groupCount - 1, 1));

		assertEquals("Wrong Brick instance.", projectBrickList.get(2), adapter.getChild(groupCount - 1, 2));

		assertEquals("Wrong Brick instance.", projectBrickList.get(3), adapter.getChild(groupCount - 1, 3));
		assertNotNull("TextView does not exist", solo.getText(solo.getString(R.string.brick_place_at_x)));

		int xPosition = 987;
		int yPosition = 654;

		UiTestUtils.clickEnterClose(solo, 0, xPosition + "");

		int currentXPosition = (Integer) Reflection.getPrivateField(placeAtBrick, "xPosition");
		assertEquals("Text not updated", xPosition + "", solo.getEditText(0).getText().toString());
		assertEquals("Value in Brick is not updated", xPosition, currentXPosition);

		UiTestUtils.clickEnterClose(solo, 1, yPosition + "");

		int currentYPosition = (Integer) Reflection.getPrivateField(placeAtBrick, "yPosition");
		assertEquals("Text not updated", yPosition + "", solo.getEditText(1).getText().toString());
		assertEquals("Value in Brick is not updated", yPosition, currentYPosition);
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		script.addBrick(new HideBrick(sprite));
		placeAtBrick = new PlaceAtBrick(sprite, 105, 206);
		script.addBrick(placeAtBrick);
		PlaySoundBrick soundBrick = new PlaySoundBrick(sprite);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName("sound.mp3");
		soundInfo.setTitle("sound.mp3");
		soundBrick.setSoundInfo(soundInfo);
		script.addBrick(soundBrick);

		script.addBrick(new SetSizeToBrick(sprite, 80));

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
