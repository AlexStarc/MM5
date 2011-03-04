package com.teleca.mm5.gallery;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class ThumbnailsView extends Activity {
	private ImageAdapter mImageAdapter;
	private int mCount;
	private GridView gridview;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.thumbnails);
		
        mImageAdapter = new ImageAdapter(this);
        mCount = mImageAdapter.getCount();
        gridview = (GridView) findViewById(R.id.gridView1);
        gridview.setAdapter(mImageAdapter);
        gridview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
 			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int mSelectItemId, long arg3) {
 		    	TextView mNameOfFile;
 		    	TextView mCountOfFiles;
 		    	Resources mRes = getResources();
 		    	//Images mImages;
 		    	
 		    	
 		    	mImageAdapter.getItem(mSelectItemId);
 		    	
 		    	mNameOfFile = (TextView)findViewById(R.id.textView1);
 		    	mNameOfFile.setText(mRes.getResourceEntryName((int) mImageAdapter.getItemId(mSelectItemId)));
 		        
 		    	mCountOfFiles = (TextView)findViewById(R.id.textView2);
 		    	mCountOfFiles.setText(String.format("%d/%d", mSelectItemId, mCount));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
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
            	TextView mNameOfFile;

            	
            	mNameOfFile = (TextView)findViewById(R.id.textView1);
            	mNameOfFile.setText("Name1");
            	
            }
        });
        


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.thubnail, menu);
        return true;
    }
 
}

