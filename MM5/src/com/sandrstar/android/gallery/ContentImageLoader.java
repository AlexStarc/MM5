/**
 *
 */
package com.sandrstar.android.gallery;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

/**
 * @author astarche
 *
 */
public class ContentImageLoader implements Runnable {
    private final Cursor imagesCursor;
    private final Handler callerHdlr;
    private Bitmap bm;
    private Integer nItemId;
    private final Integer nRequiredWidth;
    private final Integer nRequiredHeight;
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
    public ContentImageLoader(final Cursor imagesCursor,
                              final Integer nItemId,
                              final Handler callerHdlr,
                              final int nRequiredWidth,
                              final int nRequiredHeight,
                              @SuppressWarnings("unused") final int densityDpi,
                              final boolean bKeepQuality) {
        this.imagesCursor = imagesCursor;
        this.callerHdlr = callerHdlr;
        this.nRequiredWidth = nRequiredWidth;
        this.nRequiredHeight = nRequiredHeight;
        setnItemId(nItemId);

        this.decodeOpt = new BitmapFactory.Options();

        if( nRequiredWidth > 0 && nRequiredHeight > 0 ) {
            if(!bKeepQuality) {
                this.decodeOpt.inSampleSize = 6;
            } else {
                this.decodeOpt.inSampleSize = 4;
            }

            // use 72 DPI as common for displays
            this.decodeOpt.inDensity = 72;
            this.decodeOpt.inPurgeable = true;
            this.decodeOpt.inTargetDensity = 72;
            this.decodeOpt.inScaled = true;
            this.decodeOpt.inInputShareable = true;
        }
    }

    @Override
    public void run() {
        try {
            final Message callbackMsg = Message.obtain();
            Bitmap srcBitmap = null;
            Integer srcWidth = 0;
            Integer srcHeight = 0;
            String contentPath = null;
            Integer nSampleRatio = 0;

            callbackMsg.obj = this;
            callbackMsg.setTarget(this.callerHdlr);

            if(null != this.imagesCursor) {
                try {
                    this.imagesCursor.moveToPosition(this.nItemId);
                    contentPath = this.imagesCursor.getString(this.imagesCursor.getColumnIndex(MediaColumns.DATA));

                    if(null != contentPath) {
                        // try to make GC every time before we're decoding new image
                        this.decodeOpt.inJustDecodeBounds = true;
                        srcBitmap = BitmapFactory.decodeFile( contentPath,
                                                              this.decodeOpt );
                        this.decodeOpt.inJustDecodeBounds = false;
                    }
                } catch (final Exception e) {
                    Log.e(TAG,
                          "run(): " + e.getClass() + " thrown " + e.getMessage());
                }
            }

            srcWidth = this.decodeOpt.outWidth;
            srcHeight = this.decodeOpt.outHeight;

            /* need to determine if original width and height lower
             *  than requested and decode small images in better quality
             */
            if(srcWidth <= this.nRequiredWidth &&
               srcHeight <= this.nRequiredHeight &&
               this.decodeOpt.inSampleSize > 2) {
                nSampleRatio = ( this.nRequiredWidth * this.nRequiredHeight ) / ( srcWidth * srcHeight );

                if(nSampleRatio >= 2) {
                    this.decodeOpt.inSampleSize = 2;
                } else {
                    this.decodeOpt.inSampleSize = 4;
                }
            } else {
                nSampleRatio = ( srcWidth * srcHeight ) / ( this.nRequiredWidth * this.nRequiredHeight );

                if( nSampleRatio <= 2 ) {
                    this.decodeOpt.inSampleSize = 6;
                } else {
                    this.decodeOpt.inSampleSize = 8;
                }
            }

            try {
                srcBitmap = BitmapFactory.decodeFile( contentPath,
                                                      this.decodeOpt );
            } catch (final Exception e) {
                Log.e(TAG,
                      "run(): " + e.getClass() + " thrown " + e.getMessage());
            }

            /* here we need to compute out width and height properly.
             * Treat <= values as invalid and don't resize to it.
             */
            if( ( this.nRequiredWidth > 0 && this.nRequiredHeight > 0 ) &&
                ( srcWidth > this.nRequiredWidth || srcHeight > this.nRequiredHeight ) ) {
                // calculate out image dimensions keep aspect ratio
                final Double ratio = (double)srcHeight / (double)srcWidth;

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

                this.bm = Bitmap.createScaledBitmap(srcBitmap,
                                                    srcWidth,
                                                    srcHeight,
                                                    false);
                if(null != srcBitmap) {
                    srcBitmap.recycle();
                }

                srcBitmap = null;
            } else {
                this.bm = srcBitmap;
            }

            callbackMsg.sendToTarget();
        } catch (final Exception e) {
            Log.e( TAG, "run(): " + e.getClass() + " thrown " + e.getMessage());
        }
    }

    /**
     * @param bm the bm to set
     */
    public void setBm(final Bitmap bm) {
        this.bm = bm;
    }

    /**
     * @return the bm
     */
    public Bitmap getBm() {
        return this.bm;
    }

    /**
     * @param nItemId the nItemId to set
     */
    public void setnItemId(final Integer nItemId) {
        this.nItemId = nItemId;
    }

    /**
     * @return the nItemId
     */
    public Integer getnItemId() {
        return this.nItemId;
    }

}
