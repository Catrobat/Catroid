package at.tugraz.ist.catroid.constructionSite.gui.dialogs;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.R;

public class NewProjectDialog extends Dialog{

private ContentManager mContentManager;
private SharedPreferences mPreferences;
private Context mCtx;

public NewProjectDialog(Context context, ContentManager contentmanager) {
	super(context);
	mCtx = context;
	mContentManager = contentmanager;
}

@Override
protected void onCreate(Bundle savedInstanceState) {
	
	mPreferences = ((Activity)mCtx).getPreferences(Activity.MODE_PRIVATE);
	setContentView(R.layout.dialog_add_new_project); //TODO: Own View
  
	EditText file = (EditText) findViewById(R.id.newProjectNameEditText);
	file.setTextColor(Color.BLACK);
	file.setText(R.string.edit_text_filename_standard_input);
	Button commitButton = (Button) findViewById(R.id.commitNewProjectButton);
	commitButton.setText(R.string.new_project_main);

	commitButton.setOnClickListener(new Button.OnClickListener() {
	
	public void onClick(View v) {
	
		EditText projectName = (EditText) findViewById(R.id.newProjectNameEditText);
			
		File newPath = new File(Utils.concatPaths(ConstructionSiteActivity.DEFAULT_PROJECT, projectName.getText().toString()));
		String newSpfFile = new String(projectName.getText().toString());
		if(!newSpfFile.contains(ConstructionSiteActivity.DEFAULT_FILE_ENDING))
			newSpfFile = Utils.addDefaultFileEnding(newSpfFile);
		ConstructionSiteActivity.setRoot(newPath.getAbsolutePath(), newSpfFile);
		
		mContentManager.loadContent(newSpfFile);
		((Activity)mCtx).setTitle(newSpfFile);
		dismiss();
	}
});


}

}