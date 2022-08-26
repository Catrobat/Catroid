/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.formulaeditor.function

import org.catrobat.catroid.camera.mlkitdetectors.ObjectDetectorResults
import org.catrobat.catroid.formulaeditor.Functions

class ObjectDetectorFunctionProvider : FunctionProvider {
    override fun addFunctionsToMap(formulaFunctions: MutableMap<Functions, FormulaFunction>) {
        formulaFunctions[Functions.ID_OF_DETECTED_OBJECT] = UnaryFunction(UnaryFunctionAction { argument ->
            ObjectDetectorResults.result.keys.toList().getOrNull(argument.toInt() - 1)?.toDouble() ?: 0.0
        })
        formulaFunctions[Functions.OBJECT_WITH_ID_VISIBLE] = UnaryFunction(UnaryFunctionAction { argument ->
            ObjectDetectorResults.result[argument.toInt()] != null
        })
    }
}
