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
package at.tugraz.ist.catroid.content.bricks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;

public class SpeakBrick implements Brick {
	private static final long serialVersionUID = 1L;
	private static final int MAXLINES = 3;
	private Sprite sprite;
	private static String text = "";
	private TextToSpeech tts;
	private int language = 0;
	private Context context;
	private ArrayList<Locale> availableLocales = null;
	private final static String TAG = SpeakBrick.class.getSimpleName();

	public SpeakBrick(Sprite sprite, String text) {
		this.sprite = sprite;
		SpeakBrick.text = text;
	}

	public void execute() {
		tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {
					HashMap<String, String> myHashAlarm = new HashMap();
					myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));

					int result = tts.setLanguage(Locale.ENGLISH);
					if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
						Log.e(TAG, "Language is not available.");
					} else {
						tts.speak(getText(), TextToSpeech.QUEUE_FLUSH, myHashAlarm);
					}
				}
			}
		});

	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public String getText() {
		return text;
	}

	public View getView(final Context context, int brickId, final BaseExpandableListAdapter adapter) {
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_speak, null);

		//		final Spinner spinner = (Spinner) view.findViewById(R.id.SpinnerLanguage);
		//		spinner.setFocusableInTouchMode(false);
		//		spinner.setFocusable(false);
		//		ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(context,
		//				android.R.layout.simple_spinner_item);
		//		for (int i = 0; i < availableLocales.size(); i++) {
		//			spinnerAdapter.add(availableLocales.get(i).getDisplayLanguage().toString());
		//		}
		//		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//		spinner.setAdapter(spinnerAdapter);
		//
		//		spinner.setSelection(language);
		//
		//		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		//			private boolean start = true;
		//
		//			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		//				if (start) {
		//					start = false;
		//					return;
		//				}
		//				language = pos;
		//				spinner.setSelection(pos);
		//				adapter.notifyDataSetChanged();
		//			}
		//
		//			public void onNothingSelected(AdapterView parent) {
		//				//		 Do nothing.
		//			}
		//		});

		EditText editText = (EditText) brickView.findViewById(R.id.edit_text_speak);
		editText.setText(text);
		editText.setMaxLines(MAXLINES);
		//		EditTextDialog dialogX = new EditTextDialog(context, editX, text);
		//		dialogX.setOnDismissListener(this);
		//		dialogX.setOnCancelListener((OnCancelListener) context);
		//
		//		editX.setOnClickListener(dialogX);
		editText.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(context);
				final EditText input = new EditText(context);
				input.setText(text);
				dialog.setView(input);
				dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						text = (input.getText().toString()).trim();
					}
				});
				dialog.setNeutralButton(context.getString(R.string.cancel_button),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});

				dialog.show();

			}
		});
		return brickView;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.toolbox_brick_speak, null);
		return brickView;
	}

	@Override
	public Brick clone() {
		return new SpeakBrick(getSprite(), getText());
	}

	//	public void onDismiss(DialogInterface dialog) {
	//		EditTextDialog inputDialog = (EditTextDialog) dialog;
	//		text = inputDialog.getText();
	//		dialog.cancel();
	//	}

	//		private void EnumerateAvailableLanguages() {
	//			Locale locales[] = Locale.getAvailableLocales();
	//			availableLocales = new ArrayList<Locale>();
	//			System.out.println("Size" + locales.length);
	//			for (int index = 0; index < locales.length; ++index) {
	//				if (tts.isLanguageAvailable(locales[index])==0) {
	//					Log.i("TTSDemo", locales[index].getDisplayLanguage() + " (" + locales[index].getDisplayCountry() + ")");
	//					availableLocales.add(locales[index]);
	//					System.out.println("ok");
	//				}
	//			}
	//		}
}
