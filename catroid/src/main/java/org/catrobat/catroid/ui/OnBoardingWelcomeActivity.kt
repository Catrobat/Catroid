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

package org.catrobat.catroid.ui

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.databinding.DeclinedTermsOfUseAndServiceAlertViewBinding
import org.catrobat.catroid.databinding.OnboardingWelcomeContainerBinding
import org.catrobat.catroid.databinding.PrivacyPolicyViewBinding
import org.catrobat.catroid.transfers.GoogleLoginHandler
import org.catrobat.catroid.ui.recyclerview.dialog.login.LoginDialogFragment
import org.catrobat.catroid.ui.recyclerview.dialog.login.RegistrationDialogFragment
import org.catrobat.catroid.ui.recyclerview.dialog.login.SignInCompleteListener
import org.catrobat.catroid.ui.recyclerview.fragment.OnBoardingWelcomeFragmentContainer
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.Utils

private const val SDK_VERSION = 24

class OnBoardingWelcomeActivity : BaseCastActivityWithProjectLoader(), SignInCompleteListener {
    private lateinit var onBoardingWelcomeContainerBinding: OnboardingWelcomeContainerBinding
    private lateinit var googleLoginHandler: GoogleLoginHandler
    private lateinit var sharedPreferences: SharedPreferences
    private var oldPrivacyPolicy = 0

    private lateinit var privacyPolicyBinding: PrivacyPolicyViewBinding
    private lateinit var declinedTermsOfUseViewBinding: DeclinedTermsOfUseAndServiceAlertViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBoardingWelcomeContainerBinding =
            OnboardingWelcomeContainerBinding.inflate(layoutInflater)
        setContentView(onBoardingWelcomeContainerBinding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        oldPrivacyPolicy =
            sharedPreferences.getInt(SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION, 0)

        setupToolbar()
        setupFragment()

        if (oldPrivacyPolicy != Constants.CATROBAT_TERMS_OF_USE_ACCEPTED) {
            showTermsOfUseDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.onboarding_welcome_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.skipButton) {
            finishActivitySuccessfully()
        }
        return true
    }

    override fun onLoginSuccessful(bundle: Bundle) {
        val username = sharedPreferences.getString(Constants.USERNAME, "")
        showUserData(username)
    }

    @SuppressWarnings("EmptyFunctionBlock")
    override fun onLoginCancel() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        googleLoginHandler.onActivityResult(requestCode, resultCode, data)

        val username = sharedPreferences.getString(Constants.GOOGLE_USERNAME, "")
        showUserData(username)
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun onSignInWithGoogleClicked() {
        if (!Utils.checkIsNetworkAvailableAndShowErrorMessage(this)) {
            return
        }
        googleLoginHandler = GoogleLoginHandler(this)
        startActivityForResult(
            googleLoginHandler.googleSignInClient.signInIntent,
            GoogleLoginHandler.REQUEST_CODE_GOOGLE_SIGNIN
        )
    }

    fun onLoginClicked() {
        if (!Utils.checkIsNetworkAvailableAndShowErrorMessage(this)) {
            return
        }
        val dialogFragment = LoginDialogFragment()
        dialogFragment.setSignInCompleteListener(this)
        dialogFragment.show(supportFragmentManager, LoginDialogFragment.TAG)
    }

    fun onRegisterClicked() {
        if (!Utils.checkIsNetworkAvailableAndShowErrorMessage(this)) {
            return
        }
        val dialogFragment = RegistrationDialogFragment()
        dialogFragment.setSignInCompleteListener(this)
        dialogFragment.show(supportFragmentManager, RegistrationDialogFragment.TAG)
    }

    fun onTutorialModeClicked() {
        sharedPreferences
            .edit()
            .putBoolean(SharedPreferenceKeys.DISABLE_HINTS_DIALOG_SHOWN_PREFERENCE_KEY, true)
            .apply()

        finishActivitySuccessfully()
    }

    fun onNormalModeClicked() {
        finishActivitySuccessfully()
    }

    private fun finishActivitySuccessfully() {
        sharedPreferences
            .edit()
            .putBoolean(SharedPreferenceKeys.ONBOARDING_WELCOME_SCREEN_SHOWN, true)
            .apply()

        setResult(RESULT_OK)
        finish()
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

    private fun handleAgreedToPrivacyPolicyButton() {
        sharedPreferences
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

    private fun handleDeclinedPrivacyPolicyButton() {
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

    private fun setupFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                onBoardingWelcomeContainerBinding.fragmentContainer.id,
                OnBoardingWelcomeFragmentContainer(),
                OnBoardingWelcomeFragmentContainer.TAG
            )
            .commit()
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        val text = "<b><font color=#00ACC1>POCKET</font><font color=#E68B00>CODE</font></b>"
        supportActionBar?.title = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun showUserData(username: String?) {
        if (username == null || username.isEmpty()) {
            ToastUtil.showError(this, "Something went wrong during the authentication")
            Log.e(TAG, "Failed to authenticate the user")
            return
        }

        val fragment =
            supportFragmentManager.findFragmentByTag(OnBoardingWelcomeFragmentContainer::class.java.simpleName)
                as OnBoardingWelcomeFragmentContainer
        val onBoardingWelcomeFragment = fragment.getSelectedTabFragment()
        onBoardingWelcomeFragment?.onSuccessfulLogin(username)
    }

    companion object {
        val TAG: String = OnBoardingWelcomeActivity::class.java.simpleName
    }
}
