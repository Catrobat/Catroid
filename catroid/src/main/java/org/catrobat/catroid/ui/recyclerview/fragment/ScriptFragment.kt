/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.ListFragment
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME
import org.catrobat.catroid.common.Constants.UNDO_CODE_XML_FILE_NAME
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.EmptyEventBrick
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.content.bricks.VisualPlacementBrick
import org.catrobat.catroid.formulaeditor.InternToExternGenerator
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.asynctask.ProjectLoader
import org.catrobat.catroid.io.asynctask.ProjectSaver
import org.catrobat.catroid.ui.BottomBar
import org.catrobat.catroid.ui.ScriptFinder
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.UiUtils
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.controller.RecentBrickListManager
import org.catrobat.catroid.ui.dragndrop.BrickListView
import org.catrobat.catroid.ui.fragment.AddBrickFragment
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment.OnCategorySelectedListener
import org.catrobat.catroid.ui.fragment.BrickSearchFragment
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory
import org.catrobat.catroid.ui.fragment.UserDefinedBrickListFragment
import org.catrobat.catroid.ui.recyclerview.adapter.BrickAdapter
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity
import org.catrobat.catroid.ui.recyclerview.controller.BrickController
import org.catrobat.catroid.ui.recyclerview.controller.ScriptController
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.utils.SnackbarUtil
import org.catrobat.catroid.utils.ToastUtil
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.util.UUID

