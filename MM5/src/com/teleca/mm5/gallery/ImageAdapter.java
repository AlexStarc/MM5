package com.teleca.mm5.gallery;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter implements Callback {
    private static final String  TAG = "ImageAdapter";
    private static Integer zoomThumbnailWidth = 0;
    private static Integer zoomThumbnailHeight = 0;
    public  static final Integer ZOOM_THUMBNAIL_WIDTH_DPI = 180;
    public  static final Integer ZOOM_THUMBNAIL_HEIGHT_DPI = 180;
    public  static final Integer SCALE_FACTOR = 2;
    private static final Integer SIMULATEOS_DECODING_THREADS_COUNT = 3;
    private Context              mContext;
    private Cursor               contentCursor;
    private GalleryContentItem[] mContentItemsArray;
    private Bitmap mPlaceHolder = null;
    private ThreadPoolExecutor mDecodingThreadPool = null;
    private final ArrayBlockingQueue<Runnable> mDecodingQueue = new ArrayBlockingQueue<Runnable>(SIMULATEOS_DECODING_THREADS_COUNT * 50);

    public ImageAdapter(Context mDataContext, Cursor contentCursor) {
        mContext = mDataContext;
        this.contentCursor = contentCursor;

        if (null != contentCursor) {
            mContentItemsArray = new GalleryContentItem[contentCursor.getCount()];
        }

        mPlaceHolder = BitmapFactory.decodeResource(mDataContext.getResources(), R.drawable.tumbnail_holder);

        if( ImageAdapter.zoomThumbnailWidth.equals(0) ) {
            // get display metrics to determine proper resizing
            DisplayMetrics metrics = new DisplayMetrics();
            ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

            ImageAdapter.zoomThumbnailWidth = ((Float)(ImageAdapter.ZOOM_THUMBNAIL_WIDTH_DPI * metrics.density)).intValue();
            ImageAdapter.zoomThumbnailHeight = ((Float)(ImageAdapter.ZOOM_THUMBNAIL_HEIGHT_DPI * metrics.density)).intValue();
        }

        mDecodingThreadPool = new ThreadPoolExecutor( ImageAdapter.SIMULATEOS_DECODING_THREADS_COUNT,
                                                      ImageAdapter.SIMULATEOS_DECODING_THREADS_COUNT,
                                                      Long.MAX_VALUE,
                                                      TimeUnit.NANOSECONDS,
                                                      mDecodingQueue);
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
            imageView.setLayoutParams(new GridView.LayoutParams(ImageAdapter.zoomThumbnailWidth / SCALE_FACTOR,
                                                                ImageAdapter.zoomThumbnailHeight / SCALE_FACTOR));
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
            // set thumbnail placeholder in place of loading image
            if( null != mPlaceHolder)
            {
                imageView.setImageBitmap(mPlaceHolder);
            }

            mContentItemsArray[position] = new GalleryContentItem();
            // file name obtained, now provide image loading in separate thread
            mDecodingThreadPool.execute( new ContentImageLoader(contentCursor,
                                                                position,
                                                                new Handler(this),
                                                                ImageAdapter.zoomThumbnailWidth,
                                                                ImageAdapter.zoomThumbnailHeight) );
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

    public static Integer getZoomThumbnailHeight() {
        return zoomThumbnailHeight;
    }

    public static Integer getZoomThumbnailWidth() {
        return zoomThumbnailWidth;
    }
}

