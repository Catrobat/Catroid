/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.os.Bundle
import android.view.View
import androidx.annotation.PluralsRes
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.asynctask.ProjectLoader.ProjectLoadListener
import org.catrobat.catroid.io.asynctask.loadProject
import org.catrobat.catroid.ui.FinderDataManager
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.Finder
import org.catrobat.catroid.ui.UiUtils
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.recyclerview.adapter.SceneAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity
import org.catrobat.catroid.ui.recyclerview.controller.SceneController
import org.catrobat.catroid.utils.ToastUtil
import org.koin.android.ext.android.inject
import java.io.IOException

class SceneListFragment : RecyclerViewFragment<Scene?>(), ProjectLoadListener {

    private val sceneController = SceneController()
    private val projectManager: ProjectManager by inject()

    private lateinit var currentProject: Project
    private lateinit var currentScene: Scene

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val parentView = super.onCreateView(inflater, container, savedInstanceState)
        val activity = getActivity() as ProjectActivity
        recyclerView = parentView!!.findViewById(R.id.recycler_view)
        currentProject = ProjectManager.getInstance().currentProject
        currentScene = ProjectManager.getInstance().currentlyEditedScene

        finder?.setOnResultFoundListener(object : Finder.OnResultFoundListener {
            override fun onResultFound(
                sceneIndex: Int,
                spriteIndex: Int,
                elementIndex: Int,
                type: FinderDataManager.FragmentType,
                textView: TextView?
            ) {

                currentScene = currentProject.sceneList[sceneIndex]
                FinderDataManager.instance.type = type
                FinderDataManager.instance.currentMatchIndex = elementIndex

                if (type != FinderDataManager.FragmentType.SCENE) {
                    onItemClick(currentScene, MultiSelectionManager())
                } else {
                    textView?.text = createActionBarTitle()
                    initializeAdapter()
                    adapter.notifyDataSetChanged()
                    scrollToSearchResult()
                    finder.disableFocusSearchBar()
                }
                hideKeyboard()
            }
        })
        finder?.setOnCloseListener(object : Finder.OnCloseListener {
            override fun onClose() {
                activity.findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
                finishActionMode()
                FinderDataManager.instance.setSearchResultIndex(-1)
            }
        })

        finder?.setOnOpenListener(object : Finder.OnOpenListener {
            override fun onOpen() {
                activity.findViewById<View>(R.id.toolbar).visibility = View.GONE
                finder.setInitiatingFragment(FinderDataManager.FragmentType.SCENE)
                finder.setInitiatingPosition(
                    -1, -1, FinderDataManager.FragmentType.SCENE
                )
            }
        })

        return parentView
    }

    fun createActionBarTitle(): String {
        return currentProject.name
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onResume() {
        super.onResume()

        val currentProject = projectManager.currentProject
        if (currentProject.sceneList.size < 2) {
            projectManager.currentlyEditedScene = currentProject.defaultScene
            switchToSpriteListFragment()
        }
        projectManager.currentlyEditedScene = currentProject.defaultScene
        (requireActivity() as AppCompatActivity).supportActionBar?.title = currentProject.name

        if (FinderDataManager.instance.getInitiatingFragment() != FinderDataManager.FragmentType.NONE) {
            val sceneAndSpriteName = createActionBarTitle()
            finder.onFragmentChanged(sceneAndSpriteName)
            scrollToSearchResult()
            hideKeyboard()
        } else {
            finder.close()
        }
    }

    private fun switchToSpriteListFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SpriteListFragment(), SpriteListFragment.TAG).commit()
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
                Log.e(TAG, Log.getStackTraceString(e))
            }
        }
        if (packedItemCnt > 0) {
            ToastUtil.showSuccess(
                activity, resources.getQuantityString(
                    R.plurals.packed_scenes, packedItemCnt, packedItemCnt
                )
            )
            switchToBackpack()
        }
        finishActionMode()
    }

    override fun isBackpackEmpty() = BackpackListManager.getInstance().scenes.isEmpty()

    override fun switchToBackpack() {
        val intent = Intent(activity, BackpackActivity::class.java)
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
                Log.e(TAG, Log.getStackTraceString(e))
            }
        }
        if (copiedItemCnt > 0) {
            ToastUtil.showSuccess(
                activity, resources.getQuantityString(
                    R.plurals.copied_scenes, copiedItemCnt, copiedItemCnt
                )
            )
        }
        finishActionMode()
    }

    @PluralsRes
    override fun getDeleteAlertTitleId() = R.plurals.delete_scenes

    override fun deleteItems(selectedItems: List<Scene?>) {
        setShowProgressBar(true)
        var deletedItemsCount = 0
        for (item in selectedItems) {
            try {
                sceneController.delete(item)
            } catch (e: IOException) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
            adapter.remove(item)
            deletedItemsCount++
        }
        ToastUtil.showSuccess(
            activity, resources.getQuantityString(
                R.plurals.deleted_scenes, deletedItemsCount, deletedItemsCount
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
        val scene = Scene(getString(R.string.default_scene_name), currentProject)
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
                XstreamSerializer.getInstance().saveProject(currentProject)
                loadProject(currentProject.directory, requireContext().applicationContext)
                initializeAdapter()
            } else {
                ToastUtil.showError(activity, R.string.error_rename_scene)
            }
        }
        finishActionMode()
    }

    override fun onItemClick(item: Scene?, selectionManager: MultiSelectionManager?) {
        when (actionModeType) {
            RENAME -> {
                super.onItemClick(item, null)
                return
            }

            NONE -> {
                projectManager.currentlyEditedScene = item
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SpriteListFragment(), SpriteListFragment.TAG)
                    .addToBackStack(SpriteListFragment.TAG).commit()
            }

            else -> super.onItemClick(item, selectionManager)
        }
    }

    override fun onSettingsClick(item: Scene?, view: View) {
        val itemList = mutableListOf<Scene?>()
        itemList.add(item)

        val hiddenOptionMenuIds = intArrayOf(
            R.id.new_group,
            R.id.new_scene,
            R.id.show_details,
            R.id.project_options,
            R.id.edit,
            R.id.from_local,
            R.id.find
        )
        val popupMenu = UiUtils.createSettingsPopUpMenu(
            view, requireContext(), R.menu.menu_project_activity, hiddenOptionMenuIds
        )

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

    override fun onLoadFinished(success: Boolean) {
        if (!success) {
            ToastUtil.showError(activity, R.string.error_load_project)
            return
        }
        adapter.items = projectManager.currentProject.sceneList
    }

    companion object {
        val TAG: String = SceneListFragment::class.java.simpleName
    }
}
