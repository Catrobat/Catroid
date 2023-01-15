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
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.ViewGroup
import android.view.View
import android.view.Menu
import android.view.MenuInflater
import android.view.LayoutInflater
import android.widget.AdapterView
import androidx.core.content.ContextCompat.startActivity
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.adapter.PrototypeBrickAdapter
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile
import org.koin.java.KoinJavaComponent.inject

class HelpBrickFragment(val title: String) : BaseListFragment(title) {
    private var adapter: PrototypeBrickAdapter? = null
    private fun onlyBeginnerBricks(): Boolean = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(AccessibilityProfile.BEGINNER_BRICKS, false)
    private val projectManager: ProjectManager by inject(ProjectManager::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_brick_help, container, false)
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
        adapter = brickList?.let { PrototypeBrickAdapter(it) }
        listAdapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.help).isVisible = false
        menu.findItem(R.id.search).isVisible = false
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menu.findItem(R.id.comment_in_out).isVisible = false
        menu.findItem(R.id.search).isVisible = false
    }

    override fun onResume() {
        super.onResume()
        setupSelectedBrickCategory()
    }

    override fun onStart() {
        super.onStart()
        if (listIndexToFocus != -1) {
            listView.setSelection(listIndexToFocus)
            listIndexToFocus = -1
        }
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long -> adapter?.getItem(position)?.let { helpBrick(
            it,
            activity as SpriteActivity
        ) }
        }
    }

    companion object {
        @JvmField
        val HELP_BRICK_FRAGMENT_TAG = HelpBrickFragment::class.java.simpleName
        private const val BUNDLE_ARGUMENTS_SELECTED_CATEGORY = "selected_category"
        private var listIndexToFocus = -1
        @JvmStatic
        fun newInstance(selectedCategory: String?, title: String): HelpBrickFragment {
            val fragment = HelpBrickFragment(title)
            val arguments = Bundle()
            arguments.putString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY, selectedCategory)
            fragment.arguments = arguments
            return fragment
        }
    }
}

fun helpBrick(brick: Brick, activity: SpriteActivity) {
    startActivity(activity,
            Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(brick.getHelpUrl(""))
            ), null
    )
}
