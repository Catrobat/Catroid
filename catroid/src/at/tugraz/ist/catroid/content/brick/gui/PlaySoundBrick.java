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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.gui.adapter.SoundBrickAdapter;
import at.tugraz.ist.catroid.content.brick.PlaySoundBrickBase;
import at.tugraz.ist.catroid.content.entities.SoundInfo;
import at.tugraz.ist.catroid.io.StorageHandler;

public class PlaySoundBrick extends PlaySoundBrickBase implements Brick, android.content.DialogInterface.OnClickListener {

	private static final long serialVersionUID = 1L;
	private transient ArrayList<SoundInfo> soundList;; 
	private transient BaseAdapter programmAdapter;
	
	public PlaySoundBrick(String pathToSoundfile) {
		super(pathToSoundfile);
	}

	public View getView(final Context context, View convertView, BaseAdapter adapter) {
		programmAdapter = adapter;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_play_sound, null);
		Button soundButton = (Button) view.findViewById(R.id.btSoundChoose);
		if(pathToSoundfile != null) {
			int index = pathToSoundfile.lastIndexOf("/")+1;
			if(index > 0)
				soundButton.setText(pathToSoundfile.substring(index));
			else
				soundButton.setText("<choose a title>");
		} else {
			soundButton.setText("<choose a title>");
		}
		soundList = StorageHandler.getInstance((Activity)context).getSoundContent();
		soundButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(context)
					.setAdapter(new SoundBrickAdapter(context, soundList), PlaySoundBrick.this)
					.create().show();
			}
		});
		
		
		
//		Spinner spinner = (Spinner) view.findViewById(R.id.Spinner01);
//		final ArrayList<SoundInfo> soundList = StorageHandler.getInstance((Activity)context).getSoundContent();
//		spinner.setAdapter(new SoundBrickAdapter(context, soundList));
//		if(pathToSoundfile != null) {
//			int selectedPosition = soundList.indexOf(pathToSoundfile);
//			System.out.println("path: "+pathToSoundfile+", index: "+selectedPosition);
//			if(selectedPosition >= 0)
//				spinner.setSelection(selectedPosition);
//		}
//		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int position, long arg3) {
//				pathToSoundfile = soundList.get(position).getTitleWithPath();
//			}
//			public void onNothingSelected(AdapterView<?> arg0) {
//				if(soundList.size() > 0)
//					;//pathToSoundfile = soundList.get(0).getTitleWithPath();		
//			}	
//		});
		
		return view;
	}

	public void onClick(DialogInterface dialog, int which) {
		System.out.println("CLICKKKK: "+which);
		pathToSoundfile = soundList.get(which).getTitleWithPath();
		programmAdapter.notifyDataSetChanged();
	}

	
}
