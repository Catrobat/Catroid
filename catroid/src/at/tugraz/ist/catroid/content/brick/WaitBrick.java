package at.tugraz.ist.catroid.content.brick;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.exception.InterruptedRuntimeException;
import at.tugraz.ist.catroid.ui.dialogs.brickdialogs.EditIntegerDialog;

public class WaitBrick implements Brick, OnDismissListener {
	private static final long serialVersionUID = 1L;
    private int timeToWaitInMilliseconds;
    
	public WaitBrick(int timeToWaitInMilliseconds) {
		this.timeToWaitInMilliseconds = timeToWaitInMilliseconds;
	}

	public void execute() {
		long startTime = 0;
		try {
			startTime = System.currentTimeMillis();
			Thread.sleep(timeToWaitInMilliseconds);
		} catch (InterruptedException e) {
			timeToWaitInMilliseconds = timeToWaitInMilliseconds - (int)(System.currentTimeMillis() - startTime);
			throw new InterruptedRuntimeException("WaitBrick was interrupted", e);
		}
	}

	public Sprite getSprite() {
		return null;
	}

	public long getWaitTime() {
		return timeToWaitInMilliseconds;
	}
	
	public View getView(Context context, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_wait, null);
		EditText edit = (EditText)view.findViewById(R.id.InputValueEditText);
		
		edit.setText(timeToWaitInMilliseconds + "");
        EditIntegerDialog dialog = new EditIntegerDialog(context, edit, timeToWaitInMilliseconds);
        dialog.setOnDismissListener(this);
		edit.setOnClickListener(dialog);
		
		return view;
	}
	
	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_wait, null);
        return view;
    }
	
	@Override
    public Brick clone() {
		return new WaitBrick(timeToWaitInMilliseconds);
	}

	public void onDismiss(DialogInterface dialog) {
		timeToWaitInMilliseconds = ((EditIntegerDialog)dialog).getValue();
	}
}
