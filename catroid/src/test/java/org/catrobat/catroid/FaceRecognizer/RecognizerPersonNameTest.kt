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
package org.catrobat.catroid.FaceRecognizer

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The face database stores one name per line. Found in review: a name with a
 * line break ("Ada\nLovelace") was accepted, and after a restart it came back as
 * two names, so every later person's photos belonged to the wrong name.
 */
@RunWith(RobolectricTestRunner::class)
class RecognizerPersonNameTest {

    private lateinit var context: Context
    private lateinit var recognizer: Recognizer

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        FileUtils.init(context)
        FileUtils.deleteAll()
        recognizer = Recognizer.withoutModelsForTest(context)
    }

    @After
    fun tearDown() {
        FileUtils.deleteAll()
    }

    @Test
    fun aNameWithALineBreakIsRejected() {
        for (name in listOf("Ada\nLovelace", "Ada\r\nLovelace", "Ada\rLovelace", "Ada Lovelace")) {
            assertThrows("'$name' must be rejected", IllegalArgumentException::class.java) {
                recognizer.addPerson(name)
            }
        }
        assertEquals(emptyList<String>(), recognizer.classNames)
    }

    @Test
    fun theReviewCaseKeepsNamesAndPeopleTogetherAfterARestart() {
        assertThrows(IllegalArgumentException::class.java) { recognizer.addPerson("Ada\nLovelace") }
        recognizer.addPerson("Grace")

        val restarted = Recognizer.withoutModelsForTest(context)

        assertEquals(listOf("Grace"), restarted.classNames)
    }

    @Test
    fun namesWithSpacesStillWorkAfterARestart() {
        recognizer.addPerson("Ada Lovelace")
        recognizer.addPerson("Grace")

        val restarted = Recognizer.withoutModelsForTest(context)

        assertEquals(listOf("Ada Lovelace", "Grace"), restarted.classNames)
    }
}
