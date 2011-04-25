/**
 *
 */
package com.teleca.mm5.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * @author AlexStarc (sandrstar at hotmail dot com)
 *
 */
public class FullScreenView extends GalleryView<ImageView> implements GalleryViewInterface, Handler.Callback, View.OnClickListener, Animation.AnimationListener {

    private final static String TAG = "FullScreenView";
    private Integer nFocusIndex = 0;
    private Thread mImgLoader = null;
    private static final int SWIPE_MIN_DISTANCE = 60;
    private static final int SWIPE_MAX_OFF_PATH = 350;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private GestureDetector gestureDetector = null;
    View.OnTouchListener gestureListener = null;
    private Animation nextImgAnimation = null;
    private Animation nextImgRemoveAnimation = null;
    private Animation prevImgAnimation = null;
    private Animation prevImgRemoveAnimation = null;
    private Bitmap nextBitmap = null;
    private Boolean bMovingforward = true;
    private DisplayMetrics metrics = null;
    private ProgressBar mProgressBar = null;
    ImageView nextImageView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Bundle extraParameters = getIntent().getExtras();

        nFocusIndex = extraParameters.getInt("com.teleca.mm5.gallery.FocusIndex", 0);
        setContentType(GalleryViewType.GALLERY_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setProgressBarVisibility(true);

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

        // initialize animations for images changing
        nextImgAnimation = AnimationUtils.loadAnimation(this, R.anim.fullscreenview_nextimg);
        nextImgAnimation.setAnimationListener(this);
        nextImgRemoveAnimation = AnimationUtils.loadAnimation(this, R.anim.fullscreenview_nextimg_remove);
        prevImgAnimation = AnimationUtils.loadAnimation(this, R.anim.fullscreenview_previmg);
        prevImgAnimation.setAnimationListener(this);
        prevImgRemoveAnimation = AnimationUtils.loadAnimation(this, R.anim.fullscreenview_previmg_remove);

        mProgressBar = (ProgressBar)findViewById(R.id.progress_large);
        nextImageView = (ImageView)findViewById(R.id.fullscrImageViewNext);
    }

    @Override
    public void finishedWorkExecution(GalleryWorkTaskResult processingResult) {

        // Try to load image from the cursor using the index
        if(null != mImgLoader) {
            mImgLoader.stop();
        }

        super.finishedWorkExecution(processingResult);

        if(processingResult == GalleryWorkTaskResult.GALLERY_RESULT_FINISHED) {
            metrics = new DisplayMetrics();
            ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
            mImgLoader = new Thread( new ContentImageLoader(getContentCursor(),
                                                            nFocusIndex,
                                                            new Handler(this),
                                                            metrics.widthPixels,
                                                            metrics.heightPixels,
                                                            metrics.densityDpi,
                                                            true) );
            mImgLoader.run();
        }
    }

    @Override
    public boolean handleMessage(Message loaderMsg) {
        ContentImageLoader itemImageLoader = null;

        if (null != loaderMsg && loaderMsg.obj instanceof ContentImageLoader) {
            try {
                // put decoded image into main layout view
                ImageView mainView = getMainView();

                /* here we'll create new view similar to main one in order to provide slide animation
                 * and remove original when animation ends */
                mProgressBar.setVisibility(View.GONE);

                if(null != mainView) {
                    itemImageLoader = (ContentImageLoader) loaderMsg.obj;

                    if(null != itemImageLoader) {
                        nextImageView.setVisibility(View.VISIBLE);
                        nextImageView.setImageBitmap(itemImageLoader.getBm());
                        // store new bitmap
                        nextBitmap = itemImageLoader.getBm();
                        // cancel all previous animations if any
                        nextImageView.startAnimation(bMovingforward ? nextImgAnimation : prevImgAnimation);
                        getMainView().startAnimation(bMovingforward ? nextImgRemoveAnimation : prevImgRemoveAnimation);
                    }
                }

                // hide loading progress bar
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
                if(Math.abs(e1.getY() - e2.getY()) <= SWIPE_MAX_OFF_PATH &&
                   // no need to do any processing if animations arei n progress
                   ( !nextImgAnimation.hasStarted() || nextImgAnimation.hasEnded() ) &&
                   ( !prevImgAnimation.hasStarted() || prevImgAnimation.hasEnded() )) {
                    // right to left swipe
                    if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        if( nFocusIndex < (getContentCursor().getCount() - 1) ) {
                            nFocusIndex++;
                        } else {
                            nFocusIndex = 0;
                        }

                        bNeedReload = true;
                        bMovingforward = true;
                    } else if(e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        // Swipe right - increase focus
                        if(null != getContentCursor()) {
                            if( nFocusIndex > 0 ) {
                                nFocusIndex--;
                            } else {
                                nFocusIndex = (getContentCursor().getCount() - 1);
                            }

                            bNeedReload = true;
                            bMovingforward = false;
                        }
                    }

                    if( bNeedReload ) {
                        // Try to load image from the cursor using the index
                        if(null != mImgLoader) {
                            mImgLoader.stop();
                        }

                        // indicate loading
                        mProgressBar.setVisibility(View.VISIBLE);

                        // load next image
                        mImgLoader = new Thread( new ContentImageLoader(getContentCursor(),
                                                                        nFocusIndex,
                                                                        new Handler(FullScreenView.this),
                                                                        metrics.widthPixels,
                                                                        metrics.heightPixels,
                                                                        metrics.densityDpi,
                                                                        true) );
                        mImgLoader.run();
                    }
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

    @Override
    public void onAnimationEnd(Animation animation) {
        // set new image to main view and hide next image view
        getMainView().setImageBitmap(nextBitmap);
        nextBitmap = null;
        // try to clean heap after loaded image
        System.gc();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // Left empty, because don't need handling of repeation

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // Left empty, because don't need start handling
    }

}
