package at.tugraz.ist.catroid.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import at.tugraz.ist.catroid.R;

public class NewProjectDialog extends Dialog {
	private Context context;

	public NewProjectDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.new_project_dialog);
		
		Button createNewProjectButton = (Button) findViewById(R.id.createNewProjectButton);
		createNewProjectButton.setText(R.string.new_project);
		createNewProjectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
			}
		});
		
/* old version
		((Activity) context).getPreferences(Activity.MODE_PRIVATE);
		setContentView(R.layout.new_project_dialog); // TODO: Own View

		EditText file = (EditText) findViewById(R.id.newProjectNameEditText);
		file.setTextColor(Color.BLACK);
		file.setText(R.string.edit_text_filename_standard_input);
		Button commitButton = (Button) findViewById(R.id.createNewProjectButton);
		commitButton.setText(R.string.new_project);

		commitButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {

				String projectName = ((EditText) findViewById(R.id.newProjectNameEditText)).getText().toString();
				if (Utils.projectExists(projectName)) {
					// project already exists -> display error message
					Builder builder = new AlertDialog.Builder(context);

					builder.setTitle(context.getString(R.string.error));
					builder.setMessage(context.getString(R.string.error_project_exists));
					builder.setNeutralButton(context.getString(R.string.close), new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) { }
					});
					builder.show();
				} else {
					// create new project
					File newPath = new File(Utils.concatPaths(ConstructionSiteActivity.DEFAULT_ROOT, projectName));
					String newSpfFile = new String(projectName);
					if (!newSpfFile.contains(ConstructionSiteActivity.DEFAULT_FILE_ENDING))
						newSpfFile = Utils.addDefaultFileEnding(newSpfFile);
					boolean existed = newPath.exists();
					ConstructionSiteActivity.setRoot(newPath.getAbsolutePath(), newSpfFile);

					// if the project already existed just load it
                    if (existed)
                        if(!mContentManager.loadContent(newSpfFile)){
                            //TODO: error message
                        }

                    else {
                        mContentManager.initializeNewProject(projectName);
                    }
					
					((Activity) context).setTitle(newSpfFile);
					dismiss();
				}
			}
		});
		*/
	}

}