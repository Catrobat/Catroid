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
package org.catrobat.catroid.ui.recyclerview.dialog.login

import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
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
import org.catrobat.catroid.transfers.RegistrationViewModel
import org.catrobat.catroid.ui.showKeyboard
import org.catrobat.catroid.utils.DeviceSettingsProvider
import org.catrobat.catroid.utils.NetworkConnectionMonitor
import org.catrobat.catroid.utils.ToastUtil
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistrationDialogFragment : DialogFragment() {
    private lateinit var email: String
    private lateinit var username: String
    private lateinit var password: String
    private var usernameInputLayout: TextInputLayout? = null
    private var emailInputLayout: TextInputLayout? = null
    private var passwordInputLayout: TextInputLayout? = null
    private var confirmPasswordInputLayout: TextInputLayout? = null
    private var usernameEditText: EditText? = null
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var confirmPasswordEditText: EditText? = null
    private var alertDialog: AlertDialog? = null
    private var progressDialog: ProgressDialog? = null
    private var signInCompleteListener: SignInCompleteListener? = null
    private var sharedPreferences: SharedPreferences? = null
    private val connectionMonitor: NetworkConnectionMonitor by inject()
    private val viewModel: RegistrationViewModel by viewModel()

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
        val view = View.inflate(activity, R.layout.dialog_register, null)
        usernameInputLayout = view.findViewById(R.id.dialog_register_username)
        emailInputLayout = view.findViewById(R.id.dialog_register_email)
        passwordInputLayout = view.findViewById(R.id.dialog_register_password)
        confirmPasswordInputLayout = view.findViewById(R.id.dialog_register_password_confirm)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        activity?.let {
            alertDialog = AlertDialog.Builder(it)
            .setTitle(R.string.register)
            .setView(view)
            .setPositiveButton(R.string.register, null)
            .create()
            usernameEditText = usernameInputLayout?.editText
            emailEditText = emailInputLayout?.editText
            passwordEditText = passwordInputLayout?.editText
            confirmPasswordEditText = confirmPasswordInputLayout?.editText
            usernameEditText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(text: CharSequence, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) = Unit
                override fun afterTextChanged(editable: Editable) {
                    usernameChanged(editable.toString())
                    handleRegisterBtnStatus()
                }
            })
        }

        emailEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(editable: Editable) {
                emailChanged(editable.toString())
                handleRegisterBtnStatus()
            }
        })

        passwordEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(editable: Editable) {
                passwordChanged(editable.toString())
                handleRegisterBtnStatus()
            }
        })

        confirmPasswordEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(editable: Editable) {
                confirmPasswordChanged(editable.toString())
                handleRegisterBtnStatus()
            }
        })

        val showPasswordCheckBox = view.findViewById<CheckBox>(R.id.show_password)
        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                passwordEditText?.inputType = InputType.TYPE_CLASS_TEXT
                confirmPasswordEditText?.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                passwordEditText?.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                confirmPasswordEditText?.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        val eMail = DeviceSettingsProvider.getUserEmail(activity)
        eMail?.let {
            emailEditText?.setText(eMail)
            emailInputLayout?.isErrorEnabled = false
        }

        registerObservers()

        return alertDialog as AlertDialog
    }

    private fun usernameChanged(username: String) {
        when {
            username.isEmpty() ->
                usernameInputLayout?.error = getString(R.string.error_register_empty_username)
            username.trim { it <= ' ' }.contains("@") ->
                usernameInputLayout?.error = getString(R.string.error_register_username_as_email)
            username.trim { it <= ' ' }.matches(Regex("^[a-zA-Z0-9-_.]*$"))
                .not() ->
                usernameInputLayout?.error = getString(R.string.error_register_invalid_username)
            username.trim { it <= ' ' }.startsWith("-") ||
                username.startsWith("_") ||
                username.startsWith(".") ->
                usernameInputLayout?.error = getString(R.string.error_register_username_start_with)
            else -> usernameInputLayout?.isErrorEnabled = false
        }
    }

    private fun emailChanged(email: String) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()) {
            emailInputLayout?.error = getString(R.string.error_register_invalid_email_format)
        } else {
            emailInputLayout?.isErrorEnabled = false
        }
    }

    private fun passwordChanged(password: String) {
        when {
            password.isEmpty() ->
                passwordInputLayout?.error = getString(R.string.error_register_empty_password)
            password.length < MIN_PASSWORD_LENGTH ->
                passwordInputLayout?.error = getString(R.string.error_register_password_at_least_6_characters)
            password != confirmPasswordEditText?.text.toString() -> {
                confirmPasswordInputLayout?.error = getString(R.string.error_register_passwords_mismatch)
                passwordInputLayout?.isErrorEnabled = false
            }
            else -> {
                passwordInputLayout?.isErrorEnabled = false
                confirmPasswordInputLayout?.isErrorEnabled = false
            }
        }
    }

    private fun confirmPasswordChanged(confirmPassword: String) {
        when {
            confirmPassword.isEmpty() ->
                confirmPasswordInputLayout?.error = getString(R.string.error_register_empty_confirm_password)
            confirmPassword.length < MIN_PASSWORD_LENGTH ->
                confirmPasswordInputLayout?.error = getString(R.string.error_register_password_at_least_6_characters)
            confirmPassword != passwordEditText?.text.toString() ->
                confirmPasswordInputLayout?.error = getString(R.string.error_register_passwords_mismatch)
            else -> {
                confirmPasswordInputLayout?.isErrorEnabled = false
                passwordInputLayout?.isErrorEnabled = false
            }
        }
    }

    private fun registerObservers() {
        alertDialog?.setOnShowListener {
            alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
            alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener { onRegisterButtonClick() }
            usernameEditText.showKeyboard()
        }

        viewModel.isEmailInUse().observe(this, Observer { isEmailInUse ->
            if (emailInputLayout?.isErrorEnabled?.not()!! && isEmailInUse) {
                emailInputLayout?.error = getString(R.string.error_register_email_exists)
                emailEditText.showKeyboard()
            }
        })

        viewModel.isUserNameInUse().observe(this, Observer { isUserNameInUse ->
            if (emailInputLayout?.isErrorEnabled?.not() == true && isUserNameInUse) {
                usernameInputLayout?.error = getString(R.string.error_register_username_already_exists)
                usernameEditText.showKeyboard()
            }
        })

        viewModel.isRegistering().observe(this, Observer { isRegistering ->
            showProgressDialog(isRegistering)
        })

        viewModel.getLoginResponse().observe(this, Observer { registerResponse ->
            registerResponse?.let {
                val sharedPreferencesEditor = sharedPreferences?.edit()
                sharedPreferencesEditor?.putString(Constants.TOKEN, registerResponse.token)
                sharedPreferencesEditor?.putString(Constants.USERNAME, username)
                sharedPreferencesEditor?.putString(Constants.EMAIL, email)
                sharedPreferencesEditor?.apply()

                ToastUtil.showSuccess(context, R.string.new_user_registered)

                val bundle = Bundle()
                bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, Constants.NO_OAUTH_PROVIDER)
                signInCompleteListener?.onLoginSuccessful(bundle)
                dismiss()
            } ?: run {
                if (viewModel.getMessage().isEmpty()) {
                    viewModel.setMessage(getString(R.string.register_error))
                }
                confirmPasswordEditText?.error = viewModel.getMessage()
                alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
            }
        })
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

    private fun onRegisterButtonClick() {
        username = usernameEditText?.text.toString().trim { it <= ' ' }
        password = passwordEditText?.text.toString()
        email = emailEditText?.text.toString()

        connectionMonitor.observe(this, Observer { connectionActive ->
            if (connectionActive) {
                viewModel.setIsRegistering()
                val token = sharedPreferences?.getString(Constants.TOKEN, Constants.NO_TOKEN).orEmpty()
                Log.d(LoginDialogFragment.TAG, "Token stored in shared preferences $token")
                viewModel.register(true, email, username, password, token)
            } else {
                ToastUtil.showError(context, R.string.error_internet_connection)
            }
        })
    }

    private fun handleRegisterBtnStatus() {
        alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.let {
            it.isEnabled = (usernameInputLayout?.isErrorEnabled ?: false ||
                emailInputLayout?.isErrorEnabled ?: false ||
                passwordInputLayout?.isErrorEnabled ?: false ||
                confirmPasswordInputLayout?.isErrorEnabled ?: false).not()
        }
    }

    companion object {
        val TAG = RegistrationDialogFragment::class.java.simpleName
    }
}
