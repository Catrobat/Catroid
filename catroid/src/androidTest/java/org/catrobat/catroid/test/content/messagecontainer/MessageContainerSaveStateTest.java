/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class MessageContainerSaveStateTest {

	private final String projectName1 = "TestProject1";
	private final String broadcastMessage1 = "testBroadcast1";
	private final String unusedMessage = "Unused Message";

	@Before
	public void setUp() throws Exception {
		createTestProjects();
	}

	@After
	public void tearDown() throws Exception {
		StorageOperations.deleteDir(new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName1));
	}

	@Test
	public void testDoNotSaveUnusedMessages() {
		List<String> broadcastMessages = ProjectManager.getInstance().getCurrentProject()
				.getBroadcastMessageContainer().getBroadcastMessages();

		assertThat(broadcastMessages, hasItem(broadcastMessage1));
		assertThat(broadcastMessages, not(hasItem(unusedMessage)));

		assertEquals(1, broadcastMessages.size());
	}

	private void createTestProjects() throws ProjectException {
		Project project1 = new Project(ApplicationProvider.getApplicationContext(), projectName1);

		Sprite sprite1 = new Sprite("cat");
		Script script1 = new StartScript();
		BroadcastBrick brick1 = new BroadcastBrick(broadcastMessage1);
		script1.addBrick(brick1);
		sprite1.addScript(script1);

		BroadcastScript broadcastScript1 = new BroadcastScript(broadcastMessage1);
		sprite1.addScript(broadcastScript1);

		project1.getDefaultScene().addSprite(sprite1);
		project1.getBroadcastMessageContainer().addBroadcastMessage(unusedMessage);

		XstreamSerializer.getInstance().saveProject(project1);

		ProjectManager.getInstance()
				.loadProject(project1.getDirectory(), ApplicationProvider.getApplicationContext());

		ProjectManager.getInstance()
				.setCurrentlyEditedScene(ProjectManager.getInstance().getCurrentProject().getDefaultScene());
	}
}
