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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.SoundData;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.Utils;

public class SoundActivity extends ListActivity {
	private Sprite sprite;
	private ArrayList<SoundData> soundData;
	private SoundAdapter soundActivityListAdapter;
	private final int REQUEST_SELECT_MUSIC = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound);
		StorageHandler.getInstance().loadSoundContent(this);

		sprite = ProjectManager.getInstance().getCurrentSprite();
		soundData = new ArrayList<SoundData>();
		ArrayList<SoundData> currentSounds = sprite.getSoundList();
		soundActivityListAdapter = new SoundAdapter(this, R.layout.activity_soundlist_item, soundData);
		setListAdapter(soundActivityListAdapter);
		getListView().setTextFilterEnabled(true);

		if (currentSounds != null && !currentSounds.isEmpty()) {
			soundData.addAll(currentSounds);
		}

		Button addNewSound = (Button) findViewById(R.id.add_sound_button);
		addNewSound.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("audio/*");
				startActivityForResult(Intent.createChooser(intent, "Select music"), REQUEST_SELECT_MUSIC);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.checkForSdCard(this)) {
			return;
		}
	}

	private void updateSoundAdapter(String title, String path) {
		SoundData s = new SoundData();
		s.setSoundName(title);
		s.setSoundAbsolutePath(path);
		soundData.add(s);
		sprite.addToSoundList(s);
		soundActivityListAdapter.notifyDataSetChanged();
	}

	private class SoundAdapter extends ArrayAdapter<SoundData> { //TODO: distingt class

		private ArrayList<SoundData> items;

		public SoundAdapter(final Context context, int textViewResourceId, ArrayList<SoundData> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(final int position, View convView, ViewGroup parent) {

			View convertView = convView;
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.activity_soundlist_item, null);
			}

			final SoundData soundData = items.get(position);
			if (soundData != null) {
				ImageView soundImage = (ImageView) convertView.findViewById(R.id.sound_img);
				soundImage.setImageResource(R.drawable.speaker);

				TextView soundName = (TextView) convertView.findViewById(R.id.edit_sound_name);
				soundName.setText(soundData.getSoundName()); //TODO change this to edittextfield

				ImageButton playSound = (ImageButton) convertView.findViewById(R.id.play_button);
				playSound.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						SoundManager.getInstance().playSoundFile(soundData.getSoundAbsolutePath());
						notifyDataSetChanged();
					}
				});

				ImageButton stopSound = (ImageButton) convertView.findViewById(R.id.stop_button);
				stopSound.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						SoundManager.getInstance().stop();
						notifyDataSetChanged();
					}
				});

				ImageButton deleteSound = (ImageButton) convertView.findViewById(R.id.delete_button);
				deleteSound.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						items.remove(soundData);
						sprite.removeSoundList(soundData);
						//StorageHandler.getInstance().deleteFile(soundData.getSoundAbsolutePath()); //TODO this wont work I guess, check if everything is in filechecksumcontainer
						notifyDataSetChanged();
					}
				});

			}
			return convertView;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SELECT_MUSIC) {

			String audioPath = "";
			//get real path of soundfile --------------------------
			{
				Uri audioUri = data.getData();
				String[] proj = { MediaStore.Audio.Media.DATA };
				Cursor actualSoundCursor = managedQuery(audioUri, proj, null, null, null);
				int actualSoundColumnIndex = actualSoundCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
				actualSoundCursor.moveToFirst();
				audioPath = actualSoundCursor.getString(actualSoundColumnIndex);
			}

			try {
				if (audioPath.equalsIgnoreCase("")) {
					throw new IOException();
				}
				File soundFile = StorageHandler.getInstance().copySoundFile(audioPath);
				String soundPath = soundFile.getName(); //TODO wrong name ...
				String soundTitle = soundFile.getName().substring(33);
				updateSoundAdapter(soundTitle, soundPath);
			} catch (IOException e) {
				Utils.displayErrorMessage(this, this.getString(R.string.error_load_sound));
			}

		}
	}
}
