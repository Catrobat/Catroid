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
import java.io.Serializable;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.gui.adapter.SoundBrickAdapter;
import at.tugraz.ist.catroid.content.entities.SoundInfo;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.io.sound.SoundManager;

public class PlaySoundBrick implements Brick, OnItemClickListener, Serializable {
	protected String pathToSoundfile;
	private transient ArrayList<SoundInfo> soundList;
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private transient Dialog soundDialog;
	private transient BaseExpandableListAdapter adapter;

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
		this.adapter = adapter;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_play_sound, null);
		Button soundButton = (Button) view.findViewById(R.id.btSoundChoose);
		if (pathToSoundfile != null) {

			int index = pathToSoundfile.lastIndexOf("/") + 1;

			if (index > 0) {
				String soundFileName = (String) pathToSoundfile.subSequence(index + 15, pathToSoundfile.length());
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
			final SoundBrickAdapter soundBrickAdapter = new SoundBrickAdapter(context, soundList);
			soundButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					soundDialog = new Dialog(context);
					soundDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					soundDialog.setContentView(R.layout.sound_list);
					soundDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							WindowManager.LayoutParams.FLAG_FULLSCREEN);

					ListView list = (ListView) soundDialog.findViewById(R.id.sound_list);
					list.setAdapter(soundBrickAdapter);
					list.setOnItemClickListener(PlaySoundBrick.this);

					soundDialog.show();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		return view;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_play_sound, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new PlaySoundBrick(getSprite(), getPathToSoundFile());
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		File soundFile = null;
		try {
			soundFile = StorageHandler.getInstance().copySoundFile(soundList.get(position).getPath());

			if (soundFile != null)
				pathToSoundfile = soundFile.getAbsolutePath();
			else
				pathToSoundfile = soundList.get(position).getTitleWithPath();

			adapter.notifyDataSetChanged();
			soundDialog.dismiss();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
