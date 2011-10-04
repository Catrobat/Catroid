/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.stage.PreStageActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class SpeakBrick implements Brick {
	private static final long serialVersionUID = 1L;
	public static final int REQUIRED_RESSOURCES = TEXT_TO_SPEECH;

	private Sprite sprite;
	private String text = "";
	protected int position = 0;

	private transient View view;

	public SpeakBrick(Sprite sprite, String text) {
		this.sprite = sprite;
		this.text = text;
	}

	public int getRequiredResources() {
		return TEXT_TO_SPEECH;
	}

	public void execute() {
		PreStageActivity.textToSpeech(getText());
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public String getText() {
		return text;
	}

	public View getView(final Context context, int brickId, final BaseAdapter adapter) {
		view = View.inflate(context, R.layout.toolbox_brick_speak, null);

		EditText editText = (EditText) view.findViewById(R.id.toolbox_brick_speak_edit_text);
		editText.setText(text);

		editText.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(context);
				final EditText input = new EditText(context);
				input.setText(text);
				input.setSelectAllOnFocus(true);
				dialog.setView(input);
				dialog.setOnCancelListener((OnCancelListener) context);
				dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						text = (input.getText().toString()).trim();
						dialog.cancel();
					}
				});
				dialog.setNeutralButton(context.getString(R.string.cancel_button),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});

				AlertDialog finishedDialog = dialog.create();
				finishedDialog.setOnShowListener(Utils.getBrickDialogOnClickListener(context, input));

				finishedDialog.show();

			}
		});
		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_speak, null);
	}

	@Override
	public Brick clone() {
		return new SpeakBrick(this.sprite, this.text);
	}

}
