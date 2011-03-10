package at.tugraz.ist.catroid.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        setContentView(R.layout.dialog_new_script);
        setTitle(R.string.new_script_dialog_title);

        Button createNewScriptButton = (Button) findViewById(R.id.createNewScriptButton);
        createNewScriptButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ProjectManager projectManager = ProjectManager.getInstance();
                String scriptName = ((EditText) findViewById(R.id.newScriptNameEditText)).getText().toString();
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