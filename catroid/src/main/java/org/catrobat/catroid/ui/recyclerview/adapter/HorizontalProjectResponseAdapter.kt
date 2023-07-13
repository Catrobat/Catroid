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
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import org.catrobat.catroid.R
import org.catrobat.catroid.retrofit.models.ProjectResponse
import org.catrobat.catroid.ui.recyclerview.FeaturedProjectCallback
import org.catrobat.catroid.utils.loadImageFromUrl

class HorizontalProjectResponseAdapter :
    RecyclerView.Adapter<HorizontalProjectResponseAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.project_image_view)
    }

    private var data = mutableListOf<ProjectResponse>()
    private lateinit var callback: FeaturedProjectCallback

    fun setCallback(featuredProjectCallback: FeaturedProjectCallback) {
        callback = featuredProjectCallback
    }

    fun setItems(items: List<ProjectResponse>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.project_picture_listitem,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.imageView.loadImageFromUrl(item.screenshotSmall)
        holder.itemView.setOnClickListener {
            callback.onFeatureProjectClicked(item.projectUrl)
        }
    }

    override fun getItemCount() = data.size
}
