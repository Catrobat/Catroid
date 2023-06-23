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

import androidx.recyclerview.widget.RecyclerView
import androidx.annotation.IntDef
import org.catrobat.catroid.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import org.catrobat.catroid.ui.recyclerview.viewholder.ViewHolder
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

class CategoryListRVAdapter(private val items: List<CategoryListItem>) :
    RecyclerView.Adapter<ViewHolder>() {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(DEFAULT, COLLISION, NXT, EV3)
    annotation class CategoryListItemType
    class CategoryListItem(nameResId: Int, text: String, @CategoryListItemType type: Int) {
        @JvmField
        var header: String? = null
        @JvmField
        var nameResId: Int
        var text: String? = null

        @JvmField
        @CategoryListItemType
        var type: Int

        init {
            if (nameResId == R.string.formula_editor_function_regex_assistant) {
                this.text = "\t\t\t\t\t" + text
            } else {
                this.text = text
            }
            this.nameResId = nameResId
            this.type = type
        }
    }

    private var onItemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        if (holder.itemViewType == R.layout.view_holder_category_list_item_with_headline) {
            val headlineView = holder.itemView.findViewById<TextView>(R.id.headline)
            headlineView.text = items[position].header
        }
        holder.title.text = item.text
        holder.itemView.setOnClickListener { v: View? -> onItemClickListener!!.onItemClick(item) }
    }

    @LayoutRes
    override fun getItemViewType(position: Int): Int {
        return if (items[position].header != null) R.layout.view_holder_category_list_item_with_headline else R.layout.view_holder_category_list_item
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        onItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(item: CategoryListItem?)
    }

    companion object {
        const val DEFAULT = 0
        const val COLLISION = 1
        const val NXT = 2
        const val EV3 = 3
    }
}
