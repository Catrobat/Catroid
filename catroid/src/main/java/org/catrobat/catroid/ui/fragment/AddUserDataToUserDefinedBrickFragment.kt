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
import android.text.Editable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.InputWatcher
import org.catrobat.catroid.userbrick.UserDefinedBrickData.UserDefinedBrickDataType
import org.catrobat.catroid.utils.Utils

class AddUserDataToUserDefinedBrickFragment : Fragment() {

    companion object {
        const val TAG: String = "add_user_data_to_user_defined_brick_fragment"
    }

    private var dataTypeToAdd: UserDefinedBrickDataType? = null
    private var addUserDataUserBrickEditText: TextInputEditText? = null
    private var addUserDataUserBrickTextLayout: TextInputLayout? = null
    private var scrollView: ScrollView? = null

    private var nextItem: MenuItem? = null

    private var userDefinedBrick: UserDefinedBrick? = null
    private var userBrickTextView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_add_user_data_to_user_brick, container,
            false
        )
        val userBrickSpace = view.findViewById<LinearLayout>(R.id.user_brick_space)
        scrollView = view.findViewById(R.id.fragment_add_user_data_to_user_brick)

        val arguments = arguments
        arguments?.let {
            userDefinedBrick =
                arguments.getSerializable(UserDefinedBrick.USER_BRICK_BUNDLE_ARGUMENT)
                    as UserDefinedBrick
            dataTypeToAdd =
                arguments.getSerializable(UserDefinedBrick.ADD_INPUT_OR_LABEL_BUNDLE_ARGUMENT)
                    as UserDefinedBrickDataType
        }
        userDefinedBrick?.let {
            val userBrickView = userDefinedBrick?.getView(requireActivity())
            userBrickSpace.addView(userBrickView)
            userBrickTextView = userDefinedBrick?.currentUserDefinedDataTextView
        }
        addUserDataUserBrickEditText = view.findViewById(R.id.user_data_user_brick_edit_field)
        addUserDataUserBrickTextLayout = view.findViewById(R.id.user_data_user_brick_text_layout)

        val addUserDataUserBrickTextView = view.findViewById<TextView>(
            R.id
                .brick_user_defined_add_user_data_description
        )

        addUserDataUserBrickEditText?.setText(userBrickTextView?.text)
        val textWatcher = object : InputWatcher.TextWatcher() {
            override fun afterTextChanged(editable: Editable?) {
                userBrickTextView?.text = editable.toString()
                if (dataTypeToAdd == UserDefinedBrickDataType.INPUT) {
                    val error = validateInput(editable.toString(), getContext())
                    if (error != null) {
                        scrollView?.post {
                            scrollView?.fullScroll(View.FOCUS_DOWN)
                        }
                    }
                    addUserDataUserBrickTextLayout?.error = error
                    setNextItemEnabled(error == null)
                }
            }
        }

        context?.let {
            textWatcher.setScope(userDefinedBrick?.getUserDataListAsStrings(UserDefinedBrickDataType.INPUT))
            addUserDataUserBrickEditText?.addTextChangedListener(textWatcher)
            if (dataTypeToAdd == UserDefinedBrickDataType.INPUT) {
                addUserDataUserBrickTextView.text = context?.resources?.getString(
                    R.string
                        .brick_user_defined_add_input_description
                )
            } else {
                addUserDataUserBrickTextView.text = context?.resources?.getString(
                    R.string
                        .brick_user_defined_add_label_description
                )
            }
        }

        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.let {
            Utils.showStandardSystemKeyboard(appCompatActivity)
            val actionBar = appCompatActivity.supportActionBar
            if (actionBar != null) {
                appCompatActivity.supportActionBar?.setTitle(R.string.category_user_bricks)
            }
        }

        view.findViewById<LinearLayout>(R.id.bottom_constraint).requestFocus()
        setHasOptionsMenu(true)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Utils.showStandardSystemKeyboard(requireActivity())
    }

    override fun onDetach() {
        super.onDetach()
        Utils.hideStandardSystemKeyboard(requireActivity())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_next, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        for (index in 0 until menu.size()) {
            menu.getItem(index).isVisible = false
        }
        nextItem = menu.findItem(R.id.next)
        nextItem?.isVisible = true
        nextItem?.isEnabled = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.next) {
            val fragmentManager = parentFragmentManager
            Utils.hideStandardSystemKeyboard(requireActivity())
            fragmentManager.let {
                val addUserDefinedBrickFragment =
                    fragmentManager.findFragmentByTag(AddUserDefinedBrickFragment.TAG)
                        as AddUserDefinedBrickFragment
                fragmentManager.popBackStackImmediate()
                if (addUserDataUserBrickEditText?.text != null) {
                    addUserDefinedBrickFragment.addUserDataToUserBrick(
                        addUserDataUserBrickEditText?.text.toString(),
                        dataTypeToAdd
                    )
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setNextItemEnabled(enabled: Boolean) {
        nextItem?.isEnabled = enabled
    }

    fun getDataTypeToAdd(): UserDefinedBrickDataType? = dataTypeToAdd
}
