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
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView.Adapter
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.recyclerview.fragment.YourFunctionsListFragment
import org.catrobat.catroid.ui.recyclerview.viewholder.ViewHolder

class YourFunctionsListRecyclerViewAdapter(
    var items: List<YourFunctionListItem?>,
    var onItemClickListener: YourFunctionsListFragment
) : Adapter<ViewHolder>() {
    data class YourFunctionListItem(
        val text: String,
        val functionName: String,
        val functionParameters: List<String>
    )

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: YourFunctionListItem? = items.get(index = position)

        holder.title.text = item?.text
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(
                item
            )
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: YourFunctionListItem?)
    }

    @LayoutRes
    override fun getItemViewType(position: Int): Int = R.layout.view_holder_category_list_item

    override fun getItemCount(): Int = items.size
}
