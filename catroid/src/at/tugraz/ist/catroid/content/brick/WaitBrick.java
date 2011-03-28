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
import at.tugraz.ist.catroid.ui.dialogs.brickdialogs.EditDoubleDialog;

public class WaitBrick implements Brick, OnDismissListener {
	private static final long serialVersionUID = 1L;
    private int timeToWaitInMilliSeconds;
    private Sprite sprite;
    
	public WaitBrick(Sprite sprite, int timeToWaitInMilliseconds) {
		this.timeToWaitInMilliSeconds = timeToWaitInMilliseconds;
		this.sprite = sprite;
	}

	public void execute() {
		long startTime = 0;
		try {
			startTime = System.currentTimeMillis();
			Thread.sleep(timeToWaitInMilliSeconds);
			sprite.setToDraw(true);
		} catch (InterruptedException e) {
			timeToWaitInMilliSeconds = timeToWaitInMilliSeconds - (int)(System.currentTimeMillis() - startTime);
			throw new InterruptedRuntimeException("WaitBrick was interrupted", e);
		}
	}

	public Sprite getSprite() {
		return sprite;
	}

	public long getWaitTime() {
		return timeToWaitInMilliSeconds;
	}
	
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_wait, null);
		EditText edit = (EditText)view.findViewById(R.id.InputValueEditText);
		
		edit.setText((timeToWaitInMilliSeconds/1000.0) + "");
        EditDoubleDialog dialog = new EditDoubleDialog(context, edit, timeToWaitInMilliSeconds/1000.0);
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
		return new WaitBrick(getSprite(),timeToWaitInMilliSeconds);
	}

	public void onDismiss(DialogInterface dialog) {
		timeToWaitInMilliSeconds = (int) Math.round(((EditDoubleDialog)dialog).getValue()*1000);
	}
}
