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
package org.catrobat.catroid.ui.recyclerview.dialog.login

import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputLayout
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.MIN_PASSWORD_LENGTH
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.transfers.LoginViewModel
import org.catrobat.catroid.ui.WebViewActivity
import org.catrobat.catroid.ui.showKeyboard
import org.catrobat.catroid.utils.NetworkConnectionMonitor
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.web.ServerCalls
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginDialogFragment : DialogFragment() {
    private lateinit var username: String
    private lateinit var password: String
    private var usernameInputLayout: TextInputLayout? = null
    private var passwordInputLayout: TextInputLayout? = null
    private var usernameEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var alertDialog: AlertDialog? = null
    private var progressDialog: ProgressDialog? = null
    private var signInCompleteListener: SignInCompleteListener? = null
    private var sharedPreferences: SharedPreferences? = null
    private val viewModel: LoginViewModel by viewModel()
    private val connectionMonitor: NetworkConnectionMonitor by inject()

    fun setSignInCompleteListener(signInCompleteListener: SignInCompleteListener?) {
        this.signInCompleteListener = signInCompleteListener
    }

    override fun onResume() {
        super.onResume()
        connectionMonitor.registerDefaultNetworkCallback()
    }
    override fun onPause() {
        super.onPause()
        connectionMonitor.unregisterDefaultNetworkCallback()
    }

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        val view = View.inflate(activity, R.layout.dialog_login, null)
        usernameInputLayout = view.findViewById(R.id.dialog_login_username)
        passwordInputLayout = view.findViewById(R.id.dialog_login_password)
        usernameEditText = usernameInputLayout?.editText
        passwordEditText = passwordInputLayout?.editText

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val showPasswordCheckBox = view.findViewById<CheckBox>(R.id.show_password)
        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                passwordEditText?.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                passwordEditText?.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
        alertDialog = AlertDialog.Builder(requireActivity())
            .setTitle(R.string.login)
            .setView(view)
            .setPositiveButton(R.string.login, null)
            .setNegativeButton(R.string.cancel, null)
            .setNeutralButton(R.string.reset_password, null)
            .setCancelable(true)
            .create()

        usernameEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().isEmpty()) {
                    usernameInputLayout?.error = getString(R.string.error_register_empty_username)
                } else if (!editable.toString().trim { it <= ' ' }.matches(Regex("^[a-zA-Z0-9-_.]*$"))) {
                    usernameInputLayout?.error = getString(R.string.error_register_invalid_username)
                } else {
                    usernameInputLayout?.isErrorEnabled = false
                }
                handleLoginBtnStatus()
            }
        })

        passwordEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(editable: Editable) {
                when (editable.toString().length) {
                    0 -> passwordInputLayout?.error = getString(R.string.error_register_empty_password)
                    in 1 until MIN_PASSWORD_LENGTH ->
                        passwordInputLayout?.error = getString(R.string.error_register_password_at_least_6_characters)
                    else -> passwordInputLayout?.isErrorEnabled = false
                }
                handleLoginBtnStatus()
            }
        })
        alertDialog?.setOnShowListener {
            alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
            alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener { onLoginButtonClick() }
            alertDialog?.getButton(AlertDialog.BUTTON_NEUTRAL)?.setOnClickListener { onPasswordForgottenButtonClick() }
            usernameEditText.showKeyboard()
        }

        viewModel.isLoggingIn().observe(this, Observer { isLoggingIn ->
            showProgressDialog(isLoggingIn)
        })

        viewModel.getLoginResponse().observe(this, Observer { loginResponse ->
            loginResponse?.let {
                val sharedPreferencesEditor = sharedPreferences?.edit()
                sharedPreferencesEditor?.putString(Constants.TOKEN, loginResponse.token)
                sharedPreferencesEditor?.putString(Constants.REFRESH_TOKEN, loginResponse.refresh_token)
                sharedPreferencesEditor?.putString(Constants.USERNAME, username)
                sharedPreferencesEditor?.apply()

                ToastUtil.showSuccess(context, R.string.user_logged_in)
                val bundle = Bundle()
                bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, Constants.NO_OAUTH_PROVIDER)
                signInCompleteListener?.onLoginSuccessful(bundle)
                dismiss()
            } ?: run {
                if (viewModel.getMessage().isEmpty()) {
                    viewModel.setMessage(getString(R.string.sign_in_error))
                }
                passwordEditText?.error = viewModel.getMessage()
                alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
            }
        })

        return alertDialog as AlertDialog
    }

    private fun showProgressDialog(show: Boolean) {
        if (show) {
            progressDialog = ProgressDialog.show(context, getString(R.string.please_wait), getString(R.string.loading))
        } else {
            progressDialog?.let {
                if (it.isShowing) {
                    it.dismiss()
                }
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        signInCompleteListener?.onLoginCancel()
    }

    private fun onLoginButtonClick() {
        username = usernameEditText?.text.toString().replace("\\s".toRegex(), "")
        password = passwordEditText?.text.toString()

        connectionMonitor.observe(this, Observer { connectionActive ->
            if (connectionActive) {
                viewModel.setIsLoggingIn()
                val token = sharedPreferences?.getString(Constants.TOKEN, Constants.NO_TOKEN).orEmpty()
                Log.d(TAG, "Token stored in shared preferences $token")
                viewModel.login(username, password, token)
            } else {
                ToastUtil.showError(context, R.string.error_internet_connection)
            }
        })
    }

    private fun onPasswordForgottenButtonClick() {
        val baseUrl =
            if (ServerCalls.useTestUrl) ServerCalls.BASE_URL_TEST_HTTPS else FlavoredConstants.BASE_URL_HTTPS
        val url = baseUrl + PASSWORD_FORGOTTEN_PATH
        val intent = Intent(activity, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url)
        startActivity(intent)
    }

    private fun handleLoginBtnStatus() {
        alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.let {
            it.isEnabled = (usernameInputLayout?.isErrorEnabled ?: false || passwordInputLayout?.isErrorEnabled ?: false).not()
        }
    }

    companion object {
        const val PASSWORD_FORGOTTEN_PATH = "reset-password"
        val TAG = LoginDialogFragment::class.java.simpleName
    }
}