class ScriptFragment : ListFragment(),
    ActionMode.Callback,
    BrickAdapter.OnBrickClickListener,
    BrickAdapter.SelectionListener,
    OnCategorySelectedListener,
    AddBrickFragment.OnAddBrickListener,
    ProjectLoader.ProjectLoadListener {

    companion object {
        @JvmField
        val TAG: String = ScriptFragment::class.java.simpleName
        private const val BRICK_TAG = "brickToFocus"
        private const val SCRIPT_TAG = "scriptToFocus"
        private val projectManager: ProjectManager by inject(ProjectManager::class.java)

        const val NONE = 0
        const val BACKPACK = 1
        const val COPY = 2
        const val DELETE = 3
        const val COMMENT = 4
        const val CATBLOCKS = 5

        private const val AI_ASSIST_PADDING_FACTOR = 2.5
        private const val DEFAULT_PADDING_FACTOR = 3.0

        @JvmStatic
        fun <T : Serializable> newInstance(itemToFocus: T): ScriptFragment {
            val scriptFragment = ScriptFragment()
            val bundle = Bundle()
            when (itemToFocus) {
                is Brick -> bundle.putSerializable(BRICK_TAG, itemToFocus)
                is Script -> bundle.putSerializable(SCRIPT_TAG, itemToFocus)
                else -> throw IllegalArgumentException("Unsupported type for newInstance")
            }
            scriptFragment.arguments = bundle
            return scriptFragment
        }

        @JvmStatic
        @VisibleForTesting
        fun getContextMenuItems(brick: Brick): List<Int> {
            val items = ArrayList<Int>()

            if (brick is UserDefinedReceiverBrick) {
                items.add(R.string.backpack_add)
                items.add(R.string.brick_context_dialog_delete_definition)
                items.add(R.string.brick_context_dialog_move_definition)
                items.add(R.string.brick_context_dialog_help)
                return items
            }

            if (brick is ScriptBrick) {
                items.add(R.string.backpack_add)

                if (brick !is EmptyEventBrick) {
                    items.add(
                        if (brick.isCommentedOut) {
                            R.string.brick_context_dialog_comment_in_script
                        } else {
                            R.string.brick_context_dialog_comment_out_script
                        }
                    )
                }

                items.add(R.string.brick_context_dialog_copy_script)
                items.add(R.string.brick_context_dialog_delete_script)

                if (brick is FormulaBrick && brick.hasEditableFormulaField()) {
                    items.add(R.string.brick_context_dialog_formula_edit_brick)
                }
                items.add(R.string.brick_context_dialog_move_script)
                items.add(R.string.brick_context_dialog_help)
            } else {
                items.add(R.string.brick_context_dialog_copy_brick)
                if (brick.consistsOfMultipleParts()) {
                    items.add(R.string.brick_context_dialog_highlight_brick_parts)
                }
                items.add(R.string.brick_context_dialog_delete_brick)

                items.add(
                    if (brick.isCommentedOut) {
                        R.string.brick_context_dialog_comment_in
                    } else {
                        R.string.brick_context_dialog_comment_out
                    }
                )
                if (brick is VisualPlacementBrick && brick.areAllBrickFieldsNumbers()) {
                    items.add(R.string.brick_option_place_visually)
                }
                if (brick is FormulaBrick && brick.hasEditableFormulaField()) {
                    items.add(R.string.brick_context_dialog_formula_edit_brick)
                }
                if (brick == brick.allParts[0]) {
                    items.add(R.string.brick_context_dialog_move_brick)
                }

                if (brick.hasHelpPage()) {
                    items.add(R.string.brick_context_dialog_help)
                }
            }
            return items
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(NONE, BACKPACK, COPY, DELETE, COMMENT, CATBLOCKS)
    annotation class ActionModeType

    @ActionModeType
    private var actionModeType = NONE

    private var actionMode: ActionMode? = null
    private var adapter: BrickAdapter? = null
    private var listView: BrickListView? = null
    private var scriptFinder: ScriptFinder? = null
    private var currentSceneName: String? = null
    private var currentSpriteName: String? = null
    private var undoBrickPosition: Int = 0

    private val scriptController = ScriptController()
    private val brickController = BrickController()

    private var savedListViewState: Parcelable? = null
    private var brickToFocus: Brick? = null
    private var scriptToFocus: Script? = null

    private var activity: SpriteActivity? = null

    private var savedUserVariables: List<UserVariable>? = null
    private var savedMultiplayerVariables: List<UserVariable>? = null
    private var savedUserLists: List<UserList>? = null
    private var savedLocalUserVariables: List<UserVariable>? = null
    private var savedLocalLists: List<UserList>? = null

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        bundle?.let {
            this.brickToFocus = it.getSerializable(BRICK_TAG) as Brick?
            this.scriptToFocus = it.getSerializable(SCRIPT_TAG) as Script?
        }
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        val inflater = mode.menuInflater
        inflater.inflate(R.menu.context_menu, menu)

        when (actionModeType) {
            BACKPACK -> {
                adapter?.setCheckBoxMode(BrickAdapter.SCRIPTS_ONLY)
                mode.title = getString(R.string.am_backpack)
            }
            COPY -> {
                adapter?.setCheckBoxMode(BrickAdapter.CONNECTED_ONLY)
                mode.title = getString(R.string.am_copy)
            }
            DELETE -> {
                adapter?.setCheckBoxMode(BrickAdapter.ALL)
                mode.title = getString(R.string.am_delete)
            }
            COMMENT -> {
                adapter?.selectAllCommentedOutBricks()
                adapter?.setCheckBoxMode(BrickAdapter.ALL)
                mode.title = getString(R.string.comment_in_out)
            }
            NONE -> {
                adapter?.setCheckBoxMode(NONE)
                actionMode?.finish()
                return false
            }
            CATBLOCKS -> {}
        }
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        if (item.itemId == R.id.confirm) {
            handleContextualAction()
        } else {
            return false
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        resetActionModeParameters()
        adapter?.clearSelection()
        BottomBar.showBottomBar(activity)
    }

    private fun handleContextualAction() {
        if (adapter?.isEmpty == true) {
            actionMode?.finish()
        }

        when (actionModeType) {
            BACKPACK -> showNewScriptGroupAlert(adapter?.selectedItems ?: listOf())
            COPY -> copy(adapter?.selectedItems ?: listOf())
            DELETE -> showDeleteAlert(adapter?.selectedItems ?: listOf())
            COMMENT -> toggleComments(adapter?.selectedItems ?: listOf())
            NONE -> throw IllegalStateException("ActionModeType not set correctly")
            CATBLOCKS -> {}
        }
    }

    private fun resetActionModeParameters() {
        actionModeType = NONE
        actionMode = null
        adapter?.setCheckBoxMode(BrickAdapter.NONE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = View.inflate(getActivity(), R.layout.fragment_script, null)
        listView = view.findViewById(android.R.id.list)
        val bottomListPadding = if (BuildConfig.FEATURE_AI_ASSIST_ENABLED) {
            (ScreenValues.currentScreenResolution.height / AI_ASSIST_PADDING_FACTOR).toInt()
        } else {
            (ScreenValues.currentScreenResolution.height / DEFAULT_PADDING_FACTOR).toInt()
        }
        listView?.setPadding(0, 0, 0, bottomListPadding)
        listView?.clipToPadding = false

        activity = requireActivity() as SpriteActivity
        SettingsFragment.setToChosenLanguage(activity)

        scriptFinder = view.findViewById(R.id.findview)
        scriptFinder?.setOnResultFoundListener(object : ScriptFinder.OnResultFoundListener {
            override fun onResultFound(
                sceneIndex: Int,
                spriteIndex: Int,
                brickIndex: Int,
                totalResults: Int,
                textView: TextView?
            ) {
                val currentProject = projectManager.currentProject
                val currentScene = currentProject.sceneList[sceneIndex]
                val currentSprite = currentScene.spriteList[spriteIndex]

                textView?.text = createActionBarTitle(currentProject, currentScene, currentSprite)

                projectManager
                    .setCurrentSceneAndSprite(currentScene.name, currentSprite.name)

                adapter?.updateItems(currentSprite)
                adapter?.notifyDataSetChanged()
                listView?.smoothScrollToPosition(brickIndex)
                highlightBrickAtIndex(brickIndex)
                hideKeyboard()
            }
        })

        scriptFinder?.setOnCloseListener(object : ScriptFinder.OnCloseListener {
            override fun onClose() {
                listView?.cancelHighlighting()
                finishActionMode()
                if (activity != null && !activity!!.isFinishing) {
                    activity?.setCurrentSceneAndSprite(
                        projectManager.currentlyEditedScene,
                        projectManager.currentSprite
                    )
                    activity?.supportActionBar?.title = activity?.createActionBarTitle()
                    activity?.addTabs()
                }
                activity?.findViewById<View>(R.id.toolbar)?.visibility = View.VISIBLE
            }
        })

        scriptFinder?.setOnOpenListener(object : ScriptFinder.OnOpenListener {
            override fun onOpen() {
                activity?.removeTabs()
                activity?.findViewById<View>(R.id.toolbar)?.visibility = View.GONE
            }
        })

        setHasOptionsMenu(true)
        return view
    }

    fun createActionBarTitle(
        currentProject: Project,
        currentScene: Scene,
        currentSprite: Sprite
    ): String {
        return if (currentProject.sceneList.size == 1) {
            currentSprite.name
        } else {
            "${currentScene.name}: ${currentSprite.name}"
        }
    }

    private fun highlightBrickAtIndex(index: Int) {
        listView?.brickPositionsToHighlight?.clear()
        listView?.brickPositionsToHighlight?.add(index)
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if ((scriptFinder?.isOpen == true) && (activity != null)) {
            activity?.findViewById<View>(R.id.toolbar)?.visibility = View.VISIBLE
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val currentProject = projectManager.currentProject
        val currentScene = projectManager.currentlyEditedScene
        val currentSprite = projectManager.currentSprite
        currentProject.broadcastMessageContainer.update()

        adapter = BrickAdapter(projectManager.currentSprite)
        adapter?.setSelectionListener(this)
        adapter?.setOnItemClickListener(this)

        listView?.adapter = adapter
        listView?.onItemClickListener = adapter
        listView?.onItemLongClickListener = adapter

        if (currentSprite == currentScene.backgroundSprite) {
            InternToExternGenerator.setInternExternLanguageConverterMap(
                Sensors.OBJECT_NUMBER_OF_LOOKS,
                R.string.formula_editor_object_number_of_backgrounds
            )
        } else {
            InternToExternGenerator.setInternExternLanguageConverterMap(
                Sensors.OBJECT_NUMBER_OF_LOOKS,
                R.string.formula_editor_object_number_of_looks
            )
        }
    }

    override fun onResume() {
        super.onResume()

        val project = projectManager.currentProject
        val scene = projectManager.currentlyEditedScene
        val sprite = projectManager.currentSprite

        val actionBar = (activity as AppCompatActivity).supportActionBar

        if (project.sceneList.size > 1) {
            actionBar?.title = "${scene.name}: ${sprite.name}"
        } else {
            actionBar?.title = sprite.name
        }

        if (BackpackListManager.getInstance().isBackpackEmpty) {
            BackpackListManager.getInstance().loadBackpack()
        }

        BottomBar.showBottomBar(activity)
        BottomBar.showPlayButton(activity)
        BottomBar.showAddButton(activity)

        if (BuildConfig.FEATURE_AI_ASSIST_ENABLED) {
            BottomBar.showAiAssistButton(activity)
        }

        adapter?.updateItems(projectManager.currentSprite)

        savedListViewState?.let {
            listView?.onRestoreInstanceState(it)
        }

        scrollToFocusItem()
        SnackbarUtil.showHintSnackbar(activity, R.string.hint_scripts)
    }

    override fun onPause() {
        super.onPause()
        val currentProject = projectManager.currentProject
        ProjectSaver(currentProject, requireContext()).saveProjectAsync()

        savedListViewState = listView?.onSaveInstanceState()

        (activity as SpriteActivity).setUndoMenuItemVisibility(false)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.show_details).isVisible = false
        menu.findItem(R.id.rename).isVisible = false
        menu.findItem(R.id.catblocks_reorder_scripts).isVisible = false
        menu.findItem(R.id.find).isVisible = true
        if (!BuildConfig.FEATURE_CATBLOCKS_ENABLED) {
            menu.findItem(R.id.catblocks).isVisible = false
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (listView?.isCurrentlyMoving == true) {
            listView?.highlightMovingItem()
            return true
        }
        if (listView?.isCurrentlyHighlighted == true) {
            listView?.cancelHighlighting()
        }
        when (item.itemId) {
            R.id.menu_undo -> loadProjectAfterUndoOption()
            R.id.backpack -> prepareActionMode(BACKPACK)
            R.id.copy -> prepareActionMode(COPY)
            R.id.delete -> prepareActionMode(DELETE)
            R.id.comment_in_out -> prepareActionMode(COMMENT)
            R.id.catblocks -> switchToCatblocks()
            R.id.find -> scriptFinder?.open()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun notifyDataSetChanged() {
        adapter?.notifyDataSetChanged()
    }

    fun isCurrentlyMoving(): Boolean = listView?.isCurrentlyMoving == true

    fun highlightMovingItem() {
        listView?.highlightMovingItem()
    }

    fun cancelMove() {
        listView?.cancelMove()
        val sprite = projectManager.currentSprite
        adapter?.updateItems(sprite)
    }

    fun isCurrentlyHighlighted(): Boolean = listView?.isCurrentlyHighlighted == true

    fun cancelHighlighting() {
        listView?.cancelHighlighting()
    }

    @Suppress("DEPRECATION")
    private fun showCategoryFragment() {
        val brickCategoryFragment = BrickCategoryFragment()
        brickCategoryFragment.setOnCategorySelectedListener(this)

        fragmentManager?.beginTransaction()
            ?.add(
                R.id.fragment_container,
                brickCategoryFragment,
                BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG
            )
            ?.addToBackStack(BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG)
            ?.commit()
    }

    @Suppress("DEPRECATION")
    override fun onCategorySelected(category: String?) {
        val fragment: ListFragment?
        val tag: String
        val currentFragment = parentFragmentManager.findFragmentById(R.id.fragment_container)
        if (category == context?.getString(R.string.category_user_bricks)) {
            fragment = UserDefinedBrickListFragment.newInstance(this)
            tag = UserDefinedBrickListFragment.USER_DEFINED_BRICK_LIST_FRAGMENT_TAG
        } else if ((currentFragment is AddBrickFragment) || (category == context?.getString(R.string.category_search_bricks))) {
            fragment = BrickSearchFragment.newInstance(this, category)
            tag = BrickSearchFragment.BRICK_SEARCH_FRAGMENT_TAG
        } else {
            fragment = AddBrickFragment.newInstance(category, this)
            tag = AddBrickFragment.ADD_BRICK_FRAGMENT_TAG
        }

        fragmentManager?.beginTransaction()
            ?.add(R.id.fragment_container, fragment, tag)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun prepareActionMode(@ActionModeType type: Int) {
        if (type == BACKPACK) {
            if (BackpackListManager.getInstance().backpackedScriptGroups.isEmpty()) {
                startActionMode(BACKPACK)
            } else if (adapter?.isEmpty == true) {
                switchToBackpack()
            } else {
                showBackpackModeChooser()
            }
        } else {
            startActionMode(type)
        }
    }

    private fun startActionMode(@ActionModeType type: Int) {
        if (adapter?.isEmpty == true) {
            ToastUtil.showError(activity, R.string.am_empty_list)
        } else {
            actionModeType = type
            actionMode = activity?.startActionMode(this)
            BottomBar.hideBottomBar(activity)
        }
    }

    override fun onSelectionChanged(selectedItemCnt: Int) {
        when (actionModeType) {
            BACKPACK -> actionMode?.title = getString(R.string.am_backpack) + " " + selectedItemCnt
            COPY -> actionMode?.title = getString(R.string.am_copy) + " " + selectedItemCnt
            DELETE -> actionMode?.title = getString(R.string.am_delete) + " " + selectedItemCnt
            COMMENT ->
                actionMode?.title = getString(R.string.comment_in_out) + " " + selectedItemCnt
            NONE -> throw IllegalStateException("ActionModeType not set correctly")
            CATBLOCKS -> {}
        }
    }

    fun finishActionMode() {
        adapter?.clearSelection()
        if (actionModeType != NONE) {
            actionMode?.finish()
        }
    }

    fun findBrickByHash(hashCode: Int): Brick? = adapter?.findByHash(hashCode)

    fun handleAddButton() {
        if (listView?.isCurrentlyHighlighted == true) {
            listView?.cancelHighlighting()
        }
        if (listView?.isCurrentlyMoving == true) {
            listView?.highlightMovingItem()
        } else {
            (activity as SpriteActivity).setUndoMenuItemVisibility(false)
            showCategoryFragment()
        }
    }

    override fun addBrick(brick: Brick?) {
        brick?.let {
            try {
                if ((brick.javaClass != UserDefinedReceiverBrick::class.java) && (brick.javaClass != UserDefinedBrick::class.java)) {
                    RecentBrickListManager.getInstance().addBrick(brick.clone())
                }
            } catch (e: CloneNotSupportedException) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
            val sprite = projectManager.currentSprite
            addBrick(brick, sprite, adapter!!, listView!!)
        }
    }

    @VisibleForTesting
    fun addBrick(
        brick: Brick,
        sprite: Sprite,
        brickAdapter: BrickAdapter,
        brickListView: BrickListView
    ) {
        if (brickAdapter.count == 0) {
            if (brick is ScriptBrick) {
                sprite.addScript(brick.script)
            } else {
                val script = StartScript()
                script.addBrick(brick)
                sprite.addScript(script)
            }
            brickAdapter.updateItems(sprite)
        } else if ((brickAdapter.count == 1) && (brick !is ScriptBrick)) {
            sprite.scriptList[0].addBrick(brick)
            brickAdapter.updateItems(sprite)
        } else {
            val firstVisibleBrick = brickListView.firstVisiblePosition
            val lastVisibleBrick = brickListView.lastVisiblePosition
            var position = (1 + lastVisibleBrick - firstVisibleBrick) / 2
            position += firstVisibleBrick
            brickAdapter.addItem(position, brick)
            brickListView.startMoving(brick)
        }
    }

    override fun onBrickClick(item: Brick, position: Int) {
        if (listView?.isCurrentlyHighlighted == true) {
            listView?.cancelHighlighting()
            return
        }

        val options = getContextMenuItems(item)
        val names = options.map { getString(it) }

        val arrayAdapter = UiUtils.getAlertDialogAdapterForMenuIcons(
            options,
            names,
            requireContext(),
            requireActivity()
        )

        val brickView = item.getView(context)
        item.disableSpinners()

        AlertDialog.Builder(requireContext())
            .setCustomTitle(brickView)
            .setAdapter(arrayAdapter) { _, which ->
                handleContextMenuItemClick(options[which], item, position)
            }.show()
    }

    private fun handleContextMenuItemClick(itemId: Int, brick: Brick, position: Int) {
        showUndo(false)
        when (itemId) {
            R.string.backpack_add -> {
                val bricksToPack = ArrayList<Brick>()
                brick.addToFlatList(bricksToPack)
                showNewScriptGroupAlert(bricksToPack)
            }
            R.string.brick_context_dialog_copy_brick,
            R.string.brick_context_dialog_copy_script -> try {
                val clonedBrick = brick.allParts[0].clone()
                adapter?.addItem(position, clonedBrick)
                listView?.startMoving(clonedBrick)
            } catch (e: CloneNotSupportedException) {
                ToastUtil.showError(context, R.string.error_copying_brick)
                Log.e(TAG, Log.getStackTraceString(e))
            }
            R.string.brick_context_dialog_delete_brick,
            R.string.brick_context_dialog_delete_script,
            R.string.brick_context_dialog_delete_definition -> showDeleteAlert(brick.allParts)
            R.string.brick_context_dialog_comment_in,
            R.string.brick_context_dialog_comment_in_script -> {
                for (brickPart in brick.allParts) {
                    brickPart.isCommentedOut = false
                }
                adapter?.notifyDataSetChanged()
            }
            R.string.brick_context_dialog_comment_out,
            R.string.brick_context_dialog_comment_out_script -> {
                for (brickPart in brick.allParts) {
                    brickPart.isCommentedOut = true
                }
                adapter?.notifyDataSetChanged()
            }
            R.string.brick_option_place_visually -> {
                val visualPlacementBrick = brick as VisualPlacementBrick
                visualPlacementBrick.placeVisually(
                    visualPlacementBrick.xBrickField,
                    visualPlacementBrick.yBrickField
                )
            }
            R.string.brick_context_dialog_formula_edit_brick ->
                (brick as FormulaBrick).onClick(listView)
            R.string.brick_context_dialog_move_brick,
            R.string.brick_context_dialog_move_script,
            R.string.brick_context_dialog_move_definition -> onBrickLongClick(brick, position)
            R.string.brick_context_dialog_help -> openWebViewWithHelpPage(brick)
            R.string.brick_context_dialog_highlight_brick_parts -> {
                val bricksOfControlStructure = brick.allParts
                val positions = mutableListOf<Int>()
                for (brickInControlStructure in bricksOfControlStructure) {
                    adapter?.getPosition(brickInControlStructure)?.let {
                        positions.add(it)
                    }
                }
                listView?.highlightControlStructureBricks(positions as List<Int>)
            }
        }
    }

    private fun openWebViewWithHelpPage(brick: Brick) {
        val sprite = projectManager.currentSprite
        val backgroundSprite = projectManager.currentlyEditedScene.backgroundSprite
        val category = CategoryBricksFactory().getBrickCategory(
            brick, sprite ==
                backgroundSprite, requireContext()
        )

        val brickHelpUrl = brick.getHelpUrl(category)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(brickHelpUrl))
        startActivity(intent)
    }

    override fun onBrickLongClick(item: Brick, position: Int): Boolean {
        showUndo(false)
        if (listView?.isCurrentlyHighlighted == true) {
            listView?.cancelHighlighting()
        } else {
            listView?.startMoving(item)
        }
        return true
    }

    private fun showBackpackModeChooser() {
        val items: Array<CharSequence> =
            arrayOf(getString(R.string.pack), getString(R.string.unpack))
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.backpack_title)
            .setItems(items) { _, which ->
                when (which) {
                    0 -> startActionMode(BACKPACK)
                    1 -> switchToBackpack()
                }
            }
            .show()
    }

    private fun showNewScriptGroupAlert(selectedBricks: List<Brick>) {
        val builder = TextInputDialog.Builder(requireContext())
        val duplicateInputTextwatcher = DuplicateInputTextWatcher<Nameable>(null)
        duplicateInputTextwatcher.setScope(BackpackListManager.getInstance().backpackedScriptGroups)

        builder.setText(
            UniqueNameProvider().getUniqueName(
                getString(R.string.default_script_group_name),
                BackpackListManager.getInstance().backpackedScriptGroups
            )
        )

        builder.setHint(getString(R.string.script_group_label))
            .setTextWatcher(duplicateInputTextwatcher)
            .setPositiveButton(getString(R.string.ok),
                TextInputDialog.OnClickListener { _,
                                                  textInput -> pack(textInput, selectedBricks)
                })

        builder.setTitle(R.string.new_group)
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    fun pack(name: String, selectedBricks: List<Brick>) {
        try {
            scriptController.pack(name, selectedBricks)
            ToastUtil.showSuccess(activity, getString(R.string.packed_script_group))
            switchToBackpack()
        } catch (e: CloneNotSupportedException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }

        finishActionMode()
    }

    private fun switchToBackpack() {
        val intent = Intent(activity, BackpackActivity::class.java)
        intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_SCRIPTS)
        startActivity(intent)
    }

    private fun switchToCatblocks() {
        if (!BuildConfig.FEATURE_CATBLOCKS_ENABLED) {
            return
        }

        val firstVisible = listView?.firstVisiblePosition
        var firstVisibleBrickID: UUID? = null
        if (((listView?.count ?: 0) > 0) && (firstVisible != null) && (firstVisible >= 0)) {
            val firstVisibleObject = listView?.getItemAtPosition(firstVisible)
            if (firstVisibleObject is Brick) {
                firstVisibleBrickID = if (firstVisibleObject is ScriptBrick) {
                    firstVisibleObject.script.scriptId
                } else {
                    firstVisibleObject.brickID
                }
            }
        }

        SettingsFragment.setUseCatBlocks(context, true)

        val catblocksFragment = CatblocksScriptFragment(firstVisibleBrickID)

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(
            R.id.fragment_container,
            catblocksFragment,
            CatblocksScriptFragment.TAG
        )
        fragmentTransaction.commit()
    }

    private fun copy(selectedBricks: List<Brick>) {
        val sprite = projectManager.currentSprite
        brickController.copy(selectedBricks, sprite)
        adapter?.updateItems(sprite)
        finishActionMode()
    }

    private fun showDeleteAlert(selectedBricks: List<Brick>) {
        if (selectedBricks.isNotEmpty() && copyProjectForUndoOption()) {
            showUndo(true)
            undoBrickPosition = adapter?.getPosition(selectedBricks[0]) ?: 0
        }
        delete(selectedBricks)
    }

    private fun delete(selectedItems: List<Brick>) {
        val sprite = projectManager.currentSprite
        brickController.delete(selectedItems, sprite)
        adapter?.updateItems(sprite)
        finishActionMode()
    }

    private fun toggleComments(selectedBricks: List<Brick>) {
        adapter?.items?.forEach { brick ->
            brick.isCommentedOut = selectedBricks.contains(brick)
        }
        finishActionMode()
    }

    fun setUndoBrickPosition(brick: Brick) {
        undoBrickPosition = adapter?.getPosition(brick) ?: 0
    }

    fun copyProjectForUndoOption(): Boolean {
        val projectManager = projectManager
        val currentSprite = projectManager.currentSprite
        currentSpriteName = currentSprite.name
        currentSceneName = projectManager.currentlyEditedScene.name
        val project = projectManager.currentProject
        XstreamSerializer.getInstance().saveProject(project)
        val currentCodeFile = File(project.directory, CODE_XML_FILE_NAME)
        val undoCodeFile = File(project.directory, UNDO_CODE_XML_FILE_NAME)

        if (currentCodeFile.exists()) {
            try {
                StorageOperations.transferData(currentCodeFile, undoCodeFile)
                saveVariables()
                return true
            } catch (exception: IOException) {
                Log.e(TAG, "Copying project ${project.name} failed.", exception)
            }
        }
        return false
    }

    fun loadProjectAfterUndoOption() {
        val project = projectManager.currentProject
        val currentCodeFile = File(project.directory, CODE_XML_FILE_NAME)
        val undoCodeFile = File(project.directory, UNDO_CODE_XML_FILE_NAME)

        if (currentCodeFile.exists()) {
            try {
                StorageOperations.transferData(undoCodeFile, currentCodeFile)
                ProjectLoader(project.directory, requireContext()).setListener(this)
                    .loadProjectAsync()
            } catch (exception: IOException) {
                Log.e(TAG, "Replacing project ${project.name} failed.", exception)
            }
        }
    }

    override fun onLoadFinished(success: Boolean) {
        projectManager.setCurrentSceneAndSprite(currentSceneName, currentSpriteName)
        if (checkVariables()) {
            loadVariables()
        }
        refreshFragmentAfterUndo()
    }

    private fun saveVariables() {
        val projectManager = projectManager
        val currentSprite = projectManager.currentSprite
        val project = projectManager.currentProject

        savedUserVariables = project.userVariablesCopy
        savedMultiplayerVariables = project.multiplayerVariablesCopy
        savedUserLists = project.userListsCopy
        savedLocalUserVariables = currentSprite.userVariablesCopy
        savedLocalLists = currentSprite.userListsCopy
    }

    fun checkVariables(): Boolean {
        val projectManager = projectManager
        val currentSprite = projectManager.currentSprite
        val project = projectManager.currentProject

        return (project.hasUserDataChanged(project.userVariables, savedUserVariables) ||
            project.hasUserDataChanged(project.multiplayerVariables, savedMultiplayerVariables) ||
            project.hasUserDataChanged(project.userLists, savedUserLists) ||
            currentSprite.hasUserDataChanged(
                currentSprite.userVariables,
                savedLocalUserVariables
            ) ||
            currentSprite.hasUserDataChanged(currentSprite.userLists, savedLocalLists))
    }

    private fun loadVariables() {
        val projectManager = projectManager
        val currentSprite = projectManager.currentSprite
        val project = projectManager.currentProject

        project.restoreUserDataValues(project.userVariables, savedUserVariables)
        project.restoreUserDataValues(project.multiplayerVariables, savedMultiplayerVariables)
        project.restoreUserDataValues(project.userLists, savedUserLists)
        currentSprite.restoreUserDataValues(currentSprite.userVariables, savedLocalUserVariables)
        currentSprite.restoreUserDataValues(currentSprite.userLists, savedLocalLists)
    }

    private fun refreshFragmentAfterUndo() {
        val scriptFragment = activity?.supportFragmentManager?.findFragmentByTag(TAG)
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.detach(scriptFragment!!)
        fragmentTransaction?.attach(scriptFragment!!)
        fragmentTransaction?.commit()
        if (undoBrickPosition < (listView?.firstVisiblePosition ?: 0) ||
            undoBrickPosition > (listView?.lastVisiblePosition ?: 0)
        ) {
            listView?.post { listView?.setSelection(undoBrickPosition) }
        }
    }

    fun showUndo(visible: Boolean) {
        val activity = activity
        activity?.showUndo(visible)
    }

    private fun scrollToFocusItem() {
        if (scriptToFocus == null && brickToFocus == null) {
            return
        }

        var scrollToIndex = -1
        val itemCount = listView?.adapter?.count ?: 0
        for (i in 0 until itemCount) {
            val item = listView?.getItemAtPosition(i)
            if (item is Brick && shouldFocusItem(item)) {
                scrollToIndex = i
                break
            }
        }
        if (scrollToIndex == -1) {
            return
        }
        if (activity != null) {
            val finalScrollToIndex = scrollToIndex
            requireActivity().runOnUiThread {
                listView?.setSelection(finalScrollToIndex)
            }
        }
        scriptToFocus = null
        brickToFocus = null
    }

    private fun shouldFocusItem(item: Brick): Boolean {
        val isBrickFocused = brickToFocus != null && item == brickToFocus
        val isScriptFocused = scriptToFocus != null && item.script == scriptToFocus
        return isBrickFocused || isScriptFocused
    }

    fun getActionModeType(): Int = actionModeType

    fun isFinderOpen(): Boolean = scriptFinder?.isOpen == true

    fun closeFinder() {
        scriptFinder?.takeIf { !it.isClosed }?.close()
    }
}
