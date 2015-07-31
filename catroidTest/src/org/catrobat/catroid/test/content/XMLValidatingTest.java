/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.test.content;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.WhenBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory;
import org.catrobat.catroid.utils.UtilFile;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class XMLValidatingTest extends AndroidTestCase {
	private String testProjectName = "xmlTestProjectName";

	public XMLValidatingTest() throws IOException {
	}

	@Override
	public void tearDown() throws Exception {
		TestUtils.clearProject(testProjectName);
		super.tearDown();
	}

	@Override
	public void setUp() {
		TestUtils.clearProject(testProjectName);
	}

	public void testSerializeProjectWithAllBricks() throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, IOException, JSONException {

		File projectDirectory = new File(Constants.DEFAULT_ROOT + "/" + testProjectName);
		if (projectDirectory.exists()) {
			UtilFile.deleteDirectory(projectDirectory);
		}

		Project project = new Project(getContext(), testProjectName);
		Sprite sprite = new Sprite("testSprite");
		Script startScript = new StartScript();
		Script whenScript = new WhenScript();
		Script broadcastScript = new BroadcastScript("message1");
		sprite.addScript(startScript);
		sprite.addScript(whenScript);
		sprite.addScript(broadcastScript);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(startScript);

		Context context = getContext();
		String[] categories = { context.getString(R.string.category_control),
				context.getString(R.string.category_motion), context.getString(R.string.category_sound),
				context.getString(R.string.category_looks), context.getString(R.string.category_data),
				context.getString(R.string.category_lego_nxt) };

		CategoryBricksFactory brickFactory = new CategoryBricksFactory();
		List<Brick> bricks = new ArrayList<Brick>();
		for (String category : categories) {
			bricks.addAll(brickFactory.getBricks(category, sprite, context));
		}

		for (Brick brick : bricks) {
			if (brick.getClass().equals(WhenBrick.class) || brick.getClass().equals(WhenStartedBrick.class)
					|| brick.getClass().equals(BroadcastReceiverBrick.class)) {
				Log.i("XMLValidationtest", "These bricks are not in the new schema");
			} else {
				startScript.addBrick(brick);
			}
		}

		assertTrue("no bricks added to the start script", startScript.getBrickList().size() > 0);
		StorageHandler.getInstance().saveProject(project);
		// TODO: add XML validation based on xsd
		//	XMLValidationUtil.sendProjectXMLToServerForValidating(project);
	}
}
