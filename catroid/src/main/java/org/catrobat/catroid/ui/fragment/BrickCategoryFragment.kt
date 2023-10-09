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
package org.catrobat.catroid.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.ListFragment
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.BottomBar.hideBottomBar
import org.catrobat.catroid.ui.BottomBar.showBottomBar
import org.catrobat.catroid.ui.BottomBar.showPlayButton
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.ViewSwitchLock
import org.catrobat.catroid.ui.adapter.BrickCategoryAdapter
import org.catrobat.catroid.ui.addTabLayout
import org.catrobat.catroid.ui.removeTabLayout
import org.catrobat.catroid.utils.SnackbarUtil
import java.util.concurrent.locks.Lock

class BrickCategoryFragment : ListFragment() {
    private var previousActionBarTitle: CharSequence? = null
    private var scriptFragment: OnCategorySelectedListener? = null
    private var adapter: BrickCategoryAdapter? = null
    private val viewSwitchLock: Lock = ViewSwitchLock()

    companion object {
        const val BRICK_CATEGORY_FRAGMENT_TAG = "brick_category_fragment"
    }

    fun setOnCategorySelectedListener(listener: OnCategorySelectedListener) {
        scriptFragment = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isRestoringPreviouslyDestroyedActivity = savedInstanceState != null
        if (isRestoringPreviouslyDestroyedActivity) {
            parentFragmentManager.popBackStack(BRICK_CATEGORY_FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            return
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_brick_categories, container, false)
        setUpActionBar()
        hideBottomBar(activity)
        setupBrickCategories()
        return rootView
    }

    override fun onStart() {
        super.onStart()
        listView.onItemClickListener =
            AdapterView.OnItemClickListener setOnItemClickListener@{ parent: AdapterView<*>, view: View, position: Int, id: Long ->
                if (!viewSwitchLock.tryLock()) {
                    return@setOnItemClickListener
                }
                scriptFragment?.onCategorySelected(adapter?.getItem(position)) ?: return@setOnItemClickListener
            }
    }

    override fun onResume() {
        super.onResume()
        hideBottomBar(activity)
        setupBrickCategories()
        SnackbarUtil.showHintSnackbar(activity, R.string.hint_category)
    }

    override fun onPause() {
        super.onPause()
        showBottomBar(activity)
        showPlayButton(activity)
    }

    override fun onDestroy() {
        super.onDestroy()
        val actionBar = (activity as? AppCompatActivity)?.supportActionBar
        val isRestoringPreviouslyDestroyedActivity = actionBar == null
        if (!isRestoringPreviouslyDestroyedActivity) {
            actionBar?.setDisplayShowTitleEnabled(true)
            actionBar?.title = previousActionBarTitle
            showBottomBar(activity)
            showPlayButton(activity)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.delete).isVisible = false
        menu.findItem(R.id.copy).isVisible = false
        menu.findItem(R.id.backpack).isVisible = false
        menu.findItem(R.id.comment_in_out).isVisible = false
        menu.findItem(R.id.catblocks).isVisible = false
        menu.findItem(R.id.catblocks_reorder_scripts).isVisible = false
        menu.findItem(R.id.find).isVisible = false
        menu.findItem(R.id.search).isVisible = true
        menu.findItem(R.id.help).isVisible = false
    }

    private fun setUpActionBar() {
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(true)
        previousActionBarTitle = (activity as? AppCompatActivity)?.supportActionBar?.title.toString()
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.categories)
    }

    private fun setupBrickCategories() {
        val brickCategoryFactory = BrickCategoryListBuilder(requireActivity())
        val categories = brickCategoryFactory.getBrickCategoryViews()

        adapter = BrickCategoryAdapter(categories)
        listAdapter = adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity.removeTabLayout()
    }

    override fun onDetach() {
        activity.addTabLayout(SpriteActivity.FRAGMENT_SCRIPTS)
        super.onDetach()
    }

    interface OnCategorySelectedListener {
        fun onCategorySelected(category: String?)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val fragment = parentFragmentManager.findFragmentById(R.id.fragment_container)
        if (item.itemId == R.id.search && fragment is BrickCategoryFragment) {
            scriptFragment?.onCategorySelected(context?.getString(R.string.category_search_bricks))
        }
        return super.onOptionsItemSelected(item)
    }
}
