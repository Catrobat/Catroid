/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.transfers

import org.koin.java.KoinJavaComponent.inject
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.transfers.CheckOAuthTokenTask.OnCheckOAuthTokenCompleteListener
import org.catrobat.catroid.transfers.GoogleLogInTask.OnGoogleServerLogInCompleteListener
import org.catrobat.catroid.transfers.CheckEmailAvailableTask.OnCheckEmailAvailableCompleteListener
import org.catrobat.catroid.transfers.GoogleExchangeCodeTask.OnGoogleExchangeCodeCompleteListener
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import org.catrobat.catroid.transfers.OAuthViewModel
import android.app.ProgressDialog
import android.content.Intent
import org.catrobat.catroid.transfers.GoogleLoginHandler
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignIn
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.R
import org.catrobat.catroid.utils.DeviceSettingsProvider
import android.preference.PreferenceManager
import android.content.SharedPreferences
import org.catrobat.catroid.transfers.GoogleLogInTask
import org.catrobat.catroid.transfers.CheckEmailAvailableTask
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import org.catrobat.catroid.ui.recyclerview.dialog.login.SignInCompleteListener
import org.catrobat.catroid.transfers.GoogleExchangeCodeTask
import org.catrobat.catroid.ui.recyclerview.dialog.login.OAuthUsernameDialogFragment
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.huawei.hms.ml.language.common.utils.Constant
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.GOOGLE_PLUS
import org.catrobat.catroid.web.ServerAuthenticationConstants

