/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.catroid.ads

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import org.catrobat.catroid.content.bricks.AdsBannerSizeEnum

class AdsBanner constructor(private val context: Context) {

    var adView = FakeAdView(context)

    fun createNew(adSize: AdsBannerSizeEnum, relativeLayoutRule: Int) {
        adView.setAdSize(adSize)

        RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(relativeLayoutRule)
            addRule(RelativeLayout.CENTER_IN_PARENT)
        }.let { param ->
            adView.layoutParams = param
        }
    }

    fun show() {
        adView.visibility = View.VISIBLE
    }

    @SuppressWarnings("OptionalUnit")
    fun pause() = Unit

    @SuppressWarnings("OptionalUnit")
    fun resume() = Unit

    fun hide() {
        adView.visibility = View.GONE
    }

    @SuppressWarnings("OptionalUnit")
    fun initializeMobileAds() = Unit

    companion object {
        const val ADS_HIDE_BANNER = 404
        const val ADS_BANNER_BOTTOM = 20
        const val ADS_SMART_BANNER_BOTTOM = 21
        const val ADS_LARGE_BANNER_BOTTOM = 22
        const val ADS_BANNER_TOP = 30
        const val ADS_SMART_BANNER_TOP = 31
        const val ADS_LARGE_BANNER_TOP = 32
    }
}
