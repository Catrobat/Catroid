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

package org.catrobat.catroid.test.ui.shortcut

import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.asynctask.projectSaveLock
import org.catrobat.catroid.io.asynctask.renameProject
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.ui.shortcut.ShortcutDialogHelper
import org.catrobat.catroid.ui.shortcut.ShortcutHelper
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowAlertDialog
import org.robolectric.shadows.ShadowBuild

@RunWith(RobolectricTestRunner::class)
class ShortcutDialogHelperTest {

    private lateinit var activity: AppCompatActivity

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(AppCompatActivity::class.java).setup().get()
        mockkObject(ShortcutHelper)
    }

    @After
    fun tearDown() {
        unmockkAll()
        ShadowBuild.reset()
    }

    @Test
    fun `showPinShortcutDialog displays pin dialog when permission granted`() {
        every { ShortcutHelper.isXiaomiDevice() } returns false

        ShortcutDialogHelper.showPinShortcutDialog(
            context = activity,
            layoutInflater = activity.layoutInflater,
            projectName = "MyProject",
            icon = null,
            isPermissionGranted = true
        )

        val dialog = ShadowAlertDialog.getLatestAlertDialog()
        assertNotNull(dialog)
        assertTrue(dialog.isShowing)

        val nameEdit = dialog.findViewById<EditText>(R.id.shortcut_dialog_project_name_edit)
        assertEquals("MyProject", nameEdit.text.toString())
    }

    @Test
    fun `showPinShortcutDialog displays permission dialog on Xiaomi when not granted`() {
        every { ShortcutHelper.isXiaomiDevice() } returns true

        var settingsClicked = false
        ShortcutDialogHelper.showPinShortcutDialog(
            context = activity,
            layoutInflater = activity.layoutInflater,
            projectName = "MyProject",
            icon = null,
            isPermissionGranted = false,
            onSettingsClicked = { settingsClicked = true }
        )

        val dialog = ShadowAlertDialog.getLatestAlertDialog()
        assertNotNull(dialog)
        assertTrue(dialog.isShowing)

        val miuiContainer = dialog.findViewById<View>(R.id.shortcut_dialog_miui_container)
        assertEquals(View.VISIBLE, miuiContainer.visibility)

        val settingsBtn = dialog.findViewById<Button>(R.id.shortcut_dialog_miui_settings_button)
        settingsBtn.performClick()
        assertTrue(settingsClicked)
        assertFalse(dialog.isShowing)
    }

    @Test
    fun `clicking rename container or label toggles rename checkbox`() {
        every { ShortcutHelper.isXiaomiDevice() } returns false

        ShortcutDialogHelper.showPinShortcutDialog(
            context = activity,
            layoutInflater = activity.layoutInflater,
            projectName = "OriginalName",
            icon = null,
            isPermissionGranted = true
        )

        val dialog = ShadowAlertDialog.getLatestAlertDialog()
        val renameCheckbox =
            dialog.findViewById<CheckBox>(R.id.shortcut_dialog_rename_project_checkbox)
        val renameContainer =
            dialog.findViewById<View>(R.id.shortcut_dialog_rename_project_container)
        val renameLabel =
            dialog.findViewById<android.widget.TextView>(R.id.shortcut_dialog_rename_project_label)

        assertFalse(renameCheckbox.isChecked)

        renameLabel.performClick()
        assertTrue(renameCheckbox.isChecked)

        renameContainer.performClick()
        assertFalse(renameCheckbox.isChecked)
    }

    @Test
    fun `cancel button dismisses dialog`() {
        every { ShortcutHelper.isXiaomiDevice() } returns false

        ShortcutDialogHelper.showPinShortcutDialog(
            context = activity,
            layoutInflater = activity.layoutInflater,
            projectName = "MyProject",
            icon = null,
            isPermissionGranted = true
        )

        val dialog = ShadowAlertDialog.getLatestAlertDialog()
        assertTrue(dialog.isShowing)

        val cancelBtn = dialog.findViewById<Button>(R.id.shortcut_dialog_cancel_button)
        cancelBtn.performClick()
        assertFalse(dialog.isShowing)
    }

    @Test
    fun `pin button with unchanged name pins project with project name as label`() {
        every { ShortcutHelper.isXiaomiDevice() } returns false
        every { ShortcutHelper.pinProject(any(), any(), any(), any()) } returns true

        ShortcutDialogHelper.showPinShortcutDialog(
            context = activity,
            layoutInflater = activity.layoutInflater,
            projectName = "MyProject",
            icon = null,
            isPermissionGranted = true
        )

        val dialog = ShadowAlertDialog.getLatestAlertDialog()
        val pinBtn = dialog.findViewById<Button>(R.id.shortcut_dialog_pin_button)
        pinBtn.performClick()

        verify {
            ShortcutHelper.pinProject(
                context = activity,
                projectName = "MyProject",
                icon = null,
                shortcutLabel = "MyProject"
            )
        }
        assertFalse(dialog.isShowing)
    }

    @Test
    fun `pin button with changed name and unchecked rename pins with custom label`() {
        every { ShortcutHelper.isXiaomiDevice() } returns false
        every { ShortcutHelper.pinProject(any(), any(), any(), any()) } returns true

        ShortcutDialogHelper.showPinShortcutDialog(
            context = activity,
            layoutInflater = activity.layoutInflater,
            projectName = "OriginalProject",
            icon = null,
            isPermissionGranted = true
        )

        val dialog = ShadowAlertDialog.getLatestAlertDialog()
        val nameEdit = dialog.findViewById<EditText>(R.id.shortcut_dialog_project_name_edit)
        val renameCheckbox =
            dialog.findViewById<CheckBox>(R.id.shortcut_dialog_rename_project_checkbox)
        val pinBtn = dialog.findViewById<Button>(R.id.shortcut_dialog_pin_button)

        nameEdit.setText("CustomLabel")
        renameCheckbox.isChecked = false
        pinBtn.performClick()

        verify {
            ShortcutHelper.pinProject(
                context = activity,
                projectName = "OriginalProject",
                icon = null,
                shortcutLabel = "CustomLabel"
            )
        }
        assertFalse(dialog.isShowing)
    }

    @Test
    fun `pin button with blank name falls back to original project name`() {
        every { ShortcutHelper.isXiaomiDevice() } returns false
        every { ShortcutHelper.pinProject(any(), any(), any(), any()) } returns true

        ShortcutDialogHelper.showPinShortcutDialog(
            context = activity,
            layoutInflater = activity.layoutInflater,
            projectName = "MyProject",
            icon = null,
            isPermissionGranted = true
        )

        val dialog = ShadowAlertDialog.getLatestAlertDialog()
        val nameEdit = dialog.findViewById<EditText>(R.id.shortcut_dialog_project_name_edit)
        val pinBtn = dialog.findViewById<Button>(R.id.shortcut_dialog_pin_button)

        nameEdit.setText("   ")
        pinBtn.performClick()

        verify {
            ShortcutHelper.pinProject(
                context = activity,
                projectName = "MyProject",
                icon = null,
                shortcutLabel = "MyProject"
            )
        }
        assertFalse(dialog.isShowing)
    }

    @Test
    fun `relocateOpenProject updates loaded project directory and look and sound files`() {
        val oldDir = java.io.File("/tmp/projects/OldProject")
        val project = org.catrobat.catroid.content.Project(activity, "OldProject")
        project.directory = oldDir

        val lookFile = java.io.File(oldDir, "images/look1.png")
        val look = org.catrobat.catroid.common.LookData("look1", lookFile)

        val soundFile = java.io.File(oldDir, "sounds/sound1.mp3")
        val sound = org.catrobat.catroid.common.SoundInfo("sound1", soundFile)

        val sprite = project.sceneList[0].spriteList[0]
        sprite.lookList.add(look)
        sprite.soundList.add(sound)

        ShortcutDialogHelper.relocateOpenProject(project, "NewProject")

        assertEquals("NewProject", project.name)
        val expectedNewDir = java.io.File(oldDir.parentFile, "NewProject")
        assertEquals(expectedNewDir.absolutePath, project.directory.absolutePath)
        assertEquals(
            java.io.File(expectedNewDir, "images/look1.png").absolutePath,
            look.file.absolutePath
        )
        assertEquals(
            java.io.File(expectedNewDir, "sounds/sound1.mp3").absolutePath,
            sound.file.absolutePath
        )
    }

    @Test
    fun `saveProject during open project rename does not recreate old directory and saves to new directory`() {
        val rootDir = activity.filesDir
        val oldProjectDir = File(rootDir, "OriginalName").apply { mkdirs() }
        val newProjectDir = File(rootDir, "RenamedName")

        val project = Project(activity, "OriginalName")
        project.directory = oldProjectDir
        XstreamSerializer.getInstance().saveProject(project)
        assertTrue(oldProjectDir.exists())
        assertTrue(File(oldProjectDir, Constants.CODE_XML_FILE_NAME).exists())

        ProjectManager.getInstance().currentProject = project

        val saveStarted = CountDownLatch(1)
        val saveCompleted = CountDownLatch(1)
        var saveResult = false

        project.description = "Unsaved edit before pause"

        val backgroundThread = thread {
            saveStarted.countDown()
            saveResult = saveProjectSerial(project, activity)
            saveCompleted.countDown()
        }

        synchronized(projectSaveLock) {
            saveStarted.await()
            Thread.sleep(50)

            val renamedDir = renameProject(oldProjectDir, "RenamedName")
            assertNotNull(renamedDir)
            ShortcutDialogHelper.relocateOpenProject(project, "RenamedName")
        }

        saveCompleted.await()

        assertTrue("saveProjectSerial should succeed after rename completes", saveResult)
        assertFalse(
            "Old project directory must not be recreated by concurrent save",
            oldProjectDir.exists()
        )
        assertTrue("New project directory must exist", newProjectDir.exists())
        assertEquals("RenamedName", project.name)
        assertEquals(newProjectDir.absolutePath, project.directory.absolutePath)
        assertTrue(
            "code.xml must exist in new directory",
            File(newProjectDir, Constants.CODE_XML_FILE_NAME).exists()
        )

        newProjectDir.deleteRecursively()
    }

    @Test
    fun `pin button with changed name and checked rename triggers rename and relocation`() {
        val rootDir = FlavoredConstants.DEFAULT_ROOT_DIRECTORY
        val oldProjectDir = File(rootDir, "DialogRenameOriginal").apply { mkdirs() }
        val newProjectDir = File(rootDir, "DialogRenameNew")

        val project = Project(activity, "DialogRenameOriginal")
        project.directory = oldProjectDir
        XstreamSerializer.getInstance().saveProject(project)

        ProjectManager.getInstance().currentProject = project

        every { ShortcutHelper.isXiaomiDevice() } returns false
        every { ShortcutHelper.pinProject(any(), any(), any(), any()) } returns true
        coEvery { ShortcutHelper.updateShortcutOnRename(any(), any(), any()) } just Runs

        ShortcutDialogHelper.ioDispatcher = Dispatchers.Unconfined
        ShortcutDialogHelper.mainDispatcher = Dispatchers.Unconfined

        try {
            var renamedCallbackOld = ""
            var renamedCallbackNew = ""

            ShortcutDialogHelper.showPinShortcutDialog(
                context = activity,
                layoutInflater = activity.layoutInflater,
                projectName = "DialogRenameOriginal",
                icon = null,
                isPermissionGranted = true,
                onProjectRenamed = { oldName, newName ->
                    renamedCallbackOld = oldName
                    renamedCallbackNew = newName
                }
            )

            val dialog = ShadowAlertDialog.getLatestAlertDialog()
            val nameEdit = dialog.findViewById<EditText>(R.id.shortcut_dialog_project_name_edit)
            val renameCheckbox =
                dialog.findViewById<CheckBox>(R.id.shortcut_dialog_rename_project_checkbox)
            val pinBtn = dialog.findViewById<Button>(R.id.shortcut_dialog_pin_button)

            nameEdit.setText("DialogRenameNew")
            renameCheckbox.isChecked = true
            pinBtn.performClick()

            assertEquals("DialogRenameOriginal", renamedCallbackOld)
            assertEquals("DialogRenameNew", renamedCallbackNew)
            assertEquals("DialogRenameNew", project.name)
            assertEquals(newProjectDir.absolutePath, project.directory.absolutePath)
            assertFalse(oldProjectDir.exists())
            assertTrue(newProjectDir.exists())
        } finally {
            ShortcutDialogHelper.ioDispatcher = Dispatchers.IO
            ShortcutDialogHelper.mainDispatcher = Dispatchers.Main
            oldProjectDir.deleteRecursively()
            newProjectDir.deleteRecursively()
        }
    }
}
