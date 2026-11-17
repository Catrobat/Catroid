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
package org.catrobat.catroid.formulaeditor

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.SayBubbleBrick
import org.catrobat.catroid.io.XstreamSerializer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * A project can name a sensor this version does not have: earlier builds of
 * the face name feature saved ON_DEVICE_FACE_RECOGNITION, which was removed.
 * Running such a formula threw a NullPointerException and stopped the app;
 * like other undefined values it reads 0.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], qualifiers = "en")
class UnknownSensorTest {

    @After
    fun tearDown() {
        ProjectManager.getInstance().resetProjectManager()
    }

    @Test
    fun aSensorThisVersionDoesNotKnowReadsZero() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val project = Project(context, "Saved by an earlier build")
        val sprite = Sprite("Sprite")
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        val brick = XstreamSerializer.getInstance().xstream.fromXML(
            """
            <brick type="SayBubbleBrick">
              <formulaList>
                <formula category="STRING">
                  <type>SENSOR</type>
                  <value>ON_DEVICE_FACE_RECOGNITION</value>
                </formula>
              </formulaList>
            </brick>
            """.trimIndent()
        ) as SayBubbleBrick

        val value = brick.getFormulaWithBrickField(Brick.BrickField.STRING)
            .interpretObject(Scope(project, sprite, SequenceAction()))

        assertEquals(0.0, value)
    }
}
