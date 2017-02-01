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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

@SuppressLint("SetJavaScriptEnabled")
public class MarketingActivity extends Activity {

	private static final String TAG = MarketingActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout
				.activity_standalone_advertising);

		TextView appName = (TextView) findViewById(R.id.title);
		appName.setText(ProjectManager.getInstance().getCurrentProject().getName());

		Bitmap bitmap = scaleDrawable2Bitmap();

		ImageButton imageView = (ImageButton) findViewById(R.id.pocket_code_image);
		imageView.setImageBitmap(bitmap);
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("market://details?id=org.catrobat.catroid");
				Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
				try {
					startActivity(myAppLinkToMarket);
				} catch (ActivityNotFoundException e) {
					ToastUtil.showError(MarketingActivity.this, R.string.main_menu_play_store_not_installed);
				}
			}
		});

		TextView website = (TextView) findViewById(R.id.website_link);
		TextView playstore = (TextView) findViewById(R.id.playStore_link);

		website.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String urlsString = ProjectManager.getInstance().getCurrentProject().getXmlHeader().getRemixParentsUrlString();
				if (urlsString == null || urlsString.length() == 0) {
					Log.w(TAG, "Header of program contains not even one valid detail url!");
					return;
				}

				List<String> extractedUrls = Utils.extractRemixUrlsFromString(urlsString);
				if (extractedUrls.size() == 0) {
					Log.w(TAG, "Header of program contains not even one valid detail url!");
					return;
				}

				String url = extractedUrls.get(0);
				if (!urlsString.trim().startsWith("http")) {
					url = Constants.MAIN_URL_HTTPS + urlsString;
				}
				Log.d(TAG, "Program detail url: " + url);
				startWebViewActivity(url);
			}
		});

		playstore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("market://search?q=Catrobat");
				Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
				try {
					startActivity(myAppLinkToMarket);
				} catch (ActivityNotFoundException e) {
					ToastUtil.showError(MarketingActivity.this, R.string.main_menu_play_store_not_installed);
				}
			}
		});
	}

	private Bitmap scaleDrawable2Bitmap() {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pocket_code);

		int width = ScreenValues.SCREEN_WIDTH;
		double factor = ((float) width / (float) bitmap.getWidth());
		int height = (int) ((float) bitmap.getHeight() * factor);
		Log.d("GSOC", "width: " + width + "  height: " + height + "   scaleFactor: " + (int) ((float) width / (float) bitmap.getWidth()));
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
		return scaledBitmap;
	}

	private void startWebViewActivity(String url) {
		// TODO just a quick fix for not properly working webview on old devices
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.BASE_URL_HTTPS));
			startActivity(browserIntent);
		} else {
			Intent intent = new Intent(MarketingActivity.this, WebViewActivity.class);
			intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url);
			startActivity(intent);
		}
	}
}
