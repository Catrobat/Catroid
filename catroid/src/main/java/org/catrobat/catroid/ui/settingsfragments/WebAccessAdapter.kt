/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

package org.catrobat.catroid.ui.settingsfragments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableViewHolder

class WebAccessAdapter(
    private val trustedDomains: MutableList<String>
) : RVAdapter<String>(trustedDomains) {

    private var isCheckboxVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return CheckableViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int = R.layout.view_holder_web_access_item

    override fun onBindViewHolder(holder: CheckableViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.title.text = trustedDomains[position]
        holder.checkBox.isVisible = isCheckboxVisible
    }

    override fun getItemCount(): Int = trustedDomains.size

    fun addTrustedDomain(domain: String) {
        add(domain)
    }

    override fun setOnItemClickListener(onItemClickListener: OnItemClickListener<String>?) {
        super.setOnItemClickListener(onItemClickListener)
    }

    fun removeTrustedDomain(domain: String) {
        remove(domain)
    }

    fun removeTrustedDomains(domains: List<String>) {
        for (domain in domains) {
            removeTrustedDomain(domain)
        }
    }

    fun renameTrustedDomain(oldDomain: String, newDomain: String) {
        update(oldDomain, newDomain)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCheckboxVisibility(visible: Boolean) {
        isCheckboxVisible = visible
        notifyDataSetChanged()
    }

    override fun setSelectionListener(selectionListener: SelectionListener?) {
        super.setSelectionListener(selectionListener)
    }
}
