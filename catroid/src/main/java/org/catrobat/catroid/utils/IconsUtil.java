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

package org.catrobat.catroid.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BrickLayout;

public final class IconsUtil {

	private static boolean activated = false;
	private static boolean contrast = false;
	private static boolean largeSize = false;

	public static Rect smallIconSize = new Rect(0, 0, 64, 64);
	public static Rect largeIconSize = new Rect(0, 0, 128, 128);
	private static Rect smallIconSizeCategory = new Rect(-20, 0, 75, 95);
	private static Rect largeIconSizeCategory = new Rect(-20, 0, 145, 160);
	private static Rect largeIconSizeMainMenu = new Rect(0, 0, 150, 150);
	private static Rect largeIconSizeProgramMenu = new Rect(0, 0, 150, 150);
	private static Rect largeIconSizeStageDialog = new Rect(0, 0, 100, 100);
	private static Rect largeIconSizeStageDialogContinue = new Rect(0, 0, 150, 150);
	private static int largeIconBottomBar = 75;

	private static String uglySpacingString = " ";

	private IconsUtil() {
	}

	public static void addIcon(Context context, TextView textView, String category) {
		if (isActivated()) {
			Drawable drawable = null;

			if (isContrast()) {
				if (category.equals(context.getString(R.string.category_control))) {
					drawable = context.getResources().getDrawable(R.drawable.control_neg);
				} else if (category.equals(context.getString(R.string.category_motion))) {
					drawable = context.getResources().getDrawable(R.drawable.motion_neg);
				} else if (category.equals(context.getString(R.string.category_sound))) {
					drawable = context.getResources().getDrawable(R.drawable.sound_neg);
				} else if (category.equals(context.getString(R.string.category_looks))) {
					drawable = context.getResources().getDrawable(R.drawable.look_neg);
				} else if (category.equals(context.getString(R.string.category_user_bricks))) {
					drawable = context.getResources().getDrawable(R.drawable.userbrick_neg);
				} else if (category.equals(context.getString(R.string.category_data))) {
					drawable = context.getResources().getDrawable(R.drawable.data_neg);
				} else if (category.equals(context.getString(R.string.category_lego_nxt))) {
					drawable = context.getResources().getDrawable(R.drawable.legonxt_neg);
				} else if (category.equals(context.getString(R.string.category_arduino))) {
					drawable = context.getResources().getDrawable(R.drawable.arduino_neg);
				} else if (category.equals(context.getString(R.string.category_drone))) {
					drawable = context.getResources().getDrawable(R.drawable.ardrone_neg);
				} else if (category.equals(context.getString(R.string.category_phiro))) {
					drawable = context.getResources().getDrawable(R.drawable.phiro_neg);
				} else if (category.equals(context.getString(R.string.category_raspi))) {
					drawable = context.getResources().getDrawable(R.drawable.raspberrypi_neg);
				} else if (category.equals(context.getString(R.string.category_event))) {
					drawable = context.getResources().getDrawable(R.drawable.event_neg);
				} else if (category.equals(context.getString(R.string.category_pen))) {
					drawable = context.getResources().getDrawable(R.drawable.pen_neg);
				}
			} else {
				if (category.equals(context.getString(R.string.category_control))) {
					drawable = context.getResources().getDrawable(R.drawable.control_pos);
				} else if (category.equals(context.getString(R.string.category_motion))) {
					drawable = context.getResources().getDrawable(R.drawable.motion_pos);
				} else if (category.equals(context.getString(R.string.category_sound))) {
					drawable = context.getResources().getDrawable(R.drawable.sound_pos);
				} else if (category.equals(context.getString(R.string.category_looks))) {
					drawable = context.getResources().getDrawable(R.drawable.look_pos);
				} else if (category.equals(context.getString(R.string.category_user_bricks))) {
					drawable = context.getResources().getDrawable(R.drawable.userbrick_pos);
				} else if (category.equals(context.getString(R.string.category_data))) {
					drawable = context.getResources().getDrawable(R.drawable.data_pos);
				} else if (category.equals(context.getString(R.string.category_lego_nxt))) {
					drawable = context.getResources().getDrawable(R.drawable.legonxt_pos);
				} else if (category.equals(context.getString(R.string.category_arduino))) {
					drawable = context.getResources().getDrawable(R.drawable.arduino_pos);
				} else if (category.equals(context.getString(R.string.category_drone))) {
					drawable = context.getResources().getDrawable(R.drawable.ardrone_pos);
				} else if (category.equals(context.getString(R.string.category_phiro))) {
					drawable = context.getResources().getDrawable(R.drawable.phiro_pos);
				} else if (category.equals(context.getString(R.string.category_raspi))) {
					drawable = context.getResources().getDrawable(R.drawable.raspberrypi_pos);
				} else if (category.equals(context.getString(R.string.category_event))) {
					drawable = context.getResources().getDrawable(R.drawable.event_pos);
				} else if (category.equals(context.getString(R.string.category_pen))) {
					drawable = context.getResources().getDrawable(R.drawable.pen_pos);
				}
			}

			if (drawable != null) {
				if (isLargeSize()) {
					drawable.setBounds(largeIconSize);
				} else {
					drawable.setBounds(smallIconSize);
				}
				String textWithSpacing = uglySpacingString + textView.getText();
				textView.setText(textWithSpacing);
				textView.setGravity(Gravity.FILL_VERTICAL);
				textView.setCompoundDrawables(drawable, null, null, null);
			}
		}
	}

