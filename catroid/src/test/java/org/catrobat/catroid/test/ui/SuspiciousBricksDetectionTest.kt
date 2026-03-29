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

import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.BackgroundRequestBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.LookRequestBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.StartListeningBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.content.bricks.WebRequestBrick
import org.catrobat.catroid.test.StaticSingletonInitializer
import org.catrobat.catroid.ui.getListAllBricks
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.lang.reflect.Method

/**
 * Tests for the suspicious bricks detection logic as defined in CATROID-681.
 *
 * A project is considered to contain "suspicious bricks" if it contains BOTH:
 * 1. A StartListeningBrick (voice listening)
 * 2. Any of: WebRequestBrick, BackgroundRequestBrick, or LookRequestBrick (web access)
 *
 * These tests verify the detection logic in ProjectUtils.kt which is critical
 * for showing security warnings to users who download projects from the community.
 *
 * See: IDE-31, CATROID-681
 */
@RunWith(JUnit4::class)
class SuspiciousBricksDetectionTest {

    @Before
    fun setUp() {
        StaticSingletonInitializer.initializeStaticSingletonMethods()
    }


    // Since containsSuspiciousBricks is private, we test it through getListAllBricks()
    // and the public-facing suspicious brick detection logic.

    @Test
    fun testSuspiciousWhenStartListeningAndWebRequestBrickExist() {
        val bricks = listOf(
            StartListeningBrick(),
            WebRequestBrick()
        )
        assertTrue(
            "Should be suspicious when StartListeningBrick and WebRequestBrick both exist",
            containsSuspiciousBricksViaReflection(bricks)
        )
    }

    @Test
    fun testSuspiciousWhenStartListeningAndBackgroundRequestBrickExist() {
        val bricks = listOf(
            StartListeningBrick(),
            BackgroundRequestBrick()
        )
        assertTrue(
            "Should be suspicious when StartListeningBrick and BackgroundRequestBrick both exist",
            containsSuspiciousBricksViaReflection(bricks)
        )
    }

    @Test
    fun testSuspiciousWhenStartListeningAndLookRequestBrickExist() {
        val bricks = listOf(
            StartListeningBrick(),
            LookRequestBrick()
        )
        assertTrue(
            "Should be suspicious when StartListeningBrick and LookRequestBrick both exist",
            containsSuspiciousBricksViaReflection(bricks)
        )
    }

    @Test
    fun testNotSuspiciousWhenOnlyStartListeningBrickExists() {
        val bricks = listOf(
            StartListeningBrick(),
            WaitBrick(1000)
        )
        assertFalse(
            "Should NOT be suspicious when only StartListeningBrick exists without web access",
            containsSuspiciousBricksViaReflection(bricks)
        )
    }

    @Test
    fun testNotSuspiciousWhenOnlyWebRequestBrickExists() {
        val bricks = listOf(
            WebRequestBrick(),
            WaitBrick(1000)
        )
        assertFalse(
            "Should NOT be suspicious when only WebRequestBrick exists without voice listening",
            containsSuspiciousBricksViaReflection(bricks)
        )
    }

    @Test
    fun testNotSuspiciousWhenOnlyBackgroundRequestBrickExists() {
        val bricks = listOf(
            BackgroundRequestBrick(),
            WaitBrick(1000)
        )
        assertFalse(
            "Should NOT be suspicious when only BackgroundRequestBrick exists without voice listening",
            containsSuspiciousBricksViaReflection(bricks)
        )
    }

    @Test
    fun testNotSuspiciousWhenOnlyLookRequestBrickExists() {
        val bricks = listOf(
            LookRequestBrick(),
            WaitBrick(1000)
        )
        assertFalse(
            "Should NOT be suspicious when only LookRequestBrick exists without voice listening",
            containsSuspiciousBricksViaReflection(bricks)
        )
    }

    @Test
    fun testNotSuspiciousWithEmptyBrickList() {
        val bricks = emptyList<org.catrobat.catroid.content.bricks.Brick>()
        assertFalse(
            "Should NOT be suspicious with empty brick list",
            containsSuspiciousBricksViaReflection(bricks)
        )
    }

    @Test
    fun testNotSuspiciousWithOnlyNonSuspiciousBricks() {
        val bricks = listOf(
            WaitBrick(100),
            WaitBrick(200)
        )
        assertFalse(
            "Should NOT be suspicious with only non-suspicious bricks",
            containsSuspiciousBricksViaReflection(bricks)
        )
    }

    @Test
    fun testSuspiciousWithMultipleWebAccessTypes() {
        val bricks = listOf(
            StartListeningBrick(),
            WebRequestBrick(),
            BackgroundRequestBrick(),
            LookRequestBrick()
        )
        assertTrue(
            "Should be suspicious with StartListeningBrick and multiple web access bricks",
            containsSuspiciousBricksViaReflection(bricks)
        )
    }

