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
package org.catrobat.catroid.transfers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

public class GoogleLogInTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = GoogleLogInTask.class.getSimpleName();

    private Context context;
    private ProgressDialog progressDialog;
    private String mail;
    private String username;
    private String id;
    private String locale;
    private String message;
    private OnGoogleLogInCompleteListener onGoogleLogInCompleteListener;

    public GoogleLogInTask(FragmentActivity activity, String mail, String username, String id, String locale) {
        this.context = activity;
        this.mail = mail;
        this.username = username;
        this.id = id;
        this.locale = locale;
    }

    public void setOnGoogleLogInCompleteListener(OnGoogleLogInCompleteListener listener) {
        onGoogleLogInCompleteListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (context == null) {
            return;
        }
        String title = context.getString(R.string.please_wait);
        String message = context.getString(R.string.loading_google_login);
        progressDialog = ProgressDialog.show(context, title, message);

    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        try {
            if (!Utils.isNetworkAvailable(context)) {
                return false;
            }

            return ServerCalls.getInstance().googleLogin(mail, username, id, locale, context);
        } catch (WebconnectionException webconnectionException) {
            Log.e(TAG, Log.getStackTraceString(webconnectionException));
            message = webconnectionException.getMessage();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean userSignedIn) {
        super.onPostExecute(userSignedIn);

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (!userSignedIn) {
            showDialog(R.string.error_internet_connection);
            return;
        }

        if (context == null) {
            return;
        }

        if (userSignedIn) {
            ToastUtil.showSuccess(context, R.string.user_logged_in);
        }

        if (onGoogleLogInCompleteListener != null) {
            onGoogleLogInCompleteListener.onGoogleLogInComplete();
        }
    }

    private void showDialog(int messageId) {
        if (context == null) {
            return;
        }
        if (message == null) {
            new CustomAlertDialogBuilder(context).setTitle(R.string.register_error).setMessage(messageId)
                    .setPositiveButton(R.string.ok, null).show();
        } else {
            new CustomAlertDialogBuilder(context).setTitle(R.string.register_error).setMessage(message)
                    .setPositiveButton(R.string.ok, null).show();
        }
    }

    public interface OnGoogleLogInCompleteListener {
        void onGoogleLogInComplete();
    }

}
