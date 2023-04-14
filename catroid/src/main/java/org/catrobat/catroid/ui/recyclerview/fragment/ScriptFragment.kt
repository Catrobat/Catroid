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

import android.content.Context
import android.content.DialogInterface
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
import android.widget.ListAdapter
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.ListFragment
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
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
import org.catrobat.catroid.io.asynctask.ProjectLoader.ProjectLoadListener
import org.catrobat.catroid.io.asynctask.ProjectSaver
import org.catrobat.catroid.ui.BottomBar.hideBottomBar
import org.catrobat.catroid.ui.BottomBar.showAddButton
import org.catrobat.catroid.ui.BottomBar.showBottomBar
import org.catrobat.catroid.ui.BottomBar.showPlayButton
import org.catrobat.catroid.ui.ScriptFinder
import org.catrobat.catroid.ui.ScriptFinder.OnOpenListener
import org.catrobat.catroid.ui.ScriptFinder.OnResultFoundListener
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.UiUtils
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.controller.RecentBrickListManager
import org.catrobat.catroid.ui.dragndrop.BrickListView
import org.catrobat.catroid.ui.fragment.AddBrickFragment
import org.catrobat.catroid.ui.fragment.AddBrickFragment.Companion.newInstance
import org.catrobat.catroid.ui.fragment.AddBrickFragment.OnAddBrickListener
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment.OnCategorySelectedListener
import org.catrobat.catroid.ui.fragment.BrickSearchFragment
import org.catrobat.catroid.ui.fragment.BrickSearchFragment.Companion.newInstance
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory
import org.catrobat.catroid.ui.fragment.UserDefinedBrickListFragment
import org.catrobat.catroid.ui.fragment.UserDefinedBrickListFragment.Companion.newInstance
import org.catrobat.catroid.ui.recyclerview.adapter.BrickAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.BrickAdapter.OnBrickClickListener
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
import java.util.UUID

