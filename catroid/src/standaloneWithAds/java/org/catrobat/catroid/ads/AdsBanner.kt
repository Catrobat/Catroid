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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.content.bricks.AdsBannerSizeEnum

class AdsBanner constructor(private val context: Context) {

    var adView = AdView(context)

    fun createNew(adSize: AdsBannerSizeEnum, relativeLayoutRule: Int) {
        adView = AdView(context)
        adView.let {
            it.adSize = when (adSize) {
                AdsBannerSizeEnum.SMART_BANNER -> AdSize.SMART_BANNER
                AdsBannerSizeEnum.LARGE_BANNER -> AdSize.LARGE_BANNER
                else -> AdSize.BANNER
            }
            it.adUnitId = BuildConfig.ADS_BANNER_UNIT_ID
            val configuration = RequestConfiguration
                .Builder()
                .apply {
                    setTagForChildDirectedTreatment(TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                }.build()

            MobileAds.setRequestConfiguration(configuration)

            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(relativeLayoutRule)
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }.let { param ->
                adView.layoutParams = param
            }

            it.loadAd(AdRequest.Builder().build())
        }
    }

    fun show() {
        adView.visibility = View.VISIBLE
    }

    fun pause() {
        if (adView.isActivated) {
            adView.pause()
        }
    }

    fun resume() {
        if (adView.isActivated || adView.isLoading) {
            adView.resume()
        }
    }

    fun hide() {
        if (adView.isActivated || adView.isLoading) {
            adView.apply {
                dispatchSetActivated(false)
                destroy()
            }
        }
        adView.visibility = View.GONE
    }

    fun initializeMobileAds() {
        MobileAds.initialize(context)
    }

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
