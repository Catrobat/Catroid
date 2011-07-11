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

/**
 * @author ainulhusna
 *
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.SoundData;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.adapter.SoundBrickAdapter;
import at.tugraz.ist.catroid.utils.Utils;

public class SoundActivity extends ListActivity implements OnItemClickListener {
	private Sprite sprite;
	private ArrayList<SoundData> soundData;
	private ArrayList<SoundData> currentSound;
	private transient ArrayList<SoundInfo> soundList;
	private SoundAdapter s_adapter;
	private SoundBrickAdapter soundBrickAdapter;
	private Runnable viewSounds;
	Bitmap bm;
	int column_index;
	Intent intent = null;
	// Declare our Views, so we can access them later
	String filemanagerstring, selectedSoundPath, soundfileName, soundPath, soundTitle;
	int counter;
	Cursor cursor;
	private String TAG = SoundActivity.class.getSimpleName();
	protected ProjectActivity projectActivity;
	private transient Dialog soundDialog;

	private static final int SELECT_SOUND = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound);
		StorageHandler.getInstance().loadSoundContent(this);
		soundList = StorageHandler.getInstance().getSoundContent();

		sprite = ProjectManager.getInstance().getCurrentSprite();
		soundData = new ArrayList<SoundData>();
		currentSound = sprite.getSoundList();
		//Log.v(TAG, "*************************" + sprite.getSoundList().size());

		if (currentSound != null) {
			soundData.addAll(currentSound);

			Log.v(TAG,
					"^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ all old sound had been loaded and the size is" + soundData.size());
		}

		soundBrickAdapter = new SoundBrickAdapter(this, soundList);
		soundDialog = new Dialog(this);

		Button addnewsound = (Button) findViewById(R.id.add_sound_button);
		addnewsound.setOnClickListener(new OnClickListener() {
			// TODO Auto-generated method stub
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("audio/*");
				startActivityForResult(Intent.createChooser(intent, "Select music"), 0);
				//				soundDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				//				soundDialog.setContentView(R.layout.sound_list);
				//				soundDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				//										WindowManager.LayoutParams.FLAG_FULLSCREEN);
				//
				//				ListView list = (ListView) soundDialog.findViewById(R.id.sound_list);
				//				list.setAdapter(soundBrickAdapter);
				//				list.setOnItemClickListener(SoundActivity.this);
				//
				//				soundDialog.show();

			}
		});

		s_adapter = new SoundAdapter(this, R.layout.activity_soundlist, soundData);
		setListAdapter(s_adapter);
		getListView().setTextFilterEnabled(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		//initListeners();

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

	private Runnable returnRes = new Runnable() {

		public void run() {
			if (soundData != null && soundData.size() > 0) {
				s_adapter.notifyDataSetChanged();
				for (int i = 0; i < soundData.size(); i++) {
					s_adapter.add(soundData.get(i));
				}
			}
			s_adapter.notifyDataSetChanged();
		}
	};

	private void getSounds() {
		try {
			soundData = new ArrayList<SoundData>();
			SoundData s = new SoundData();
			s.setSoundName(soundTitle);
			s.setSoundAbsolutePath(soundfileName);
			soundData.add(s);
			sprite.setSoundList(s);

			Log.i("ARRAY", "" + soundData.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		runOnUiThread(returnRes);
	}

	private class SoundAdapter extends ArrayAdapter<SoundData> {

		private ArrayList<SoundData> items;

		public SoundAdapter(final Context context, int textViewResourceId, ArrayList<SoundData> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.activity_soundlist, null);
			}

			final SoundData s = items.get(position);
			if (s != null) {
				ImageView soundImage = (ImageView) v.findViewById(R.id.sound_img);
				soundImage.setImageResource(R.drawable.speaker);

				TextView soundName = (TextView) v.findViewById(R.id.edit_sound_name);
				soundName.setText(s.getSoundName());

				ImageButton playSound = (ImageButton) v.findViewById(R.id.play_button);
				playSound.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						SoundManager.getInstance().playSoundFile(s.getSoundAbsolutePath());
						notifyDataSetChanged();
					}
				});

				ImageButton stopSound = (ImageButton) v.findViewById(R.id.stop_button);
				stopSound.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						SoundManager.getInstance().stop();
						notifyDataSetChanged();
					}
				});

				ImageButton deleteSound = (ImageButton) v.findViewById(R.id.delete_button);
				deleteSound.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						items.remove(s);
						sprite.removeSoundList(s);
						notifyDataSetChanged();
					}
				});

			}
			return v;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && requestCode == 0) {

			//get real path of soundfile --------------------------
			Uri audioUri = data.getData();
			String[] proj = { MediaStore.Audio.Media.DATA };
			Cursor actualSoundCursor = managedQuery(audioUri, proj, null, null, null);
			int actualSoundColumnIndex = actualSoundCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
			actualSoundCursor.moveToFirst();
			String audioPath = actualSoundCursor.getString(actualSoundColumnIndex);
			//-----------------------------------------------------

			try {
				File soundFile = StorageHandler.getInstance().copySoundFile(audioPath);
				soundfileName = soundFile.getName();
				soundTitle = soundFile.getName().substring(33);
			} catch (IOException e) {
				Utils.displayErrorMessage(this, this.getString(R.string.error_load_image)); //TODO fix to audio
			}

			viewSounds = new Runnable() {
				public void run() {
					getSounds();
				}
			};

			Thread thread = new Thread(null, viewSounds, "MagentoBackground");
			thread.start();

		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		File soundFile = null;

		try {
			soundFile = StorageHandler.getInstance().copySoundFile(soundList.get(position).getPath());

			if (soundFile != null) {
				soundfileName = soundFile.getName();
			} else {
				soundfileName = soundList.get(position).getTitleWithPath();
			}

			soundTitle = soundList.get(position).getTitle();
			soundDialog.dismiss();

		} catch (IOException e) {
			e.printStackTrace();
		}

		viewSounds = new Runnable() {
			public void run() {
				getSounds();
			}
		};

		Thread thread = new Thread(null, viewSounds, "MagentoBackground");
		thread.start();

	}
}
