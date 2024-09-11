/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableViewHolder

class BroadcastListAdapter(
    private val broadcastMessages: List<String>,
    private var currentBroadcastMessage: String
) : RVAdapter<String>(broadcastMessages), RVAdapter.SelectionListener {

    override fun onSelectionChanged(selectedItemCnt: Int) = Unit

    fun setCurrentBroadcastMessage(newMessage: String) {
        currentBroadcastMessage = newMessage
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return R.layout.view_holder_broadcast_message_with_headline
        }
        return R.layout.view_holder_broadcast_message_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return CheckableViewHolder(view)
    }

    override fun onBindViewHolder(holder: CheckableViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val view = holder.itemView
        if (position == 0) {
            val headline = view.findViewById<View>(R.id.headline) as TextView
            headline.setText(R.string.broadcast_message_headline)
        }
        val currentMessage = broadcastMessages[position]
        highlightCurrentMessage(view, currentMessage)
        holder.title.text = currentMessage
    }

    private fun highlightCurrentMessage(view: View, currentMessage: String) {
        val layout = view.findViewById<View>(R.id.message_layout)
        if (currentMessage == currentBroadcastMessage) {
            layout.setBackgroundResource(R.drawable.button_background_pressed)
        } else {
            layout.setBackgroundResource(R.drawable.button_background_selector)
        }
    }

    override fun getItemCount(): Int = broadcastMessages.size
}
