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

package org.catrobat.catroid.test.history;

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.IdPool;

import java.util.HashSet;
import java.util.Set;

public class UniqueIdTest extends AndroidTestCase {

	private final ProjectManager pm = ProjectManager.getInstance();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Reflection.setPrivateField(pm, "asynchronTask", false);
	}

	@Override
	protected void tearDown() throws Exception {
		Reflection.setPrivateField(pm, "asynchronTask", true);
		super.tearDown();
	}

	public void testUniqueId() throws CompatibilityProjectException, OutdatedVersionProjectException, LoadingProjectException {
		Project project = TestUtils.createEmptyProject();
		Sprite firstSprite = new Sprite("A");
		project.addSprite(firstSprite);
		project.addSprite(new Sprite("B"));
		project.removeSprite(firstSprite);

		pm.setProject(project);
		pm.saveProject(getContext());
		Reflection.setPrivateField(pm, "idPool", new IdPool());
//		Reflection.setPrivateField(pm, "idList", new ArrayList<Integer>());
		pm.loadProject(project.getName(), getContext());
		project = pm.getCurrentProject();

		project.addSprite(new Sprite("C"));
		project.addSprite(new Sprite("D"));

		Set<Integer> ids = new HashSet<>();
		for (Sprite sprite : project.getSpriteList()) {
			int id = sprite.getId();
			assertFalse("Id is not unique", ids.contains(id));
			ids.add(id);
		}
	}
}
