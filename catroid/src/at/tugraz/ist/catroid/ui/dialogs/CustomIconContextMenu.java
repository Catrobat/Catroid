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
 *  
 * 		This file incorporates work covered by the following copyright and  
 * 		permission notice:  
 * 
 * 		Copyright (C) 2010 Tani Group 
 * 		http://android-demo.blogspot.com/
 *
 * 		Licensed under the Apache License, Version 2.0 (the "License");
 * 		you may not use this file except in compliance with the License.
 * 		You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * 		Unless required by applicable law or agreed to in writing, software
 * 		distributed under the License is distributed on an "AS IS" BASIS,
 * 		WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 		See the License for the specific language governing permissions and
 * 		limitations under the License.
 */

package at.tugraz.ist.catroid.ui.dialogs;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;

public class CustomIconContextMenu implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

	private static final int LIST_HEIGHT = 65;

	private IconMenuAdapter menuAdapter;
	private Activity activity;
	private int dialogId;
	public AlertDialog dialog;

	private IconContextMenuOnClickListener clickListener;

	public CustomIconContextMenu(Activity parent, int id) {
		this.activity = parent;
		this.dialogId = id;
		menuAdapter = new IconMenuAdapter(activity);
	}

	public void addItem(Resources resource, String title, int imageResourceId, int id) {
		menuAdapter.addItem(new CustomContextMenuItem(resource, title, imageResourceId, id));
	}

	public void setOnClickListener(IconContextMenuOnClickListener listener) {
		clickListener = listener;
	}

	public Dialog createMenu(String menuTitle) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		View customTitle = View.inflate(activity, R.layout.alert_dialog_title, null);
		TextView customTitleTextView = (TextView) customTitle.findViewById(R.id.alert_dialog_title);
		customTitleTextView.setText(menuTitle);
		builder.setCustomTitle(customTitle);

		builder.setAdapter(menuAdapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int position) {
				CustomContextMenuItem item = (CustomContextMenuItem) menuAdapter.getItem(position);

				if (clickListener != null) {
					clickListener.onClick(item.contextMenuItemId);
				}
			}
		});

		builder.setInverseBackgroundForced(true);

		dialog = builder.create();
		dialog.setOnCancelListener(this);
		dialog.setOnDismissListener(this);
		dialog.setCanceledOnTouchOutside(true);

		return dialog;
	}

	public void onCancel(DialogInterface dialog) {
		cleanup();
	}

	public void onDismiss(DialogInterface arg0) {
	}

	private void cleanup() {
		activity.dismissDialog(dialogId);
	}

	public interface IconContextMenuOnClickListener {
		public abstract void onClick(int menuId);
	}

	/**
	 * Menu-like list adapter with icon
	 */
	protected class IconMenuAdapter extends BaseAdapter {
		private Context context = null;

		private ArrayList<CustomContextMenuItem> items = new ArrayList<CustomContextMenuItem>();

		public IconMenuAdapter(Context context) {
			this.context = context;
		}

		public void addItem(CustomContextMenuItem menuItem) {
			items.add(menuItem);
		}

		//View for each list element (icon and text)
		public View getView(int position, View convertView, ViewGroup parent) {
			CustomContextMenuItem item = (CustomContextMenuItem) getItem(position);

			Resources resource = activity.getResources();

			if (convertView == null) {
				TextView tempTextView = new TextView(context);
				AbsListView.LayoutParams param = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,
						AbsListView.LayoutParams.WRAP_CONTENT);
				tempTextView.setLayoutParams(param);
				tempTextView.setPadding((int) toPixel(resource, 15), 0, (int) toPixel(resource, 15), 0);
				tempTextView.setGravity(android.view.Gravity.CENTER_VERTICAL);

				Theme theme = context.getTheme();
				TypedValue typedValue = new TypedValue();

				if (theme.resolveAttribute(android.R.attr.textAppearanceLargeInverse, typedValue, true)) {
					tempTextView.setTextAppearance(context, typedValue.resourceId);
				}

				tempTextView.setMinHeight(LIST_HEIGHT);
				tempTextView.setCompoundDrawablePadding((int) toPixel(resource, 14));
				convertView = tempTextView;
			}

			TextView textView = (TextView) convertView;
			textView.setTextColor(Color.BLACK);
			textView.setTag(item);
			textView.setText(item.text);
			textView.setCompoundDrawablesWithIntrinsicBounds(item.icon, null, null, null);
			textView.setBackgroundResource(R.color.contextmenu_item_background);

			return textView;
		}

		private float toPixel(Resources resource, int dip) {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, resource.getDisplayMetrics());
		}

		public Object getItem(int position) {
			return items.get(position);
		}

		public int getCount() {
			return items.size();
		}

		public long getItemId(int position) {
			CustomContextMenuItem item = (CustomContextMenuItem) getItem(position);
			return item.contextMenuItemId;
		}
	}

	protected class CustomContextMenuItem {
		public String text;
		public Drawable icon;
		public int contextMenuItemId;

		public CustomContextMenuItem(Resources resource, String title, int iconResourceId, int contextMenuItemId) {
			text = title;
			icon = (iconResourceId != -1) ? resource.getDrawable(iconResourceId) : null;
			this.contextMenuItemId = contextMenuItemId;
		}
	}
}
