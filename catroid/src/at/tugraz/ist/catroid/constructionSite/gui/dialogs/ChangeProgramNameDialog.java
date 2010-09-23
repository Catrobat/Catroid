package at.tugraz.ist.catroid.constructionSite.gui.dialogs;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.R;

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
  setContentView(R.layout.dialog_change_program_name_layout);
  
  EditText newNameToChange = (EditText) findViewById(R.id.changeProjectNameEditText);
  newNameToChange.setTextColor(Color.BLACK);
  
  newNameToChange.setText(ConstructionSiteActivity.SPF_FILE.replace(".spf", ""));
  
  Button commitButton = (Button) findViewById(R.id.commitChangeButton);
  commitButton.setText(R.string.change_project_name_main);
  
  commitButton.setOnClickListener(new Button.OnClickListener() {
	
	public void onClick(View v) {
		EditText newNameToChangeText = (EditText) findViewById(R.id.changeProjectNameEditText);//TODO change this to own xml
	
		File old_path = new File(ConstructionSiteActivity.ROOT);
		File new_path = new File(Utils.concatPaths(old_path.getParent(),newNameToChangeText.getText().toString()));
		old_path.renameTo(new_path);
	
		File newPathOldSpf = new File(Utils.concatPaths(new_path.getAbsolutePath(),  ConstructionSiteActivity.SPF_FILE));
		if(Utils.copyFile(newPathOldSpf.getAbsolutePath(), 
				Utils.concatPaths(new_path.getAbsolutePath(), newNameToChangeText.getText().toString())+".spf"))
			Utils.deleteFile(newPathOldSpf.getAbsolutePath());
		else
			Log.e("ChangeProgramNameDialog", "Copy File failed");
		
		ConstructionSiteActivity.setRoot(new_path.getAbsolutePath(), newNameToChangeText.getText().toString()+".spf");
		String name = Utils.addDefaultFileEnding(newNameToChangeText.getText().toString());
		mContentManager.loadContent(name);
		((Activity)mCtx).setTitle(name);
	
		dismiss();
	}
});

}

}