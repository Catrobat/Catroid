/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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
package org.catrobat.catroid.content.actions

import org.catrobat.catroid.R
import org.catrobat.catroid.plot.PlotColor
import org.catrobat.catroid.plot.SVGPlotGenerator
import org.catrobat.catroid.stage.StageActivity
import java.io.File

class SaveLaserAction : SavePlotAction() {

    override fun checkIfDataIsReady(): Boolean {
        val scope = this.scope ?: return false
        val plot = scope.sprite.plot ?: return false
        return SVGPlotGenerator(plot).hasLaserData()
    }

    override fun showDataMissingMessage() {
        val message = context.getString(R.string.error_laser_data_not_found)
        val params = ArrayList<Any>(listOf(message))
        StageActivity.messageHandler.obtainMessage(StageActivity.SHOW_TOAST, params).sendToTarget()
    }

    override fun handleFileWork(file: File): Boolean {
        val scope = this.scope ?: return false
        val plot = scope.sprite.plot ?: return false
        val svgFileGenerator = SVGPlotGenerator(plot)
        svgFileGenerator.action = PlotColor.BLUE
        val engravePath = svgFileGenerator.pathFromData(plot.engraveDataPointLists)
        svgFileGenerator.action = PlotColor.RED
        val cutPath = svgFileGenerator.pathFromData(plot.cutDataPointLists)
        val finalLaserData = engravePath + cutPath
        svgFileGenerator.writeToSVGFile(file, finalLaserData)
        return true
    }
}
