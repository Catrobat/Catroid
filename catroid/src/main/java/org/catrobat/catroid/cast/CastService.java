/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.cast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.widget.RelativeLayout;

import com.google.android.gms.cast.CastPresentation;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;

import org.catrobat.catroid.R;
import org.catrobat.catroid.utils.ToastUtil;

public class CastService extends CastRemoteDisplayLocalService {

	private Display display;
	private CastPresentation presentation;

	@Override
	public void onCreatePresentation(Display display) {
		createPresentation(display);
	}

	@Override
	public void onDismissPresentation() {
		dismissPresentation();
	}
	@SuppressLint("NewApi")
	private void dismissPresentation() {
		if (presentation != null) {
			presentation.dismiss();
			presentation = null;
		}
	}
	@SuppressLint("NewApi")
	public void createPresentation(Display display) {
		if (display != null) {
			this.display = display;
		}
		dismissPresentation();
		presentation = new FirstScreenPresentation(this, this.display);

		try {
			presentation.show();
		} catch (Exception ex) {
			ToastUtil.showError(getApplicationContext(), getString(R.string.cast_error_not_connected_msg));
			//When does this happen?
			dismissPresentation();
		}
	}

	public class FirstScreenPresentation extends CastPresentation {

		public FirstScreenPresentation(Context serviceContext, Display display) {
			super(serviceContext, display);
		}

		@Override
		@SuppressLint("NewApi")
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			RelativeLayout layout = new RelativeLayout(getApplication());
			setContentView(layout);

			CastManager.getInstance().setIsConnected(true);
			CastManager.getInstance().setRemoteLayout(layout);
			CastManager.getInstance().setRemoteLayoutToIdleScreen(getApplicationContext());
		}
	}
}
