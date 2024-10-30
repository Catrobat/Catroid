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
package org.catrobat.catroid.ui

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.cast.CastManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.common.FlavoredConstants.CATROBAT_HELP_URL
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.common.Survey
import org.catrobat.catroid.databinding.ActivityMainMenuBinding
import org.catrobat.catroid.databinding.ActivityMainMenuSplashscreenBinding
import org.catrobat.catroid.databinding.DeclinedTermsOfUseAndServiceAlertViewBinding
import org.catrobat.catroid.databinding.PrivacyPolicyViewBinding
import org.catrobat.catroid.databinding.ProgressBarBinding
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.io.asynctask.ProjectLoader
import org.catrobat.catroid.io.asynctask.ProjectLoader.ProjectLoadListener
import org.catrobat.catroid.io.asynctask.ProjectSaver
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.ui.dialogs.TermsOfUseDialogFragment
import org.catrobat.catroid.ui.recyclerview.dialog.AboutDialogFragment
import org.catrobat.catroid.ui.recyclerview.fragment.MainMenuFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.utils.FileMetaDataExtractor
import org.catrobat.catroid.utils.ScreenValueHandler
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.Utils
import org.catrobat.catroid.utils.setVisibleOrGone
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException

private const val SDK_VERSION = 24

class MainMenuActivity : BaseCastActivity(), ProjectLoadListener {

