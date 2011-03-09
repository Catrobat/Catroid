package at.tugraz.ist.catroid.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.utils.Utils;

public class NewSpriteDialog extends Dialog {
    private final Context context;

    public NewSpriteDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_new_sprite);
        setTitle(R.string.new_sprite_dialog_title);

        Button createNewSpriteButton = (Button) findViewById(R.id.createNewSpriteButton);
        createNewSpriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String spriteName = ((EditText) findViewById(R.id.newSpriteNameEditText)).getText().toString();

                for (Sprite tempSprite : ProjectManager.getInstance().getCurrentProject().getSpriteList()) {
                    if (tempSprite.getName().equalsIgnoreCase(spriteName)) {
                        Utils.displayErrorMessage(context, context.getString(R.string.spritename_already_exists));
                        return;
                    }
                }
                Sprite sprite = new Sprite(spriteName);
                ProjectManager.getInstance().addSprite(sprite);
                //TODO: go to new activity
                //Intent intent = new Intent(context, SpriteActivity.class);
                //context.startActivity(intent);
                dismiss();
            }
        });
        
        Button cancelButton = (Button) findViewById(R.id.cancelDialogButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		dismiss();
        	}
		});
    }
}