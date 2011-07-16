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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.Utils;

//TODO: done and cancel buttons for edittext, use + button from action bar for new sounds

public class SoundActivity extends ListActivity {
	private Sprite sprite;
	private ArrayList<SoundInfo> soundInfoList;
	private SoundAdapter soundActivityListAdapter;
	private final int REQUEST_SELECT_MUSIC = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sound);
		sprite = ProjectManager.getInstance().getCurrentSprite();

		ArrayList<SoundInfo> currentSounds = sprite.getSoundList();
		soundInfoList = new ArrayList<SoundInfo>(currentSounds);

		soundActivityListAdapter = new SoundAdapter(this, R.layout.activity_sound_soundlist_item, soundInfoList);
		setListAdapter(soundActivityListAdapter);
		getListView().setTextFilterEnabled(true);
	}

	public void handleAddNewSoundButton(View view) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		startActivityForResult(Intent.createChooser(intent, "Select music"), REQUEST_SELECT_MUSIC);
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

	private void updateSoundAdapter(String title, String path, String fileName) {
		SoundInfo newSoundInfo = new SoundInfo();
		newSoundInfo.setTitle(title);
		newSoundInfo.setAbsolutePath(path);
		newSoundInfo.setSoundFileName(fileName);
		soundInfoList.add(newSoundInfo);
		sprite.addSoundInfoToSoundList(newSoundInfo);
		soundActivityListAdapter.notifyDataSetChanged();
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
			//-----------------------------------------------------

			//copy music to catroid:
			try {
				if (audioPath.equalsIgnoreCase("")) {
					throw new IOException();
				}
				File soundFile = StorageHandler.getInstance().copySoundFile(audioPath);
				String absoluteSoundPath = soundFile.getAbsolutePath();
				String soundTitle = soundFile.getName().substring(33, soundFile.getName().length() - 4);
				String soundFileName = soundFile.getName();
				updateSoundAdapter(soundTitle, absoluteSoundPath, soundFileName);
			} catch (IOException e) {
				Utils.displayErrorMessage(this, this.getString(R.string.error_load_sound));
			}

		}
	}

	private class SoundAdapter extends ArrayAdapter<SoundInfo> { //TODO: distinct class

		private ArrayList<SoundInfo> items;

		public SoundAdapter(final Context context, int textViewResourceId, ArrayList<SoundInfo> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(final int position, View convView, ViewGroup parent) {

			View convertView = convView;
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.activity_sound_soundlist_item, null);
			}

			final SoundInfo soundInfo = items.get(position);

			if (soundInfo != null) {
				ImageView soundImage = (ImageView) convertView.findViewById(R.id.sound_img);
				soundImage.setImageResource(R.drawable.speaker);

				final EditText soundNameEditText = (EditText) convertView.findViewById(R.id.edit_sound_name);
				soundNameEditText.setText(soundInfo.getTitle());
				final Button editSoundNameButton = (Button) convertView.findViewById(R.id.rename_sound_button);
				editSoundNameButton.setVisibility(Button.INVISIBLE);

				soundNameEditText.addTextChangedListener(new TextWatcher() {
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						if (s.length() == 0 || (s.length() == 1 && s.charAt(0) == '.')) {
							Toast.makeText(SoundActivity.this, R.string.notification_invalid_text_entered,
									Toast.LENGTH_SHORT).show();
							editSoundNameButton.setVisibility(Button.INVISIBLE);
						} else {
							editSoundNameButton.setVisibility(Button.VISIBLE);
						}
					}

					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					public void afterTextChanged(Editable arg0) {
					}
				});

				//rename with pressing enter key (does not rename the actual file but the name shown in the activity)
				soundNameEditText.setOnKeyListener(new OnKeyListener() {
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
							//clear the focus of EditText and kill keyboard
							soundNameEditText.clearFocus();
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(editSoundNameButton.getWindowToken(), 0);

							//rename
							String newSoundTitle = soundNameEditText.getText().toString();
							soundInfo.setTitle(newSoundTitle);
							return true;
						}
						return false;
					}
				});

				//rename sounds (does not rename the actual file but the name shown in the activity
				editSoundNameButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						//deactivate renameButton, clear the focus of EditText and kill keyboard
						editSoundNameButton.setEnabled(false);
						soundNameEditText.clearFocus();
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(editSoundNameButton.getWindowToken(), 0);

						//rename
						String newSoundTitle = soundNameEditText.getText().toString();
						soundInfo.setTitle(newSoundTitle);
					}
				});

				ImageButton playSound = (ImageButton) convertView.findViewById(R.id.play_button);
				playSound.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						SoundManager.getInstance().playSoundFile(soundInfo.getAbsolutePath());
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
						items.remove(soundInfo);
						sprite.removeSoundInfoFromSoundList(soundInfo);
						StorageHandler.getInstance().deleteFile(soundInfo.getAbsolutePath());
						notifyDataSetChanged();
					}
				});

			}
			return convertView;
		}
	}
}
