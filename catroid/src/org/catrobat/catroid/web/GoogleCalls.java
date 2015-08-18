/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.web;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.CheckEmailAvailableTask;
import org.catrobat.catroid.transfers.GoogleFetchCodeTask;
import org.catrobat.catroid.ui.dialogs.SignInDialog;
import org.json.JSONObject;

public final class GoogleCalls {

	private static final String TAG = GoogleCalls.class.getSimpleName();

	private static final GoogleCalls INSTANCE = new GoogleCalls();

	private GoogleCalls() {

	}

	public static GoogleCalls getInstance() {
		return INSTANCE;
	}

	private OnGetGoogleUserInfoCompleteListener onGetGoogleUserInfoCompleteListener;

	public interface OnGetGoogleUserInfoCompleteListener {
        void onGetGoogleUserInfoComplete(GraphResponse response);
    }

    public interface OnGetGoogleTokenCompleteListener {
        void onGetGoogleTokenComplete(GraphResponse response);
    }

	public void setOnGetGoogleUserInfoCompleteListener(OnGetGoogleUserInfoCompleteListener listener) {
		onGetGoogleUserInfoCompleteListener = listener;
	}

	public void getGoogleUserInfo(String accessToken) {

	}

    public void getGoogleAuthorizationCode(Activity context, String accountName, SignInDialog signInDialog) {
        GoogleFetchCodeTask googleFetchCodeTask = new GoogleFetchCodeTask(context, accountName, signInDialog);
		googleFetchCodeTask.setOnGoogleFetchCodeCompleteListener(signInDialog);
        googleFetchCodeTask.execute();
    }

}
