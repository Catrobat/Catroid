package at.tugraz.ist.catroid.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.ui.ProjectActivity;

public class EditSpriteDialog extends Dialog {
    protected ProjectActivity projectActivity;

    public EditSpriteDialog(ProjectActivity projectActivity) {
        super(projectActivity);
        this.projectActivity = projectActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_sprite);
        setTitle(R.string.edit_sprite_dialog_title);

        Button createNewProjectButton = (Button) findViewById(R.id.deleteSpriteButton);
        createNewProjectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ProjectManager projectManager = ProjectManager.getInstance();
                projectManager.getCurrentProject().getSpriteList().remove(projectActivity.getSpriteToEdit());
                if (projectManager.getCurrentSprite() != null
                        && projectManager.getCurrentSprite().equals(projectActivity.getSpriteToEdit())) {
                    projectManager.setCurrentSprite(null);
                }
                dismiss();
            }
        });
        
        Button cancelButton = (Button) findViewById(R.id.renameSpriteButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		dismiss();
        	}
		});
    }
}