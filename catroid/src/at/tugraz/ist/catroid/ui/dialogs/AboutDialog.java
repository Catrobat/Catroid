/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Window;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;

public class AboutDialog extends Dialog {

	private Context context;
	
    public AboutDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_LEFT_ICON);
    	setContentView(R.layout.dialog_about);
    	setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);
    	setTitle(R.string.about_title);
    	setCanceledOnTouchOutside(true);
    	
//    	Resources res = context.getResources();
//    	String text = String.format(res.getString(R.string.about_link_template), res.getString(R.string.about_catroid_url), res.getString(R.string.about_link_text));
//    	text = String.format(res.getString(R.string.about_link_template), "http://www.google.at", "World");
//    	CharSequence htmlText = Html.fromHtml(text);
    	
//    	System.out.println(text);
//    	System.out.println(htmlText);
    	
    	TextView aboutTextView = (TextView)findViewById(R.id.tvAboutURL);
//    	aboutTextView.append(text);
//    	aboutTextView.setAutoLinkMask(Linkify.ALL);
    	aboutTextView.setMovementMethod(LinkMovementMethod.getInstance());
//    	aboutTextView.setText(text);
    	
    }

}
