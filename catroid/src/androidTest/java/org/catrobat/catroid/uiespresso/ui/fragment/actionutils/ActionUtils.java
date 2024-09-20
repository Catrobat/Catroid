/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.fragment.actionutils;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;

public class ActionUtils {

	protected ActionUtils() {
		throw new UnsupportedOperationException();
	}

	public static void addLook(ProjectManager projectManager, String lookName) throws IOException {
		File imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.drawable.catroid_banzai,
				new File(projectManager.getCurrentProject().getDefaultScene().getDirectory(),
						IMAGE_DIRECTORY_NAME),
				"catroid_sunglasses.png",
				1);

		List<LookData> lookDataList = projectManager.getCurrentSprite().getLookList();

		LookData lookData1 = new LookData();
		lookData1.setFile(imageFile);
		lookData1.setName(lookName);
		lookDataList.add(lookData1);
		XstreamSerializer.getInstance().saveProject(projectManager.getCurrentProject());
	}

	public static void addSound(ProjectManager projectManager, String soundName) throws IOException {
		File soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.longsound,
				new File(projectManager.getCurrentProject().getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME),
				"longsound.mp3");

		List<SoundInfo> soundInfoList = projectManager.getCurrentSprite().getSoundList();
		SoundInfo soundInfo = new SoundInfo();

		soundInfo.setFile(soundFile);
		soundInfo.setName(soundName);
		soundInfoList.add(soundInfo);
		XstreamSerializer.getInstance().saveProject(projectManager.getCurrentProject());
	}
}
