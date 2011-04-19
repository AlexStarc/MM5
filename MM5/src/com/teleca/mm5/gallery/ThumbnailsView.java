package com.teleca.mm5.gallery;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ThumbnailsView extends GalleryView<GridView> implements GalleryViewInterface {
    private ImageAdapter mImageAdapter;
    private static final String TAG = "ThumbnailsView";
    private Animation mSelectionAnimation = null;
    private Animation mOptionBarShow = null;
    private Animation mOptionBarHide = null;
    private Integer nFocusIndex = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        TextView mTextView = null;

        setContentType(GalleryViewType.GALLERY_THUMBNAILS);
        super.onCreate(savedInstanceState);

        mImageAdapter = new ImageAdapter(this, getContentCursor());

        try {
            getMainView().setAdapter(mImageAdapter);
            getMainView().setSelector(R.drawable.thumbnail_selector);

            // VIEW option handling
            Button mViewButton = (Button)findViewById(R.id.viewButton);
            mViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  Intent launchFullScreen = new Intent(getGalleryViewContext(), FullScreenView.class);
                  if(nFocusIndex >= 0) {
                      startActivity( launchFullScreen.putExtra("com.teleca.mm5.gallery.FocusIndex", nFocusIndex) );
                  } else {
                      startActivity( launchFullScreen.putExtra("com.teleca.mm5.gallery.FocusIndex", 0) );
                  }
                }
            });

            // DELETE option handling
            Button mDeleteButton = (Button)findViewById(R.id.deleteButton);
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: handle delete here
                }
            });

            mSelectionAnimation = AnimationUtils.loadAnimation(this, R.anim.thumbnail_selection);
            mOptionBarShow = AnimationUtils.loadAnimation(this, R.anim.thumbnail_optionbar_show);
            mOptionBarHide = AnimationUtils.loadAnimation(this, R.anim.thumbnail_optionbar_hide);

            // set empty tests for text view - name and counter
            mTextView = (TextView)findViewById(R.id.thumbnailItemDisplayName);
            mTextView.setText( " " );

            mTextView = (TextView)findViewById(R.id.thumbnailCounter);
            mTextView.setText( " " );
        } catch (Exception e) {
            Log.e(TAG, "onCreate(): " + e.getClass() + " thrown " + e.getMessage());
        }
    }

    private void update(int mSelectItemId, View selectedView){
        TextView mTextView = null;
        ImageView mResizeImage = null;
        ImageView imageView = null;
        Resources mRes = getResources();

        mTextView = (TextView)findViewById(R.id.thumbnailItemDisplayName);
        mTextView.setText( mImageAdapter.getNameItemId(mSelectItemId, mRes));

        mTextView = (TextView)findViewById(R.id.thumbnailCounter);
        mTextView.setText(String.format("%d/%d", mSelectItemId + 1, mImageAdapter.getCount()));

        if( null != selectedView && selectedView instanceof ImageView) {
            imageView = (ImageView)selectedView;
        }

        if( null != imageView ) {
            mResizeImage = (ImageView) mImageAdapter.getView(mSelectItemId,
                                                             findViewById(R.id.imageView1),
                                                             null);
            ViewGroup.LayoutParams zoomedImageLayoutParams = mResizeImage.getLayoutParams();

            if( zoomedImageLayoutParams instanceof ViewGroup.MarginLayoutParams ) {
                Integer thumbnailX = getMainView().getLeft() + imageView.getLeft() - ( ImageAdapter.getZoomThumbnailWidth() - imageView.getWidth() ) / 2;
                Integer thumbnailY = getMainView().getTop() + imageView.getTop() - ( ImageAdapter.getZoomThumbnailHeight() - imageView.getHeight() ) / 2;

                ((ViewGroup.MarginLayoutParams)zoomedImageLayoutParams).setMargins(thumbnailX,
                                                                                   thumbnailY,
                                                                                   0,
                                                                                   0);
                zoomedImageLayoutParams.height = ImageAdapter.getZoomThumbnailWidth();
                zoomedImageLayoutParams.width = ImageAdapter.getZoomThumbnailHeight();
                mResizeImage.setLayoutParams(zoomedImageLayoutParams);
            }

            if(null != mSelectionAnimation) {
                mSelectionAnimation.cancel();
                mResizeImage.startAnimation(mSelectionAnimation);
            }

            // if focus already presented - there's no need to show up options bar
            if( nFocusIndex < 0 ) {
                // show options bar
                View optionsBar = findViewById(R.id.thimbnail_optionbar);

                optionsBar.startAnimation(mOptionBarShow);
                optionsBar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.thumbnail_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean retVal = true;

        switch (item.getItemId()) {
        case R.id.DETAILS_ID :
            /*!! Intent intent = new Intent(this, DetaisView.class);
            startActivity(intent);!!*/
            retVal = true;
            break;

        case R.id.LISTVIEW_ID:
            /*!! Intent intent = new Intent(this, ListViewView.class);
            startActivity(intent);!!*/
            retVal = true;
            break;

        case R.id.FULLSCREEN_ID:
            Intent launchFullScreen = new Intent(getGalleryViewContext(), FullScreenView.class);

            if(nFocusIndex >= 0) {
                startActivity(launchFullScreen.putExtra("com.teleca.mm5.gallery.FocusIndex", nFocusIndex));
            } else {
                startActivity(launchFullScreen.putExtra("com.teleca.mm5.gallery.FocusIndex", 0));
            }
            retVal = true;
            break;

        default:
            retVal = false;
            break;
        }

        return retVal;
    }

    @Override
    public void finishedWorkExecution(GalleryWorkTaskResult processingResult) {
        // store status of processing
        if( GalleryWorkTaskResult.GALLERY_RESULT_FINISHED == processingResult ) {

            mImageAdapter.setContentCursor(getContentCursor());

            getMainView().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View view,
                                           int mSelectItemId, long arg3) {
                    update(mSelectItemId, view);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // nothing to do here
                }
            });

            getMainView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View view,
                                        int mSelectItemId, long arg3) {
                    // if the same image has been selected again, open fullscreen view
                    if( nFocusIndex == mSelectItemId ) {
                        Intent launchFullScreen = new Intent(getGalleryViewContext(), FullScreenView.class);
                        startActivity(launchFullScreen.putExtra("com.teleca.mm5.gallery.FocusIndex", nFocusIndex));
                    } else {
                        update(mSelectItemId, view);
                        nFocusIndex = mSelectItemId;
                    }
                }
            });

            getMainView().setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    ImageView mResizeImage = (ImageView)findViewById(R.id.imageView1);

                    if( null != mResizeImage ) {
                        mResizeImage.setImageBitmap(null);

                        if(nFocusIndex >= 0) {
                            // reset focus index also
                            nFocusIndex = -1;

                            // play animation to hide options bar
                            View optionBar = findViewById(R.id.thimbnail_optionbar);

                            optionBar.startAnimation(mOptionBarHide);
                            optionBar.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                }
            });
        }
    }
}

