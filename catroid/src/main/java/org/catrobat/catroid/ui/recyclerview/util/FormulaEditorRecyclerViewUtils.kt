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

package org.catrobat.catroid.ui.recyclerview.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.YourFunctionsListRecyclerViewAdapter
import java.util.Locale

object FormulaEditorRecyclerViewUtils {
    @JvmStatic
    fun getLanguage(activity: Activity): String {
        val sharedPreferences =
            getSharedPreferences(activity.applicationContext)
        val languageTag =
            sharedPreferences?.getString(SharedPreferenceKeys.LANGUAGE_TAG_KEY, "")
        val mLocale: Locale = if (languageTag == SharedPreferenceKeys.DEVICE_LANGUAGE) {
            Locale.forLanguageTag(CatroidApplication.defaultSystemLanguage)
        } else {
            if (listOf(SharedPreferenceKeys.LANGUAGE_TAGS)
                    .contains(languageTag)
            ) Locale.forLanguageTag(languageTag) else Locale.forLanguageTag(CatroidApplication.defaultSystemLanguage)
        }
        return "?language=" + mLocale.language
    }

    @JvmStatic
    fun getSharedPreferences(context: Context): SharedPreferences? =
        PreferenceManager.getDefaultSharedPreferences(context)

    @JvmStatic
    fun addResourceToActiveFormulaInFormulaEditor(
        fragmentManager: androidx.fragment.app.FragmentManager?,
        item: Any?
    ): FormulaEditorFragment? {
        var formulaEditorFragment: FormulaEditorFragment? = null
        if (fragmentManager != null) {
            formulaEditorFragment = fragmentManager
                .findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG) as FormulaEditorFragment?
            if (formulaEditorFragment != null) {
                if (item is YourFunctionsListRecyclerViewAdapter.YourFunctionListItem) {
                    formulaEditorFragment.addUserDefinedResourceToActiveFormula(
                        item.functionName,
                        item.functionParameters
                    )
                    return formulaEditorFragment
                } else if (item is CategoryListRVAdapter.CategoryListItem) {
                    formulaEditorFragment.addResourceToActiveFormula(item.nameResId)
                }
            }
        }
        return formulaEditorFragment
    }
}