    private lateinit var privacyPolicyBinding: PrivacyPolicyViewBinding
    private lateinit var declinedTermsOfUseViewBinding: DeclinedTermsOfUseAndServiceAlertViewBinding
    private lateinit var mainMenuBinding: ActivityMainMenuBinding
    private val projectManager: ProjectManager by inject()
    private var oldPrivacyPolicy = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SettingsFragment.setToChosenLanguage(this)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, true)
        PreferenceManager.setDefaultValues(this, R.xml.nxt_preferences, true)
        PreferenceManager.setDefaultValues(this, R.xml.ev3_preferences, true)
        ScreenValueHandler.updateScreenWidthAndHeight(this)

        oldPrivacyPolicy = PreferenceManager.getDefaultSharedPreferences(this)
            .getInt(SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION, 0)

        loadContent()

        if (oldPrivacyPolicy != Constants.CATROBAT_TERMS_OF_USE_ACCEPTED) {
            showTermsOfUseDialog()
        }

        surveyCampaign = Survey(this)
        surveyCampaign?.showSurvey(this)
    }

    private fun showTermsOfUseDialog() {
        privacyPolicyBinding = PrivacyPolicyViewBinding.inflate(layoutInflater)
        val view = privacyPolicyBinding.root
        val termsOfUseUrlTextView = privacyPolicyBinding.dialogPrivacyPolicyTextViewUrl

        termsOfUseUrlTextView.movementMethod = LinkMovementMethod.getInstance()

        val termsOfUseUrlStringText = getString(R.string.main_menu_terms_of_use)
        val termsOfUseUrl = getString(
            R.string.terms_of_use_link_template,
            Constants.CATROBAT_TERMS_OF_USE_URL +
                Constants.CATROBAT_TERMS_OF_USE_TOKEN_FLAVOR_URL + BuildConfig.FLAVOR +
                Constants.CATROBAT_TERMS_OF_USE_TOKEN_VERSION_URL + BuildConfig.VERSION_CODE,
            termsOfUseUrlStringText
        )

        termsOfUseUrlTextView.text = if (Build.VERSION.SDK_INT >= SDK_VERSION) {
            Html.fromHtml(termsOfUseUrl, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(termsOfUseUrl)
        }

        AlertDialog.Builder(this)
            .setNegativeButton(R.string.decline) { _, _ -> handleDeclinedPrivacyPolicyButton() }
            .setPositiveButton(R.string.accept) { _, _ -> handleAgreedToPrivacyPolicyButton() }
            .setCancelable(false)
            .setOnKeyListener { _, keyCode: Int, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish()
                    return@setOnKeyListener true
                }
                false
            }
            .setView(view)
            .show()
    }

    fun handleAgreedToPrivacyPolicyButton() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putInt(
                SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION,
                Constants.CATROBAT_TERMS_OF_USE_ACCEPTED
            )
            .apply()
        if (BuildConfig.FEATURE_APK_GENERATOR_ENABLED) {
            prepareStandaloneProject()
        }
    }

    fun handleDeclinedPrivacyPolicyButton() {
        declinedTermsOfUseViewBinding =
            DeclinedTermsOfUseAndServiceAlertViewBinding.inflate(layoutInflater)
        val dialogView = declinedTermsOfUseViewBinding.root

        val linkString = getString(
            R.string.about_link_template,
            Constants.BASE_APP_URL_HTTPS,
            getString(R.string.share_website_text)
        )

        val linkTextView = declinedTermsOfUseViewBinding.shareWebsiteView
        linkTextView.movementMethod = LinkMovementMethod.getInstance()
        linkTextView.text = if (Build.VERSION.SDK_INT >= SDK_VERSION) {
            Html.fromHtml(linkString, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(linkString)
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton(R.string.ok) { _, _ -> showTermsOfUseDialog() }
            .setCancelable(false)
            .setOnKeyListener { dialog: DialogInterface, keyCode: Int, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.cancel()
                    showTermsOfUseDialog()
                    return@setOnKeyListener true
                }
                false
            }
            .show()
    }

    private fun loadContent() {
        if (BuildConfig.FEATURE_APK_GENERATOR_ENABLED) {
            val mainMenuSplashscreenBinding =
                ActivityMainMenuSplashscreenBinding.inflate(layoutInflater)
            setContentView(mainMenuSplashscreenBinding.root)
            if (oldPrivacyPolicy == Constants.CATROBAT_TERMS_OF_USE_ACCEPTED) {
                prepareStandaloneProject()
            }
            return
        }
        mainMenuBinding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(mainMenuBinding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setIcon(R.drawable.pc_toolbar_icon)
        supportActionBar?.setTitle(R.string.app_name)

        setShowProgressBar(true)

        if (SettingsFragment.isCastSharedPreferenceEnabled(this)) {
            CastManager.getInstance().initializeCast(this)
        }
        loadFragment()
    }

    private fun loadFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                mainMenuBinding.fragmentContainer.id, MainMenuFragment(),
                MainMenuFragment.TAG
            )
            .commit()
        setShowProgressBar(false)

        val intent = intent
        if (intent.action != null && intent.action == "android.intent.action.VIEW" && intent.data != null) {
            val shareUri = intent.data
            val webIntent = Intent(this, WebViewActivity::class.java)
            webIntent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, shareUri.toString())
            startActivity(webIntent)
        }
    }

    private fun setShowProgressBar(show: Boolean) {
        val progressBarBinding = ProgressBarBinding.inflate(layoutInflater)
        progressBarBinding.root.setVisibleOrGone(show)
        mainMenuBinding.fragmentContainer.setVisibleOrGone(!show)
    }

    public override fun onPause() {
        super.onPause()
        val currentProject = projectManager.currentProject
        if (currentProject != null) {
            ProjectSaver(currentProject, applicationContext).saveProjectAsync()
            Utils.setLastUsedProjectName(applicationContext, currentProject.name)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_menu, menu)
        val scratchConverter = getString(R.string.main_menu_scratch_converter)
        val scratchConverterBeta = SpannableString(
            scratchConverter + " " + getString(R.string.beta)
        )

        scratchConverterBeta.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.beta_label_color, theme)),
            scratchConverter.length, scratchConverterBeta.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        menu.findItem(R.id.menu_scratch_converter).title = scratchConverterBeta
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.menu_login).isVisible =
            !Utils.isUserLoggedIn(this)
        menu.findItem(R.id.menu_logout).isVisible =
            Utils.isUserLoggedIn(this)
        if (!BuildConfig.FEATURE_SCRATCH_CONVERTER_ENABLED) {
            menu.removeItem(R.id.menu_scratch_converter)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_rate_app -> if (Utils.checkIsNetworkAvailableAndShowErrorMessage(this)) {
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$packageName")
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    Log.e(TAG, "onOptionsItemSelected: ", e)
                    ToastUtil.showError(this, R.string.main_menu_play_store_not_installed)
                }
            }
            R.id.menu_terms_of_use -> TermsOfUseDialogFragment().show(
                supportFragmentManager,
                TermsOfUseDialogFragment.TAG
            )
            R.id.menu_privacy_policy -> {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(FlavoredConstants.PRIVACY_POLICY_URL)
                )
                startActivity(browserIntent)
            }
            R.id.menu_about -> AboutDialogFragment().show(
                supportFragmentManager,
                AboutDialogFragment.TAG
            )
            R.id.menu_scratch_converter -> if (Utils.checkIsNetworkAvailableAndShowErrorMessage(this)) {
                startActivity(Intent(this, ScratchConverterActivity::class.java))
            }
            R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.menu_login -> startActivity(Intent(this, SignInActivity::class.java))
            R.id.menu_logout -> {
                Utils.logoutUser(this)
                ToastUtil.showSuccess(this, R.string.logout_successful)
            }
            R.id.menu_help -> startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(CATROBAT_HELP_URL)
                )
            )
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun prepareStandaloneProject() {
        try {
            val inputStream = assets.open(BuildConfig.START_PROJECT + ".zip")
            val projectDir = File(
                FlavoredConstants.DEFAULT_ROOT_DIRECTORY,
                FileMetaDataExtractor.encodeSpecialCharsForFileSystem(
                    BuildConfig.PROJECT_NAME
                )
            )
            ZipArchiver()
                .unzip(inputStream, projectDir)
            ProjectLoader(projectDir, this)
                .setListener(this)
                .loadProjectAsync()
        } catch (e: IOException) {
            Log.e("STANDALONE", "Cannot unpack standalone project: ", e)
        }
    }

    override fun onLoadFinished(success: Boolean) {
        if (BuildConfig.FEATURE_APK_GENERATOR_ENABLED && success) {
            startActivityForResult(
                Intent(this, StageActivity::class.java), StageActivity.REQUEST_START_STAGE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (BuildConfig.FEATURE_APK_GENERATOR_ENABLED) {
            if (requestCode == StageActivity.REQUEST_START_STAGE) {
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        val TAG = MainMenuActivity::class.java.simpleName

        @JvmField
        var surveyCampaign: Survey? = null
    }
}
