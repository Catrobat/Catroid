/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.utils

import android.util.Log

object FrameSizeProvider {

    private val allowedUnits = listOf<String>("cm", "inch")

    private val listOfAllowedSizeInCm = listOf<String>("default",
                                                        "10 x 10 ${allowedUnits.get(0)}",
                                                       "18 x 13 ${allowedUnits.get(0)}",
                                                       "26 x 16 ${allowedUnits.get(0)}",
                                                       "30 x 18 ${allowedUnits.get(0)}",
                                                       "20 x 20 ${allowedUnits.get(0)}",
                                                       "30 x 20 ${allowedUnits.get(0)}",
                                                       "30 x 23 ${allowedUnits.get(0)}",
                                                       "36 x 24 ${allowedUnits.get(0)}")

    private val listOfAllowedSizeInInch = listOf<String>("default",
                                                         "4 x 4 ${allowedUnits.get(1)}",
                                                         "5 x 7 ${allowedUnits.get(1)}",
                                                         "6 x 10 ${allowedUnits.get(1)}",
                                                         "7 x 12 ${allowedUnits.get(1)}",
                                                         "8 x 8 ${allowedUnits.get(1)}",
                                                         "8 x 12 ${allowedUnits.get(1)}",
                                                         "9 x 12 ${allowedUnits.get(1)}",
                                                         "9.5 x 14 ${allowedUnits.get(1)}")


    private var selectedUnit = allowedUnits.get(0)
    private var selectedHeight = 0.0
    private var selectedWidth = 0.0

    fun getFrameSize(isPortrait:Boolean, isCm:Boolean):List<String> {

//        var lst_sizes = listOf<String>()

        if (isCm){
            selectedUnit = allowedUnits.get(0)
            return listOfAllowedSizeInCm
        }


        selectedUnit = allowedUnits.get(1)
        return listOfAllowedSizeInInch
    }

    fun selectedUnit() : String{
        return selectedUnit
    }

    private val TAG = FrameSizeProvider::class.java.simpleName

    fun selectedHeightInCm(selectedString: String):Double{
        Log.e(TAG, "selectedHeight: str => ${selectedString}")


        if (selectedString.equals("default", true))return 0.0


        try {
            selectedHeight = selectedString.replace(selectedUnit(),"").split(" x ")[1].trim()
                .toDouble()

            if (selectedUnit().equals(allowedUnits.get(1), true)){
                selectedHeight = selectedHeight * 2.54
            }
            Log.e(TAG, "selectedHeight: ${selectedHeight}", )
            return selectedHeight
        }
        catch (e : Exception){
            Log.e(TAG, "selectedHeight: err -> ${e.message}")
            return 0.0
        }

    }

    fun selectedWidthInCm(selectedString: String):Double{
        Log.e(TAG, "selectedWidth: str => ${selectedString}")

        if (selectedString.equals("default", true))return 0.0

        try {
            selectedWidth = selectedString.replace(selectedUnit(),"").split(" x ")[0].trim()
                .toDouble()

            if (selectedUnit().equals(allowedUnits.get(1), true)){
                selectedWidth = selectedWidth * 2.54
            }
            Log.e(TAG, "selectedWidth: ${selectedWidth}", )
            return selectedWidth
        }
        catch (e : Exception){
            Log.e(TAG, "selectedWidth: err -> ${e.message}")
            return 0.0
        }
    }

}