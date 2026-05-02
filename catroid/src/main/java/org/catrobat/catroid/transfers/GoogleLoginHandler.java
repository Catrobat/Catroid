/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

package org.catrobat.catroid.transfers;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.recyclerview.dialog.login.SignInCompleteListener;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.web.LoginHelper;
import org.catrobat.catroid.web.LoginRepository;

import kotlin.Lazy;

import static com.google.android.gms.auth.api.signin.GoogleSignIn.getClient;
import static com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.GOOGLE_LOGIN_CATROWEB_SERVER_CLIENT_ID;
import static org.koin.java.KoinJavaComponent.inject;

public class GoogleLoginHandler {

	private AppCompatActivity activity;
	public static final int REQUEST_CODE_GOOGLE_SIGNIN = 100;
	private GoogleSignInClient googleSignInClient;

	private final Lazy<LoginRepository> loginRepository = inject(LoginRepository.class);

	@SuppressWarnings("RestrictedApi")
	public GoogleLoginHandler(AppCompatActivity activity) {
		this.activity = activity;

		GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.requestIdToken(GOOGLE_LOGIN_CATROWEB_SERVER_CLIENT_ID)
				.build();
		googleSignInClient = getClient(this.activity, googleSignInOptions);
	}

	public GoogleSignInClient getGoogleSignInClient() {
		return googleSignInClient;
	}

	@SuppressWarnings("RestrictedApi")
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_GOOGLE_SIGNIN) {
			Task<GoogleSignInAccount> task = getSignedInAccountFromIntent(data);
			if (task.isSuccessful()) {
				onGoogleLogInComplete(task.getResult());
			} else {
				String errorMsg = task.getException() != null && task.getException().getLocalizedMessage() != null
						? task.getException().getLocalizedMessage().replace(":", "")
						: "Unknown error";
				ToastUtil.showError(activity,
						String.format(activity.getString(R.string.error_google_plus_sign_in), errorMsg));
			}
		}
	}

	public void onGoogleLogInComplete(GoogleSignInAccount account) {
		String idToken = account.getIdToken();

		if (idToken == null) {
			ToastUtil.showError(activity, R.string.sign_in_error);
			return;
		}

		LoginHelper.performGoogleLogin(
				loginRepository.getValue(),
				idToken,
				() -> {
					if (activity instanceof SignInCompleteListener && !activity.isFinishing()) {
						Bundle bundle = new Bundle();
						bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, Constants.GOOGLE_PLUS);
						((SignInCompleteListener) activity).onLoginSuccessful(bundle);
					}
				},
				errorMsg -> ToastUtil.showError(activity, errorMsg)
		);
	}
}
