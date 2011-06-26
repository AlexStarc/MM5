package com.sandrstar.android.gallery;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ThumbnailsView extends GalleryView<GridView> implements Handler.Callback {
    private ImageAdapter mImageAdapter;
    private static final String TAG = "ThumbnailsView";
    private Animation mSelectionAnimation = null;
    Integer nFocusIndex = -1;
    GalleryOptionsBar optionsBar = null;
    ImageView mResizeImage = null;
    private TextView  mFileNameView = null;
    private TextView  mCounterView = null;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        setContentType(GalleryViewType.GALLERY_THUMBNAILS);
        super.onCreate(savedInstanceState);

        this.mImageAdapter = new ImageAdapter(this, getContentCursor());

        try {
            getMainView().setAdapter(this.mImageAdapter);
            getMainView().setSelector(R.drawable.thumbnail_selector);

            this.mSelectionAnimation = AnimationUtils.loadAnimation(this, R.anim.thumbnail_selection);

            // set empty tests for text view - name and counter
            this.mFileNameView = (TextView)findViewById(R.id.thumbnailItemDisplayName);
            this.mFileNameView.setText( " " );

            this.mCounterView = (TextView)findViewById(R.id.thumbnailCounter);
            this.mCounterView.setText( " " );
        } catch (final Exception e) {
            Log.e(TAG, "onCreate(): " + e.getClass() + " thrown " + e.getMessage());
        }

        // setup options bar
        this.optionsBar = new GalleryOptionsBar(this, R.id.thimbnail_optionbar);
        this.optionsBar.setOptionsHandler(this);

        this.mResizeImage = (ImageView)findViewById(R.id.imageView1);
    }

    void update(final int mSelectItemId, final View selectedView){
        ImageView imageView = null;
        final Resources mRes = getResources();

        this.mFileNameView.setText( this.mImageAdapter.getNameItemId(mSelectItemId, mRes));

        this.mCounterView.setText(String.format("%d/%d", mSelectItemId + 1, this.mImageAdapter.getCount()));

        if( null != selectedView && selectedView instanceof ImageView) {
            imageView = (ImageView)selectedView;
        }

        if( null != imageView ) {
            this.mResizeImage = (ImageView) this.mImageAdapter.getView(mSelectItemId,
                                                             this.mResizeImage,
                                                             null);
            final ViewGroup.LayoutParams zoomedImageLayoutParams = this.mResizeImage.getLayoutParams();

            if( zoomedImageLayoutParams instanceof ViewGroup.MarginLayoutParams ) {
                final Integer thumbnailX = getMainView().getLeft() + imageView.getLeft() - ( ImageAdapter.getZoomThumbnailWidth() - imageView.getWidth() ) / 2;
                final Integer thumbnailY = getMainView().getTop() + imageView.getTop() - ( ImageAdapter.getZoomThumbnailHeight() - imageView.getHeight() ) / 2;

                ((ViewGroup.MarginLayoutParams)zoomedImageLayoutParams).setMargins(thumbnailX,
                                                                                   thumbnailY,
                                                                                   0,
                                                                                   0);
                zoomedImageLayoutParams.height = ImageAdapter.getZoomThumbnailWidth();
                zoomedImageLayoutParams.width = ImageAdapter.getZoomThumbnailHeight();
                this.mResizeImage.setLayoutParams(zoomedImageLayoutParams);
            }

            if(null != this.mSelectionAnimation) {
                this.mSelectionAnimation.cancel();
                this.mResizeImage.startAnimation(this.mSelectionAnimation);
            }

            // if focus already presented - there's no need to show up options bar
            if( this.nFocusIndex < 0 ) {
                // show options bar
                this.optionsBar.showOptionBar();
            }
        }
    }

    @Override
    public void finishedWorkExecution(final GalleryWorkTaskResult processingResult) {
        super.finishedWorkExecution(processingResult);

        if( GalleryWorkTaskResult.GALLERY_RESULT_FINISHED == processingResult ) {

            this.mImageAdapter.setContentCursor(getContentCursor());

            getMainView().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(final AdapterView<?> arg0, final View view,
                                           final int mSelectItemId, final long arg3) {
                    update(mSelectItemId, view);
                }

                @Override
                public void onNothingSelected(final AdapterView<?> arg0) {
                    // nothing to do here
                }
            });

            getMainView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> arg0, final View view,
                                        final int mSelectItemId, final long arg3) {
                    // if the same image has been selected again, open fullscreen view
                    if( ThumbnailsView.this.nFocusIndex == mSelectItemId ) {
                        final Intent launchFullScreen = new Intent(getGalleryViewContext(), FullScreenView.class);
                        startActivity(launchFullScreen.putExtra("com.teleca.mm5.gallery.FocusIndex", ThumbnailsView.this.nFocusIndex));
                    } else {
                        update(mSelectItemId, view);
                        ThumbnailsView.this.nFocusIndex = mSelectItemId;
                    }
                }
            });

            getMainView().setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(final AbsListView view, final int scrollState) {
                    if( null != ThumbnailsView.this.mResizeImage ) {
                        ThumbnailsView.this.mResizeImage.setImageBitmap(null);

                        if(ThumbnailsView.this.nFocusIndex >= 0) {
                            // reset focus index also
                            ThumbnailsView.this.nFocusIndex = -1;

                            // play animation to hide options bar
                            ThumbnailsView.this.optionsBar.hideOptionBar();
                        }
                    }
                }

                @Override
                public void onScroll(final AbsListView view, final int firstVisibleItem,
                                     final int visibleItemCount, final int totalItemCount) {
                    // nothing to do here
                }
            });
        }
    }

    /**
     * This is Option handler
     */
    @Override
    public boolean handleMessage(final Message msg) {
        boolean retVal = false;

        switch(msg.arg1) {
        // VIEW option
        case R.id.viewButton:
            final Intent launchFullScreen = new Intent(getGalleryViewContext(), FullScreenView.class);
            if(this.nFocusIndex >= 0) {
                startActivity( launchFullScreen.putExtra("com.teleca.mm5.gallery.FocusIndex", this.nFocusIndex) );
            } else {
                startActivity( launchFullScreen.putExtra("com.teleca.mm5.gallery.FocusIndex", 0) );
            }
            retVal = true;
            break;

        // DELETE option
        case R.id.deleteButton:
            // TODO: handle delete here
            retVal = true;
            break;

        case R.id.infoButton:
            // launch details with extras
            final Intent launchDetails = new Intent(getGalleryViewContext(), DetailsView.class);

            if(this.nFocusIndex >= 0) {
                launchDetails.putExtra("com.teleca.mm5.gallery.FocusIndex", this.nFocusIndex);
            } else {
                launchDetails.putExtra("com.teleca.mm5.gallery.FocusIndex", 0);
            }

            launchDetails.putExtra("com.teleca.mm5.gallery.ContentType", GalleryViewType.GALLERY_IMAGE_DETAILS.ordinal());

            startActivity(launchDetails);
            retVal = true;
            break;

        default:
            break;
        }

        return retVal;
    }
}

