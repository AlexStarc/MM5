package com.teleca.mm5.gallery;

import android.os.AsyncTask;
import android.util.Log;


@ClassPreamble (
		author = "Alexander Starchenko",
		description = "applet work task, main purpose - interact with fs and providers"
	)
class GalleryWorkTask extends AsyncTask<Integer, Integer, Integer> {
	private static final String TAG = "GalleryWorkTask";
	private gallery parentGallery;
	
	/** Constructors of async gallery work task */
	public GalleryWorkTask() {
	}	
	
	public GalleryWorkTask(gallery parentGallery) {
		this.setParentGallery(parentGallery);
	}
	
	protected Integer doInBackground(Integer... type) {
		Log.i( TAG, "work task bg processing started" );
		return 0;
	}
	
	protected void onProgressUpdate(Integer... progress) {
	}
	
	protected void onPostExecute(Integer result) {
		Log.i( TAG, "bg processign finished, invoke back UI" );
	}

	/**
	 * @param parentGallery the parentGallery to set
	 */
	protected void setParentGallery(gallery parentGallery) {
		this.parentGallery = parentGallery;
	}

	/**
	 * @return the parentGallery
	 */
	protected gallery getParentGallery() {
		return parentGallery;
	}
}
