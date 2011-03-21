package at.tugraz.ist.catroid.content.brick;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.entities.PrimitiveWrapper;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.exception.InterruptedRuntimeException;
import at.tugraz.ist.catroid.ui.dialogs.brickdialogs.EditIntegerDialog;

public class WaitBrick implements Brick {
	private static final long serialVersionUID = 1L;
    protected PrimitiveWrapper<Integer> timeToWaitInMilliseconds;
    
	public WaitBrick(int timeToWaitInMilliseconds) {
		this.timeToWaitInMilliseconds = new PrimitiveWrapper<Integer>(timeToWaitInMilliseconds);
	}

	public void execute() {
		long startTime = 0;
		try {
			startTime = System.currentTimeMillis();
			Thread.sleep(timeToWaitInMilliseconds.getValue());
		} catch (InterruptedException e) {
			timeToWaitInMilliseconds.setValue(timeToWaitInMilliseconds.getValue() - (int)(System.currentTimeMillis() - startTime));
			throw new InterruptedRuntimeException("WaitBrick was interrupted", e);
		}
	}

	public Sprite getSprite() {
		return null;
	}

	public long getWaitTime() {
		return timeToWaitInMilliseconds.getValue();
	}
	
	public View getView(Context context, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_wait, null);
		EditText edit = (EditText)view.findViewById(R.id.InputValueEditText);
		
		edit.setText(timeToWaitInMilliseconds.getValue() + "");
		
        EditIntegerDialog dialog = new EditIntegerDialog(context, edit, timeToWaitInMilliseconds);
		
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
		return new WaitBrick(timeToWaitInMilliseconds.getValue());
	}
}
