package org.catrobat.catroid.SocialMediaFeature;

/**
 * Created by manthan on 29/3/16.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.catrobat.catroid.BuildConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by manthan on 29/3/16.
 */

public class FlickrManager {

    private static final String FLICKR_BASE_URL = "https://api.flickr.com/services/rest/?method=";
    private static final String FLICKR_PHOTOS_SEARCH_STRING = "flickr.photos.search";
    private static final String FLICKR_GET_SIZES_STRING = "flickr.photos.getSizes";
    private static final int FLICKR_PHOTOS_SEARCH_ID = 1;
    private static final int FLICKR_GET_SIZES_ID = 2;
    private static final int NUMBER_OF_PHOTOS = 20;

    //API key can be modified here  
    private static final String APIKEY_SEARCH_STRING = "&api_key="+ BuildConfig.FLICKR_API_KEY;

    private static final String TAGS_STRING = "&tags=";
    private static final String PHOTO_ID_STRING = "&photo_id=";
    private static final String FORMAT_STRING = "&format=json";
    public static final int PHOTO_THUMB = 111;
    public static final int PHOTO_LARGE = 222;

    private static String createURL(int methodId, String parameter) {
        String method_type = "";
        String url = null;
        switch (methodId) {
            case FLICKR_PHOTOS_SEARCH_ID:
                method_type = FLICKR_PHOTOS_SEARCH_STRING;
                url = FLICKR_BASE_URL + method_type + APIKEY_SEARCH_STRING + TAGS_STRING + parameter + FORMAT_STRING + "&per_page="+NUMBER_OF_PHOTOS+"&media=photos";
                break;
            case FLICKR_GET_SIZES_ID:
                method_type = FLICKR_GET_SIZES_STRING;
                url = FLICKR_BASE_URL + method_type + PHOTO_ID_STRING + parameter + APIKEY_SEARCH_STRING + FORMAT_STRING;
                break;
        }
        return url;
    }

    public static void getImageURLS(ImageContener imgCon) {
        String url = createURL(FLICKR_GET_SIZES_ID, imgCon.id);
        ByteArrayOutputStream baos = URLConnector.readBytes(url);
        String json = baos.toString();
        try {
            JSONObject root = new JSONObject(json.replace("jsonFlickrApi(", "").replace(")", ""));
            JSONObject sizes = root.getJSONObject("sizes");
            JSONArray size = sizes.getJSONArray("size");
            for (int i = 0; i < size.length(); i++) {
                JSONObject image = size.getJSONObject(i);
                if (image.getString("label").equals("Square")) {
                    imgCon.setThumbURL(image.getString("source"));
                } else if (image.getString("label").equals("Medium")) {
                    imgCon.setLargeURL(image.getString("source"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getImage(ImageContener imgCon) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(imgCon.largeURL);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (Exception e) {
            Log.e("FlickrManager", e.getMessage());
        }
        return bm;
    }

    public static void getThumbnails(ArrayList<ImageContener> imgCon){
        for (int i = 0; i < imgCon.size(); i++)
            new GetThumbnailsThread(imgCon.get(i)).start();
    }

    public static Bitmap getThumbnail(ImageContener imgCon) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(imgCon.thumbURL);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (Exception e) {
            Log.e("FlickrManager", e.getMessage());
        }
        return bm;
    }

    public static class GetThumbnailsThread extends Thread {
        ImageContener imgContener;

        public GetThumbnailsThread( ImageContener imgCon) {
            this.imgContener = imgCon;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            imgContener.thumb = getThumbnail(imgContener);
            if (imgContener.thumb != null) {

            }
        }

    }

    public static ArrayList<ImageContener> searchImagesByTag(Context ctx, String tag) {
        String url = createURL(FLICKR_PHOTOS_SEARCH_ID, tag);
        ArrayList<ImageContener> tmp = new ArrayList<ImageContener>();
        String jsonString = null;
        try {
            if (URLConnector.isOnline(ctx)) {
                ByteArrayOutputStream baos = URLConnector.readBytes(url);
                jsonString = baos.toString();
            }
            try {
                JSONObject root = new JSONObject(jsonString.replace("jsonFlickrApi(", "").replace(")", ""));
                JSONObject photos = root.getJSONObject("photos");
                JSONArray imageJSONArray = photos.getJSONArray("photo");
                for (int i = 0; i < imageJSONArray.length(); i++) {
                    JSONObject item = imageJSONArray.getJSONObject(i);
                    ImageContener imgCon = new ImageContener(item.getString("id"), item.getString("owner"), item.getString("secret"), item.getString("server"),
                            item.getString("farm"));
                    imgCon.position = i;
                    tmp.add(imgCon);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (NullPointerException nue) {
            nue.printStackTrace();
        }

        return tmp;
    }

}
