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
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.PluralsRes
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.content.GroupSprite
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.merge.ImportLocalObjectActivity
import org.catrobat.catroid.merge.ImportLocalObjectActivity.Companion.REQUEST_PROJECT
import org.catrobat.catroid.merge.ImportUtils
import org.catrobat.catroid.merge.ImportVariablesManager
import org.catrobat.catroid.ui.BottomBar.hideBottomBar
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.UiUtils
import org.catrobat.catroid.ui.WebViewActivity
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.recyclerview.adapter.MultiViewSpriteAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.SpriteAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperAdapterInterface
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperCallback
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

    public override fun shouldShowEmptyView() =
        actionModeType != IMPORT_LOCAL && adapter.itemCount == 1

    override fun onActivityCreated(savedInstance: Bundle?) {
        super.onActivityCreated(savedInstance)
        if (ImportLocalObjectActivity.hasExtraTAG(activity) == true) {
            prepareActionMode(IMPORT_LOCAL)
            ImportLocalObjectActivity.spritesToImport = null
        }
    }

    override fun onResume() {
        initializeAdapter()
        super.onResume()
        if (ImportLocalObjectActivity.hasExtraTAG(activity) == false) {
            SnackbarUtil.showHintSnackbar(requireActivity(), R.string.hint_objects)
            val currentProject = projectManager.currentProject
            val title: String = if (!currentProject.hasMultipleScenes()) {
                currentProject.name
            } else {
                val currentScene = projectManager.currentlyEditedScene
                currentProject.name + ": " + currentScene.name
            }
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
                .putBoolean(SharedPreferenceKeys.INDEXING_VARIABLE_PREFERENCE_KEY, false).apply()
            (requireActivity() as AppCompatActivity).supportActionBar?.title = title
        }
    }

    override fun onAdapterReady() {
        super.onAdapterReady()
        if (ImportLocalObjectActivity.hasExtraTAG(activity) == false) {
            val callback: ItemTouchHelper.Callback = MultiViewTouchHelperCallback(adapter)
            touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(recyclerView)
        } else {
            adapter.showRipples = false
            adapter.showSettings = false
            adapter.showCheckBoxes = true
        }
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
        adapter = if (ImportLocalObjectActivity.hasExtraTAG(activity) == true) {
            SpriteAdapter(ImportLocalObjectActivity.sceneToImportFrom?.spriteList)
        } else {
            emptyView.setText(R.string.fragment_sprite_text_description)
            MultiViewSpriteAdapter(projectManager.currentlyEditedScene.spriteList)
        }

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
        if (resultCode != RESULT_OK) {
            return
        }
        val uri: Uri?
        when (requestCode) {
            IMPORT_MEDIA_OBJECT -> {
                uri = data?.data ?: return
                addImportedSpriteOrObject(uri, IMPORT_MEDIA_OBJECT, null)
            }
            IMPORT_LOCAL_OBJECT -> {
                val extras = data?.extras ?: return
                uri = Uri.fromFile(extras.get(REQUEST_PROJECT) as File)
                addImportedSpriteOrObject(uri, IMPORT_LOCAL_OBJECT, extras)
            }
        }
    }

    @VisibleForTesting(otherwise = Context.MODE_PRIVATE)
    fun addImportedSpriteOrObject(uri: Uri, requestCode: Int, extras: Bundle?) {
        ImportUtils(requireContext()).processImportData(
            uri,
            requestCode,
            extras
        )?.forEach {
            currentSprite?.mergeSprites(it.sprite, projectManager.currentlyEditedScene)
            ImportVariablesManager.importProjectVariables(it.sourceProject)
        }
    }

    private fun addFromLibrary(item: Sprite?) {
        currentSprite = item
        val intent = Intent(requireContext(), WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, FlavoredConstants.LIBRARY_OBJECT_URL)
        startActivityForResult(intent, IMPORT_MEDIA_OBJECT)
    }

    private fun addFromLocalProject(item: Sprite?) {
        currentSprite = item
        val intent = Intent(requireContext(), ImportLocalObjectActivity::class.java)
        intent.putExtra(ImportLocalObjectActivity.TAG, REQUEST_PROJECT)
        startActivityForResult(intent, IMPORT_LOCAL_OBJECT)
    }

    override fun getRenameDialogTitle() = R.string.rename_sprite_dialog

    override fun getRenameDialogHint() = R.string.sprite_name_label

    override fun renameItem(item: Sprite?, name: String) {
        item?.rename(name)
        finishActionMode()
    }

    override fun onItemClick(item: Sprite?) {
        if (item is GroupSprite) {
            item.isCollapsed = !item.isCollapsed
            adapter.notifyDataSetChanged()
        } else {
            if (actionModeType == RENAME) {
                super.onItemClick(item)
                return
            }
            if (actionModeType == NONE) {
                projectManager.currentSprite = item
                val intent = Intent(requireContext(), SpriteActivity::class.java)
                intent.putExtra(
                    SpriteActivity.EXTRA_FRAGMENT_POSITION,
                    SpriteActivity.FRAGMENT_SCRIPTS
                )
                startActivity(intent)
            }
        }
    }

    override fun onItemLongClick(item: Sprite?, holder: CheckableViewHolder) {
        super.onItemLongClick(item, holder)
    }

    override fun onSettingsClick(item: Sprite?, view: View) {
        val itemList = mutableListOf<Sprite?>()
        itemList.add(item)
        val hiddenMenuOptionIds = mutableListOf(
            R.id.new_group,
            R.id.project_options,
            R.id.new_scene,
            R.id.show_details,
            R.id.edit
        )
        if (item is GroupSprite) {
            hiddenMenuOptionIds.add(R.id.backpack)
            hiddenMenuOptionIds.add(R.id.copy)
            hiddenMenuOptionIds.add(R.id.from_library)
            hiddenMenuOptionIds.add(R.id.from_local)
        }
        val popupMenu = UiUtils.createSettingsPopUpMenu(
            view, requireContext(), R.menu.menu_project_activity, hiddenMenuOptionIds.toIntArray()
        )
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.backpack -> packItems(itemList)
                R.id.copy -> copyItems(itemList)
                R.id.delete -> deleteItems(itemList)
                R.id.rename -> showRenameDialog(item)
                R.id.from_library -> addFromLibrary(item)
                R.id.from_local -> addFromLocalProject(item)
                else -> {
                }
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
        const val IMPORT_MEDIA_OBJECT = 0
        const val IMPORT_LOCAL_OBJECT = 1
    }

    override fun importItems(selectedItems: MutableList<Sprite?>?) {
        if (selectedItems != null) {
            ImportLocalObjectActivity.spritesToImport =
                selectedItems.map { it?.name } as ArrayList<String>
            (activity as ImportLocalObjectActivity).finish()
        }
    }

    override fun onImport(menu: Menu?, mode: ActionMode?) {
        super.onImport(menu, mode)
        mode!!.setTitle(R.string.import_object)
        adapter.selectionMode = RVAdapter.MULTIPLE
        menu!!.findItem(R.id.confirm).isVisible = true
        menu.findItem(R.id.overflow).isVisible = true
        menu.findItem(R.id.toggle_selection).isVisible = true
    }

    override fun onRename(menu: Menu?) {
        adapter.selectionMode = RVAdapter.SINGLE
        adapter.showSettings = false
        adapter.showRipples = false
        menu!!.findItem(R.id.confirm).isVisible = false
        menu.findItem(R.id.overflow).isVisible = false
        menu.findItem(R.id.toggle_selection).isVisible = false
        (adapter as MultiViewSpriteAdapter).setBackgroundVisible(View.GONE)
        if (this.isSingleVisibleSprite) {
            showRenameDialog(adapter.items[1])
        }
        notifyDataSetChanged()
    }

    override fun startActionMode(type: Int) {
        if (type == IMPORT_LOCAL && adapter.items.isNotEmpty() || adapter.items.size > 1) {
            actionModeType = type
            actionMode = requireActivity().startActionMode(this)
            hideBottomBar(activity)
        } else {
            ToastUtil.showError(activity, R.string.am_empty_list)
            resetActionModeParameters()
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        if (actionModeType != IMPORT_LOCAL) {
            super.onDestroyActionMode(mode)
            return
        }

        if (activity is ImportLocalObjectActivity && ImportLocalObjectActivity.spritesToImport ==
            null
        ) {
            (activity as ImportLocalObjectActivity).onBackPressed()
        }
    }

    override fun showRenameDialog(selectedItem: Sprite?) {
        val builder = TextInputDialog.Builder(requireContext())
        builder.setHint(getString(renameDialogHint))
            .setText(selectedItem!!.name)
            .setTextWatcher(DuplicateInputTextWatcher(adapter.items as List<Nameable>?))
            .setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, textInput: String? ->
                renameItem(
                    selectedItem,
                    textInput!!
                )
            }

        builder.setTitle(renameDialogTitle)
            .setNegativeButton(R.string.cancel, null)
            .setOnDismissListener {
                (adapter as MultiViewSpriteAdapter).setBackgroundVisible(View.VISIBLE)
                if (actionMode != null) {
                    finishActionMode()
                }
            }
            .show()
    }
}
