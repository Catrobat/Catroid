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

package org.catrobat.catroid.test.io.asynctask;

import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.asynctask.ProjectLoadTask;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)

public class ProjectLoadTaskTest {

	private final String projectName = "testProject";
	private Project defaultProject;

	@Before
	public void setUp() throws IOException {
		TestUtils.deleteProjects(projectName);
		defaultProject = DefaultProjectHandler.createAndSaveDefaultProject(projectName,
				ApplicationProvider.getApplicationContext(), false);
	}

	@After
	public void tearDown() throws IOException {
		TestUtils.deleteProjects(projectName);
	}

	@Test
	public void projectLoadTaskTest() {
		assertFalse(ProjectLoadTask.task(null, ApplicationProvider.getApplicationContext()));
		assertTrue(ProjectLoadTask.task(defaultProject.getDirectory(), ApplicationProvider.getApplicationContext()));
	}
}
