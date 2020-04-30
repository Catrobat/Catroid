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

package org.catrobat.catroid.ui;

import 	android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.catrobat.catroid.R;


import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

	int RC_SIGN_IN = 0;
	SignInButton signInButton;
	GoogleSignInClient mGoogleSignInClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);

		//Initializing Views
		signInButton = findViewById(R.id.sign_in_button);

		// Configure sign-in to request the user's ID, email address, and basic
		// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.build();

		// Build a GoogleSignInClient with the options specified by gso.
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

		signInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()){
					case R.id.sign_in_button:
						signIn();
						break;
				}
			}
		});
	}

	private void signIn() {
		Intent signInIntent = mGoogleSignInClient.getSignInIntent();
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			// The Task returned from this call is always completed, no need to attach
			// a listener.
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			handleSignInResult(task);
		}
	}

	private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
		try {
			GoogleSignInAccount account = completedTask.getResult(ApiException.class);
			// Signed in successfully, show authenticated UI.
			startActivity(new Intent(SignInActivity.this, SignInActivitySecond.class));
		} catch (ApiException e) {
			// The ApiException status code indicates the detailed failure reason.
			// Please refer to the GoogleSignInStatusCodes class reference for more information.
			Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
			Toast.makeText(SignInActivity.this, "Failed", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onStart() {
		// Check for existing Google Sign In account, if the user is already signed in
		// the GoogleSignInAccount will be non-null.
		GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
		if(account != null) {
			startActivity(new Intent(SignInActivity.this, SignInActivitySecond.class));
		}
		super.onStart();
	}
}