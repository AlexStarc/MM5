package com.sandrstar.android.gallery;

import javax.security.auth.callback.Callback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;

/** Type of content the gallery able to display */
enum GalleryContentType {
    GALLERY_AUDIO,
    GALLERY_IMAGES,
    GALLERY_VIDEO,
    GALLERY_STATIC,
    GALLERY_ALLTYPES
}

//enum for results of work task processing
enum GalleryWorkTaskResult {
    GALLERY_RESULT_EMPTY,
    GALLERY_RESULT_FINISHED,
    GALLERY_RESULT_ERROR,
}


/** enum for available gallery views to be used in
   methods and data which these views provides */
enum GalleryViewType {
    GALLERY_MAINMENU      ( R.layout.main,               R.id.main_menu,        GalleryContentType.GALLERY_STATIC ),
    GALLERY_LIST          ( R.layout.listviewgallery,    R.id.listview_gallery, GalleryContentType.GALLERY_AUDIO ),
    GALLERY_THUMBNAILS    ( R.layout.thumbnails,         R.id.gridView1,        GalleryContentType.GALLERY_IMAGES ),
    GALLERY_FULLSCREEN    ( R.layout.fullscreen,         R.id.fullscrImageView, GalleryContentType.GALLERY_IMAGES ),
    GALLERY_IMAGE_DETAILS ( R.layout.details_image_view, R.id.details_layout,   GalleryContentType.GALLERY_IMAGES ),
    GALLERY_SOUND_DETAILS ( R.layout.details_sound_view, R.id.details_layout,   GalleryContentType.GALLERY_AUDIO );

    private final Integer layoutId;
    private final Integer mainViewId;
    private final GalleryContentType contentType;

    GalleryViewType(final Integer layoutId, final Integer mainViewId, final GalleryContentType contentType ) {
        this.layoutId = layoutId;
        this.contentType = contentType;
        this.mainViewId = mainViewId;
    }

    /**
     * @return the viewId
     */
    public Integer layoutId() {
        return this.layoutId;
    }

    /**
     * @return the contentType
     */
    public GalleryContentType contentType() {
        return this.contentType;
    }

    /**
     * @return the mainViewId
     */
    public Integer mainViewId() {
        return this.mainViewId;
    }
}



@ClassPreamble (
                author = "Alexander Starchenko",
                description = "interface for view for interaction with GalleryWorkTask task"
)
interface GalleryViewInterface {
    public void progressWorkExecution( int NumberFiles );
    public void finishedWorkExecution( GalleryWorkTaskResult processingResult );
    public void setContentCursor( Cursor contentViewCursor );
    public Context getGalleryViewContext();
}

@ClassPreamble (
                author = "Alexander Starchenko",
                description = "General abstract class; every Gallery View should extend it"
)
public abstract class GalleryView<V extends View> extends Activity implements Callback, GalleryViewInterface {
    private V mainView;
    private GalleryWorkTask galleryWorkBg;
    private Cursor contentCursor;
    private GalleryViewType contentViewType;
    private final static String contentPathField = MediaColumns.DATA;
    private final static String contentNameField = MediaColumns.DISPLAY_NAME;
    private final static String TAG = "GalleryView";

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.contentViewType.layoutId());

        try {
            setMainView((V)findViewById(this.contentViewType.mainViewId()));

            if( !this.contentViewType.contentType().equals(GalleryContentType.GALLERY_STATIC) ) {
                setGalleryWorkBg( new GalleryWorkTask(this) );
                this.galleryWorkBg.execute(this.contentViewType.contentType());
            }
        } catch(final Exception e) {
            Log.e( TAG, "onCreate(): " + e.getClass() + " thrown " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        // need additional stop BG work task
        try {
            if( null != this.galleryWorkBg )
            {
                this.galleryWorkBg.cancel(true);
            }
        } catch(final Exception e) {
            Log.e( TAG, "onDestroy(): " + e.getClass() + " thrown " + e.getMessage());
        }
        super.onDestroy();
    }

    @Override
    /**
     * Get context of this {GalleryView}
     *
     * @return {Context}
     */
    public Context getGalleryViewContext() {
        return getApplicationContext();
    }

    /**
     * @param mainView the mainView to set
     */
    public final void setMainView(final V mainView) {
        this.mainView = mainView;
    }

    /**
     * @return the mainView
     */
    public final V getMainView() {
        return this.mainView;
    }

    /**
     * @param galleryWorkBg the galleryWorkBg to set
     */
    public final void setGalleryWorkBg(final GalleryWorkTask galleryWorkBg) {
        this.galleryWorkBg = galleryWorkBg;
    }

    /**
     * @return the galleryWorkBg
     */
    public final GalleryWorkTask getGalleryWorkBg() {
        return this.galleryWorkBg;
    }

    /**
     * @param contentType the contentType to set
     */
    public final void setContentType(final GalleryViewType contentType) {
        this.contentViewType = contentType;
    }

    /**
     * @return the contentType
     */
    public final GalleryViewType getContentType() {
        return this.contentViewType;
    }

    /**
     * @return the contentPathField
     */
    public final String getContentPathField() {
        return GalleryView.contentPathField;
    }

    /**
     * @return the contentNameField
     */
    public final String getContentNameField() {
        return GalleryView.contentNameField;
    }

    /**
     * @param contentCursor the contentCursor to set
     */
    @Override
    public final void setContentCursor(final Cursor contentCursor) {
        this.contentCursor = contentCursor;
    }

    /**
     * @return the contentCursor
     */
    public final Cursor getContentCursor() {
        return this.contentCursor;
    }

    @Override
    public void progressWorkExecution(final int NumberFiles) {
        // TODO Auto-generated method stub
    }

    @Override
    public void finishedWorkExecution( final GalleryWorkTaskResult processingResult ) {
        if(processingResult != GalleryWorkTaskResult.GALLERY_RESULT_FINISHED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    if(which == DialogInterface.BUTTON_NEUTRAL) {
                        // close dialog by the button
                        GalleryView.this.finish();
                    }
                }
            };

            switch(processingResult) {
            case GALLERY_RESULT_EMPTY:
                builder.setMessage(R.string.empty_dialog);
                builder.setCancelable(true);
                builder.setNeutralButton(R.string.back_button, clickListener);
                // show dialog about folder empty
                break;

            case GALLERY_RESULT_ERROR:
                //  show dialog about error
                builder.setMessage(R.string.error_dialog);
                builder.setCancelable(true);
                builder.setNeutralButton(R.string.back_button, clickListener);
                break;

            default:
                break;
            }
        }
    }
}
