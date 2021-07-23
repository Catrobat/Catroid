/*
 * Catroid: An on-device visual programming system for Android devices
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

import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.PluralsRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.content.GroupSprite
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.recyclerview.adapter.MultiViewSpriteAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperAdapterInterface
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperCallback
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH
import org.catrobat.catroid.utils.SnackbarUtil
import org.catrobat.catroid.utils.ToastUtil
import java.io.IOException
import java.util.ArrayList

class SpriteListFragment : RecyclerViewFragment<Sprite?>() {
    private val spriteController = SpriteController()

    internal inner class MultiViewTouchHelperCallback(adapterInterface: TouchHelperAdapterInterface?) :
        TouchHelperCallback(adapterInterface) {
        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            when (actionState) {
                ItemTouchHelper.ACTION_STATE_IDLE -> {
                    val items: MutableList<Sprite> = adapter.getItems()
                    for (sprite in items) {
                        if (sprite is GroupSprite) {
                            continue
                        }
                        if (sprite.toBeConverted()) {
                            val convertedSprite = spriteController.convert(sprite)
                            items[items.indexOf(sprite)] = convertedSprite
                        }
                    }
                    for (item in items) {
                        if (item is GroupSprite) {
                            val groupSprite = item
                            groupSprite.isCollapsed = groupSprite.isCollapsed
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    public override fun shouldShowEmptyView(): Boolean {
        return adapter.itemCount == 1
    }

    override fun onResume() {
        initializeAdapter()
        super.onResume()
        SnackbarUtil.showHintSnackbar(activity, R.string.hint_objects)
        val currentProject = ProjectManager.getInstance().currentProject
        val title: String
        title = if (currentProject.sceneList.size < 2) {
            currentProject.name
        } else {
            val currentScene = ProjectManager.getInstance().currentlyEditedScene
            currentProject.name + ": " + currentScene.name
        }
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = title
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
        val builder = TextInputDialog.Builder(context!!)
        val groups: List<Sprite> = adapter.getItems()
        val groupNames: MutableList<String> = ArrayList()
        for (sprite in groups) {
            groupNames.add(sprite.name)
        }
        val uniqueNameProvider = UniqueNameProvider()
        builder.setHint(getString(R.string.sprite_group_name_label))
            .setTextWatcher(DuplicateInputTextWatcher<Sprite>(adapter.items))
            .setText(
                uniqueNameProvider.getUniqueName(
                    getString(R.string.default_group_name),
                    groupNames
                )
            )
            .setPositiveButton(
                getString(R.string.ok),
                TextInputDialog.OnClickListener { dialog, textInput ->
                    adapter.add(GroupSprite(textInput))
                })
        builder.setTitle(R.string.new_group)
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun initializeAdapter() {
        sharedPreferenceDetailsKey = SharedPreferenceKeys.SHOW_DETAILS_SPRITES_PREFERENCE_KEY
        val items = ProjectManager.getInstance().currentlyEditedScene.spriteList
        adapter = MultiViewSpriteAdapter(items)
        emptyView.setText(R.string.fragment_sprite_text_description)
        onAdapterReady()
    }

    protected override fun packItems(selectedItems: List<Sprite>) {
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
                activity, resources.getQuantityString(
                    R.plurals.packed_sprites,
                    packedItemCnt,
                    packedItemCnt
                )
            )
            switchToBackpack()
        }
        finishActionMode()
    }

    override fun isBackpackEmpty(): Boolean {
        return BackpackListManager.getInstance().sprites.isEmpty()
    }

    override fun switchToBackpack() {
        val intent = Intent(activity, BackpackActivity::class.java)
        intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_SPRITES)
        startActivity(intent)
    }

    protected override fun copyItems(selectedItems: List<Sprite>) {
        setShowProgressBar(true)
        val currentProject = ProjectManager.getInstance().currentProject
        val currentScene = ProjectManager.getInstance().currentlyEditedScene
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
                activity, resources.getQuantityString(
                    R.plurals.copied_sprites,
                    copiedItemCnt,
                    copiedItemCnt
                )
            )
        }
        finishActionMode()
    }

    @PluralsRes
    override fun getDeleteAlertTitleId(): Int {
        return R.plurals.delete_sprites
    }

    protected override fun deleteItems(selectedItems: List<Sprite>) {
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
            activity, resources.getQuantityString(
                R.plurals.deleted_sprites,
                selectedItems.size,
                selectedItems.size
            )
        )
        finishActionMode()
    }

    override fun getRenameDialogTitle(): Int {
        return R.string.rename_sprite_dialog
    }

    override fun getRenameDialogHint(): Int {
        return R.string.sprite_name_label
    }

    override fun renameItem(item: Sprite, name: String) {
        item.rename(name)
        finishActionMode()
    }

    override fun onItemClick(item: Sprite) {
        if (item is GroupSprite) {
            val groupSprite = item
            groupSprite.isCollapsed = !groupSprite.isCollapsed
            adapter.notifyDataSetChanged()
        } else {
            if (actionModeType == RENAME) {
                super.onItemClick(item)
                return
            }
            if (actionModeType == NONE) {
                ProjectManager.getInstance().currentSprite = item
                val intent = Intent(activity, SpriteActivity::class.java)
                intent.putExtra(
                    SpriteActivity.EXTRA_FRAGMENT_POSITION,
                    SpriteActivity.FRAGMENT_SCRIPTS
                )
                startActivity(intent)
            }
        }
    }

    override fun onItemLongClick(item: Sprite, holder: CheckableVH) {
        if (item is GroupSprite) {
            val items = arrayOf<CharSequence>(
                getString(R.string.delete),
                getString(R.string.rename)
            )
            AlertDialog.Builder(context!!)
                .setTitle(item.getName())
                .setItems(items) { dialog, which ->
                    when (which) {
                        0 -> showDeleteAlert(ArrayList<Sprite>(listOf(item)))
                        1 -> showRenameDialog(item)
                        else -> dialog.dismiss()
                    }
                }
                .show()
        } else {
            super.onItemLongClick(item, holder)
        }
    }

    val isSingleVisibleSprite: Boolean
        get() = adapter.items.size == 2 && adapter.items[1] !is GroupSprite

    companion object {
        @JvmField
		val TAG = SpriteListFragment::class.java.simpleName
    }
}