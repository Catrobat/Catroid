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
package org.catrobat.catroid.utils

import org.catrobat.catroid.scratchconverter.Client.ProjectDownloadCallback
import org.catrobat.catroid.ui.recyclerview.fragment.MainMenuFragment
import java.lang.ref.WeakReference

object ProjectDownloadUtil : ProjectDownloadCallback {
    private var fragment: WeakReference<MainMenuFragment>? = null
    fun setFragment(fragment: MainMenuFragment) {
        this.fragment = WeakReference<MainMenuFragment>(fragment)
    }

    override fun onDownloadFinished(catrobatProgramName: String, url: String) {
        fragment?.get()?.refreshData()
    }

    override fun onDownloadStarted(string: String) = Unit

    override fun onDownloadProgress(progress: Int, url: String) = Unit

    override fun onUserCanceledDownload(url: String) = Unit
}
