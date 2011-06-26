/**
 *
 */
package com.sandrstar.android.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * @author AlexStarc (sandrstar at hotmail dot com)
 *
 */
public class FullScreenView extends GalleryView<ImageView> implements Handler.Callback, View.OnClickListener, Animation.AnimationListener {

    private final static String TAG = "FullScreenView";
    Integer nFocusIndex = 0;
    Thread mImgLoader = null;
    private static final int SWIPE_MIN_DISTANCE = 60;
    private static final int SWIPE_MAX_OFF_PATH = 350;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    GestureDetector gestureDetector = null;
    View.OnTouchListener gestureListener = null;
    Animation nextImgAnimation = null;
    private Animation nextImgRemoveAnimation = null;
    Animation prevImgAnimation = null;
    private Animation prevImgRemoveAnimation = null;
    private Bitmap nextBitmap = null;
    Boolean bMovingforward = true;
    DisplayMetrics metrics = null;
    ProgressBar mProgressBar = null;
    ImageView nextImageView = null;

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        final Bundle extraParameters = getIntent().getExtras();

        this.nFocusIndex = extraParameters.getInt("com.teleca.mm5.gallery.FocusIndex", 0);
        setContentType(GalleryViewType.GALLERY_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setProgressBarVisibility(true);

        // Gesture detections
        this.gestureDetector = new GestureDetector(new FullScreenGestureDetector());
        this.gestureListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                if (FullScreenView.this.gestureDetector.onTouchEvent(event)) {
                    return true;
                }

                return false;
            }
        };

        getMainView().setOnClickListener(this);
        getMainView().setOnTouchListener(this.gestureListener);

        // initialize animations for images changing
        this.nextImgAnimation = AnimationUtils.loadAnimation(this, R.anim.fullscreenview_nextimg);
        this.nextImgAnimation.setAnimationListener(this);
        this.nextImgRemoveAnimation = AnimationUtils.loadAnimation(this, R.anim.fullscreenview_nextimg_remove);
        this.prevImgAnimation = AnimationUtils.loadAnimation(this, R.anim.fullscreenview_previmg);
        this.prevImgAnimation.setAnimationListener(this);
        this.prevImgRemoveAnimation = AnimationUtils.loadAnimation(this, R.anim.fullscreenview_previmg_remove);

        this.mProgressBar = (ProgressBar)findViewById(R.id.progress_large);
        this.nextImageView = (ImageView)findViewById(R.id.fullscrImageViewNext);
    }

    @Override
    public void finishedWorkExecution(final GalleryWorkTaskResult processingResult) {

        // Try to load image from the cursor using the index
        if(null != this.mImgLoader) {
            this.mImgLoader.stop();
        }

        super.finishedWorkExecution(processingResult);

        if(processingResult == GalleryWorkTaskResult.GALLERY_RESULT_FINISHED) {
            this.metrics = new DisplayMetrics();
            ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(this.metrics);
            this.mImgLoader = new Thread( new ContentImageLoader(getContentCursor(),
                                                            this.nFocusIndex,
                                                            new Handler(this),
                                                            this.metrics.widthPixels,
                                                            this.metrics.heightPixels,
                                                            this.metrics.densityDpi,
                                                            true) );
            this.mImgLoader.start();
        }
    }

    @Override
    public boolean handleMessage(final Message loaderMsg) {
        ContentImageLoader itemImageLoader = null;

        if (null != loaderMsg && loaderMsg.obj instanceof ContentImageLoader) {
            try {
                // put decoded image into main layout view
                final ImageView mainView = getMainView();

                /* here we'll create new view similar to main one in order to provide slide animation
                 * and remove original when animation ends */
                this.mProgressBar.setVisibility(View.GONE);

                if(null != mainView) {
                    itemImageLoader = (ContentImageLoader) loaderMsg.obj;

                    if(null != itemImageLoader) {
                        this.nextImageView.setVisibility(View.VISIBLE);
                        this.nextImageView.setImageBitmap(itemImageLoader.getBm());
                        // store new bitmap
                        this.nextBitmap = itemImageLoader.getBm();
                        // cancel all previous animations if any
                        this.nextImageView.startAnimation(this.bMovingforward ? this.nextImgAnimation : this.prevImgAnimation);
                        getMainView().startAnimation(this.bMovingforward ? this.nextImgRemoveAnimation : this.prevImgRemoveAnimation);
                    }
                }

                // hide loading progress bar
            } catch (final Exception e) {
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

        if(null != this.mImgLoader) {
            this.mImgLoader.stop();
        }
    }

    class FullScreenGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
            boolean bNeedReload = false;

            try {
                if(Math.abs(e1.getY() - e2.getY()) <= SWIPE_MAX_OFF_PATH &&
                   // no need to do any processing if animations arei n progress
                   ( !FullScreenView.this.nextImgAnimation.hasStarted() || FullScreenView.this.nextImgAnimation.hasEnded() ) &&
                   ( !FullScreenView.this.prevImgAnimation.hasStarted() || FullScreenView.this.prevImgAnimation.hasEnded() )) {
                    // right to left swipe
                    if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        if( FullScreenView.this.nFocusIndex < (getContentCursor().getCount() - 1) ) {
                            FullScreenView.this.nFocusIndex++;
                        } else {
                            FullScreenView.this.nFocusIndex = 0;
                        }

                        bNeedReload = true;
                        FullScreenView.this.bMovingforward = true;
                    } else if(e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        // Swipe right - increase focus
                        if(null != getContentCursor()) {
                            if( FullScreenView.this.nFocusIndex > 0 ) {
                                FullScreenView.this.nFocusIndex--;
                            } else {
                                FullScreenView.this.nFocusIndex = (getContentCursor().getCount() - 1);
                            }

                            bNeedReload = true;
                            FullScreenView.this.bMovingforward = false;
                        }
                    }

                    if( bNeedReload ) {
                        // Try to load image from the cursor using the index
                        if(null != FullScreenView.this.mImgLoader) {
                            FullScreenView.this.mImgLoader.stop();
                        }

                        // indicate loading
                        FullScreenView.this.mProgressBar.setVisibility(View.VISIBLE);

                        // load next image
                        FullScreenView.this.mImgLoader = new Thread( new ContentImageLoader(getContentCursor(),
                                                                        FullScreenView.this.nFocusIndex,
                                                                        new Handler(FullScreenView.this),
                                                                        FullScreenView.this.metrics.widthPixels,
                                                                        FullScreenView.this.metrics.heightPixels,
                                                                        FullScreenView.this.metrics.densityDpi,
                                                                        true) );
                        FullScreenView.this.mImgLoader.start();
                    }
                }
            } catch (final Exception e) {
                Log.e(TAG,
                      "handleMessage(): " + e.getClass() + " thrown "
                      + e.getMessage());
            }

            return false;
        }

    }

    @Override
    public void onClick(final View v) {
        // Left empty by intension to allow gestures handling
    }

    @Override
    public void onAnimationEnd(final Animation animation) {
        // set new image to main view and hide next image view
        getMainView().setImageBitmap(this.nextBitmap);
        this.nextBitmap = null;
    }

    @Override
    public void onAnimationRepeat(final Animation animation) {
        // Left empty, because don't need handling of repeation

    }

    @Override
    public void onAnimationStart(final Animation animation) {
        // Left empty, because don't need start handling
    }

}
