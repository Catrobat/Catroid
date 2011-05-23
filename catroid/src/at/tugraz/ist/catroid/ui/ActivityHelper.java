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
package at.tugraz.ist.catroid.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import at.tugraz.ist.catroid.R;

/**
 * @author David Reisenberger
 * 
 */
public class ActivityHelper {
	private Activity activity;

	public ActivityHelper(Activity activity) {
		this.activity = activity;
	}

	public ViewGroup getActionBar() {
		return (ViewGroup) activity.findViewById(R.id.actionbar);
	}

	public boolean setupActionBar(boolean isHome, String title) {
		//TODO: check if first item should be the logo or a home-button
		final ViewGroup actionBar = getActionBar();
		if (actionBar == null) {
			return false;
		}

		ImageButton imgButton = new ImageButton(activity);
		if (isHome) {
			imgButton.setImageResource(R.drawable.catroid_logo);

			imgButton.setLayoutParams(
						new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) activity.getResources()
								.getDimension(R.dimen.actionbar_height)));

			imgButton.setBackgroundResource(0);
			imgButton.setScaleType(ImageView.ScaleType.CENTER);
			imgButton.setClickable(false);
			actionBar.addView(imgButton);
		} else {
			imgButton.setImageResource(R.drawable.ic_title_home);

			imgButton.setLayoutParams(
						new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) activity.getResources()
								.getDimension(R.dimen.actionbar_height)));

			imgButton.setBackgroundResource(0);
			imgButton.setScaleType(ImageView.ScaleType.CENTER);
			imgButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					goHome();
				}
			});
			actionBar.addView(imgButton);
		}

		//spring layout ensures that all consecutive items are added at the right side of the actionbar
		LinearLayout.LayoutParams springLayoutParams = new LinearLayout.LayoutParams(0,
				ViewGroup.LayoutParams.FILL_PARENT);
		springLayoutParams.weight = 1;
		View spring = new View(activity);
		spring.setLayoutParams(springLayoutParams);
		actionBar.addView(spring);

		return true;
	}

	public boolean addActionButton(int imgResId, View.OnClickListener clickListener, boolean separatorAfter) {
		final ViewGroup actionBar = getActionBar();

		if (actionBar == null) {
			return false;
		}

		ImageView separator = new ImageView(activity);
		separator.setLayoutParams(
				new ViewGroup.LayoutParams(2, ViewGroup.LayoutParams.FILL_PARENT));
		separator.setBackgroundResource(R.drawable.actionbar_separator);

		ImageButton imgButton = new ImageButton(activity);
		imgButton.setImageResource(imgResId);

		imgButton.setLayoutParams(
					new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) activity.getResources()
							.getDimension(R.dimen.actionbar_height)));

		imgButton.setBackgroundResource(0);
		imgButton.setScaleType(ImageView.ScaleType.CENTER);
		if (clickListener != null) {
			imgButton.setOnClickListener(clickListener);
		}

		if (!separatorAfter) {
			actionBar.addView(separator);
		}

		actionBar.addView(imgButton);

		if (separatorAfter) {
			actionBar.addView(separator);
		}
		return true;
	}

	private void goHome() {
		Intent intent = new Intent(activity, MainMenuActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
	}
}
