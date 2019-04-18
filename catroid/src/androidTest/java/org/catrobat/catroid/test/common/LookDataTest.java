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

package org.catrobat.catroid.test.common;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.utils.ImageEditing;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class LookDataTest {
	private LookData lookData;
	private String filePath;

	@Before
	public void setUp() throws Exception {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), getClass().getSimpleName());
		filePath = project.getDefaultScene().getDirectory() + "/" + IMAGE_DIRECTORY_NAME + "/collision_donut.png";

		Sprite sprite = new SingleSprite("testSprite");
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		XstreamSerializer.getInstance().saveProject(project);

		File imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				org.catrobat.catroid.test.R.raw.collision_donut,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				"collision_donut.png",
				1);

		lookData = new LookData("test", imageFile);
		sprite.getLookList().add(lookData);
	}

	@Test
	public void testCollisionInformation() {
		lookData.getCollisionInformation().loadOrCreateCollisionPolygon();

		String metadata = ImageEditing.readMetaDataStringFromPNG(filePath, Constants.COLLISION_PNG_META_TAG_KEY);

		final String expectedMetadata = "0.0;228.0;9.0;321.0;57.0;411.0;136.0;474.0;228.0;500.0;305.0;495.0;375.0;"
				+ "468.0;436.0;419.0;474.0;364.0;497.0;295.0;499.0;218.0;481.0;151.0;443.0;89.0;385.0;38.0;321.0;9.0;"
				+ "179.0;9.0;115.0;38.0;57.0;89.0;19.0;151.0|125.0;248.0;154.0;330.0;201.0;365.0;248.0;375.0;313.0;"
				+ "358.0;365.0;299.0;374.0;234.0;346.0;170.0;285.0;130.0;206.0;133.0;150.0;175.0";

		assertEquals(expectedMetadata, metadata);
	}

	@Test
	public void testPixmapAndTextureRegionDisposal() {
		LookData lookData = new LookData();
		Pixmap pixmap = mock(Pixmap.class);
		Texture texture = mock(Texture.class);
		TextureRegion textureRegion = new TextureRegion(texture);

		lookData.setPixmap(pixmap);
		lookData.setTextureRegion(textureRegion);

		lookData.dispose();

		verify(pixmap, times(1)).dispose();
		verify(textureRegion.getTexture(), times(1)).dispose();
	}

	@Test
	public void testNoExceptionThrownOnCallingResetLookData() {
		LookData lookData = new LookData();
		lookData.dispose();
	}
}
