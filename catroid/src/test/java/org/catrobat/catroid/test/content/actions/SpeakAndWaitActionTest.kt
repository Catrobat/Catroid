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

package org.catrobat.catroid.test.content.actions

import android.content.Context
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.SpeakAndWaitAction
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.Brick.ResourcesSet
import org.catrobat.catroid.content.bricks.SpeakAndWaitBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.koin.projectManagerModule
import org.catrobat.catroid.koin.stop
import org.catrobat.catroid.stage.SpeechSynthesizer
import org.catrobat.catroid.test.MockUtil
import org.catrobat.catroid.utils.MobileServiceAvailability
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.koin.core.module.Module
import org.koin.java.KoinJavaComponent.inject
import org.mockito.Mockito
import org.mockito.Mockito.spy
import java.lang.reflect.Method
import java.util.Collections

class SpeakAndWaitActionTest {
    private lateinit var sprite: Sprite
    private var scope: Scope? = null
    private val temporaryFolder = TemporaryFolder()
    lateinit var mobileServiceAvailability: MobileServiceAvailability
    lateinit var contextMock: Context

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private val dependencyModules: List<Module> = Collections.singletonList(projectManagerModule)

    @Before
    @Throws(Exception::class)
    fun setUp() {
        contextMock = MockUtil.mockContextForProject(dependencyModules)
        temporaryFolder.create()
        val temporaryCacheFolder = temporaryFolder.newFolder("SpeakTest")
        Mockito.`when`(contextMock.cacheDir).thenAnswer { temporaryCacheFolder }
        mobileServiceAvailability = Mockito.mock(MobileServiceAvailability::class.java)
        Mockito.`when`(mobileServiceAvailability.isGmsAvailable(contextMock)).thenReturn(true)
        sprite = Sprite("testSprite")
        scope = Scope(projectManager.currentProject, sprite, SequenceAction())
        val project = Project(contextMock, "Project")
        projectManager.currentProject = project
    }

    @After
    fun tearDown() {
        stop(dependencyModules)
    }

    @Test
    @Throws(Exception::class)
    fun testWaitOnSynthesis() {
        val action = spy(SpeakAndWaitAction())
        val synthesizer = Mockito.mock(SpeechSynthesizer::class.java)
        action.speechSynthesizer = synthesizer
        action.mobileServiceAvailability = mobileServiceAvailability
        action.context = contextMock

        action.act(0.1f)
        assert(!action.synthesizingFinished)
        assertEquals(Float.MAX_VALUE, action.duration, 0.0f)

        val onDone: Method = SpeakAndWaitAction::class.java.getDeclaredMethod("onDone")
        onDone.isAccessible = true
        onDone.invoke(action)

        assert(action.synthesizingFinished)
        action.act(0.1f)
        assertEquals(0.0f, action.duration, 0.0f)
    }

    @Test
    fun testRequirements() {
        val speakAndWaitBrick = SpeakAndWaitBrick(Formula(""))
        val resourcesSet = ResourcesSet()
        speakAndWaitBrick.addRequiredResources(resourcesSet)
        Assert.assertTrue(resourcesSet.contains(Brick.TEXT_TO_SPEECH))
    }
}
