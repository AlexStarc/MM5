package com.teleca.mm5.gallery;

import java.lang.annotation.Documented;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

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
    public void setContentList( GalleryContentItem[] contentArray );
    public Context getGalleryViewContext();
    public GalleryContentItem[] getContentList();
}

@ClassPreamble (
                author = "Alexander Starchenko",
                description = "main applet class"
)
public class gallery extends Activity implements GalleryView {
    private static final String TAG = "gallery";
    private AsyncTask<GalleryWorkTaskContentType, Integer, GalleryWorkTaskResult> galleryWorkBg;
    static GalleryContentItem[] resultingContentList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        GalleryWorkTask galleryWorkBg = new GalleryWorkTask(this);

        Log.i( TAG, "Started" );
        super.onCreate(savedInstanceState);
        /* start background work process execution */
        galleryWorkBg.execute(GalleryWorkTaskContentType.GALLERY_IMAGES);

        setContentView(R.layout.main);
        /* Select media type (image/music) on the main view then start next activity  */

        /* onSlect start Thumbnails view of images */
        Intent intent = new Intent(this, ThumbnailsView.class);
        startActivity(intent);
    }

    @Override
    public void progressWorkExecution( int NumberFiles ) {
        Log.i( TAG, "BG work has" + NumberFiles );
    }

    @Override
    public void finishedWorkExecution( GalleryWorkTaskResult processingResult ) {
        /** Here all items received by view via ArrayList. It get called just before finishedWorkExecution;
         *  Contents of view might needs to be updated only after status received via finishedWorkExecution */
        // TODO: provide list updating
        Log.i( TAG, "BG work has finished with " + processingResult );
    }

    @Override
    public void setContentList( GalleryContentItem[] contentArray ) {
        setResultingContentList(contentArray);
    }

    @Override
    public Context getGalleryViewContext() {
        return getApplicationContext();
    }

    public void setResultingContentList(GalleryContentItem[] mresultingContentList) {
        resultingContentList = mresultingContentList;

        /* onSlect start Thumbnails view of images */
        Intent intent = new Intent(this, ThumbnailsView.class);
        ///intent.putExtra(ThumbnailsView.EXT_GELLERYCONTENT, resultingContentList);

        startActivity(intent);
    }

    public GalleryContentItem[] getResultingContentList() {
        return resultingContentList;
    }

    @Override
    public GalleryContentItem[] getContentList() {
        return resultingContentList;
    }

}