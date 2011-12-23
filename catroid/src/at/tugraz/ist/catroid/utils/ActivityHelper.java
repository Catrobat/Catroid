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
package at.tugraz.ist.catroid.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.ui.MainMenuActivity;

/**
 * @author David Reisenberger
 * 
 */
public class ActivityHelper {
	private Activity activity;
	private int buttonWidth;
	private int buttonHeight;
	private int logoWidth;
	private int tvMaxWidth;
	private TextView titleText;

	public ActivityHelper(Activity activity) {
		this.activity = activity;

		tvMaxWidth = 0;
	}

	public ViewGroup getActionBar() {
		return (ViewGroup) activity.findViewById(R.id.actionbar);
	}

	public void setupActionBar(boolean isMainMenu, String title) {
		final ViewGroup actionBar = getActionBar();
		Utils.updateScreenWidthAndHeight(activity);
		buttonWidth = (int) activity.getResources().getDimension(R.dimen.actionbar_height);
		buttonHeight = buttonWidth;
		logoWidth = (int) activity.getResources().getDimension(R.dimen.actionbar_catroid_logo);
		if (actionBar == null) {
			return;
		}
		LinearLayout.LayoutParams springLayoutParams = new LinearLayout.LayoutParams(0,
				ViewGroup.LayoutParams.FILL_PARENT);
		ImageButton imageButton = new ImageButton(activity);
		if (isMainMenu) {
			imageButton.setId(R.id.btn_home);
			imageButton.setImageResource(R.drawable.catroid_logo);
			int buttonWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
			int buttonHeight = (int) activity.getResources().getDimension(R.dimen.actionbar_height);
			imageButton.setLayoutParams(new ViewGroup.LayoutParams(buttonWidth, buttonHeight));

			imageButton.setBackgroundResource(0);
			imageButton.setScaleType(ImageView.ScaleType.CENTER);
			imageButton.setClickable(false);
		} else {
			imageButton.setId(R.id.btn_action_home);
			imageButton.setImageResource(R.drawable.ic_home_black);
			imageButton.setBackgroundResource(R.drawable.btn_actionbar_selector);

			imageButton.setLayoutParams(new ViewGroup.LayoutParams(buttonWidth, buttonHeight));
			imageButton.setScaleType(ImageView.ScaleType.CENTER);
			imageButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					goToMainMenu();
				}
			});
		}

		actionBar.addView(imageButton);

		ImageView separator = new ImageView(activity);
		separator.setBackgroundResource(R.drawable.actionbar_separator);
		separator.setLayoutParams(new ViewGroup.LayoutParams(2, ViewGroup.LayoutParams.FILL_PARENT));

		actionBar.addView(separator);

		LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.FILL_PARENT);

		int paddingRight = 10;
		if (isMainMenu) {
			tvMaxWidth = Values.SCREEN_WIDTH - logoWidth - paddingRight;
		} else {
			tvMaxWidth = Values.SCREEN_WIDTH - buttonWidth - paddingRight;
		}

		titleText = new TextView(activity);
		titleText.setLayoutParams(textViewLayout);
		titleText.setId(R.id.tv_title);
		titleText.setText(title);
		titleText.setGravity(Gravity.CENTER_VERTICAL);
		titleText.setTypeface(null, Typeface.BOLD);
		titleText.setTextColor(Color.BLACK);
		titleText.setSingleLine(true);
		titleText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		titleText.setMarqueeRepeatLimit(-1);
		titleText.setSelected(true);
		titleText.setPadding(5, 0, 0, 0);
		titleText.setMaxWidth(tvMaxWidth);

		titleText.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Utils.displayToast(activity, titleText.getText().toString());
			}
		});

		actionBar.addView(titleText);

		//spring layout ensures that all consecutive items are added at the far right side of the actionbar
		springLayoutParams.weight = 1;
		View spring = new View(activity);
		spring.setLayoutParams(springLayoutParams);
		actionBar.addView(spring);
	}

	public boolean addActionButton(int buttonId, int imageResourceId, View.OnClickListener clickListener,
			boolean separatorAfter) {
		final ViewGroup actionBar = getActionBar();
		if (actionBar == null) {
			return false;
		}

		ImageView separator = new ImageView(activity);
		separator.setLayoutParams(new ViewGroup.LayoutParams(2, ViewGroup.LayoutParams.FILL_PARENT));
		separator.setBackgroundResource(R.drawable.actionbar_separator);

		ImageButton imageButton = new ImageButton(activity);
		imageButton.setId(buttonId);
		imageButton.setImageResource(imageResourceId);

		//2 times actionbar_height, cause we want the button to be square
		int buttonWidth = (int) activity.getResources().getDimension(R.dimen.actionbar_height);
		int buttonHeight = (int) activity.getResources().getDimension(R.dimen.actionbar_height);
		imageButton.setLayoutParams(new ViewGroup.LayoutParams(buttonWidth, buttonHeight));

		imageButton.setBackgroundResource(R.drawable.btn_actionbar_selector);
		imageButton.setScaleType(ImageView.ScaleType.CENTER);
		if (clickListener != null) {
			imageButton.setOnClickListener(clickListener);
		}

		if (!separatorAfter) {
			actionBar.addView(separator);
		}

		actionBar.addView(imageButton);

		if (separatorAfter) {
			actionBar.addView(separator);
		}
		tvMaxWidth -= buttonWidth;
		titleText.setMaxWidth(tvMaxWidth);
		return true;
	}

	public void changeClickListener(int resourceId, View.OnClickListener clickListener) {
		final ViewGroup actionBar = getActionBar();
		if (actionBar == null) {
			return;
		}

		View buttonView = actionBar.findViewById(resourceId);
		if (buttonView != null) {
			buttonView.setOnClickListener(clickListener);
		}
	}

	public void changeButtonIcon(int resourceId, int imageRecourseId) {
		final ViewGroup actionBar = getActionBar();
		if (actionBar == null) {
			return;
		}

		ImageButton imageButtonView = (ImageButton) actionBar.findViewById(resourceId);
		if (imageButtonView != null) {
			imageButtonView.setImageResource(imageRecourseId);
		}
	}

	private void goToMainMenu() {
		Intent intent = new Intent(activity, MainMenuActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
	}
}
