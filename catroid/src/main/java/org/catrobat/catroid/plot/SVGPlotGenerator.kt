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
import java.io.File

class SVGPlotGenerator(plot : Plot?){
    private val data = plot?.data()
    private var width = plot?.width
    private var height = plot?.height

    fun writeToSVGFile(targetFile : File){
        targetFile.writeText(generateSVGContent())
    }
    private fun generateSVGPath(line : List<PointF>, xAlignment : Float?, yAlignment : Float?) :
        String {
        var path = ""
        if(line.size < 2) return path
        path = "<path fill=\"none\" style=\"stroke:rgb(0,0,0);stroke-width:1;stroke-linecap:round;stroke-opacity:1;\" d=\"M"
        path += "%.2f".format(line[0].x - xAlignment!!) + " " + "%.2f".format(line[0].y - yAlignment!!)

        for (point in line.subList(1, line.size))
            path = path + " L" + "%.2f".format(point.x - xAlignment) + " " + "%.2f".format(point
                                                                                               .y
                                                                                               - yAlignment)

        path += "\" />"
        return path
    }

    private fun generateSVGContent() : String {
        val xAlignment = width?.div(-2.0F)
        val yAlignment = height?.div(-2.0F)
        val builder = StringBuilder()
        builder.append("<?xml version=\"1.0\" standalone=\"no\"?>\n<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n")
        builder.append("<svg width=\"")
        builder.append("%.2f".format(width))
        builder.append("\" height=\"")
        builder.append("%.2f".format(height))
        builder.append("\" ")
        builder.append("viewBox=\"")
        builder.append("%.2f".format(0.0F))
        builder.append(" ")
        builder.append("%.2f".format(0.0F))
        builder.append(" ")
        builder.append("%.2f".format(width))
        builder.append(" ")
        builder.append("%.2f".format(height))
        builder.append("\" ")
        builder.append("style=\"background-color:#ffffff\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n")

        builder.append("<title>Plotter export</title>\n")

        if (data != null) {
            for (line in data) {
                builder.append(generateSVGPath(line, xAlignment, yAlignment))
            }
        }
        builder.append("\n</svg>")
        return builder.toString()
    }
}