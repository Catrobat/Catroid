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
import at.tugraz.ist.catroid.utils.Utils;

public class NewProjectDialog extends Dialog {
    private final Context context;

    public NewProjectDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_project);
        setTitle(R.string.new_project_dialog_title);

        Button createNewProjectButton = (Button) findViewById(R.id.createNewProjectButton);
        createNewProjectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String projectName = ((EditText) findViewById(R.id.newProjectNameEditText)).getText().toString();
                try {
                    if (StorageHandler.getInstance().projectExists(projectName)) {
                        Utils.displayErrorMessage(context, context.getString(R.string.projectname_already_exists));
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
				ProjectManager.getInstance().initializeNewProject(projectName, context);
                Intent intent = new Intent(context, ProjectActivity.class);
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