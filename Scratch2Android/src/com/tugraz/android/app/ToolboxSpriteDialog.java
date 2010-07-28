package com.tugraz.android.app;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ToolboxSpriteDialog extends Dialog

{

	private Context mCtx;
	private Animation mSlide_in;
	private Animation mSlide_out;
	
	protected ListView mMainListView;
	private BaseAdapter mAdapter;
	public ArrayList<HashMap<String, String>> mContentArrayList;
	ContentManager mContentManager;
	   
	
	private LinearLayout mToolboxLayout;
	
	private int mFlagId;
	
	public ToolboxSpriteDialog(Context context, boolean cancelable,
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
		mMainListView = (ListView) findViewById(R.id.toolboxListView);
		
		
		allBricks();
		mAdapter = new ToolboxSpriteAdapter(mCtx, mContentArrayList);
		((ToolboxSpriteAdapter)mAdapter).setContentManager(mContentManager);
		
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
	public void allBricks(){
		HashMap<String, String> map = new HashMap<String, String>();
		map = new HashMap<String, String>();
	    map.put(BrickDefine.BRICK_ID, "2");
	    map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
	    map.put(BrickDefine.BRICK_NAME, "");
	    map.put(BrickDefine.BRICK_VALUE, "1");
	    mContentArrayList.add(map);
	    map = new HashMap<String, String>();
	    map.put(BrickDefine.BRICK_ID, "3");
	    map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
	    map.put(BrickDefine.BRICK_NAME, "");
	    map.put(BrickDefine.BRICK_VALUE, "1");
	    mContentArrayList.add(map);
	    map = new HashMap<String, String>();
	    map.put(BrickDefine.BRICK_ID, "4");
	    map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.HIDE));
	    map.put(BrickDefine.BRICK_NAME, "");
	    map.put(BrickDefine.BRICK_VALUE, "");
	    mContentArrayList.add(map);
	    map = new HashMap<String, String>();
	    map.put(BrickDefine.BRICK_ID, "5");
	    map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SHOW));
	    map.put(BrickDefine.BRICK_NAME, "");
	    map.put(BrickDefine.BRICK_VALUE, "");
	    mContentArrayList.add(map);
	    map = new HashMap<String, String>();
	    map.put(BrickDefine.BRICK_ID, "6");
	    map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.GO_TO));
	    map.put(BrickDefine.BRICK_NAME, "");
	    map.put(BrickDefine.BRICK_VALUE, "1");
	    map.put(BrickDefine.BRICK_VALUE, "1");
	    mContentArrayList.add(map);
	    map = new HashMap<String, String>();
	    map.put(BrickDefine.BRICK_ID, "7");
	    map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_COSTUME));
	    map.put(BrickDefine.BRICK_NAME, "");
	    map.put(BrickDefine.BRICK_VALUE, "1");
	    mContentArrayList.add(map);
		
	}

	

	public void setContentManager(ContentManager contentManager){
		mContentManager = contentManager;
	}
	

	
}
