/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.ListSelectorBrick
import org.catrobat.catroid.formulaeditor.UserData
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.ui.BottomBar
import org.catrobat.catroid.ui.UiUtils
import org.catrobat.catroid.ui.recyclerview.adapter.DataListAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH
import org.catrobat.catroid.utils.ToastUtil

import java.util.ArrayList

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher
import org.catrobat.catroid.utils.UserDataUtil

class ListSelectorFragment : Fragment(), RVAdapter.SelectionListener,
    RVAdapter.OnItemClickListener<UserData<*>> {

    private var recyclerView: RecyclerView? = null
    private var adapter: DataListAdapter? = null
    private var listSelectorInterface: ListSelectorInterface? = null
    private var preSelection: List<UserList>? = null

    private fun updateSelection(userLists: List<UserList>) {
        adapter?.clearSelection()
        userLists.forEach { list ->
            adapter?.setSelection(list, true)
        }

        updateTitle()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.context_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toggle_selection -> {
                if (adapter?.itemCount != adapter?.selectedItemCount) {
                    adapter?.selectAll()
                } else {
                    adapter?.clearSelection()
                }
                updateTitle()
            }
            R.id.confirm -> handleContextualAction()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleContextualAction() {
        val selectedItems = adapter?.selectedItems ?: return
        listSelectorInterface?.onUserListSelected(selectedItems.filterIsInstance<UserList>())
        fragmentManager?.popBackStack()
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

    override fun onActivityCreated(savedInstance: Bundle?) {
        super.onActivityCreated(savedInstance)
        initializeAdapter()
    }

    override fun onResume() {
        super.onResume()
        BottomBar.showBottomBar(activity)
        BottomBar.hidePlayButton(activity)
    }

    override fun onStop() {
        super.onStop()
        BottomBar.hideBottomBar(activity)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        for (index in 0 until menu.size()) {
            menu.getItem(index).isVisible = false
        }

        menu.findItem(R.id.toggle_selection).setEnabled(true).isVisible = true
        menu.findItem(R.id.confirm).setEnabled(true).isVisible = true

        super.onPrepareOptionsMenu(menu)
    }

    private fun initializeAdapter() {
        val globalLists = ProjectManager.getInstance().currentProject.userLists
        val localLists = ProjectManager.getInstance().currentSprite.userLists

        adapter = DataListAdapter(ArrayList(), ArrayList(), ArrayList(), ArrayList(), globalLists, localLists)
        adapter?.showCheckBoxes(true)
        onAdapterReady()
    }

    private fun onAdapterReady() {
        recyclerView?.adapter = adapter
        adapter?.setSelectionListener(this)
        adapter?.setOnItemClickListener(this)

        updateSelection(preSelection ?: emptyList())
    }

    fun notifyDataSetChanged() {
        adapter?.notifyDataSetChanged()
    }

    private fun deleteItems(selectedItems: List<UserData<*>>) {
        for (item in selectedItems) {
            adapter?.remove(item)
        }
        ProjectManager.getInstance().currentProject.deselectElements(selectedItems)
        ToastUtil.showSuccess(
            activity, resources.getQuantityString(
                R.plurals.deleted_Items,
                selectedItems.size,
                selectedItems.size
            )
        )
    }

    private fun showDeleteAlert(selectedItems: List<UserData<*>>) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.deletion_alert_title)
            .setMessage(R.string.deletion_alert_text)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteItems(
                    selectedItems
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .setCancelable(false)
            .show()
    }

    private fun showRenameDialog(selectedItems: List<UserData<*>>) {
        val item = selectedItems[0]
        val builder = TextInputDialog.Builder(requireContext())
        val items = adapter!!.items
        builder.setHint(getString(R.string.data_label))
            .setText(item.name)
            .setTextWatcher(
                DuplicateInputTextWatcher(
                    items
                )
            )
            .setPositiveButton(
                getString(R.string.ok)
            ) { _: DialogInterface?, textInput: String? ->
                renameItem(
                    item,
                    textInput
                )
            }
        builder.setTitle(R.string.rename_data_dialog)
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun renameItem(item: UserData<*>, name: String?) {
        val previousName = item.name
        ProjectManager.getInstance().currentProject.updateUserDataReferences(
            previousName,
            name,
            item
        )
        UserDataUtil.renameUserData(item, name ?: "")
        adapter?.updateDataSet()
    }

    private fun updateTitle() {
        adapter?.selectedItemCount?.let {
            (activity as? AppCompatActivity)?.supportActionBar?.title = resources.getQuantityString(
                R.plurals.list_selection_plural, it, it
            )
        }
    }

    override fun onSelectionChanged(selectedItemCnt: Int) {
        updateTitle()
    }

    override fun onItemClick(item: UserData<*>) {
        adapter?.toggleSelection(item)
        updateTitle()
    }

    override fun onItemLongClick(item: UserData<*>, holder: CheckableVH) {
        val items = arrayOf<CharSequence>(getString(R.string.delete), getString(R.string.rename))
        AlertDialog.Builder(requireActivity())
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> showDeleteAlert(listOf(item))
                    1 -> showRenameDialog(listOf(item))
                    else -> dialog.dismiss()
                }
            }
            .show()
    }

    interface ListSelectorInterface {
        fun onUserListSelected(userLists: List<UserList>)
    }

    companion object {
        private val TAG: String = ListSelectorFragment::class.java.simpleName

        @JvmStatic
        fun showFragment(context: Context, selectorBrick: ListSelectorBrick) {
            val activity = UiUtils.getActivityFromContextWrapper(context) ?: return

            var listSelectorFragment = activity.supportFragmentManager
                .findFragmentByTag(TAG) as ListSelectorFragment?

            if (listSelectorFragment == null) {
                listSelectorFragment = ListSelectorFragment()

                listSelectorFragment.listSelectorInterface = selectorBrick

                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, listSelectorFragment, TAG)
                    .addToBackStack(TAG)
                    .commit()
            }

            listSelectorFragment.preSelection = selectorBrick.userLists
        }
    }
}
