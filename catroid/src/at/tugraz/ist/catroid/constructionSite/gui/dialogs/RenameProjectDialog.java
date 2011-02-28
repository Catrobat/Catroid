package at.tugraz.ist.catroid.constructionSite.gui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;
import at.tugraz.ist.catroid.utils.Utils;

public class RenameProjectDialog extends Dialog {

    private ContentManager mContentManager;
    private Context mCtx;

    public RenameProjectDialog(Context context, ContentManager contentmanager) {
        super(context);
        mContentManager = contentmanager;
        mCtx = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ((Activity) mCtx).getPreferences(Activity.MODE_PRIVATE);
        setContentView(R.layout.dialog_change_program_name_layout);

        EditText changeProjectNameEditText = (EditText) findViewById(R.id.changeProjectNameEditText);
        changeProjectNameEditText.setTextColor(Color.BLACK);
        changeProjectNameEditText.setText(ConstructionSiteActivity.SPF_FILE.replace(".spf", ""));

        Button commitButton = (Button) findViewById(R.id.commitChangeButton);
        commitButton.setText(R.string.change_project_name_main);

        commitButton.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                String newProjectName = ((EditText) findViewById(R.id.changeProjectNameEditText)).getText().toString();
                if (Utils.renameProject(mCtx, null, newProjectName)) {
                    String newProjectFileName = Utils.addDefaultFileEnding(newProjectName);
                    if(!mContentManager.loadContent(newProjectFileName)){
                        //TODO: something
                    }

                    ((Activity) mCtx).setTitle(newProjectName);
                } else {
                    Utils.displayErrorMessage(mCtx, mCtx.getString(R.string.error_project_rename));
                }

                dismiss();
            }
        });

    }

}