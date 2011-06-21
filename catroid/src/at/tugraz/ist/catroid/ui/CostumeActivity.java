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
import android.database.Cursor;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;

public class CostumeActivity extends ListActivity {
	private LayoutInflater mInflater;
	private Vector<RowData> data;
	RowData rd;

	private static final int SELECT_IMAGE = 1;

	private String selectedImagePath;

	static final String[] title = new String[] {
			"*New*Apple iPad Wi-Fi (16GB)", "7 Touch Tablet -2GB Google Android",
			"Apple iPad Wi-Fi (16GB) Rarely Used ", "Apple iPad Wi-Fi (16GB) AppleCase" };

	private Integer[] imgid = {
			R.drawable.bsfimg, R.drawable.bsfimg4, R.drawable.bsfimg2,
			R.drawable.bsfimg5 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_costume);

		mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		data = new Vector<RowData>();
		for (int i = 0; i < title.length; i++) {
			try {
				rd = new RowData(i, title[i]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			data.add(rd);
		}

		CustomAdapter adapter = new CustomAdapter(this, R.layout.activity_costumelist, R.id.costume_edit_name, data);
		setListAdapter(adapter);
		getListView().setTextFilterEnabled(true);

		Button addnewcostume = (Button) findViewById(R.id.add_costume_button);
		addnewcostume.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);

			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_IMAGE) {
				Uri selectedImageUri = data.getData();
				selectedImagePath = getPath(selectedImageUri);
			}
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		Toast.makeText(getApplicationContext(),
				"You have selected " + (position + 1) + "th item",
				Toast.LENGTH_SHORT).show();
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
			i11.setImageResource(imgid[rowData.mId]);
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
