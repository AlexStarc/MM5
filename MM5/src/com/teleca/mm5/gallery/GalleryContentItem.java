/**
 * 
 */
package com.teleca.mm5.gallery;

import android.graphics.Bitmap;

@ClassPreamble (
        author = "Alexander Starchenko",
        description = "class for representing single content item under gallery"
)
public class GalleryContentItem {
    private Bitmap contentBitmap;
    private String contentName;

    /** Constructors */
    GalleryContentItem() {
    }

    GalleryContentItem( Bitmap contentBitmap, String contentName ) {
        setContentBitmap( contentBitmap );
        setContentName( contentName );
    }
    /**
     * @param contentBitmap the contentBitmap to set
     */
    public void setContentBitmap(Bitmap contentBitmap) {
        this.contentBitmap = contentBitmap;
    }

    /**
     * @return the contentBitmap
     */
    public Bitmap getContentBitmap() {
        return contentBitmap;
    }

    /**
     * @param contentName the contentName to set
     */
    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    /**
     * @return the contentName
     */
    public String getContentName() {
        return contentName;
    }

}
