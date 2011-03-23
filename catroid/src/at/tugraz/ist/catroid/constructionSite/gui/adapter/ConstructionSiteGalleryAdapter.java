package at.tugraz.ist.catroid.constructionSite.gui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.sprite.Costume;

public class ConstructionSiteGalleryAdapter extends BaseAdapter {

	private Context context;
	private List<Costume> costumeList;
	private LayoutInflater inflater;

//	public ConstructionSiteGalleryAdapter(Context context, List<Costume> costumeList, ImageContainer imageContainer) {
//		this.context = context;
//		this.costumeList = costumeList;
//		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	}

	public int getCount() {
		return costumeList.size();
	}

	public Object getItem(int position) {
		return costumeList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		View view;
		if (convertView == null)
			view = inflater.inflate(R.layout.construction_gallery_element, null);
		else
			view = convertView;

		imageView = (ImageView) view.findViewWithTag(context.getString(R.string.constructional_gallery_element_image_view));
		// imageView.setBackgroundResource(android.R.drawable.picture_frame);

		// imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		// imageView.setLayoutParams(new Gallery.LayoutParams(100, 100));

		imageView.setImageBitmap(costumeList.get(position).getBitmap());

		return view;
	}
}
