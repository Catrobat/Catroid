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
package at.tugraz.ist.catroid.content.brick;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.gui.adapter.SoundBrickAdapter;
import at.tugraz.ist.catroid.content.entities.SoundInfo;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.io.sound.SoundManager;

public class PlaySoundBrick implements Brick, android.content.DialogInterface.OnClickListener {
	protected String pathToSoundfile;
	private transient ArrayList<SoundInfo> soundList;
	private transient BaseExpandableListAdapter programmAdapter;
	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	public PlaySoundBrick(Sprite sprite, String pathToSoundfile) {
		this.pathToSoundfile = pathToSoundfile;
		this.sprite = sprite;
	}

	public void execute() {
		SoundManager.getInstance().playSoundFile(pathToSoundfile);
	}

	public Sprite getSprite() {
		return sprite;
	}

	public String getPathToSoundFile() {
		return pathToSoundfile;
	}

	public View getView(final Context context, int brickId, BaseExpandableListAdapter adapter) {
		programmAdapter = adapter;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_play_sound, null);
		Button soundButton = (Button) view.findViewById(R.id.btSoundChoose);
		if (pathToSoundfile != null) {

			int index = pathToSoundfile.lastIndexOf("/") + 1;

			if (index > 0) {
				String soundFileName = (String) pathToSoundfile.subSequence(index, pathToSoundfile.length());
				soundButton.setText(soundFileName);
			} else {
				soundButton.setText("<choose a title>");
			}
		} else {
			soundButton.setText("<choose a title>");
		}
		try {
			StorageHandler.getInstance().loadSoundContent(context);
			soundList = StorageHandler.getInstance().getSoundContent();
			soundButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					new AlertDialog.Builder(context)
					.setAdapter(new SoundBrickAdapter(context, soundList), PlaySoundBrick.this).create().show();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		// TODO: Is this still needed?
		// Spinner spinner = (Spinner) view.findViewById(R.id.Spinner01);
		// final ArrayList<SoundInfo> soundList =
		// StorageHandler.getInstance((Activity)context).getSoundContent();
		// spinner.setAdapter(new SoundBrickAdapter(context, soundList));
		// if(pathToSoundfile != null) {
		// int selectedPosition = soundList.indexOf(pathToSoundfile);
		// System.out.println("path: "+pathToSoundfile+", index: "+selectedPosition);
		// if(selectedPosition >= 0)
		// spinner.setSelection(selectedPosition);
		// }
		// spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		// public void onItemSelected(AdapterView<?> arg0, View arg1,
		// int position, long arg3) {
		// pathToSoundfile = soundList.get(position).getTitleWithPath();
		// }
		// public void onNothingSelected(AdapterView<?> arg0) {
		// if(soundList.size() > 0)
		// ;//pathToSoundfile = soundList.get(0).getTitleWithPath();
		// }
		// });

		return view;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_play_sound, null);
		return view;
	}

	public void onClick(DialogInterface dialog, int which) {
		File soundFile = null;
		try {
			soundFile = StorageHandler.getInstance().copySoundFile(soundList.get(which).getPath());

			if (soundFile != null)
				pathToSoundfile = soundFile.getAbsolutePath();
			else
				pathToSoundfile = soundList.get(which).getTitleWithPath();
			programmAdapter.notifyDataSetChanged();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Brick clone() {
		return new PlaySoundBrick(getSprite(), getPathToSoundFile());
	}
}
