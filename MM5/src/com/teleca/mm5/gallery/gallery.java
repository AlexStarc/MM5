package com.teleca.mm5.gallery;

import java.lang.annotation.Documented;

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



@ClassPreamble (
                author = "Alexander Starchenko",
                description = "main menu view"
)
public class gallery extends GalleryView<ListView> implements OnItemClickListener {
    private static final String TAG = "gallery";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i( TAG, "Started" );

        setContentType(GalleryViewType.GALLERY_MAINMENU);
        super.onCreate(savedInstanceState);

        try {
            getMainView().setAdapter(new ArrayAdapter<String>(this, R.layout.mainmenu_item, getResources().getStringArray(R.array.mainmenu_items) ));
        } catch(Exception e) {
            Log.e( TAG, "onCreate(): " + e.getClass() + " thrown " + e.getMessage());
        }

        ListView lv = getMainView();

        if( null != lv ) {
            lv.setTextFilterEnabled(true);
            lv.setOnItemClickListener(this);
        }
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
