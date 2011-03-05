package com.teleca.mm5.gallery;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.provider.MediaStore;
import android.net.Uri;
import android.database.Cursor;
import java.lang.IllegalArgumentException;
import android.content.ContentResolver;
import android.content.Intent;

import java.util.ArrayList;


//enum for results of work task processing
enum GalleryWorkTaskResult {
	GALLERY_RESULT_EMPTY,
	GALLERY_RESULT_FINISHED,
	GALLERY_RESULT_ERROR,
}

enum GalleryWorkTaskContentType {
	GALLERY_AUDIO,
	GALLERY_IMAGES
}

@ClassPreamble (
		author = "Alexander Starchenko",
		description = "applet work task, main purpose - interact with fs and providers"
	)
class GalleryWorkTask extends AsyncTask<GalleryWorkTaskContentType, Integer, GalleryWorkTaskResult> {
	private static final String TAG = "GalleryWorkTask";
	private GalleryView parentGalleryView;
	ContentResolver mainContentResolver;
	Cursor mainContentCursor;
	ArrayList<GalleryContentItem> resultingContentList;
	
	/** Constructors of async gallery work task */
	public GalleryWorkTask() {
	}	
	
	public GalleryWorkTask(GalleryView parentGalleryView) {
		this.setParentGalleryView(parentGalleryView);
	}
	
	/** below function contents get executed in a separate thread */
	protected GalleryWorkTaskResult doInBackground(GalleryWorkTaskContentType... type) {
		GalleryWorkTaskResult resultProcessing;
		String mediaStorageState = Environment.getExternalStorageState();
		
		Log.i( TAG, "work task bg processing started with media state " + mediaStorageState);
		
		if (Environment.MEDIA_MOUNTED.equals(mediaStorageState)) {
			// first of all ensure we'll get refreshed data by sending intent to MediaScanner
			try {
				parentGalleryView.getGalleryViewContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
			}
			catch(Exception e) {
				Log.e( TAG, "something wrong with sendBroadcast: " + e.getClass() + " thrown " + e.getMessage());
			}

			// now wait until all contents are scanned
			try {
				while( isMediaScannerScanning() );
			}
			catch(Exception e) {
				Log.e( TAG, "something wrong with the waiting: " + e.getClass() + " thrown " + e.getMessage());
			}
			
			switch(type[0]) {
			case GALLERY_AUDIO:
				break;
			
			case GALLERY_IMAGES:
				mainContentCursor = MediaStore.Images.Media.query(mainContentResolver,
																  MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
														          null );
				break;
				
			default:
				Log.i( TAG, "incorrect parameter encounted" );
				throw new IllegalArgumentException("incorrect GalleryWorkTaskContentType passed by view");
			}
		
			// here we get URI and ready to prepare query, however, need to cancel previous one
			if( null != mainContentResolver && null == mainContentCursor ) {
				mainContentCursor = mainContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);			
			}
			
			if( null != mainContentCursor ) {
				String name;
				String assetData;
				Boolean recordsAvailable = false;
				int nameColumn = 0;
				int dataColumn = 0;
				
				try {
					recordsAvailable = mainContentCursor.moveToFirst();
					
					if( recordsAvailable  ) {
						nameColumn = mainContentCursor.getCount();
						nameColumn = mainContentCursor.getColumnIndex(type[0] == GalleryWorkTaskContentType.GALLERY_AUDIO ?
																	  MediaStore.Audio.Media.DISPLAY_NAME :
																	  MediaStore.Images.Media.DISPLAY_NAME);
						dataColumn = mainContentCursor.getColumnIndex(type[0] == GalleryWorkTaskContentType.GALLERY_AUDIO ?
																	  MediaStore.Audio.Media.DATA :
																	  MediaStore.Images.Media.DATA);
					
						do {
							name = mainContentCursor.getString(nameColumn);
							assetData = mainContentCursor.getString(dataColumn);
							Log.i(TAG, "content display name " + name);
						} while( mainContentCursor.moveToNext() );
					} else {
						Log.e( TAG, "No records available!");
					}
				}
				catch(Exception e) {
					Log.e( TAG, "something wrong with the base: " + e.getClass() + " thrown " + e.getMessage());
				}
			}
		}
		// set processing end result
		resultProcessing = GalleryWorkTaskResult.GALLERY_RESULT_FINISHED;
		return resultProcessing;
	}
	
	protected void onProgressUpdate(Integer... progress) {
		parentGalleryView.progressWorkExecution(1);
	}
	
	protected void onPostExecute(GalleryWorkTaskResult result) {
		Log.i( TAG, "bg processign finished, invoke back parent view" );
		/* Share execution resulted list with parent view,
		 * where's no need to share it if its empty */
		if( null != resultingContentList &&
			!resultingContentList.isEmpty() ) {
			parentGalleryView.setContentList(resultingContentList);
		}

		/* Share status with parent view in order to notify about errors, if any */
		parentGalleryView.finishedWorkExecution(result);
	}

	/**
	 * @param parentGalleryView the parentGalleryView to set
	 */
	protected void setParentGalleryView(GalleryView parentGalleryView) {
		this.parentGalleryView = parentGalleryView;
		
		// get content provider here also
		if( null != parentGalleryView ) {
			mainContentResolver = parentGalleryView.getGalleryViewContext().getContentResolver();
		}
	}

	/**
	 * @return the parentGalleryView
	 */
	protected GalleryView getParentGalleryView() {
		return parentGalleryView;
	}
	
	
    private boolean isMediaScannerScanning() {
        boolean result = false;
        Uri scannerUri = MediaStore.getMediaScannerUri();
        Cursor cursor = parentGalleryView.getGalleryViewContext().getContentResolver().query( scannerUri, 
        					   																  null,
        					   																  null,
        					   																  null,
        					   																  null );
        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                result = "external".equals(cursor.getString(0));
            }
            cursor.close(); 
        } 

        return result;
    }
}
