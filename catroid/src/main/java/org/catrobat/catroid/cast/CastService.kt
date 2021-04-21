/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.cast

import android.content.Context
import android.os.Bundle
import android.view.Display
import android.widget.RelativeLayout
import com.google.android.gms.cast.CastPresentation
import com.google.android.gms.cast.CastRemoteDisplayLocalService
import org.catrobat.catroid.R
import org.catrobat.catroid.cast.CastManager.Companion.instance
import org.catrobat.catroid.utils.ToastUtil

class CastService : CastRemoteDisplayLocalService() {
    private var presentation: CastPresentation? = null
    override fun onCreatePresentation(display: Display) {
        createPresentation(display)
    }

    override fun onDismissPresentation() {
        dismissPresentation()
    }

    private fun dismissPresentation() {
        if (presentation != null) {
            presentation!!.dismiss()
            presentation = null
        }
    }

    private fun createPresentation(display: Display?) {

        dismissPresentation()
        presentation = FirstScreenPresentation(this, display)
        try {
            (presentation as FirstScreenPresentation).show()
        } catch (ex: Exception) {
            ToastUtil.showError(
                applicationContext,
                getString(R.string.cast_error_not_connected_msg)
            )
            dismissPresentation()
        }
    }

    inner class FirstScreenPresentation(serviceContext: Context?, display: Display?) :
        CastPresentation(serviceContext, display) {
        override fun onCreate(savedInstanceState: Bundle) {
            super.onCreate(savedInstanceState)
            val layout = RelativeLayout(application)
            setContentView(layout)
            instance.setIsConnected(true)
            instance.setRemoteLayout(layout)
            instance.setRemoteLayoutToIdleScreen(applicationContext)
        }
    }
}