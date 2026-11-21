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
import io.mockk.every
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.asynctask.tryRenameTo
import org.catrobat.catroid.ui.shortcut.ShortcutDialogHelper
import org.catrobat.catroid.ui.shortcut.ShortcutHelper
import org.catrobat.catroid.ui.shortcut.ShortcutHelper.PinResult
import org.catrobat.catroid.utils.FileMetaDataExtractor
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowAlertDialog
import org.robolectric.shadows.ShadowBuild
import org.robolectric.shadows.ShadowToast
import java.io.File
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class ShortcutDialogHelperTest {

    private lateinit var activity: AppCompatActivity
    private val testScope = CoroutineScope(Dispatchers.Unconfined)

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(AppCompatActivity::class.java).setup().get()
        mockkObject(ShortcutHelper)
        ShortcutDialogHelper.ioDispatcher = Dispatchers.Unconfined
    }

    @After
    fun tearDown() {
        unmockkAll()
        ShadowBuild.reset()
        ShortcutDialogHelper.ioDispatcher = Dispatchers.IO
        ProjectManager.getInstance().currentProject = null
    }

    @Test
    fun `showPinShortcutDialog displays pin dialog when permission granted`() {
        every { ShortcutHelper.isXiaomiDevice() } returns false

        ShortcutDialogHelper.showPinShortcutDialog(
            context = activity,
            layoutInflater = activity.layoutInflater,
            projectName = "MyProject",
            icon = null,
            coroutineScope = testScope,
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
            coroutineScope = testScope,
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
            coroutineScope = testScope,
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
            coroutineScope = testScope,
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
        every { ShortcutHelper.pinProject(any(), any(), any(), any()) } returns PinResult.REQUESTED

        ShortcutDialogHelper.showPinShortcutDialog(
            context = activity,
            layoutInflater = activity.layoutInflater,
            projectName = "MyProject",
            icon = null,
            coroutineScope = testScope,
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
        every { ShortcutHelper.pinProject(any(), any(), any(), any()) } returns PinResult.REQUESTED

        ShortcutDialogHelper.showPinShortcutDialog(
            context = activity,
            layoutInflater = activity.layoutInflater,
            projectName = "OriginalProject",
            icon = null,
            coroutineScope = testScope,
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
        every { ShortcutHelper.pinProject(any(), any(), any(), any()) } returns PinResult.REQUESTED

        ShortcutDialogHelper.showPinShortcutDialog(
            context = activity,
            layoutInflater = activity.layoutInflater,
            projectName = "MyProject",
            icon = null,
            coroutineScope = testScope,
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
    fun `pin button tells the user when the project is already pinned`() {
        every { ShortcutHelper.isXiaomiDevice() } returns false
        every {
            ShortcutHelper.pinProject(any(), any(), any(), any())
        } returns PinResult.ALREADY_PINNED

        showDialog("MyProject")
        pinButton().performClick()

        assertEquals(
            activity.getString(R.string.shortcut_already_pinned),
            ShadowToast.getTextOfLatestToast()
        )
    }

    @Test
    fun `pin button tells the user when the launcher refuses the pin`() {
        every { ShortcutHelper.isXiaomiDevice() } returns false
        every { ShortcutHelper.pinProject(any(), any(), any(), any()) } returns PinResult.FAILED

        showDialog("MyProject")
        pinButton().performClick()

        assertEquals(
            activity.getString(R.string.shortcut_pin_failed),
            ShadowToast.getTextOfLatestToast()
        )
    }

    @Test
    fun `pin button with checked rename renames, reloads and pins the open project`() {
        val oldProjectDir = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, "DialogRenameOriginal")
        val newProjectDir = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, "DialogRenameNew")
        openSavedProject("DialogRenameOriginal")
        stubShortcutCalls()

        try {
            var renamed: Pair<String, String>? = null
            showDialog("DialogRenameOriginal") { old, new -> renamed = old to new }

            renameAndPin("DialogRenameNew")

            assertEquals("DialogRenameOriginal" to "DialogRenameNew", renamed)
            val currentProject = ProjectManager.getInstance().currentProject
            assertEquals("DialogRenameNew", currentProject.name)
            assertEquals(newProjectDir.absolutePath, currentProject.directory.absolutePath)
            assertFalse(oldProjectDir.exists())
            assertTrue(newProjectDir.exists())
            assertFalse(ShadowAlertDialog.getLatestAlertDialog().isShowing)
            verify {
                ShortcutHelper.updateShortcutOnRename(
                    any(), "DialogRenameOriginal", "DialogRenameNew"
                )
            }
            verify {
                ShortcutHelper.pinProject(any(), "DialogRenameNew", null, "DialogRenameNew")
            }
        } finally {
            oldProjectDir.deleteRecursively()
            newProjectDir.deleteRecursively()
        }
    }

    @Test
    fun `failed rename keeps the dialog open, shows an error and does not pin`() {
        val oldProjectDir = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, "DialogFailOriginal")
        val newProjectDir = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, "DialogFailNew")
        val project = openSavedProject("DialogFailOriginal")
        stubShortcutCalls()
        mockkStatic(XstreamSerializer::class)
        every { XstreamSerializer.renameProject(any(), any()) } throws IOException("EACCES")

        try {
            var renamedCallbackCalled = false
            showDialog("DialogFailOriginal") { _, _ -> renamedCallbackCalled = true }

            renameAndPin("DialogFailNew")

            val dialog = ShadowAlertDialog.getLatestAlertDialog()
            assertTrue(dialog.isShowing)
            assertTrue(pinButton().isEnabled)
            assertFalse(renamedCallbackCalled)
            assertEquals(
                activity.getString(R.string.error_rename_project),
                ShadowToast.getTextOfLatestToast()
            )
            assertTrue(File(oldProjectDir, Constants.CODE_XML_FILE_NAME).exists())
            assertFalse(newProjectDir.exists())
            assertEquals(project, ProjectManager.getInstance().currentProject)
            assertEquals(oldProjectDir.absolutePath, project.directory.absolutePath)
            verify(exactly = 0) { ShortcutHelper.pinProject(any(), any(), any(), any()) }
            verify(exactly = 0) { ShortcutHelper.updateShortcutOnRename(any(), any(), any()) }
        } finally {
            oldProjectDir.deleteRecursively()
            newProjectDir.deleteRecursively()
        }
    }

    @Test
    fun `pin button with case-only rename succeeds and flushes in-memory edits`() {
        val oldProjectDir = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, "dialogcaseold")
        val newProjectDir = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, "DialogCaseOld")
        val project = openSavedProject("dialogcaseold")
        stubShortcutCalls()

        try {
            project.description = "Unsaved memory edit that must be flushed"
            showDialog("dialogcaseold")

            renameAndPin("DialogCaseOld")

            assertEquals("DialogCaseOld", ProjectManager.getInstance().currentProject.name)
            assertTrue(newProjectDir.exists())
            val loadedProject = XstreamSerializer.getInstance().loadProject(newProjectDir, activity)
            assertEquals("Unsaved memory edit that must be flushed", loadedProject.description)
        } finally {
            oldProjectDir.deleteRecursively()
            newProjectDir.deleteRecursively()
        }
    }

    @Test
    fun `pin button with encoded special character case-only rename succeeds`() {
        val rootDir = FlavoredConstants.DEFAULT_ROOT_DIRECTORY
        val oldProjectDir =
            File(rootDir, FileMetaDataExtractor.encodeSpecialCharsForFileSystem("dialog:case"))
        val newProjectDir =
            File(rootDir, FileMetaDataExtractor.encodeSpecialCharsForFileSystem("Dialog:Case"))
        openSavedProject("dialog:case")
        stubShortcutCalls()

        try {
            var renamed: Pair<String, String>? = null
            showDialog("dialog:case") { old, new -> renamed = old to new }

            renameAndPin("Dialog:Case")

            assertEquals("dialog:case" to "Dialog:Case", renamed)
            assertEquals("Dialog:Case", ProjectManager.getInstance().currentProject.name)
            assertTrue(newProjectDir.exists())
        } finally {
            oldProjectDir.deleteRecursively()
            newProjectDir.deleteRecursively()
        }
    }

    @Test
    fun `rename to the name of another existing project is refused`() {
        val rootDir = FlavoredConstants.DEFAULT_ROOT_DIRECTORY
        val projectDir = File(rootDir, "DialogTaken1")
        val otherDir = File(rootDir, "DialogTaken2").apply { mkdirs() }
        openSavedProject("DialogTaken1")
        stubShortcutCalls()

        try {
            showDialog("DialogTaken1")
            renameAndPin("DialogTaken2")

            assertEquals(
                activity.getString(R.string.name_already_exists),
                ShadowToast.getTextOfLatestToast()
            )
            assertTrue(ShadowAlertDialog.getLatestAlertDialog().isShowing)
            assertTrue(projectDir.exists())
            verify(exactly = 0) { ShortcutHelper.pinProject(any(), any(), any(), any()) }
        } finally {
            projectDir.deleteRecursively()
            otherDir.deleteRecursively()
        }
    }

    @Test
    fun `tryRenameTo executes temporary hop for case-only rename`() {
        val rootDir = activity.filesDir
        val sourceDir = File(rootDir, "testcasehop").apply { mkdirs() }
        val destDir = File(rootDir, "TestCaseHop")

        val result = tryRenameTo(sourceDir, destDir)
        assertTrue(result)
        assertTrue(destDir.exists())
        assertEquals("TestCaseHop", destDir.canonicalFile.name)

        destDir.deleteRecursively()
    }

    @Test
    fun `tryRenameTo standard rename succeeds`() {
        val rootDir = activity.filesDir
        val sourceDir = File(rootDir, "standard_old").apply { mkdirs() }
        val destDir = File(rootDir, "standard_new")

        val result = tryRenameTo(sourceDir, destDir)
        assertTrue(result)
        assertFalse(sourceDir.exists())
        assertTrue(destDir.exists())

        destDir.deleteRecursively()
    }

    private fun openSavedProject(name: String): Project {
        val projectDir = File(
            FlavoredConstants.DEFAULT_ROOT_DIRECTORY,
            FileMetaDataExtractor.encodeSpecialCharsForFileSystem(name)
        ).apply { mkdirs() }
        val project = Project(activity, name)
        project.directory = projectDir
        XstreamSerializer.getInstance().saveProject(project)
        ProjectManager.getInstance().currentProject = project
        return project
    }

    private fun stubShortcutCalls() {
        every { ShortcutHelper.isXiaomiDevice() } returns false
        every { ShortcutHelper.pinProject(any(), any(), any(), any()) } returns PinResult.REQUESTED
        every { ShortcutHelper.updateShortcutOnRename(any(), any(), any()) } just Runs
    }

    private fun showDialog(
        projectName: String,
        onProjectRenamed: ((String, String) -> Unit)? = null
    ) {
        ShortcutDialogHelper.showPinShortcutDialog(
            context = activity,
            layoutInflater = activity.layoutInflater,
            projectName = projectName,
            icon = null,
            coroutineScope = testScope,
            isPermissionGranted = true,
            onProjectRenamed = onProjectRenamed
        )
    }

    private fun pinButton(): Button =
        ShadowAlertDialog.getLatestAlertDialog()
            .findViewById(R.id.shortcut_dialog_pin_button)

    private fun renameAndPin(newName: String) {
        val dialog = ShadowAlertDialog.getLatestAlertDialog()
        dialog.findViewById<EditText>(R.id.shortcut_dialog_project_name_edit).setText(newName)
        dialog.findViewById<CheckBox>(R.id.shortcut_dialog_rename_project_checkbox)
            .isChecked = true
        pinButton().performClick()
    }
}
