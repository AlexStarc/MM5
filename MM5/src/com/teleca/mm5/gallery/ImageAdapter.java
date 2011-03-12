package com.teleca.mm5.gallery;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter implements Callback {
    private static final String  TAG = "ImageAdapter";
    private Context              mContext;
    private int                  mCountDefaultWallpapers;
    private Cursor               contentCursor;
    private GalleryContentItem[] mContentItemsArray;

    public ImageAdapter(Context mDataContext, Cursor contentCursor) {
        mContext = mDataContext;
        mCountDefaultWallpapers = mDefaultWallpapers.length;
        this.contentCursor = contentCursor;

        if (null != contentCursor) {
            mContentItemsArray = new GalleryContentItem[contentCursor.getCount()];
        }
    }

    @Override
    public int getCount() {
        return (contentCursor.getCount() + getCountDefaultWallpapers());
    }

    public int getCountDefaultWallpapers() {
        return mCountDefaultWallpapers;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (position < mCountDefaultWallpapers) {
            return mDefaultWallpapers[position];
        } else {
            return 0;
        }
    }

    public String getNameItemId(int position, Resources mRes) {
        String mNameFile = null;

        if (position < mCountDefaultWallpapers && mRes != null) {
            mNameFile = mRes.getResourceEntryName(mDefaultWallpapers[position]);
        } else {
            try {
                contentCursor.moveToPosition(position - mCountDefaultWallpapers);
                mNameFile = contentCursor.getString(contentCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            } catch (Exception e) {
                Log.e(TAG,
                      "getNameItemId(): " + e.getClass() + " thrown "
                      + e.getMessage());
            }
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

        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (position < mCountDefaultWallpapers) {
            imageView.setImageResource(mDefaultWallpapers[position]);
        } else {
            if (null != mContentItemsArray[position - mCountDefaultWallpapers]) {
                imageView.setImageBitmap(mContentItemsArray[position - mCountDefaultWallpapers].getContentBitmap());
            } else {
                String fileName = null;
                ContentImageLoader itemImageLoader = null;

                try {
                    contentCursor.moveToPosition(position - mCountDefaultWallpapers);
                    fileName = contentCursor.getString(contentCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                } catch (Exception e) {
                    Log.e(TAG,
                          "getView(): " + e.getClass() + " thrown " + e.getMessage());
                }

                mContentItemsArray[position - mCountDefaultWallpapers] = new GalleryContentItem(null,
                                                                                                fileName);
                // file name obtained, now provide image loading in separate thread
                itemImageLoader = new ContentImageLoader(imageView,
                                                         fileName,
                                                         parent,
                                                         position - mCountDefaultWallpapers,
                                                         new Handler(this));

                itemImageLoader.run();
            }
        }
        return imageView;
    }

    private Integer[] mDefaultWallpapers = { R.drawable.image1,
            R.drawable.image2, R.drawable.image4, R.drawable.image4,
            R.drawable.image4, R.drawable.image4, R.drawable.image2,
            R.drawable.image1, R.drawable.image4, R.drawable.image4,
            R.drawable.image4, R.drawable.image4, R.drawable.image2,
            R.drawable.image1, R.drawable.image4, R.drawable.image4,
            R.drawable.image4, R.drawable.image4 };

    @Override
    public boolean handleMessage(Message loaderMsg) {
        ContentImageLoader itemImageLoader = null;

        if (null != loaderMsg && loaderMsg.obj instanceof ContentImageLoader) {
            try {
                itemImageLoader = (ContentImageLoader) loaderMsg.obj;

                if (null != itemImageLoader) {
                    ImageView iv = itemImageLoader.getIv();
                    View parentView = itemImageLoader.getParent();
                    Integer nItemId = itemImageLoader.getnItemId();

                    if (null != mContentItemsArray && 0 <= nItemId
                            && null != mContentItemsArray[nItemId]) {
                        mContentItemsArray[nItemId].setContentBitmap(itemImageLoader.getBm());

                        if (null != iv) {
                            iv.setImageBitmap(mContentItemsArray[nItemId].getContentBitmap());

                            if (null != parentView) {
                                parentView.postInvalidate();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG,
                      "handleMessage(): " + e.getClass() + " thrown "
                      + e.getMessage());
            }
        }
        return true;
    }

}
