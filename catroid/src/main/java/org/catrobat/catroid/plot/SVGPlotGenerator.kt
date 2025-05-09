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
import java.io.File
import java.util.Locale

enum class PlotColor(val rgb: Int) {
    RED(0xFF0000),
    BLACK(0x000000),
    BLUE(0x0000FF)
}

class SVGPlotGenerator(plot: Plot) {
    private var width: Float = plot.width
    private var height: Float = plot.height
    private var lineWidth: Double = 1.0
    var action: PlotColor = PlotColor.BLACK
    private val scaleMMtoPx: Float = 0.2f / 0.2646f

    private fun dotDecinalRound(number: Float): String {
        return "%.2f".format(Locale.ENGLISH, number * scaleMMtoPx)
    }

    fun writeToSVGFile(targetFile: File, path: String) {
        targetFile.writeText(generateSVGContent(path))
    }

    private fun toHexSting(rgb: Int): String {
        return String.format("#%06X", 0xFFFFFF and rgb)
    }

    private fun stroke(): String {
        return toHexSting(action.rgb)
    }

    private fun generateSVGPath(line: List<PointF>, xAlignment: Float, yAlignment: Float):
        String {
        var path = ""
        if (line.size < 2) return path
        path =
            "<path fill=\"none\" style=\"stroke:" + stroke() + ";" +
                "stroke-width:" + lineWidth.toString() + ";" +
                "stroke-linecap:round;stroke-opacity:1;\" d=\"M"
        path += dotDecinalRound(line[0].x - xAlignment) + " " + dotDecinalRound(line[0].y - yAlignment)

        for (point in line.subList(1, line.size))
            path = path + " L" + dotDecinalRound(point.x - xAlignment) + " " + dotDecinalRound(
                point
                    .y
                    - yAlignment
            )

        path += "\" />\n"
        return path
    }

    private fun generateSVGHeader(): String {
        val builder = StringBuilder()
        builder.append("<?xml version=\"1.0\" standalone=\"no\"?>\n<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n")
        builder.append("<svg width=\"")
        builder.append(dotDecinalRound(width))
        builder.append("\" height=\"")
        builder.append(dotDecinalRound(height))
        builder.append("\" ")
        builder.append("viewBox=\"")
        builder.append(dotDecinalRound(0.0F))
        builder.append(" ")
        builder.append(dotDecinalRound(0.0F))
        builder.append(" ")
        builder.append(dotDecinalRound(width))
        builder.append(" ")
        builder.append(dotDecinalRound(height))
        builder.append("\" ")
        builder.append("style=\"background-color:#ffffff\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n")

        builder.append("<title>Plotter export</title>\n")
        return builder.toString()
    }

    fun pathFromData(data: ArrayList<ArrayList<PointF>>): String {
        val xAlignment = width.div(-2.0F)
        val yAlignment = height.div(-2.0F)
        val builder = StringBuilder()
        for (line in data) {
            builder.append(generateSVGPath(line, xAlignment, yAlignment))
        }
        return builder.toString()
    }

    fun generateSVGContent(path: String): String {
        val builder = StringBuilder()
        builder.append(generateSVGHeader())
        builder.append(path)
        builder.append("\n</svg>")
        return builder.toString()
    }
}