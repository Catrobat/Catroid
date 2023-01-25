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

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.annotation.PluralsRes
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_LOOKS_PREFERENCE_KEY
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.UiUtils
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.recyclerview.adapter.LookAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity
import org.catrobat.catroid.ui.recyclerview.controller.LookController
import org.catrobat.catroid.utils.SnackbarUtil
import org.catrobat.catroid.utils.ToastUtil
import org.koin.android.ext.android.inject
import java.io.IOException
import java.util.ArrayList

class LookListFragment : RecyclerViewFragment<LookData?>() {
    private val lookController = LookController()
    private var currentItem: LookData? = null

    private val projectManager: ProjectManager by inject()

    companion object {
        @JvmField
        val TAG = LookListFragment::class.java.simpleName
    }

    override fun initializeAdapter() {
        sharedPreferenceDetailsKey = SHOW_DETAILS_LOOKS_PREFERENCE_KEY
        val items = projectManager.currentSprite.lookList
        adapter = LookAdapter(items)
        emptyView.setText(R.string.fragment_look_text_description)
        onAdapterReady()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.catblocks_reorder_scripts).isVisible = false
        menu.findItem(R.id.catblocks).isVisible = false
        menu.findItem(R.id.find).isVisible = false
    }

    override fun onResume() {
        super.onResume()
        SnackbarUtil.showHintSnackbar(requireActivity(), R.string.hint_looks)
    }

    override fun packItems(selectedItems: List<LookData?>) {
        setShowProgressBar(true)
        var packedItemCnt = 0
        for (item in selectedItems) {
            try {
                BackpackListManager.getInstance().backpackedLooks.add(lookController.pack(item))
                BackpackListManager.getInstance().saveBackpack()
                packedItemCnt++
            } catch (e: IOException) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
        }
        if (packedItemCnt > 0) {
            ToastUtil.showSuccess(
                requireContext(), resources.getQuantityString(
                    R.plurals.packed_looks,
                    packedItemCnt,
                    packedItemCnt
                )
            )
            switchToBackpack()
        }
        finishActionMode()
    }

    override fun isBackpackEmpty(): Boolean =
        BackpackListManager.getInstance().backpackedLooks.isEmpty()

    override fun switchToBackpack() {
        val intent = Intent(requireContext(), BackpackActivity::class.java)
        intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_LOOKS)
        startActivity(intent)
    }

    override fun copyItems(selectedItems: List<LookData?>) {
        setShowProgressBar(true)
        var copiedItemCnt = 0
        val currentScene = projectManager.currentlyEditedScene
        val currentSprite = projectManager.currentSprite

        for (item in selectedItems) {
            try {
                adapter.add(lookController.copy(item, currentScene, currentSprite))
                copiedItemCnt++
            } catch (e: IOException) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
        }
        if (copiedItemCnt > 0) {
            ToastUtil.showSuccess(
                requireContext(), resources.getQuantityString(
                    R.plurals.copied_looks,
                    copiedItemCnt,
                    copiedItemCnt
                )
            )
        }
        finishActionMode()
    }

    private fun disposeItem() {
        if (Constants.TMP_LOOK_FILE.exists()) {
            Constants.TMP_LOOK_FILE.delete()
            currentItem = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeItem()
        val activity: Activity = requireActivity()
        if (activity is SpriteActivity) {
            activity.setUndoMenuItemVisibility(false)
            activity.checkForChange()
            activity.showUndo(false)
        }
    }

    @PluralsRes
    override fun getDeleteAlertTitleId(): Int = R.plurals.delete_looks

    override fun deleteItems(selectedItems: List<LookData?>) {
        setShowProgressBar(true)
        for (item in selectedItems) {
            try {
                lookController.delete(item)
            } catch (e: IOException) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
            adapter.remove(item)
        }
        ToastUtil.showSuccess(
            requireContext(), resources.getQuantityString(
                R.plurals.deleted_looks,
                selectedItems.size,
                selectedItems.size
            )
        )
        finishActionMode()
    }

    override fun getRenameDialogTitle(): Int = R.string.rename_look_dialog

    override fun getRenameDialogHint(): Int = R.string.look_name_label

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SpriteActivity.EDIT_LOOK && resultCode == Activity.RESULT_OK) {
            val activity: Activity = requireActivity()
            if (activity is SpriteActivity) {
                activity.setUndoMenuItemVisibility(true)
            }
        }
    }

    fun undo(): Boolean {
        currentItem?.let {
            try {
                StorageOperations.copyFile(Constants.TMP_LOOK_FILE, it.file)
            } catch (e: IOException) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
            it.invalidateThumbnailBitmap()
            adapter.notifyDataSetChanged()
            disposeItem()
            return true
        }
        return false
    }

    fun deleteItem(lookData: LookData?) {
        deleteItems(listOf(lookData))
    }

    override fun onItemClick(item: LookData?, selectionManager: MultiSelectionManager) {
        if (actionModeType == RENAME) {
            super.onItemClick(item, null)
            return
        }
        if (actionModeType != NONE) {
            super.onItemClick(item, selectionManager)
            return
        }

        currentItem = item
        item?.invalidateThumbnailBitmap()
        item?.clearCollisionInformation()
        try {
            StorageOperations.copyFile(currentItem?.file, Constants.TMP_LOOK_FILE)
        } catch (e: IOException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
        val intent = Intent("android.intent.action.MAIN")
        intent.component = ComponentName(requireActivity(), Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME)
        val bundle = Bundle()
        bundle.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, item?.file?.absolutePath)
        intent.putExtras(bundle)
        intent.addCategory("android.intent.category.LAUNCHER")
        startActivityForResult(intent, SpriteActivity.EDIT_LOOK)
    }

    override fun onSettingsClick(item: LookData?, view: View?) {
        val itemList: MutableList<LookData?> = ArrayList()
        itemList.add(item)
        val hiddenOptionMenuIds = intArrayOf(
            R.id.new_group,
            R.id.new_scene,
            R.id.show_details,
            R.id.project_options,
            R.id.edit,
            R.id.from_local,
            R.id.from_library
        )
        val popupMenu = UiUtils.createSettingsPopUpMenu(view, requireContext(), R.menu
            .menu_project_activity, hiddenOptionMenuIds)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.backpack -> packItems(itemList)
                R.id.copy -> copyItems(itemList)
                R.id.rename -> showRenameDialog(item)
                R.id.delete -> deleteItems(itemList)
                else -> {
                }
            }
            true
        }
        popupMenu.menu.findItem(R.id.backpack).setTitle(R.string.pack)
        popupMenu.show()
    }
}