class GoogleLoginHandler(private val activity: AppCompatActivity) :
    OnCheckOAuthTokenCompleteListener, OnGoogleServerLogInCompleteListener,
    OnCheckEmailAvailableCompleteListener, OnGoogleExchangeCodeCompleteListener {
    val googleSignInClient: GoogleSignInClient
    private val viewModel: Lazy<OAuthViewModel> = inject(OAuthViewModel::class.java)
    private var progressDialog: ProgressDialog? = null
    private var sharedPreferences: SharedPreferences? = null
    private var signInCompleteListener: SignInCompleteListener? = null
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_GOOGLE_SIGNIN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                onGoogleLogInComplete(task.result)
            } else {
                ToastUtil.showError(
                    activity,
                    String.format(
                        activity.getString(R.string.error_google_plus_sign_in),
                        task.exception.localizedMessage.replace(":", "")
                    )
                )
            }
        }
    }

    fun onGoogleLogInComplete(account: GoogleSignInAccount) {
        val id = account.id
        val personName = account.displayName
        val email = account.email
        val locale = DeviceSettingsProvider.getUserCountryCode()
        val idToken = account.idToken
        val code = account.serverAuthCode
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
        PreferenceManager.getDefaultSharedPreferences(activity).edit()
            .putString(Constants.GOOGLE_ID, id)
            .putString(Constants.GOOGLE_USERNAME, personName)
            .putString(Constants.GOOGLE_EMAIL, email)
            .putString(Constants.GOOGLE_LOCALE, locale)
            .putString(Constants.GOOGLE_ID_TOKEN, idToken)
            .putString(Constants.GOOGLE_EXCHANGE_CODE, code)
            .apply()

        viewModel.value.isOAuthLoggingIn().observe(activity, Observer {
                            isOAuthLoggingIn ->
            showProgressDialog(isOAuthLoggingIn)
        })
        viewModel.value.getOAuthResponse().observe(activity, Observer { loginResponse ->
            loginResponse?.let {
                val sharedPreferencesEditor = sharedPreferences?.edit()
                sharedPreferencesEditor?.putString(Constants.TOKEN, loginResponse.token)
                //sharedPreferencesEditor?.putString(Constants.REFRESH_TOKEN, loginResponse.refresh_token)
                sharedPreferencesEditor?.apply()

                ToastUtil.showSuccess(activity, R.string.user_logged_in)
                onCheckOAuthTokenComplete(true, Constants.GOOGLE_PLUS)
            } ?: run {
                if (viewModel.value.getMessage().isEmpty()) {
                    viewModel.value.setMessage(activity.getString(R.string.sign_in_error))
                }
            }
        })

        viewModel.value.setIsOAuthLoggingIn()
        viewModel.value.oAuthLogin(idToken, "google")
        /*CheckOAuthTokenTask checkOAuthTokenTask = new CheckOAuthTokenTask(activity, id,
				Constants.GOOGLE_PLUS);
		checkOAuthTokenTask.setOnCheckOAuthTokenCompleteListener(this);
		checkOAuthTokenTask.execute();*/
    }

      private fun showProgressDialog(show: Boolean) {
            if (show) {
                activity.let{
                    progressDialog = ProgressDialog.show(activity, activity.getString(R.string.please_wait), activity.getString(R.string.loading))
                }
            } else {
                progressDialog?.let { progressDialog ->
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()
                    }
                }
            }
        }

    override fun onCheckOAuthTokenComplete(tokenAvailable: Boolean, provider: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            activity
        )
        if (tokenAvailable) {
            val googleLogInTask = GoogleLogInTask(
                activity,
                sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL),
                sharedPreferences.getString(
                    Constants.GOOGLE_USERNAME,
                    Constants.NO_GOOGLE_USERNAME
                ),
                sharedPreferences.getString(Constants.GOOGLE_ID, Constants.NO_GOOGLE_ID),
                sharedPreferences.getString(Constants.GOOGLE_LOCALE, Constants.NO_GOOGLE_LOCALE)
            )
            googleLogInTask.setOnGoogleServerLogInCompleteListener(this)
            googleLogInTask.execute()
        } else {
            val email =
                sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL)
            val checkEmailAvailableTask = CheckEmailAvailableTask(email, Constants.GOOGLE_PLUS)
            checkEmailAvailableTask.setOnCheckEmailAvailableCompleteListener(this)
            checkEmailAvailableTask.execute()
        }
    }

    override fun onGoogleServerLogInComplete() {
        val bundle = Bundle()
        bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, Constants.GOOGLE_PLUS)
        (activity as SignInCompleteListener).onLoginSuccessful(bundle)
    }

    override fun onCheckEmailAvailableComplete(emailAvailable: Boolean, provider: String) {
        if (emailAvailable) {
            exchangeGoogleAuthorizationCode()
        } else {
            showOauthUserNameDialog(Constants.GOOGLE_PLUS)
        }
    }

    fun exchangeGoogleAuthorizationCode() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            activity
        )
        val googleExchangeCodeTask = GoogleExchangeCodeTask(
            activity,
            sharedPreferences.getString(
                Constants.GOOGLE_EXCHANGE_CODE,
                Constants.NO_GOOGLE_EXCHANGE_CODE
            ),
            sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL),
            sharedPreferences.getString(Constants.GOOGLE_USERNAME, Constants.NO_GOOGLE_USERNAME),
            sharedPreferences.getString(Constants.GOOGLE_ID, Constants.NO_GOOGLE_ID),
            sharedPreferences.getString(Constants.GOOGLE_LOCALE, Constants.NO_GOOGLE_LOCALE),
            sharedPreferences.getString(Constants.GOOGLE_ID_TOKEN, Constants.NO_GOOGLE_ID_TOKEN)
        )
        googleExchangeCodeTask.setOnGoogleExchangeCodeCompleteListener(this)
        googleExchangeCodeTask.execute()
    }

    private fun showOauthUserNameDialog(provider: String) {
        val dialog = OAuthUsernameDialogFragment()
        val bundle = Bundle()
        bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, provider)
        dialog.arguments = bundle
        dialog.setSignInCompleteListener(activity as SignInCompleteListener)
        dialog.show(activity.supportFragmentManager, OAuthUsernameDialogFragment.TAG)
    }

    override fun onGoogleExchangeCodeComplete() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            activity
        )
        val googleLogInTask = GoogleLogInTask(
            activity,
            sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL),
            sharedPreferences.getString(Constants.GOOGLE_USERNAME, Constants.NO_GOOGLE_USERNAME),
            sharedPreferences.getString(Constants.GOOGLE_ID, Constants.NO_GOOGLE_ID),
            sharedPreferences.getString(Constants.GOOGLE_LOCALE, Constants.NO_GOOGLE_LOCALE)
        )
        googleLogInTask.setOnGoogleServerLogInCompleteListener(this)
        googleLogInTask.execute()
    }

    companion object {
        const val REQUEST_CODE_GOOGLE_SIGNIN = 100
    }

    init {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(ServerAuthenticationConstants.GOOGLE_LOGIN_CATROWEB_SERVER_CLIENT_ID)
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptions)
    }
}