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
import org.catrobat.catroid.ui.recyclerview.adapter.OnBoardingWelcomeFragmentAdapter

class OnBoardingWelcomeFragmentContainer : Fragment() {
    private lateinit var adapter: OnBoardingWelcomeFragmentAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.onboarding_welcome_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = OnBoardingWelcomeFragmentAdapter(this)
        addFragmentsToAdapter()

        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = adapter

        tabLayout = view.findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
    }

    fun getSelectedTabFragment(): OnBoardingWelcomeFragment? {
        if (childFragmentManager.fragments.size > viewPager.currentItem) {
            return childFragmentManager.fragments[viewPager.currentItem] as
                OnBoardingWelcomeFragment
        }
        return null
    }

    private fun addFragmentsToAdapter() {
        for (i in 0 until TAB_COUNT) {
            adapter.addFragment(OnBoardingWelcomeFragment(i))
        }
    }

    companion object {
        val TAG = OnBoardingWelcomeFragmentContainer::class.java.simpleName
        val TAB_COUNT = 4
    }
}
