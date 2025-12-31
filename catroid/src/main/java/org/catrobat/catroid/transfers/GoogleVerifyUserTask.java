/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.util.List;

import static org.catrobat.catroid.web.ServerAuthenticationConstants.GOOGLE_LOGIN_CATROWEB_SERVER_CLIENT_ID;

public class GoogleVerifyUserTask extends AsyncTask<String, Void, Boolean> {
	private static final String TAG = GoogleVerifyUserTask.class.getSimpleName();

	private GoogleIdTokenCredential account;
	private String email;

	private OnGoogleVerifyUserCompleteListener onGoogleVerifyUserCompleteListener;

	public GoogleVerifyUserTask(GoogleIdTokenCredential account) {
		this.account = account;
	}

	public void setOnGoogleVerifyUserCompleteListener(OnGoogleVerifyUserCompleteListener listener) {
		onGoogleVerifyUserCompleteListener = listener;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		try {
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, new GsonFactory())
					.setAudience(List.of(GOOGLE_LOGIN_CATROWEB_SERVER_CLIENT_ID))
					.build();
			GoogleIdToken googleIdToken = verifier.verify(account.getIdToken());
			email = googleIdToken.getPayload().getEmail();
			return true;
		} catch (Exception e) {
			Log.e("Google", "Google account verification failed: ", e);
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);

		if (onGoogleVerifyUserCompleteListener != null) {
			onGoogleVerifyUserCompleteListener.onGoogleVerifyUserComplete(account, email);
		}
	}

	public interface OnGoogleVerifyUserCompleteListener {
		void onGoogleVerifyUserComplete(GoogleIdTokenCredential account, String googleEmail);
	}
}
