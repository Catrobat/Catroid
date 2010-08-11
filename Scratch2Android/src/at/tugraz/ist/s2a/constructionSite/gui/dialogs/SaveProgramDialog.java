package at.tugraz.ist.s2a.constructionSite.gui.dialogs;

import java.io.File;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.s2a.R;
import at.tugraz.ist.s2a.constructionSite.content.ContentManager;

public class SaveProgramDialog extends Dialog{

private ContentManager mContentManager;

public SaveProgramDialog(Context context, ContentManager contentmanager) {
	super(context);
	mContentManager = contentmanager;
}

@Override
protected void onCreate(Bundle savedInstanceState) {

  setContentView(R.layout.dialog_save_program_layout);
  setTitle(R.string.save_file_main);

  EditText file = (EditText) findViewById(R.id.saveFilename);
  file.setTextColor(Color.BLACK);
  file.setText("filename");//TODO Localize
  Button saveButton = (Button) findViewById(R.id.saveButton);
  saveButton.setText("Speichern");//TODO Localize
  
  saveButton.setOnClickListener(new Button.OnClickListener() {
	
	public void onClick(View v) {
	EditText file = (EditText) findViewById(R.id.saveFilename);
	File tfile = new File(file.getText().toString()+".spf");
	mContentManager.saveContent(tfile.toString());
	dismiss();
	}
});

}

}