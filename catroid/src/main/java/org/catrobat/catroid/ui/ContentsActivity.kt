/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.ui

import android.os.Bundle
import androidx.fragment.app.ListFragment
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.fragment.AddBrickFragment
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment.OnCategorySelectedListener
import org.catrobat.catroid.ui.fragment.UserDefinedBrickListFragment

class ContentsActivity : BaseActivity(), OnCategorySelectedListener {
    companion object {
        const val FRAGMENT_TAG_EXTRA = "fragmentTagExtra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        BottomBar.hideBottomBar(this)
         // TODO BrickCategoryFragment, DataListFragment, ListSelectorFragment should be handled
        //  here and SpriteActivity should be removed

        intent.getStringExtra(FRAGMENT_TAG_EXTRA)?.let {
            when (it) {
                BRICK_CATEGORY_FRAGMENT_TAG -> BrickCategoryFragment().apply {
                    setOnCategorySelectedListener(this@ContentsActivity)
                }
                else -> throw Exception("no fragment handled for this tag $it")
            }.apply {
                supportFragmentManager.beginTransaction()
                    .add(
                        R.id.fragment_container,
                        this,
                        it
                    )
                    .addToBackStack(it)
                    .commit()
            }
        }
    }

    override fun onCategorySelected(category: String?) {
        val addListFragment: ListFragment
        val tag: String

        //TODO change signature of called newInstances
        if (category == getString(R.string.category_user_bricks)) {
            addListFragment = UserDefinedBrickListFragment.newInstance(null)
            tag = UserDefinedBrickListFragment.USER_DEFINED_BRICK_LIST_FRAGMENT_TAG
        } else {
            addListFragment = AddBrickFragment.newInstance(category, null)
            tag = AddBrickFragment.ADD_BRICK_FRAGMENT_TAG
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, addListFragment, tag)
            .addToBackStack(null)
            .commit()
    }
}