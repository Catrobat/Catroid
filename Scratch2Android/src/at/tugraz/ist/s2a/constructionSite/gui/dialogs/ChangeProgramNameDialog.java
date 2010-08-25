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

public class ChangeProgramNameDialog extends Dialog{

private ContentManager mContentManager;
private SharedPreferences mPreferences;
private Context mCtx;


public ChangeProgramNameDialog(Context context, ContentManager contentmanager) {
	super(context);
	mContentManager = contentmanager;
	mCtx = context;
}

@Override
protected void onCreate(Bundle savedInstanceState) {
	
  mPreferences = ((Activity)mCtx).getPreferences(Activity.MODE_PRIVATE);
  setContentView(R.layout.dialog_save_program_layout);//TODO Own xml/View
  
  EditText file = (EditText) findViewById(R.id.saveFilename);
  file.setTextColor(Color.BLACK);
  File file1 = new File(ConstructionSiteActivity.ROOT);
  file.setText(file1.getAbsolutePath().replace(file1.getParent(), "").replace("/", ""));
  Button saveButton = (Button) findViewById(R.id.saveButton);
  saveButton.setText(R.string.change_project_name_main);
  
  saveButton.setOnClickListener(new Button.OnClickListener() {
	
	public void onClick(View v) {
	
	EditText etext = (EditText) findViewById(R.id.saveFilename);//TODO change this to own xml
	
	File old_path = new File(ConstructionSiteActivity.ROOT);
	File new_path = new File(old_path.getParent()+"/"+etext.getText().toString());	
	File spfFile = new File(etext.getText().toString().subSequence(0, etext.getText().toString().length()-2)+".spf");
	
	Environment.getExternalStorageDirectory(); 
	old_path.renameTo(new_path);
	
	ConstructionSiteActivity.setRoot(new_path.getAbsolutePath(), spfFile.toString());
	mContentManager.clearSprites();
	//TODO not really efficient
	mContentManager.loadContent(spfFile.toString());
	
	dismiss();
	}
});

}

}