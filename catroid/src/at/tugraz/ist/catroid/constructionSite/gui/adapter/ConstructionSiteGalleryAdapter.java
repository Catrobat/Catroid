package at.tugraz.ist.catroid.constructionSite.gui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.utils.ImageContainer;

public class ConstructionSiteGalleryAdapter extends BaseAdapter {
	
	private Context mCtx;
	private ArrayList<String> mGalleryData;
	private ImageContainer mImageContainer;
	private LayoutInflater mInflater;
	
    public ConstructionSiteGalleryAdapter(Context c, ArrayList<String> data, ImageContainer imageContainer) {
    	mCtx = c;
    	mGalleryData = data;
    	mImageContainer = imageContainer;
    	
    	mInflater = (LayoutInflater)mCtx.getSystemService(
			      Context.LAYOUT_INFLATER_SERVICE);	
    }

    public int getCount() {
        return mGalleryData.size();
    }

    public Object getItem(int position) {
        return mGalleryData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        View view;
        if (convertView == null)
        	view =  mInflater.inflate(R.layout.construction_gallery_element, null); 
        else
        	view = convertView;
        	
        	
		imageView = (ImageView) view.findViewWithTag(mCtx.getString
					(R.string.constructional_gallery_element_image_view));
		//imageView.setBackgroundResource(android.R.drawable.picture_frame);
		
            //imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            //imageView.setLayoutParams(new Gallery.LayoutParams(100, 100));
            


        imageView.setImageBitmap((mImageContainer.getImage(mGalleryData.get(position))));

        return view;
    }
}
