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

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.PluralsRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.TMP_IMAGE_FILE_NAME
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.content.GroupSprite
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.merge.ImportProjectHelper
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.ui.ProjectListActivity.Companion.IMPORT_LOCAL_INTENT
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.UiUtils
import org.catrobat.catroid.ui.WebViewActivity
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.recyclerview.adapter.MultiViewSpriteAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperAdapterInterface
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperCallback
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableViewHolder
import org.catrobat.catroid.utils.SnackbarUtil
import org.catrobat.catroid.utils.ToastUtil
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException

@SuppressLint("NotifyDataSetChanged")
class SpriteListFragment : RecyclerViewFragment<Sprite?>() {
    private val spriteController = SpriteController()
    private val projectManager: ProjectManager by inject()

    private var currentSprite: Sprite? = null

    internal inner class MultiViewTouchHelperCallback(adapterInterface: TouchHelperAdapterInterface?) :
        TouchHelperCallback(adapterInterface) {

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            when (actionState) {
                ItemTouchHelper.ACTION_STATE_IDLE -> {
                    val items = adapter.items
                    for (sprite in items) {
                        if (sprite is GroupSprite) {
                            continue
                        }
                        if (sprite?.toBeConverted() == true) {
                            val convertedSprite = spriteController.convert(sprite)
                            items[items.indexOf(sprite)] = convertedSprite
                        }
                    }
                    for (item in items) {
                        if (item is GroupSprite) {
                            item.isCollapsed = item.isCollapsed
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    public override fun shouldShowEmptyView() = adapter.itemCount == 1

    override fun onResume() {
        initializeAdapter()
        super.onResume()
        SnackbarUtil.showHintSnackbar(requireActivity(), R.string.hint_objects)
        val currentProject = projectManager.currentProject
        val title: String = if (currentProject.sceneList.size < 2) {
            currentProject.name
        } else {
            val currentScene = projectManager.currentlyEditedScene
            currentProject.name + ": " + currentScene.name
        }
        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
            .putBoolean(SharedPreferenceKeys.INDEXING_VARIABLE_PREFERENCE_KEY, false).apply()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    override fun onAdapterReady() {
        super.onAdapterReady()
        val callback: ItemTouchHelper.Callback = MultiViewTouchHelperCallback(adapter)
        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.new_group).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_group -> showNewGroupDialog()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun showNewGroupDialog() {
        val builder = TextInputDialog.Builder(requireContext())
        val uniqueNameProvider = UniqueNameProvider()
        builder.setHint(getString(R.string.sprite_group_name_label))
            .setTextWatcher(DuplicateInputTextWatcher<Sprite>(adapter.items))
            .setText(
                uniqueNameProvider.getUniqueNameInNameables(
                    getString(R.string.default_group_name),
                    adapter.items
                )
            )
            .setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, textInput: String? ->
                adapter.add(GroupSprite(textInput))
            }
        builder.setTitle(R.string.new_group)
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun initializeAdapter() {
        sharedPreferenceDetailsKey = SharedPreferenceKeys.SHOW_DETAILS_SPRITES_PREFERENCE_KEY
        val items = projectManager.currentlyEditedScene.spriteList
        adapter = MultiViewSpriteAdapter(items)
        emptyView.setText(R.string.fragment_sprite_text_description)
        onAdapterReady()
    }

    override fun packItems(selectedItems: List<Sprite?>) {
        setShowProgressBar(true)
        var packedItemCnt = 0
        for (item in selectedItems) {
            try {
                BackpackListManager.getInstance().sprites.add(spriteController.pack(item))
                BackpackListManager.getInstance().saveBackpack()
                packedItemCnt++
            } catch (e: IOException) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
        }
        if (packedItemCnt > 0) {
            ToastUtil.showSuccess(
                requireContext(), resources.getQuantityString(
                    R.plurals.packed_sprites,
                    packedItemCnt,
                    packedItemCnt
                )
            )
            switchToBackpack()
        }
        finishActionMode()
    }

    override fun isBackpackEmpty() = BackpackListManager.getInstance().sprites.isEmpty()

    override fun switchToBackpack() {
        val intent = Intent(requireContext(), BackpackActivity::class.java)
        intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_SPRITES)
        startActivity(intent)
    }

    override fun copyItems(selectedItems: List<Sprite?>) {
        setShowProgressBar(true)
        val currentProject = projectManager.currentProject
        val currentScene = projectManager.currentlyEditedScene
        var copiedItemCnt = 0
        for (item in selectedItems) {
            try {
                adapter.add(spriteController.copy(item, currentProject, currentScene))
                copiedItemCnt++
            } catch (e: IOException) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
        }
        if (copiedItemCnt > 0) {
            ToastUtil.showSuccess(
                requireContext(), resources.getQuantityString(
                    R.plurals.copied_sprites,
                    copiedItemCnt,
                    copiedItemCnt
                )
            )
        }
        finishActionMode()
    }

    @PluralsRes
    override fun getDeleteAlertTitleId() = R.plurals.delete_sprites

    override fun deleteItems(selectedItems: List<Sprite?>) {
        setShowProgressBar(true)
        for (item in selectedItems) {
            if (item is GroupSprite) {
                for (sprite in item.groupItems) {
                    sprite.setConvertToSprite(true)
                    val convertedSprite = spriteController.convert(sprite)
                    adapter.items[adapter.items.indexOf(sprite)] = convertedSprite
                }
                adapter.notifyDataSetChanged()
            }
            spriteController.delete(item)
            adapter.remove(item)
        }
        ToastUtil.showSuccess(
            requireContext(), resources.getQuantityString(
                R.plurals.deleted_sprites,
                selectedItems.size,
                selectedItems.size
            )
        )
        finishActionMode()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMPORT_OBJECT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri = if (data?.hasExtra(IMPORT_LOCAL_INTENT) == true) {
                Uri.fromFile(File(data.getStringExtra(IMPORT_LOCAL_INTENT)))
            } else {
                Uri.fromFile(File(data?.getStringExtra(WebViewActivity.MEDIA_FILE_PATH)))
            }

            val currentScene = projectManager.currentlyEditedScene
            val resolvedName: String
            val resolvedFileName =
                StorageOperations.resolveFileName(requireActivity().contentResolver, uri)
            val lookFileName: String
            val useDefaultSpriteName = resolvedFileName == null ||
                StorageOperations.getSanitizedFileName(resolvedFileName) == TMP_IMAGE_FILE_NAME
            if (useDefaultSpriteName) {
                resolvedName = getString(R.string.default_sprite_name)
                lookFileName = resolvedName + Constants.CATROBAT_EXTENSION
            } else {
                lookFileName = resolvedFileName
            }
            val importProjectHelper = ImportProjectHelper(
                lookFileName,
                currentScene,
                requireActivity()
            )
            if (!importProjectHelper.checkForConflicts()) {
                return
            }
            if (currentSprite != null) {
                importProjectHelper.addObjectDataToNewSprite(currentSprite)
            } else {
                importProjectHelper.rejectImportDialog(null)
            }
        }
    }

    private fun addFromLibrary(selectedItem: Sprite?) {
        currentSprite = selectedItem
        val intent = Intent(requireContext(), WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, FlavoredConstants.LIBRARY_OBJECT_URL)
        startActivityForResult(intent, IMPORT_OBJECT_REQUEST_CODE)
    }

    private fun addFromLocalProject(item: Sprite?) {
        currentSprite = item
        val intent = Intent(requireContext(), ProjectListActivity::class.java)
        intent.putExtra(
            IMPORT_LOCAL_INTENT,
            getString(R.string.import_sprite_from_project_launcher))
        startActivityForResult(intent, IMPORT_OBJECT_REQUEST_CODE)
    }

    override fun getRenameDialogTitle() = R.string.rename_sprite_dialog

    override fun getRenameDialogHint() = R.string.sprite_name_label

    override fun renameItem(item: Sprite?, name: String) {
        item?.rename(name)
        finishActionMode()
    }

    override fun onItemClick(item: Sprite?, selectionManager: MultiSelectionManager?) {
        if (item is GroupSprite) {
            item.isCollapsed = !item.isCollapsed
            adapter.notifyDataSetChanged()
        } else {
            when (actionModeType) {
                RENAME -> {
                    super.onItemClick(item, null)
                    return
                }
                NONE -> {
                    projectManager.currentSprite = item
                    val intent = Intent(requireContext(), SpriteActivity::class.java)
                    intent.putExtra(
                        SpriteActivity.EXTRA_FRAGMENT_POSITION,
                        SpriteActivity.FRAGMENT_SCRIPTS
                    )
                    startActivity(intent)
                }
                else -> super.onItemClick(item, selectionManager)
            }
        }
    }

    override fun onItemLongClick(item: Sprite?, holder: CheckableViewHolder) {
        super.onItemLongClick(item, holder)
    }

    override fun onSettingsClick(item: Sprite?, view: View) {
        val itemList = mutableListOf<Sprite?>()
        itemList.add(item)
        val hiddenMenuOptionIds = mutableListOf<Int>(
            R.id.new_group, R.id.project_options, R.id.new_scene, R.id.show_details, R.id.edit
        )
        if (item is GroupSprite) {
            hiddenMenuOptionIds.add(R.id.backpack)
            hiddenMenuOptionIds.add(R.id.copy)
            hiddenMenuOptionIds.add(R.id.from_library)
            hiddenMenuOptionIds.add(R.id.from_local)
        }
        val popupMenu = UiUtils.createSettingsPopUpMenu(
            view, requireContext(), R.menu
                .menu_project_activity, hiddenMenuOptionIds.toIntArray()
        )
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.backpack -> packItems(itemList)
                R.id.copy -> copyItems(itemList)
                R.id.delete -> deleteItems(itemList)
                R.id.rename -> showRenameDialog(item)
                R.id.from_library -> addFromLibrary(item)
                R.id.from_local -> addFromLocalProject(item)
                else -> {}
            }
            true
        }
        if (item !is GroupSprite) {
            popupMenu.menu.findItem(R.id.backpack).setTitle(R.string.pack)
        }
        popupMenu.show()
    }

    val isSingleVisibleSprite: Boolean
        get() = adapter.items.size == 2 && adapter.items[1] !is GroupSprite

    companion object {
        val TAG: String = SpriteListFragment::class.java.simpleName
        const val IMPORT_OBJECT_REQUEST_CODE = 0
    }
}
