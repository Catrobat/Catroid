package at.tugraz.ist.s2a.constructionSite.gui.adapter;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.inputmethodservice.Keyboard.Key;
import android.location.GpsStatus.Listener;
import android.util.Log;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.s2a.R;
import at.tugraz.ist.s2a.constructionSite.content.BrickDefine;
import at.tugraz.ist.s2a.utils.filesystem.MediaFileLoader;

public class ConstructionSiteListViewAdapter extends BaseAdapter implements OnClickListener, TextView.OnEditorActionListener, AdapterView.OnItemSelectedListener{
	private static final float THUMBNAIL_WIDTH = 80;
	private static final float THUMBNAIL_HEIGHT = 80;
	
	private Context mCtx;
    private MediaFileLoader mMediaFileLoader;
    private ListView mMainListView;
    
    private ArrayList<HashMap<String, String>> mBrickList;
    
    private HashMap<String, ArrayList<View>> mViewContainerNotInUse;
    
	public ConstructionSiteListViewAdapter(Context context,
			ArrayList<HashMap<String, String>> data, ListView listview) {
		mCtx = context;
		mBrickList = data;	
		mMainListView = listview;
		mMediaFileLoader = new MediaFileLoader(mCtx);
		mMediaFileLoader.loadSoundContent();
		
		mInflater = (LayoutInflater)mCtx.getSystemService(
			      Context.LAYOUT_INFLATER_SERVICE);
		
	}

	
	
	@Override
	public int getItemViewType(int position) {
		return Integer.parseInt(mBrickList.get(position).get(BrickDefine.BRICK_ID));
	}

	@Override
	public int getViewTypeCount() {	
		return BrickDefine.getNumberOfBrickType();
	}



	private LayoutInflater mInflater;
	
	private View organizeViewHandling(String type, int typeId, View convertView){

		if(convertView != null)
			return convertView;

		View view =  mInflater.inflate(typeId, null); 
		view.setId(Integer.parseInt(type));		

		return view;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final HashMap<String, String> brick = mBrickList.get(position);
		
		final String type = mBrickList.get(position).get(BrickDefine.BRICK_TYPE);
		final String value = mBrickList.get(position).get(BrickDefine.BRICK_VALUE);
		final String value1 = mBrickList.get(position).get(BrickDefine.BRICK_VALUE_1);
		
		
		switch(Integer.valueOf(type).intValue()){
			
			case (BrickDefine.SET_BACKGROUND): 
			{
				View view = organizeViewHandling(type, R.layout.construction_brick_set_background, convertView);
				ImageView imageView = (ImageView) view.findViewWithTag(mCtx.getString
						(R.string.constructional_brick_set_background_image_view_tag));
				imageView.setOnClickListener(this);
				setImage(imageView, position);
				return 	view;			
			}
			case (BrickDefine.PLAY_SOUND): 
			{
				View view = organizeViewHandling(type, R.layout.construction_brick_play_sound, convertView);		
				Spinner spinner = (Spinner) view.findViewWithTag(mCtx.getString
						(R.string.constructional_brick_play_sound_spinner_tag));
				
				final SimpleAdapter adapter = new SimpleAdapter(mCtx, mMediaFileLoader.getSoundContent(), R.layout.sound_spinner,
				new String[] {MediaFileLoader.SOUND_NAME}, new int[] {R.id.SoundSpinnerTextView});
				spinner.setAdapter(adapter);

				try {
					spinner.setSelection(getIndexFromElementSound(adapter, brick.get(BrickDefine.BRICK_VALUE)));
				} catch (Exception e) {}

				spinner.setOnItemSelectedListener(this);

				return view;		
			}
			case (BrickDefine.WAIT): 
			{
				View view = organizeViewHandling(type, R.layout.construction_brick_wait, convertView);
				EditText eText = (EditText) view.findViewWithTag(mCtx.getString(R.string.constructional_brick_wait_edit_text_tag));
				eText.setOnEditorActionListener(this);
				eText.setText(value);
				return view;		
			}
			case (BrickDefine.HIDE):
			{
				return organizeViewHandling(type, R.layout.construction_brick_simple_text_view, convertView);
			}
			case (BrickDefine.SHOW):
			{
				return organizeViewHandling(type, R.layout.construction_brick_simple_text_view, convertView);
			}
			case (BrickDefine.GO_TO):
			{
				View view = organizeViewHandling(type, R.layout.construction_brick_goto, convertView);
				EditText eTextX = (EditText) view.findViewWithTag(mCtx.getString(R.string.constructional_brick_go_to_x_tag));
				eTextX.setOnEditorActionListener(this);
				eTextX.setText(value);
				
				EditText eTextY = (EditText) view.findViewWithTag(mCtx.getString(R.string.constructional_brick_go_to_y_tag));
				eTextY.setOnEditorActionListener(this);
				eTextY.setText(value1);
				return view;
			}	
			case (BrickDefine.SET_COSTUME): 
			{
				View view = organizeViewHandling(type, R.layout.construction_brick_set_costume, convertView);
				ImageView imageView = (ImageView) view.findViewWithTag(mCtx.getString
					(R.string.constructional_brick_set_costume_image_view_tag));
				imageView.setOnClickListener(this);
				setImage(imageView, position);
				return 	view;
			}
			
			default: 
			{
				return null;
		    }
		
		}
	}

