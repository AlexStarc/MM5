package com.sandrstar.android.gallery;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.provider.MediaStore.MediaColumns;
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
    private final Context              mContext;
    private Cursor               contentCursor;
    private GalleryContentItem[] mContentItemsArray;
    private Bitmap mPlaceHolder = null;
    private ThreadPoolExecutor mDecodingThreadPool = null;
    private final ArrayBlockingQueue<Runnable> mDecodingQueue = new ArrayBlockingQueue<Runnable>(SIMULATEOS_DECODING_THREADS_COUNT * 50);
    private DisplayMetrics metrics = null;

    public ImageAdapter(final Context mDataContext, final Cursor contentCursor) {
        this.mContext = mDataContext;
        this.contentCursor = contentCursor;

        if (null != contentCursor) {
            this.mContentItemsArray = new GalleryContentItem[contentCursor.getCount()];
        }

        this.mPlaceHolder = BitmapFactory.decodeResource(mDataContext.getResources(), R.drawable.tumbnail_holder);

        if( ImageAdapter.getZoomThumbnailWidth().equals(0) ) {
            // get display metrics to determine proper resizing
            this.metrics = new DisplayMetrics();
            ((WindowManager)this.mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(this.metrics);

            ImageAdapter.setZoomThumbnailWidth(((Float)(ImageAdapter.ZOOM_THUMBNAIL_WIDTH_DPI * this.metrics.density)).intValue());
            ImageAdapter.setZoomThumbnailHeight(((Float)(ImageAdapter.ZOOM_THUMBNAIL_HEIGHT_DPI * this.metrics.density)).intValue());
        }

        this.mDecodingThreadPool = new ThreadPoolExecutor( ImageAdapter.SIMULATEOS_DECODING_THREADS_COUNT,
                                                      ImageAdapter.SIMULATEOS_DECODING_THREADS_COUNT,
                                                      Long.MAX_VALUE,
                                                      TimeUnit.NANOSECONDS,
                                                      this.mDecodingQueue);
    }

    @Override
    public int getCount() {
        return (null != this.contentCursor ? this.contentCursor.getCount() : 0);
    }

    @Override
    public Object getItem(final int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(final int position) {
        return 0;
    }

    /**
     * @param mRes resources to be used to get the item
     */
    public String getNameItemId(final int position, final Resources mRes) {
        String mNameFile = null;

        try {
            this.contentCursor.moveToPosition(position);
            mNameFile = this.contentCursor.getString(this.contentCursor
                    .getColumnIndex(MediaColumns.DISPLAY_NAME));
        } catch (final Exception e) {
            Log.e(TAG,
                  "getNameItemId(): " + e.getClass() + " thrown "
                  + e.getMessage());
        }

        return mNameFile;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        ImageView imageView = null;

        if (convertView == null) {
            imageView = new ImageView(this.mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ImageAdapter.getZoomThumbnailWidth() / SCALE_FACTOR,
                                                                ImageAdapter.getZoomThumbnailHeight() / SCALE_FACTOR));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView = (ImageView) convertView;
        }

        if (null != this.mContentItemsArray[position]) {
            if( null != this.mContentItemsArray[position].getContentBitmap() ) {
                imageView.setImageBitmap(this.mContentItemsArray[position].getContentBitmap());
            }
        } else {
            // set thumbnail placeholder in place of loading image
            if( null != this.mPlaceHolder) {
                imageView.setImageBitmap(this.mPlaceHolder);
            }

            this.mContentItemsArray[position] = new GalleryContentItem();
            // file name obtained, now provide image loading in separate thread
            this.mDecodingThreadPool.execute( new ContentImageLoader(this.contentCursor,
                                                                position,
                                                                new Handler(this),
                                                                ImageAdapter.getZoomThumbnailWidth(),
                                                                ImageAdapter.getZoomThumbnailHeight(),
                                                                this.metrics.densityDpi,
                                                                false) );
        }

        return imageView;
    }

    @Override
    public boolean handleMessage(final Message loaderMsg) {
        ContentImageLoader itemImageLoader = null;

        if (null != loaderMsg && loaderMsg.obj instanceof ContentImageLoader) {
            try {
                itemImageLoader = (ContentImageLoader) loaderMsg.obj;

                if (null != itemImageLoader) {
                    final Integer nItemId = itemImageLoader.getnItemId();

                    if (null != this.mContentItemsArray && 0 <= nItemId
                            && null != this.mContentItemsArray[nItemId]) {
                        this.mContentItemsArray[nItemId].setContentBitmap(itemImageLoader.getBm());
                    }
                }

                notifyDataSetChanged();
            } catch (final Exception e) {
                Log.e(TAG,
                      "handleMessage(): " + e.getClass() + " thrown "
                      + e.getMessage());
            }
        }

        return true;
    }

    public Cursor getContentCursor() {
        return this.contentCursor;
    }

    public void setContentCursor(final Cursor contentCursor) {
        this.contentCursor = contentCursor;

        if (null != contentCursor) {
            this.mContentItemsArray = new GalleryContentItem[contentCursor.getCount()];
        }

        notifyDataSetChanged();
    }

    public static Integer getZoomThumbnailHeight() {
        return zoomThumbnailHeight;
    }

    public static Integer getZoomThumbnailWidth() {
        return zoomThumbnailWidth;
    }

    public static void setZoomThumbnailHeight(Integer zoomThumbnailHeight) {
        ImageAdapter.zoomThumbnailHeight = zoomThumbnailHeight;
    }

    public static void setZoomThumbnailWidth(Integer zoomThumbnailWidth) {
        ImageAdapter.zoomThumbnailWidth = zoomThumbnailWidth;
    }
}

