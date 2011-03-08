package com.teleca.mm5.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ThumbnailsView extends Activity implements GalleryView{
    private ImageAdapter mImageAdapter;
    private GridView gridview;
    private GalleryContentItem[] contentThumbnailsArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        GalleryWorkTask galleryWorkBg = new GalleryWorkTask(this);
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
    }

    private void update(int mSelectItemId){
        TextView mTextView;
        ImageView mResizeImage;
        Resources mRes = getResources();

        mTextView = (TextView)findViewById(R.id.textView1);
        mTextView.setText( mImageAdapter.getNameItemId(mSelectItemId, mRes));

        mTextView = (TextView)findViewById(R.id.textView2);
        mTextView.setText(String.format("%d/%d", mSelectItemId + 1, mImageAdapter.getCount()));

        ImageView imageView = (ImageView)gridview.getChildAt(mSelectItemId);

        int[] locationImageVoew = new int[2];
        int[] locationGridVoew = new int[2];
        imageView.getLocationOnScreen(locationImageVoew);
        gridview.getLocationOnScreen(locationGridVoew);

        mResizeImage = (ImageView)mImageAdapter.getView( mSelectItemId, findViewById(R.id.imageView1), null);

        mResizeImage.setX(( locationImageVoew[0] - locationGridVoew[0] ));
        mResizeImage.setY(( locationImageVoew[1] - locationGridVoew[1] ));
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
            mImageAdapter = new ImageAdapter(this, contentThumbnailsArray);

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
    public void setContentList(GalleryContentItem[] contentArray) {
        // store content list
        contentThumbnailsArray = contentArray.clone();
    }

    @Override
    public Context getGalleryViewContext() {
        return getApplicationContext();
    }
}

