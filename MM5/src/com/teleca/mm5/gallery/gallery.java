package com.teleca.mm5.gallery;

import java.lang.annotation.Documented;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@Documented
@interface ClassPreamble {
    String author();
    int currentRevision() default 1;
    String description();
}

// enum for available gallery views to be used in methods
enum GalleryViewTypes {
    GALLERY_LIST,
    GALLERY_THUMBNAILS,
    GALLERY_FULLSCREEN,
    GALLERY_DETAILS
}

@ClassPreamble (
                author = "Alexander Starchenko",
                description = "interface for view for interaction with GalleryWorkTask task"
)
interface GalleryView {
    public void progressWorkExecution( int NumberFiles );
    public void finishedWorkExecution( GalleryWorkTaskResult processingResult );
    public void setContentList( GalleryContentItem[] contentArray );
    public Context getGalleryViewContext();
}

@ClassPreamble (
                author = "Alexander Starchenko",
                description = "main applet class"
)
public class gallery extends ListActivity implements GalleryView, OnItemClickListener {
    private static final String TAG = "gallery";
    /* Array which contains items which used in main menu */
    static final String[] GALLERY_MAIN_MENU = new String[] {
        "Thumbnails", "List", "FullScreen", "Help"
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i( TAG, "Started" );
        super.onCreate(savedInstanceState);

        setListAdapter(new ArrayAdapter<String>(this, R.layout.mainmenu_item, GALLERY_MAIN_MENU));

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(this);
    }

    @Override
    public void progressWorkExecution( int NumberFiles ) {
        Log.i( TAG, "BG work has" + NumberFiles );
    }

    @Override
    public void finishedWorkExecution( GalleryWorkTaskResult processingResult ) {
        /** Here all items received by view via ArrayList. It get called just before finishedWorkExecution;
         *  Contents of view might needs to be updated only after status received via finishedWorkExecution */
        // TODO: provide list updating
        Log.i( TAG, "BG work has finished with " + processingResult );
    }

    @Override
    public void setContentList( GalleryContentItem[] contentArray ) {
        // TODO update view with content
    }

    @Override
    public Context getGalleryViewContext() {
        return getApplicationContext();
    }

    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {
        // When clicked, show a toast with the TextView text
        Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                       Toast.LENGTH_SHORT).show();

        switch( position ) {
        case 0:
            Intent intent = new Intent(this, ThumbnailsView.class);
            startActivity(intent);
            break;

        default:
            break;
        }
    }
}
