/**
 *
 */
package com.teleca.mm5.gallery;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * @author astarche
 *
 */
public class FullScreenView extends GalleryView<ImageView> implements GalleryViewInterface, Handler.Callback, View.OnClickListener {

    private final static String TAG = "FullScreenView";
    private Integer nFocusIndex = 0;
    private Thread mImgLoader = null;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector = null;
    View.OnTouchListener gestureListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Bundle extraParameters = getIntent().getExtras();

        nFocusIndex = extraParameters.getInt("com.teleca.mm5.gallery.FocusIndex", 0);
        setContentType(GalleryViewType.GALLERY_FULLSCREEN);
        super.onCreate(savedInstanceState);

        // Gesture detections
        gestureDetector = new GestureDetector(new FullScreenGestureDetector());
        gestureListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };

        getMainView().setOnClickListener(this);
        getMainView().setOnTouchListener(gestureListener);

    }

    @Override
    public void finishedWorkExecution(GalleryWorkTaskResult processingResult) {
        // Try to load image from the cursor using the index
        if(null != mImgLoader) {
            mImgLoader.stop();
        }

        mImgLoader = new Thread( new ContentImageLoader(getContentCursor(),
                                                        nFocusIndex,
                                                        new Handler(this),
                                                        -1,
                                                        -1) );
        mImgLoader.run();
    }

    @Override
    public boolean handleMessage(Message loaderMsg) {
        ContentImageLoader itemImageLoader = null;

        if (null != loaderMsg && loaderMsg.obj instanceof ContentImageLoader) {
            try {
                // put decoded image into main layout view
                ImageView mainView = getMainView();

                if(null != mainView) {
                    itemImageLoader = (ContentImageLoader) loaderMsg.obj;

                    if(null != itemImageLoader) {
                        mainView.setImageBitmap(itemImageLoader.getBm());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(null != mImgLoader) {
            mImgLoader.stop();
        }
    }

    class FullScreenGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean bNeedReload = false;

            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                    return false;
                }
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if( nFocusIndex > 0 ) {
                        nFocusIndex--;
                        bNeedReload = true;
                    }
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    // Swipe right - increase focus
                    if(null != getContentCursor() &&
                       nFocusIndex < (getContentCursor().getCount() - 1) ) {
                        nFocusIndex++;
                        bNeedReload = true;
                    }
                }

                if( bNeedReload ) {
                    // Try to load image from the cursor using the index
                    if(null != mImgLoader) {
                        mImgLoader.stop();
                    }

                    mImgLoader = new Thread( new ContentImageLoader(getContentCursor(),
                                                                    nFocusIndex,
                                                                    new Handler(FullScreenView.this),
                                                                    -1,
                                                                    -1) );
                    mImgLoader.run();
                }
            } catch (Exception e) {
                Log.e(TAG,
                      "handleMessage(): " + e.getClass() + " thrown "
                      + e.getMessage());
            }
            return false;
        }

    }

    @Override
    public void onClick(View v) {
        // Left empty by intension to allow gestures handling
    }

}
