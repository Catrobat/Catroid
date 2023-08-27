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

package org.catrobat.catroid.uiespresso.ui.activity.rtl

import androidx.appcompat.widget.AppCompatImageView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.DefaultProjectHandler
import org.catrobat.catroid.common.NfcTagData
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.nfc.NfcHandler
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.testsuites.annotations.Cat
import org.catrobat.catroid.testsuites.annotations.Cat.RTLTests
import org.catrobat.catroid.ui.BaseActivity
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.ui.NfcTagsActivity
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils
import org.catrobat.catroid.utils.dpToPx
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.koin.java.KoinJavaComponent.inject

@Category(RTLTests::class, Cat.AppUi::class)
@RunWith(Parameterized::class)
class RTLOptionMenuIconsTest(
    activityClass: Class<out BaseActivity>
) {
    @get:Rule
    var baseActivityTestRule = ActivityTestRule(activityClass, true, false)

    private var projectName = "MyTestProject"
    lateinit var project: Project
    private val projectManager: Lazy<ProjectManager> = inject(ProjectManager::class.java)
    var width: Float = 0F

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            MainMenuActivity::class.java, NfcTagsActivity::class.java,
            ProjectActivity::class.java, ProjectListActivity::class.java,
            SpriteActivity::class.java, BackpackActivity::class.java
        )
    }

    @Before
    fun setUp() {
        TestUtils.deleteProjects(projectName)

        project = DefaultProjectHandler.createAndSaveDefaultProject(
            projectName,
            ApplicationProvider.getApplicationContext(),
            false
        )
        addNfcData()
        projectManager.value.currentProject = project
        projectManager.value.currentSprite = project.defaultScene.spriteList[1]
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(projectName)
    }

    @Test
    fun testMenuIconsWithLTRLanguage() {
        val englishLanguageTag = "en"
        setLanguage(englishLanguageTag)
        baseActivityTestRule.launchActivity(null)
        assertIcons()
    }

    @Test
    fun testMenuIconsWithLRTLanguage() {
        val arabicLanguageTag = "ar"
        setLanguage(arabicLanguageTag)
        baseActivityTestRule.launchActivity(null)
        assertIcons()
    }

    private fun setLanguage(languageTag: String) {
        SensorHandler.startSensorListener(ApplicationProvider.getApplicationContext())
        SettingsFragment.setLanguageSharedPreference(ApplicationProvider.getApplicationContext(), languageTag)
    }

    private fun assertIcons() {
        width = baseActivityTestRule.activity.applicationContext.dpToPx(24F)
        val optionsMenu = baseActivityTestRule.activity.optionsMenu

        for (i in 0 until optionsMenu.size()) {
            val currentItem = optionsMenu.getItem(i)
            if (currentItem.isVisible) {
                Assert.assertNotNull(currentItem.icon)
                currentItem.icon?.let { Assert.assertTrue(it.isVisible) }
                Assert.assertTrue((currentItem.icon?.minimumWidth ?: 0) >= width)

                onData(withId(currentItem.itemId)).onChildView(hasDescendant(instanceOf(AppCompatImageView::class.java)))
            }
        }
    }

    private fun addNfcData() {
        val firstTagData = NfcTagData()
        firstTagData.name = "abc"
        firstTagData.nfcTagUid = NfcHandler.byteArrayToHex(UiNFCTestUtils.FIRST_TEST_TAG_ID.toByteArray())
        val secondTagData = NfcTagData()
        secondTagData.name = "def"
        secondTagData.nfcTagUid = NfcHandler.byteArrayToHex(UiNFCTestUtils.SECOND_TEST_TAG_ID.toByteArray())
        project.defaultScene.spriteList[1].nfcTagList.addAll(mutableListOf(firstTagData, secondTagData))
    }
}
