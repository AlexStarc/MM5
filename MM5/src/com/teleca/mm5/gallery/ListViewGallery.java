/*            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
                    Version 2, December 2004

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>

 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document, and changing it is allowed as long
 as the name is changed.

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

  0. You just DO WHAT THE FUCK YOU WANT TO.
*/

/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */

/**
 * @author AlexStarc
 *
 * @description This is implementation of list view for Content Gallery. It's intended to display list of
 * phone digital sound assets.
 */
package com.teleca.mm5.gallery;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * @author negr
 *
 */
public class ListViewGallery extends GalleryView<ListView> implements GalleryViewInterface {
    private static final String TAG = "ListViewGallery";
    private Integer nFocusIndex = 0;
    private ListViewCursorAdapter contentAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setContentType(GalleryViewType.GALLERY_LIST);
        super.onCreate(savedInstanceState);
    }

    private void update(int mSelectItemId, View selectedView){
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.listviewgallery_options, menu);
        return true;
    }

    @Override
    public void finishedWorkExecution(GalleryWorkTaskResult processingResult) {
        // store status of processing
        if( GalleryWorkTaskResult.GALLERY_RESULT_FINISHED == processingResult ) {

            getMainView().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View view,
                                           int mSelectItemId, long arg3) {
                    update(mSelectItemId, view);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            getMainView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view,
                                        int mSelectItemId, long arg3) {
                    nFocusIndex = mSelectItemId;
                    update(mSelectItemId, view);
                }
            });

            // create adapter based on received cursor and attach it to list view
            contentAdapter = new ListViewCursorAdapter(getApplicationContext(),
                                                       getContentCursor(),
                                                       false,
                                                       R.layout.listviewgallery_item);
            getMainView().setAdapter(contentAdapter);
        }
    }
}
