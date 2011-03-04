package com.teleca.mm5.gallery;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter  {
	private Context mContext;
	private int mCountDefaultWallpapers;

	public ImageAdapter( Context mDataContext ){
		mContext = mDataContext;
		mCountDefaultWallpapers = mDefaultWallpapers.length;
	}
	
	public int getCount(){
		return mCountDefaultWallpapers;
	}
	
	public int getCountDefaultWallpapers(){
		return mCountDefaultWallpapers;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		return mDefaultWallpapers[position];
	}
	
	public String getNameItemId(int position, Resources mRes){
		String mNameFile = null;
		
		if( position < mCountDefaultWallpapers &&
			mRes != null){
			mNameFile = mRes.getResourceEntryName((int) mDefaultWallpapers[position]);
		}
		
		return mNameFile;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) { 
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(60, 60));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER );
        } else {
            imageView = (ImageView) convertView;
        }
        
        if( position < mCountDefaultWallpapers ){
        	imageView.setImageResource(mDefaultWallpapers[position]);
        }
        return imageView;
	}
	
    private Integer[] mDefaultWallpapers = {
            R.drawable.image1, R.drawable.image2,
            R.drawable.image4, R.drawable.image4,
            R.drawable.image4, R.drawable.image4,
            R.drawable.image2, R.drawable.image1,
            R.drawable.image4, R.drawable.image4,
            R.drawable.image4, R.drawable.image4,
            R.drawable.image2, R.drawable.image1,
            R.drawable.image4, R.drawable.image4,
            R.drawable.image4, R.drawable.image4
    };

}