	public int  getIndexFromElementSound(SimpleAdapter adapter, String element) {
		ArrayList<HashMap<String, String>> arrayList = mMediaFileLoader.getSoundContent();
		for(int i = 0; i < adapter.getCount(); i++) {
			String value = arrayList.get(i).get(MediaFileLoader.SOUND_PATH);
			if(value.equals((element))) {
				return i;
			}
		}
		return 0;
	}


	//TODO log bitmaps and do not create every time
	public void setImage(ImageView v, int position){
		String value = mBrickList.get(position).get(BrickDefine.BRICK_VALUE);
		
		 if(value != null && value != "" && new File(value).exists()){
				Bitmap bm = null;		
			    bm = BitmapFactory.decodeFile(value); 
			    v.setBackgroundResource(0);
				Matrix matrix = new Matrix();
				float scaleWidth = (((float)THUMBNAIL_WIDTH)/bm.getWidth());
				float scaleHeight = (((float)THUMBNAIL_HEIGHT)/bm.getHeight());
			    matrix.postScale(scaleWidth, scaleHeight);
				Bitmap newbm = Bitmap.createBitmap(bm, 0, 0,bm.getWidth() ,bm.getHeight() , matrix, true);
				v.setImageBitmap(newbm);
		 }
	}
	

	public int getCount() {
		return mBrickList.size();
	}
	
	public Object getItem(int arg0) {
		
		return mBrickList.get(arg0);
	}
	
	public long getItemId(int position) {
		String type = mBrickList.get(position).get(BrickDefine.BRICK_ID);
		return Integer.valueOf(type).intValue();
	}



	public void onClick(View v) {	
		String tag = (String) v.getTag();
		
		if(mCtx.getString(R.string.constructional_brick_set_background_image_view_tag).equals(tag)){
			mMediaFileLoader.openPictureGallery(mMainListView.getPositionForView(v), (ImageView)v);
		}else
		if(mCtx.getString(R.string.constructional_brick_set_costume_image_view_tag).equals(tag)){
			mMediaFileLoader.openPictureGallery(mMainListView.getPositionForView(v), (ImageView)v);
		}
	}

	public void onItemSelected(AdapterView<?> spinner, View v, int position,
			long id) {
		String tag = (String) v.getTag();
	
		if(mCtx.getString(R.string.constructional_brick_play_sound_spinner_tag).equals(tag)){	
			int brickPosition = mMainListView.getPositionForView(spinner);
			HashMap<String, String> map = (HashMap<String, String>)spinner.getAdapter().getItem(position);
			mBrickList.get(brickPosition).put(BrickDefine.BRICK_VALUE, map.get(MediaFileLoader.SOUND_PATH));
		}		
	}



	public void onNothingSelected(AdapterView<?> arg0) {}



	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		
		if(actionId == 6 || event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
			String tag = (String) v.getTag();
			
			if(mCtx.getString(R.string.constructional_brick_go_to_x_tag).equals(tag)){
				int brickPosition = mMainListView.getPositionForView((EditText)v);
				mBrickList.get(brickPosition).put(BrickDefine.BRICK_VALUE, ((EditText)v).getText().toString());
				return false;
			}else
			if(mCtx.getString(R.string.constructional_brick_go_to_y_tag).equals(tag)){
				int brickPosition = mMainListView.getPositionForView((EditText)v);
				mBrickList.get(brickPosition).put(BrickDefine.BRICK_VALUE_1, ((EditText)v).getText().toString());
				return false;
			}else
			if(mCtx.getString(R.string.constructional_brick_wait_edit_text_tag).equals(tag)){
				int brickPosition = mMainListView.getPositionForView((EditText)v);
				mBrickList.get(brickPosition).put(BrickDefine.BRICK_VALUE, ((EditText)v).getText().toString());
				return false;
			}
		}

		return false;
	}

	
