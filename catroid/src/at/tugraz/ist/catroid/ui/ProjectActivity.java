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

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.dialogs.NewSpriteDialog;

public class ProjectActivity extends Activity {

    final static int NEW_SPRITE_DIALOG = 0;

    private void initListeners() {

        //TODO: access and fill SpriteList

        Button mainMenuButton = (Button) findViewById(R.id.mainMenuButton);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Button NewSpriteButton = (Button) findViewById(R.id.addSpriteButton);
        NewSpriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(NEW_SPRITE_DIALOG);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.project_activity);
        initListeners();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        //Save Content here?

        switch (id) {
        case NEW_SPRITE_DIALOG:
            dialog = new NewSpriteDialog(this);
            break;
        default:
            dialog = null;
            break;
        }

        return dialog;
    }
}
