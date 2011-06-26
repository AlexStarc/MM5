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
package com.sandrstar.android.gallery;

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
public class ListViewGallery extends GalleryView<ListView> implements OnClickListener, Handler.Callback {
    private static final String TAG = "ListViewGallery";
    Integer nFocusIndex = 0;
    ListViewCursorAdapter contentAdapter = null;
    GalleryOptionsBar mOptionsBar = null;

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        setContentType(GalleryViewType.GALLERY_LIST);
        super.onCreate(savedInstanceState);

        this.nFocusIndex = CGalleryConstants.GALLERY_INVALID_INDEX.value();
        // setup options bar
        this.mOptionsBar = new GalleryOptionsBar(this, R.id.listview_optionbar);
        this.mOptionsBar.setOptionsHandler(this);
    }

    @Override
    public void finishedWorkExecution(final GalleryWorkTaskResult processingResult) {
        super.finishedWorkExecution(processingResult);

        if( GalleryWorkTaskResult.GALLERY_RESULT_FINISHED == processingResult ) {

            getMainView().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(final AdapterView<?> arg0, final View view,
                                           final int mSelectItemId, final long arg3) {
                    ListViewGallery.this.nFocusIndex = mSelectItemId;
                }

                @Override
                public void onNothingSelected(final AdapterView<?> arg0) {
                    // nothing to do here
                }
            });

            getMainView().setOnItemClickListener( new OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent,
                                        final View view,
                                        final int position,
                                        final long id) {
                    if( ListViewGallery.this.nFocusIndex < 0 ) {
                        // show options bar
                        ListViewGallery.this.mOptionsBar.showOptionBar();
                    }

                    if(ListViewGallery.this.nFocusIndex != position) {
                        ListViewGallery.this.contentAdapter.setNFocus(position);
                        ListViewGallery.this.nFocusIndex = position;
                    }
                }
            });

            getMainView().setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(final AbsListView view, final int scrollState) {
                    if(ListViewGallery.this.nFocusIndex >= 0) {
                        // reset focus
                        ListViewGallery.this.nFocusIndex = CGalleryConstants.GALLERY_INVALID_INDEX.value();
                        ListViewGallery.this.contentAdapter.setNFocus(ListViewGallery.this.nFocusIndex);
                        ListViewGallery.this.mOptionsBar.hideOptionBar();
                    }
                }

                @Override
                public void onScroll(final AbsListView view, final int firstVisibleItem,
                                     final int visibleItemCount, final int totalItemCount) {
                    // nothing to do here
                }
            });

            // create adapter based on received cursor and attach it to list view
            this.contentAdapter = new ListViewCursorAdapter(getApplicationContext(),
                                                       getContentCursor(),
                                                       false
            );
            this.contentAdapter.setPlayButtonListener(this);
            getMainView().setAdapter(this.contentAdapter);
        }
    }

    /**
     * Listener for click event on Play/Stop button on list items
     */
    @Override
    public void onClick(final View v) {
        try {
            // Listener for play click
            final View listItemView = (View)v.getParent();
            Button playButton;
            final View playView = getMainView().getChildAt(this.contentAdapter.getNPlayIndex());

            GalleryContentItem itemTag = (GalleryContentItem)listItemView.getTag();

            if(itemTag == null) {
                // somehow we haven't received tag, so, retrieve index and obtain tag
                this.contentAdapter.bindView(listItemView, getApplicationContext(), getContentCursor());
                itemTag = (GalleryContentItem)listItemView.getTag();
            }

            if( playView != null ) {
                playButton = (Button)playView.findViewById(R.id.listview_item_play);

                playButton.setBackgroundResource(R.drawable.listview_item_button);
            }

            if(null != playView &&
               this.contentAdapter.getNPlayIndex().equals(itemTag.getIndex())) {
                this.contentAdapter.setNPlayIndex(CGalleryConstants.GALLERY_INVALID_INDEX.value(), null);
            } else {
                if(null != itemTag) {
                    playButton = (Button)listItemView.findViewById(R.id.listview_item_play);

                    playButton.setBackgroundResource(R.drawable.listview_item_stop_button);

                    // Also focus playing item if its not already expanded
                    if(!itemTag.getIndex().equals(this.contentAdapter.getNFocus())) {
                        this.contentAdapter.setNFocus(itemTag.getIndex());
                        this.nFocusIndex = itemTag.getIndex();
                    }

                    this.contentAdapter.setNPlayIndex(itemTag.getIndex(), listItemView);
                }
            }
        } catch(final Exception e) {
            Log.e(TAG, "onClick(): " + e.getClass() + " thrown " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        try {
            this.contentAdapter.setNPlayIndex(CGalleryConstants.GALLERY_INVALID_INDEX.value(), null);
        } catch (final IOException e) {
            Log.e(TAG, "onPause(): " + e.getClass() + " thrown " + e.getMessage());
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        try {
            this.contentAdapter.setNPlayIndex(CGalleryConstants.GALLERY_INVALID_INDEX.value(), null);
        } catch (final IOException e) {
            Log.e(TAG, "onStop(): " + e.getClass() + " thrown " + e.getMessage());
        }
        super.onStop();
    }

    @Override
    public boolean handleMessage(final Message msg) {
        Boolean retVal = false;

        switch(msg.arg1) {
        case R.id.infoButton:
            // launch details with extras
            final Intent launchDetails = new Intent(getGalleryViewContext(), DetailsView.class);

            if(this.nFocusIndex >= 0) {
                launchDetails.putExtra("com.teleca.mm5.gallery.FocusIndex", this.nFocusIndex);
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
