package com.teleca.mm5.gallery;

import android.app.Activity;
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

public class ThumbnailsView extends Activity {
	private ImageAdapter mImageAdapter;
	private int mCount;
	private GridView gridview;
	private int prevFocus;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.thumbnails);
		
        mImageAdapter = new ImageAdapter(this);
        mCount = mImageAdapter.getCount();

        gridview = (GridView) findViewById(R.id.gridView1);
        prevFocus = -1;
        
        gridview.setAdapter(mImageAdapter);
        gridview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
 			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int mSelectItemId, long arg3) {
 				update(mSelectItemId);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
 		    	if( prevFocus != -1 ){
 		    		ImageView imageView = (ImageView)gridview.getChildAt(prevFocus);
 	 		    	imageView.setLayoutParams(new GridView.LayoutParams(60, 60));
 	 		    	prevFocus = -1;
 		    	}
			}
		});

        
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int mSelectItemId, long arg3) {
				update(mSelectItemId);
			}
		});
        


        Button mViewButton = (Button)findViewById(R.id.button1);
        mViewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
               /*!! Intent intent = new Intent(this, ThumbnailsView.class);
                startActivity(intent);!!*/
            }
        });

        Button mOptionsButton = (Button)findViewById(R.id.button2);
        mOptionsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openOptionsMenu();
            }
        });
    }

    private void update(int mSelectItemId){
    	TextView mNameOfFile;
	    	TextView mCountOfFiles;
	    	Resources mRes = getResources();
	    	//Images mImages;

	    	if( prevFocus != -1 ){
	    		ImageView imageView = (ImageView)gridview.getChildAt(prevFocus);
		    	imageView.setLayoutParams(new GridView.LayoutParams(60, 60));
	    	}
	    	
	    	mNameOfFile = (TextView)findViewById(R.id.textView1);
	    	mNameOfFile.setText( mImageAdapter.getNameItemId(mSelectItemId, mRes));
	        
	    	mCountOfFiles = (TextView)findViewById(R.id.textView2);
	    	mCountOfFiles.setText(String.format("%d/%d", mSelectItemId + 1, mCount));

	    	ImageView imageView = (ImageView)gridview.getChildAt(mSelectItemId);
	    	imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
	    	prevFocus = mSelectItemId;
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
}