	public static void addIconCategory(Context context, TextView textView, String category) {
		if (isActivated()) {
			Drawable drawable = null;

			if (isContrast()) {
				if (category.equals(context.getString(R.string.category_control))) {
					drawable = context.getResources().getDrawable(R.drawable.control_neg);
				} else if (category.equals(context.getString(R.string.category_motion))) {
					drawable = context.getResources().getDrawable(R.drawable.motion_neg);
				} else if (category.equals(context.getString(R.string.category_sound))) {
					drawable = context.getResources().getDrawable(R.drawable.sound_neg);
				} else if (category.equals(context.getString(R.string.category_looks))) {
					drawable = context.getResources().getDrawable(R.drawable.look_neg);
				} else if (category.equals(context.getString(R.string.category_user_bricks))) {
					drawable = context.getResources().getDrawable(R.drawable.userbrick_neg);
				} else if (category.equals(context.getString(R.string.category_data))) {
					drawable = context.getResources().getDrawable(R.drawable.data_neg);
				} else if (category.equals(context.getString(R.string.category_lego_nxt))) {
					drawable = context.getResources().getDrawable(R.drawable.legonxt_neg);
				} else if (category.equals(context.getString(R.string.category_arduino))) {
					drawable = context.getResources().getDrawable(R.drawable.arduino_neg);
				} else if (category.equals(context.getString(R.string.category_drone))) {
					drawable = context.getResources().getDrawable(R.drawable.ardrone_neg);
				} else if (category.equals(context.getString(R.string.category_phiro))) {
					drawable = context.getResources().getDrawable(R.drawable.phiro_neg);
				} else if (category.equals(context.getString(R.string.category_raspi))) {
					drawable = context.getResources().getDrawable(R.drawable.raspberrypi_neg);
				} else if (category.equals(context.getString(R.string.category_event))) {
					drawable = context.getResources().getDrawable(R.drawable.event_neg);
				} else if (category.equals(context.getString(R.string.category_pen))) {
					drawable = context.getResources().getDrawable(R.drawable.pen_neg);
				}
			} else {
				if (category.equals(context.getString(R.string.category_control))) {
					drawable = context.getResources().getDrawable(R.drawable.control_pos);
				} else if (category.equals(context.getString(R.string.category_motion))) {
					drawable = context.getResources().getDrawable(R.drawable.motion_pos);
				} else if (category.equals(context.getString(R.string.category_sound))) {
					drawable = context.getResources().getDrawable(R.drawable.sound_pos);
				} else if (category.equals(context.getString(R.string.category_looks))) {
					drawable = context.getResources().getDrawable(R.drawable.look_pos);
				} else if (category.equals(context.getString(R.string.category_user_bricks))) {
					drawable = context.getResources().getDrawable(R.drawable.userbrick_pos);
				} else if (category.equals(context.getString(R.string.category_data))) {
					drawable = context.getResources().getDrawable(R.drawable.data_pos);
				} else if (category.equals(context.getString(R.string.category_lego_nxt))) {
					drawable = context.getResources().getDrawable(R.drawable.legonxt_pos);
				} else if (category.equals(context.getString(R.string.category_arduino))) {
					drawable = context.getResources().getDrawable(R.drawable.arduino_pos);
				} else if (category.equals(context.getString(R.string.category_drone))) {
					drawable = context.getResources().getDrawable(R.drawable.ardrone_pos);
				} else if (category.equals(context.getString(R.string.category_phiro))) {
					drawable = context.getResources().getDrawable(R.drawable.phiro_pos);
				} else if (category.equals(context.getString(R.string.category_raspi))) {
					drawable = context.getResources().getDrawable(R.drawable.raspberrypi_pos);
				} else if (category.equals(context.getString(R.string.category_event))) {
					drawable = context.getResources().getDrawable(R.drawable.event_pos);
				} else if (category.equals(context.getString(R.string.category_pen))) {
					drawable = context.getResources().getDrawable(R.drawable.pen_pos);
				}
			}

			if (drawable != null) {
				if (isLargeSize()) {
					drawable.setBounds(largeIconSizeCategory);
				} else {
					drawable.setBounds(smallIconSizeCategory);
				}
				textView.setCompoundDrawables(drawable, null, null, null);
			}
		}
	}

