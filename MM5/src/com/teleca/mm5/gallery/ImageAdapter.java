package com.teleca.mm5.gallery;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private static final Integer ZOOM_THUMBNAIL_WIDTH = 150;
    private static final Integer ZOOM_THUMBNAIL_HEIGHT = 150;
    private Context              mContext;
    private Cursor               contentCursor;
    private GalleryContentItem[] mContentItemsArray;
    private Bitmap mPlaceHolder = null;

    public ImageAdapter(Context mDataContext, Cursor contentCursor) {
        mContext = mDataContext;
        this.contentCursor = contentCursor;

        if (null != contentCursor) {
            mContentItemsArray = new GalleryContentItem[contentCursor.getCount()];
        }

        mPlaceHolder = BitmapFactory.decodeResource(mDataContext.getResources(), R.drawable.tumbnail_holder);
    }

    @Override
    public int getCount() {
        return (null != contentCursor ? contentCursor.getCount() : 0);
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public String getNameItemId(int position, Resources mRes) {
        String mNameFile = null;

        try {
            contentCursor.moveToPosition(position);
            mNameFile = contentCursor.getString(contentCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
        } catch (Exception e) {
            Log.e(TAG,
                  "getNameItemId(): " + e.getClass() + " thrown "
                  + e.getMessage());
        }

        return mNameFile;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = null;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(60, 60));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView = (ImageView) convertView;
        }

        if (null != mContentItemsArray[position]) {
            if( null != mContentItemsArray[position].getContentBitmap() )
            {
                imageView.setImageBitmap(mContentItemsArray[position].getContentBitmap());
            }
        } else {
            @SuppressWarnings("unused")
            ContentImageLoader itemImageLoader = null;

            // set thumbnail placeholder in place of loading image
            if( null != mPlaceHolder)
            {
                imageView.setImageBitmap(mPlaceHolder);
            }

            mContentItemsArray[position] = new GalleryContentItem(null,
                                                                  null);
            // file name obtained, now provide image loading in separate thread
            itemImageLoader = new ContentImageLoader(contentCursor,
                                                     position,
                                                     new Handler(this),
                                                     ZOOM_THUMBNAIL_WIDTH,
                                                     ZOOM_THUMBNAIL_HEIGHT);
        }

        return imageView;
    }

    @Override
    public boolean handleMessage(Message loaderMsg) {
        ContentImageLoader itemImageLoader = null;

        if (null != loaderMsg && loaderMsg.obj instanceof ContentImageLoader) {
            try {
                itemImageLoader = (ContentImageLoader) loaderMsg.obj;

                if (null != itemImageLoader) {
                    Integer nItemId = itemImageLoader.getnItemId();

                    if (null != mContentItemsArray && 0 <= nItemId
                            && null != mContentItemsArray[nItemId]) {
                        mContentItemsArray[nItemId].setContentBitmap(itemImageLoader.getBm());
                    }
                }

                this.notifyDataSetChanged();
            } catch (Exception e) {
                Log.e(TAG,
                      "handleMessage(): " + e.getClass() + " thrown "
                      + e.getMessage());
            }
        }

        return true;
    }

    public Cursor getContentCursor() {
        return contentCursor;
    }

    public void setContentCursor(Cursor contentCursor) {
        this.contentCursor = contentCursor;

        if (null != contentCursor) {
            mContentItemsArray = new GalleryContentItem[contentCursor.getCount()];
        }

        this.notifyDataSetChanged();
    }
}

