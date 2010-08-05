package com.tugraz.android.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ToolboxSpritesDialog extends Dialog implements Observer

{

	private Context mCtx;
	private Animation mSlide_in;
	private Animation mSlide_out;
	
	public ListView mMainListView;
	public EditText mSpriteName;
	public Button mSpriteButton;
	private Button mMainSpriteButton;    
	private ToolboxSpritesAdapter mAdapter;
	public ArrayList<String> mContentArrayList;
	ContentManager mContentManager;
	
	private RelativeLayout mToolboxLayout;
	
	private int mFlagId;
	
	public ToolboxSpritesDialog(Context context, boolean cancelable,
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
		getWindow().setGravity(Gravity.TOP);
		//getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.toolboxsprites);
		//this.setTitle("Objekte");
		
		mSlide_in = AnimationUtils.loadAnimation(mCtx, R.anim.toolboxsprites_in);
		mSlide_out = AnimationUtils.loadAnimation(mCtx, R.anim.toolboxsprites_out);
		mSlide_out.setAnimationListener(new AnimationListener() {
			
			
			public void onAnimationStart(Animation animation) {		}
			
			public void onAnimationRepeat(Animation animation) {		}
			
			public void onAnimationEnd(Animation animation) {
				close();
			}
		}
	
		);
		
		mToolboxLayout = (RelativeLayout) findViewById(R.id.toolboxsprites_layout);
		
		mContentArrayList = mContentManager.getAllSprites();
		
		mMainListView = (ListView) findViewById(R.id.spritesListView);
		
		mAdapter = new ToolboxSpritesAdapter(mCtx, mContentManager.getAllSprites());
		mAdapter.setContentManager(mContentManager);
		mAdapter.setDialog(this);
		mContentManager.setObserver(this);
		
		mMainListView.setAdapter(mAdapter);
		
		mSpriteName = (EditText) findViewById(R.id.newsprite);
		mSpriteButton = (Button) findViewById(R.id.NewSpriteButton);
		mSpriteButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				mContentManager.addSprite(mSpriteName.getText().toString(), new ArrayList<HashMap<String,String>>());
				mAdapter.notifyDataSetChanged();
				dismiss();
				}

		});
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
        mContentArrayList.add("Sprite");
        
	}

	

	public void setContentManager(ContentManager contentManager){
		mContentManager = contentManager;
	}
	

	public void update(Observable observable, Object data) {
		mAdapter.notifyDataSetChanged();	
	}
	
	
	
}

