package com.teleca.mm5.gallery;

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

public class ThumbnailsView extends GalleryView<GridView> implements GalleryViewInterface/*, ValueAnimator.AnimatorUpdateListener*/ {
    private ImageAdapter mImageAdapter;
    private static final String TAG = "ThumbnailsView";
    private Animation mSelectionAnimation = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        TextView mTextView = null;

        setContentType(GalleryViewType.GALLERY_THUMBNAILS);
        super.onCreate(savedInstanceState);

        mImageAdapter = new ImageAdapter(this, getContentCursor());

        try {
            getMainView().setAdapter(mImageAdapter);
            getMainView().setSelector(R.drawable.thumbnail_selector);
            Button mViewButton = (Button)findViewById(R.id.button1);
            mViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*!! Intent intent = new Intent(this, ThumbnailsView.class);
                startActivity(intent);!!*/
                }
            });

            Button mOptionsButton = (Button)findViewById(R.id.button2);
            mOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openOptionsMenu();
                }
            });

            mSelectionAnimation = AnimationUtils.loadAnimation(this, R.anim.thumbnail_selection);

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

        if( null != selectedView && selectedView instanceof ImageView)
        {
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

        switch (item.getItemId()) {
        case R.id.DETAILS_ID :
            /*!! Intent intent = new Intent(this, DetaisView.class);
            startActivity(intent);!!*/
            return true;
        case R.id.LISTVIEW_ID:
            /*!! Intent intent = new Intent(this, ListViewView.class);
            startActivity(intent);!!*/
            return true;

        case R.id.FULLSCREEN_ID:
            /*!! Intent intent = new Intent(this, FullScreenView.class);
            startActivity(intent);!!*/
            return true;

        default:
            return false;
        }
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

                }
            });

            getMainView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view,
                                        int mSelectItemId, long arg3) {
                    update(mSelectItemId, view);
                }
            });

            getMainView().setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    // no need to handle scroll state change
                    ImageView mResizeImage = (ImageView)findViewById(R.id.imageView1);

                    if( null != mResizeImage )
                    {
                        mResizeImage.setImageBitmap(null);
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

