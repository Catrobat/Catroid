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

package org.catrobat.catroid.ui;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import org.catrobat.catroid.R;

public class GoogleCodeInActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_code_in);
		findViewById(R.id.gci_ok_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GoogleCodeInActivity.this.finish();
			}
		});
		final ScrollView scrollView = (ScrollView) findViewById(R.id.gci_scroll_view);
		findViewById(R.id.gci_linear_layout).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		findViewById(R.id.gci_prizes).setPadding(size.x / 6, 25, size.x / 6, 25);
	}
}
