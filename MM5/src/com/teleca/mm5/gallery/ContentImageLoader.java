/**
 *
 */
package com.teleca.mm5.gallery;

import java.io.FileInputStream;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

/**
 * @author astarche
 *
 */
public class ContentImageLoader implements Runnable {
    private Cursor imagesCursor;
    private Handler callerHdlr;
    private Bitmap bm;
    private Integer nItemId;
    private Integer nRequiredWidth;
    private Integer nRequiredHeight;
    private final static String TAG = "ContentImageLoader";

    /**
     * Creates an ImageLoader object with the specified parameters.
     * @param contentPath path to the image which should be loaded
     * @param nItemId item id in the list
     * @param callerHdlr handler which will receive message about decoding finish
     * @param nRequiredWidth expected width of out image
     * @param nRequiredHeight expected height of out image
     */
    public ContentImageLoader(Cursor imagesCursor,
                              Integer nItemId,
                              Handler callerHdlr,
                              int nRequiredWidth,
                              int nRequiredHeight) {
        this.imagesCursor = imagesCursor;
        this.callerHdlr = callerHdlr;
        this.nRequiredWidth = nRequiredWidth;
        this.nRequiredHeight = nRequiredHeight;
        this.setnItemId(nItemId);
    }

    @Override
    public void run() {
        try {
            Message callbackMsg = Message.obtain();
            Bitmap srcBitmap = null;
            Integer srcWidth = 0;
            Integer srcHeight = 0;
            String contentPath = null;

            callbackMsg.obj = this;
            callbackMsg.setTarget(callerHdlr);

            if(null != imagesCursor)
            {
                try {
                    imagesCursor.moveToPosition(nItemId);
                    contentPath = imagesCursor.getString(imagesCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                } catch (Exception e) {
                    Log.e(TAG,
                          "run(): " + e.getClass() + " thrown " + e.getMessage());
                }

                if(null != contentPath)
                {
                    srcBitmap = BitmapFactory.decodeStream( new FileInputStream( contentPath ) );
                }
            }

            if( null != srcBitmap )
            {
                srcWidth = srcBitmap.getWidth();
                srcHeight = srcBitmap.getHeight();

                // here we need to compute out width and height properly
                if( srcWidth > this.nRequiredWidth || srcHeight > this.nRequiredHeight )
                {
                    // calculate out image dimensions keep aspect ratio
                    Double ratio = (double)srcHeight / (double)srcWidth;

                    if( this.nRequiredWidth <= srcWidth )
                    {
                        srcWidth = this.nRequiredWidth;
                        srcHeight = (int)(srcWidth * ratio);
                    }

                    if( this.nRequiredHeight <= srcHeight )
                    {
                        srcHeight = this.nRequiredHeight;
                        srcWidth = (int)( srcHeight / ratio );
                    }

                    // verify if some of image dimensions become 0
                    if( srcWidth <= 0 )
                    {
                        srcWidth = 1;
                    }

                    if( srcHeight <= 0 )
                    {
                        srcHeight = 1;
                    }

                    bm = Bitmap.createScaledBitmap(srcBitmap,
                                                   srcWidth,
                                                   srcHeight,
                                                   true);
                } else {
                    bm = Bitmap.createBitmap(srcBitmap);
                }
            }

            callbackMsg.sendToTarget();
        } catch (Exception e) {
            Log.e( TAG, "run(): " + e.getClass() + " thrown " + e.getMessage());
        }
    }

    /**
     * @param bm the bm to set
     */
    public void setBm(Bitmap bm) {
        this.bm = bm;
    }

    /**
     * @return the bm
     */
    public Bitmap getBm() {
        return bm;
    }

    /**
     * @param nItemId the nItemId to set
     */
    public void setnItemId(Integer nItemId) {
        this.nItemId = nItemId;
    }

    /**
     * @return the nItemId
     */
    public Integer getnItemId() {
        return nItemId;
    }

}
