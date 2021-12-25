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
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.PluralsRes
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.asynctask.ProjectLoadTask
import org.catrobat.catroid.io.asynctask.ProjectLoadTask.ProjectLoadListener
import org.catrobat.catroid.io.asynctask.ProjectSaver
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.recyclerview.adapter.SceneAdapter
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity
import org.catrobat.catroid.ui.recyclerview.controller.SceneController
import org.catrobat.catroid.utils.ToastUtil
import org.koin.android.ext.android.inject
import java.io.IOException

val SCENE_LIST_FRAGMENT_TAG = SceneListFragment::class.java.simpleName

class SceneListFragment :
    RecyclerViewFragment<Scene?>(),
    ProjectLoadListener {

    private val sceneController = SceneController()
    private val projectManager: ProjectManager by inject()

    override fun onResume() {
        super.onResume()
        val currentProject = projectManager.currentProject
        if (currentProject.sceneList.size < 2) {
            projectManager.currentlyEditedScene = currentProject.defaultScene
            switchToSpriteListFragment()
        }
        projectManager.currentlyEditedScene = currentProject.defaultScene
        (requireActivity() as AppCompatActivity).supportActionBar?.title = currentProject.name
    }

    private fun switchToSpriteListFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SpriteListFragment(), SPRITE_LIST_FRAGMENT_TAG)
            .commit()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.new_group).isVisible = false
        menu.findItem(R.id.new_scene).isVisible = false
    }

    override fun initializeAdapter() {
        sharedPreferenceDetailsKey = SharedPreferenceKeys.SHOW_DETAILS_SCENES_PREFERENCE_KEY
        val items = projectManager.currentProject.sceneList
        adapter = SceneAdapter(items)
        onAdapterReady()
    }

    override fun packItems(selectedItems: List<Scene?>) {
        setShowProgressBar(true)
        var packedItemCnt = 0
        for (item in selectedItems) {
            try {
                BackpackListManager.getInstance().scenes.add(sceneController.pack(item))
                BackpackListManager.getInstance().saveBackpack()
                packedItemCnt++
            } catch (e: IOException) {
                Log.e(SCENE_LIST_FRAGMENT_TAG, Log.getStackTraceString(e))
            }
        }
        if (packedItemCnt > 0) {
            ToastUtil.showSuccess(
                requireContext(), resources.getQuantityString(
                    R.plurals.packed_scenes,
                    packedItemCnt,
                    packedItemCnt
                )
            )
            switchToBackpack()
        }
        finishActionMode()
    }

    override fun isBackpackEmpty() = BackpackListManager.getInstance().scenes.isEmpty()

    override fun switchToBackpack() {
        val intent = Intent(requireContext(), BackpackActivity::class.java)
        intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_SCENES)
        startActivity(intent)
    }

    override fun copyItems(selectedItems: List<Scene?>) {
        setShowProgressBar(true)
        var copiedItemCnt = 0
        for (item in selectedItems) {
            try {
                adapter.add(sceneController.copy(item, projectManager.currentProject))
                copiedItemCnt++
            } catch (e: IOException) {
                Log.e(SCENE_LIST_FRAGMENT_TAG, Log.getStackTraceString(e))
            }
        }
        if (copiedItemCnt > 0) {
            ToastUtil.showSuccess(
                requireContext(), resources.getQuantityString(
                    R.plurals.copied_scenes,
                    copiedItemCnt,
                    copiedItemCnt
                )
            )
        }
        finishActionMode()
    }

    @PluralsRes
    override fun getDeleteAlertTitleId() = R.plurals.delete_scenes

    override fun deleteItems(selectedItems: List<Scene?>) {
        setShowProgressBar(true)
        for (item in selectedItems) {
            try {
                sceneController.delete(item)
            } catch (e: IOException) {
                Log.e(SCENE_LIST_FRAGMENT_TAG, Log.getStackTraceString(e))
            }
            adapter.remove(item)
        }
        ToastUtil.showSuccess(
            requireContext(), resources.getQuantityString(
                R.plurals.deleted_scenes,
                selectedItems.size,
                selectedItems.size
            )
        )
        finishActionMode()
        if (adapter.items.isEmpty()) {
            createEmptySceneWithDefaultName()
        }
        val currentProject = projectManager.currentProject
        if (currentProject.sceneList.size < 2) {
            projectManager.currentlyEditedScene = currentProject.defaultScene
            switchToSpriteListFragment()
        }
    }

    private fun createEmptySceneWithDefaultName() {
        setShowProgressBar(true)
        val currentProject = projectManager.currentProject
        val scene = Scene(getString(R.string.default_scene_name, 1), currentProject)
        val backgroundSprite = Sprite(getString(R.string.background))
        backgroundSprite.look.zIndex = Constants.Z_INDEX_BACKGROUND
        scene.addSprite(backgroundSprite)
        adapter.add(scene)
        if (!currentProject.hasScene()) {
            currentProject.addScene(scene)
        }
        setShowProgressBar(false)
    }

    override fun getRenameDialogTitle() = R.string.rename_scene_dialog

    override fun getRenameDialogHint() = R.string.scene_name_label

    override fun renameItem(item: Scene?, name: String) {
        if (item?.name != name) {
            if (sceneController.rename(item, name)) {
                val currentProject = projectManager.currentProject
                ProjectSaver(currentProject, requireContext()).saveProjectAsync()
                ProjectLoadTask(currentProject.directory, requireContext())
                    .setListener(this)
                    .execute()
            } else {
                ToastUtil.showError(requireContext(), R.string.error_rename_scene)
            }
        }
        finishActionMode()
    }

    override fun onItemClick(item: Scene?) {
        if (actionModeType == RENAME) {
            super.onItemClick(item)
            return
        }
        if (actionModeType == NONE) {
            projectManager.currentlyEditedScene = item
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SpriteListFragment(), SPRITE_LIST_FRAGMENT_TAG)
                .addToBackStack(SPRITE_LIST_FRAGMENT_TAG)
                .commit()
        }
    }

    override fun onLoadFinished(success: Boolean) {
        if (!success) {
            ToastUtil.showError(requireContext(), R.string.error_load_project)
            return
        }
        adapter.items = projectManager.currentProject.sceneList
    }

    override fun onSettingsClick(item: Scene?, view: View?) {
        val popupMenu = PopupMenu(context, view)
        val itemList = mutableListOf<Scene?>()
        itemList.add(item)

        popupMenu.menuInflater.inflate(R.menu.menu_project_activity, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.backpack -> packItems(itemList)
                R.id.copy -> copyItems(itemList)
                R.id.delete -> deleteItems(itemList)
                R.id.rename -> showRenameDialog(item)
                else -> {
                }
            }
            true
        }
        popupMenu.menu.findItem(R.id.backpack).setTitle(R.string.pack)
        popupMenu.menu.findItem(R.id.new_group).isVisible = false
        popupMenu.menu.findItem(R.id.new_scene).isVisible = false
        popupMenu.menu.findItem(R.id.show_details).isVisible = false
        popupMenu.menu.findItem(R.id.project_options).isVisible = false
        popupMenu.show()
    }
}
