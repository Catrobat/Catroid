/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

package org.catrobat.catroid.ui

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.cast.CastManager
import org.catrobat.catroid.common.Constants.DEFAULT_IMAGE_EXTENSION
import org.catrobat.catroid.common.Constants.DEFAULT_SOUND_EXTENSION
import org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME
import org.catrobat.catroid.common.Constants.JPEG_IMAGE_EXTENSION
import org.catrobat.catroid.common.Constants.MEDIA_LIBRARY_CACHE_DIRECTORY
import org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME
import org.catrobat.catroid.common.Constants.TMP_IMAGE_FILE_NAME
import org.catrobat.catroid.common.FlavoredConstants.LIBRARY_BACKGROUNDS_URL_LANDSCAPE
import org.catrobat.catroid.common.FlavoredConstants.LIBRARY_BACKGROUNDS_URL_PORTRAIT
import org.catrobat.catroid.common.FlavoredConstants.LIBRARY_LOOKS_URL
import org.catrobat.catroid.common.FlavoredConstants.LIBRARY_SOUNDS_URL
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.SharedPreferenceKeys.INDEXING_VARIABLE_PREFERENCE_KEY
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.VisualPlacementBrick
import org.catrobat.catroid.formulaeditor.UserData
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.asynctask.ProjectSaver
import org.catrobat.catroid.pocketmusic.PocketMusicActivity
import org.catrobat.catroid.soundrecorder.SoundRecorderActivity
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.TestResult
import org.catrobat.catroid.stage.TestResult.Companion.TEST_RESULT_MESSAGE
import org.catrobat.catroid.ui.WebViewActivity.MEDIA_FILE_PATH
import org.catrobat.catroid.ui.controller.RecentBrickListManager
import org.catrobat.catroid.ui.fragment.AddBrickFragment
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher
import org.catrobat.catroid.ui.recyclerview.fragment.CatblocksScriptFragment
import org.catrobat.catroid.ui.recyclerview.fragment.DataListFragment
import org.catrobat.catroid.ui.recyclerview.fragment.ListSelectorFragment
import org.catrobat.catroid.ui.recyclerview.fragment.LookListFragment
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment
import org.catrobat.catroid.ui.recyclerview.fragment.SoundListFragment
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.utils.SnackbarUtil
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.Utils
import org.catrobat.catroid.visualplacement.VisualPlacementActivity.CHANGED_COORDINATES
import org.catrobat.catroid.visualplacement.VisualPlacementActivity.X_COORDINATE_BUNDLE_ARGUMENT
import org.catrobat.catroid.visualplacement.VisualPlacementActivity.Y_COORDINATE_BUNDLE_ARGUMENT
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.io.IOException

class SpriteActivity : BaseActivity() {

    companion object {
        val TAG: String = SpriteActivity::class.java.simpleName

        const val FRAGMENT_SCRIPTS = 0
        const val FRAGMENT_LOOKS = 1
        const val FRAGMENT_SOUNDS = 2

        const val SPRITE_POCKET_PAINT = 0
        const val SPRITE_LIBRARY = 1
        const val SPRITE_FILE = 2
        const val SPRITE_CAMERA = 3

        const val BACKGROUND_POCKET_PAINT = 4
        const val BACKGROUND_LIBRARY = 5
        const val BACKGROUND_FILE = 6
        const val BACKGROUND_CAMERA = 7

        const val LOOK_POCKET_PAINT = 8
        const val LOOK_LIBRARY = 9
        const val LOOK_FILE = 10
        const val LOOK_CAMERA = 11

        const val SOUND_RECORD = 12
        const val SOUND_LIBRARY = 13
        const val SOUND_FILE = 14

        const val REQUEST_CODE_VISUAL_PLACEMENT = 2019
        const val EDIT_LOOK = 2020

        const val EXTRA_FRAGMENT_POSITION = "fragmentPosition"
        const val EXTRA_BRICK_HASH = "BRICK_HASH"

        const val EXTRA_X_TRANSFORM = "X"
        const val EXTRA_Y_TRANSFORM = "Y"
        const val EXTRA_TEXT = "TEXT"
        const val EXTRA_TEXT_COLOR = "TEXT_COLOR"
        const val EXTRA_TEXT_SIZE = "TEXT_SIZE"
        const val EXTRA_TEXT_ALIGNMENT = "TEXT_ALIGNMENT"
    }

