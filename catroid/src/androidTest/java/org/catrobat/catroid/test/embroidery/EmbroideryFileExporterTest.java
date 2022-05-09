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

package org.catrobat.catroid.test.embroidery;

import android.content.Intent;
import android.net.Uri;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.ExportEmbroideryFileLauncher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;

import androidx.core.content.FileProvider;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class EmbroideryFileExporterTest {

	private StageActivity stageActivity;
	private String filename = EmbroideryFileExporterTest.class.getName() + ".dst";

	@Before
	public void setUp() {
		stageActivity = Mockito.mock(StageActivity.class);
		Mockito.when(stageActivity.getPackageManager()).thenReturn(ApplicationProvider.getApplicationContext().getPackageManager());
		Mockito.when(stageActivity.getPackageName()).thenReturn(ApplicationProvider.getApplicationContext().getPackageName());
	}

	@Test
	public void testShareSimpleFile() {
		File dstFile = new File(Constants.CACHE_DIR, filename);
		Uri uriForFile = FileProvider.getUriForFile(stageActivity, stageActivity.getPackageName() + ".fileProvider", dstFile);

		new ExportEmbroideryFileLauncher(stageActivity, dstFile).startActivity();

		ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
		Mockito.verify(stageActivity, Mockito.times(1)).startActivity(captor.capture());

		Intent actualChooserIntent = captor.getValue();
		Intent actualShareIntent = actualChooserIntent.getParcelableExtra(Intent.EXTRA_INTENT);

		Intent expectedShareIntent = new Intent(Intent.ACTION_SEND, uriForFile);
		expectedShareIntent.setType("text/*");
		expectedShareIntent.putExtra(Intent.EXTRA_STREAM, uriForFile);
		expectedShareIntent.putExtra(Intent.EXTRA_SUBJECT, dstFile.getName());

		assertEquals(expectedShareIntent.toUri(0), actualShareIntent.toUri(0));

		Intent expectedChooserIntent = new Intent(Intent.ACTION_CHOOSER);
		expectedChooserIntent.putExtra(Intent.EXTRA_INTENT, expectedShareIntent);
		expectedChooserIntent.putExtra(Intent.EXTRA_TITLE, "Share embroidery file");

		assertEquals(expectedChooserIntent.toUri(0), actualChooserIntent.toUri(0));
	}
}
