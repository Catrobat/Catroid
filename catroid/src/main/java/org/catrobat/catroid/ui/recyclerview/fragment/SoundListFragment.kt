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
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.annotation.PluralsRes
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.pocketmusic.PocketMusicActivity
import org.catrobat.catroid.ui.UiUtils
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.recyclerview.adapter.SoundAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity
import org.catrobat.catroid.ui.recyclerview.controller.SoundController
import org.catrobat.catroid.utils.SnackbarUtil
import org.catrobat.catroid.utils.ToastUtil
import org.koin.android.ext.android.inject
import java.io.IOException

class SoundListFragment : RecyclerViewFragment<SoundInfo?>() {

    private val soundController = SoundController()
    private val projectManager: ProjectManager by inject()

    companion object {
        @JvmField
        val TAG = SoundListFragment::class.java.simpleName
    }

    override fun initializeAdapter() {
        sharedPreferenceDetailsKey = SharedPreferenceKeys.SHOW_DETAILS_SOUNDS_PREFERENCE_KEY
        val items = projectManager.currentSprite.soundList
        adapter = SoundAdapter(items)
        emptyView.setText(R.string.fragment_sound_text_description)
        onAdapterReady()
    }

    override fun onResume() {
        super.onResume()
        SnackbarUtil.showHintSnackbar(requireActivity(), R.string.hint_sounds)
    }

    override fun onPause() {
        super.onPause()
        adapter.stopSound()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        menu.findItem(R.id.catblocks_reorder_scripts).isVisible = false
        menu.findItem(R.id.catblocks).isVisible = false
        menu.findItem(R.id.find).isVisible = false
    }

    override fun packItems(selectedItems: List<SoundInfo?>) {
        setShowProgressBar(true)
        var packedItemCnt = 0

        for (item in selectedItems) {
            try {
                BackpackListManager.getInstance().backpackedSounds.add(soundController.pack(item))
                BackpackListManager.getInstance().saveBackpack()
                packedItemCnt++
            } catch (e: IOException) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
        }
        if (packedItemCnt > 0) {
            ToastUtil.showSuccess(
                requireContext(), resources.getQuantityString(
                    R.plurals.packed_sounds,
                    packedItemCnt,
                    packedItemCnt
                )
            )
            switchToBackpack()
        }
        finishActionMode()
    }

    override fun isBackpackEmpty(): Boolean =
        BackpackListManager.getInstance().backpackedSounds.isEmpty()

    override fun switchToBackpack() {
        val intent = Intent(requireContext(), BackpackActivity::class.java)
        intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_SOUNDS)
        startActivity(intent)
    }

    override fun copyItems(selectedItems: List<SoundInfo?>) {
        setShowProgressBar(true)
        var copiedItemCnt = 0

        val currentScene = projectManager.currentlyEditedScene
        val currentSprite = projectManager.currentSprite
        for (item in selectedItems) {
            try {
                adapter.add(soundController.copy(item, currentScene, currentSprite))
                copiedItemCnt++
            } catch (e: IOException) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
        }
        if (copiedItemCnt > 0) {
            ToastUtil.showSuccess(
                requireContext(), resources.getQuantityString(
                    R.plurals.copied_sounds,
                    copiedItemCnt,
                    copiedItemCnt
                )
            )
        }
        finishActionMode()
    }

    @PluralsRes
    override fun getDeleteAlertTitleId(): Int = R.plurals.delete_sounds

    override fun deleteItems(selectedItems: List<SoundInfo?>) {
        setShowProgressBar(true)

        for (item in selectedItems) {
            try {
                soundController.delete(item)
            } catch (e: IOException) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
            adapter.remove(item)
        }

        ToastUtil.showSuccess(
            requireContext(), resources.getQuantityString(
                R.plurals.deleted_sounds,
                selectedItems.size,
                selectedItems.size
            )
        )
        finishActionMode()
    }

    override fun getRenameDialogTitle(): Int = R.string.rename_sound_dialog

    override fun getRenameDialogHint(): Int = R.string.sound_name_label

    override fun onItemClick(item: SoundInfo?, selectionManager: MultiSelectionManager) {
        if (actionModeType == RENAME) {
            super.onItemClick(item, null)
            return
        }

        if (actionModeType != NONE) {
            super.onItemClick(item, selectionManager)
            return
        }

        if (!BuildConfig.FEATURE_POCKETMUSIC_ENABLED) {
            return
        }

        if (item?.file?.name?.matches(".*MUS-.*\\.midi".toRegex()) == true) {
            val intent = Intent(requireContext(), PocketMusicActivity::class.java)
            intent.putExtra(PocketMusicActivity.TITLE, item.name)
            intent.putExtra(PocketMusicActivity.ABSOLUTE_FILE_PATH, item.file.absolutePath)
            startActivity(intent)
        }
    }

    override fun onSettingsClick(item: SoundInfo?, view: View?) {
        val itemList = mutableListOf<SoundInfo?>()
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
