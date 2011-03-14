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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.utils.Utils;

public class NewSpriteDialog extends Dialog {
    private final Context context;

    public NewSpriteDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_new_sprite);
        setTitle(R.string.new_sprite_dialog_title);

        //EditText clearText = (EditText) findViewById(R.id.newScriptNameEditText);
       
        
        Button createNewSpriteButton = (Button) findViewById(R.id.createNewSpriteButton);
        createNewSpriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String spriteName = ((EditText) findViewById(R.id.newSpriteNameEditText)).getText().toString();

                for (Sprite tempSprite : ProjectManager.getInstance().getCurrentProject().getSpriteList()) {
                    if (tempSprite.getName().equalsIgnoreCase(spriteName)) {
                        Utils.displayErrorMessage(context, context.getString(R.string.spritename_already_exists));
                        return;
                    }
                }
                Sprite sprite = new Sprite(spriteName);
                ProjectManager.getInstance().addSprite(sprite);
                //Intent intent = new Intent(context, SpriteActivity.class);
                //context.startActivity(intent);
                //((EditText) findViewById(R.id.newSpriteNameEditText)).clearComposingText();
                dismiss();
                ((EditText) findViewById(R.id.newSpriteNameEditText)).setText(null);
            }
        });
        
        Button cancelButton = (Button) findViewById(R.id.cancelDialogButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		dismiss();
        		((EditText) findViewById(R.id.newSpriteNameEditText)).setText(null);
        	}
		});
    }

	/**
	 * @param string
	 */
	
}