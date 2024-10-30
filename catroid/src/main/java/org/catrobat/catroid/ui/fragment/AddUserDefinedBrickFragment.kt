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
 * but WITHOUT ANY WARRANTY without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.userbrick.UserDefinedBrickData.UserDefinedBrickDataType
import org.catrobat.catroid.utils.ToastUtil
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import org.catrobat.catroid.userbrick.UserDefinedBrickData.UserDefinedBrickDataType.INPUT
import org.catrobat.catroid.userbrick.UserDefinedBrickData.UserDefinedBrickDataType.LABEL
import org.koin.android.ext.android.inject

class AddUserDefinedBrickFragment : Fragment() {
    companion object {
        const val TAG: String = "add_user_defined_brick_fragment"
        fun newInstance(addBrickListener: AddBrickFragment.OnAddBrickListener): AddUserDefinedBrickFragment {
            val fragment = AddUserDefinedBrickFragment()
            fragment.addBrickListener = addBrickListener
            return fragment
        }
    }

    private var addBrickListener: AddBrickFragment.OnAddBrickListener? = null
    private var userDefinedBrick: UserDefinedBrick? = null
    private var userBrickView: View? = null
    private var userBrickSpace: LinearLayout? = null
    private var scrollView: ScrollView? = null
    private var confirmItem: MenuItem? = null

    private var addLabel: Button? = null

    private var addInput: Button? = null
    private val projectManager: ProjectManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_user_defined_brick, container, false)
        userBrickSpace = view.findViewById(R.id.user_brick_space)
        scrollView = view.findViewById(R.id.fragment_add_user_defined_brick)

        addLabel = view.findViewById(R.id.button_add_label)
        addInput = view.findViewById(R.id.button_add_input)

        addLabel?.setOnClickListener { handleAddUserDefinedBrickData(LABEL) }
        addInput?.setOnClickListener { handleAddUserDefinedBrickData(INPUT) }

        val arguments = arguments
        arguments?.let {
            userDefinedBrick =
                arguments.getSerializable(UserDefinedBrick.USER_BRICK_BUNDLE_ARGUMENT)
                    as UserDefinedBrick
            if (userDefinedBrick != null) {
                userBrickView = userDefinedBrick?.getView(activity)
                userBrickSpace?.addView(userBrickView)
            }
        }
        val activity = activity as AppCompatActivity

        val actionBar = activity.supportActionBar
        actionBar?.setTitle(R.string.category_user_bricks)
        setHasOptionsMenu(true)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val activity = activity as AppCompatActivity
        val actionBar = activity.supportActionBar
        actionBar?.let { activity.supportActionBar }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_confirm_userdefined, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        for (index in 0 until menu.size()) {
            menu.getItem(index).isVisible = false
        }
        confirmItem = menu.findItem(R.id.confirm)
        confirmItem?.isVisible = true
        confirmItem?.isEnabled = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.confirm) {
            val currentSprite = projectManager.currentSprite
            val brickIsEmpty: Boolean = userDefinedBrick?.isEmpty as Boolean

            if (brickIsEmpty) {
                userDefinedBrick?.addLabel("")
            }

            if (currentSprite.doesUserBrickAlreadyExist(userDefinedBrick)) {
                ToastUtil.showErrorWithColor(
                    context,
                    R.string.brick_user_defined_already_exists,
                    Color.RED
                )
                if (brickIsEmpty) {
                    userDefinedBrick?.removeLastLabel()
                }
            } else {
                currentSprite.addUserDefinedBrick(userDefinedBrick)
                addUserDefinedScriptToScript(userDefinedBrick)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addUserDefinedScriptToScript(brickToAddScript: UserDefinedBrick?) {

        val scriptBrick = UserDefinedReceiverBrick(brickToAddScript)
        addBrickListener?.addBrick(scriptBrick)

        val fragmentManager = parentFragmentManager
        fragmentManager.let {
            val fragmentTransaction = fragmentManager.beginTransaction()

            val categoryFragment = fragmentManager.findFragmentByTag(
                BrickCategoryFragment
                    .BRICK_CATEGORY_FRAGMENT_TAG
            )

            if (categoryFragment != null) {
                fragmentTransaction.remove(categoryFragment)
                fragmentManager.popBackStack()
            }

            val userBrickListFragment = fragmentManager.findFragmentByTag(
                UserDefinedBrickListFragment
                    .USER_DEFINED_BRICK_LIST_FRAGMENT_TAG
            )
            if (userBrickListFragment != null) {
                fragmentTransaction.remove(userBrickListFragment)
                fragmentManager.popBackStack()
            }

            val addUserBrickFragment =
                fragmentManager.findFragmentByTag(TAG)

            if (addUserBrickFragment != null) {
                fragmentTransaction.remove(addUserBrickFragment)
                fragmentManager.popBackStack()
            }

            fragmentTransaction.commit()
        }
    }

    private fun handleAddUserDefinedBrickData(dataType: UserDefinedBrickDataType) {
        val addUserDataToUserDefinedBrickFragment = AddUserDataToUserDefinedBrickFragment()

        val bundle = Bundle()
        bundle.putSerializable(UserDefinedBrick.USER_BRICK_BUNDLE_ARGUMENT, userDefinedBrick)
        bundle.putSerializable(UserDefinedBrick.ADD_INPUT_OR_LABEL_BUNDLE_ARGUMENT, dataType)

        addUserDataToUserDefinedBrickFragment.arguments = bundle

        val fragmentManager = parentFragmentManager
        fragmentManager.let {
            fragmentManager.beginTransaction()
                .add(
                    R.id.fragment_container, addUserDataToUserDefinedBrickFragment,
                    AddUserDataToUserDefinedBrickFragment.TAG
                )
                .addToBackStack(AddUserDataToUserDefinedBrickFragment.TAG)
                .commit()
        }
    }

    fun addUserDataToUserBrick(input: String?, dataType: UserDefinedBrickDataType?) {
        if (dataType == INPUT) {
            userDefinedBrick?.addInput(input)
        } else {
            userDefinedBrick?.addLabel(input)
        }
        updateBrickView()
        scrollView?.fullScroll(View.FOCUS_DOWN)
    }

    private fun updateBrickView() {
        userBrickSpace?.removeView(userBrickView)
        userBrickView = userDefinedBrick?.getView(activity)
        userBrickSpace?.addView(userBrickView)
    }
}