    // Integration tests: getListAllBricks with suspicious bricks

    @Test
    fun testSuspiciousBricksDetectedInFlatScript() {
        val sprite = Sprite("TestSprite")
        val script = StartScript()
        script.addBrick(StartListeningBrick())
        script.addBrick(WebRequestBrick())
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()
        val hasSuspicious = containsSuspiciousBricksViaReflection(allBricks)

        assertTrue(
            "Suspicious bricks should be detected in a flat script",
            hasSuspicious
        )
    }

    @Test
    fun testSuspiciousBricksDetectedWhenNestedInsideForeverBrick() {
        val sprite = Sprite("TestSprite")
        val script = StartScript()

        val foreverBrick = ForeverBrick()
        foreverBrick.addBrick(StartListeningBrick())
        foreverBrick.addBrick(WebRequestBrick())
        script.addBrick(foreverBrick)
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()
        val hasSuspicious = containsSuspiciousBricksViaReflection(allBricks)

        assertTrue(
            "Suspicious bricks nested inside ForeverBrick should be detected",
            hasSuspicious
        )
    }

    @Test
    fun testSuspiciousBricksDetectedWhenNestedInsideIfBrick() {
        val sprite = Sprite("TestSprite")
        val script = StartScript()

        val ifBrick = IfLogicBeginBrick()
        ifBrick.addBrickToIfBranch(StartListeningBrick())
        ifBrick.addBrickToElseBranch(WebRequestBrick())
        script.addBrick(ifBrick)
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()
        val hasSuspicious = containsSuspiciousBricksViaReflection(allBricks)

        assertTrue(
            "Suspicious bricks: one in if-branch and one in else-branch should be detected",
            hasSuspicious
        )
    }

    @Test
    fun testSuspiciousBricksDetectedWhenNestedInsideRepeatBrick() {
        val sprite = Sprite("TestSprite")
        val script = StartScript()

        val repeatBrick = RepeatBrick()
        repeatBrick.addBrick(StartListeningBrick())
        repeatBrick.addBrick(BackgroundRequestBrick())
        script.addBrick(repeatBrick)
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()
        val hasSuspicious = containsSuspiciousBricksViaReflection(allBricks)

        assertTrue(
            "Suspicious bricks nested inside RepeatBrick should be detected",
            hasSuspicious
        )
    }

    @Test
    fun testSuspiciousBricksSpreadAcrossFlatAndNestedScopes() {
        val sprite = Sprite("TestSprite")
        val script = StartScript()

        // StartListeningBrick at top level
        script.addBrick(StartListeningBrick())

        // WebRequestBrick inside ForeverBrick (nested)
        val foreverBrick = ForeverBrick()
        foreverBrick.addBrick(WebRequestBrick())
        script.addBrick(foreverBrick)
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()
        val hasSuspicious = containsSuspiciousBricksViaReflection(allBricks)

        assertTrue(
            "Suspicious bricks should be detected even when spread across flat and nested scopes",
            hasSuspicious
        )
    }

    @Test
    fun testSuspiciousBricksSpreadAcrossMultipleScripts() {
        val sprite = Sprite("TestSprite")

        val script1 = StartScript()
        script1.addBrick(StartListeningBrick())
        sprite.addScript(script1)

        val script2 = StartScript()
        script2.addBrick(LookRequestBrick())
        sprite.addScript(script2)

        val allBricks = sprite.getListAllBricks()
        val hasSuspicious = containsSuspiciousBricksViaReflection(allBricks)

        assertTrue(
            "Suspicious bricks spread across different scripts should be detected",
            hasSuspicious
        )
    }

    @Test
    fun testNotSuspiciousWhenNestedBricksAreOnlyNonSuspicious() {
        val sprite = Sprite("TestSprite")
        val script = StartScript()

        val foreverBrick = ForeverBrick()
        foreverBrick.addBrick(WaitBrick(100))
        foreverBrick.addBrick(WaitBrick(200))
        script.addBrick(foreverBrick)
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()
        val hasSuspicious = containsSuspiciousBricksViaReflection(allBricks)

        assertFalse(
            "Non-suspicious nested bricks should not trigger detection",
            hasSuspicious
        )
    }

    //  shouldDisplaySuspiciousBricksWarning() tests via Project

    @Test
    fun testProjectWithSuspiciousBricksShouldWarn() {
        val project = createProjectWithBricks(
            listOf(StartListeningBrick(), WebRequestBrick())
        )

        val shouldWarn = shouldDisplaySuspiciousBricksWarningViaReflection(project)

        assertTrue(
            "Project with both StartListeningBrick and WebRequestBrick should trigger warning",
            shouldWarn
        )
    }

    @Test
    fun testProjectWithoutSuspiciousBricksShouldNotWarn() {
        val project = createProjectWithBricks(
            listOf(WaitBrick(100))
        )

        val shouldWarn = shouldDisplaySuspiciousBricksWarningViaReflection(project)

        assertFalse(
            "Project without suspicious bricks should not trigger warning",
            shouldWarn
        )
    }

