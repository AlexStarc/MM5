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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

/**
 * @author negr
 *
 */
public class ListViewGallery extends GalleryView<ListView> implements GalleryViewInterface, OnClickListener, MediaPlayer.OnCompletionListener {
    private static final String TAG = "ListViewGallery";
    private Integer nFocusIndex = 0;
    private ListViewCursorAdapter contentAdapter = null;
    private MediaPlayer player = null;
    private static final Integer GALLERY_INVALID_INDEX = -1;

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

            getMainView().setOnItemClickListener( new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent,
                                        View view,
                                        int position,
                                        long id) {
                    nFocusIndex = position;

                    contentAdapter.setnFocus(nFocusIndex);
                }
            });

            getMainView().setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if(nFocusIndex >= 0) {
                        // reset focus
                        nFocusIndex = GALLERY_INVALID_INDEX;
                        contentAdapter.setnFocus(nFocusIndex);
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
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

    /**
     * Listener for click event on Play/Stop button on list items
     */
    @Override
    public void onClick(View v) {
        try {
            // Listener for play click
            View listItemView = (View)v.getParent();
            Button playButton = null;
            View playView = getMainView().getChildAt(contentAdapter.getnPlayIndex());

            if( playView != null ) {
                playButton = (Button)playView.findViewById(R.id.listview_item_play);

                playButton.setBackgroundResource(R.drawable.listview_item_button);
            }

            player.reset();

            if(null != playView &&
               playView.equals(listItemView)) {
                playView = null;
                contentAdapter.setnPlayIndex(GALLERY_INVALID_INDEX);
            } else {
                GalleryContentItem itemTag = (GalleryContentItem)listItemView.getTag();

                if(itemTag == null) {
                    // somehow we haven't received tag, so, retrieve index and obtain tag
                    contentAdapter.bindView(listItemView, getApplicationContext(), getContentCursor());
                    itemTag = (GalleryContentItem)listItemView.getTag();
                }

                if(null != itemTag) {
                    contentAdapter.setnPlayIndex(itemTag.getIndex());
                    // Also expand playing item
                    contentAdapter.setnFocus(itemTag.getIndex());
                    player.setDataSource(itemTag.getContentPath());
                    player.prepare();
                    player.start();
                    playView = listItemView;
                    playButton = (Button)listItemView.findViewById(R.id.listview_item_play);

                    playButton.setBackgroundResource(R.drawable.listview_item_stop_button);
                }
            }
        } catch(Exception e) {
            Log.e(TAG, "onClick(): " + e.getClass() + " thrown " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        player.reset();
        contentAdapter.setnPlayIndex(GALLERY_INVALID_INDEX);
        super.onPause();
    }

    @Override
    protected void onStop() {
        player.reset();
        contentAdapter.setnPlayIndex(GALLERY_INVALID_INDEX);
        super.onStop();
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        // we need to handle this play on both - selection and underlying item
        this.onClick(getMainView().getChildAt(contentAdapter.getnPlayIndex()).findViewById(R.id.listview_item_play));
        contentAdapter.setnPlayIndex(GALLERY_INVALID_INDEX);
    }
}
