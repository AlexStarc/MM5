/**
 * This is implementation of Details view for ContentGallery application
 */
package com.teleca.mm5.gallery;

import android.os.Bundle;
import android.widget.RelativeLayout;

/**
 * @author AlexStarc
 *
 */
public class DetailsView extends GalleryView<RelativeLayout> {
    private static final String TAG = "ListViewGallery";

    private Integer mnFocusIndex = 0;
    private GalleryViewType mContentViewType = GalleryViewType.GALLERY_IMAGE_DETAILS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle extraParameters = getIntent().getExtras();

        // we're awaiting FocusIndex parameter to decide which file should handle
        mnFocusIndex = extraParameters.getInt("com.teleca.mm5.gallery.FocusIndex", 0);
        mContentViewType = GalleryViewType.values()[extraParameters.getInt("com.teleca.mm5.gallery.ContentType", 0)];

        setContentType(mContentViewType);
        super.onCreate(savedInstanceState);
    }


}
