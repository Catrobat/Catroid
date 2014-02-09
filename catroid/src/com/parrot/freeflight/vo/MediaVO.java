package com.parrot.freeflight.vo;

import android.net.Uri;

public class MediaVO implements Comparable<MediaVO>
{
    private int id;
    private long dateAdded;
    private String path;

    private boolean isVideo;
    private boolean isSelected;
    private String key;
    private Uri uri;

    public boolean isVideo()
    {
        return this.isVideo;
    }

    public void setVideo(boolean isVideo)
    {
        this.isVideo = isVideo;
    }

    public boolean isSelected()
    {
        return this.isSelected;
    }

    public void setSelected(boolean isSelected)
    {
        this.isSelected = isSelected;
    }

    public int getId()
    {
        return this.id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getPath()
    {
        return this.path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }
    
    public void setUri(Uri uri)
    {
        this.uri = uri;
    }
    
    
    public Uri getUri()
    {
        return uri;
    }

    public long getDateAdded()
    {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded)
    {
        this.dateAdded = dateAdded;
    }
    
    public String getKey()
    {
        if (key == null) {
            key = (isVideo?"video_":"") + id;
        }
        
        return key;
    }
    
    public int compareTo(MediaVO another)
    {
        long anotherDate = another.getDateAdded();
        int result = dateAdded < anotherDate? -1 : 1;
        
        if(anotherDate == dateAdded)
        {
            result = 0;
        }
        
        return result;
    }

}
