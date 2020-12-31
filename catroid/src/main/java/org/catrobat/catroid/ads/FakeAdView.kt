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
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.AdsBannerSizeEnum

class FakeAdView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val linearLayout: LinearLayout

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.fake_ads_view, this, true)
        linearLayout = view.findViewById(R.id.container)
    }

    companion object {
        const val LARGE_BANNER_HEIGHT = 100f
        const val STANDARD_BANNER_HEIGHT = 50f
        const val SMART_BANNER_MARGIN = 16f
    }

    fun setAdSize(adSize: AdsBannerSizeEnum) {
        val height = when (adSize) {
            AdsBannerSizeEnum.LARGE_BANNER -> TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, LARGE_BANNER_HEIGHT, resources
                    .displayMetrics
            ).toInt()
            AdsBannerSizeEnum.BANNER -> TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, STANDARD_BANNER_HEIGHT, resources
                    .displayMetrics
            ).toInt()
            else -> LayoutParams.WRAP_CONTENT
        }

        val params = LayoutParams(LayoutParams.MATCH_PARENT, height)
        val margin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, SMART_BANNER_MARGIN, resources
                .displayMetrics
        ).toInt()

        if (adSize == AdsBannerSizeEnum.SMART_BANNER) {
            params.setMargins(margin, 0, margin, margin.div(2))
        }
        linearLayout.layoutParams = params
    }
}
