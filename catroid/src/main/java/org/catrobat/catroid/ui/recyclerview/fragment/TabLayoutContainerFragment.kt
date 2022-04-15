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

package org.catrobat.catroid.ui.recyclerview.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.TabLayoutFragmentAdapter

class TabLayoutContainerFragment : Fragment() {
    private lateinit var adapter: TabLayoutFragmentAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_tab_layout_container, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = TabLayoutFragmentAdapter(this)
        addFragmentsToAdapter()

        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = adapter

        tabLayout = view.findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.setIcon(R.drawable.ic_program_menu_scripts)
                1 -> tab.setIcon(R.drawable.ic_program_menu_looks)
                else -> tab.setIcon(R.drawable.ic_program_menu_sounds)
            }
        }.attach()
    }

    fun getSelectedTabFragment(): Fragment? {
        if (childFragmentManager.fragments.size > viewPager.currentItem) {
            return childFragmentManager.fragments[viewPager.currentItem]
        }
        return null
    }

    private fun addFragmentsToAdapter() {
        adapter.addFragment(ScriptFragment())
        adapter.addFragment(LookListFragment())
        adapter.addFragment(SoundListFragment())
    }

    companion object {
        @JvmField
        val TAG = TabLayoutContainerFragment::class.java.simpleName
    }
}
