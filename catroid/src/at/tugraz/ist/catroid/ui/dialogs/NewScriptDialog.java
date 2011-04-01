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
import android.widget.LinearLayout.LayoutParams;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.utils.Utils;

public class NewScriptDialog extends Dialog {
    private final Context context;

    public NewScriptDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_script);
        setTitle(R.string.new_script_dialog_title);
        setCanceledOnTouchOutside(true);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        Button createNewScriptButton = (Button) findViewById(R.id.createNewScriptButton);
        createNewScriptButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String scriptName = ((EditText) findViewById(R.id.newScriptNameEditText)).getText().toString();
                if(scriptName.length() == 0) {
                    Utils.displayErrorMessage(context, context.getString(R.string.error_no_name_entered));
                    return;
                }
                
                ProjectManager projectManager = ProjectManager.getInstance();
                if (projectManager.scriptExists(scriptName)) {
                    Utils.displayErrorMessage(context, context.getString(R.string.scriptname_already_exists));
                    return;
                }
                Script script = new Script(scriptName);
                projectManager.getCurrentSprite().getScriptList().add(script);
                projectManager.setCurrentScript(script);
				// Intent intent = new Intent(context, ScriptActivity.class);
				// context.startActivity(intent);
                dismiss();
                ((EditText) findViewById(R.id.newScriptNameEditText)).setText(null);
            }
        });
        
        Button cancelButton = (Button) findViewById(R.id.cancelDialogButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		dismiss();
        		((EditText) findViewById(R.id.newScriptNameEditText)).setText(null);
        	}
		});
    }
}