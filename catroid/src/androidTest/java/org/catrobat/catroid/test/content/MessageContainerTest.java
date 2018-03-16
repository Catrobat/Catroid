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
package org.catrobat.catroid.test.content;

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.TestUtils;

import java.util.List;

public class MessageContainerTest extends AndroidTestCase {

	private final String projectName1 = "TestProject1";
	private final String projectName2 = "TestProject2";
	private final String broadcastMessage1 = "testBroadcast1";
	private final String broadcastMessage2 = "testBroadcast2";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createTestProjects();
		ProjectManager.getInstance().loadProject(projectName1, getContext());
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		ProjectManager.getInstance().setCurrentScene(currentProject.getDefaultScene());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestUtils.deleteTestProjects(projectName1, projectName2);
	}

	public void testLoadProject() {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		List<String> broadcastMessages = currentProject.getBroadcastMessageContainer().getBroadcastMessages();
		assertTrue("Broadcast message is not in the message container", broadcastMessages.contains(broadcastMessage1));
		assertEquals("Message container does not have expected size", 1, broadcastMessages.size());
	}

	public void testLoadTwoProjects() throws CompatibilityProjectException, OutdatedVersionProjectException, LoadingProjectException {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		currentProject.getBroadcastMessageContainer().update();

		ProjectManager.getInstance().loadProject(projectName2, getContext());
		currentProject = ProjectManager.getInstance().getCurrentProject();
		ProjectManager.getInstance().setCurrentScene(currentProject.getDefaultScene());
		List<String> broadcastMessages = currentProject.getBroadcastMessageContainer().getBroadcastMessages();

		assertTrue("Broadcast message is in the message container", !broadcastMessages.contains(broadcastMessage1));
		assertTrue("Broadcast message is not in the message container", broadcastMessages.contains(broadcastMessage2));
	}

	private void createTestProjects() {
		Project project1 = new Project(getContext(), projectName1);

		Sprite sprite1 = new SingleSprite("cat");
		Script script1 = new StartScript();
		BroadcastBrick brick1 = new BroadcastBrick(broadcastMessage1);
		script1.addBrick(brick1);
		sprite1.addScript(script1);

		BroadcastScript broadcastScript1 = new BroadcastScript(broadcastMessage1);
		sprite1.addScript(broadcastScript1);

		project1.getDefaultScene().addSprite(sprite1);

		StorageHandler.getInstance().saveProject(project1);

		Project project2 = new Project(getContext(), projectName2);

		Sprite sprite2 = new SingleSprite("cat");
		Script script2 = new StartScript();
		BroadcastBrick brick2 = new BroadcastBrick(broadcastMessage2);
		script2.addBrick(brick2);
		sprite2.addScript(script2);

		BroadcastScript broadcastScript2 = new BroadcastScript(broadcastMessage2);
		sprite2.addScript(broadcastScript2);

		project2.getDefaultScene().addSprite(sprite2);

		boolean result = StorageHandler.getInstance().saveProject(project2);
		assertTrue("error on saving project", result);
	}
}
