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

package org.catrobat.catroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.GoogleLoginHandler;
import org.catrobat.catroid.ui.recyclerview.dialog.login.LoginDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.login.RegistrationDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.login.SignInCompleteListener;
import org.catrobat.catroid.utils.Utils;

import static org.catrobat.catroid.transfers.GoogleLoginHandler.REQUEST_CODE_GOOGLE_SIGNIN;

public class SignInActivity extends BaseActivity implements SignInCompleteListener {
	public static final String LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL";

	private GoogleLoginHandler googleLoginHandler;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_sign_in);
		setUpGoogleSignin();

		TextView termsOfUseLinkTextView = findViewById(R.id.register_terms_link);

		String termsOfUseUrl = getString(R.string.about_link_template, Constants.CATROBAT_TERMS_OF_USE_URL,
				getString(R.string.register_code_terms_of_use_text));
		termsOfUseLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
		termsOfUseLinkTextView.setText(Html.fromHtml(termsOfUseUrl));
	}

	public void setUpGoogleSignin() {
		googleLoginHandler = new GoogleLoginHandler(this);
		findViewById(R.id.sign_in_google_login_button).setOnClickListener(this::onButtonClick);
	}

	public void onButtonClick(final View view) {
		if (Utils.checkIsNetworkAvailableAndShowErrorMessage(this)) {
			onButtonClickForRealThisTime(view);
		}
	}

	private void onButtonClickForRealThisTime(View view) {
		switch (view.getId()) {
			case R.id.sign_in_login:
				LoginDialogFragment logInDialog = new LoginDialogFragment();
				logInDialog.setSignInCompleteListener(this);
				logInDialog.show(getSupportFragmentManager(), LoginDialogFragment.TAG);
				break;
			case R.id.sign_in_register:
				RegistrationDialogFragment registrationDialog = new RegistrationDialogFragment();
				registrationDialog.setSignInCompleteListener(this);
				registrationDialog.show(getSupportFragmentManager(), RegistrationDialogFragment.TAG);
				break;
			case R.id.sign_in_google_login_button:
				startActivityForResult(googleLoginHandler.getGoogleSignInClient().getSignInIntent(), REQUEST_CODE_GOOGLE_SIGNIN);
				break;
			default:
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		googleLoginHandler.onActivityResult(requestCode, resultCode, data);

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onLoginSuccessful(Bundle bundle) {
		Intent intent = new Intent();
		intent.putExtra(LOGIN_SUCCESSFUL, bundle);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onLoginCancel() {
	}
}
