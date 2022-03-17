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

package org.catrobat.catroid.ui.recyclerview.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ReportBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.ui.recyclerview.adapter.YourFunctionsListRecyclerViewAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.YourFunctionsListRecyclerViewAdapter.YourFunctionListItem
import org.catrobat.catroid.ui.recyclerview.util.FormulaEditorRecyclerViewUtils

class YourFunctionsListFragment : Fragment(),
    YourFunctionsListRecyclerViewAdapter.OnItemClickListener {
    private var recyclerView: RecyclerView? = null
    private var currentSprite: Sprite? = null

    companion object {
        @JvmField
        val TAG: String = YourFunctionsListFragment::class.java.simpleName
        const val ACTION_BAR_TITLE_BUNDLE_ARGUMENT = "actionBarTitle"
        const val CURRENT_SPRITE_BUNDLE_ARGUMENT = "currentSprite"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parent = inflater.inflate(R.layout.fragment_list_view, container, false)
        recyclerView = parent.findViewById(R.id.recycler_view)
        setHasOptionsMenu(true)
        return parent
    }

    override fun onResume() {
        super.onResume()
        val arguments = arguments ?: return
        val appCompatActivity = activity as AppCompatActivity? ?: return
        val supportActionBar = appCompatActivity.supportActionBar
        if (supportActionBar != null) {
            val title = arguments.getString(ACTION_BAR_TITLE_BUNDLE_ARGUMENT)
            supportActionBar.title = title
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeAdapter()
    }

    private fun initializeAdapter() {
        arguments?.getSerializable(CURRENT_SPRITE_BUNDLE_ARGUMENT)
            .let { currentSprite = it as Sprite? }
        val brickList: List<UserDefinedReceiverBrick> = getReportBricks(currentSprite) ?: return
        val items = brickList.mapNotNull { brick ->
            val label = brick.userDefinedBrick.userDefinedBrickDataList.firstOrNull { data ->
                data.isLabel
            } ?: return@mapNotNull null

            val parametersList = brick.userDefinedBrick.userDefinedBrickDataList
                .filter { data ->
                    data.isInput
                }
                .map { data ->
                    data.name
                }

            val parameterListAsString = label.name + "(" + parametersList.joinToString(" ,") + ")"
            brick.nameForFormulaEditor = parameterListAsString

            YourFunctionListItem(parameterListAsString, label.name, parametersList)
        }

        val adapter = YourFunctionsListRecyclerViewAdapter(items, this)
        recyclerView?.adapter = adapter
    }

    private fun getReportBricks(currentSprite: Sprite?): List<UserDefinedReceiverBrick>? {
        val allBricks: MutableList<Brick> = currentSprite?.allBricks ?: return null
        val userDefBricks: List<UserDefinedReceiverBrick> =
            allBricks.filterIsInstance<UserDefinedReceiverBrick>()
        val brickList = arrayListOf<UserDefinedReceiverBrick>()
        for (brick in userDefBricks) {
            val childBricks: List<Brick> = brick.script.brickList
            val reportBricks = childBricks.filterIsInstance<ReportBrick>()
            if (reportBricks.isNotEmpty()) {
                brickList.add(brick)
            }
        }
        return brickList
    }

    override fun onItemClick(item: YourFunctionListItem?) {
        item?.let { yourFunctionListItem ->
            FormulaEditorRecyclerViewUtils.addResourceToActiveFormulaInFormulaEditor(
                fragmentManager,
                yourFunctionListItem
            )
        }
        requireActivity().onBackPressed()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        for (index in 0 until menu.size()) {
            menu.getItem(index).isVisible = false
        }
        val appCompatActivity = activity as AppCompatActivity
        appCompatActivity.menuInflater.inflate(R.menu.menu_formulareditor_category, menu)
        val supportActionBar = appCompatActivity.supportActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.wiki_help) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(Constants.CATROBAT_YOUR_FUNCTIONS_WIKI_URL + activity?.let { fragmentActivity ->
                        FormulaEditorRecyclerViewUtils.getLanguage(fragmentActivity)
                    })
                )
            )
        }
        return true
    }
}
