/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.catroid.test.content.messagecontainer;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.io.asynctask.ProjectSaverKt.saveProjectSerial;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class MessageContainerTest {

	private final String projectName1 = "TestProject1";
	private final String projectName2 = "TestProject2";
	private final String broadcastMessage1 = "testBroadcast1";
	private final String broadcastMessage2 = "testBroadcast2";

	private Project project1;
	private Project project2;

	@Before
	public void setUp() throws Exception {
		createTestProjects();
	}

	@After
	public void tearDown() throws Exception {
		StorageOperations.deleteDir(new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName1));
		StorageOperations.deleteDir(new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName2));
	}

	@Test
	public void testLoadProject() {
		List<String> broadcastMessages = ProjectManager.getInstance().getCurrentProject()
				.getBroadcastMessageContainer().getBroadcastMessages();

		assertThat(broadcastMessages, hasItem(broadcastMessage1));
		assertEquals(1, broadcastMessages.size());
	}

	@Test
	public void testLoadTwoProjects() throws ProjectException {

		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		currentProject.getBroadcastMessageContainer().update();

		ProjectManager.getInstance()
				.loadProject(project2.getDirectory(), ApplicationProvider.getApplicationContext());

		currentProject = ProjectManager.getInstance().getCurrentProject();
		ProjectManager.getInstance().setCurrentlyEditedScene(currentProject.getDefaultScene());
		List<String> broadcastMessages = currentProject.getBroadcastMessageContainer().getBroadcastMessages();

		assertThat(broadcastMessages, not(hasItem(broadcastMessage1)));
		assertThat(broadcastMessages, hasItem(broadcastMessage2));
		assertEquals(1, broadcastMessages.size());
	}

	private void createTestProjects() throws ProjectException {
		project1 = new Project(ApplicationProvider.getApplicationContext(), projectName1);

		Sprite sprite1 = new Sprite("cat");
		Script script1 = new StartScript();
		BroadcastBrick brick1 = new BroadcastBrick(broadcastMessage1);
		script1.addBrick(brick1);
		sprite1.addScript(script1);

		BroadcastScript broadcastScript1 = new BroadcastScript(broadcastMessage1);
		sprite1.addScript(broadcastScript1);

		project1.getDefaultScene().addSprite(sprite1);
		saveProjectSerial(project1, ApplicationProvider.getApplicationContext());

		project2 = new Project(ApplicationProvider.getApplicationContext(), projectName2);

		Sprite sprite2 = new Sprite("cat");
		Script script2 = new StartScript();
		BroadcastBrick brick2 = new BroadcastBrick(broadcastMessage2);
		script2.addBrick(brick2);
		sprite2.addScript(script2);

		BroadcastScript broadcastScript2 = new BroadcastScript(broadcastMessage2);
		sprite2.addScript(broadcastScript2);

		project2.getDefaultScene().addSprite(sprite2);
		XstreamSerializer.getInstance().saveProject(project2);

		ProjectManager.getInstance()
				.loadProject(new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName2),
						ApplicationProvider.getApplicationContext());
	}
}