//public View getView(int position, View convertView, ViewGroup parent) {
//		
//		
//		
//		final HashMap<String, String> brick = mBrickList.get(position);
//		//TODO Reuse Views
//		final String type = mBrickList.get(position).get(BrickDefine.BRICK_TYPE);
//		final String value = mBrickList.get(position).get(BrickDefine.BRICK_VALUE);
//
//		LayoutInflater inflater = (LayoutInflater)mCtx.getSystemService(
//	      Context.LAYOUT_INFLATER_SERVICE); 
//		
//		switch(Integer.valueOf(type).intValue()){
//		case (BrickDefine.SET_BACKGROUND): 
//		{
//			RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.construction_brick_set_background, null);
//			TextView text = (TextView)view.getChildAt(1);
//			text.setText(R.string.set_background_main_adapter);
//
//			ImageView imageView = (ImageView)view.getChildAt(0);
//			imageView.setOnClickListener(new View.OnClickListener() {
//				
//				public void onClick(View v) {
//					    Log.d("Position", ((Integer)mMainListView.getPositionForView(v)).toString());
//						mMediaFileLoader.openPictureGallery(mMainListView.getPositionForView(v), (ImageView)v);
//				}		
//				});
//
//		    if(value != null && value != "" && new File(value).exists()){
//		    	Bitmap bm = null;	
//		    	Log.d("Content", value);
//		    	bm = BitmapFactory.decodeFile(value); 
//		    	imageView.setBackgroundResource(0);
//		    	Matrix matrix = new Matrix();
//		    	float scaleWidth = (((float)THUMBNAIL_WIDTH)/bm.getWidth());
//		    	float scaleHeight = (((float)THUMBNAIL_HEIGHT)/bm.getHeight());
//		    	matrix.postScale(scaleWidth, scaleHeight);
//		    	Bitmap newbm = Bitmap.createBitmap(bm, 0, 0,bm.getWidth() ,bm.getHeight() , matrix, true);
//		    	imageView.setImageBitmap(newbm);
//		    }
////		    LayoutParams params = (LayoutParams) view.getLayoutParams();
////		    params.addRule(RelativeLayout.ALIGN_BOTTOM, parent.getChildAt(size-1).getId());
////		    view.setLayoutParams(params);
//
//			return view;
//		}
//		case (BrickDefine.PLAY_SOUND): 
//		{
//			RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.construction_brick_play_sound, null);
//			TextView text = (TextView)view.getChildAt(0);
//			text.setText(R.string.play_sound_main_adapter);
//			
//			Spinner spinner = (Spinner)view.getChildAt(1);
//         
//			final SimpleAdapter adapter = new SimpleAdapter(mCtx, mMediaFileLoader.getSoundContent(), R.layout.sound_spinner,
//					new String[] {MediaFileLoader.SOUND_NAME},
//	                new int[] {R.id.SoundSpinnerTextView});
//			spinner.setAdapter(adapter);
//			if (adapter.getCount() != 0) {
//				spinner.setSelection(0);
//				OnItemSelectedListener listener = new OnItemSelectedListener(){
//	
//					
//					public void onItemSelected(AdapterView<?> arg0, View arg1,
//							int arg2, long arg3) {
//							brick.put(BrickDefine.BRICK_VALUE, ((HashMap<String, String>)adapter.getItem(arg2)).get(MediaFileLoader.SOUND_PATH));
//					}
//	
//					
//					public void onNothingSelected(AdapterView<?> arg0) {
//						// TODO Auto-generated method stub
//						
//					}
//					
//				};
//			    spinner.setOnItemSelectedListener(listener);
//				//view.setBackgroundColor(Color.BLUE);
//				spinner.setSelection(getIndexFromElementSound(adapter, brick.get(BrickDefine.BRICK_VALUE)));
//			}
//			
//			return view;
//		}
//		case (BrickDefine.WAIT): 
//		{
//			RelativeLayout view =  (RelativeLayout)inflater.inflate(R.layout.construction_brick_wait, null);
//			  TextView text = (TextView) view.getChildAt(0);
//			  text.setText(R.string.wait_main_adapter);
//			  //text.setTextColor(Color.BLUE);
//	          EditText etext = (EditText) view.getChildAt(1);
//	          etext.setText("1");
//	          
//	          etext.setText(brick.get(BrickDefine.BRICK_VALUE));
//	          
//	          etext.addTextChangedListener(new TextWatcher()
//	          {
//
//				
//				public void afterTextChanged(Editable s) {
//					// TODO Auto-generated method stub
//					
//				}
//
//				
//				public void beforeTextChanged(CharSequence s, int start,
//						int count, int after) {
//					// TODO Auto-generated method stub
//					
//				}
//
//				
//				public void onTextChanged(CharSequence s, int start,
//						int before, int count) {
//					brick.remove(BrickDefine.BRICK_VALUE);
//					brick.put(BrickDefine.BRICK_VALUE, s.toString());	
//				}  
//	          });
//	         // view.setBackgroundColor(Color.argb(255, 255, 215, 0));
//			return view;
//			
//		}
//		case (BrickDefine.HIDE):
//		{
//			LinearLayout view =  (LinearLayout)inflater.inflate(R.layout.construction_brick_simple_text_view, null);
//			TextView text = (TextView) view.getChildAt(0);
//			text.setText(R.string.hide_main_adapter);
//			//view.setBackgroundColor(Color.argb(255, 255, 215, 100));
//			return view;
//		}
//		case (BrickDefine.SHOW):
//		{
//			LinearLayout view =  (LinearLayout)inflater.inflate(R.layout.construction_brick_simple_text_view, null);
//			TextView text = (TextView) view.getChildAt(0);
//			text.setText(R.string.show_main_adapter);
//			//view.setBackgroundColor(Color.argb(255, 255, 215, 200));
//			return view;
//		}
//		 case (BrickDefine.GO_TO):
//		  {
//		   RelativeLayout view =  (RelativeLayout)inflater.inflate(R.layout.construction_brick_goto, null);
//		   
//		   TextView text = (TextView) view.getChildAt(0);
//		   text.setText(R.string.goto_main_adapter);
//		   
//		   LinearLayout layout = (LinearLayout) view.getChildAt(1);
//		   
//		   EditText textX = (EditText) layout.getChildAt(0);
//		   textX.setText(brick.get(BrickDefine.BRICK_VALUE));
//		   textX.addTextChangedListener(new TextWatcher()
//		           {
//		    
//		    public void afterTextChanged(Editable s) {
//		     // TODO Auto-generated method stub 
//		    }
//		    
//		    public void beforeTextChanged(CharSequence s, int start,
//		      int count, int after) {
//		     // TODO Auto-generated method stub     
//		    }
//		    
//		    public void onTextChanged(CharSequence s, int start,
//		      int before, int count) {
//		     brick.remove(BrickDefine.BRICK_VALUE);
//		     brick.put(BrickDefine.BRICK_VALUE, s.toString());     
//		    } 
//		           });
//		   
//		   EditText textY = (EditText) layout.getChildAt(1);
//		   textY.setText(brick.get(BrickDefine.BRICK_VALUE_1));
//		   textY.addTextChangedListener(new TextWatcher()
//		           {
//		    
//		    public void afterTextChanged(Editable s) {
//		     // TODO Auto-generated method stub 
//		    }
//		    
//		    public void beforeTextChanged(CharSequence s, int start,
//		      int count, int after) {
//		     // TODO Auto-generated method stub 
//		    }
//		    
//		    public void onTextChanged(CharSequence s, int start,
//		      int before, int count) {
//		     brick.remove(BrickDefine.BRICK_VALUE_1);
//		     brick.put(BrickDefine.BRICK_VALUE_1, s.toString()); 
//		    } 
//		           });
//		   //view.setBackgroundColor(Color.argb(255, 255, 215, 255));
//		   return view;
//		  }
//	
//		case (BrickDefine.SET_COSTUME): 
//		{
//			RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.construction_brick_set_costume, null);
//			//text1.setTextColor(Color.BLUE);
//			TextView text = (TextView)view.getChildAt(1);
//			text.setText(R.string.costume_main_adapter);
//			//text2.setTextColor(Color.BLUE);
//			//view.setBackgroundColor(Color.argb(255, 139, 0, 50));
//
//			ImageView imageView = (ImageView)view.getChildAt(0);
//			imageView.setOnClickListener(new View.OnClickListener() {
//				
//				public void onClick(View v) {
//				  mMediaFileLoader.openPictureGallery(mMainListView.getPositionForView(v), (ImageView)v);
//				}
//			});
//			 if(value != null && value != "" && new File(value).exists()){
//				Bitmap bm = null;	
//				Log.d("Content", value);
//			    bm = BitmapFactory.decodeFile(value); 
//			    imageView.setBackgroundResource(0);
//				Matrix matrix = new Matrix();
//				float scaleWidth = (((float)THUMBNAIL_WIDTH)/bm.getWidth());
//				float scaleHeight = (((float)THUMBNAIL_HEIGHT)/bm.getHeight());
//			    matrix.postScale(scaleWidth, scaleHeight);
//				Bitmap newbm = Bitmap.createBitmap(bm, 0, 0,bm.getWidth() ,bm.getHeight() , matrix, true);
//				imageView.setImageBitmap(newbm);
//			    }
//			
//			return view;
//		}
//		
//		
//		
//		
//		case (BrickDefine.NOT_DEFINED):
//		{
//			return null;
//		}
//		default: 
//		{
//			// Not defined Error
//			return null;
//	    }
//		
//		}
//	}

}
