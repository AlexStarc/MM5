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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentType(GalleryViewType.GALLERY_THUMBNAILS);
        super.onCreate(savedInstanceState);
    }

}
