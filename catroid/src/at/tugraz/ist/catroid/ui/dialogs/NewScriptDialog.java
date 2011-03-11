package at.tugraz.ist.catroid.ui.dialogs;

import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class NewScriptDialog extends Dialog {
    private final Context context;

	//private final ProjectManager contentManager;

    public NewScriptDialog(Context context) {
        super(context);
        this.context = context;
		//this.contentManager = contentManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_new_script);
        setTitle(R.string.new_script_dialog_title);

        Button createNewScriptButton = (Button) findViewById(R.id.createNewScriptButton);
        createNewScriptButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String scriptName = ((EditText) findViewById(R.id.newScriptNameEditText)).getText().toString();
                try {
                    if (StorageHandler.getInstance().scriptExists(scriptName)) {
                        Utils.displayErrorMessage(context, context.getString(R.string.scriptname_already_exists));
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
				ProjectManager.getInstance().initializeNewScript(scriptName, context);
                Intent intent = new Intent(context, ScriptActivity.class);
            	context.startActivity(intent);
                dismiss();
            }
        });
        
        Button cancelButton = (Button) findViewById(R.id.cancelDialogButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		dismiss();
        	}
		});
    }
}