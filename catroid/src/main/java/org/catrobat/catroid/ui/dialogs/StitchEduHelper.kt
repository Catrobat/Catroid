/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
package org.catrobat.catroid.ui.dialogs

import android.content.ActivityNotFoundException
import android.content.Context
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.web.WebpageUtils

/**
 * Helper object managing StitchEDU integration within Catroid.
 *
 * StitchEDU is an external embroidery designer tool created by Bernadette Spieler's team.
 * This helper provides flavor-availability checks and safe browser navigation.
 */
object StitchEduHelper {

    /**
     * Determines whether the StitchEDU feature is supported in the current application flavor.
     * Currently exclusively available in the Embroidery Designer flavor.
     */
    val isStitchEduSupported: Boolean
        get() = FlavoredConstants.FLAVOR_NAME == "embroidery"

    /**
     * Opens the StitchEDU web application in an external web browser.
     *
     * @param context the Android [Context] used to launch the browser intent and show notifications.
     * @return `true` if the web page was opened successfully; `false` if no suitable browser was found.
     */
    fun openStitchEdu(context: Context): Boolean {
        return try {
            WebpageUtils.openWebPage(context, Constants.STITCH_EDU_URL)
            true
        } catch (_: ActivityNotFoundException) {
            ToastUtil.showError(context, R.string.error_no_web_browser_found)
            false
        }
    }
}
