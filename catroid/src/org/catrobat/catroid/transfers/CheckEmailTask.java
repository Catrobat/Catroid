/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.transfers;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import org.catrobat.catroid.R;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

public class CheckEmailTask extends AsyncTask<Void, Void, Boolean> {

	private Context context;
	private ProgressDialog progressDialog;
	private String email;

	private String message;
	private boolean emailAvailable;

	private OnCheckEmailCompleteListener onCheckEmailCompleteListener;

	public CheckEmailTask(Context activity, String email) {
		this.context = activity;
		this.email = email;
	}

	public void setOnCheckEmailCompleteListener(OnCheckEmailCompleteListener listener) {
		onCheckEmailCompleteListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (context == null) {
			return;
		}
		String title = context.getString(R.string.please_wait);
		String message = context.getString(R.string.loading);
		progressDialog = ProgressDialog.show(context, title, message);
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
        return true;
        /*
        //TODO: implement with Marko
        try {
			if (!Utils.isNetworkAvailable(context)) {
				return false;
			}

			emailAvailable = ServerCalls.getInstance().checkEmail(email, context);
            return emailAvailable;
            return true;
		} catch (WebconnectionException exception) {
			exception.printStackTrace();
			message = exception.getMessage();
		}
		return false;
		*/
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (!result) {
			showDialog(R.string.error_internet_connection);
			return;
		}

		if (context == null) {
			return;
		}

		if (onCheckEmailCompleteListener != null) {
			onCheckEmailCompleteListener.onCheckEmailComplete(true);
		}
	}

	private void showDialog(int messageId) {
		if (context == null) {
			return;
		}
		if (message == null) {
			new Builder(context).setTitle(R.string.register_error).setMessage(messageId).setPositiveButton(R.string.ok, null)
					.show().setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							if (onCheckEmailCompleteListener != null) {
								onCheckEmailCompleteListener.onCheckEmailComplete(false);
							}
						}
					});
		} else {
			new Builder(context).setTitle(R.string.register_error).setMessage(message).setPositiveButton(R.string.ok, null)
					.show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (onCheckEmailCompleteListener != null) {
                        onCheckEmailCompleteListener.onCheckEmailComplete(false);
                    }
                }
            });
		}
	}

	public interface OnCheckEmailCompleteListener {
		public void onCheckEmailComplete(boolean success);
	}
}
