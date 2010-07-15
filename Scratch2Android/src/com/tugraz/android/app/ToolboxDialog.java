package com.tugraz.android.app;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class ToolboxDialog extends Dialog

{

	private Context mCtx;
	private Animation mSlide_in;
	private Animation mSlide_out;
	
	private LinearLayout mToolboxLayout;
	
	private int mFlagId;
	
	public ToolboxDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener, int flagid) {
		super(context, cancelable, cancelListener);
		mCtx = context;
		mFlagId = flagid;
	}
	
	public void setFlag(int flagid){
		mFlagId = flagid;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		//getWindow().setGravity(Gravity.BOTTOM);
		//getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.toolbox);
		this.setTitle("Baukasten");
		
		mSlide_in = AnimationUtils.loadAnimation(mCtx, R.anim.toolbox_in);
		mSlide_out = AnimationUtils.loadAnimation(mCtx, R.anim.toolbox_out);
		mSlide_out.setAnimationListener(new AnimationListener() {
			
			
			public void onAnimationStart(Animation animation) {		}
			
			public void onAnimationRepeat(Animation animation) {		}
			
			public void onAnimationEnd(Animation animation) {
				close();
			}
		});
		
		mToolboxLayout = (LinearLayout) findViewById(R.id.toolbox_layout);
	}

	@Override
	public void show() {
		super.show();
		mToolboxLayout.startAnimation(mSlide_in);
	}

	@Override
	public void cancel() {
		mToolboxLayout.startAnimation(mSlide_out);
		
	}
	
	private void close() {
		super.cancel();
	}
	
}
