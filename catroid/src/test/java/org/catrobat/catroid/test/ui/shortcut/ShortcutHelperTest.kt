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

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import org.robolectric.RuntimeEnvironment
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.catrobat.catroid.ui.shortcut.ShortcutHelper
import org.catrobat.catroid.ui.shortcut.ShortcutTrampolineActivity
import org.catrobat.catroid.utils.FileMetaDataExtractor
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowBuild

/**
 * Unit tests for [ShortcutHelper].
 *
 * Tests here serve four roles beyond bug detection: behavioral specification
 * of the feature contract, guardrails for safe future refactoring, collective
 * code ownership enablers so any team member can understand edge cases at a
 * glance, and a reliability layer for AI-assisted development.
 *
 * Each test name is a declarative sentence describing expected behavior.
 */
@RunWith(RobolectricTestRunner::class)
class ShortcutHelperTest {

    private val context get() = RuntimeEnvironment.getApplication()

    @Before
    fun setUp() {
        mockkStatic(ShortcutManagerCompat::class)
        every { ShortcutManagerCompat.getDynamicShortcuts(any()) } returns emptyList()
        every { ShortcutManagerCompat.getShortcuts(any(), any()) } returns emptyList()
        every { ShortcutManagerCompat.getMaxShortcutCountPerActivity(any()) } returns 5
    }

    @After
    fun tearDown() {
        unmockkAll()
        ShadowBuild.reset()
    }

    private fun projectShortcut(
        id: String,
        projectName: String,
        label: String = projectName
    ): ShortcutInfoCompat = mockk {
        every { this@mockk.id } returns id
        every { shortLabel } returns label
        every { isEnabled } returns true
        every { intent } returns Intent().putExtra(
            ShortcutTrampolineActivity.EXTRA_PROJECT_NAME,
            projectName
        )
    }

    // Must: Launcher unsupported guard

    @Test
    fun `pinProject returns false when launcher does not support shortcuts`() {
        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns false
        every { ShortcutManagerCompat.pushDynamicShortcut(any(), any()) } returns true
        every { ShortcutManagerCompat.requestPinShortcut(any(), any(), any()) } returns false

        // ShortcutHelper no longer shows Toast — just returns false
        // UI layer (Fragment) is responsible for Snackbar feedback
        val result = ShortcutHelper.pinProject(context, "TestProject", null)

        assertFalse(result)
        verify { ShortcutManagerCompat.pushDynamicShortcut(any(), any()) }
    }

    // Must: Blank project name rejected

    @Test
    fun `blank project name produces empty encoded shortcut ID`() {
        val encoded = FileMetaDataExtractor.encodeSpecialCharsForFileSystem("")
        assertEquals("", encoded)
    }

    // Must: Rename calls updateShortcuts

    @Test
    fun `rename calls updateShortcuts with new label`() = runTest {
        every { ShortcutManagerCompat.getDynamicShortcuts(any()) } returns listOf(
            projectShortcut("OldName", "OldName")
        )
        every { ShortcutManagerCompat.updateShortcuts(any(), any()) } returns true

        mockkObject(ShortcutHelper)
        coEvery { ShortcutHelper.loadProjectIcon(any()) } returns null

        ShortcutHelper.updateShortcutOnRename(context, "OldName", "NewName")

        verify {
            ShortcutManagerCompat.updateShortcuts(any(), match { shortcuts ->
                shortcuts.size == 1 && shortcuts[0].shortLabel == "NewName"
            })
        }
    }

    // Must: Delete calls disableShortcuts

    @Test
    fun `delete calls disableShortcuts for removed projects`() {
        every { ShortcutManagerCompat.getDynamicShortcuts(any()) } returns listOf(
            projectShortcut("Project1", "Project1"),
            projectShortcut("Project2", "Project2")
        )
        every { ShortcutManagerCompat.removeLongLivedShortcuts(any(), any()) } just Runs
        every { ShortcutManagerCompat.removeDynamicShortcuts(any(), any()) } just Runs
        every { ShortcutManagerCompat.disableShortcuts(any(), any(), any()) } just Runs

        ShortcutHelper.removeShortcutsForProjects(context, listOf("Project1", "Project2"))

        verify {
            ShortcutManagerCompat.disableShortcuts(any(), listOf("Project1", "Project2"), any())
        }
        verify { ShortcutManagerCompat.removeLongLivedShortcuts(any(), any()) }
    }

    // Must: Shortcut ID = encoded project name

