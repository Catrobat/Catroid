/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
package org.catrobat.catroid.content.actions

import android.util.Log
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.ads.AdsBanner
import org.catrobat.catroid.content.bricks.AdsBannerPositionEnum
import org.catrobat.catroid.content.bricks.AdsBannerSizeEnum
import org.catrobat.catroid.stage.StageActivity

class ShowAdsBannerAction : TemporalAction() {
    private lateinit var mSize: AdsBannerSizeEnum
    private lateinit var mPosition: AdsBannerPositionEnum

    fun setMobSize(size: AdsBannerSizeEnum) {
        mSize = size
    }

    fun setAdMobPosition(position: AdsBannerPositionEnum) {
        mPosition = position
    }

    override fun update(percent: Float) {
        showAdsBanner()
    }

    private fun showAdsBanner() {
        if (StageActivity.adsHandler == null) {
            return
        }
        when (mPosition) {
            AdsBannerPositionEnum.BOTTOM ->
                when (mSize) {
                    AdsBannerSizeEnum.SMART_BANNER -> {
                        StageActivity.adsHandler.obtainMessage(AdsBanner.ADS_SMART_BANNER_BOTTOM)
                            .sendToTarget()
                        Log.d(TAG, "ADS_SMART_BANNER_BOTTOM")
                    }
                    AdsBannerSizeEnum.LARGE_BANNER -> {
                        StageActivity.adsHandler.obtainMessage(AdsBanner.ADS_LARGE_BANNER_BOTTOM)
                            .sendToTarget()
                        Log.d(TAG, "ADS_LARGE_BANNER_BOTTOM")
                    }
                    AdsBannerSizeEnum.BANNER -> {
                        StageActivity.adsHandler.obtainMessage(AdsBanner.ADS_BANNER_BOTTOM)
                            .sendToTarget()
                        Log.d(TAG, "ADS_BANNER_BOTTOM")
                    }
                }
            AdsBannerPositionEnum.TOP ->
                when (mSize) {
                    AdsBannerSizeEnum.SMART_BANNER -> {
                        StageActivity.adsHandler.obtainMessage(AdsBanner.ADS_SMART_BANNER_TOP)
                            .sendToTarget()
                        Log.d(TAG, "ADS_SMART_BANNER_TOP")
                    }
                    AdsBannerSizeEnum.LARGE_BANNER -> {
                        StageActivity.adsHandler.obtainMessage(AdsBanner.ADS_LARGE_BANNER_TOP)
                            .sendToTarget()
                        Log.d(TAG, "ADS_LARGE_BANNER_TOP")
                    }
                    AdsBannerSizeEnum.BANNER -> {
                        StageActivity.adsHandler.obtainMessage(AdsBanner.ADS_BANNER_TOP)
                            .sendToTarget()
                        Log.d(TAG, "ADS_BANNER_TOP")
                    }
                }
        }
    }

    companion object {
        val TAG = ShowAdsBannerAction::class.java.simpleName
    }
}
