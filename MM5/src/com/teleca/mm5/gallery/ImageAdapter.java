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
    private GalleryContentItem[] mGalleryContentItem;

    public ImageAdapter( Context mDataContext, GalleryContentItem[] GalleryContentItem ){
        mContext = mDataContext;
        mCountDefaultWallpapers = mDefaultWallpapers.length;
        mGalleryContentItem = GalleryContentItem;
    }

    @Override
    public int getCount(){
        return mGalleryContentItem.length + getCountDefaultWallpapers();
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
        if( position < mCountDefaultWallpapers ){
            return mDefaultWallpapers[position];
        }
        else {
            return 0;
        }
    }

    public String getNameItemId(int position, Resources mRes){
        String mNameFile = null;

        if( position < mCountDefaultWallpapers &&
                mRes != null){
            mNameFile = mRes.getResourceEntryName(mDefaultWallpapers[position]);
        } else {
            mNameFile = mGalleryContentItem[position-mCountDefaultWallpapers].getContentName();
        }

        return mNameFile;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(60, 60));
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER );

        if( position < mCountDefaultWallpapers ){
            imageView.setImageResource(mDefaultWallpapers[position]);
        } else {
            imageView.setImageBitmap(mGalleryContentItem[position - mCountDefaultWallpapers].getContentBitmap());
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
