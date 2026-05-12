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

package org.catrobat.catroid.stage

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Matrix4
import org.catrobat.catroid.embroidery.EmbroideryPatternManager
import org.catrobat.catroid.embroidery.StitchPoint
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock

class EmbroideryActorProjectionTest {

    @Test
    fun testDrawUsesBatchMatricesForEmbroideryRendering() {
        val embroideryPatternManager = mock(EmbroideryPatternManager::class.java)
        val firstPoint = connectingPoint()
        val secondPoint = connectingPoint()
        `when`(embroideryPatternManager.embroideryPatternList).thenReturn(
            listOf(firstPoint, secondPoint)
        )

        val renderer = mock(ShapeRenderer::class.java)
        val batch = mock(Batch::class.java)
        val projectionMatrix = Matrix4()
        val transformMatrix = Matrix4()
        `when`(batch.projectionMatrix).thenReturn(projectionMatrix)
        `when`(batch.transformMatrix).thenReturn(transformMatrix)

        val actor = EmbroideryActor(1.0f, embroideryPatternManager, renderer)

        actor.draw(batch, 1.0f)

        inOrder(batch, renderer).apply {
            verify(batch).end()
            verify(renderer).setProjectionMatrix(projectionMatrix)
            verify(renderer).setTransformMatrix(transformMatrix)
            verify(renderer).begin(ShapeRenderer.ShapeType.Filled)
            verify(batch).begin()
        }
    }

    private fun connectingPoint(): StitchPoint =
        mock(StitchPoint::class.java).also {
            `when`(it.isConnectingPoint).thenReturn(true)
        }
}
