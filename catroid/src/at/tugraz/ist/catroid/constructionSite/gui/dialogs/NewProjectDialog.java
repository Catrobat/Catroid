package at.tugraz.ist.catroid.constructionSite.gui.dialogs;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;
import at.tugraz.ist.catroid.utils.Utils;

public class NewProjectDialog extends Dialog {

	private ContentManager mContentManager;
	private Context mCtx;

	public NewProjectDialog(Context context, ContentManager contentmanager) {
		super(context);
		mCtx = context;
		mContentManager = contentmanager;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		((Activity) mCtx).getPreferences(Activity.MODE_PRIVATE);
		setContentView(R.layout.dialog_add_new_project); // TODO: Own View

		EditText file = (EditText) findViewById(R.id.newProjectNameEditText);
		file.setTextColor(Color.BLACK);
		file.setText(R.string.edit_text_filename_standard_input);
		Button commitButton = (Button) findViewById(R.id.commitNewProjectButton);
		commitButton.setText(R.string.new_project_main);

		commitButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {

				String projectName = ((EditText) findViewById(R.id.newProjectNameEditText)).getText().toString();
				if (Utils.projectExists(projectName)) {
					// project already exists -> display error message
					Builder builder = new AlertDialog.Builder(mCtx);

					builder.setTitle(mCtx.getString(R.string.error));
					builder.setMessage(mCtx.getString(R.string.error_project_exists));
					builder.setNeutralButton(mCtx.getString(R.string.close), new OnClickListener() {
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
					
					((Activity) mCtx).setTitle(newSpfFile);
					dismiss();
				}
			}
		});

	}

}