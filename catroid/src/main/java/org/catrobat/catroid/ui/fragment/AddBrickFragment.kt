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

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.ListFragment
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.cast.CastManager
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.adapter.PrototypeBrickAdapter
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile
import org.catrobat.catroid.utils.SnackbarUtil
import org.catrobat.catroid.utils.ToastUtil
import org.koin.java.KoinJavaComponent.inject

class AddBrickFragment : ListFragment() {
    private var addBrickListener: OnAddBrickListener? = null
    private var previousActionBarTitle: CharSequence? = null
    private var adapter: PrototypeBrickAdapter? = null
    private fun onlyBeginnerBricks(): Boolean = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(AccessibilityProfile.BEGINNER_BRICKS, false)
    private val projectManager: ProjectManager by inject(ProjectManager::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_brick_add, container, false)
        previousActionBarTitle = (activity as? AppCompatActivity)?.supportActionBar?.title
        (activity as? AppCompatActivity)?.supportActionBar?.title = arguments?.getString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY)
        setupSelectedBrickCategory()
        return view
    }

    private fun setupSelectedBrickCategory() {
        val context: Context? = activity
        val sprite = projectManager.currentSprite
        val backgroundSprite = projectManager.currentlyEditedScene.backgroundSprite
        val selectedCategory = arguments?.getString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY)
        val categoryBricksFactory: CategoryBricksFactory = when {
            onlyBeginnerBricks() -> CategoryBeginnerBricksFactory()
            else -> CategoryBricksFactory()
        }
        val brickList = selectedCategory?.let { context?.let { it1 ->
            categoryBricksFactory.getBricks(it, backgroundSprite == sprite,
                                            it1
            )
        } }
        adapter = PrototypeBrickAdapter(brickList)
        listAdapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menu.findItem(R.id.comment_in_out).isVisible = false
    }

    override fun onDestroy() {
        val actionBar = (activity as? AppCompatActivity)?.supportActionBar
        val isRestoringPreviouslyDestroyedActivity = actionBar == null
        if (!isRestoringPreviouslyDestroyedActivity) {
            actionBar?.title = previousActionBarTitle
        }
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        setupSelectedBrickCategory()
        SnackbarUtil.showHintSnackbar(activity, R.string.hint_bricks)
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
            ADD_BRICK_FRAGMENT_TAG) }
        }
    }

    interface OnAddBrickListener {
        fun addBrick(brick: Brick?)
    }

    companion object {
        @JvmField
        val ADD_BRICK_FRAGMENT_TAG = AddBrickFragment::class.java.simpleName
        private const val BUNDLE_ARGUMENTS_SELECTED_CATEGORY = "selected_category"
        private var listIndexToFocus = -1
        @JvmStatic
        fun newInstance(selectedCategory: String?, addBrickListener: OnAddBrickListener?): AddBrickFragment {
            val fragment = AddBrickFragment()
            val arguments = Bundle()
            arguments.putString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY, selectedCategory)
            fragment.arguments = arguments
            fragment.addBrickListener = addBrickListener
            return fragment
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.search) {
            (addBrickListener as BrickCategoryFragment.OnCategorySelectedListener).onCategorySelected(arguments?.getString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY))
        }
        return super.onOptionsItemSelected(item)
    }
}
fun addBrickToScript(brick: Brick, activity: SpriteActivity, addBrickListener: AddBrickFragment.OnAddBrickListener?, parentFragmentManager: FragmentManager, tag: String) {
    if (ProjectManager.getInstance().currentProject.isCastProject && CastManager.unsupportedBricks.contains(brick.javaClass)) {
        ToastUtil.showError(activity, R.string.error_unsupported_bricks_chromecast)
        return
    }
    try {
        val brickToAdd = brick.clone()
        addBrickListener?.addBrick(brickToAdd)
        SnackbarUtil.showHintSnackbar(activity, R.string.hint_scripts)
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        val categoryFragment = parentFragmentManager.findFragmentByTag(BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG)
        if (categoryFragment != null) {
            fragmentTransaction.remove(categoryFragment)
            parentFragmentManager.popBackStack()
        }
        val fragment = parentFragmentManager.findFragmentByTag(tag)
        if (fragment != null) {
            fragmentTransaction.remove(fragment)
            parentFragmentManager.popBackStack()
        }
        fragmentTransaction.commit()
    } catch (e: CloneNotSupportedException) {
        Log.e(tag, e.localizedMessage)
        ToastUtil.showError(activity, R.string.error_adding_brick)
    }
}
