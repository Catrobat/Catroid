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

package org.catrobat.catroid.ui.recyclerview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.catrobat.catroid.R
import org.catrobat.catroid.retrofit.models.ProjectCategoryWithResponses
import org.catrobat.catroid.ui.recyclerview.CategoryTitleCallback
import org.catrobat.catroid.ui.recyclerview.FeaturedProjectCallback

class CategoriesAdapter : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryTitle: TextView = itemView.findViewById(R.id.categoryTitle)
        val categoryRecyclerView: RecyclerView = itemView.findViewById(R.id.categoryRecyclerView)
    }

    private var data = mutableListOf<ProjectCategoryWithResponses>()
    private lateinit var projectCallback: FeaturedProjectCallback
    private lateinit var categoryTitleCallback: CategoryTitleCallback

    fun setOnProjectClickCallback(featuredProjectCallback: FeaturedProjectCallback) {
        projectCallback = featuredProjectCallback
    }

    fun setOnCategoryTitleClickCallback(callback: CategoryTitleCallback) {
        categoryTitleCallback = callback
    }

    fun setItems(items: List<ProjectCategoryWithResponses>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.project_category_item_view,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.categoryTitle.apply {
            text = item.category.name
            setOnClickListener {
                categoryTitleCallback.onCategoryTitleClicked(item.category.type)
            }
        }
        holder.categoryRecyclerView.apply {
            setHasFixedSize(true)
            HorizontalProjectResponseAdapter().apply {
                setItems(item.projectsList)
                setCallback(projectCallback)
            }.let {
                this.adapter = it
            }
        }
    }

    override fun getItemCount() = data.size
}
