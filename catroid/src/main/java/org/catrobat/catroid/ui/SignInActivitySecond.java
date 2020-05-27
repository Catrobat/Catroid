/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.catrobat.catroid.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivitySecond extends AppCompatActivity {
	GoogleSignInClient mGoogleSignInClient;
	Button sign_out;
	TextView name, email, id;
	ImageView photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in_profile);

		sign_out = findViewById(R.id.log_out);
		name = findViewById(R.id.name);
		email = findViewById(R.id.email);
		id = findViewById(R.id.id);
		photo = findViewById(R.id.photo);

		// Configure sign-in to request the user's ID, email address, and basic
		// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.build();

		// Build a GoogleSignInClient with the options specified by gso.
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

		GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(SignInActivitySecond.this);
		if (acct != null) {
			String personName = acct.getDisplayName();
			String personGivenName = acct.getGivenName();
			String personFamilyName = acct.getFamilyName();
			String personEmail = acct.getEmail();
			String personId = acct.getId();
			Uri personPhoto = acct.getPhotoUrl();

			name.setText("Name: "+personName);
			email.setText("Email: "+personEmail);
			id.setText("ID: "+personId);
			Glide.with(this).load(personPhoto).into(photo);
		}

		sign_out.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				signOut();
			}
		});
	}

	private void signOut() {
		mGoogleSignInClient.signOut()
				.addOnCompleteListener(this, new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						Toast.makeText(SignInActivitySecond.this,"Successfully signed out", Toast.LENGTH_SHORT).show();
						startActivity(new Intent(SignInActivitySecond.this, SignInActivity.class));
						finish();
					}
				});
	}
}
