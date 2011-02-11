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
package at.tugraz.ist.catroid.content.brick.gui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.gui.adapter.SoundBrickAdapter;
import at.tugraz.ist.catroid.constructionSite.gui.adapter.SpritesAdapter;
import at.tugraz.ist.catroid.content.brick.PlaySoundBrickBase;
import at.tugraz.ist.catroid.content.entities.SoundInfo;
import at.tugraz.ist.catroid.io.StorageHandler;

public class PlaySoundBrick extends PlaySoundBrickBase implements Brick {

	private static final long serialVersionUID = 1L;

	public PlaySoundBrick(String pathToSoundfile) {
		super(pathToSoundfile);
	}

	public View getView(Context context, View convertView, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_play_sound, null);
		Spinner spinner = (Spinner) view.findViewById(R.id.Spinner01);
		final ArrayList<SoundInfo> soundList = StorageHandler.getInstance((Activity)context).getSoundContent();
		spinner.setAdapter(new SoundBrickAdapter(context, soundList));
//		spinner.set(new OnItemClickListener() {
//
//			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//					long arg3) {
//				pathToSoundfile = soundList.get(position).getPath();
//			}
//		});
		return view;
	}
}
