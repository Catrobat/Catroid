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

package org.catrobat.catroid.test.io;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.defaultprojectcreators.ArDroneProjectCreator;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DefaultDroneProgramLoadingTest {

	private String projectName;
	private Project currentProjectBuffer;
	private Project project;

	@Before
	public void setUp() throws Exception {
		Context targetContext = InstrumentationRegistry.getTargetContext();
		currentProjectBuffer = ProjectManager.getInstance().getCurrentProject();
		projectName = targetContext.getString(R.string.default_drone_project_name);
		project = new ArDroneProjectCreator()
				.createDefaultProject(projectName, InstrumentationRegistry.getTargetContext(), false);
	}

	@After
	public void tearDown() throws Exception {
		ProjectManager.getInstance().setCurrentProject(currentProjectBuffer);
		TestUtils.deleteProjects(projectName);
	}

	@Test
	public void testDroneProgramLoadingSuccessfully() throws IOException, LoadingProjectException {
		Project loadedProject = XstreamSerializer.getInstance()
				.loadProject(project.getDirectory(), InstrumentationRegistry.getTargetContext());

		Scene preScene = project.getDefaultScene();
		Scene postScene = loadedProject.getDefaultScene();

		List<Sprite> preSpriteList = project.getDefaultScene().getSpriteList();
		List<Sprite> postSpriteList = loadedProject.getDefaultScene().getSpriteList();

		assertEquals(project.getName(), loadedProject.getName());
		assertEquals(preScene.getName(), postScene.getName());

		assertEquals(preSpriteList, postSpriteList);

		assertEquals(1, postSpriteList.get(0).getLookList().size());
		assertEquals(1, postSpriteList.get(1).getLookList().size());
		assertEquals(1, postSpriteList.get(2).getLookList().size());
		assertEquals(1, postSpriteList.get(3).getLookList().size());
		assertEquals(1, postSpriteList.get(4).getLookList().size());
		assertEquals(1, postSpriteList.get(5).getLookList().size());
		assertEquals(1, postSpriteList.get(6).getLookList().size());
		assertEquals(1, postSpriteList.get(7).getLookList().size());
		assertEquals(1, postSpriteList.get(8).getLookList().size());
		assertEquals(1, postSpriteList.get(9).getLookList().size());
		assertEquals(1, postSpriteList.get(10).getLookList().size());
		assertEquals(1, postSpriteList.get(11).getLookList().size());
	}
}
