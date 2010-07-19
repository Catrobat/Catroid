package com.tugraz.android.app;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.view.View.OnClickListener;

public class ToolboxDialog extends Dialog

{

	private Context mCtx;
	private Animation mSlide_in;
	private Animation mSlide_out;
	
	protected ListView mMainListView;
	private ToolboxAdapter mAdapter;
	public ArrayList<HashMap<String, String>> mContentArrayList;
	ContentManager mContentManager;
	
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
		}
	
		);
		
		mToolboxLayout = (LinearLayout) findViewById(R.id.toolbox_layout);
		
		//Set Bricks
		mContentArrayList = new ArrayList<HashMap<String,String>>();
		
		testSet();
		
		mMainListView = (ListView) findViewById(R.id.toolboxListView);
		
		mAdapter = new ToolboxAdapter(mCtx, mContentArrayList);
		mAdapter.setContentManager(mContentManager);
		mMainListView.setAdapter(mAdapter);
	        
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
	
	/**
	 * test method
	 *
	 */
	public void testSet(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "bla");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "2");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test2");
        map.put(BrickDefine.BRICK_VALUE, "blabla1");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "3");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "Test3");
        map.put(BrickDefine.BRICK_VALUE, "blabla2");
        mContentArrayList.add(map);
	}

	

	public void setContentManager(ContentManager contentManager){
		mContentManager = contentManager;
	}
	
}
