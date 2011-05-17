/**
 * Content item to be used by views
 */
package com.sandrstar.android.gallery;

import android.graphics.Bitmap;

@ClassPreamble (
                author = "Alexander Starchenko",
                description = "class for representing single content item under gallery"
)
public class GalleryContentItem implements Cloneable {
    private Bitmap contentBitmap;
    private String contentName;
    private String contentPath;
    private Integer index = -1;

    /** Constructors */
    GalleryContentItem() {
    }

    GalleryContentItem( Bitmap contentBitmap, String contentName ) {
        setContentBitmap( contentBitmap );
        setContentName( contentName );
    }

    GalleryContentItem( Bitmap contentBitmap, String contentName, String contentPath ) {
        setContentBitmap( contentBitmap );
        setContentName( contentName );
        setContentPath( contentPath );
    }

    GalleryContentItem( String contentName, String contentPath ) {
        setContentName( contentName );
        setContentPath( contentPath );
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

    /**
     * @param contentPath the contentPath to set
     */
    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    /**
     * @return the contentPath
     */
    public String getContentPath() {
        return contentPath;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * @return the index
     */
    public Integer getIndex() {
        return index;
    }
}
