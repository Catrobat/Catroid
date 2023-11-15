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

package org.catrobat.catroid.ui

import android.annotation.SuppressLint
import androidx.annotation.VisibleForTesting
import org.catrobat.catroid.formulaeditor.FormulaEditorEditText
import org.catrobat.catroid.formulaeditor.InternToken

import java.util.ArrayList

@SuppressLint("InflateParams")
class FormulaEditorClipboard(private val formulaEditorEditText: FormulaEditorEditText) {

    @VisibleForTesting
    var clipboard: List<InternToken>? = null

    private fun cloneTokens(tokens: List<InternToken>): List<InternToken> {
        val clonedTokens = ArrayList<InternToken>()
        tokens.forEach { token -> clonedTokens.add(token.deepCopy()) }
        return clonedTokens
    }

    private fun copyTokens(tokens: List<InternToken>?) {
        clipboard = if (tokens != null) {
            cloneTokens(tokens)
        } else {
            null
        }
    }

    fun copy() {
        copyTokens(formulaEditorEditText.selectedTokens)
    }

    fun paste() {
        clipboard?.let { formulaEditorEditText.addTokens(cloneTokens(it)); }
    }
}
