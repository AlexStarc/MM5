package com.teleca.mm5.gallery;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ThumbnailsView extends Activity
implements
GalleryView,
ValueAnimator.AnimatorUpdateListener {
    private ImageAdapter mImageAdapter;
    private GridView gridview;
    private ImageView curSelectionImageView;
    private Cursor contentCursor;
    private GalleryWorkTask     galleryWorkBg;
    private static final String TAG = "ThumbnailsView";
    private ValueAnimator       selectionUpdateAnimator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        TextView mTextView = null;

        galleryWorkBg = new GalleryWorkTask(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.thumbnails);

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

        galleryWorkBg.execute(GalleryWorkTaskContentType.GALLERY_IMAGES);

        // Setup animation for displaying of selected thumbnail
        selectionUpdateAnimator = ValueAnimator.ofPropertyValuesHolder(PropertyValuesHolder.ofFloat("scaleX", 0.6f, 1),
                                                                       PropertyValuesHolder.ofFloat("scaleY", 0.6f, 1));
        selectionUpdateAnimator.setDuration(500);
        selectionUpdateAnimator.setInterpolator(new android.view.animation.DecelerateInterpolator());
        selectionUpdateAnimator.addUpdateListener(this);

        // set empty tests for text view - name and counter
        mTextView = (TextView)findViewById(R.id.thumbnailItemDisplayName);
        mTextView.setText( " " );

        mTextView = (TextView)findViewById(R.id.thumbnailCounter);
        mTextView.setText( " " );
    }

    @Override
    protected void onDestroy() {
        // need additional stop BG work task
        try {
            galleryWorkBg.cancel(true);
        }
        catch(Exception e) {
            Log.e( TAG, "EXCEPTION: " + e.getClass() + " thrown " + e.getMessage());
        }
        super.onDestroy();
    }

    private void update(int mSelectItemId){
        TextView mTextView;
        ImageView mResizeImage;
        Resources mRes = getResources();

        mTextView = (TextView)findViewById(R.id.thumbnailItemDisplayName);
        mTextView.setText( mImageAdapter.getNameItemId(mSelectItemId, mRes));

        mTextView = (TextView)findViewById(R.id.thumbnailCounter);
        mTextView.setText(String.format("%d/%d", mSelectItemId + 1, mImageAdapter.getCount()));

        ImageView imageView = (ImageView)gridview.getChildAt(mSelectItemId);

        int[] locationImageView = new int[2];
        int[] locationGridView = new int[2];

        imageView.getLocationOnScreen(locationImageView);
        gridview.getLocationOnScreen(locationGridView);

        mResizeImage = (ImageView) mImageAdapter.getView(mSelectItemId,
                                                         findViewById(R.id.imageView1),
                                                         null);

        mResizeImage.setX(locationImageView[0] - 40);
        mResizeImage.setY(locationImageView[1] - 20 - locationGridView[1]);

        selectionUpdateAnimator.cancel();
        // store currently animated view
        curSelectionImageView = mResizeImage;
        selectionUpdateAnimator.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.thubnail, menu);
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
    public void progressWorkExecution(int NumberFiles) {
        // TODO Auto-generated method stub
    }

    @Override
    public void finishedWorkExecution(GalleryWorkTaskResult processingResult) {
        // store status of processing
        if( GalleryWorkTaskResult.GALLERY_RESULT_FINISHED == processingResult ) {
            mImageAdapter = new ImageAdapter(this, contentCursor);

            gridview = (GridView) findViewById(R.id.gridView1);
            gridview.setAdapter(mImageAdapter);

            gridview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View view,
                                           int mSelectItemId, long arg3) {
                    update(mSelectItemId);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int mSelectItemId, long arg3) {
                    update(mSelectItemId);
                    gridview.setSelection(mSelectItemId);
                }
            });
        }
    }

    @Override
    public void setContentCursor(Cursor contentViewCursor) {
        // store content list
        contentCursor = contentViewCursor;
    }

    @Override
    public Context getGalleryViewContext() {
        return getApplicationContext();
    }

    @Override
    public void onAnimationUpdate( ValueAnimator animation ) {
        PropertyValuesHolder[] propertyValues = null;

        try {
            if (null != curSelectionImageView) {
                propertyValues = animation.getValues();

                if( 2 == propertyValues.length )
                {
                    curSelectionImageView.setScaleX((Float)animation.getAnimatedValue( propertyValues[0].getPropertyName() ));
                    curSelectionImageView.setScaleY((Float)animation.getAnimatedValue( propertyValues[1].getPropertyName() ));
                }
            }
        } catch (Exception e) {
            Log.e(TAG,
                  "onAnimationUpdate(): " + e.getClass() + " thrown "
                  + e.getMessage());
        }
    }
}

