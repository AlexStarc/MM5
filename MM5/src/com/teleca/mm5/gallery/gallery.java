package com.teleca.mm5.gallery;

import java.lang.annotation.*;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;

@Documented
@interface ClassPreamble {
	   String author();
	   int currentRevision() default 1;
	   String description();
	}

// enum for available gallery views to be used in methods
enum GalleryViewTypes {
	GALLERY_LIST,
	GALLERY_THUMBNAILS,
	GALLERY_FULLSCREEN,
	GALLERY_DETAILS
}

@ClassPreamble (
		author = "Alexander Starchenko",
		description = "interface for view for interaction with GalleryWorkTask task"
	)
interface GalleryView {
	public void progressWorkExecution( int NumberFiles );
	public void finishedWorkExecution( GalleryWorkTaskResult processingResult );
	public void setContentList( ArrayList<GalleryContentItem> contentList );
	public Context getGalleryViewContext();
}

@ClassPreamble (
		author = "Alexander Starchenko",
		description = "main applet class"
	)
public class gallery extends Activity implements GalleryView {
	private static final String TAG = "gallery";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	GalleryWorkTask galleryWorkBg = new GalleryWorkTask(this); 

    	Log.i( TAG, "Started" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /* start background work process execution */
        galleryWorkBg.execute(GalleryWorkTaskContentType.GALLERY_AUDIO);
    }

    public void progressWorkExecution( int NumberFiles ) {
    	Log.i( TAG, "BG work has" + NumberFiles );
    }
    
    public void finishedWorkExecution( GalleryWorkTaskResult processingResult ) {
    	Log.i( TAG, "BG work has finished with " + processingResult );
    }
    
    public void setContentList( ArrayList<GalleryContentItem> contentList ) {
    	/** Here all items received by view via ArrayList. It get called just before finishedWorkExecution;
    	 *  Contents of view might needs to be updated only after status received via finishedWorkExecution */
    	// TODO: provide list updating
    }
    
    public Context getGalleryViewContext() {
    	return getApplicationContext();
    }
}