class ScriptFragment : ListFragment(), ActionMode.Callback, OnBrickClickListener,
    BrickAdapter.SelectionListener, OnCategorySelectedListener, OnAddBrickListener,
    ProjectLoadListener {
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(NONE, BACKPACK, COPY, DELETE, COMMENT, CATBLOCKS)
    internal annotation class ActionModeType

    @ActionModeType
    var actionModeType = NONE
        private set

    private val projectManager: ProjectManager = inject(ProjectManager::class.java).value
    private var actionMode: ActionMode? = null
    private lateinit var adapter: BrickAdapter
    private lateinit var listView: BrickListView
    private lateinit var scriptFinder: ScriptFinder
    private var currentSceneName: String? = null
    private var currentSpriteName: String? = null
    private var undoBrickPosition = 0
    private val scriptController = ScriptController()
    private val brickController = BrickController()
    private var savedListViewState: Parcelable? = null
    private var brickToFocus: Brick? = null
    private var scriptToFocus: Script? = null
    private lateinit var activity: SpriteActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            brickToFocus = bundle[BRICK_TAG] as Brick?
            scriptToFocus = bundle[SCRIPT_TAG] as Script?
        }
    }

    private var savedUserVariables: List<UserVariable>? = null
    private var savedMultiplayerVariables: List<UserVariable>? = null
    private var savedUserLists: List<UserList>? = null

    @Transient
    private var savedLocalUserVariables: List<UserVariable>? = null

    @Transient
    private var savedLocalLists: List<UserList>? = null
    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        val inflater = mode.menuInflater
        inflater.inflate(R.menu.context_menu, menu)
        when (actionModeType) {
            BACKPACK -> {
                adapter.setCheckBoxMode(BrickAdapter.SCRIPTS_ONLY)
                mode.title = getString(R.string.am_backpack)
            }
            COPY -> {
                adapter.setCheckBoxMode(BrickAdapter.CONNECTED_ONLY)
                mode.title = getString(R.string.am_copy)
            }
            DELETE -> {
                adapter.setCheckBoxMode(BrickAdapter.ALL)
                mode.title = getString(R.string.am_delete)
            }
            COMMENT -> {
                adapter.selectAllCommentedOutBricks()
                adapter.setCheckBoxMode(BrickAdapter.ALL)
                mode.title = getString(R.string.comment_in_out)
            }
            NONE -> {
                adapter.setCheckBoxMode(NONE)
                mode.finish()
                return false
            }
            CATBLOCKS -> {}
        }
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        if (item.itemId == R.id.confirm) {
            handleContextualAction(mode)
        } else {
            return false
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        resetActionModeParameters()
        adapter.clearSelection()
        showBottomBar(getActivity())
    }

    private fun handleContextualAction(mode: ActionMode) {
        if (adapter.isEmpty) {
            mode.finish()
        }
        when (actionModeType) {
            BACKPACK -> showNewScriptGroupAlert(adapter.selectedItems)
            COPY -> copy(adapter.selectedItems)
            DELETE -> showDeleteAlert(adapter.selectedItems)
            COMMENT -> toggleComments(adapter.selectedItems)
            NONE -> throw IllegalStateException("ActionModeType not set correctly")
            CATBLOCKS -> {}
        }
    }

    private fun resetActionModeParameters() {
        actionModeType = NONE
        adapter.setCheckBoxMode(BrickAdapter.NONE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = View.inflate(getActivity(), R.layout.fragment_script, null)
        listView = view.findViewById(android.R.id.list)
        val bottomListPadding = ScreenValues.SCREEN_HEIGHT / 3
        listView.setPadding(0, 0, 0, bottomListPadding)
        listView.clipToPadding = false
        activity = requireActivity() as SpriteActivity
        initScriptFinder(view)
        setHasOptionsMenu(true)
        return view
    }

    private fun initScriptFinder(view: View) {
        scriptFinder = view.findViewById(R.id.findview)
        scriptFinder.setOnResultFoundListener(object : OnResultFoundListener {
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
                textView?.text = createActionBarTitle(
                    currentProject, currentScene, currentSprite
                )
                projectManager.setCurrentSceneAndSprite(
                    currentScene.name, currentSprite.name
                )
                adapter.updateItems(currentSprite)
                adapter.notifyDataSetChanged()
                listView.smoothScrollToPosition(brickIndex)
                highlightBrickAtIndex(brickIndex)
                hideKeyboard()
            }
        })
        scriptFinder.setOnCloseListener(object : ScriptFinder.OnCloseListener {
            override fun onClose() {
                listView.cancelHighlighting()
                finishActionMode()
                if (!activity.isFinishing) {
                    activity.setCurrentSceneAndSprite(
                        projectManager.currentlyEditedScene, projectManager.currentSprite
                    )
                    activity.supportActionBar?.title = activity.createActionBarTitle()
                    activity.addTabs()
                }
                activity.findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
            }
        })
        scriptFinder.setOnOpenListener(object : OnOpenListener {
            override fun onOpen() {
                activity.removeTabs()
                activity.findViewById<View>(R.id.toolbar).visibility = View.GONE
            }
        })
    }

    private fun createActionBarTitle(
        currentProject: Project, currentScene: Scene, currentSprite: Sprite
    ): String {
        return if (currentProject.sceneList.size == 1) {
            currentSprite.name
        } else {
            currentScene.name + ": " + currentSprite.name
        }
    }

    private fun highlightBrickAtIndex(index: Int) {
        listView.brickPositionsToHighlight.clear()
        listView.brickPositionsToHighlight.add(index)
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (scriptFinder.isOpen) {
            activity.findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val currentProject = projectManager.currentProject
        val currentScene = projectManager.currentlyEditedScene
        val currentSprite = projectManager.currentSprite
        currentProject.broadcastMessageContainer.update()
        adapter = BrickAdapter(projectManager.currentSprite)
        adapter.setSelectionListener(this)
        adapter.setOnItemClickListener(this)
        listView.adapter = adapter
        listView.onItemClickListener = adapter
        listView.onItemLongClickListener = adapter
        if (currentSprite == currentScene.backgroundSprite) {
            InternToExternGenerator.setInternExternLanguageConverterMap(
                Sensors.OBJECT_NUMBER_OF_LOOKS, R.string.formula_editor_object_number_of_backgrounds
            )
        } else {
            InternToExternGenerator.setInternExternLanguageConverterMap(
                Sensors.OBJECT_NUMBER_OF_LOOKS, R.string.formula_editor_object_number_of_looks
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val project = projectManager.currentProject
        val scene = projectManager.currentlyEditedScene
        val sprite = projectManager.currentSprite
        val actionBar = (getActivity() as AppCompatActivity?)?.supportActionBar
        if (project.sceneList.size > 1) {
            actionBar?.title = scene.name + ": " + sprite.name
        } else {
            actionBar?.title = sprite.name
        }
        if (BackpackListManager.getInstance().isBackpackEmpty) {
            BackpackListManager.getInstance().loadBackpack()
        }
        showBottomBar(getActivity())
        showPlayButton(getActivity())
        showAddButton(getActivity())
        adapter.updateItems(projectManager.currentSprite)
        if (savedListViewState != null) {
            listView.onRestoreInstanceState(savedListViewState)
        }
        scrollToFocusItem()
        SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_scripts)
    }

    override fun onPause() {
        super.onPause()
        ProjectSaver(projectManager.currentProject, requireContext()).saveProjectAsync()
        savedListViewState = listView.onSaveInstanceState()
        (requireActivity() as SpriteActivity).setUndoMenuItemVisibility(false)
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
        if (listView.isCurrentlyMoving) {
            listView.highlightMovingItem()
            return true
        }
        if (listView.isCurrentlyHighlighted) {
            listView.cancelHighlighting()
        }
        when (item.itemId) {
            R.id.menu_undo -> loadProjectAfterUndoOption()
            R.id.backpack -> prepareActionMode(BACKPACK)
            R.id.copy -> prepareActionMode(COPY)
            R.id.delete -> prepareActionMode(DELETE)
            R.id.comment_in_out -> prepareActionMode(COMMENT)
            R.id.catblocks -> switchToCatblocks()
            R.id.find -> scriptFinder.open()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun notifyDataSetChanged() {
        adapter.notifyDataSetChanged()
    }

    val isCurrentlyMoving: Boolean
        get() = listView.isCurrentlyMoving

    fun highlightMovingItem() {
        listView.highlightMovingItem()
    }

    fun cancelMove() {
        listView.cancelMove()
        val sprite = projectManager.currentSprite
        adapter.updateItems(sprite)
    }

    val isCurrentlyHighlighted: Boolean
        get() = listView.isCurrentlyHighlighted

    fun cancelHighlighting() {
        listView.cancelHighlighting()
    }

    private fun showCategoryFragment() {
        val brickCategoryFragment = BrickCategoryFragment()
        brickCategoryFragment.setOnCategorySelectedListener(this)
        parentFragmentManager.beginTransaction().add(
            R.id.fragment_container,
            brickCategoryFragment,
            BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG
        ).addToBackStack(BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG).commit()
    }

    override fun onCategorySelected(category: String?) {
        val fragment: ListFragment?
        val tag: String?
        val currentFragment = parentFragmentManager.findFragmentById(R.id.fragment_container)
        if (category == context?.getString(R.string.category_user_bricks)) {
            fragment = newInstance(this)
            tag = UserDefinedBrickListFragment.USER_DEFINED_BRICK_LIST_FRAGMENT_TAG
        } else if (currentFragment is AddBrickFragment || category == context?.getString(R.string.category_search_bricks)) {
            fragment = newInstance(this, category)
            tag = BrickSearchFragment.BRICK_SEARCH_FRAGMENT_TAG
        } else {
            fragment = newInstance(category, this)
            tag = AddBrickFragment.ADD_BRICK_FRAGMENT_TAG
        }
        parentFragmentManager.beginTransaction().add(R.id.fragment_container, fragment, tag)
            .addToBackStack(null).commit()
    }

    private fun prepareActionMode(@ActionModeType type: Int) {
        if (type == BACKPACK) {
            if (BackpackListManager.getInstance().backpackedScriptGroups.isEmpty()) {
                startActionMode(BACKPACK)
            } else if (adapter.isEmpty) {
                switchToBackpack()
            } else {
                showBackpackModeChooser()
            }
        } else {
            startActionMode(type)
        }
    }

    private fun startActionMode(@ActionModeType type: Int) {
        if (adapter.isEmpty) {
            ToastUtil.showError(getActivity(), R.string.am_empty_list)
        } else {
            actionModeType = type
            actionMode = requireActivity().startActionMode(this)
            hideBottomBar(getActivity())
        }
    }

    override fun onSelectionChanged(selectedItemCnt: Int) {
        when (actionModeType) {
            BACKPACK -> actionMode?.title = getString(R.string.am_backpack) + " " + selectedItemCnt
            COPY -> actionMode?.title = getString(R.string.am_copy) + " " + selectedItemCnt
            DELETE -> actionMode?.title = getString(R.string.am_delete) + " " + selectedItemCnt
            COMMENT -> actionMode?.title =
                getString(R.string.comment_in_out) + " " + selectedItemCnt
            CATBLOCKS, NONE -> {}
        }
    }

    fun finishActionMode() {
        adapter.clearSelection()
        if (actionModeType != NONE) {
            actionMode?.finish()
        }
    }

    fun findBrickByHash(hashCode: Int): Brick? {
        return adapter.findByHash(hashCode)
    }

    fun handleAddButton() {
        if (listView.isCurrentlyHighlighted) {
            listView.cancelHighlighting()
        }
        if (listView.isCurrentlyMoving) {
            listView.highlightMovingItem()
        } else {
            (getActivity() as SpriteActivity?)?.setUndoMenuItemVisibility(false)
            showCategoryFragment()
        }
    }

    override fun addBrick(brick: Brick?) {
        try {
            if (brick?.javaClass != UserDefinedReceiverBrick::class.java && brick?.javaClass !=
                UserDefinedBrick::class.java) {
                RecentBrickListManager.getInstance().addBrick(brick?.clone())
            }
        } catch (e: CloneNotSupportedException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
        val sprite = projectManager.currentSprite
        if (brick != null) {
            addBrick(brick, sprite, adapter, listView)
        }
    }

    fun addBrick(
        brick: Brick, sprite: Sprite, brickAdapter: BrickAdapter, brickListView: BrickListView
    ) {
        if (brickAdapter.count == 0) {
            if (brick is ScriptBrick) {
                sprite.addScript(brick.getScript())
            } else {
                val script: Script = StartScript()
                script.addBrick(brick)
                sprite.addScript(script)
            }
            brickAdapter.updateItems(sprite)
        } else if (brickAdapter.count == 1 && brick !is ScriptBrick) {
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

    override fun onBrickClick(brick: Brick, position: Int) {
        if (listView.isCurrentlyHighlighted) {
            listView.cancelHighlighting()
            return
        }
        val options = getContextMenuItems(brick)
        val names: MutableList<String> = ArrayList()
        for (option in options) {
            names.add(getString(option))
        }
        val arrayAdapter: ListAdapter = UiUtils.getAlertDialogAdapterForMenuIcons(
            options, names, requireContext(), requireActivity()
        )
        val brickView = brick.getView(context)
        brick.disableSpinners()
        AlertDialog.Builder(requireContext()).setCustomTitle(brickView)
            .setAdapter(arrayAdapter, DialogInterface.OnClickListener { _, which ->
                handleContextMenuItemClick(
                    options[which], brick, position
                )
            }).show()
    }

    private fun handleContextMenuItemClick(itemId: Int, brick: Brick, position: Int) {
        showUndo(false)
        when (itemId) {
            R.string.backpack_add -> {
                val bricksToPack: List<Brick> = ArrayList()
                brick.addToFlatList(bricksToPack)
                showNewScriptGroupAlert(bricksToPack)
            }
            R.string.brick_context_dialog_copy_brick, R.string.brick_context_dialog_copy_script -> try {
                val clonedBrick = brick.allParts[0].clone()
                adapter.addItem(position, clonedBrick)
                listView.startMoving(clonedBrick)
            } catch (e: CloneNotSupportedException) {
                ToastUtil.showError(context, R.string.error_copying_brick)
                Log.e(TAG, Log.getStackTraceString(e))
            }
            R.string.brick_context_dialog_delete_brick, R.string.brick_context_dialog_delete_script -> showDeleteAlert(
                brick.allParts
            )
            R.string.brick_context_dialog_delete_definition -> showDeleteAlert(brick.allParts)
            R.string.brick_context_dialog_comment_in, R.string.brick_context_dialog_comment_in_script -> {
                for (brickPart in brick.allParts) {
                    brickPart.isCommentedOut = false
                }
                adapter.notifyDataSetChanged()
            }
            R.string.brick_context_dialog_comment_out, R.string.brick_context_dialog_comment_out_script -> {
                for (brickPart in brick.allParts) {
                    brickPart.isCommentedOut = true
                }
                adapter.notifyDataSetChanged()
            }
            R.string.brick_option_place_visually -> {
                val visualPlacementBrick = brick as VisualPlacementBrick
                visualPlacementBrick.placeVisually(
                    visualPlacementBrick.xBrickField, visualPlacementBrick.yBrickField
                )
            }
            R.string.brick_context_dialog_formula_edit_brick -> (brick as FormulaBrick).onClick(
                listView
            )
            R.string.brick_context_dialog_move_brick, R.string.brick_context_dialog_move_script, R.string.brick_context_dialog_move_definition -> onBrickLongClick(
                brick, position
            )
            R.string.brick_context_dialog_help -> openWebViewWithHelpPage(brick)
            R.string.brick_context_dialog_highlight_brick_parts -> {
                val bricksOfControlStructure = brick.allParts
                val positions: MutableList<Int> = ArrayList()
                for (brickInControlStructure in bricksOfControlStructure) {
                    positions.add(adapter.getPosition(brickInControlStructure))
                }
                listView.highlightControlStructureBricks(positions)
            }
        }
    }

    private fun openWebViewWithHelpPage(brick: Brick) {
        val sprite = projectManager.currentSprite
        val backgroundSprite = projectManager.currentlyEditedScene.backgroundSprite
        val category = CategoryBricksFactory().getBrickCategory(
            brick,
            sprite === backgroundSprite,
            requireContext()
        )
        val brickHelpUrl = brick.getHelpUrl(category)
        val intent = Intent(
            Intent.ACTION_VIEW, Uri.parse(brickHelpUrl)
        )
        startActivity(intent)
    }

    override fun onBrickLongClick(brick: Brick, position: Int): Boolean {
        showUndo(false)
        if (listView.isCurrentlyHighlighted) {
            listView.cancelHighlighting()
        } else {
            listView.startMoving(brick)
        }
        return true
    }

    private fun showBackpackModeChooser() {
        val items = arrayOf<CharSequence>(getString(R.string.pack), getString(R.string.unpack))
        AlertDialog.Builder(requireContext()).setTitle(R.string.backpack_title)
            .setItems(items, DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                when (which) {
                    0 -> startActionMode(
                        BACKPACK
                    )
                    1 -> switchToBackpack()
                }
            }).show()
    }

    private fun showNewScriptGroupAlert(selectedBricks: List<Brick>?) {
        val builder = TextInputDialog.Builder(requireContext())
        builder.setText(
            UniqueNameProvider().getUniqueName(
                getString(R.string.default_script_group_name),
                BackpackListManager.getInstance().backpackedScriptGroups
            )
        )
        builder.setHint(getString(R.string.script_group_label))
            .setTextWatcher(DuplicateInputTextWatcher(BackpackListManager.getInstance().backpackedScriptGroups as List<Nameable>))
            .setPositiveButton(getString(R.string.ok),
                               TextInputDialog.OnClickListener {
                                       _: DialogInterface?, textInput: String? ->
                                   pack(textInput,
                                        selectedBricks)
                               })
        builder.setTitle(R.string.new_group).setNegativeButton(R.string.cancel, null).show()
    }

    fun pack(name: String?, selectedBricks: List<Brick>?) {
        try {
            scriptController.pack(name, selectedBricks)
            ToastUtil.showSuccess(getActivity(), getString(R.string.packed_script_group))
            switchToBackpack()
        } catch (e: CloneNotSupportedException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
        finishActionMode()
    }

    private fun switchToBackpack() {
        val intent = Intent(getActivity(), BackpackActivity::class.java)
        intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_SCRIPTS)
        startActivity(intent)
    }

    private fun switchToCatblocks() {
        if (!BuildConfig.FEATURE_CATBLOCKS_ENABLED) {
            return
        }
        val firstVisible = listView.firstVisiblePosition
        var firstVisibleBrickID: UUID? = null
        if (listView.count > 0 && firstVisible >= 0) {
            val firstVisibleObject = listView.getItemAtPosition(firstVisible)
            if (firstVisibleObject is Brick) {
                firstVisibleBrickID = if (firstVisibleObject is ScriptBrick) {
                    firstVisibleObject.getScript().scriptId
                } else {
                    firstVisibleObject.brickID
                }
            }
        }
        SettingsFragment.setUseCatBlocks(context, true)
        val catblocksFragment = CatblocksScriptFragment(firstVisibleBrickID)
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(
            R.id.fragment_container, catblocksFragment, CatblocksScriptFragment.TAG
        )
        fragmentTransaction.commit()
    }

    private fun copy(selectedBricks: List<Brick>) {
        val sprite = projectManager.currentSprite
        brickController.copy(selectedBricks, sprite)
        adapter.updateItems(sprite)
        finishActionMode()
    }

    private fun showDeleteAlert(selectedBricks: List<Brick>) {
        if (selectedBricks.isNotEmpty() && copyProjectForUndoOption()) {
            showUndo(true)
            undoBrickPosition = adapter.getPosition(selectedBricks[0])
        }
        delete(selectedBricks)
    }

    private fun delete(selectedItems: List<Brick>) {
        val sprite = projectManager.currentSprite
        brickController.delete(selectedItems, sprite)
        adapter.updateItems(sprite)
        finishActionMode()
    }

    private fun toggleComments(selectedBricks: List<Brick>) {
        for (brick in adapter.items) {
            brick.isCommentedOut = selectedBricks.contains(brick)
        }
        finishActionMode()
    }

    fun setUndoBrickPosition(brick: Brick?) {
        undoBrickPosition = adapter.getPosition(brick)
    }

    fun copyProjectForUndoOption(): Boolean {
        val projectManager = projectManager
        val currentSprite = projectManager.currentSprite
        currentSpriteName = currentSprite.name
        currentSceneName = projectManager.currentlyEditedScene.name
        val project = projectManager.currentProject
        XstreamSerializer.getInstance().saveProject(project)
        val currentCodeFile = File(project.directory, Constants.CODE_XML_FILE_NAME)
        val undoCodeFile = File(project.directory, Constants.UNDO_CODE_XML_FILE_NAME)
        if (currentCodeFile.exists()) {
            try {
                StorageOperations.transferData(currentCodeFile, undoCodeFile)
                saveVariables()
                return true
            } catch (exception: IOException) {
                Log.e(TAG, "Copying project " + project.name + " failed.", exception)
            }
        }
        return false
    }

    fun loadProjectAfterUndoOption() {
        val project = projectManager.currentProject
        val currentCodeFile = File(project.directory, Constants.CODE_XML_FILE_NAME)
        val undoCodeFile = File(project.directory, Constants.UNDO_CODE_XML_FILE_NAME)
        if (currentCodeFile.exists()) {
            try {
                StorageOperations.transferData(undoCodeFile, currentCodeFile)
                ProjectLoader(project.directory, requireContext()).setListener(this).loadProjectAsync()
            } catch (exception: IOException) {
                Log.e(TAG, "Replacing project " + project.name + " failed.", exception)
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
        val currentSprite = projectManager.currentSprite
        val project = projectManager.currentProject
        return (project.hasUserDataChanged(
            project.userVariables, savedUserVariables
        ) || project.hasUserDataChanged(
            project.multiplayerVariables, savedMultiplayerVariables
        ) || project.hasUserDataChanged(
            project.userLists, savedUserLists
        ) || currentSprite.hasUserDataChanged(
            currentSprite.userVariables, savedLocalUserVariables
        ) || currentSprite.hasUserDataChanged(currentSprite.userLists, savedLocalLists))
    }

    private fun loadVariables() {
        val currentSprite = projectManager.currentSprite
        val project = projectManager.currentProject
        project.restoreUserDataValues(project.userVariables, savedUserVariables)
        project.restoreUserDataValues(project.multiplayerVariables, savedMultiplayerVariables)
        project.restoreUserDataValues(project.userLists, savedUserLists)
        currentSprite.restoreUserDataValues(currentSprite.userVariables, savedLocalUserVariables)
        currentSprite.restoreUserDataValues(currentSprite.userLists, savedLocalLists)
    }

    private fun refreshFragmentAfterUndo() {
        val scriptFragment = requireActivity().supportFragmentManager.findFragmentByTag(TAG) ?: return
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.detach(scriptFragment)
        fragmentTransaction.attach(scriptFragment)
        fragmentTransaction.commit()
        if (undoBrickPosition < listView.firstVisiblePosition || undoBrickPosition > listView.lastVisiblePosition) {
            listView.post(Runnable { listView.setSelection(undoBrickPosition) })
        }
    }

    fun showUndo(visible: Boolean) {
        val activity = getActivity() as SpriteActivity?
        if (activity != null) {
            (getActivity() as SpriteActivity?)?.showUndo(visible)
        }
    }

    private fun scrollToFocusItem() {
        if (scriptToFocus == null && brickToFocus == null) {
            return
        }
        var scrollToIndex = -1
        for (i in 0 until listView.adapter.count) {
            val item = listView.getItemAtPosition(i) as? Brick ?: continue
            if (brickToFocus != null && item === brickToFocus || scriptToFocus != null && item.script === scriptToFocus) {
                scrollToIndex = i
                break
            }
        }
        if (scrollToIndex == -1) {
            return
        }
        if (getActivity() != null) {
            val finalScrollToIndex = scrollToIndex
            getActivity()?.runOnUiThread(Runnable { listView.setSelection(finalScrollToIndex) })
        }
        scriptToFocus = null
        brickToFocus = null
    }

    fun closeFinder() {
        if (!scriptFinder.isClosed) {
            scriptFinder.close()
        }
    }

    companion object {
        @JvmField
        val TAG: String = ScriptFragment::class.java.simpleName
        private const val BRICK_TAG = "brickToFocus"
        private const val SCRIPT_TAG = "scriptToFocus"
        private const val NONE = 0
        private const val BACKPACK = 1
        private const val COPY = 2
        private const val DELETE = 3
        private const val COMMENT = 4
        private const val CATBLOCKS = 5
        fun newInstance(brickToFocus: Brick?): ScriptFragment {
            val scriptFragment = ScriptFragment()
            val bundle = Bundle()
            bundle.putSerializable(BRICK_TAG, brickToFocus)
            scriptFragment.arguments = bundle
            return scriptFragment
        }

        fun newInstance(scriptToFocus: Script?): ScriptFragment {
            val scriptFragment = ScriptFragment()
            val bundle = Bundle()
            bundle.putSerializable(SCRIPT_TAG, scriptToFocus)
            scriptFragment.arguments = bundle
            return scriptFragment
        }

        @VisibleForTesting
        fun getContextMenuItems(brick: Brick): List<Int> {
            val items: MutableList<Int> = ArrayList()
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
                    items.add(if (brick.isCommentedOut()) R.string.brick_context_dialog_comment_in_script else R.string.brick_context_dialog_comment_out_script)
                }
                items.add(R.string.brick_context_dialog_copy_script)
                items.add(R.string.brick_context_dialog_delete_script)
                if (brick is FormulaBrick && (brick as FormulaBrick).hasEditableFormulaField()) {
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
                items.add(if (brick.isCommentedOut) R.string.brick_context_dialog_comment_in else R.string.brick_context_dialog_comment_out)
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

    fun isFinderOpen(): Boolean {
        return scriptFinder.isOpen
    }
}
