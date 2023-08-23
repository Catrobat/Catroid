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

package androidx.preference

import android.content.Context
import android.content.res.TypedArray
import android.os.Parcelable
import android.util.AttributeSet
import org.catrobat.catroid.R
import org.catrobat.catroid.TrustedDomainManager

class TrustListEditorPreference(context: Context, attrs: AttributeSet) : EditTextPreference
    (context, attrs) {
    init {
        setDialogTitle(R.string.preference_screen_web_access_title)
        setPositiveButtonText(R.string.ok)
        setNegativeButtonText(R.string.cancel)
        onPreferenceChangeListener = Listener()
    }

    class Listener : OnPreferenceChangeListener {
        override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
            TrustedDomainManager.setUserTrustList(newValue.toString())
            return true
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        text = TrustedDomainManager.getUserTrustList()
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any =
        TrustedDomainManager.getUserTrustList()
}
