package at.tugraz.ist.s2a.constructionSite.gui.dialogs;

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
import at.tugraz.ist.s2a.ConstructionSiteActivity;
import at.tugraz.ist.s2a.R;
import at.tugraz.ist.s2a.constructionSite.content.ContentManager;

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
  setContentView(R.layout.dialog_save_program_layout); //TODO: Own View
  
  EditText file = (EditText) findViewById(R.id.saveFilename);
  file.setTextColor(Color.BLACK);
  file.setText(R.string.edit_text_filename_standard_input);
  Button saveButton = (Button) findViewById(R.id.saveButton);
  saveButton.setText(R.string.new_project_main);
  
  saveButton.setOnClickListener(new Button.OnClickListener() {
	
	public void onClick(View v) {
	
	EditText projectName = (EditText) findViewById(R.id.saveFilename);
	//TODO save old file
	//mContentManager.saveContent(tfile.toString());
			
	Environment.getExternalStorageDirectory(); 
	File newPath = new File(ConstructionSiteActivity.DEFAULT_PROJECT+projectName.getText().toString());
	String newSpfFile = new String(projectName.getText().toString());
	if(!newSpfFile.contains(".spf"))
		newSpfFile = newSpfFile + ".spf";
	ConstructionSiteActivity.setRoot(newPath.getAbsolutePath(), newSpfFile);

    mContentManager.clearSprites();
    

    
	dismiss();
	}
});


}

}