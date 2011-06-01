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
package at.tugraz.ist.catroid.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.utils.Utils;

public class NewSpriteDialog extends Dialog {
	private final Context context;

	public NewSpriteDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_new_sprite);
		setTitle(R.string.new_sprite_dialog_title);
		setCanceledOnTouchOutside(true);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		this.setOnShowListener(new OnShowListener() {
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(findViewById(R.id.newSpriteNameEditText), InputMethodManager.SHOW_IMPLICIT);
			}
		});

		Button createNewSpriteButton = (Button) findViewById(R.id.createNewSpriteButton);
		createNewSpriteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String spriteName = ((EditText) findViewById(R.id.newSpriteNameEditText)).getText().toString();
				if (spriteName.length() == 0) {
					Utils.displayErrorMessage(context, context.getString(R.string.error_no_name_entered));
					return;
				}

				ProjectManager projectManager = ProjectManager.getInstance();

				if (projectManager.spriteExists(spriteName)) {
					Utils.displayErrorMessage(context, context.getString(R.string.error_sprite_exists));
					return;
				}
				Sprite sprite = new Sprite(spriteName);
				projectManager.addSprite(sprite);

				((EditText) findViewById(R.id.newSpriteNameEditText)).setText(null);
				dismiss();
			}
		});

		this.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
						case KeyEvent.KEYCODE_ENTER: {
							String spriteName = ((EditText) findViewById(R.id.newSpriteNameEditText)).getText()
									.toString();
							if (spriteName.length() == 0) {
								Utils.displayErrorMessage(context, context.getString(R.string.error_no_name_entered));
								return true;
							}

							ProjectManager projectManager = ProjectManager.getInstance();

							if (projectManager.spriteExists(spriteName)) {
								Utils.displayErrorMessage(context, context.getString(R.string.error_sprite_exists));
								return true;
							}
							Sprite sprite = new Sprite(spriteName);
							projectManager.addSprite(sprite);

							((EditText) findViewById(R.id.newSpriteNameEditText)).setText(null);
							dismiss();
							return true;
						}
						default:
							break;
					}
				}
				return false;
			}
		});

		Button cancelButton = (Button) findViewById(R.id.cancelDialogButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				((EditText) findViewById(R.id.newSpriteNameEditText)).setText(null);
				dismiss();
			}
		});
	}
}