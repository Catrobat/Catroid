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
package org.catrobat.catroid.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearSnapHelper
import kotlinx.android.synthetic.main.project_category_view.view.categoryProjectsRecyclerView
import kotlinx.android.synthetic.main.project_category_view.view.categoryTitle
import org.catrobat.catroid.R
import org.catrobat.catroid.retrofit.models.ShareCategory
import org.catrobat.catroid.ui.recyclerview.FeaturedProjectCallback
import org.catrobat.catroid.ui.recyclerview.adapter.HorizontalShareProjectAdapter

class ShareCategoryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) :
    LinearLayout(context, attrs, defStyle, defStyleRes) {

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.project_category_view, this, true)
    }

    private val horizontalAdapter = HorizontalShareProjectAdapter()

    fun setShareCategory(shareCategory: ShareCategory) {
        categoryTitle.setText(shareCategory.nameResourceId)
        categoryProjectsRecyclerView.apply {
            setHasFixedSize(true)
            LinearSnapHelper().attachToRecyclerView(this)
            adapter = horizontalAdapter
        }
        horizontalAdapter.setItems(shareCategory.projects)
    }

    fun setCallback(featuredProjectCallback: FeaturedProjectCallback) {
        horizontalAdapter.setCallback(featuredProjectCallback)
    }
}