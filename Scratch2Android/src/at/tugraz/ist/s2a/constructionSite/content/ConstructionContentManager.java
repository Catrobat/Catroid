package at.tugraz.ist.s2a.constructionSite.content;

import android.content.Context;
import at.tugraz.ist.s2a.utils.ImageContainer;

public class ConstructionContentManager extends ContentManager{

	private ImageContainer mImageContainer;
	public ConstructionContentManager(Context context, ImageContainer imageContainer) {
		super(context);
		mImageContainer = imageContainer;
	}
	@Override
	public void loadContent(String file) {
		mImageContainer.init();
		super.loadContent(file);
	}

	
	
	
	
}
