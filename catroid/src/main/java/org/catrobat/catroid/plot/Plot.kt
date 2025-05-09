/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025  The Catrobat Team
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

class Plot {
    private var isPlotting = false
    private var isEngraving = false
    private var isCutting = false
    var plotDataPointLists = ArrayList<ArrayList<PointF>>()
    private var plotQueue = Queue<Queue<PointF>>()

    var engraveDataPointLists = ArrayList<ArrayList<PointF>>()
    private var engraveQueue = Queue<Queue<PointF>>()

    var cutDataPointLists = ArrayList<ArrayList<PointF>>()
    private var cutQueue = Queue<Queue<PointF>>()

    var width = 0.0F
    var height = 0.0F

    fun pausePlot() {
        isPlotting = false;
    }

    fun resumePlot() {
        isPlotting = true;
    }

    fun pauseCut() {
        isCutting = false;
    }
    fun resumeCut() {
        isCutting = true;
    }
    fun pauseEngrave() {
        isEngraving = false;
    }
    fun resumeEngrave() {
        isEngraving = true;
    }

    fun isPlotting(): Boolean {
        return isPlotting
    }

    fun isCutting(): Boolean {
        return isCutting
    }

    fun isEngraving(): Boolean {
        return isEngraving
    }

    private fun startLine(data: ArrayList<ArrayList<PointF>>, queue: Queue<Queue<PointF>>) {
        data.add(ArrayList())
        queue.addLast(Queue())
    }

    private fun startNewLine(
        point: PointF,
        data: ArrayList<ArrayList<PointF>>,
        queue: Queue<Queue<PointF>>
    ) {
        data.add(arrayListOf(point))
        queue.addLast(Queue())
        queue.last().addLast(point)
    }

    private fun addPoint(
        point: PointF,
        data: ArrayList<ArrayList<PointF>>,
        queue: Queue<Queue<PointF>>
    ) {
        data.last().add(point)
        queue.last().addLast(point)
    }

    fun startNewPlotLine() {
        startLine(plotDataPointLists, plotQueue)
    }

    fun startNewPlotLine(point: PointF) {
        startNewLine(point, plotDataPointLists, plotQueue)
    }

    fun addPlotPoint(point: PointF) {
        addPoint(point, plotDataPointLists, plotQueue)
    }

    fun startNewCutLine() {
        startLine(cutDataPointLists, cutQueue)
    }

    fun startNewCutLine(point: PointF) {
        startNewLine(point, cutDataPointLists, cutQueue)
    }

    fun addCutPoint(point: PointF) {
        addPoint(point, cutDataPointLists, cutQueue)
    }

    fun startNewEngraveLine() {
        startLine(engraveDataPointLists, engraveQueue)
    }

    fun startNewEngraveLine(point: PointF) {
        startNewLine(point, engraveDataPointLists, engraveQueue)
    }

    fun addEngravePoint(point: PointF) {
        addPoint(point, engraveDataPointLists, engraveQueue)
    }

    fun data(): ArrayList<ArrayList<PointF>> {
        return plotDataPointLists
    }

    private fun canDraw(): Boolean {
        return plotQueue.size > 2 || (!plotQueue.isEmpty && plotQueue.last().size > 2)
    }

    private fun canCut(): Boolean {
        return cutQueue.size > 2 || (!cutQueue.isEmpty && cutQueue.last().size > 2)
    }

    private fun canEngrave(): Boolean {
        return engraveQueue.size > 2 || (!engraveQueue.isEmpty && engraveQueue.last().size > 2)
    }

    private fun updateQueue(queue: Queue<Queue<PointF>>) {
        if (queue.isEmpty || queue.size == 1) return
        if (queue.first().size == 1)
            queue.removeFirst()
    }

    fun drawLinesForSprite(screenRatio: Float, camera: Camera?) {
        if (camera == null)
            return

        val renderer = StageActivity.stageListener.shapeRenderer
        renderer.color = Color.BLACK
        renderer.begin(ShapeRenderer.ShapeType.Filled)

        while (canDraw()) {
            drawLine(plotQueue, screenRatio, renderer, camera)
            updateQueue(plotQueue)
        }
        renderer.end()

        renderer.color = Color.RED
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        while (canCut()) {
            drawLine(cutQueue, screenRatio, renderer, camera)
            updateQueue(plotQueue)
        }
        renderer.end()


        renderer.color = Color.BLUE
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        while (canEngrave()) {
            drawLine(engraveQueue, screenRatio, renderer, camera)
            updateQueue(engraveQueue)
        }
        renderer.end()

        width = camera.viewportWidth
        height = camera.viewportHeight
    }

    private fun drawLine(
        queue: Queue<Queue<PointF>>, screenRatio: Float, renderer:
        ShapeRenderer, camera: Camera
    ) {
        val currentPosition: PointF = queue.first().removeFirst()
        val nextPosition: PointF = queue.first().first()
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