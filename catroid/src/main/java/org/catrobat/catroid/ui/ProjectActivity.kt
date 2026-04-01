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
package org.catrobat.catroid.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.DEFAULT_IMAGE_EXTENSION
import org.catrobat.catroid.common.Constants.TMP_IMAGE_FILE_NAME
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.databinding.ActivityRecyclerBinding
import org.catrobat.catroid.databinding.DialogNewActorBinding
import org.catrobat.catroid.databinding.ProgressBarBinding
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.asynctask.ProjectSaver
import org.catrobat.catroid.merge.ImportProjectHelper
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.TestResult
import org.catrobat.catroid.ui.BottomBar.showBottomBar
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.dialogs.LegoSensorConfigInfoDialog
import org.catrobat.catroid.ui.fragment.ProjectOptionsFragment
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity
import org.catrobat.catroid.ui.recyclerview.controller.SceneController
import org.catrobat.catroid.ui.recyclerview.dialog.NewSpriteDialogFragment
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher
import org.catrobat.catroid.ui.recyclerview.fragment.RecyclerViewFragment
import org.catrobat.catroid.ui.recyclerview.fragment.SceneListFragment
import org.catrobat.catroid.ui.recyclerview.fragment.SpriteListFragment
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.Utils
import org.catrobat.catroid.utils.setVisibleOrGone
import org.catrobat.catroid.visualplacement.VisualPlacementActivity
import org.koin.android.ext.android.inject
import java.io.File

class ProjectActivity : BaseCastActivity() {

    companion object {
        const val EXTRA_FRAGMENT_POSITION = "fragmentPosition"

        const val FRAGMENT_SCENES = 0
        const val FRAGMENT_SPRITES = 1

        const val SPRITE_POCKET_PAINT = 0
        const val SPRITE_LIBRARY = 1
        const val SPRITE_FILE = 2
        const val SPRITE_CAMERA = 3
        const val SPRITE_OBJECT = 4
        const val SPRITE_FROM_LOCAL = 5
    }

