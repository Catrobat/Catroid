package at.tugraz.ist.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;

public class NewProjectDialog extends AlertDialog {
    private final Context context;
//    private final ContentManager contentManager;

    public NewProjectDialog(Context context, ContentManager contentManager) {
        super(context);
        this.context = context;
//        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
//		final EditText input = new EditText(context);
//		alert.setView(input);
//		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//				String value = input.getText().toString().trim();
//				Toast.makeText(context_, value,
//						Toast.LENGTH_SHORT).show();
//			}
//		});
//
//		alert.setNegativeButton("Cancel",
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						dialog.cancel();
//					}
//				});
//
//		alert.show();
        
//        setView(input);


        
//        this.context = context;
//        this.contentManager = contentManager;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//    	EditText input = new EditText(context);
//    	setView(input);
//    	setTitle(R.string.new_project_dialog_title);
    	
    	setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.new_project_dialog_button),
    			new OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						
						
					}
				});
    	
    	setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel_button),
    			new OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						
						dismiss();
					}
				});
    	
//        setContentView(R.layout.new_project_dialog);
//        setTitle(R.string.new_project_dialog_title);
//
//        Button createNewProjectButton = (Button) findViewById(R.id.createNewProjectButton);
//        createNewProjectButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                String projectName = ((EditText) findViewById(R.id.newProjectNameEditText)).getText().toString();
//                try {
//                    if (StorageHandler.getInstance().projectExists(projectName)) {
//                        Utils.displayErrorMessage(context, context.getString(R.string.projectname_already_exists));
//                        return;
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                contentManager.initializeNewProject(projectName);
//                dismiss();
//            }
//        });
//        
//        Button cancelButton = (Button) findViewById(R.id.cancelDialogButton);
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//        	public void onClick(View v) {
//        		dismiss();
//        	}
//		});
    }
}