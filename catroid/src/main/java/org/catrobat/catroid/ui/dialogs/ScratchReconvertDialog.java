/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import org.catrobat.catroid.R;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ScratchReconvertDialog extends DialogFragment {
	public interface ReconvertDialogCallback {
		void onDownloadExistingProgram();
		void onReconvertProgram();
		void onUserCanceledConversion();
	}

	private static final String TAG = ScratchReconvertDialog.class.getSimpleName();
	public static final String DIALOG_FRAGMENT_TAG = "scratch_reconvert_dialog";

	protected RadioButton downloadExistingProgramRadioButton;
	protected RadioButton reconvertProgramRadioButton;
	protected Context context;
	protected Date cachedDate;
	protected ReconvertDialogCallback callback;

	public ScratchReconvertDialog() {
		super();
	}

	public void setCachedDate(final Date cachedDate) {
		this.cachedDate = cachedDate;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setReconvertDialogCallback(final ReconvertDialogCallback callback) {
		this.callback = callback;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_scratch_reconvert, null);

		downloadExistingProgramRadioButton = (RadioButton) dialogView.findViewById(R.id.dialog_scratch_reconvert_radio_download);
		reconvertProgramRadioButton = (RadioButton) dialogView.findViewById(R.id.dialog_scratch_reconvert_radio_reconvert);

		final Date now = new Date();
		final long timeDifferenceInMS = now.getTime() - cachedDate.getTime();
		final int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(timeDifferenceInMS);
		final int hours = (int) TimeUnit.MILLISECONDS.toHours(timeDifferenceInMS);
		final int days = (int) TimeUnit.MILLISECONDS.toDays(timeDifferenceInMS);

		final String quantityString;
		if (days > 0) {
			quantityString = getResources().getQuantityString(R.plurals.days, days, days);
		} else if (hours > 0) {
			quantityString = getResources().getQuantityString(R.plurals.hours, hours, hours);
		} else {
			quantityString = getResources().getQuantityString(R.plurals.minutes, minutes, minutes);
		}
		final String titleText = getResources().getString(R.string.reconvert_text, quantityString);

		Dialog dialog = new AlertDialog.Builder(getActivity()).setView(dialogView).setTitle(titleText)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handleOkButton();
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d(TAG, "User canceled dialog by pressing Cancel-button");
						ToastUtil.showError(context, R.string.notification_reconvert_download_program_cancel);
						if (callback != null) {
							callback.onUserCanceledConversion();
						}
					}
				}).create();

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						handleOkButton();
					}
				});
			}
		});

		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					boolean okButtonResult = handleOkButton();
					if (okButtonResult) {
						dismiss();
					}
					return okButtonResult;
				} else if (keyCode == KeyEvent.KEYCODE_BACK) {
					Log.d(TAG, "User canceled dialog by pressing Back-button");
					ToastUtil.showError(context, R.string.notification_reconvert_download_program_cancel);
					if (callback != null) {
						callback.onUserCanceledConversion();
					}
					dismiss();
					return true;
				}

				return false;
			}
		});

		return dialog;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		Log.d(TAG, "User canceled dialog by clicking outside of the Dialog fragment");
		ToastUtil.showError(context, R.string.notification_reconvert_download_program_cancel);
		if (callback != null) {
			callback.onUserCanceledConversion();
		}
	}

	private boolean handleOkButton() {
		if (downloadExistingProgramRadioButton.isChecked()) {
			callback.onDownloadExistingProgram();
		} else if (reconvertProgramRadioButton.isChecked()) {
			callback.onReconvertProgram();
		}
		dismiss();
		return true;
	}
}
