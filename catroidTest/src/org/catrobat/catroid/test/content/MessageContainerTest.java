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

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MessageContainerTest extends AndroidTestCase {

	private final String projectName1 = "TestProject1";
	private final String projectName2 = "TestProject2";
	private final String projectName3 = "TestProject3";
	private final String broadcastMessage1 = "testBroadcast1";
	private final String broadcastMessage2 = "testBroadcast2";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createTestProjects();
	}

	@Override
	protected void tearDown() {
		TestUtils.deleteTestProjects(projectName1, projectName2, projectName3);
	}

	public void testLoadProject() {
		try {
			ProjectManager.getInstance().loadProject(projectName1, getContext());

			Set<String> keySet = getMessages();
			assertEquals("Broadcast message is not in the message container", true, keySet.contains(broadcastMessage1));
		} catch (ProjectException projectException) {
			fail("Project is not loaded successfully");
		}
	}

	public void testLoadTwoProjects() {
		try {
			ProjectManager.getInstance().loadProject(projectName1, getContext());
			assertTrue("Project1 is loaded successfully", true);
		} catch (ProjectException projectException) {
			fail("Project1 is not loaded successfully");
		}

		Set<String> keySet = getMessages();
		assertEquals("Broadcast message is not in the message container", true, keySet.contains(broadcastMessage1));

		try {
			ProjectManager.getInstance().loadProject(projectName2, getContext());
			assertTrue("Project2 is loaded successfully", true);
		} catch (ProjectException projectException) {
			fail("Project2 is not loaded successfully");
		}

		keySet = getMessages();
		assertEquals("Broadcast message is in the message container", false, keySet.contains(broadcastMessage1));
		assertEquals("Broadcast message is not in the message container", true, keySet.contains(broadcastMessage2));
	}

	public void testLoadCorruptedProjectAndCheckForBackup() {
		try {
			ProjectManager.getInstance().loadProject(projectName1, getContext());
			assertTrue("Project1 is loaded successfully", true);
		} catch (ProjectException projectException) {
			fail("Project1 is not loaded successfully");
		}

		Set<String> keySet = getMessages();
		assertEquals("Broadcast message has the false position", true, keySet.contains(broadcastMessage1));

		try {
			ProjectManager.getInstance().loadProject(projectName3, getContext());
			fail("Project3 should be corrupted");
		} catch (LoadingProjectException expected) {
		} catch (ProjectException projectExceptions) {
			fail("Project corruption test is failed");
		}

		keySet = getMessages();
		assertEquals("Broadcast message is not in the message container", true, keySet.contains(broadcastMessage1));
	}

	private void createTestProjects() {
		Project project1 = new Project(getContext(), projectName1);

		Sprite sprite1 = new Sprite("cat");
		Script script1 = new StartScript();
		BroadcastBrick brick1 = new BroadcastBrick(broadcastMessage1);
		script1.addBrick(brick1);
		sprite1.addScript(script1);

		BroadcastScript broadcastScript1 = new BroadcastScript(broadcastMessage1);
		sprite1.addScript(broadcastScript1);

		project1.addSprite(sprite1);

		StorageHandler.getInstance().saveProject(project1);

		Project project2 = new Project(getContext(), projectName2);

		Sprite sprite2 = new Sprite("cat");
		Script script2 = new StartScript();
		BroadcastBrick brick2 = new BroadcastBrick(broadcastMessage2);
		script2.addBrick(brick2);
		sprite2.addScript(script2);

		BroadcastScript broadcastScript2 = new BroadcastScript(broadcastMessage2);
		sprite2.addScript(broadcastScript2);

		project2.addSprite(sprite2);

		StorageHandler.getInstance().saveProject(project2);
	}

	@SuppressWarnings("unchecked")
	private Set<String> getMessages() {
		return ((Map<String, List<BroadcastScript>>) Reflection.getPrivateField(MessageContainer.class, "receiverMap"))
				.keySet();
	}
}
