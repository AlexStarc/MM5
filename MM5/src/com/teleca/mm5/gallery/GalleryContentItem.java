/**
 * Content item to be used by views
 */
package com.teleca.mm5.gallery;

import android.graphics.Bitmap;

@ClassPreamble (
                author = "Alexander Starchenko",
                description = "class for representing single content item under gallery"
)
public class GalleryContentItem implements Cloneable {
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
    protected final void setContentBitmap(Bitmap contentBitmap) {
        this.contentBitmap = contentBitmap;
    }

    /**
     * @return the contentBitmap
     */
    protected final Bitmap getContentBitmap() {
        return contentBitmap;
    }

    /**
     * @param contentName the contentName to set
     */
    protected final void setContentName(String contentName) {
        this.contentName = contentName;
    }

    /**
     * @return the contentName
     */
    protected final String getContentName() {
        return contentName;
    }

    @Override
    public String toString() {
        return ("GalleryContentItem <" + contentName + ">");
    }
}
