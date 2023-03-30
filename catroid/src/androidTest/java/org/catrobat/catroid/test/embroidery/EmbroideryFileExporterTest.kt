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
package org.catrobat.catroid.test.embroidery

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.ui.ExportEmbroideryFileLauncher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import java.io.File

@RunWith(AndroidJUnit4::class)
class EmbroideryFileExporterTest {
    private var stageActivity: StageActivity? = null
    private val filename = EmbroideryFileExporterTest::class.java.name + ".dst"
    @Before
    fun setUp() {
        stageActivity = Mockito.mock(StageActivity::class.java)
        Mockito.`when`(stageActivity?.packageManager)
            .thenReturn(ApplicationProvider.getApplicationContext<Context>().packageManager)
        Mockito.`when`(stageActivity?.packageName)
            .thenReturn(ApplicationProvider.getApplicationContext<Context>().packageName)
    }

    @Test
    fun testShareSimpleFile() {
        val dstFile = File(Constants.CACHE_DIRECTORY, filename)
        val uriForFile = FileProvider.getUriForFile(
            stageActivity!!,
            stageActivity!!.packageName + ".fileProvider",
            dstFile
        )
        ExportEmbroideryFileLauncher(stageActivity!!, dstFile).startActivity()
        val captor = ArgumentCaptor.forClass(
            Intent::class.java
        )
        Mockito.verify(stageActivity, Mockito.times(1))?.startActivity(captor.capture())
        val actualChooserIntent = captor.value
        val actualShareIntent = actualChooserIntent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
        val expectedShareIntent = Intent(Intent.ACTION_SEND, uriForFile)
        expectedShareIntent.type = "text/*"
        expectedShareIntent.putExtra(Intent.EXTRA_STREAM, uriForFile)
        expectedShareIntent.putExtra(Intent.EXTRA_SUBJECT, dstFile.name)
        Assert.assertEquals(expectedShareIntent.toUri(0), actualShareIntent!!.toUri(0))
        val expectedChooserIntent = Intent(Intent.ACTION_CHOOSER)
        expectedChooserIntent.putExtra(Intent.EXTRA_INTENT, expectedShareIntent)
        expectedChooserIntent.putExtra(Intent.EXTRA_TITLE, "Share embroidery file")
        Assert.assertEquals(expectedChooserIntent.toUri(0), actualChooserIntent.toUri(0))
    }
}
