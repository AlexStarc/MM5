package com.teleca.mm5.gallery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter  {
	private Context mContext;

	public ImageAdapter( Context mDataContext ){
		mContext = mDataContext;
	}
	
	public int getCount(){
		return mThumbIds.length;
	}
	
	public int getIndexOfItems(){
		return -1;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		return mThumbIds[position];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) { 
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(60, 60));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
           /// imageView.setPadding(10, 10, 10, 10);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
	}
	
    private Integer[] mThumbIds = {
            R.drawable.image1, R.drawable.image2,
            R.drawable.image4, R.drawable.image5,
            R.drawable.image6, R.drawable.image7,
            R.drawable.image2, R.drawable.image1,
            R.drawable.image4, R.drawable.image5,
            R.drawable.image6, R.drawable.image7,
            R.drawable.image2, R.drawable.image1,
            R.drawable.image4, R.drawable.image5,
            R.drawable.image6, R.drawable.image7
    };

}
