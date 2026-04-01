/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.catrobat.catroid.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

public final class UiUtils {

	private UiUtils() {
		throw new AssertionError("No.");
	}

	@Nullable
	public static AppCompatActivity getActivityFromContextWrapper(Context context) {
		while (context instanceof ContextWrapper) {
			if (context instanceof AppCompatActivity) {
				break;
			}
			context = ((ContextWrapper) context).getBaseContext();
		}

		if (context instanceof AppCompatActivity) {
			return (AppCompatActivity) context;
		}
		return null;
	}

	@Nullable
	public static AppCompatActivity getActivityFromView(View view) {
		return getActivityFromContextWrapper(view.getContext());
	}

	public static @DrawableRes ArrayList<Integer> getDrawablesForItems(@StringRes List<Integer> items) {
		@DrawableRes ArrayList<Integer> drawables = new ArrayList<Integer>();
		for (@StringRes int item : items) {
			drawables.add(getDrawableForItem(item));
		}
		return drawables;
	}

	public static @DrawableRes int getDrawableForItem(@StringRes int itemId) {
		switch (itemId) {
			case R.string.backpack_add:
				return R.drawable.ic_content_paste_small;
			case R.string.brick_context_dialog_copy_brick:
			case R.string.brick_context_dialog_copy_script:
				return R.drawable.ic_content_copy;
			case R.string.brick_context_dialog_delete_brick:
			case R.string.delete:
			case R.string.brick_context_dialog_delete_script:
				return R.drawable.ic_delete;
			case R.string.brick_context_dialog_formula_edit_brick:
			case R.string.rename:
				return R.drawable.ic_edit;
			case R.string.brick_context_dialog_help:
				return R.drawable.ic_help_small;
			case R.string.from_local:
				return R.drawable.ic_library_add_small;
			case R.string.from_library:
				return R.drawable.ic_apps_small;
			case R.string.menu_rate_us:
				return R.drawable.ic_star_rate;
			default:
				return R.drawable.ic_placeholder;
		}
	}

	@NonNull
	public static PopupMenu createSettingsPopUpMenu(
			View view, Context context,
			@MenuRes int menuLayout,
			@IdRes @Nullable int[] hiddenOptionItems) {
		PopupMenu popupMenu = new PopupMenu(context, view);
		popupMenu.inflate(menuLayout);

		if (hiddenOptionItems != null) {
			for (@IdRes int option : hiddenOptionItems) {
				popupMenu.getMenu().findItem(option).setVisible(false);
			}
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			popupMenu.setForceShowIcon(true);
		}
		return popupMenu;
	}

	public static ArrayAdapter getAlertDialogAdapterForMenuIcons(List<Integer> options,
			List<String> names, Context context, Activity activity) {

		@DrawableRes ArrayList<Integer> icons = UiUtils.getDrawablesForItems(options);

		return new ArrayAdapter(context,
				R.layout.alert_dialog_layout, R.id.title_option_item, options) {
			TextView item;

			@NonNull
			@Override
			public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
				final LayoutInflater inflater = (LayoutInflater)
						context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				if (convertView == null) {
					convertView = inflater.inflate(R.layout.alert_dialog_layout, parent,
							false);

					item = (TextView) convertView.findViewById(R.id.title_option_item);
					convertView.setTag(item);
				} else {
					item = (TextView) convertView.getTag();
				}
				item.setText(names.get(position));
				if (activity.getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
					item.setCompoundDrawablesWithIntrinsicBounds(icons.get(position), 0, 0,
							0);
				} else {
					item.setCompoundDrawablesWithIntrinsicBounds(0, 0, icons.get(position),
							0);
				}

				return convertView;
			}
		};
	}
}
