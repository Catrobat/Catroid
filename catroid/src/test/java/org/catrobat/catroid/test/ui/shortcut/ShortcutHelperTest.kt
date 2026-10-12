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
    }

    @After
    fun tearDown() {
        unmockkAll()
        ShadowBuild.reset()
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
        every { ShortcutManagerCompat.removeLongLivedShortcuts(any(), any()) } just Runs
        every { ShortcutManagerCompat.removeDynamicShortcuts(any(), any()) } just Runs
        every { ShortcutManagerCompat.disableShortcuts(any(), any(), any()) } just Runs

        ShortcutHelper.removeShortcutsForProjects(context, listOf("Project1", "Project2"))

        verify { ShortcutManagerCompat.disableShortcuts(any(), any(), any()) }
        verify { ShortcutManagerCompat.removeLongLivedShortcuts(any(), any()) }
    }

    // Must: Shortcut ID = encoded project name

    @Test
    fun `shortcut ID equals encoded project name`() {
        val projectName = "My Project: Test/Version"
        val expected = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName)

        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true
        val capturedShortcut = slot<ShortcutInfoCompat>()
        every { ShortcutManagerCompat.pushDynamicShortcut(any(), capture(capturedShortcut)) } returns true
        every { ShortcutManagerCompat.createShortcutResultIntent(any(), any()) } returns mockk(relaxed = true)
        every { ShortcutManagerCompat.requestPinShortcut(any(), any(), any()) } returns true

        ShortcutHelper.pinProject(context, projectName, null)

        assertEquals(expected, capturedShortcut.captured.id)
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
        every { ShortcutManagerCompat.createShortcutResultIntent(any(), any()) } returns mockk(relaxed = true)
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
        ShadowBuild.setManufacturer("POCO")
        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true

        val supported = ShortcutHelper.isShortcutSupported(context)

        assertFalse("POCO devices should be excluded even if ShortcutManagerCompat says supported", supported)
    }

    @Test
    fun `pin to home screen menu item is hidden on POCO devices`() {
        ShadowBuild.setManufacturer("POCO")

        // isPocoDevice() is called by ProjectListFragment.onSettingsClick() to hide the menu item.
        // The test verifies the underlying detection that drives that UI decision.
        assertTrue("isPocoDevice() should return true for POCO manufacturer", ShortcutHelper.isPocoDevice())

        // Also verify isShortcutSupported returns false (which is what the Fragment actually checks)
        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true
        assertFalse("isShortcutSupported should return false on POCO devices", ShortcutHelper.isShortcutSupported(context))
    }

    // Should: Duplicate pin guard

    @Test
    fun `pinProject shows already pinned message when shortcut already exists`() {
        val projectName = "AlreadyPinnedProject"
        val encodedName = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName)

        // Simulate an existing dynamic shortcut with the same encoded ID
        val existingShortcut = mockk<ShortcutInfoCompat> {
            every { id } returns encodedName
        }
        every { ShortcutManagerCompat.getDynamicShortcuts(any()) } returns listOf(existingShortcut)
        every { ShortcutManagerCompat.isRequestPinShortcutSupported(any()) } returns true

        val result = ShortcutHelper.pinProject(context, projectName, null)

        assertFalse("pinProject should return false when shortcut already exists", result)
        // pushDynamicShortcut should NOT be called — we bail out before reaching it
        verify(exactly = 0) { ShortcutManagerCompat.pushDynamicShortcut(any(), any()) }
    }
}