    private var onNewSpriteListener: NewItemInterface<Sprite>? = null
    private var onNewLookListener: NewItemInterface<LookData>? = null
    private var onNewSoundListener: NewItemInterface<SoundInfo>? = null

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private var currentProject: Project? = null
    private var currentSprite: Sprite? = null
    private var currentScene: Scene? = null
    private var currentLookData: LookData? = null
    private var generatedVariableName: String? = null

    private var isUndoMenuItemVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isFinishing) {
            return
        }

        currentProject = projectManager.currentProject
        currentSprite = projectManager.currentSprite
        currentScene = projectManager.currentlyEditedScene

        setContentView(R.layout.activity_sprite)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = createActionBarTitle()

        if (RecentBrickListManager.getInstance().getRecentBricks(true).isEmpty()) {
            RecentBrickListManager.getInstance().loadRecentBricks()
        }

        val fragmentPosition =
            intent.extras?.getInt(EXTRA_FRAGMENT_POSITION, FRAGMENT_SCRIPTS) ?: FRAGMENT_SCRIPTS

        loadFragment(fragmentPosition)
        addTabLayout(fragmentPosition)
    }

    fun createActionBarTitle(): String {
        return if (currentProject?.sceneList?.size == 1) {
            currentSprite!!.name
        } else {
            "${currentScene!!.name}: ${currentSprite!!.name}"
        }
    }

    fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.fragment_container)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_script_activity, menu)
        optionsMenu = menu
        return super.onCreateOptionsMenu(menu)
    }

    fun showUndo(visible: Boolean) {
        optionsMenu.findItem(R.id.menu_undo).isVisible = visible
        if (visible) {
            projectManager.changedProject(currentProject!!.name)
        }
    }

    fun checkForChange() {
        if (optionsMenu.findItem(R.id.menu_undo)?.isVisible == true) {
            projectManager.changedProject(currentProject!!.name)
        } else {
            projectManager.resetChangedFlag(currentProject)
        }
    }

    fun setUndoMenuItemVisibility(isVisible: Boolean) {
        isUndoMenuItemVisible = isVisible
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        when (getCurrentFragment()) {
            is ScriptFragment -> {
                menu.findItem(R.id.comment_in_out).isVisible = true
                showUndo(isUndoMenuItemVisible)
            }
            is LookListFragment -> {
                showUndo(isUndoMenuItemVisible)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val isDragAndDropActiveInFragment =
            getCurrentFragment() is ScriptFragment && (getCurrentFragment() as ScriptFragment).isCurrentlyMoving

        if (item.itemId == android.R.id.home && isDragAndDropActiveInFragment) {
            (getCurrentFragment() as ScriptFragment).highlightMovingItem()
            return true
        }

        if (item.itemId == R.id.menu_undo && getCurrentFragment() is LookListFragment) {
            setUndoMenuItemVisibility(false)
            showUndo(isUndoMenuItemVisible)
            val fragment = getCurrentFragment()
            if (fragment is LookListFragment && !fragment.undo() && currentLookData != null) {
                fragment.deleteItem(currentLookData)
                currentLookData?.dispose()
                currentLookData = null
            }
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        saveProject()
        RecentBrickListManager.getInstance().saveRecentBrickList()
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        saveProject()

        when (val currentFragment = getCurrentFragment()) {
            is ScriptFragment -> {
                when {
                    currentFragment.isCurrentlyMoving -> {
                        currentFragment.cancelMove()
                        return
                    }
                    currentFragment.isFinderOpen -> {
                        currentFragment.closeFinder()
                        return
                    }
                    currentFragment.isCurrentlyHighlighted -> {
                        currentFragment.cancelHighlighting()
                        return
                    }
                }
            }
            is FormulaEditorFragment -> {
                currentFragment.exitFormulaEditorFragment()
                return
            }
            else -> {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    when (currentFragment) {
                        is BrickCategoryFragment -> SnackbarUtil.showHintSnackbar(
                            this,
                            R.string.hint_scripts
                        )
                        is AddBrickFragment -> SnackbarUtil.showHintSnackbar(
                            this,
                            R.string.hint_category
                        )
                    }
                    supportFragmentManager.popBackStack()
                    return
                }
            }
        }
        super.onBackPressed()
    }

    private fun saveProject() {
        currentProject = projectManager.currentProject
        ProjectSaver(currentProject!!, applicationContext).saveProjectAsync()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == TestResult.STAGE_ACTIVITY_TEST_SUCCESS || resultCode == TestResult.STAGE_ACTIVITY_TEST_FAIL) {
            val message = data?.getStringExtra(TEST_RESULT_MESSAGE)
            ToastUtil.showError(this, message.orEmpty())
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val testResult = ClipData.newPlainText(
                "TestResult", projectManager.currentProject!!
                    .name + "\n" + message.orEmpty()
            )
            clipboard.setPrimaryClip(testResult)
        }

        if (resultCode != RESULT_OK) {
            if (SettingsFragment.isCastSharedPreferenceEnabled(this) && projectManager.currentProject?.isCastProject == true && !CastManager.getInstance().isConnected) {
                CastManager.getInstance().openDeviceSelectorOrDisconnectDialog(this)
                return
            }
            return
        }

        val uri: Uri?

        when (requestCode) {
            SPRITE_POCKET_PAINT -> {
                uri = ImportFromPocketPaintLauncher(this).getPocketPaintCacheUri()
                addSpriteFromUri(uri)
            }
            SPRITE_LIBRARY -> {
                uri = Uri.fromFile(File(data?.getStringExtra(MEDIA_FILE_PATH).orEmpty()))
                addSpriteFromUri(uri)
            }
            SPRITE_FILE -> {
                uri = data?.data
                addSpriteFromUri(uri, JPEG_IMAGE_EXTENSION)
            }
            SPRITE_CAMERA -> {
                uri = ImportFromCameraLauncher(this).getCacheCameraUri()
                addSpriteFromUri(uri, JPEG_IMAGE_EXTENSION)
            }
            BACKGROUND_POCKET_PAINT -> {
                uri = ImportFromPocketPaintLauncher(this).getPocketPaintCacheUri()
                addBackgroundFromUri(uri)
            }
            BACKGROUND_LIBRARY -> {
                uri = Uri.fromFile(File(data?.getStringExtra(MEDIA_FILE_PATH).orEmpty()))
                addBackgroundFromUri(uri)
            }
            BACKGROUND_FILE -> {
                uri = data?.data
                addBackgroundFromUri(uri, JPEG_IMAGE_EXTENSION)
            }
            BACKGROUND_CAMERA -> {
                uri = ImportFromCameraLauncher(this).getCacheCameraUri()
                addBackgroundFromUri(uri, JPEG_IMAGE_EXTENSION)
            }
            LOOK_POCKET_PAINT -> {
                uri = ImportFromPocketPaintLauncher(this).getPocketPaintCacheUri()
                addLookFromUri(uri)
                setUndoMenuItemVisibility(true)
            }
            LOOK_LIBRARY -> {
                uri = Uri.fromFile(File(data?.getStringExtra(MEDIA_FILE_PATH).orEmpty()))
                addLookFromUri(uri)
                setUndoMenuItemVisibility(true)
            }
            LOOK_FILE -> {
                uri = data?.data
                addLookFromUri(uri, JPEG_IMAGE_EXTENSION)
                setUndoMenuItemVisibility(true)
            }
            LOOK_CAMERA -> {
                uri = ImportFromCameraLauncher(this).getCacheCameraUri()
                addLookFromUri(uri, JPEG_IMAGE_EXTENSION)
                setUndoMenuItemVisibility(true)
            }
            SOUND_RECORD, SOUND_FILE -> {
                uri = data?.data
                addSoundFromUri(uri)
            }
            SOUND_LIBRARY -> {
                uri = Uri.fromFile(File(data?.getStringExtra(MEDIA_FILE_PATH).orEmpty()))
                addSoundFromUri(uri)
            }
            REQUEST_CODE_VISUAL_PLACEMENT -> {
                val extras = data?.extras ?: return

                val xCoordinate = extras.getInt(X_COORDINATE_BUNDLE_ARGUMENT)
                val yCoordinate = extras.getInt(Y_COORDINATE_BUNDLE_ARGUMENT)
                val brickHash = extras.getInt(EXTRA_BRICK_HASH)

                val fragment = getCurrentFragment()
                var brick: Brick? = null

                if (fragment is ScriptFragment) {
                    brick = fragment.findBrickByHash(brickHash)
                } else if (fragment is FormulaEditorFragment) {
                    brick = fragment.formulaBrick
                }

                if (brick != null) {
                    (brick as? VisualPlacementBrick)?.setCoordinates(xCoordinate, yCoordinate)
                    if (fragment is FormulaEditorFragment) {
                        fragment.updateFragmentAfterVisualPlacement()
                    }
                }

                setUndoMenuItemVisibility(extras.getBoolean(CHANGED_COORDINATES))
            }
        }
    }

    fun registerOnNewSpriteListener(listener: NewItemInterface<Sprite>) {
        onNewSpriteListener = listener
    }

    fun registerOnNewLookListener(listener: NewItemInterface<LookData>) {
        onNewLookListener = listener
    }

    fun registerOnNewSoundListener(listener: NewItemInterface<SoundInfo>) {
        onNewSoundListener = listener
    }

    private fun addSpriteFromUri(uri: Uri?, imageExtension: String = DEFAULT_IMAGE_EXTENSION) {
        val resolvedFileName = StorageOperations.resolveFileName(contentResolver, uri)
        val useDefaultSpriteName =
            resolvedFileName == null || StorageOperations.getSanitizedFileName(resolvedFileName) == TMP_IMAGE_FILE_NAME

        val resolvedName: String
        val lookFileName: String

        if (useDefaultSpriteName) {
            resolvedName = getString(R.string.default_sprite_name)
            lookFileName = resolvedName + imageExtension
        } else {
            resolvedName = StorageOperations.getSanitizedFileName(resolvedFileName)
            lookFileName = resolvedFileName
        }

        val lookDataName = UniqueNameProvider().getUniqueNameInNameables(
            resolvedName,
            currentScene?.spriteList.orEmpty()
        )

        val builder = TextInputDialog.Builder(this)

        builder.setHint(getString(R.string.sprite_name_label))
            .setText(lookDataName)
            .setTextWatcher(DuplicateInputTextWatcher(currentScene?.spriteList.orEmpty()))
            .setPositiveButton(getString(R.string.ok)) { _: DialogInterface, textInput: String ->
                val sprite = Sprite(textInput)
                currentScene!!.addSprite(sprite)
                try {
                    val imageDirectory = File(currentScene!!.directory, IMAGE_DIRECTORY_NAME)
                    val file = StorageOperations.copyUriToDir(
                        contentResolver,
                        uri,
                        imageDirectory,
                        lookFileName
                    )
                    Utils.removeExifData(imageDirectory, lookFileName)
                    val lookData = LookData(textInput, file)
                    sprite.lookList.add(lookData)
                    lookData.collisionInformation.calculate()
                } catch (e: IOException) {
                    Log.e(TAG, Log.getStackTraceString(e))
                }
                onNewSpriteListener?.addItem(sprite)
                (getCurrentFragment() as? ScriptFragment)?.notifyDataSetChanged()
            }

        builder.setTitle(R.string.new_sprite_dialog_title)
            .setNegativeButton(R.string.cancel) { _, _ ->
                try {
                    if (MEDIA_LIBRARY_CACHE_DIRECTORY.exists()) {
                        StorageOperations.deleteDir(MEDIA_LIBRARY_CACHE_DIRECTORY)
                    }
                } catch (e: IOException) {
                    Log.e(TAG, Log.getStackTraceString(e))
                }
            }
            .show()
    }

    private fun addBackgroundFromUri(uri: Uri?, imageExtension: String = DEFAULT_IMAGE_EXTENSION) {
        val resolvedFileName = StorageOperations.resolveFileName(contentResolver, uri)
        val useSpriteName =
            resolvedFileName == null || StorageOperations.getSanitizedFileName(resolvedFileName) == TMP_IMAGE_FILE_NAME

        var lookDataName: String
        val lookFileName: String

        if (useSpriteName) {
            lookDataName = currentSprite!!.name
            lookFileName = lookDataName + imageExtension
        } else {
            lookDataName = StorageOperations.getSanitizedFileName(resolvedFileName)
            lookFileName = resolvedFileName
        }

        lookDataName = UniqueNameProvider().getUniqueNameInNameables(
            lookDataName,
            currentSprite?.lookList.orEmpty()
        )

        try {
            val imageDirectory = File(currentScene?.directory, IMAGE_DIRECTORY_NAME)
            val file =
                StorageOperations.copyUriToDir(contentResolver, uri, imageDirectory, lookFileName)
            Utils.removeExifData(imageDirectory, lookFileName)
            val look = LookData(lookDataName, file)
            currentSprite?.lookList?.add(look)
            look.collisionInformation.calculate()
            onNewLookListener?.addItem(look)
        } catch (e: IOException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
    }

    private fun addLookFromUri(uri: Uri?, imageExtension: String = DEFAULT_IMAGE_EXTENSION) {
        val resolvedFileName = StorageOperations.resolveFileName(contentResolver, uri)
        val useSpriteName =
            resolvedFileName == null || StorageOperations.getSanitizedFileName(resolvedFileName) == TMP_IMAGE_FILE_NAME

        var lookDataName: String
        val lookFileName: String

        if (useSpriteName) {
            lookDataName = currentSprite!!.name
            lookFileName = lookDataName + imageExtension
        } else {
            lookDataName = StorageOperations.getSanitizedFileName(resolvedFileName)
            lookFileName = resolvedFileName
        }

        lookDataName = UniqueNameProvider().getUniqueNameInNameables(
            lookDataName,
            currentSprite?.lookList.orEmpty()
        )

        try {
            val imageDirectory = File(currentScene?.directory, IMAGE_DIRECTORY_NAME)
            val file =
                StorageOperations.copyUriToDir(contentResolver, uri, imageDirectory, lookFileName)
            Utils.removeExifData(imageDirectory, lookFileName)
            val look = LookData(lookDataName, file)
            this.currentLookData = look
            currentSprite?.lookList?.add(look)
            look.collisionInformation.calculate()
            onNewLookListener?.addItem(look)
        } catch (e: IOException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
    }

    private fun addSoundFromUri(uri: Uri?) {
        val resolvedFileName = StorageOperations.resolveFileName(contentResolver, uri)
        val useSpriteName = resolvedFileName == null

        var soundInfoName: String
        val soundFileName: String

        if (useSpriteName) {
            soundInfoName = currentSprite!!.name
            soundFileName = soundInfoName + DEFAULT_SOUND_EXTENSION
        } else {
            soundInfoName = StorageOperations.getSanitizedFileName(resolvedFileName)
            soundFileName = resolvedFileName
        }

        soundInfoName = UniqueNameProvider().getUniqueNameInNameables(
            soundInfoName,
            currentSprite?.soundList.orEmpty()
        )

        try {
            val soundDirectory = File(currentScene?.directory, SOUND_DIRECTORY_NAME)
            val file =
                StorageOperations.copyUriToDir(contentResolver, uri, soundDirectory, soundFileName)
            val sound = SoundInfo(soundInfoName, file)
            currentSprite?.soundList?.add(sound)
            onNewSoundListener?.addItem(sound)
        } catch (e: IOException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun handleAiAssistButton(view: View) {
        Log.d(TAG, "Here a Flutter module will be called in the future.")
    }

    @Suppress("UNUSED_PARAMETER")
    fun handleAddButton(view: View) {
        getCurrentFragment()?.let {
            when (it) {
                is ScriptFragment -> it.handleAddButton()
                is CatblocksScriptFragment -> it.handleAddButton()
                is DataListFragment -> handleAddUserDataButton()
                is LookListFragment -> handleAddLookButton()
                is SoundListFragment -> handleAddSoundButton()
                is ListSelectorFragment -> handleAddUserListButton()
            }
        }
    }

    fun handleAddSpriteButton() {
        val root = View.inflate(this, R.layout.dialog_new_look, null)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.new_sprite_dialog_title)
            .setView(root)
            .create()

        root.findViewById<View>(R.id.dialog_new_look_paintroid).setOnClickListener {
            ImportFromPocketPaintLauncher(this)
                .startActivityForResult(SPRITE_POCKET_PAINT)
            alertDialog.dismiss()
        }
        root.findViewById<View>(R.id.dialog_new_look_media_library).setOnClickListener {
            ImportFormMediaLibraryLauncher(this, LIBRARY_LOOKS_URL)
                .startActivityForResult(SPRITE_LIBRARY)
            alertDialog.dismiss()
        }
        root.findViewById<View>(R.id.dialog_new_look_gallery).setOnClickListener {
            ImportFromFileLauncher(this, "image/*", getString(R.string.select_look_from_gallery))
                .startActivityForResult(SPRITE_FILE)
            alertDialog.dismiss()
        }
        root.findViewById<View>(R.id.dialog_new_look_camera).setOnClickListener {
            ImportFromCameraLauncher(this)
                .startActivityForResult(SPRITE_CAMERA)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    fun handleAddBackgroundButton() {
        val root = View.inflate(this, R.layout.dialog_new_look, null)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.new_look_dialog_title)
            .setView(root)
            .create()

        val mediaLibraryUrl: String = if (projectManager.isCurrentProjectLandscapeMode) {
            LIBRARY_BACKGROUNDS_URL_LANDSCAPE
        } else {
            LIBRARY_BACKGROUNDS_URL_PORTRAIT
        }

        root.findViewById<View>(R.id.dialog_new_look_paintroid).setOnClickListener {
            ImportFromPocketPaintLauncher(this)
                .startActivityForResult(BACKGROUND_POCKET_PAINT)
            alertDialog.dismiss()
        }
        root.findViewById<View>(R.id.dialog_new_look_media_library).setOnClickListener {
            ImportFormMediaLibraryLauncher(this, mediaLibraryUrl)
                .startActivityForResult(BACKGROUND_LIBRARY)
            alertDialog.dismiss()
        }
        root.findViewById<View>(R.id.dialog_new_look_gallery).setOnClickListener {
            ImportFromFileLauncher(this, "image/*", getString(R.string.select_look_from_gallery))
                .startActivityForResult(BACKGROUND_FILE)
            alertDialog.dismiss()
        }
        root.findViewById<View>(R.id.dialog_new_look_camera).setOnClickListener {
            ImportFromCameraLauncher(this)
                .startActivityForResult(BACKGROUND_CAMERA)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    fun handleAddLookButton() {
        val root = View.inflate(this, R.layout.dialog_new_look, null)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.new_look_dialog_title)
            .setView(root)
            .create()

        val mediaLibraryUrl: String = if (currentSprite == currentScene?.backgroundSprite) {
            if (projectManager.isCurrentProjectLandscapeMode) {
                LIBRARY_BACKGROUNDS_URL_LANDSCAPE
            } else {
                LIBRARY_BACKGROUNDS_URL_PORTRAIT
            }
        } else {
            LIBRARY_LOOKS_URL
        }

        root.findViewById<View>(R.id.dialog_new_look_paintroid).setOnClickListener {
            ImportFromPocketPaintLauncher(this)
                .startActivityForResult(LOOK_POCKET_PAINT)
            alertDialog.dismiss()
        }
        root.findViewById<View>(R.id.dialog_new_look_media_library).setOnClickListener {
            ImportFormMediaLibraryLauncher(this, mediaLibraryUrl)
                .startActivityForResult(LOOK_LIBRARY)
            alertDialog.dismiss()
        }
        root.findViewById<View>(R.id.dialog_new_look_gallery).setOnClickListener {
            ImportFromFileLauncher(this, "image/*", getString(R.string.select_look_from_gallery))
                .startActivityForResult(LOOK_FILE)
            alertDialog.dismiss()
        }
        root.findViewById<View>(R.id.dialog_new_look_camera).setOnClickListener {
            ImportFromCameraLauncher(this)
                .startActivityForResult(LOOK_CAMERA)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    fun handleAddSoundButton() {
        val root = View.inflate(this, R.layout.dialog_new_sound, null)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.new_sound_dialog_title)
            .setView(root)
            .create()

        root.findViewById<View>(R.id.dialog_new_sound_recorder).setOnClickListener {
            startActivityForResult(Intent(this, SoundRecorderActivity::class.java), SOUND_RECORD)
            alertDialog.dismiss()
        }
        root.findViewById<View>(R.id.dialog_new_sound_media_library).setOnClickListener {
            ImportFormMediaLibraryLauncher(this, LIBRARY_SOUNDS_URL)
                .startActivityForResult(SOUND_LIBRARY)
            alertDialog.dismiss()
        }
        root.findViewById<View>(R.id.dialog_new_sound_gallery).setOnClickListener {
            ImportFromFileLauncher(this, "audio/*", getString(R.string.sound_select_source))
                .startActivityForResult(SOUND_FILE)
            alertDialog.dismiss()
        }

        if (BuildConfig.FEATURE_POCKETMUSIC_ENABLED) {
            root.findViewById<View>(R.id.dialog_new_sound_pocketmusic).visibility = View.VISIBLE
            root.findViewById<View>(R.id.dialog_new_sound_pocketmusic).setOnClickListener {
                startActivity(Intent(this, PocketMusicActivity::class.java))
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }

    @Suppress("UNCHECKED_CAST")
    private fun handleAddUserDataButton() {
        val view = View.inflate(this, R.layout.dialog_new_user_data, null)

        val makeListCheckBox = view.findViewById<CheckBox>(R.id.make_list)
        makeListCheckBox.visibility = View.VISIBLE

        val multiplayerRadioButton = view.findViewById<RadioButton>(R.id.multiplayer)
        if (SettingsFragment.isMultiplayerVariablesPreferenceEnabled(applicationContext)) {
            multiplayerRadioButton.visibility = View.VISIBLE
            multiplayerRadioButton.setOnCheckedChangeListener { _, isChecked ->
                makeListCheckBox.isEnabled = !isChecked
            }
        }

        val addToProjectUserDataRadioButton = view.findViewById<RadioButton>(R.id.global)

        val variables = mutableListOf<UserData<Any>>()

        currentSprite = projectManager.currentSprite
        currentProject = projectManager.currentProject

        variables.addAll(currentProject?.userVariables.orEmpty())
        variables.addAll(currentProject?.multiplayerVariables.orEmpty())
        variables.addAll(currentSprite?.userVariables.orEmpty())

        val lists = mutableListOf<UserData<Any>>()
        lists.addAll(currentProject?.userLists.orEmpty() as List<UserData<Any>>)
        lists.addAll(currentSprite?.userLists.orEmpty() as List<UserData<Any>>)

        val textWatcher = DuplicateInputTextWatcher(variables)
        val builder = TextInputDialog.Builder(this)
        val uniqueVariableNameProvider =
            builder.createUniqueNameProvider(R.string.default_variable_name)
        val uniqueListNameProvider = builder.createUniqueNameProvider(R.string.default_list_name)
        generatedVariableName = uniqueVariableNameProvider.getUniqueName(
            getString(R.string.default_variable_name),
            null
        )
        builder.setTextWatcher(textWatcher)
            .setText(generatedVariableName)
            .setPositiveButton(
                getString(R.string.ok),
                TextInputDialog.OnClickListener { _, textInput ->
                    val addToProjectUserData = addToProjectUserDataRadioButton.isChecked
                    val addToMultiplayerData = multiplayerRadioButton.isChecked

                    getSharedPreferences(
                        packageName + "_preferences",
                        Context.MODE_PRIVATE
                    ).edit()
                        .putBoolean(INDEXING_VARIABLE_PREFERENCE_KEY, false)
                        .apply()

                    if (makeListCheckBox.isChecked) {
                        val userList = UserList(textInput)
                        if (addToProjectUserData) {
                            currentProject?.addUserList(userList)
                        } else {
                            currentSprite?.addUserList(userList)
                        }
                    } else {
                        val userVariable = UserVariable(textInput)
                        if (addToMultiplayerData) {
                            currentProject?.addMultiplayerVariable(userVariable)
                        } else if (addToProjectUserData) {
                            currentProject?.addUserVariable(userVariable)
                        } else {
                            currentSprite?.addUserVariable(userVariable)
                        }
                    }

                    getCurrentFragment()?.let { fragment ->
                        if (fragment is DataListFragment) {
                            fragment.notifyDataSetChanged()
                            fragment.indexAndSort()
                        }
                    }
                })

        val alertDialog = builder.setTitle(R.string.formula_editor_variable_dialog_title)
            .setView(view)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()

        makeListCheckBox.setOnCheckedChangeListener { _, checked ->
            val textInputEditText =
                alertDialog.findViewById<TextInputEditText>(R.id.input_edit_text)
            val currentName = textInputEditText?.text.toString()
            if (checked) {
                alertDialog.setTitle(getString(R.string.formula_editor_list_dialog_title))
                textWatcher.setOriginalScope(lists)
                if (currentName == generatedVariableName) {
                    generatedVariableName = uniqueListNameProvider.getUniqueName(
                        getString(R.string.default_list_name),
                        null
                    )
                    textInputEditText?.setText(generatedVariableName)
                }
            } else {
                alertDialog.setTitle(getString(R.string.formula_editor_variable_dialog_title))
                textWatcher.setOriginalScope(variables)
                if (currentName == generatedVariableName) {
                    generatedVariableName =
                        uniqueVariableNameProvider.getUniqueName(
                            getString(R.string.default_variable_name),
                            null
                        )
                    textInputEditText?.setText(generatedVariableName)
                }
            }
            multiplayerRadioButton.isEnabled = !checked
        }

        alertDialog.show()
    }

    private fun handleAddUserListButton() {
        val view = View.inflate(this, R.layout.dialog_new_user_data, null)
        val addToProjectUserDataRadioButton = view.findViewById<RadioButton>(R.id.global)

        val lists = mutableListOf<UserData<List<Any>>>()
        lists.addAll(currentProject?.userLists.orEmpty())
        lists.addAll(currentSprite?.userLists.orEmpty())

        val textWatcher = DuplicateInputTextWatcher(lists)

        val builder = TextInputDialog.Builder(this)
            .setTextWatcher(textWatcher)
            .setPositiveButton(
                getString(R.string.ok),
                TextInputDialog.OnClickListener { _, textInput ->
                    val addToProjectUserData = addToProjectUserDataRadioButton.isChecked

                    val userList = UserList(textInput)
                    if (addToProjectUserData) {
                        currentProject?.addUserList(userList)
                    } else {
                        currentSprite?.addUserList(userList)
                    }

                    getCurrentFragment()?.let { fragment ->
                        if (fragment is ListSelectorFragment) {
                            fragment.notifyDataSetChanged()
                        }
                    }
                })

        val alertDialog = builder.setTitle(R.string.formula_editor_list_dialog_title)
            .setView(view)
            .create()

        alertDialog.show()
    }

    fun handlePlayButton() {
        val currentFragment = getCurrentFragment()
        if (currentFragment is ScriptFragment) {
            if (currentFragment.isCurrentlyHighlighted) {
                currentFragment.cancelHighlighting()
            }
            if (currentFragment.isCurrentlyMoving) {
                currentFragment.highlightMovingItem()
                return
            }
        }

        while (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
        StageActivity.handlePlayButton(projectManager, this)
    }

    @SuppressLint("AppCompatMethod")
    override fun startActionMode(callback: ActionMode.Callback?): ActionMode? {
        val fragment = getCurrentFragment()
        if (fragment.isFragmentWithTablayout()) {
            this.removeTabLayout()
        }
        return super.startActionMode(callback)
    }

    override fun onActionModeFinished(mode: ActionMode?) {
        val fragment = getCurrentFragment()
        if (fragment.isFragmentWithTablayout() && (fragment !is ScriptFragment || !fragment.isFinderOpen)) {
            addTabLayout(fragment.getTabPositionInSpriteActivity())
        }
        super.onActionModeFinished(mode)
    }

    fun setCurrentSprite(sprite: Sprite?) {
        currentSprite = sprite
    }

    fun setCurrentSceneAndSprite(scene: Scene?, sprite: Sprite?) {
        currentScene = scene
        currentSprite = sprite
    }

    fun removeTabs() {
        removeTabLayout()
    }

    fun addTabs() {
        addTabLayout(getCurrentFragment().getTabPositionInSpriteActivity())
    }
}

