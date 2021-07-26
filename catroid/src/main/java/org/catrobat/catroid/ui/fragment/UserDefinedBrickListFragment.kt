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

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.ListFragment
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.databinding.FragmentUserDefinedBrickListBinding
import org.catrobat.catroid.ui.adapter.PrototypeBrickAdapter
import org.catrobat.catroid.ui.fragment.AddBrickFragment.OnAddBrickListener
import org.catrobat.catroid.utils.ToastUtil

class UserDefinedBrickListFragment : ListFragment(), View.OnClickListener {

    private var addBrickListener: OnAddBrickListener? = null
    private var adapter: PrototypeBrickAdapter? = null

    private var _binding: FragmentUserDefinedBrickListBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        setActionBar(R.string.categories)
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDefinedBrickListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonAddUserBrick.setOnClickListener(this)

        setupUserDefinedBrickListView()
        setActionBar(R.string.category_user_bricks)
    }

    private fun setActionBar(resId: Int) {
        val activity = activity as AppCompatActivity?
        val actionBar = activity?.supportActionBar
        actionBar?.setTitle(resId)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.search).isVisible = false
    }

    override fun onStart() {
        super.onStart()
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
                adapter?.getItem(position)?.let {
                    addUserDefinedBrickToScript(it)
                }
            }
    }

    private fun setupUserDefinedBrickListView() {
        val categoryBricksFactory = CategoryBricksFactory()
        requireContext().let {
            val brickList = categoryBricksFactory.getBricks(
                getString(R.string.category_user_bricks),
                false,
                it
            )
            adapter = PrototypeBrickAdapter(brickList)
            listAdapter = adapter
        }
    }

    override fun onClick(v: View) {
        val addUserDefinedBrickFragment = AddUserDefinedBrickFragment.newInstance(addBrickListener!!)
        val userDefinedBrick = UserDefinedBrick()

        val bundle = Bundle()
        bundle.putSerializable(UserDefinedBrick.USER_BRICK_BUNDLE_ARGUMENT, userDefinedBrick)
        addUserDefinedBrickFragment.arguments = bundle

        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction()
            .add(
                R.id.fragment_container,
                addUserDefinedBrickFragment,
                AddUserDefinedBrickFragment.TAG
            )
            .addToBackStack(AddUserDefinedBrickFragment.TAG).commit()
    }

    private fun addUserDefinedBrickToScript(userDefinedBrickToAdd: Brick) {
        try {
            val clonedBrick = userDefinedBrickToAdd.clone()
            if (userDefinedBrickToAdd is UserDefinedBrick) {
                (clonedBrick as UserDefinedBrick).setCallingBrick(true)
            }
            addBrickListener?.addBrick(clonedBrick)

            val fragmentTransaction = parentFragmentManager.beginTransaction()

            val categoryFragment =
                parentFragmentManager.findFragmentByTag(BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG)
            categoryFragment?.let {
                fragmentTransaction.remove(it)
                parentFragmentManager.popBackStack()
            }

            val addBrickFragment =
                parentFragmentManager.findFragmentByTag(USER_DEFINED_BRICK_LIST_FRAGMENT_TAG)
            addBrickFragment?.let {
                fragmentTransaction.remove(addBrickFragment)
                parentFragmentManager.popBackStack()
            }
            fragmentTransaction.commit()
        } catch (e: CloneNotSupportedException) {
            e.message?.let { Log.e(tag, it) }
            ToastUtil.showError(requireContext(), R.string.error_adding_brick)
        }
    }

    companion object {
        @JvmField
        val USER_DEFINED_BRICK_LIST_FRAGMENT_TAG = AddBrickFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(addBrickListener: OnAddBrickListener?): UserDefinedBrickListFragment {
            val fragment = UserDefinedBrickListFragment()
            fragment.addBrickListener = addBrickListener
            return fragment
        }
    }
}
