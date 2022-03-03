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

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.google.common.base.Charsets
import com.google.common.io.Files
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.databinding.ActivityUploadBinding
import org.catrobat.catroid.databinding.DialogReplaceApiKeyBinding
import org.catrobat.catroid.databinding.DialogUploadUnchangedProjectBinding
import org.catrobat.catroid.exceptions.ProjectException
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader
import org.catrobat.catroid.io.asynctask.ProjectLoader.ProjectLoadListener
import org.catrobat.catroid.io.asynctask.loadProject
import org.catrobat.catroid.io.asynctask.renameProject
import org.catrobat.catroid.transfers.CheckTokenTask
import org.catrobat.catroid.transfers.CheckTokenTask.TokenCheckListener
import org.catrobat.catroid.transfers.GetTagsTask
import org.catrobat.catroid.transfers.GetTagsTask.TagResponseListener
import org.catrobat.catroid.transfers.project.ResultReceiverWrapper
import org.catrobat.catroid.transfers.project.ResultReceiverWrapperInterface
import org.catrobat.catroid.ui.controller.ProjectUploadController
import org.catrobat.catroid.ui.controller.ProjectUploadController.ProjectUploadInterface
import org.catrobat.catroid.utils.FileMetaDataExtractor
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.Utils
import org.catrobat.catroid.web.ServerAuthenticationConstants
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.ArrayList
import java.util.Objects
import java.util.regex.Matcher
import java.util.regex.Pattern

private const val WEB_REQUEST_BRICK = "WebRequestBrick"
private const val BACKGROUND_REQUEST_BRICK = "BackgroundRequestBrick"
private const val LOOK_REQUEST_BRICK = "LookRequestBrick"
private const val OPEN_URL_BRICK = "OpenUrlBrick"
private const val WIKI_URL =
    "<a href='https://catrob.at/webbricks'>" + "https://catrob.at/webbricks</a>"
private const val LICENSE_TO_PLAY_URL =
    "<a href='https://catrob.at/ltp'>" + "https://catrob.at/ltp</a>"
private const val PROGRAM_NAME_START_TAG = "<programName>"
private const val PROGRAM_NAME_END_TAG = "</programName>"
private const val THUMBNAIL_SIZE = 100
private val TAG = ProjectUploadActivity::class.java.simpleName

const val PROJECT_DIR = "projectDir"
const val SIGN_IN_CODE = 42
const val NUMBER_OF_UPLOADED_PROJECTS = "number_of_uploaded_projects"

