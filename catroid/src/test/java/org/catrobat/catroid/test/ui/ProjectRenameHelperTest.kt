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

package org.catrobat.catroid.test.ui

import android.app.Activity
import android.preference.PreferenceManager
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.asynctask.renameProject
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.ui.ProjectRenameHelper
import org.catrobat.catroid.ui.shortcut.ShortcutHelper
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

@RunWith(RobolectricTestRunner::class)
class ProjectRenameHelperTest {

    private lateinit var activity: Activity
    private val rootDir get() = FlavoredConstants.DEFAULT_ROOT_DIRECTORY
    private val createdDirs = mutableListOf<File>()

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        mockkObject(ShortcutHelper)
        every { ShortcutHelper.updateShortcutOnRename(any(), any(), any()) } just Runs
    }

    @After
    fun tearDown() {
        unmockkAll()
        ProjectManager.getInstance().currentProject = null
        createdDirs.forEach { it.deleteRecursively() }
    }

    @Test
    fun `renameProject moves the folder back when rewriting code xml fails`() {
        val oldDir = createSavedProject("XmlFailOld").directory
        val newDir = projectDir("XmlFailNew")
        mockkStatic(XstreamSerializer::class)
        every { XstreamSerializer.renameProject(any(), any()) } throws IOException("EACCES")

        assertNull(renameProject(oldDir, "XmlFailNew"))

        assertTrue(File(oldDir, Constants.CODE_XML_FILE_NAME).exists())
        assertFalse(newDir.exists())
    }

    @Test
    fun `renameProject treats a missing programName tag as failure and moves the folder back`() {
        val oldDir = createSavedProject("NoTagOld").directory
        val newDir = projectDir("NoTagNew")
        File(oldDir, Constants.CODE_XML_FILE_NAME).writeText("<program></program>")

        assertNull(renameProject(oldDir, "NoTagNew"))

        assertTrue(oldDir.exists())
        assertFalse(newDir.exists())
    }

    @Test
    fun `failed rename of the open project keeps it pointing at its intact folder`() {
        val project = createSavedProject("OpenFailOld")
        val oldDir = project.directory
        val newDir = projectDir("OpenFailNew")
        addLook(project, "look1.png")
        XstreamSerializer.getInstance().saveProject(project)
        openProject(project)
        mockkStatic(XstreamSerializer::class)
        every { XstreamSerializer.renameProject(any(), any()) } throws IOException("EACCES")

        val result =
            ProjectRenameHelper.renameProject(activity, oldDir, "OpenFailOld", "OpenFailNew")

        assertNull(result)
        assertSame(project, ProjectManager.getInstance().currentProject)
        assertEquals(oldDir.absolutePath, project.directory.absolutePath)
        assertTrue(lookFile(project).exists())

        // The next save (e.g. in onPause) must not split the project into two folders
        assertTrue(saveProjectSerial(project.apply { description = "edit" }, activity))
        assertFalse(newDir.exists())
        verify(exactly = 0) { ShortcutHelper.updateShortcutOnRename(any(), any(), any()) }
    }

    @Test
    fun `renaming the open project reloads it from the new folder with all file references`() {
        val project = createSavedProject("OpenOld")
        val oldDir = project.directory
        val newDir = projectDir("OpenNew")
        addLook(project, "look1.png")
        val secondScene = Scene("Scene2", project).apply {
            // loadProject renames the first sprite of every scene to the background name
            addSprite(Sprite("Background"))
            addSprite(Sprite("Sprite2"))
        }
        project.addScene(secondScene)
        XstreamSerializer.getInstance().saveProject(project)
        openProject(project)
        ProjectManager.getInstance().currentlyEditedScene = secondScene
        ProjectManager.getInstance().currentSprite = secondScene.getSprite("Sprite2")
        project.description = "Unsaved edit"

        val result = ProjectRenameHelper.renameProject(activity, oldDir, "OpenOld", "OpenNew")

        assertEquals(newDir.absolutePath, result?.absolutePath)
        assertFalse(oldDir.exists())
        val reloaded = ProjectManager.getInstance().currentProject
        assertNotSame(project, reloaded)
        assertEquals("OpenNew", reloaded.name)
        assertEquals("Unsaved edit", reloaded.description)
        assertEquals(newDir.absolutePath, reloaded.directory.absolutePath)
        val reloadedLook = lookFile(reloaded)
        assertTrue(reloadedLook.absolutePath.startsWith(newDir.absolutePath))
        assertTrue(reloadedLook.exists())
        assertEquals("Scene2", ProjectManager.getInstance().currentlyEditedScene.name)
        assertSame(reloaded, ProjectManager.getInstance().currentlyEditedScene.project)
        assertEquals("Sprite2", ProjectManager.getInstance().currentSprite.name)
        verify { ShortcutHelper.updateShortcutOnRename(any(), "OpenOld", "OpenNew") }
    }

    @Test
    fun `renaming a project that is not open leaves the open project alone`() {
        val openProject = createSavedProject("OtherOpen")
        openProject(openProject)
        val closedDir = createSavedProject("ClosedOld").directory

        val result = ProjectRenameHelper.renameProject(activity, closedDir, "ClosedOld", "ClosedNew")

        assertNotNull(result)
        assertTrue(projectDir("ClosedNew").exists())
        assertSame(openProject, ProjectManager.getInstance().currentProject)
    }

    @Test
    fun `renaming updates the last used project name`() {
        val dir = createSavedProject("PrefOld").directory
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        preferences.edit().putString(Constants.PREF_PROJECTNAME_KEY, "PrefOld").commit()

        ProjectRenameHelper.renameProject(activity, dir, "PrefOld", "PrefNew")

        assertEquals("PrefNew", preferences.getString(Constants.PREF_PROJECTNAME_KEY, null))
    }

    @Test
    fun `serializer save waiting for the lock during a rename writes to the new folder`() {
        assertWaitingSaveFollowsRename("DirectSaveOld", "DirectSaveNew") { project ->
            XstreamSerializer.getInstance().saveProject(project)
        }
    }

    @Test
    fun `saveProjectSerial waiting for the lock during a rename writes to the new folder`() {
        assertWaitingSaveFollowsRename("SerialSaveOld", "SerialSaveNew") { project ->
            saveProjectSerial(project, activity)
        }
    }

    /**
     * Holds the save lock, starts a save of the old project object on another thread, waits
     * until that thread is blocked on the lock, then renames. The save can therefore only run
     * after the rename, which is the ordering that used to recreate the old folder.
     */
    private fun assertWaitingSaveFollowsRename(
        oldName: String,
        newName: String,
        save: (Project) -> Boolean
    ) {
        val project = createSavedProject(oldName)
        val oldDir = project.directory
        val newDir = projectDir(newName)
        openProject(project)
        val saver: Thread
        synchronized(XstreamSerializer.getSaveLock()) {
            saver = thread {
                project.description = "Edit saved after the rename"
                save(project)
            }
            awaitBlocked(saver)
            assertNotNull(ProjectRenameHelper.renameProject(activity, oldDir, oldName, newName))
        }
        saver.join(TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS))

        assertFalse("Save did not finish in time", saver.isAlive)
        assertFalse("Old project folder must not be recreated", oldDir.exists())
        val saved = XstreamSerializer.getInstance().loadProject(newDir, activity)
        assertEquals("Edit saved after the rename", saved.description)
    }

    private fun awaitBlocked(thread: Thread) {
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(TIMEOUT_SECONDS)
        while (thread.state != Thread.State.BLOCKED) {
            assertTrue("Thread never blocked on the save lock", System.nanoTime() < deadline)
            Thread.yield()
        }
    }

    private fun projectDir(name: String) = File(rootDir, name).also { createdDirs += it }

    private fun createSavedProject(name: String): Project {
        val project = Project(activity, name)
        project.directory = projectDir(name).apply { mkdirs() }
        XstreamSerializer.getInstance().saveProject(project)
        return project
    }

    private fun openProject(project: Project) {
        ProjectManager.getInstance().currentProject = project
    }

    private fun addLook(project: Project, fileName: String) {
        val scene = project.defaultScene
        val imageDir = File(scene.directory, Constants.IMAGE_DIRECTORY_NAME).apply { mkdirs() }
        val file = File(imageDir, fileName).apply { writeBytes(byteArrayOf(1, 2, 3)) }
        scene.spriteList[0].lookList.add(LookData("look", file))
    }

    private fun lookFile(project: Project): File =
        project.defaultScene.spriteList[0].lookList[0].file

    private companion object {
        const val TIMEOUT_SECONDS = 5L
    }
}
