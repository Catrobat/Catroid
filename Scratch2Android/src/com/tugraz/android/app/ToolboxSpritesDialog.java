package com.tugraz.android.app;

import java.util.ArrayList;
import java.util.HashMap;

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

public class ToolboxSpritesDialog extends Dialog

{

	private Context mCtx;
	private Animation mSlide_in;
	private Animation mSlide_out;
	
	public ListView mMainListView;
	//TODO choose better name
	public EditText mEditText;
	public Button mSpriteButton;
	private Button mMainSpriteButton;    
	private ToolboxSpritesAdapter mAdapter;
	public ArrayList<String> mContentArrayList;
	ContentManager mContentManager;
	private String mSpriteText = "Neuer Sprite";
	
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
		//TODO set what to do in a text view, try to shorten the name in the button
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
		mMainListView.setAdapter(mAdapter);
		
		mEditText = (EditText) findViewById(R.id.newsprite);
		mSpriteButton = (Button) findViewById(R.id.NewSpriteButton);
		mSpriteButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				mContentManager.addSprite(mSpriteText, new ArrayList<HashMap<String,String>>());
				mAdapter.notifyDataSetChanged();
				}

		});
		mEditText.addTextChangedListener(new TextWatcher()
		{

				
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}

				
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub
				}

				
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					mSpriteText = s.toString();
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
	
	
	
}

