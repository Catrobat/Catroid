/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
package org.catrobat.catroid.uiespresso.ui.activity

import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ProjectListActivityTest {
    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        ProjectListActivity::class.java, false, false
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        baseActivityTestRule.deleteAllProjects()
        defaultSharedPreferences.edit()
            .putBoolean(SharedPreferenceKeys.SHOW_DETAILS_PROJECTS_PREFERENCE_KEY, true)
            .apply()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        defaultSharedPreferences.edit()
            .remove(SharedPreferenceKeys.SHOW_DETAILS_PROJECTS_PREFERENCE_KEY)
            .apply()
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    @Throws(
        IOException::class
    )
    fun testInvalidMetaDataDoesNotCrash() {
        val projectDirectory = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, PROJECT_NAME)
        val codeXML = File(projectDirectory, Constants.CODE_XML_FILE_NAME)
        Assert.assertTrue(projectDirectory.mkdir())
        Assert.assertTrue(codeXML.createNewFile())
        baseActivityTestRule.launchActivity(null)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    @Throws(
        IOException::class
    )
    fun testInvalidProjectXMLDoesNotCrashWhenShowDetailsEnabled() {
        val projectDirectory = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, PROJECT_NAME)
        val codeXML = File(projectDirectory, Constants.CODE_XML_FILE_NAME)
        Assert.assertTrue(projectDirectory.mkdir())
        val inputStream = assets.open(INVALID_PROJECT_XML)
        StorageOperations.copyStreamToFile(inputStream, codeXML)
        baseActivityTestRule.launchActivity(null)
    }

    private val assets = InstrumentationRegistry.getInstrumentation().context.assets
    private val defaultSharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())

    companion object {
        private const val PROJECT_NAME = "projectName"
        private const val INVALID_PROJECT_XML = "invalid_project.xml"
    }
}
