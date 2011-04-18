/**
 *
 */
package com.teleca.mm5.gallery;

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
    private BitmapFactory.Options decodeOpt = null;

    /**
     * Creates an ImageLoader object with the specified parameters.
     *
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
                              int nRequiredHeight,
                              int densityDpi,
                              boolean bKeepQuality) {
        this.imagesCursor = imagesCursor;
        this.callerHdlr = callerHdlr;
        this.nRequiredWidth = nRequiredWidth;
        this.nRequiredHeight = nRequiredHeight;
        this.setnItemId(nItemId);

        decodeOpt = new BitmapFactory.Options();

        if( nRequiredWidth > 0 && nRequiredHeight > 0 ) {
            if(!bKeepQuality) {
                decodeOpt.inSampleSize = 6;
            } else {
                decodeOpt.inSampleSize = 4;
            }

            // use 72 DPI as common for displays
            decodeOpt.inDensity = 72;
            decodeOpt.inPurgeable = true;
            decodeOpt.inTargetDensity = 72;
            decodeOpt.inScaled = true;
            decodeOpt.inInputShareable = true;
        }
    }

    @Override
    public void run() {
        try {
            Message callbackMsg = Message.obtain();
            Bitmap srcBitmap = null;
            Integer srcWidth = 0;
            Integer srcHeight = 0;
            String contentPath = null;
            Integer nSampleRatio = 0;

            callbackMsg.obj = this;
            callbackMsg.setTarget(callerHdlr);

            if(null != imagesCursor) {
                try {
                    imagesCursor.moveToPosition(nItemId);
                    contentPath = imagesCursor.getString(imagesCursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    if(null != contentPath) {
                        // try to make GC every time before we're decoding new image
                        System.gc();
                        decodeOpt.inJustDecodeBounds = true;
                        srcBitmap = BitmapFactory.decodeFile( contentPath,
                                                              decodeOpt );
                        decodeOpt.inJustDecodeBounds = false;
                    }
                } catch (Exception e) {
                    Log.e(TAG,
                          "run(): " + e.getClass() + " thrown " + e.getMessage());
                }
            }

            srcWidth = decodeOpt.outWidth;
            srcHeight = decodeOpt.outHeight;

            /* need to determine if original width and height lower
             *  than requested and decode small images in better quality
             */
            if(srcWidth <= this.nRequiredWidth &&
               srcHeight <= this.nRequiredHeight &&
               decodeOpt.inSampleSize > 2) {
                nSampleRatio = ( this.nRequiredWidth * this.nRequiredHeight ) / ( srcWidth * srcHeight );

                if(nSampleRatio >= 2) {
                    decodeOpt.inSampleSize = 2;
                } else {
                    decodeOpt.inSampleSize = 4;
                }
            } else {
                nSampleRatio = ( srcWidth * srcHeight ) / ( this.nRequiredWidth * this.nRequiredHeight );

                if( nSampleRatio <= 2 ) {
                    decodeOpt.inSampleSize = 6;
                } else {
                    decodeOpt.inSampleSize = 8;
                }
            }

            try {
                System.gc();
                srcBitmap = BitmapFactory.decodeFile( contentPath,
                                                      decodeOpt );
            } catch (Exception e) {
                Log.e(TAG,
                      "run(): " + e.getClass() + " thrown " + e.getMessage());
            }

            /* here we need to compute out width and height properly.
             * Treat <= values as invalid and don't resize to it.
             */
            if( ( this.nRequiredWidth > 0 && this.nRequiredHeight > 0 ) &&
                ( srcWidth > this.nRequiredWidth || srcHeight > this.nRequiredHeight ) ) {
                // calculate out image dimensions keep aspect ratio
                Double ratio = (double)srcHeight / (double)srcWidth;

                if( this.nRequiredWidth <= srcWidth ) {
                    srcWidth = this.nRequiredWidth;
                    srcHeight = (int)(srcWidth * ratio);
                }

                if( this.nRequiredHeight <= srcHeight ) {
                    srcHeight = this.nRequiredHeight;
                    srcWidth = (int)( srcHeight / ratio );
                }

                // verify if some of image dimensions become 0
                if( srcWidth <= 0 ) {
                    srcWidth = 1;
                }

                if( srcHeight <= 0 ) {
                    srcHeight = 1;
                }

                bm = Bitmap.createScaledBitmap(srcBitmap,
                                               srcWidth,
                                               srcHeight,
                                               false);
                srcBitmap.recycle();
                srcBitmap = null;
            } else {
                bm = srcBitmap;
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
