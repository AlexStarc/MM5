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

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

/**
 * @author negr
 *
 */
public class ListViewGallery extends GalleryView<ListView> implements GalleryViewInterface, OnClickListener {
    private static final String TAG = "ListViewGallery";
    private Integer nFocusIndex = 0;
    private View    playView = null;
    private ListViewCursorAdapter contentAdapter = null;
    private MediaPlayer player = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setContentType(GalleryViewType.GALLERY_LIST);
        super.onCreate(savedInstanceState);

        // create MediaPlayer for playing of selected sound
        player = new MediaPlayer();
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
                    nFocusIndex = mSelectItemId;
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            // create adapter based on received cursor and attach it to list view
            contentAdapter = new ListViewCursorAdapter(getApplicationContext(),
                                                       getContentCursor(),
                                                       false,
                                                       R.layout.listviewgallery_item);
            contentAdapter.setPlayButtonListener(this);
            getMainView().setAdapter(contentAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            // Listener for play click
            View listItemView = (View)v.getParent();
            Button playButton = null;

            if( playView != null ) {
                playButton = (Button)playView.findViewById(R.id.listview_item_play);

                playButton.setBackgroundResource(R.drawable.listview_item_button);
            }

            player.reset();

            if(null != playView &&
               playView.equals(listItemView))
            {
                playView = null;
            } else {
                GalleryContentItem itemTag = (GalleryContentItem)listItemView.getTag();

                player.setDataSource(itemTag.getContentPath());
                player.prepare();
                player.start();
                playView = listItemView;
                playButton = (Button)listItemView.findViewById(R.id.listview_item_play);

                playButton.setBackgroundResource(R.drawable.listview_item_stop_button);
            }
        } catch(Exception e) {
            Log.e(TAG, "onClick(): " + e.getClass() + " thrown " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        player.reset();

        super.onPause();
    }

    @Override
    protected void onStop() {
        player.reset();

        super.onStop();
    }
}
