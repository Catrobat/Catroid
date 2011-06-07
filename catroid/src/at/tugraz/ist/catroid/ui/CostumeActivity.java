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

import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;

public class CostumeActivity extends ListActivity {
	private LayoutInflater mInflater;
	private Vector<RowData> data;
	RowData rd;
	private Sprite sprite;

	static final String[] imageName = new String[] {};

	private Integer[] thumbnail = {};

	private void initListeners() {
		sprite = ProjectManager.getInstance().getCurrentSprite();

		Button addCostumeButton = (Button) findViewById(R.id.add_costume_button);
		addCostumeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
			}
		});

	}

	/*
	 * public View getView(final Context context, final int brickId, BaseExpandableListAdapter adapter) {
	 * LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 * View view = inflater.inflate(R.layout.construction_brick_set_costume, null);
	 * 
	 * OnClickListener listener = new OnClickListener() {
	 * public void onClick(View v) {
	 * Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	 * intent.setType("image/*");
	 * ((Activity) context).startActivityForResult(intent, brickId);
	 * }
	 * };
	 * 
	 * ImageView imageView = (ImageView) view.findViewById(R.id.costume_image_view);
	 * 
	 * if (imageName != null) {
	 * if (thumbnail == null) {
	 * thumbnail = ImageEditing.getScaledBitmap(getAbsoluteImagePath(), Consts.THUMBNAIL_HEIGHT,
	 * Consts.THUMBNAIL_WIDTH);
	 * }
	 * 
	 * imageView.setImageBitmap(thumbnail);
	 * imageView.setBackgroundDrawable(null);
	 * }
	 * 
	 * imageView.setOnClickListener(listener);
	 * 
	 * return view;
	 * }
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_costume);
		mInflater = (LayoutInflater) getSystemService(
				Activity.LAYOUT_INFLATER_SERVICE);
		data = new Vector<RowData>();
		for (int i = 0; i < imageName.length; i++) {
			try {
				rd = new RowData(i, imageName[i]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			data.add(rd);
		}
		CustomAdapter adapter = new CustomAdapter(this, R.layout.activity_costumelist, R.id.editName, data);
		setListAdapter(adapter);
		getListView().setTextFilterEnabled(true);
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		Toast.makeText(getApplicationContext(), "You have selected "
						+ (position + 1) + "th item", Toast.LENGTH_SHORT).show();
	}

	private class RowData {
		protected int mId;
		protected String mTitle;
		protected String mDetail;

		RowData(int id, String title) {
			mId = id;
			mTitle = title;
		}

		@Override
		public String toString() {
			return mId + " " + mTitle + " " + mDetail;
		}
	}

	private class CustomAdapter extends ArrayAdapter<RowData> {
		public CustomAdapter(Context context, int resource,
							int textViewResourceId, List<RowData> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			TextView title = null;
			TextView detail = null;
			ImageView i11 = null;
			RowData rowData = getItem(position);
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.activity_costumelist, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}
			holder = (ViewHolder) convertView.getTag();
			title = holder.gettitle();
			title.setText(rowData.mTitle);
			i11 = holder.getImage();
			i11.setImageResource(thumbnail[rowData.mId]);
			return convertView;
		}

		private class ViewHolder {
			private View mRow;
			private TextView title = null;
			private ImageView i11 = null;

			public ViewHolder(View row) {
				mRow = row;
			}

			public TextView gettitle() {
				if (null == title) {
					title = (TextView) mRow.findViewById(R.id.editName);
				}
				return title;
			}

			public ImageView getImage() {
				if (null == i11) {
					i11 = (ImageView) mRow.findViewById(R.id.img);
				}
				return i11;
			}
		}
	}
}
