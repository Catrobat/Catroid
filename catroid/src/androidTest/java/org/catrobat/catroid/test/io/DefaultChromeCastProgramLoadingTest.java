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
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.defaultprojectcreators.ChromeCastProjectCreator;
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
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DefaultChromeCastProgramLoadingTest {

	private final XstreamSerializer storageHandler;
	private String projectName;
	private Project currentProjectBuffer;
	private Context targetContext;
	private Project project;

	public DefaultChromeCastProgramLoadingTest() {
		storageHandler = XstreamSerializer.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		targetContext = InstrumentationRegistry.getTargetContext();
		currentProjectBuffer = ProjectManager.getInstance().getCurrentProject();
		projectName = targetContext.getString(R.string.default_cast_project_name);
		project = new ChromeCastProjectCreator().createDefaultProject(projectName, targetContext, true);
	}

	@After
	public void tearDown() throws Exception {
		ProjectManager.getInstance().setProject(currentProjectBuffer);
		TestUtils.deleteProjects(projectName);
	}

	@Test
	public void testLoadingChromeCastProgram() throws IOException, LoadingProjectException {
		Project loadedProject = storageHandler.loadProject(projectName, targetContext);
		Scene preScene = project.getDefaultScene();
		Scene postScene = loadedProject.getDefaultScene();

		ArrayList<Sprite> preSpriteList = (ArrayList<Sprite>) project.getDefaultScene().getSpriteList();
		ArrayList<Sprite> postSpriteList = (ArrayList<Sprite>) loadedProject.getDefaultScene().getSpriteList();

		assertEquals(project.getName(), loadedProject.getName());
		assertEquals(preScene.getName(), postScene.getName());

		assertEquals(preSpriteList.get(0).getName(), postSpriteList.get(0).getName());
		assertEquals(preSpriteList.get(1).getName(), postSpriteList.get(1).getName());
		assertEquals(preSpriteList.get(2).getName(), postSpriteList.get(2).getName());
		assertEquals(preSpriteList.get(3).getName(), postSpriteList.get(3).getName());
	}
}