	public static void addIcons(ViewGroup viewGroup, String category) {
		if (!isActivated()) {
			return;
		}
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			if (viewGroup.getChildAt(i) instanceof BrickLayout) {
				BrickLayout l = (BrickLayout) viewGroup.getChildAt(i);
				if (l.getChildAt(0) instanceof TextView) {
					TextView textView = (TextView) l.getChildAt(0);
					addIcon(viewGroup.getContext(), textView, category);
				} else if (l.getChildAt(0) instanceof RelativeLayout) {
					RelativeLayout rl = (RelativeLayout) l.getChildAt(0);
					if (rl.getChildAt(0) instanceof LinearLayout) {
						LinearLayout ll = (LinearLayout) rl.getChildAt(0);
						if (ll.getChildAt(0) instanceof TextView) {
							TextView textView = (TextView) ll.getChildAt(0);
							addIcon(viewGroup.getContext(), textView, category);
						}
					}
				} else if (l.getChildAt(0) instanceof LinearLayout) {
					LinearLayout ll = (LinearLayout) l.getChildAt(0);
					if (ll.getChildAt(0) instanceof TableRow) {
						TableRow tr = (TableRow) ll.getChildAt(0);
						if (tr.getChildAt(0) instanceof TextView) {
							TextView textView = (TextView) tr.getChildAt(0);
							addIcon(viewGroup.getContext(), textView, category);
						}
					}
				}
			}
		}
	}

	public static void setMainMenuIconSize(Context context, View contentView) {
		if (!isLargeSize()) {
			return;
		}

		setLeftDrawableSize(context, contentView, largeIconSizeMainMenu, R.id.main_menu_button_continue,
				R.drawable.ic_main_menu_continue);
		setLeftDrawableSize(context, contentView, largeIconSizeMainMenu, R.id.main_menu_button_new,
				R.drawable.ic_main_menu_new);
		setLeftDrawableSize(context, contentView, largeIconSizeMainMenu, R.id.main_menu_button_programs,
				R.drawable.ic_main_menu_programs);
		setLeftDrawableSize(context, contentView, largeIconSizeMainMenu, R.id.main_menu_button_help,
				R.drawable.ic_main_menu_help);
		setLeftDrawableSize(context, contentView, largeIconSizeMainMenu, R.id.main_menu_button_web,
				R.drawable.ic_main_menu_community);
		setLeftDrawableSize(context, contentView, largeIconSizeMainMenu, R.id.main_menu_button_upload,
				R.drawable.ic_main_menu_upload);
	}

	public static void setProgramMenuIconSize(Context context, View contentView) {
		if (!isLargeSize()) {
			return;
		}

		setLeftDrawableSize(context, contentView, largeIconSizeProgramMenu, R.id.program_menu_button_scripts,
				R.drawable.ic_program_menu_scripts);
		setLeftDrawableSize(context, contentView, largeIconSizeProgramMenu, R.id.program_menu_button_looks,
				R.drawable.ic_program_menu_looks);
		setLeftDrawableSize(context, contentView, largeIconSizeProgramMenu, R.id.program_menu_button_sounds,
				R.drawable.ic_program_menu_sounds);
		setLeftDrawableSize(context, contentView, largeIconSizeProgramMenu, R.id.program_menu_button_nfctags,
				R.drawable.ic_program_menu_nfc);

		setBottomBarIconSize(context, contentView);
	}

	private static void setLeftDrawableSize(Context context, View contentView, Rect bounds, int viewId, int
			drawableId) {
		Drawable drawable = context.getResources().getDrawable(drawableId);
		drawable.setBounds(bounds);
		Button button = (Button) contentView.findViewById(viewId);
		button.setCompoundDrawables(drawable, null, null, null);
	}

	public static void setStageDialogIconSize(Context context, View contentView) {
		if (!isLargeSize()) {
			return;
		}

		setTopDrawableSize(context, contentView, largeIconSizeStageDialog, R.id.stage_dialog_button_back,
				R.drawable.stage_dialog_button_back_selector);
		setTopDrawableSize(context, contentView, largeIconSizeStageDialog, R.id.stage_dialog_button_restart,
				R.drawable.stage_dialog_button_restart_selector);
		setTopDrawableSize(context, contentView, largeIconSizeStageDialog, R.id.stage_dialog_button_screenshot,
				R.drawable.stage_dialog_button_screenshot_selector);
		setTopDrawableSize(context, contentView, largeIconSizeStageDialog, R.id.stage_dialog_button_toggle_axes,
				R.drawable.stage_dialog_button_toggle_axis_selector);
		setTopDrawableSize(context, contentView, largeIconSizeStageDialogContinue, R.id.stage_dialog_button_continue,
				R.drawable.stage_dialog_button_continue_selector);
	}

	private static void setTopDrawableSize(Context context, View contentView, Rect bounds, int viewId, int
			drawableId) {
		Drawable drawable = context.getResources().getDrawable(drawableId);
		drawable.setBounds(bounds);
		Button button = (Button) contentView.findViewById(viewId);
		button.setCompoundDrawables(null, drawable, null, null);
	}

	public static void setBottomBarIconSize(Context context, View contentView) {
		if (!isLargeSize()) {
			return;
		}

		setBitmapSize(context, contentView, largeIconBottomBar, R.id.button_play, R.drawable.ic_play);
		setBitmapSize(context, contentView, largeIconBottomBar, R.id.button_add, R.drawable.ic_plus);
	}

	private static void setBitmapSize(Context context, View contentView, int size, int buttonId, int drawableId) {
		ImageButton button = (ImageButton) contentView.findViewById(buttonId);
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
		button.setImageBitmap(Bitmap.createScaledBitmap(bitmap, size, size, false));
	}

	public static boolean isActivated() {
		return activated;
	}

	public static void setActivated(boolean value) {
		activated = value;
	}

	public static boolean isContrast() {
		return contrast;
	}

	public static void setContrast(boolean value) {
		contrast = value;
	}

	public static boolean isLargeSize() {
		return largeSize;
	}

	public static void setLargeSize(boolean value) {
		largeSize = value;
	}
}
