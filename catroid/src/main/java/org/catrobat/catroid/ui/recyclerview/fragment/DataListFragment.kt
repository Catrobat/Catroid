/*Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.content.DialogInterface
import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import kotlinx.android.synthetic.main.fragment_list_view.view.empty_view
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.content.bricks.UserListBrick
import org.catrobat.catroid.content.bricks.UserVariableBrickWithFormula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.UserData
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.ui.BottomBar
import org.catrobat.catroid.ui.recyclerview.adapter.DataListAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH
import org.catrobat.catroid.userbrick.UserDefinedBrickInput
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.UserDataUtil.renameUserData
import java.util.ArrayList

open class DataListFragment : Fragment(),
    ActionMode.Callback, RVAdapter.SelectionListener,
    RVAdapter.OnItemClickListener<UserData<*>> {
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(NONE, DELETE)
    internal annotation class ActionModeType

    private val projectManager = ProjectManager.getInstance()

    @VisibleForTesting
    var recyclerView: RecyclerView? = null
    private var adapter: DataListAdapter? = null
    private var actionMode: ActionMode? = null
    private var formulaEditorDataInterface: FormulaEditorDataInterface? = null
    private var parentScriptBrick: ScriptBrick? = null
    private var emptyView: TextView? = null

    @ActionModeType
    var actionModeType = NONE
    fun setFormulaEditorDataInterface(formulaEditorDataInterface: FormulaEditorDataInterface?) {
        this.formulaEditorDataInterface = formulaEditorDataInterface
    }

    override fun onCreateActionMode(
        mode: ActionMode,
        menu: Menu
    ): Boolean {
        when (actionModeType) {
            DELETE -> mode.title = getString(R.string.am_delete)
            NONE -> return false
        }
        val inflater = mode.menuInflater
        inflater.inflate(R.menu.context_menu, menu)
        adapter?.showCheckBoxes(true)
        adapter?.updateDataSet()
        return true
    }

    override fun onPrepareActionMode(
        mode: ActionMode,
        menu: Menu
    ): Boolean = false

    override fun onActionItemClicked(
        mode: ActionMode,
        item: MenuItem
    ): Boolean {
        when (item.itemId) {
            R.id.confirm -> handleContextualAction()
            else -> return false
        }
        return true
    }

    fun shouldShowEmptyView(): Boolean = adapter!!.itemCount == 0

    fun setShowEmptyView(visible: Boolean) {
        emptyView!!.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private var observer: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            setShowEmptyView(shouldShowEmptyView())
        }
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        resetActionModeParameters()
        adapter?.clearSelection()
    }

    private fun handleContextualAction() {
        if (adapter?.selectedItems?.isEmpty() != false) {
            actionMode?.finish()
            return
        }
        when (actionModeType) {
            DELETE -> deleteItemsNotInUse(adapter!!.selectedItems)
            NONE -> throw IllegalStateException("ActionModeType not set Correctly")
        }
    }

    private fun resetActionModeParameters() {
        actionModeType = NONE
        actionMode = null
        adapter?.showCheckBoxes(false)
        adapter?.allowMultiSelection = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parent =
            inflater.inflate(R.layout.fragment_list_view, container, false)
        recyclerView = parent.findViewById(R.id.recycler_view)
        emptyView = parent.empty_view
        setHasOptionsMenu(true)
        return parent
    }

    override fun onActivityCreated(savedInstance: Bundle?) {
        super.onActivityCreated(savedInstance)
        initializeAdapter()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)?.supportActionBar?.setTitle(R.string.formula_editor_data)
        adapter?.apply {
            notifyDataSetChanged()
            registerAdapterDataObserver(observer)
        }
        setShowEmptyView(shouldShowEmptyView())

        BottomBar.showBottomBar(activity)
        BottomBar.hidePlayButton(activity)
    }

    override fun onPause() {
        super.onPause()
        adapter?.unregisterAdapterDataObserver(observer)
    }

    override fun onStop() {
        super.onStop()
        finishActionMode()
        BottomBar.hideBottomBar(activity)
    }

    @VisibleForTesting
    fun initializeAdapter() {
        arguments?.getSerializable(PARENT_SCRIPT_BRICK_BUNDLE_ARGUMENT).let { parentScriptBrick = it as ScriptBrick? }

        val currentProject =
            ProjectManager.getInstance().currentProject
        val currentSprite =
            ProjectManager.getInstance().currentSprite

        var userDefinedBrickInputs = listOf<UserDefinedBrickInput>()
        if (parentScriptBrick is UserDefinedReceiverBrick) {
            userDefinedBrickInputs = (parentScriptBrick as UserDefinedReceiverBrick).userDefinedBrick.userDefinedBrickInputs
        }

        val globalVars = currentProject.userVariables
        val localVars = currentSprite.userVariables
        val multiplayerVars = currentProject.multiplayerVariables
        val globalLists = currentProject.userLists
        val localLists = currentSprite.userLists
        adapter = DataListAdapter(
            userDefinedBrickInputs, multiplayerVars, globalVars, localVars, globalLists,
            localLists
        )
        emptyView?.setText(R.string.fragment_data_text_description)
        onAdapterReady()
    }

    private fun onAdapterReady() {
        recyclerView?.adapter = adapter
        adapter?.setSelectionListener(this)
        adapter?.setOnItemClickListener(this)
    }

    fun notifyDataSetChanged() {
        adapter?.notifyDataSetChanged()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        for (index in 0 until menu.size()) {
            menu.getItem(index).isVisible = false
        }
        menu.findItem(R.id.delete).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> startActionMode(DELETE)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun startActionMode(@ActionModeType type: Int) {
        if (adapter?.items?.isEmpty() != false) {
            ToastUtil.showError(requireActivity(), R.string.am_empty_list)
        } else {
            actionModeType = type
            actionMode = requireActivity().startActionMode(this)
        }
    }

    private fun finishActionMode() {
        adapter?.clearSelection()
        if (actionModeType != NONE) {
            actionMode?.finish()
        }
    }

    private fun deleteItems(selectedItems: List<UserData<*>>) {
        finishActionMode()
        for (item in selectedItems) {
            adapter?.remove(item)
        }
        ProjectManager.getInstance().currentProject.deselectElements(selectedItems)
        showSuccess(selectedItems.size)
    }

    @VisibleForTesting
    open fun showSuccess(size: Int?) {
        ToastUtil.showSuccess(
            activity, resources.getQuantityString(
                R.plurals.deleted_Items,
                size!!,
                size
            )
        )
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

    private fun renameItem(
        item: UserData<*>,
        name: String?
    ) {
        val previousName = item.name
        updateUserDataReferences(previousName, name, item)
        renameUserData(item, name ?: "")
        adapter?.updateDataSet()
        finishActionMode()
        if (item is UserVariable) {
            formulaEditorDataInterface?.onVariableRenamed(previousName, name)
        } else {
            formulaEditorDataInterface?.onListRenamed(previousName, name)
        }
    }

    private fun showEditDialog(selectedItems: List<UserData<*>>) {
        val item = selectedItems[0]
        val builder = TextInputDialog.Builder(requireContext())

        builder.setHint(getString(R.string.data_value))
            .setText(item.value.toString())
            .setPositiveButton(
                getString(R.string.save)
            ) { _: DialogInterface?, textInput: String? ->
                editItem(
                    item,
                    textInput
                )
            }
        builder.setTitle("Edit " + item.name)
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun editItem(
        item: UserData<*>,
        value: String?
    ) {
        updateUserVariableValue(value, item)
        adapter?.updateDataSet()
        finishActionMode()
    }

    override fun onSelectionChanged(selectedItemCnt: Int) {
        if (actionModeType == DELETE) {
            actionMode?.title = getString(R.string.am_delete) + " " + selectedItemCnt
        }
    }

    override fun onItemClick(item: UserData<*>) {
        if (actionModeType == NONE) {
            formulaEditorDataInterface?.onDataItemSelected(item)
            fragmentManager?.popBackStack()
        }
    }

    override fun onItemLongClick(
        item: UserData<*>,
        holder: CheckableVH
    ) {
        if (item is UserDefinedBrickInput) {
            return
        } else if (item is UserVariable) {
            val items =
                arrayOf<CharSequence>(
                    getString(R.string.delete), getString(R.string.rename),
                    getString(R.string.edit)
                )
            AlertDialog.Builder(requireActivity())
                .setItems(
                    items
                ) { dialog: DialogInterface, which: Int ->
                    when (which) {
                        0 -> deleteItemsNotInUse(
                            ArrayList(
                                listOf(item)
                            )
                        )
                        1 -> showRenameDialog(
                            ArrayList(
                                listOf(item)
                            )
                        )
                        2 -> showEditDialog(
                            ArrayList(
                                listOf(item)
                            )
                        )
                        else -> dialog.dismiss()
                    }
                }
                .show()
        } else {
            val items =
                arrayOf<CharSequence>(
                    getString(R.string.delete), getString(R.string.rename)
                )
            AlertDialog.Builder(requireActivity())
                .setItems(
                    items
                ) { dialog: DialogInterface, which: Int ->
                    when (which) {
                        0 -> deleteItemsNotInUse(
                            ArrayList(
                                listOf(item)
                            )
                        )
                        1 -> showRenameDialog(
                            ArrayList(
                                listOf(item)
                            )
                        )
                        2 -> showEditDialog(
                            ArrayList(
                                listOf(item)
                            )
                        )
                        else -> dialog.dismiss()
                    }
                }
                .show()
        }
    }

    @VisibleForTesting
    fun deleteItemsNotInUse(selectedItems: List<UserData<*>>) {
        val itemsToDelete: List<UserData<*>> = filterDeletableUserDataItems(selectedItems)

        if (itemsToDelete.isNotEmpty()) {
            deleteItems(itemsToDelete)
        }
    }

    private fun filterDeletableUserDataItems(selectedItems: List<UserData<*>>): List<UserData<*>> {
        var brickFlatList: List<Brick>
        val currentSprite: Sprite = projectManager.currentSprite
        val currentProject: Project = projectManager.currentProject
        val itemsToDelete: MutableList<UserData<*>> = ArrayList()
        val globalLists = currentProject.userLists
        val globalVariables = currentProject.userVariables

        for (item in selectedItems) {
            brickFlatList = if (globalVariables.contains(item) || globalLists.contains(item)) {
                getBrickList(currentProject.sceneList)
            } else {
                currentSprite.allBricks
            }
            if (!checkIfItemIsUsed(item, brickFlatList)) {
                itemsToDelete.add(item)
            }
        }
        return itemsToDelete
    }

    private fun checkIfItemIsUsed(item: UserData<*>, brickFlatList: List<Brick>): Boolean {
        for (brick in brickFlatList) {
            if (isItemInUse(item, brick)) {
                showInUseAlert(item)
                return true
            }
        }

        for (brick in brickFlatList) {
            if (isItemInDropDown(item, brick)) {
                showInDropDownDialog(item, brickFlatList)
                return true
            }
        }
        return false
    }

    private fun isItemInUse(item: UserData<*>, brick: Brick): Boolean {
        val itemName = item.name

        if (brick is FormulaBrick) {
            for (formula in brick.formulas) {
                val formulaTree: FormulaElement = formula.root

                if (item is UserVariable && formulaTree.containsVariable(itemName)) {
                    return true
                } else if (item is UserList && formulaTree.containsList(itemName)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isItemInDropDown(item: UserData<*>, brick: Brick): Boolean {
        val itemName = item.name

        if (brick is UserVariableBrickWithFormula && item is UserVariable && isItemNameAndVariableNameSame(itemName, brick)) {
            return true
        } else if (brick is UserListBrick && item is UserList && isItemNameAndListNameSame(itemName, brick)) {
            return true
        }
        return false
    }

    @VisibleForTesting
    fun deleteUserDataItemFromDropDown(item: UserData<*>, brickFlatList: List<Brick?>) {
        val itemName = item.name

        for (brick in brickFlatList) {
            if (brick is UserVariableBrickWithFormula && item is UserVariable && isItemNameAndVariableNameSame(itemName, brick)) {
                brick.userVariable = null
            } else if (brick is UserListBrick && item is UserList && isItemNameAndListNameSame(itemName, brick)) {
                brick.userList = null
            }
        }
        deleteItem(item)
    }

    private fun getBrickList(sceneList: List<Scene>): List<Brick> {
        val brickFlatList: List<Brick> = ArrayList()

        for (scene in sceneList) {
            for (sprite in scene.spriteList) {
                for (script in sprite.scriptList) {
                    script.addToFlatList(brickFlatList)
                }
            }
        }
        return brickFlatList
    }

    private fun isItemNameAndVariableNameSame(itemName: String, brick: Brick): Boolean =
        (brick as UserVariableBrickWithFormula).userVariable != null && itemName === brick.userVariable.name

    private fun isItemNameAndListNameSame(itemName: String, brick: Brick): Boolean =
        (brick as UserListBrick).userList != null && itemName === brick.userList.name

    private fun deleteItem(item: UserData<*>) {
        val items: MutableList<UserData<*>> = ArrayList()
        items.add(item)
        deleteItems(items)
    }

    @VisibleForTesting
    open fun showInUseAlert(item: UserData<*>) {
        val message = getString(R.string.deletion_alert_text_in_use_with_variable_name, item.name)

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.deletion_alert_warning)
            .setMessage(message)
            .setNeutralButton(R.string.ok, null)
            .setCancelable(true)
            .show()
    }

    @VisibleForTesting
    open fun showInDropDownDialog(item: UserData<*>, brickFlatList: List<Brick>) {
        val message = getString(R.string.deletion_alert_text_drop_down_with_variable_name, item.name)

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.deletion_alert_title)
            .setMessage(message)
            .setPositiveButton(
                R.string.deletion_alert_yes
            ) { _: DialogInterface?, _: Int ->
                deleteUserDataItemFromDropDown(item, brickFlatList)
            }
            .setNegativeButton(R.string.no, null)
            .setCancelable(true)
            .show()
    }

    interface FormulaEditorDataInterface {
        fun onDataItemSelected(item: UserData<*>?)
        fun onVariableRenamed(
            previousName: String?,
            newName: String?
        )

        fun onListRenamed(previousName: String?, newName: String?)
    }

    companion object {
        @JvmField
        val TAG: String = DataListFragment::class.java.simpleName
        const val PARENT_SCRIPT_BRICK_BUNDLE_ARGUMENT: String = "parent_script_brick"
        private const val NONE = 0
        private const val DELETE = 1

        @JvmStatic
        fun updateUserDataReferences(oldName: String?, newName: String?, item: UserData<*>?) {
            ProjectManager.getInstance().currentProject
                .updateUserDataReferences(oldName, newName, item)
        }

        @JvmStatic
        fun updateUserVariableValue(value: String?, item: UserData<*>) {
            item.value = value
        }
    }
}
