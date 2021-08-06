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

package org.catrobat.catroid.ui.settingsfragments.webaccess

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.catrobat.catroid.databinding.ViewHolderWebAccessBinding
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager
import org.catrobat.catroid.utils.setVisibleOrGone

class WebAccessAdapter : RecyclerView.Adapter<WebAccessAdapter.WebAccessViewHolder>() {

    inner class WebAccessViewHolder(val binding: ViewHolderWebAccessBinding) :
        RecyclerView.ViewHolder(binding.root)

    var selectionManager = MultiSelectionManager()
    private val items = mutableListOf<TrustedDomain>()
    private var showCheckBoxes = false

    fun setItems(newItems: List<TrustedDomain>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebAccessViewHolder {
        return WebAccessViewHolder(
            ViewHolderWebAccessBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WebAccessViewHolder, position: Int) {
        with(items[position]) {
            holder.binding.title.text = this.name
            holder.binding.checkbox.apply {
                setVisibleOrGone(showCheckBoxes)
                setOnClickListener {
                    selectionManager.toggleSelection(position)
                }
                isChecked = selectionManager.isPositionSelected(position)
            }

            holder.binding.root.setOnLongClickListener {
                Toast.makeText(holder.itemView.context, this.name, Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    override fun getItemCount() = items.count()

    fun showCheckBoxes(show: Boolean) {
        showCheckBoxes = show
        notifyDataSetChanged()
    }

    fun clearSelection() {
        selectionManager.clearSelection()
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<TrustedDomain> {
        val selectedItems = mutableListOf<TrustedDomain>()
        selectionManager.selectedPositions.forEach {
            selectedItems.add(items[it])
        }
        return selectedItems
    }
}
