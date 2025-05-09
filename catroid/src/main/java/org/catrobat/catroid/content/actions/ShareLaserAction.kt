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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.plot.SVGPlotGenerator
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageActivity.IntentListener
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.plot.PlotColor
import org.catrobat.catroid.utils.Utils
import java.io.File
import java.io.IOException

class ShareLaserAction : SharePlotAction() {
    override fun writePlotDataToFile(destinationFile: File) {
        if (scope == null) {
            return
        }
        val plot = scope!!.sprite.plot
        val svgFileGenerator = SVGPlotGenerator(plot)
        svgFileGenerator.action = PlotColor.BLUE
        val engravePath = svgFileGenerator.pathFromData(plot.engraveDataPointLists)
        svgFileGenerator.action = PlotColor.RED
        val cutPath = svgFileGenerator.pathFromData(plot.cutDataPointLists)
        svgFileGenerator.writeToSVGFile(destinationFile, engravePath + cutPath)
    }
}