    @Test
    fun testProjectWithSuspiciousBricksAcrossMultipleScenes() {
        val project = Project()
        project.sceneList.clear()

        val scene1 = Scene("Scene1", project)
        val sprite1 = Sprite("Sprite1")
        val script1 = StartScript()
        script1.addBrick(StartListeningBrick())
        sprite1.addScript(script1)
        scene1.addSprite(sprite1)
        project.addScene(scene1)

        val scene2 = Scene("Scene2", project)
        val sprite2 = Sprite("Sprite2")
        val script2 = StartScript()
        script2.addBrick(WebRequestBrick())
        sprite2.addScript(script2)
        scene2.addSprite(sprite2)
        project.addScene(scene2)

        val shouldWarn = shouldDisplaySuspiciousBricksWarningViaReflection(project)

        assertTrue(
            "Project with suspicious bricks spread across multiple scenes should trigger warning",
            shouldWarn
        )
    }

    @Test
    fun testProjectWithSuspiciousBricksAcrossMultipleSprites() {
        val project = Project()
        project.sceneList.clear()

        val scene = Scene("Scene1", project)
        val sprite1 = Sprite("Sprite1")
        val script1 = StartScript()
        script1.addBrick(StartListeningBrick())
        sprite1.addScript(script1)
        scene.addSprite(sprite1)

        val sprite2 = Sprite("Sprite2")
        val script2 = StartScript()
        script2.addBrick(BackgroundRequestBrick())
        sprite2.addScript(script2)
        scene.addSprite(sprite2)

        project.addScene(scene)

        val shouldWarn = shouldDisplaySuspiciousBricksWarningViaReflection(project)

        assertTrue(
            "Project with suspicious bricks spread across multiple sprites should trigger warning",
            shouldWarn
        )
    }

    @Test
    fun testProjectWithSuspiciousBricksNestedInsideLoopShouldWarn() {
        val project = Project()
        project.sceneList.clear()

        val scene = Scene("Scene1", project)
        val sprite = Sprite("Sprite1")
        val script = StartScript()

        val foreverBrick = ForeverBrick()
        foreverBrick.addBrick(StartListeningBrick())
        foreverBrick.addBrick(LookRequestBrick())
        script.addBrick(foreverBrick)
        sprite.addScript(script)
        scene.addSprite(sprite)
        project.addScene(scene)

        val shouldWarn = shouldDisplaySuspiciousBricksWarningViaReflection(project)

        assertTrue(
            "Project with suspicious bricks nested inside loops should trigger warning. " +
                "This is the exact regression scenario described in IDE-31.",
            shouldWarn
        )
    }

    @Test
    fun testEmptyProjectShouldNotWarn() {
        val project = Project()

        val shouldWarn = shouldDisplaySuspiciousBricksWarningViaReflection(project)

        assertFalse(
            "Empty project should not trigger warning",
            shouldWarn
        )
    }

    //  Helper methods

    /**
     * Uses reflection to call the private extension function containsSuspiciousBricks()
     * on a List<Brick>. This is necessary because the function is private in ProjectUtils.kt.
     */
    private fun containsSuspiciousBricksViaReflection(
        bricks: List<org.catrobat.catroid.content.bricks.Brick>
    ): Boolean {
        val clazz = Class.forName("org.catrobat.catroid.ui.ProjectUtils")
        val method: Method = clazz.getDeclaredMethod(
            "containsSuspiciousBricks",
            List::class.java
        )
        method.isAccessible = true
        return method.invoke(null, bricks) as Boolean
    }

    /**
     * Uses reflection to call the private extension function shouldDisplaySuspiciousBricksWarning()
     * on a Project. This is necessary because the function is private in ProjectUtils.kt.
     */
    private fun shouldDisplaySuspiciousBricksWarningViaReflection(
        project: Project
    ): Boolean {
        val clazz = Class.forName("org.catrobat.catroid.ui.ProjectUtils")
        val method: Method = clazz.getDeclaredMethod(
            "shouldDisplaySuspiciousBricksWarning",
            Project::class.java
        )
        method.isAccessible = true
        return method.invoke(null, project) as Boolean
    }

    /**
     * Creates a simple Project with one scene, one sprite, and a script containing the given bricks.
     */
    private fun createProjectWithBricks(
        bricks: List<org.catrobat.catroid.content.bricks.Brick>
    ): Project {
        val project = Project()
        project.sceneList.clear()
        val scene = Scene("TestScene", project)
        val sprite = Sprite("TestSprite")
        val script = StartScript()
        bricks.forEach { script.addBrick(it) }
        sprite.addScript(script)
        scene.addSprite(sprite)
        project.addScene(scene)
        return project
    }
}
