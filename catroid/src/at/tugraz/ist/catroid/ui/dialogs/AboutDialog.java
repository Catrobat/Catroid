/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Window;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.utils.Utils;

public class AboutDialog extends Dialog {

	private Context context;

	public AboutDialog(Context context) {
		super(context, R.style.custom_dialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.dialog_about);
		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);

		setTitle(R.string.about_title);
		setCanceledOnTouchOutside(true);

		TextView aboutUrlTextView = (TextView) findViewById(R.id.dialog_about_url_text_view);
		aboutUrlTextView.setMovementMethod(LinkMovementMethod.getInstance());

		Resources resources = context.getResources();
		String aboutUrl = String.format(resources.getString(R.string.about_link_template),
				resources.getString(R.string.about_catroid_url), resources.getString(R.string.about_link_text));

		aboutUrlTextView.setText(Html.fromHtml(aboutUrl));

		TextView aboutVersionNameTextView = (TextView) findViewById(R.id.dialog_about_version_name_text_view);
		String versionName = Utils.getVersionName(context);
		aboutVersionNameTextView.setText(versionName);
	}
}
