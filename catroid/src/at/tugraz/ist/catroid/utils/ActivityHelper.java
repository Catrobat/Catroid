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
import android.util.TypedValue;
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
	private int padding;
	private TextView titleText;

	public ActivityHelper(Activity activity) {
		this.activity = activity;
		this.padding = Utils.getPhysicalPixels(5, activity);

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
		LinearLayout linearLayout = new LinearLayout(activity);
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
			linearLayout.setId(R.id.btn_action_home);
			linearLayout.setBackgroundResource(R.drawable.btn_actionbar_selector);
			linearLayout.setLayoutParams(new ViewGroup.LayoutParams(buttonWidth, buttonHeight));
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			linearLayout.setGravity(Gravity.CENTER);

			ImageView image = new ImageView(activity);
			image.setImageResource(R.drawable.ic_home_black);
			image.setPadding(0, padding, 0, 0);

			TextView text = new TextView(activity);
			text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			text.setGravity(Gravity.CENTER);
			text.setText(activity.getString(R.string.home));
			text.setTypeface(null, Typeface.BOLD);

			linearLayout.addView(image);
			linearLayout.addView(text);
			linearLayout.setClickable(true);
			linearLayout.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					goToMainMenu();
				}
			});

		}
		if (isMainMenu) {
			actionBar.addView(imageButton);
		} else {
			actionBar.addView(linearLayout);
		}
		ImageView separator = new ImageView(activity);
		separator.setBackgroundResource(R.drawable.actionbar_separator);
		separator.setLayoutParams(new ViewGroup.LayoutParams(2, ViewGroup.LayoutParams.FILL_PARENT));

		actionBar.addView(separator);

		LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.FILL_PARENT);

		int paddingRight = 2 * padding;
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
		titleText.setPadding(padding, 0, 0, 0);
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

	public boolean addActionButton(int buttonId, int imageResourceId, int textId, View.OnClickListener clickListener,
			boolean separatorAfter) {
		final ViewGroup actionBar = getActionBar();
		if (actionBar == null) {
			return false;
		}

		ImageView separator = new ImageView(activity);
		separator.setLayoutParams(new ViewGroup.LayoutParams(2, ViewGroup.LayoutParams.FILL_PARENT));
		separator.setBackgroundResource(R.drawable.actionbar_separator);

		if (!separatorAfter) {
			actionBar.addView(separator);
		}
		LinearLayout linearLayout = new LinearLayout(activity);
		linearLayout.setId(buttonId);
		linearLayout.setBackgroundResource(R.drawable.btn_actionbar_selector);
		linearLayout.setLayoutParams(new ViewGroup.LayoutParams(buttonWidth, buttonHeight));
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setGravity(Gravity.CENTER);

		ImageView image = new ImageView(activity);
		image.setImageResource(imageResourceId);
		image.setPadding(0, padding, 0, 0);

		TextView text = new TextView(activity);
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		text.setGravity(Gravity.CENTER);
		text.setText(activity.getString(textId));
		text.setTypeface(null, Typeface.BOLD);

		linearLayout.addView(image);
		linearLayout.addView(text);
		linearLayout.setClickable(true);
		linearLayout.setOnClickListener(clickListener);

		actionBar.addView(linearLayout);

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
		ImageView image = (ImageView) ((LinearLayout) (actionBar.findViewById(resourceId))).getChildAt(0);
		if (image != null) {
			image.setImageResource(imageRecourseId);
		}
	}

	private void goToMainMenu() {
		Intent intent = new Intent(activity, MainMenuActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
	}
}