open class ProjectUploadActivity : BaseActivity(),
    ProjectLoadListener,
    TokenCheckListener,
    TagResponseListener,
    ResultReceiverWrapperInterface,
    ProjectUploadInterface {

    private lateinit var project: Project
    private lateinit var xmlFile: File
    private lateinit var xml: String
    private lateinit var originalProjectName: String
    private lateinit var backUpXml: String
    private lateinit var apiMatcher: Matcher

    private var uploadProgressDialog: AlertDialog? = null
    private val uploadResultReceiver = ResultReceiverWrapper(this, Handler())

    private val nameInputTextWatcher = NameInputTextWatcher()
    private var enableNextButton = true
    private var notesAndCreditsScreen = false

    private val projectManager: ProjectManager by inject()

    private lateinit var binding: ActivityUploadBinding
    private lateinit var dialogUploadUnchangedProjectBinding: DialogUploadUnchangedProjectBinding
    private lateinit var dialogReplaceApiKeyBinding: DialogReplaceApiKeyBinding
    private var tags: List<String> = ArrayList()

    @JvmField
    protected var projectUploadController: ProjectUploadController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setTitle(R.string.upload_project_dialog_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        notesAndCreditsScreen = false
        setShowProgressBar(true)

        loadProjectActivity()
    }

    protected open fun createProjectUploadController(): ProjectUploadController? =
        ProjectUploadController(this)

    override fun onLoadFinished(success: Boolean) {
        if (success) {
            loadProjectActivity()
        } else {
            ToastUtil.showError(this, R.string.error_load_project)
            setShowProgressBar(false)
            finish()
        }
    }

    private fun loadProjectActivity() {
        getTags()
        project = projectManager.currentProject
        projectUploadController = createProjectUploadController()
        verifyUserIdentity()
    }

    private fun onCreateView() {
        val thumbnailSize = THUMBNAIL_SIZE
        val screenshotLoader = ProjectAndSceneScreenshotLoader(
            thumbnailSize,
            thumbnailSize
        )

        screenshotLoader.loadAndShowScreenshot(
            project.directory.name,
            screenshotLoader.getScreenshotSceneName(project.directory),
            false,
            findViewById(R.id.project_image_view)
        )

        binding.projectSizeView.text =
            FileMetaDataExtractor.getSizeAsString(project.directory, this)

        if (!projectManager.isChangedProject(project)) {
            showUploadIsUnchangedDialog()
        }

        binding.inputProjectName.editText?.setText(project.name)
        binding.inputProjectDescription.editText?.setText(project.description)
        binding.inputProjectNotesAndCredits.editText?.setText(project.notesAndCredits)
        binding.inputProjectName.editText?.addTextChangedListener(nameInputTextWatcher)
        originalProjectName = project.name

        checkCodeForApiKey()
        setShowProgressBar(false)
        setNextButtonEnabled(true)
    }

    private fun showUploadIsUnchangedDialog() {
        dialogUploadUnchangedProjectBinding =
            DialogUploadUnchangedProjectBinding.inflate(layoutInflater)
        dialogUploadUnchangedProjectBinding.unchangedUploadUrl.movementMethod =
            LinkMovementMethod.getInstance()

        val warningURL = getString(
            R.string.unchanged_upload_url,
            LICENSE_TO_PLAY_URL

        )
        dialogUploadUnchangedProjectBinding.unchangedUploadUrl.text = Html.fromHtml(warningURL)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.warning)
            .setView(dialogUploadUnchangedProjectBinding.root)
            .setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int -> finish() }
            .setCancelable(false)
            .create()

        alertDialog.show()
    }

    override fun onDestroy() {
        if (uploadProgressDialog?.isShowing == true) {
            uploadProgressDialog?.dismiss()
        }
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_next, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.next).isEnabled = enableNextButton
        return true
    }

    override fun onBackPressed() {
        if (notesAndCreditsScreen) {
            setScreen(notesAndCreditsScreen)
            notesAndCreditsScreen = false
        } else {
            loadBackup()
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.next -> onNextButtonClick()
            android.R.id.home -> {
                if (notesAndCreditsScreen) {
                    setScreen(notesAndCreditsScreen)
                    notesAndCreditsScreen = false
                }
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun setShowProgressBar(show: Boolean) {
        findViewById<View>(R.id.progress_bar).visibility = if (show) View.VISIBLE else View.GONE
        binding.uploadLayout.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun setNextButtonEnabled(enabled: Boolean) {
        enableNextButton = enabled
        invalidateOptionsMenu()
    }

    private fun onNextButtonClick() {
        if (!notesAndCreditsScreen) {
            val name = binding.inputProjectName.editText?.text.toString().trim()
            val error = nameInputTextWatcher.validateName(name)

            error?.let {
                binding.inputProjectName.error = it
                return
            }

            setScreen(notesAndCreditsScreen)
            notesAndCreditsScreen = true
        } else {
            setNextButtonEnabled(false)
            setShowProgressBar(true)
            showSelectTagsDialog()
        }
    }

    private fun checkCodeForApiKey() {
        xmlFile = File(project.directory, Constants.CODE_XML_FILE_NAME)

        try {
            xml = Files.asCharSource(xmlFile, Charsets.UTF_8).read()
            backUpXml = xml
        } catch (exception: IOException) {
            Log.e(TAG, Log.getStackTraceString(exception))
        }

        xml.findAnyOf(
            listOf(
                WEB_REQUEST_BRICK, BACKGROUND_REQUEST_BRICK,
                LOOK_REQUEST_BRICK, OPEN_URL_BRICK
            )
        )?.let {
            checkApiPattern()
        }
    }

    private fun checkApiPattern() {
        val regex = "<value>.*?((?=[A-Za-z]+[0-9]|[0-9]+[A-Za-z])[A-Za-z0-9]{24,45})"
        val apiPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        apiMatcher = apiPattern.matcher(xml)
        if (apiMatcher.find()) {
            showApiReplacementDialog(Objects.requireNonNull(apiMatcher.group(1)))
        }
    }

    private fun apiKeyFound() {
        if (apiMatcher.find(apiMatcher.end())) {
            showApiReplacementDialog(Objects.requireNonNull(apiMatcher.group(1)))
        }
    }

    private fun replaceSecret(secret: String) {
        xml = xml.replace(secret.toRegex(), getString(R.string.api_replacement))
        try {
            val stream = FileOutputStream(xmlFile)
            stream.write(xml.toByteArray(StandardCharsets.UTF_8))
            stream.close()
        } catch (exception: IOException) {
            Log.e(TAG, Log.getStackTraceString(exception))
        }
        reloadProject()
        apiKeyFound()
    }

    private fun reloadProject() {
        try {
            projectManager.loadProject(project.directory)
            project = projectManager.currentProject
        } catch (exception: ProjectException) {
            Log.e(TAG, Log.getStackTraceString(exception))
        }
    }

    private fun loadBackup() {
        val currentName = project.name
        if (currentName != originalProjectName) {
            val toReplace = PROGRAM_NAME_START_TAG + originalProjectName + PROGRAM_NAME_END_TAG
            val replaceWith = PROGRAM_NAME_START_TAG + currentName + PROGRAM_NAME_END_TAG
            xmlFile = File(project.directory, Constants.CODE_XML_FILE_NAME)
            backUpXml = backUpXml.replace(toReplace, replaceWith)
        }
        try {
            val stream = FileOutputStream(xmlFile)
            stream.write(backUpXml.toByteArray(StandardCharsets.UTF_8))
            stream.close()
        } catch (exception: IOException) {
            Log.e(TAG, Log.getStackTraceString(exception))
        }
        reloadProject()
    }

    private fun showApiReplacementDialog(secret: String) {
        dialogReplaceApiKeyBinding = DialogReplaceApiKeyBinding.inflate(layoutInflater)
        dialogReplaceApiKeyBinding.replaceApiKeyWarning.movementMethod =
            LinkMovementMethod.getInstance()

        val warningURL = getString(
            R.string.api_replacement_dialog_warning,
            WIKI_URL
        )
        dialogReplaceApiKeyBinding.replaceApiKeyWarning.text = Html.fromHtml(warningURL)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.warning)
            .setView(dialogReplaceApiKeyBinding.root)
            .setPositiveButton(getString(R.string.api_replacement_dialog_accept)) { _, _ ->
                replaceSecret(
                    secret
                )
            }
            .setNegativeButton(getText(R.string.api_replacement_dialog_neutral)) { _, _ -> apiKeyFound() }
            .setNeutralButton(getText(R.string.cancel)) { _, _ ->
                loadBackup()
                finish()
            }
            .setCancelable(false)
            .create()

        alertDialog.show()
        dialogReplaceApiKeyBinding.replaceApiKey.text = secret
    }

    private fun showSelectTagsDialog() {
        val checkedTags: MutableList<String> = ArrayList()
        val availableTags = tags.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle(R.string.upload_tag_dialog_title)
            .setMultiChoiceItems(
                availableTags,
                null
            ) { dialog: DialogInterface, indexSelected: Int, isChecked: Boolean ->
                if (isChecked) {
                    if (checkedTags.size >= Constants.MAX_NUMBER_OF_CHECKED_TAGS) {
                        ToastUtil.showError(getContext(), R.string.upload_tags_maximum_error)
                        (dialog as AlertDialog).listView.setItemChecked(indexSelected, false)
                    } else {
                        checkedTags.add(availableTags[indexSelected])
                    }
                } else {
                    checkedTags.remove(availableTags[indexSelected])
                }
            }
            .setPositiveButton(getText(R.string.next)) { _, _ ->
                project.tags = checkedTags
                projectUploadController?.startUpload(
                    projectName, projectDescription,
                    notesAndCredits, project
                )
            }
            .setNegativeButton(getText(R.string.cancel)) { dialog, which ->
                Utils.invalidateLoginTokenIfUserRestricted(this)
                setShowProgressBar(false)
                setNextButtonEnabled(true)
            }
            .setCancelable(false)
            .show()
    }

    private fun setScreen(screen: Boolean) {
        if (screen) setVisibility(View.VISIBLE) else setVisibility(View.GONE)
        binding.projectNotesAndCreditsExplanation.visibility =
            if (screen) View.GONE else View.VISIBLE
        binding.inputProjectNotesAndCredits.visibility = if (screen) View.GONE else View.VISIBLE
    }

    private fun setVisibility(visibility: Int) {
        binding.projectImageView.visibility = visibility
        binding.projectSize.visibility = visibility
        binding.projectSizeView.visibility = visibility
        binding.inputProjectName.visibility = visibility
        binding.inputProjectDescription.visibility = visibility
    }

    private val projectName: String
        get() {
            val name = binding.inputProjectName.editText?.text.toString().trim { it <= ' ' }
            if (project.name != name) {
                val renamedDirectory = renameProject(project.directory, name)
                if (renamedDirectory == null) {
                    Log.e(TAG, "Creating renamed directory failed!")
                    return name
                }
                loadProject(renamedDirectory, applicationContext)
                project = projectManager.currentProject
            }
            return name
        }

    private val projectDescription: String
        get() = binding.inputProjectDescription.editText?.text.toString().trim { it <= ' ' }

    private val notesAndCredits: String
        get() = binding.inputProjectNotesAndCredits.editText?.text.toString().trim { it <= ' ' }

    fun showUploadDialog() {
        if (MainMenuActivity.surveyCampaign != null) {
            MainMenuActivity.surveyCampaign.uploadFlag = true
        }

        uploadProgressDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.upload_project_dialog_title))
            .setView(R.layout.dialog_upload_project_progress)
            .setPositiveButton(R.string.progress_upload_dialog_show_program) { _, _ ->
                loadBackup()
                projectManager.resetChangedFlag(project)
            }
            .setNegativeButton(R.string.done) { _, _ ->
                loadBackup()
                projectManager.resetChangedFlag(project)
                MainMenuActivity.surveyCampaign?.showSurvey(this)

                finish()
            }
            .setCancelable(false)
            .create()
        uploadProgressDialog?.show()
        uploadProgressDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
    }

    override fun getResultReceiverWrapper() = uploadResultReceiver

    override fun getContext() = this@ProjectUploadActivity

    override fun startUploadService(intent: Intent?) {
        showUploadDialog()
        startService(intent)
    }

    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {

        if (resultCode != Constants.UPLOAD_RESULT_RECEIVER_RESULT_CODE || resultData == null || uploadProgressDialog?.isShowing == false) {
            uploadProgressDialog?.findViewById<View>(R.id.dialog_upload_progress_progressbar)?.visibility =
                View.GONE
            uploadProgressDialog?.findViewById<View>(R.id.dialog_upload_message_failed)?.visibility =
                View.VISIBLE
            val image =
                uploadProgressDialog?.findViewById<ImageView>(R.id.dialog_upload_progress_image)
            image?.setImageResource(R.drawable.ic_upload_failed)
            image?.visibility = View.VISIBLE
            return
        }

        val projectId = resultData.getString(Constants.EXTRA_PROJECT_ID)
        val positiveButton = uploadProgressDialog?.getButton(DialogInterface.BUTTON_POSITIVE)

        positiveButton?.setOnClickListener {
            val projectUrl = Constants.SHARE_PROJECT_URL + projectId
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, projectUrl)
            startActivity(intent)
            loadBackup()
            projectManager.resetChangedFlag(project)
            finish()
        }

        positiveButton?.isEnabled = true
        uploadProgressDialog?.findViewById<View>(R.id.dialog_upload_progress_progressbar)?.visibility =
            View.GONE

        val image = uploadProgressDialog?.findViewById<ImageView>(R.id.dialog_upload_progress_image)
        image?.setImageResource(R.drawable.ic_upload_success)
        image?.visibility = View.VISIBLE

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val numberOfUploadedProjects = sharedPreferences.getInt(NUMBER_OF_UPLOADED_PROJECTS, 0) + 1
        sharedPreferences.edit()
            .putInt(NUMBER_OF_UPLOADED_PROJECTS, numberOfUploadedProjects)
            .apply()

        if (numberOfUploadedProjects != 2) {
            return
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.rating_dialog_title))
            .setView(R.layout.dialog_rate_pocketcode)
            .setPositiveButton(R.string.rating_dialog_rate_now) { _, _ ->
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$packageName")
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                } catch (e: ActivityNotFoundException) {
                    Log.e(TAG, "onReceiveResult: ", e)
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(Constants.PLAY_STORE_PAGE_LINK + packageName)
                        )
                    )
                }
            }
            .setNeutralButton(getString(R.string.rating_dialog_rate_later)) { _, _ ->
                sharedPreferences
                    .edit()
                    .putInt(NUMBER_OF_UPLOADED_PROJECTS, 0)
                    .apply()
            }
            .setNegativeButton(getString(R.string.rating_dialog_rate_never), null)
            .setCancelable(false)
            .show()
    }

    protected open fun verifyUserIdentity() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val username = sharedPreferences.getString(Constants.USERNAME, Constants.NO_USERNAME)
        val isTokenSetInPreferences =
            token != Constants.NO_TOKEN && token?.length == ServerAuthenticationConstants.TOKEN_LENGTH && token != ServerAuthenticationConstants.TOKEN_CODE_INVALID
        if (isTokenSetInPreferences) {
            CheckTokenTask(this)
                .execute(token, username)
        } else {
            startSignInWorkflow()
        }
    }

    override fun onTokenCheckComplete(tokenValid: Boolean, connectionFailed: Boolean) {
        if (connectionFailed) {
            if (!tokenValid) {
                ToastUtil.showError(this, R.string.error_session_expired)
                Utils.logoutUser(this)
                startSignInWorkflow()
            } else {
                ToastUtil.showError(this, R.string.error_internet_connection)
                return
            }
        } else if (!tokenValid) {
            startSignInWorkflow()
            return
        }
        onCreateView()
    }

    fun startSignInWorkflow() {
        startActivityForResult(Intent(this, SignInActivity::class.java), SIGN_IN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SIGN_IN_CODE) {
            if (resultCode == RESULT_OK) {
                onCreateView()
            } else {
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getTags() {
        val getTagsTask = GetTagsTask()
        getTagsTask.setOnTagsResponseListener(this)
        getTagsTask.execute()
    }

    override fun onTagsReceived(tags: List<String>) {
        this.tags = tags
    }

    inner class NameInputTextWatcher : TextWatcher {
        fun validateName(name: String): String? {
            var name = name
            if (name.isEmpty()) {
                return getString(R.string.name_empty)
            }
            name = name.trim { it <= ' ' }
            if (name.isEmpty()) {
                return getString(R.string.name_consists_of_spaces_only)
            }
            if (name == getString(R.string.default_project_name)) {
                return getString(R.string.error_upload_project_with_default_name)
            }
            return if (name != project.name &&
                FileMetaDataExtractor.getProjectNames(FlavoredConstants.DEFAULT_ROOT_DIRECTORY)
                    .contains(name)
            ) {
                getString(R.string.name_already_exists)
            } else null
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable) {
            val error = validateName(s.toString())
            binding.inputProjectName.error = error
            setNextButtonEnabled(error == null)
        }
    }
}
