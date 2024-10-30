/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

package org.catrobat.catroid.plot

import android.graphics.PointF
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Queue
import org.catrobat.catroid.stage.StageActivity

class Plot
{
    private var isPlotting = false
    private var dataPointLists = ArrayList<ArrayList<PointF>>()
    private var drawQueue = Queue<Queue<PointF>>()

    var width = 0.0F
    var height = 0.0F


    fun pause(){
        isPlotting = false;
    }

    fun resume(){
        isPlotting = true;
    }

    fun isPlotting(): Boolean {
        return isPlotting
    }

    fun startNewPlotLine(){
        dataPointLists.add(ArrayList())
        drawQueue.addLast(Queue())
    }
    fun startNewPlotLine(point : PointF){
        dataPointLists.add(arrayListOf(point))
        drawQueue.addLast(Queue())
        drawQueue.last().addLast(point)
    }

    fun addPoint(point : PointF){
        dataPointLists.last().add(point)
        drawQueue.last().addLast(point)
    }

    fun data() : ArrayList<ArrayList<PointF>>{
        return dataPointLists
    }

    private fun canDraw(): Boolean {
        return drawQueue.size > 2 || (!drawQueue.isEmpty && drawQueue.last().size > 2)
    }

    private fun updateQueue(){
        if (drawQueue.isEmpty || drawQueue.size == 1) return
        if(drawQueue.first().size == 1)
            drawQueue.removeFirst()
    }

    fun drawLinesForSprite(screenRatio: Float, camera: Camera?) {
        if (camera == null)
            return

        val renderer = StageActivity.stageListener.shapeRenderer
        renderer.color = Color(0.0F, 0.0F, 0.0F, 255.0F)
        renderer.begin(ShapeRenderer.ShapeType.Filled)

        while (canDraw()) {
            drawLine(screenRatio, renderer, camera)
            updateQueue()
        }

        renderer.end()
        width = camera.viewportWidth
        height = camera.viewportHeight

    }

    private fun drawLine(screenRatio: Float, renderer: ShapeRenderer, camera: Camera) {
        val currentPosition: PointF = drawQueue.first().removeFirst()
        val nextPosition: PointF = drawQueue.first().first()
        currentPosition.x += camera.position.x
        currentPosition.y += camera.position.y
        nextPosition.x += camera.position.x
        nextPosition.y += camera.position.y
        if (currentPosition.x != nextPosition.x || currentPosition.y != nextPosition.y) {
            val penSize: Float = screenRatio * 2.0F
            renderer.circle(currentPosition.x, currentPosition.y, penSize / 2)
            renderer.rectLine(
                currentPosition.x, currentPosition.y, nextPosition.x, nextPosition.y,
                penSize
            )
            renderer.circle(nextPosition.x, nextPosition.y, penSize / 2)
        }
        nextPosition.x -= camera.position.x
        nextPosition.y -= camera.position.y
    }
}