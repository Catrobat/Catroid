/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.SpeakAndWaitAction
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.Brick.ResourcesSet
import org.catrobat.catroid.content.bricks.SpeakAndWaitBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.stage.SpeechSynthesizer
import org.catrobat.catroid.test.MockUtil
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.Mockito
import org.mockito.Mockito.spy
import java.lang.reflect.Method

class SpeakAndWaitActionTest {
    private lateinit var sprite: Sprite
    private var textNumber: Formula? = null
    private var scope: Scope? = null
    private val factory = ActionFactory()
    private val temporaryFolder = TemporaryFolder()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val contextMock = MockUtil.mockContextForProject()
        temporaryFolder.create()
        val temporaryCacheFolder = temporaryFolder.newFolder("SpeakTest")
        Mockito.`when`(contextMock.cacheDir).thenAnswer { temporaryCacheFolder }
        sprite = Sprite("testSprite")
        textNumber = Formula(888.88)
        scope = Scope(ProjectManager.getInstance().currentProject, sprite, SequenceAction())
        val project = Project(MockUtil.mockContextForProject(), "Project")
        ProjectManager.getInstance().currentProject = project
    }

    @Test
    @Throws(Exception::class)
    fun testWaitOnSynthesis() {
        val action = spy(SpeakAndWaitAction())
        val synthesizer = Mockito.mock(SpeechSynthesizer::class.java)
        action.speechSynthesizer = synthesizer

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

    @Test
    fun testSynthesizeInitialization() {
        val action = factory.createSpeakAndWaitAction(sprite, SequenceAction(), textNumber) as SpeakAndWaitAction
        Assert.assertNotNull(action.speechSynthesizer)
    }
}
