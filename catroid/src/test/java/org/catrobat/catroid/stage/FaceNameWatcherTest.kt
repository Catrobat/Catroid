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
package org.catrobat.catroid.stage

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The name dialog of the training brick. Found in review: the duplicate check was
 * case-sensitive while the face database is not. With "Ada" trained, "ada" passed
 * the dialog, the database returned Ada's entry, and the photo picker then failed
 * with "That name is no longer in the list". The dialog rejects such a name inline.
 */
@RunWith(RobolectricTestRunner::class)
class FaceNameWatcherTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val watcher = BrickDialogManager.FaceNameWatcher().apply { setScope(listOf("Ada")) }

    @Test
    fun aNameThatDiffersOnlyInCaseIsADuplicate() {
        assertEquals(context.getString(R.string.name_already_exists), watcher.validateInput("ada", context))
        assertEquals(context.getString(R.string.name_already_exists), watcher.validateInput("ADA", context))
    }

    @Test
    fun aNameWithSpacesAroundItIsADuplicate() {
        assertEquals(context.getString(R.string.name_already_exists), watcher.validateInput(" Ada ", context))
    }

    @Test
    fun aNewNameIsAccepted() {
        assertNull(watcher.validateInput("Bob", context))
    }

    @Test
    fun aNameWithALineBreakIsRejected() {
        assertEquals(context.getString(R.string.face_train_name_line_break), watcher.validateInput("Bo\nb", context))
    }
}
