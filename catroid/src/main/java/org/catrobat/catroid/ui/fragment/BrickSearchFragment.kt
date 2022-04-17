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

package org.catrobat.catroid.ui.fragment

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.ListFragment
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.ui.BottomBar.hideBottomBar
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.adapter.PrototypeBrickAdapter
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.ui.hideKeyboard
import java.util.Locale
import android.widget.AbsListView

class BrickSearchFragment : ListFragment() {

    private var previousActionBarTitle: CharSequence? = null

    private var searchView: SearchView? = null
    private var queryTextListener: SearchView.OnQueryTextListener? = null
    private var availableBricks: MutableList<Brick> = mutableListOf()
    private var searchResults = mutableListOf<Brick>()
    private var addBrickListener: AddBrickFragment.OnAddBrickListener? = null
    private var category: String? = null

    private var adapter: PrototypeBrickAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_brick_search, container, false)
        val actionBar = (activity as? AppCompatActivity)?.supportActionBar
        previousActionBarTitle = actionBar?.title
        hideBottomBar(activity)
        setHasOptionsMenu(true)
        category?.let { prepareBrickList(it) }
        return view
    }

    override fun onStart() {
        super.onStart()
        if (listIndexToFocus != -1) {
            listView.setSelection(listIndexToFocus)
            listIndexToFocus = -1
        }
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long -> adapter?.getItem(position)?.let { addBrickToScript(
            it,
            activity as SpriteActivity,
            addBrickListener,
            parentFragmentManager,
            BRICK_SEARCH_FRAGMENT_TAG
        ) }
        }
    }

    override fun onDestroy() {
        val actionBar = (activity as? AppCompatActivity)?.supportActionBar
        val isRestoringPreviouslyDestroyedActivity = actionBar == null
        if (!isRestoringPreviouslyDestroyedActivity) {
            actionBar?.title = previousActionBarTitle
        }
        searchView.hideKeyboard()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.search_bar).actionView
        val searchManager: SearchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (searchItem as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            isIconified = false
            queryHint = context.getString(R.string.search_hint)
        }
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(
                view: AbsListView,
                scrollState: Int
            ) {
                    searchView.hideKeyboard()
            }

            @SuppressWarnings("EmptyFunctionBlock")
            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {}
        })

        searchView = searchItem
        if (searchView != null) {
            searchView?.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            queryTextListener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(query: String): Boolean {
                    searchResults.clear()
                    searchBrick(query)
                    if (searchResults.isEmpty()) {
                        ToastUtil.showError(context, context?.getString(R.string.no_results_found))
                    }
                    if (query.isEmpty()) {
                        searchResults.clear()
                    }
                    adapter = PrototypeBrickAdapter(searchResults)
                    listAdapter = adapter

                    return true
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    searchResults.clear()
                    searchBrick(query)
                    adapter = PrototypeBrickAdapter(searchResults)
                    listAdapter = adapter
                    if (searchResults.isEmpty()) {
                        ToastUtil.showError(context, context?.getString(R.string.no_results_found))
                    } else searchView?.clearFocus()
                    return true
                }
            }
            searchView?.setOnQueryTextListener(queryTextListener)
            searchView?.requestFocus()
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        for (index in 0 until menu.size()) {
            menu.getItem(index).setVisible(false)
        }
        menu.findItem(R.id.search_bar).isVisible = true
    }

    private fun onlyBeginnerBricks(): Boolean = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(AccessibilityProfile.BEGINNER_BRICKS, false)

    private fun searchBrick(query: String) {
        availableBricks.forEach { brick ->
            val regexQuery = (".*" + query.toLowerCase(Locale.ROOT).replace("\\s".toRegex(), ".*") + ".*").toRegex()
            val brickView = brick.getView(context)
            if (regexQuery.containsMatchIn(findBrickString(brickView)) && !searchResultContains(brick)) {
                searchResults.add(brick)
            }
        }
    }
    private fun searchResultContains(brick: Brick): Boolean {
        searchResults.forEach {
            if (brick.javaClass == it.javaClass) {
                return true
            }
        }
        return false
    }

    private fun findBrickString(view: View): String {
        var wholeStringFoundInBrick = ""
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                val stringFoundInBrick = findBrickString(child)
                if (stringFoundInBrick.isNotBlank()) wholeStringFoundInBrick = wholeStringFoundInBrick.plus(stringFoundInBrick)
            }
        } else if (view is TextView) return view.text.toString().toLowerCase(Locale.ROOT)
        return wholeStringFoundInBrick
        }

    @SuppressWarnings("ComplexMethod")
    fun prepareBrickList(category: String = "") {
        val categoryBricksFactory: CategoryBricksFactory = when {
            onlyBeginnerBricks() -> CategoryBeginnerBricksFactory()
            else -> CategoryBricksFactory()
        }
        val backgroundSprite = ProjectManager.getInstance().currentlyEditedScene.backgroundSprite
        val sprite = ProjectManager.getInstance().currentSprite
        if (category != context?.getString(R.string.category_search_bricks)) {
            availableBricks.clear()
            availableBricks.addAll(categoryBricksFactory.getBricks(category, backgroundSprite.equals(sprite), requireContext()))
        } else {
            availableBricks.addAll(categoryBricksFactory.getBricks(requireContext().getString(R.string.category_recently_used), backgroundSprite.equals(sprite), requireContext()))
            availableBricks.addAll(categoryBricksFactory.getBricks(requireContext().getString(R.string.category_event), backgroundSprite.equals(sprite), requireContext()))
            availableBricks.addAll(categoryBricksFactory.getBricks(requireContext().getString(R.string.category_control), backgroundSprite.equals(sprite), requireContext()))
            availableBricks.addAll(categoryBricksFactory.getBricks(requireContext().getString(R.string.category_motion), backgroundSprite.equals(sprite), requireContext()))
            availableBricks.addAll(categoryBricksFactory.getBricks(requireContext().getString(R.string.category_sound), backgroundSprite.equals(sprite), requireContext()))
            availableBricks.addAll(categoryBricksFactory.getBricks(requireContext().getString(R.string.category_looks), backgroundSprite.equals(sprite), requireContext()))

            if (!onlyBeginnerBricks()) {
                availableBricks.addAll(categoryBricksFactory.getBricks(requireContext().getString(R.string.category_pen), backgroundSprite.equals(sprite), requireContext()))
            }
            availableBricks.addAll(categoryBricksFactory.getBricks(requireContext().getString(R.string.category_data), backgroundSprite.equals(sprite), requireContext()))
            availableBricks.addAll(categoryBricksFactory.getBricks(requireContext().getString(R.string.category_device), backgroundSprite.equals(sprite), requireContext()))
            if (!onlyBeginnerBricks()) {
                availableBricks.addAll(categoryBricksFactory.getBricks(requireContext().getString(R.string.category_user_bricks), backgroundSprite.equals(sprite), requireContext()))
            }
            if (SettingsFragment.isTestSharedPreferenceEnabled(activity)) {
                availableBricks.addAll(categoryBricksFactory.getBricks(requireContext().getString(R.string.category_assertions), backgroundSprite.equals(sprite), requireContext()))
            }
        }
    }

    companion object {
        @JvmField
        val BRICK_SEARCH_FRAGMENT_TAG = BrickSearchFragment::class.java.simpleName
        private var listIndexToFocus = -1
        @JvmStatic
        fun newInstance(addBrickListener: AddBrickFragment.OnAddBrickListener?, selectedCategory: String?):
            BrickSearchFragment {
            val fragment = BrickSearchFragment()
            fragment.category = selectedCategory
            fragment.addBrickListener = addBrickListener
            return fragment
        }
    }
}
