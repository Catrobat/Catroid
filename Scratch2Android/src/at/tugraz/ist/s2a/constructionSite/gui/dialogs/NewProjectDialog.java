package at.tugraz.ist.s2a.constructionSite.gui.dialogs;

import java.io.File;

import android.app.Dialog;
import android.content.Context;
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

public NewProjectDialog(Context context, ContentManager contentmanager) {
	super(context);
	mContentManager = contentmanager;
}

@Override
protected void onCreate(Bundle savedInstanceState) {

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
	File newPath = new File("sdcard"+projectName.getText().toString().replace(".spf", ""));
	setRoot(newPath.getAbsolutePath());

    mContentManager.clearSprites();

	dismiss();
	}
});


}
public void setRoot(String root){
	File rootFile = new File(root);
	rootFile.mkdirs();
	ConstructionSiteActivity.ROOT = rootFile.getPath();
	File rootImageFile = new File(root+"images/");
	rootImageFile.mkdirs();
	ConstructionSiteActivity.ROOT_IMAGES = rootImageFile.getPath();
	File rootSoundFile = new File(root+"sounds/");
	rootSoundFile.mkdirs();
	ConstructionSiteActivity.ROOT_SOUNDS = rootSoundFile.getPath();
}
}