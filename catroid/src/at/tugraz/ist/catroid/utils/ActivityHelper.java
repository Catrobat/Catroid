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
package at.tugraz.ist.catroid.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.MainMenuActivity;

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

	public void setupActionBar(boolean isMainMenu, String title) {
		final ViewGroup actionBar = getActionBar();
		if (actionBar == null) {
			return;
		}
		LinearLayout.LayoutParams springLayoutParams = new LinearLayout.LayoutParams(0,
				ViewGroup.LayoutParams.FILL_PARENT);
		ImageButton imgButton = new ImageButton(activity);
		if (isMainMenu) {
			imgButton.setId(R.id.btn_home);
			imgButton.setImageResource(R.drawable.catroid_logo);
			int buttonWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
			int buttonHeight = (int) activity.getResources().getDimension(R.dimen.actionbar_height);
			imgButton.setLayoutParams(new ViewGroup.LayoutParams(buttonWidth, buttonHeight));

			imgButton.setBackgroundResource(0);
			imgButton.setScaleType(ImageView.ScaleType.CENTER);
			imgButton.setClickable(false);
			actionBar.addView(imgButton);
		} else {
			imgButton.setId(R.id.btn_action_home);
			imgButton.setImageResource(R.drawable.ic_home_black);
			imgButton.setBackgroundResource(R.drawable.btn_actionbar_selector);

			//2 times actionbar_height, cause we want the button to be square
			int buttonWidth = (int) activity.getResources().getDimension(R.dimen.actionbar_height);
			int buttonHeight = (int) activity.getResources().getDimension(R.dimen.actionbar_height);
			imgButton.setLayoutParams(new ViewGroup.LayoutParams(buttonWidth, buttonHeight));
			imgButton.setScaleType(ImageView.ScaleType.CENTER);
			imgButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					goToMainMenu();
				}
			});

			actionBar.addView(imgButton);

			ImageView separator = new ImageView(activity);
			separator.setBackgroundResource(R.drawable.actionbar_separator);
			separator.setLayoutParams(
					new ViewGroup.LayoutParams(2, ViewGroup.LayoutParams.FILL_PARENT));

			actionBar.addView(separator);

			TextView titleText = new TextView(activity);
			titleText.setLayoutParams(springLayoutParams);
			titleText.setText(title);
			titleText.setGravity(Gravity.CENTER_VERTICAL);
			titleText.setTypeface(null, Typeface.BOLD);
			titleText.setPadding(10, 0, 0, 0);

			actionBar.addView(titleText);
		}

		//spring layout ensures that all consecutive items are added at the far right side of the actionbar
		springLayoutParams.weight = 1;
		View spring = new View(activity);
		spring.setLayoutParams(springLayoutParams);
		actionBar.addView(spring);
	}

	public boolean addActionButton(int buttonId, int imgResId, View.OnClickListener clickListener,
			boolean separatorAfter) {
		final ViewGroup actionBar = getActionBar();

		if (actionBar == null) {
			return false;
		}

		ImageView separator = new ImageView(activity);
		separator.setLayoutParams(
				new ViewGroup.LayoutParams(2, ViewGroup.LayoutParams.FILL_PARENT));
		separator.setBackgroundResource(R.drawable.actionbar_separator);

		ImageButton imgButton = new ImageButton(activity);
		imgButton.setId(buttonId);
		imgButton.setImageResource(imgResId);

		//2 times actionbar_height, cause we want the button to be square
		int buttonWidth = (int) activity.getResources().getDimension(R.dimen.actionbar_height);
		int buttonHeight = (int) activity.getResources().getDimension(R.dimen.actionbar_height);
		imgButton.setLayoutParams(new ViewGroup.LayoutParams(buttonWidth, buttonHeight));

		imgButton.setBackgroundResource(R.drawable.btn_actionbar_selector);
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

	private void goToMainMenu() {
		Intent intent = new Intent(activity, MainMenuActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
	}
}
