/**
 * This is implementation of Details view for ContentGallery application
 */
package com.sandrstar.android.gallery;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author AlexStarc
 *
 */
public class DetailsView extends GalleryView<RelativeLayout> {
    private static final String TAG = "ListViewGallery";

    private Integer mnFocusIndex = 0;
    private GalleryViewType mContentViewType = GalleryViewType.GALLERY_IMAGE_DETAILS;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        // As there's possibility to miss so extras - try the block
        try {
            final Bundle extraParameters = this.getIntent().getExtras();

            // we're awaiting FocusIndex parameter to decide which file should handle
            this.mnFocusIndex = extraParameters.getInt("com.teleca.mm5.gallery.FocusIndex", 0);
            this.mContentViewType = GalleryViewType.values()[extraParameters.getInt("com.teleca.mm5.gallery.ContentType", 0)];

            this.setContentType(this.mContentViewType);
            super.onCreate(savedInstanceState);
        } catch(final Exception e) {
            Log.e(TAG, "onCreate(): " + e.getClass() + " thrown " + e.getMessage());
        }
    }

    @Override
    public void finishedWorkExecution(final GalleryWorkTaskResult processingResult) {
        TextView textView = null;
        String sItemText = null;

        // Try to load image from the cursor using the index
        super.finishedWorkExecution(processingResult);

        if(processingResult == GalleryWorkTaskResult.GALLERY_RESULT_FINISHED) {
            // got to the position of item we need to display details of
            this.getContentCursor().moveToPosition(this.mnFocusIndex);

            // we've obtained cursor, fill up views in the list

            // Fill up display name to be displayed on the top of details screen
            textView = (TextView)this.getMainView().findViewById(R.id.details_displayname);
            textView.setText(this.getContentCursor().getString(this.getContentCursor().getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)));

            // Fill up full path
            textView = (TextView)this.getMainView().findViewById(R.id.details_path);

            sItemText = this.getContentCursor().getString(this.getContentCursor().getColumnIndex(MediaStore.MediaColumns.DATA));

            textView.setText(sItemText);
        }
    }
}
