/*
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *                     Version 2, December 2004
 *
 *  Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>
 *
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document, and changing it is allowed as long
 *  as the name is changed.
 *
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   0. You just DO WHAT THE FUCK YOU WANT TO.
 *
 *  This program is free software. It comes without any warranty, to
 *  the extent permitted by applicable law. You can redistribute it
 *  and/or modify it under the terms of the Do What The Fuck You Want
 *  To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details.
 */

/**
 * @author AlexStarc
 *
 * @description This is implementation of list view for Content Gallery. It's intended to display list of
 * phone digital sound assets.
 */
package com.teleca.mm5.gallery;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

/**
 * @author AlexStarc
 *
 */
public class ListViewGallery extends GalleryView<ListView> implements GalleryViewInterface, OnClickListener, Handler.Callback {
    private static final String TAG = "ListViewGallery";
    private Integer nFocusIndex = 0;
    private ListViewCursorAdapter contentAdapter = null;
    private GalleryOptionsBar mOptionsBar = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setContentType(GalleryViewType.GALLERY_LIST);
        super.onCreate(savedInstanceState);

        nFocusIndex = CGalleryConstants.GALLERY_INVALID_INDEX.value();
        // setup options bar
        mOptionsBar = new GalleryOptionsBar(this, R.id.listview_optionbar);
        mOptionsBar.setOptionsHandler(this);
    }

    @Override
    public void finishedWorkExecution(GalleryWorkTaskResult processingResult) {
        super.finishedWorkExecution(processingResult);

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
                    if( nFocusIndex < 0 ) {
                        // show options bar
                        mOptionsBar.showOptionBar();
                    }

                    if(nFocusIndex != position) {
                        contentAdapter.setNFocus(position);
                        nFocusIndex = position;
                    }
                }
            });

            getMainView().setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if(nFocusIndex >= 0) {
                        // reset focus
                        nFocusIndex = CGalleryConstants.GALLERY_INVALID_INDEX.value();
                        contentAdapter.setNFocus(nFocusIndex);
                        mOptionsBar.hideOptionBar();
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
                                                       false
            );
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
            Button playButton;
            View playView = getMainView().getChildAt(contentAdapter.getNPlayIndex());

            GalleryContentItem itemTag = (GalleryContentItem)listItemView.getTag();

            if(itemTag == null) {
                // somehow we haven't received tag, so, retrieve index and obtain tag
                contentAdapter.bindView(listItemView, getApplicationContext(), getContentCursor());
                itemTag = (GalleryContentItem)listItemView.getTag();
            }

            if( playView != null ) {
                playButton = (Button)playView.findViewById(R.id.listview_item_play);

                playButton.setBackgroundResource(R.drawable.listview_item_button);
            }

            if(null != playView &&
               contentAdapter.getNPlayIndex().equals(itemTag.getIndex())) {
                contentAdapter.setNPlayIndex(CGalleryConstants.GALLERY_INVALID_INDEX.value(), null);
            } else {
                if(null != itemTag) {
                    playButton = (Button)listItemView.findViewById(R.id.listview_item_play);

                    playButton.setBackgroundResource(R.drawable.listview_item_stop_button);

                    // Also focus playing item if its not already expanded
                    if(!itemTag.getIndex().equals(contentAdapter.getNFocus())) {
                        contentAdapter.setNFocus(itemTag.getIndex());
                        nFocusIndex = itemTag.getIndex();
                    }

                    contentAdapter.setNPlayIndex(itemTag.getIndex(), listItemView);
                }
            }
        } catch(Exception e) {
            Log.e(TAG, "onClick(): " + e.getClass() + " thrown " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        try {
            contentAdapter.setNPlayIndex(CGalleryConstants.GALLERY_INVALID_INDEX.value(), null);
        } catch (IOException e) {
            Log.e(TAG, "onPause(): " + e.getClass() + " thrown " + e.getMessage());
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        try {
            contentAdapter.setNPlayIndex(CGalleryConstants.GALLERY_INVALID_INDEX.value(), null);
        } catch (IOException e) {
            Log.e(TAG, "onStop(): " + e.getClass() + " thrown " + e.getMessage());
        }
        super.onStop();
    }

    @Override
    public boolean handleMessage(Message msg) {
        Boolean retVal = false;

        switch(msg.arg1) {
        case R.id.infoButton:
            // launch details with extras
            Intent launchDetails = new Intent(getGalleryViewContext(), DetailsView.class);

            if(nFocusIndex >= 0) {
                launchDetails.putExtra("com.teleca.mm5.gallery.FocusIndex", nFocusIndex);
            } else {
                launchDetails.putExtra("com.teleca.mm5.gallery.FocusIndex", 0);
            }

            launchDetails.putExtra("com.teleca.mm5.gallery.ContentType", GalleryViewType.GALLERY_SOUND_DETAILS.ordinal());

            startActivity(launchDetails);
            retVal = true;
            break;

        default:
            break;
        }

        return retVal;
    }
}
