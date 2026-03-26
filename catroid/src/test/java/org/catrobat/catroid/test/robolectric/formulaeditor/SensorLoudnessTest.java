/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

package org.catrobat.catroid.formulaeditor;

import android.os.Build;

import org.catrobat.catroid.common.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class SensorLoudnessTest {

	@Test
	public void defaultRecorderPathUsesAppCacheFileInsteadOfDevNull() {
		AtomicReference<String> createdPath = new AtomicReference<>();

		new SensorLoudness(path -> {
			createdPath.set(path);
			return null;
		});

		assertFalse("/dev/null should no longer be used for loudness recording",
				"/dev/null".equals(createdPath.get()));
		assertTrue("Recorder path should live in the sound recorder cache directory",
				createdPath.get().startsWith(Constants.SOUND_RECORDER_CACHE_DIRECTORY.getAbsolutePath()
						+ File.separator));
	}
}
