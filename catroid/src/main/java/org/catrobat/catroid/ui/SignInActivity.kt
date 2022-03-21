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

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.CATROBAT_TERMS_OF_USE_URL
import org.catrobat.catroid.common.Constants.CURRENT_OAUTH_PROVIDER
import org.catrobat.catroid.common.Constants.GOOGLE_PROVIDER
import org.catrobat.catroid.common.Constants.REFRESH_TOKEN
import org.catrobat.catroid.common.Constants.TOKEN
import org.catrobat.catroid.transfers.OAuthTask
import org.catrobat.catroid.ui.recyclerview.dialog.login.LoginDialogFragment
import org.catrobat.catroid.ui.recyclerview.dialog.login.RegistrationDialogFragment
import org.catrobat.catroid.ui.recyclerview.dialog.login.SignInCompleteListener
import org.catrobat.catroid.utils.DeviceSettingsProvider
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.Utils
import org.catrobat.catroid.web.ServerAuthenticationConstants.GOOGLE_LOGIN_CATROWEB_SERVER_CLIENT_ID
import org.koin.android.ext.android.inject

class SignInActivity : BaseActivity(), SignInCompleteListener {
    private var googleSignInClient: GoogleSignInClient? = null
    private val oAuthTask: OAuthTask by inject()
    private var progressDialog: ProgressDialog? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        setUpGoogleSignIn()

        val termsOfUseLinkTextView = findViewById<TextView>(R.id.register_terms_link)
        val termsOfUseUrl = getString(R.string.about_link_template, CATROBAT_TERMS_OF_USE_URL,
            getString(R.string.register_code_terms_of_use_text))
        termsOfUseLinkTextView.movementMethod = LinkMovementMethod.getInstance()
        termsOfUseLinkTextView.text = Html.fromHtml(termsOfUseUrl)

        oAuthTask.isOAuthLoggingIn().observe(this, Observer { isOAuthLoggingIn ->
            showProgressDialog(isOAuthLoggingIn)
        })

        oAuthTask.getOAuthResponse().observe(this, Observer { loginResponse ->
            loginResponse?.let {
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                val sharedPreferencesEditor = sharedPreferences?.edit()
                sharedPreferencesEditor?.putString(TOKEN, loginResponse.token)
                sharedPreferencesEditor?.putString(REFRESH_TOKEN, loginResponse.refresh_token)
                sharedPreferencesEditor?.apply()

                ToastUtil.showSuccess(this, R.string.user_logged_in)
                val bundle = Bundle()
                bundle.putString(CURRENT_OAUTH_PROVIDER, GOOGLE_PROVIDER)
                onLoginSuccessful(bundle)
            } ?: run {
                if (oAuthTask.getMessage().isEmpty()) {
                    oAuthTask.setMessage(getString(R.string.sign_in_error))
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        oAuthTask.clear()
    }

    private fun setUpGoogleSignIn() {
        val googleSignInOptions = GoogleSignInOptions.Builder(DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(GOOGLE_LOGIN_CATROWEB_SERVER_CLIENT_ID)
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        findViewById<View>(R.id.sign_in_google_login_button).setOnClickListener { view: View ->
            onButtonClick(view)
        }
    }

    fun onButtonClick(view: View) {
        if (!Utils.checkIsNetworkAvailableAndShowErrorMessage(this)) {
            return
        }

        when (view.id) {
            R.id.sign_in_login -> {
                val logInDialog = LoginDialogFragment()
                logInDialog.setSignInCompleteListener(this)
                logInDialog.show(supportFragmentManager, LoginDialogFragment.TAG)
            }
            R.id.sign_in_register -> {
                val registrationDialog = RegistrationDialogFragment()
                registrationDialog.setSignInCompleteListener(this)
                registrationDialog.show(supportFragmentManager, RegistrationDialogFragment.TAG)
            }
            R.id.sign_in_google_login_button -> startActivityForResult(googleSignInClient?.signInIntent, REQUEST_CODE_GOOGLE_SIGNIN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_GOOGLE_SIGNIN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                onGoogleLogInComplete(task.result)
            } else {
                ToastUtil.showError(this, String.format(getString(R.string.error_google_plus_sign_in),
                                                        task.exception?.localizedMessage?.replace(":", "")))
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onGoogleLogInComplete(account: GoogleSignInAccount) {
        val id = account.id
        val personName = account.displayName
        val email = account.email
        val locale = DeviceSettingsProvider.getUserCountryCode()
        val idToken = account.idToken

        PreferenceManager.getDefaultSharedPreferences(this).edit()
            .putString(Constants.GOOGLE_ID, id)
            .putString(Constants.GOOGLE_USERNAME, personName)
            .putString(Constants.GOOGLE_EMAIL, email)
            .putString(Constants.GOOGLE_LOCALE, locale)
            .putString(Constants.GOOGLE_ID_TOKEN, idToken)
            .apply()

        oAuthTask.setIsOAuthLoggingIn()
        oAuthTask.oAuthLogin(idToken.orEmpty(), GOOGLE_PROVIDER)
    }

    private fun showProgressDialog(show: Boolean) {
        if (show) {
            progressDialog = ProgressDialog.show(this, getString(R.string.please_wait), getString(R.string.loading_check_oauth_token))
        } else {
            progressDialog?.let { progressDialog ->
                if (progressDialog.isShowing) {
                    progressDialog.dismiss()
                }
            }
        }
    }

    override fun onLoginSuccessful(bundle: Bundle) {
        val intent = Intent()
        intent.putExtra(LOGIN_SUCCESSFUL, bundle)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onLoginCancel() = Unit

    companion object {
        const val REQUEST_CODE_GOOGLE_SIGNIN = 100
        const val LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL"
    }
}
