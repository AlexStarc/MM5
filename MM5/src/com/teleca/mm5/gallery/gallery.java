package com.teleca.mm5.gallery;

import java.lang.annotation.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

@Documented
@interface ClassPreamble {
	   String author();
	   int currentRevision() default 1;
	   String description();
	}

enum GalleryViews {
	GALLERY_LIST,
	GALLERY_THUMBNAILS,
	GALLERY_FULLSCREEN,
	GALLERY_DETAILS
}

@ClassPreamble (
		author = "Alexander Starchenko",
		description = "main applet class"
	)
public class gallery extends Activity {
	private static final String TAG = "gallery";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	GalleryWorkTask galleryWorkBg = new GalleryWorkTask(this); 

    	Log.i( TAG, "Started" );
        super.onCreate(savedInstanceState);
        
        /* start background work process execution */
        galleryWorkBg.execute(0);
        
        setContentView(R.layout.main);
        /* Select media type (image/music) on the main view then start next activity  */
        
        /* onSlect start Thumbnails view of images */
        Intent intent = new Intent(this, ThumbnailsView.class);
        startActivity(intent);

    }
}