    @Test
    fun `shortcut ID equals encoded project name`() {
        val projectName = "My Project: Test/Version"
        val expected = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName)

        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true
        val capturedShortcut = slot<ShortcutInfoCompat>()
        every {
            ShortcutManagerCompat.pushDynamicShortcut(
                any(),
                capture(capturedShortcut)
            )
        } returns true
        every { ShortcutManagerCompat.createShortcutResultIntent(any(), any()) } returns mockk(
            relaxed = true
        )
        every { ShortcutManagerCompat.requestPinShortcut(any(), any(), any()) } returns true

        ShortcutHelper.pinProject(context, projectName, null)

        assertEquals(expected, capturedShortcut.captured.id)
    }

    @Test
    fun `pinProject uses custom shortcut label when provided`() {
        val projectName = "My Original Project"
        val customLabel = "Custom Home Nickname"

        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true
        val capturedShortcut = slot<ShortcutInfoCompat>()
        every {
            ShortcutManagerCompat.pushDynamicShortcut(
                any(),
                capture(capturedShortcut)
            )
        } returns true
        every { ShortcutManagerCompat.createShortcutResultIntent(any(), any()) } returns mockk(
            relaxed = true
        )
        every { ShortcutManagerCompat.requestPinShortcut(any(), any(), any()) } returns true

        ShortcutHelper.pinProject(context, projectName, null, shortcutLabel = customLabel)

        assertEquals(customLabel, capturedShortcut.captured.shortLabel)
        assertEquals(
            FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName),
            capturedShortcut.captured.id
        )
    }

    // Must: OOM full-res fallback — double OOM returns null

    @Test
    fun `decodeBitmapWithFallback returns null on double OOM`() {
        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeFile(any(), any()) } throws OutOfMemoryError("test OOM")

        val method = ShortcutHelper::class.java.getDeclaredMethod(
            "decodeBitmapWithFallback", String::class.java
        )
        method.isAccessible = true

        val result = method.invoke(ShortcutHelper, "/fake/path.png")
        assertNull(result)
    }

    // Must: OOM RGB_565 fallback — retries at half-resolution

    @Test
    fun `decodeBitmapWithFallback retries RGB565 after ARGB OOM`() {
        mockkStatic(BitmapFactory::class)
        val fakeBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.RGB_565)

        var callCount = 0
        every { BitmapFactory.decodeFile(any(), any()) } answers {
            callCount++
            if (callCount == 1) throw OutOfMemoryError("first attempt OOM")
            fakeBitmap
        }

        val method = ShortcutHelper::class.java.getDeclaredMethod(
            "decodeBitmapWithFallback", String::class.java
        )
        method.isAccessible = true

        val result = method.invoke(ShortcutHelper, "/fake/path.png") as? Bitmap
        assertNotNull(result)
        assertEquals(2, callCount)
    }

    // Must: Default drawable when icon is null

    @Test
    fun `pinProject uses default drawable when icon is null`() {
        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true
        every { ShortcutManagerCompat.pushDynamicShortcut(any(), any()) } returns true
        every { ShortcutManagerCompat.createShortcutResultIntent(any(), any()) } returns mockk(
            relaxed = true
        )
        every { ShortcutManagerCompat.requestPinShortcut(any(), any(), any()) } returns true

        // Should not throw even with null icon
        ShortcutHelper.pinProject(context, "TestProject", null)

        verify { ShortcutManagerCompat.pushDynamicShortcut(any(), any()) }
    }

    // Should: Reflection failure defaults to granted (HyperOS safety)

    @Test
    fun `isShortcutPermissionGranted returns true when reflection fails`() {
        ShadowBuild.setManufacturer("Xiaomi")

        // On a non-MIUI JVM, the MIUI AppOps reflection will fail.
        // The method should default to TRUE to avoid blocking the user.
        val result = ShortcutHelper.isShortcutPermissionGranted(context)
        assertTrue("Should default to granted when reflection fails", result)
    }

    // Should: Probe cleans up dummy shortcut

    @Test
    fun `probeIsShortcutCreationBlocked cleans up dummy shortcut`() = runTest {
        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true
        every { ShortcutManagerCompat.pushDynamicShortcut(any(), any()) } returns true
        every { ShortcutManagerCompat.getDynamicShortcuts(any()) } returns emptyList()
        every { ShortcutManagerCompat.removeDynamicShortcuts(any(), any()) } just Runs

        ShortcutHelper.probeIsShortcutCreationBlocked(context)

        verify { ShortcutManagerCompat.removeDynamicShortcuts(any(), any()) }
    }

    // Should: Probe detects blocked state

    @Test
    fun `probeIsShortcutCreationBlocked detects blocked state`() = runTest {
        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true
        every { ShortcutManagerCompat.pushDynamicShortcut(any(), any()) } returns true
        // Empty list = the OS silently rejected the shortcut
        every { ShortcutManagerCompat.getDynamicShortcuts(any()) } returns emptyList()
        every { ShortcutManagerCompat.removeDynamicShortcuts(any(), any()) } just Runs

        val blocked = ShortcutHelper.probeIsShortcutCreationBlocked(context)
        assertTrue("Should detect blocked state when shortcut doesn't appear", blocked)
    }

    // Must: Xiaomi device detection

    @Test
    fun `isXiaomiDevice detects Xiaomi manufacturer`() {
        ShadowBuild.setManufacturer("Xiaomi")
        assertTrue(ShortcutHelper.isXiaomiDevice())
    }

    @Test
    fun `isXiaomiDevice detects Redmi and POCO brands`() {
        ShadowBuild.setManufacturer("Redmi")
        assertTrue(ShortcutHelper.isXiaomiDevice())

        ShadowBuild.setManufacturer("POCO")
        assertTrue(ShortcutHelper.isXiaomiDevice())

        ShadowBuild.setManufacturer("Samsung")
        assertFalse(ShortcutHelper.isXiaomiDevice())
    }

    // Should: POCO device exclusion

    @Test
    fun `isShortcutSupported returns false on POCO devices`() {
        ShadowBuild.setManufacturer("Xiaomi")
        ShadowBuild.setBrand("POCO")
        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true

        val supported = ShortcutHelper.isShortcutSupported(context)

        assertFalse(
            "POCO devices should be excluded even if ShortcutManagerCompat says supported",
            supported
        )
    }

    @Test
    fun `pin to home screen menu item is hidden on POCO devices`() {
        ShadowBuild.setManufacturer("Xiaomi")
        ShadowBuild.setBrand("POCO")

        // isPocoDevice() is called by ProjectListFragment.onSettingsClick() to hide the menu item.
        // The test verifies the underlying detection that drives that UI decision.
        assertTrue(
            "isPocoDevice() should return true for POCO brand",
            ShortcutHelper.isPocoDevice()
        )

        // Also verify isShortcutSupported returns false (which is what the Fragment actually checks)
        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true
        assertFalse(
            "isShortcutSupported should return false on POCO devices",
            ShortcutHelper.isShortcutSupported(context)
        )
    }

    @Test
    fun `isPocoDevice detects POCO by manufacturer brand or model`() {
        ShadowBuild.reset()
        ShadowBuild.setManufacturer("Xiaomi")
        ShadowBuild.setBrand("POCO")
        assertTrue(
            "Should detect POCO when manufacturer is Xiaomi and brand is POCO",
            ShortcutHelper.isPocoDevice()
        )

        ShadowBuild.reset()
        ShadowBuild.setManufacturer("POCO")
        assertTrue("Should detect POCO when manufacturer is POCO", ShortcutHelper.isPocoDevice())

        ShadowBuild.reset()
        ShadowBuild.setModel("POCO F5")
        assertTrue("Should detect POCO when model contains POCO", ShortcutHelper.isPocoDevice())

        ShadowBuild.reset()
        ShadowBuild.setManufacturer("Samsung")
        ShadowBuild.setBrand("Samsung")
        assertFalse("Should return false for non-POCO device", ShortcutHelper.isPocoDevice())
    }

    // Should: Re-pin and duplicate handling

    @Test
    fun `pinProject updates and re-requests pin when shortcut already exists`() {
        val projectName = "AlreadyPinnedProject"
        val encodedName = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName)

        val existingShortcut = projectShortcut(encodedName, projectName)
        every { ShortcutManagerCompat.getDynamicShortcuts(any()) } returns listOf(existingShortcut)
        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true
        every { ShortcutManagerCompat.updateShortcuts(any(), any()) } returns true
        every { ShortcutManagerCompat.createShortcutResultIntent(any(), any()) } returns mockk(
            relaxed = true
        )
        every { ShortcutManagerCompat.requestPinShortcut(any(), any(), any()) } returns true

        val result = ShortcutHelper.pinProject(context, projectName, null)

        assertTrue(
            "pinProject should return true and re-request pin when shortcut already exists",
            result
        )
        verify(exactly = 0) { ShortcutManagerCompat.pushDynamicShortcut(any(), any()) }
        verify { ShortcutManagerCompat.updateShortcuts(any(), any()) }
        verify { ShortcutManagerCompat.requestPinShortcut(any(), any(), any()) }
    }

    @Test
    fun `sequential renames preserve original shortcut ID`() = runTest {
        every { ShortcutManagerCompat.updateShortcuts(any(), any()) } returns true

        mockkObject(ShortcutHelper)
        coEvery { ShortcutHelper.loadProjectIcon(any()) } returns null

        val originalId = FileMetaDataExtractor.encodeSpecialCharsForFileSystem("Name1")
        val existingShortcut = projectShortcut(originalId, "Name2", "Name2")
        every { ShortcutManagerCompat.getDynamicShortcuts(any()) } returns listOf(existingShortcut)

        // Rename from Name2 to Name3 — it should still update the originalId
        ShortcutHelper.updateShortcutOnRename(context, "Name2", "Name3")

        verify {
            ShortcutManagerCompat.updateShortcuts(any(), match { shortcuts ->
                shortcuts.size == 1 && shortcuts[0].id == originalId && shortcuts[0].shortLabel == "Name3"
            })
        }
    }

    @Test
    fun `reusing project name allocates distinct shortcut ID`() {
        val originalId = FileMetaDataExtractor.encodeSpecialCharsForFileSystem("ProjectA")
        // Project A was pinned, then renamed to B, so its shortcut ID is still "ProjectA"
        val existingShortcutB = projectShortcut(originalId, "ProjectB", "ProjectB")
        every { ShortcutManagerCompat.getDynamicShortcuts(any()) } returns listOf(existingShortcutB)
        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true
        val pushedShortcut = slot<ShortcutInfoCompat>()
        every { ShortcutManagerCompat.pushDynamicShortcut(any(), capture(pushedShortcut)) } returns true
        every { ShortcutManagerCompat.createShortcutResultIntent(any(), any()) } returns mockk(
            relaxed = true
        )
        every { ShortcutManagerCompat.requestPinShortcut(any(), any(), any()) } returns true

        // Now a new project named ProjectA is pinned
        val result = ShortcutHelper.pinProject(context, "ProjectA", null)

        assertTrue(result)
        verify { ShortcutManagerCompat.pushDynamicShortcut(any(), any()) }
        assertEquals("${originalId}_2", pushedShortcut.captured.id)
    }

    @Test
    fun `deleting project does not touch another project with matching custom label`() {
        // Project A has custom label "ProjectB"
        val shortcutA = projectShortcut("ProjectA", "ProjectA", label = "ProjectB")
        every { ShortcutManagerCompat.getDynamicShortcuts(any()) } returns listOf(shortcutA)
        every { ShortcutManagerCompat.removeLongLivedShortcuts(any(), any()) } just Runs
        every { ShortcutManagerCompat.removeDynamicShortcuts(any(), any()) } just Runs
        every { ShortcutManagerCompat.disableShortcuts(any(), any(), any()) } just Runs

        // Deleting Project B should NOT disable Project A's shortcut
        ShortcutHelper.removeShortcutsForProjects(context, listOf("ProjectB"))

        verify(exactly = 0) { ShortcutManagerCompat.disableShortcuts(any(), any(), any()) }
    }

    @Test
    fun `renaming project does not touch another project with matching custom label`() = runTest {
        // Project A has custom label "ProjectB"
        val shortcutA = projectShortcut("ProjectA", "ProjectA", label = "ProjectB")
        every { ShortcutManagerCompat.getDynamicShortcuts(any()) } returns listOf(shortcutA)
        every { ShortcutManagerCompat.updateShortcuts(any(), any()) } returns true

        mockkObject(ShortcutHelper)
        coEvery { ShortcutHelper.loadProjectIcon(any()) } returns null

        // Renaming Project B to Project C should NOT update Project A's shortcut
        ShortcutHelper.updateShortcutOnRename(context, "ProjectB", "ProjectC")

        verify(exactly = 0) { ShortcutManagerCompat.updateShortcuts(any(), any()) }
    }

    @Test
    fun `probeIsShortcutCreationBlocked is skipped when dynamic list is full`() = runTest {
        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true
        every { ShortcutManagerCompat.getMaxShortcutCountPerActivity(any()) } returns 2
        every { ShortcutManagerCompat.getDynamicShortcuts(any()) } returns listOf(
            projectShortcut("1", "P1"),
            projectShortcut("2", "P2")
        )

        val blocked = ShortcutHelper.probeIsShortcutCreationBlocked(context)
        assertFalse("Probe should be skipped and return false when dynamic list is full", blocked)
        verify(exactly = 0) { ShortcutManagerCompat.pushDynamicShortcut(any(), any()) }
    }

    @Test
    fun `removeShortcutsForProjects resolves and disables shortcuts by launch intent`() {
        val originalId = FileMetaDataExtractor.encodeSpecialCharsForFileSystem("OriginalName")
        val existingShortcut = projectShortcut(originalId, "RenamedName", "RenamedName")
        every { ShortcutManagerCompat.getDynamicShortcuts(any()) } returns listOf(existingShortcut)
        every { ShortcutManagerCompat.removeLongLivedShortcuts(any(), any()) } just Runs
        every { ShortcutManagerCompat.removeDynamicShortcuts(any(), any()) } just Runs
        every { ShortcutManagerCompat.disableShortcuts(any(), any(), any()) } just Runs

        ShortcutHelper.removeShortcutsForProjects(context, listOf("RenamedName"))

        verify {
            ShortcutManagerCompat.removeDynamicShortcuts(any(), listOf(originalId))
            ShortcutManagerCompat.disableShortcuts(any(), listOf(originalId), any())
        }
    }

    @Test
    fun `canAddMoreShortcuts reflects system limit`() {
        every { ShortcutManagerCompat.getMaxShortcutCountPerActivity(any()) } returns 5
        assertTrue(ShortcutHelper.canAddMoreShortcuts(context))

        every { ShortcutManagerCompat.getMaxShortcutCountPerActivity(any()) } returns 0
        assertFalse(ShortcutHelper.canAddMoreShortcuts(context))
    }

    @Test
    fun `probeIsShortcutCreationBlocked returns false when probe exists in dynamic list`() =
        runTest {
            val slot = slot<ShortcutInfoCompat>()
            every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true
            every { ShortcutManagerCompat.pushDynamicShortcut(any(), capture(slot)) } answers {
                every { ShortcutManagerCompat.getDynamicShortcuts(any()) } returns listOf(
                    mockk { every { id } returns slot.captured.id }
                )
                true
            }
            every { ShortcutManagerCompat.removeDynamicShortcuts(any(), any()) } just Runs

            val blocked = ShortcutHelper.probeIsShortcutCreationBlocked(context)
            assertFalse("Probe should report unblocked when dummy shortcut is found", blocked)
        }

    @Test
    fun `verifyShortcutPermission returns true immediately for non-Xiaomi devices`() = runTest {
        ShadowBuild.setManufacturer("Google")
        ShadowBuild.setBrand("Pixel")

        val result = ShortcutHelper.verifyShortcutPermission(context)
        assertTrue("Non-Xiaomi devices should always return true for shortcut permission", result)
    }

    @Test
    fun `pinProject re-enables disabled shortcut before reusing its ID when recreating project`() {
        val projectName = "DeletedAndRecreatedProject"
        val encodedId = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName)

        val disabledShortcut = mockk<ShortcutInfoCompat> {
            every { id } returns encodedId
            every { shortLabel } returns projectName
            every { isEnabled } returns false
            every { intent } returns Intent().putExtra(
                ShortcutTrampolineActivity.EXTRA_PROJECT_NAME,
                projectName
            )
        }

        every { ShortcutManagerCompat.getDynamicShortcuts(any()) } returns emptyList()
        every {
            ShortcutManagerCompat.getShortcuts(any(), ShortcutManagerCompat.FLAG_MATCH_PINNED)
        } returns listOf(disabledShortcut)
        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true
        every { ShortcutManagerCompat.enableShortcuts(any(), any()) } just Runs
        every { ShortcutManagerCompat.updateShortcuts(any(), any()) } returns true
        every { ShortcutManagerCompat.createShortcutResultIntent(any(), any()) } returns mockk(
            relaxed = true
        )
        every { ShortcutManagerCompat.requestPinShortcut(any(), any(), any()) } returns true

        val result = ShortcutHelper.pinProject(context, projectName, null)

        assertTrue(
            "pinProject should return true when re-pinning previously deleted project",
            result
        )
        verify {
            ShortcutManagerCompat.enableShortcuts(any(), match { list ->
                list.size == 1 && list[0].id == encodedId
            })
        }
        verify { ShortcutManagerCompat.updateShortcuts(any(), any()) }
        verify { ShortcutManagerCompat.requestPinShortcut(any(), any(), any()) }
    }

    @Test
    fun `pinProject handles exception from createShortcutResultIntent gracefully`() {
        val projectName = "TestProject"
        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true
        every { ShortcutManagerCompat.pushDynamicShortcut(any(), any()) } returns true
        every {
            ShortcutManagerCompat.createShortcutResultIntent(any(), any())
        } throws IllegalArgumentException("Shortcut is disabled")
        every { ShortcutManagerCompat.requestPinShortcut(any(), any(), null) } returns true

        val result = ShortcutHelper.pinProject(context, projectName, null)
        assertTrue(result)
        verify { ShortcutManagerCompat.requestPinShortcut(any(), any(), null) }
    }
}
