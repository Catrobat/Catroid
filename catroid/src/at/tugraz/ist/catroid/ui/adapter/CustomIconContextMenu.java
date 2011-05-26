/*
 *  Copyright (C) 2010 Tani Group 
 *
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
 * 
 */

package at.tugraz.ist.catroid.ui.adapter;

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

	public void addItem(Resources res, String title, int imageResourceId, int id) {
		menuAdapter.addItem(new CustomContextMenuItem(res, title, imageResourceId, id));
	}

	public void setOnClickListener(IconContextMenuOnClickListener listener) {
		clickListener = listener;
	}

	public Dialog createMenu(String menuItitle) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(menuItitle);
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

			Resources res = activity.getResources();

			if (convertView == null) {
				TextView temp = new TextView(context);
				AbsListView.LayoutParams param = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,
						AbsListView.LayoutParams.WRAP_CONTENT);
				temp.setLayoutParams(param);
				temp.setPadding((int) toPixel(res, 15), 0, (int) toPixel(res, 15), 0);
				temp.setGravity(android.view.Gravity.CENTER_VERTICAL);

				Theme th = context.getTheme();
				TypedValue tv = new TypedValue();

				if (th.resolveAttribute(android.R.attr.textAppearanceLargeInverse, tv, true)) {
					temp.setTextAppearance(context, tv.resourceId);
				}

				temp.setMinHeight(LIST_HEIGHT);
				temp.setCompoundDrawablePadding((int) toPixel(res, 14));
				convertView = temp;
			}

			TextView textView = (TextView) convertView;
			textView.setTextColor(Color.BLACK);
			textView.setTag(item);
			textView.setText(item.text);
			textView.setCompoundDrawablesWithIntrinsicBounds(item.icon, null, null, null);

			return textView;
		}

		private float toPixel(Resources res, int dip) {
			float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, res.getDisplayMetrics());
			return px;
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

		public CustomContextMenuItem(Resources res, String title,
				int iconResourceId, int contextMenuItemId) {
			text = title;
			if (iconResourceId != -1) {
				icon = res.getDrawable(iconResourceId);
			} else {
				icon = null;
			}
			this.contextMenuItemId = contextMenuItemId;
		}
	}
}