    private lateinit var binding: ActivityRecyclerBinding
    private val projectManager: ProjectManager by inject()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerBinding.inflate(layoutInflater)
        if (isFinishing) {
            return
        }
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var fragmentPosition = FRAGMENT_SCENES
        val bundle = intent.extras
        if (bundle != null) {
            fragmentPosition = bundle.getInt(EXTRA_FRAGMENT_POSITION, FRAGMENT_SCENES)
        }
        loadFragment(fragmentPosition)
        showWarningForSuspiciousBricksOnce(this)
        showLegoSensorConfigInfo()
        binding.bottomBar.apply {
            buttonAdd.setOnClickListener {
                handleAddButton()
            }
            buttonPlay.setOnClickListener {
                handlePlayButton()
            }
        }
        projectManager.currentProject.checkIfSpriteNameEqualBackground(this)
    }

    private fun loadFragment(fragmentPosition: Int) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        when (fragmentPosition) {
            FRAGMENT_SCENES -> fragmentTransaction.replace(
                R.id.fragment_container,
                SceneListFragment(),
                SceneListFragment.TAG
            )
            FRAGMENT_SPRITES -> fragmentTransaction.replace(
                R.id.fragment_container,
                SpriteListFragment(),
                SpriteListFragment.TAG
            )
            else -> throw IllegalArgumentException("Invalid fragmentPosition in Activity.")
        }
        fragmentTransaction.commit()
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.fragment_container)

    fun setShowProgressBar(show: Boolean) {
        ProgressBarBinding.inflate(layoutInflater).progressBar.setVisibleOrGone(show)
        binding.fragmentContainer.setVisibleOrGone(!show)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_project_activity, menu)
        menu.findItem(R.id.from_library).isVisible = false
        menu.findItem(R.id.from_local).isVisible = false
        menu.findItem(R.id.edit).isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_scene -> handleAddSceneButton()
            R.id.project_options -> supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container, ProjectOptionsFragment(),
                    ProjectOptionsFragment.TAG
                )
                .addToBackStack(ProjectOptionsFragment.TAG)
                .commit()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        saveProject(projectManager.currentProject)
    }

    override fun onBackPressed() {
        val currentProject = projectManager.currentProject
        if (currentProject == null) {
            finish()
            return
        }
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment !is ProjectOptionsFragment) {
            saveProject(currentProject)
        }
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            showBottomBar(this)
            return
        } else {
            projectManager.resetProjectManager()
        }
        val multiSceneProject = projectManager.currentProject.sceneList.size > 1
        if (currentFragment is SpriteListFragment && multiSceneProject) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SceneListFragment(), SceneListFragment.TAG)
                .commit()
        } else {
            super.onBackPressed()
        }
    }

    private fun saveProject(currentProject: Project?) {
        if (currentProject == null) {
            Utils.setLastUsedProjectName(applicationContext, null)
            return
        }
        ProjectSaver(currentProject, applicationContext).saveProjectAsync()
        Utils.setLastUsedProjectName(applicationContext, currentProject.name)
    }

    @Suppress("ComplexMethod")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == TestResult.STAGE_ACTIVITY_TEST_SUCCESS ||
            resultCode == TestResult.STAGE_ACTIVITY_TEST_FAIL
        ) {
            val message = data?.getStringExtra(TestResult.TEST_RESULT_MESSAGE)
            ToastUtil.showError(this, message)
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val testResult = ClipData.newPlainText(
                "TestResult",
                "${projectManager.currentProject.name} $message".trimIndent()
            )
            clipboard.setPrimaryClip(testResult)
        }
        if (resultCode != RESULT_OK) {
            if (requestCode == SPRITE_POCKET_PAINT) {
                addEmptySpriteObject()
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
                uri = Uri.fromFile(File(data!!.getStringExtra(WebViewActivity.MEDIA_FILE_PATH)))
                addSpriteFromUri(uri)
            }
            SPRITE_OBJECT -> {
                uri = Uri.fromFile(File(data!!.getStringExtra(WebViewActivity.MEDIA_FILE_PATH)))
                addObjectFromUri(uri)
            }
            SPRITE_FILE -> {
                uri = data?.data
                addSpriteFromUri(uri, Constants.JPEG_IMAGE_EXTENSION)
            }
            SPRITE_CAMERA -> {
                uri = ImportFromCameraLauncher(this).getCacheCameraUri()
                addSpriteFromUri(uri, Constants.JPEG_IMAGE_EXTENSION)
            }
            SpriteActivity.REQUEST_CODE_VISUAL_PLACEMENT -> {
                val extras = data?.extras ?: return
                val xCoordinate =
                    extras.getInt(VisualPlacementActivity.X_COORDINATE_BUNDLE_ARGUMENT)
                val yCoordinate =
                    extras.getInt(VisualPlacementActivity.Y_COORDINATE_BUNDLE_ARGUMENT)
                val placeAtBrick = PlaceAtBrick(xCoordinate, yCoordinate)
                val currentSprite = projectManager.currentSprite
                val startScript = StartScript()
                currentSprite.prependScript(startScript)
                startScript.addBrick(placeAtBrick)
            }
            SPRITE_FROM_LOCAL ->
                if (data != null && data.hasExtra(ProjectListActivity.IMPORT_LOCAL_INTENT)) {
                    uri = Uri.fromFile(
                        File(data.getStringExtra(ProjectListActivity.IMPORT_LOCAL_INTENT))
                    )
                    addObjectFromUri(uri)
                }
        }
    }

    private fun addSpriteFromUri(uri: Uri?, imageExtension: String = DEFAULT_IMAGE_EXTENSION) {
        addSpriteObjectFromUri(uri, imageExtension, false)
    }

    fun addObjectFromUri(uri: Uri?) {
        addSpriteObjectFromUri(uri, Constants.CATROBAT_EXTENSION, true)
    }

    private fun addSpriteObjectFromUri(uri: Uri?, extension: String, isObject: Boolean) {
        val currentScene = projectManager.currentlyEditedScene
        val resolvedName: String
        val resolvedFileName = StorageOperations.resolveFileName(contentResolver, uri)
        val lookFileName: String
        val useDefaultSpriteName = resolvedFileName == null ||
            StorageOperations.getSanitizedFileName(resolvedFileName) == TMP_IMAGE_FILE_NAME
        if (useDefaultSpriteName) {
            resolvedName = getString(R.string.default_sprite_name)
            lookFileName = resolvedName + extension
        } else {
            resolvedName = StorageOperations.getSanitizedFileName(resolvedFileName)
            lookFileName = resolvedFileName
        }
        var lookDataName = UniqueNameProvider().getUniqueNameInNameables(
            resolvedName,
            currentScene.spriteList
        )
        var importProjectHelper: ImportProjectHelper? = null
        if (isObject) {
            importProjectHelper = ImportProjectHelper(
                lookFileName, currentScene, this
            )
            if (!importProjectHelper.checkForConflicts()) {
                return
            }
            lookDataName = UniqueNameProvider().getUniqueNameInNameables(
                importProjectHelper.getSpriteToAddName(),
                currentScene.spriteList
            )
        }
        NewSpriteDialogFragment(
            false,
            lookDataName,
            lookFileName,
            contentResolver,
            uri,
            currentFragment!!,
            isObject,
            importProjectHelper
        ).show(supportFragmentManager, NewSpriteDialogFragment.TAG)
    }

    private fun addEmptySpriteObject() {
        val currentScene = projectManager.currentlyEditedScene
        val lookDataName = UniqueNameProvider().getUniqueNameInNameables(
            getString(R.string.default_sprite_name),
            currentScene.spriteList
        )
        NewSpriteDialogFragment(
            true,
            lookDataName,
            currentFragment!!
        ).show(supportFragmentManager, NewSpriteDialogFragment.TAG)
    }

    private fun handleAddButton() {
        if (currentFragment is SceneListFragment) {
            handleAddSceneButton()
            return
        }
        if (currentFragment is SpriteListFragment) {
            handleAddSpriteButton()
        }
    }

    fun handleAddSceneButton() {
        val currentProject = projectManager.currentProject
        val defaultSceneName = UniqueNameProvider().getUniqueNameInNameables(
            resources.getString(R.string.default_scene_name),
            currentProject.sceneList
        )
        val builder = TextInputDialog.Builder(this)
        builder.setHint(getString(R.string.scene_name_label))
            .setText(defaultSceneName)
            .setTextWatcher(DuplicateInputTextWatcher(currentProject.sceneList))
            .setPositiveButton(
                getString(R.string.ok)
            ) { _: DialogInterface?, textInput: String? ->
                val scene = SceneController
                    .newSceneWithBackgroundSprite(
                        textInput,
                        getString(R.string.background),
                        currentProject
                    )
                currentProject.addScene(scene)
                if (currentFragment is SceneListFragment) {
                    (currentFragment as RecyclerViewFragment<*>).notifyDataSetChanged()
                } else {
                    val intent = Intent(this, ProjectActivity::class.java)
                    intent.putExtra(EXTRA_FRAGMENT_POSITION, FRAGMENT_SCENES)
                    startActivity(intent)
                    finish()
                }
            }
        builder.setTitle(R.string.new_scene_dialog)
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun handleAddSpriteButton() {
        val dialogNewActorBinding = DialogNewActorBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.new_sprite_dialog_title)
            .setView(dialogNewActorBinding.root)
            .create()

        dialogNewActorBinding.dialogNewLookPaintroid.setOnClickListener {
            ImportFromPocketPaintLauncher(this)
                .startActivityForResult(SPRITE_POCKET_PAINT)
            alertDialog.dismiss()
        }
        dialogNewActorBinding.dialogNewLookMediaLibrary.setOnClickListener {
            ImportFormMediaLibraryLauncher(this, FlavoredConstants.LIBRARY_LOOKS_URL)
                .startActivityForResult(SPRITE_LIBRARY)
            alertDialog.dismiss()
        }

        dialogNewActorBinding.dialogNewLookObjectLibrary.setOnClickListener {
            ImportFormMediaLibraryLauncher(this, FlavoredConstants.LIBRARY_OBJECT_URL)
                .startActivityForResult(SPRITE_OBJECT)
            alertDialog.dismiss()
        }
        dialogNewActorBinding.dialogNewLookGallery.setOnClickListener {
            ImportFromFileLauncher(this, "image/*", getString(R.string.select_look_from_gallery))
                .startActivityForResult(SPRITE_FILE)
            alertDialog.dismiss()
        }
        dialogNewActorBinding.dialogNewLookCamera.setOnClickListener {
            ImportFromCameraLauncher(this)
                .startActivityForResult(SPRITE_CAMERA)
            alertDialog.dismiss()
        }
        dialogNewActorBinding.dialogNewLookBackpack.setOnClickListener {
            if (BackpackListManager.getInstance().sprites.isNotEmpty()) {
                val intent = Intent(this, BackpackActivity::class.java)
                intent.putExtra(
                    BackpackActivity.EXTRA_FRAGMENT_POSITION,
                    BackpackActivity.FRAGMENT_SPRITES
                )
                startActivity(intent)
            } else {
                ToastUtil.showError(this, R.string.backpack_empty)
            }
            alertDialog.dismiss()
        }
        dialogNewActorBinding.dialogNewLookFromLocal.setOnClickListener {
            ImportFromLocalProjectListLauncher(
                this,
                getString(R.string.import_sprite_from_project_launcher)
            )
                .startActivityForResult(SPRITE_FROM_LOCAL)
            alertDialog.dismiss()
        }
        dialogNewActorBinding.dialogNewLookEmptyObject.setOnClickListener {
            addEmptySpriteObject()
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun handlePlayButton() {
        StageActivity.handlePlayButton(projectManager, this)
    }

    private fun showLegoSensorConfigInfo() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val nxtDialogDisabled = preferences.getBoolean(
            SettingsFragment.SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED,
            false
        )
        val ev3DialogDisabled = preferences.getBoolean(
            SettingsFragment.SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED,
            false
        )
        val resourcesSet = projectManager.currentProject.requiredResources
        if (!nxtDialogDisabled && resourcesSet.contains(Brick.BLUETOOTH_LEGO_NXT)) {
            val dialog: DialogFragment = LegoSensorConfigInfoDialog.newInstance(Constants.NXT)
            dialog.show(supportFragmentManager, LegoSensorConfigInfoDialog.DIALOG_FRAGMENT_TAG)
        }
        if (!ev3DialogDisabled && resourcesSet.contains(Brick.BLUETOOTH_LEGO_EV3)) {
            val dialog: DialogFragment = LegoSensorConfigInfoDialog.newInstance(Constants.EV3)
            dialog.show(supportFragmentManager, LegoSensorConfigInfoDialog.DIALOG_FRAGMENT_TAG)
        }
    }
}
