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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;

public class CostumeActivity extends ListActivity {
	private ListView costumeListView;
	private LayoutInflater mInflater;
	ImageView img;
	private Vector<RowData> costume_data;
	int column_index;
	Cursor cursor;
	Intent intent = null;
	RowData rd;
	String imagePath;
	private static final long serialVersionUID = 1L;

	private static final int SELECT_IMAGE = 1;

	private String[] costumeName = { "cat1" };

	private Integer[] imgid = { R.drawable.catroid };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_costume);
		costumeListView = (ListView) findViewById(android.R.id.list);

		mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		costume_data = new Vector<RowData>();
		for (int i = 0; i < costumeName.length; i++) {
			try {
				rd = new RowData(i, costumeName[i]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			costume_data.add(rd);
		}

		CostumeAdapter adapter = new CostumeAdapter(this, R.layout.activity_costumelist, R.id.costume_edit_name,
				costume_data);
		costumeListView.setAdapter(adapter);
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
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_IMAGE) {
				Uri selectedImageUri = data.getData();

				//OI FILE Manager
				String filemanagerstring = selectedImageUri.getPath();

				//MEDIA GALLERY
				String selectedImagePath = getPath(selectedImageUri);

				img.setImageURI(selectedImageUri);

				imagePath.getBytes();

				String imageName = imagePath.toString();

				costumeName[(costumeName.length) + 1] = new String(imageName);

				//TextView costumeName = (TextView) findViewById(R.id.edit_costume);
				//costumeName.setText(imagePath.toString());

				Bitmap bm = BitmapFactory.decodeFile(imagePath);

			}

		}

	}

	public String getPath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		imagePath = cursor.getString(column_index);

		return cursor.getString(column_index);
	}

	private class RowData {
		protected int mId;
		protected String mTitle;
		protected String mDetail;

		RowData(int id, String costumeName) {
			mId = id;
			mTitle = costumeName;

		}

		@Override
		public String toString() {
			return mId + " " + mTitle + " " + mDetail;
		}
	}

	private class CostumeAdapter extends ArrayAdapter<RowData> {
		public CostumeAdapter(Context context, int resource, int textViewResourceId, List<RowData> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			TextView costumeName = null;
			ImageView costumeImage = null;
			RowData rowData = getItem(position);
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.activity_costumelist, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}
			holder = (ViewHolder) convertView.getTag();
			costumeName = holder.gettitle();
			costumeName.setText(rowData.mTitle);
			costumeImage = holder.getImage();
			costumeImage.setImageResource(imgid[rowData.mId]);
			return convertView;
		}

		private class ViewHolder {
			private View mRow;
			private TextView costumeName = null;
			private ImageView costumeImage = null;

			public ViewHolder(View row) {
				mRow = row;
			}

			public TextView gettitle() {
				if (null == costumeName) {
					costumeName = (TextView) mRow.findViewById(R.id.costume_edit_name);
				}
				return costumeName;
			}

			public ImageView getImage() {
				if (null == costumeImage) {
					costumeImage = (ImageView) mRow.findViewById(R.id.costume_image);
				}
				return costumeImage;
			}
		}
	}
}
