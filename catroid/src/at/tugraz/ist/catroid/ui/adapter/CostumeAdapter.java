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
package at.tugraz.ist.catroid.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.CostumeActivity.costumeData;

/**
 * @author ainulhusna
 * 
 */
class CostumeAdapter extends ArrayAdapter<costumeData> {

	private ArrayList<costumeData> items;

	public CostumeAdapter(Context context, int textViewResourceId, ArrayList<costumeData> items) {
		super(context, textViewResourceId, items);
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.activity_costumelist, null);
		}

		costumeData c = items.get(position);
		if (c != null) {
			Button editCostume = (Button) v.findViewById(R.id.edit_costume);
			Button copyCostume = (Button) v.findViewById(R.id.copy_costume);
			ImageButton deleteCostume = (ImageButton) v.findViewById(R.id.delete_button);
			//				deleteCostume.setOnClickListener(new DeleteCostume(position));
			//				c_adapter.notifyDataSetChanged();
			EditText costumeName = (EditText) v.findViewById(R.id.costume_edit_name);
			ImageView costumeImage = (ImageView) v.findViewById(R.id.costume_image);
			if (costumeName != null) {
				costumeName.setText(c.getCostumeName());
			}
			if (costumeImage != null) {
				costumeImage.setImageBitmap(c.getCostumeImage());
			}
		}
		return v;
	}

	/**
	 * @param layoutInflaterService
	 * @return
	 */
	private LayoutInflater getSystemService(String layoutInflaterService) {
		// TODO Auto-generated method stub
		return null;
	}
}