package com.teleca.mm5.gallery;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;





@ClassPreamble (
                author = "Alexander Starchenko",

                description = "applet work task, main purpose - interact with fs and providers"
)


class GalleryWorkTask extends AsyncTask<GalleryContentType, Integer, GalleryWorkTaskResult> {
    private static final String TAG = "GalleryWorkTask";
    private GalleryViewInterface parentGalleryView;
    ContentResolver mainContentResolver;
    Cursor mainContentCursor;

    /** Constructors of async gallery work task */
    public GalleryWorkTask() {
    }

    public GalleryWorkTask(GalleryViewInterface parentGalleryView) {
        this.setParentGalleryView(parentGalleryView);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        parentGalleryView.progressWorkExecution(1);
    }

    @Override
    protected void onPostExecute(GalleryWorkTaskResult result) {
        Log.i( TAG, "bg processign finished, invoke back parent view" );
        /* Share execution resulted list with parent view,
         * where's no need to share it if its empty */
        parentGalleryView.setContentCursor(mainContentCursor);

        /* Share status with parent view in order to notify about errors, if any */
        parentGalleryView.finishedWorkExecution(result);
    }

    /**
     * @param parentGalleryView the parentGalleryView to set
     */
    protected final void setParentGalleryView(GalleryViewInterface parentGalleryView) {
        this.parentGalleryView = parentGalleryView;

        // get content provider here also
        if( null != parentGalleryView ) {
            mainContentResolver = parentGalleryView.getGalleryViewContext().getContentResolver();
        }
    }

    /**
     * @return the parentGalleryView
     */
    protected final GalleryViewInterface getParentGalleryView() {
        return parentGalleryView;
    }

    @Override
    public String toString() {
        return ("GalleryWorkTask <" + parentGalleryView.toString() + ">");
    }

    @Override
    protected GalleryWorkTaskResult doInBackground(GalleryContentType... params) {
        GalleryWorkTaskResult resultProcessing = GalleryWorkTaskResult.GALLERY_RESULT_ERROR;
        String mediaStorageState = Environment.getExternalStorageState();
        Uri uriGalleryContent = null;

        Log.i( TAG, "work task bg processing started with media state " + mediaStorageState);

        if (Environment.MEDIA_MOUNTED.equals(mediaStorageState)) {
            switch(params[0]) {
            case GALLERY_AUDIO:
                uriGalleryContent = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                break;

            case GALLERY_IMAGES:
                uriGalleryContent = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                break;

            default:
                Log.i( TAG, "doInBackground(): incorrect parameter encounted" );
                throw new IllegalArgumentException("incorrect GalleryWorkTaskContentType passed by view");
            }

            // here we get URI and ready to prepare query, however, need to cancel previous one
            if( null != mainContentResolver && null == mainContentCursor ) {
                mainContentCursor = mainContentResolver.query(uriGalleryContent, null, null, null, null);
            }

            if( null != mainContentCursor ) {
                Boolean recordsAvailable = false;
                int contentCount = 0;

                try {
                    recordsAvailable = mainContentCursor.moveToFirst();
                    contentCount = mainContentCursor.getCount();

                    if( recordsAvailable && 0 < contentCount ) {
                        // processing finished
                        resultProcessing = GalleryWorkTaskResult.GALLERY_RESULT_FINISHED;
                    } else {
                        // set empty status
                        resultProcessing = GalleryWorkTaskResult.GALLERY_RESULT_EMPTY;

                        Log.e( TAG, "No records available for requested content type");
                    }
                }
                catch(Exception e) {
                    Log.e( TAG, "doInBackground(): " + e.getClass() + " thrown " + e.getMessage());
                }
            }
        }

        return resultProcessing;
    }
}
