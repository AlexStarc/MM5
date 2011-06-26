/**
 * Content item to be used by views
 */
package com.sandrstar.android.gallery;

import android.graphics.Bitmap;

@ClassPreamble (
                author = "Alexander Starchenko",
                description = "class for representing single content item under gallery"
)
public class GalleryContentItem {
    private Bitmap contentBitmap;
    private String contentName;
    private String contentPath;
    private Integer index = -1;

    GalleryContentItem( final Bitmap contentBitmap, final String contentName ) {
        setContentBitmap( contentBitmap );
        setContentName( contentName );
    }

    GalleryContentItem( final Bitmap contentBitmap, final String contentName, final String contentPath ) {
        setContentBitmap( contentBitmap );
        setContentName( contentName );
        setContentPath( contentPath );
    }

    GalleryContentItem( final String contentName, final String contentPath ) {
        setContentName( contentName );
        setContentPath( contentPath );
    }

    public GalleryContentItem() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param contentBitmap the contentBitmap to set
     */
    protected final void setContentBitmap(final Bitmap contentBitmap) {
        this.contentBitmap = contentBitmap;
    }

    /**
     * @return the contentBitmap
     */
    protected final Bitmap getContentBitmap() {
        return this.contentBitmap;
    }

    /**
     * @param contentName the contentName to set
     */
    protected final void setContentName(final String contentName) {
        this.contentName = contentName;
    }

    /**
     * @return the contentName
     */
    protected final String getContentName() {
        return this.contentName;
    }

    @Override
    public String toString() {
        return ("GalleryContentItem <" + this.contentName + ">");
    }

    /**
     * @param contentPath the contentPath to set
     */
    public void setContentPath(final String contentPath) {
        this.contentPath = contentPath;
    }

    /**
     * @return the contentPath
     */
    public String getContentPath() {
        return this.contentPath;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(final Integer index) {
        this.index = index;
    }

    /**
     * @return the index
     */
    public Integer getIndex() {
        return this.index;
    }
}
